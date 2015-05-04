package water.rapids;


import water.Iced;
import water.Key;
import water.MRTask;

import java.util.HashSet;

/**
 * Exec is an interpreter of abstract syntax trees.
 *
 * Trees have a Lisp-like structure with the following "reserved" special characters:
 *
 *     '('   signals the parser to parse a function name, the next token is an identifier or a (single char) flag
 *     '#'   signals the parser to parse a double: attached_token
 *     '"'   signals the parser to parse a String (double quote): attached_token
 *     "'"   signals the parser to parse a String (single quote): attached_token
 *     '%'   signals a variable lookup: attached_token
 *     '!'   signals a variable set: attached_token
 *     '['   signals a column slice by index - R handles all named to int conversions (as well as 1-based to 0-based)
 *     'def' signals the parser to a parse a function: (def name args body).
 *     '='   signals the parser to assign the RHS to the LHS.
 *     'g'   signals &gt;
 *     'G'   signals &gt;=
 *     'l'   signals &lt;
 *     'L'   signals &lt;=
 *     'n'   signals ==
 *     'N'   signals !=
 *     'not'   signals negation (!)
 *     '{'   signals the parser to begin parsing a ';'-separated array of flagged inputs (#, %, ", ') (ASTSeries is the resulting AST)
 *
 * In the above, attached_token signals that the special char has extra chars that must be parsed separately. These are
 * variable names (in the case of % and !), doubles (in the case of #), or Strings (in the case of ' and ").
 *
 * Everything else is a function call (prefix/infix/func) and has a leading char of '('.
 */
public class Exec extends Iced {

  //parser
  final byte[] _ast;
  final String _str;
  int _x;

  //global env
  final Env _env;

  public Exec(String ast, Env env) {
    _str = ast;
    _ast = ast == null ? null : ast.getBytes();
    _env = env;
  }

  public static Env exec( String str ) throws IllegalArgumentException {
    cluster_init();
    // Preload the global environment from existing Frames
    HashSet<Key> locked = new HashSet<>();
    Env env = Env.make(locked);

    try {
      Exec ex = new Exec(str, env);

      // Parse
      AST ast = ex.parse();
      if (!ex.allDone()) throwErr("Note that only a single statement can be processed at a time. Junk at the end of the statement: ",ex);

      // Execute
      env = ast.treeWalk(env);

      // Write back to DKV (if needed) and return
      env.postWrite();

    } catch( RuntimeException t ) {
      env.remove_and_unlock();
      throw t;
    }
    return env;
  }

  public static void new_func(final String str) throws IllegalArgumentException {
    cluster_init();

    new MRTask() {
      @Override public void setupLocal() {
        HashSet<Key> locked = new HashSet<>();
        Env env = Env.make(locked);
        Exec ex = new Exec(str, env);
        ex.parse_fun();
      }
    }.doAllNodes();
  }

  protected AST parse() {
    skipWS();
    // Parse a token --> look for a function or a special char.
    if (!hasNext()) throw new IllegalASTException("End of input unexpected. Badly formed AST.");
    String tok = parseID();
    if (!hasNext()) throw new IllegalASTException("End of input unexpected. Badly formed AST.");
    //lookup of the token
    AST ast = lookup(tok);
    return ast.parse_impl(this);
  }

  protected void parse_fun() {
    // parse a token -> should be "def"
    String tok = parseID();
    if (!tok.equals("def")) throw new IllegalArgumentException("Expected function definition but got "+tok);
    ASTFuncDef ast = new ASTFuncDef();
    ast.parse_func(this);
  }

  private AST lookup(String tok) {
    AST sym = ASTOp.SYMBOLS.get(tok);
    if (sym != null) return sym.make();
    sym = ASTOp.UDF_OPS.get(tok);
    if (sym != null) return sym.make();
    throw new IllegalArgumentException("*Unimplemented* failed lookup on token: `"+tok+"`. Contact support@0xdata.com for more information.");
  }

  String parseID() {
    StringBuilder sb = new StringBuilder();
    if (peek() == '(') { // eat the '(' and any ws.
      _x++; skipWS();
      if( peek() == ')' ) { sb.append((char)_ast[_x++]); return sb.toString(); } // handles the case where we have a lisp-like null: ()
      return parseID();  // peel out the ID
    }
    if ( isSpecial(peek())) { return sb.append((char)_ast[_x++]).toString(); } // if attached_token, then use parse_impl
    while( _x < _ast.length && _ast[_x] != ' ' && _ast[_x] != ')' && _ast[_x] != ';' && _ast[_x]!= '\'' && _ast[_x]!='\"' ) {  // while not WS...
      sb.append((char)_ast[_x++]);
    }
    skipWS();
    return sb.toString();
  }

  String parseString(char eq) {
    StringBuilder sb = new StringBuilder();
    while(_ast[_x] != eq) {
      sb.append((char)_ast[_x++]);
    }
    _x++;
    return sb.toString();
  }

  boolean hasNext() { return _x < _ast.length; }
  boolean allDone() {
    skipWS();
    if( _x >= _ast.length ) return true;
    while( isEnd() && _x < _ast.length ) {
      _x++; skipWS();
    }
    return _x >= _ast.length;
  }
  double nextDbl() {
    AST a = parse();
    if( a instanceof ASTNum) return ((ASTNum)a)._d;
    else throw new IllegalArgumentException("Expected to parse a number, but got " + a.getClass());
  }

  String nextStr() {
    AST a = parse();
    // did it get caught by the horrible hack to auto-lookup strings?
    if( a instanceof ASTFrame ) return ((ASTFrame)a)._key;
    else if( a instanceof ASTString ) return ((ASTString)a)._s;
    else throw new IllegalArgumentException("Expected to parse a String, but got " + a.getClass());
  }

  Exec xpeek(char c) {
    assert _ast[_x] == c : "Expected '"+c+"'. Got: '"+(char)_ast[_x]+"'. unparsed: "+ unparsed() + " ; _x = "+_x;
    _x++; return this;
  }

  char ppeek() { return (char)_ast[_x-1];}  // past peek
  char peek() { return (char)_ast[_x]; }    // ppek ahead
  char peekPlus() { skipWS(); return (char)_ast[_x++]; } // peek and move ahead
  boolean isEnd() { return _x >= _ast.length || (char) _ast[_x] == ')'; } // out of chars OR end of AST (signaled by ')' )
  void eatEnd() {
    skipWS();
    if( !isEnd() ) throwErr("No end to eat!",this);
      _x++;
    skipWS();
  }
  Exec skipWS() {
    while (true) {
      if (_x >= _ast.length) break;
      if (peek() == ' ' || peek() == ';') {
        _x++;
        continue;
      }
      break;
    }
    return this;
  }

  boolean isSpecial(char c) { return c == '\"' || c == '\'' || c == '#' || c == '!' || c == '%' || c =='{'; }
  boolean isQuoted(char c) { return c == '\"' || c == '\''; }
  char getQuote() { return (char)_ast[_x++]; }
  String unparsed() { return new String(_ast,_x,_ast.length-_x); }

  static AST throwErr( String msg, Exec E) {
    int idx = E._ast.length-1;
    int lo = E._x, hi=idx;

    String str = E._str;
    if( idx < lo ) { lo = idx; hi=lo; }
    String s = msg+ '\n'+str+'\n';
    int i;
    for( i=0; i<lo; i++ ) s+= ' ';
    s+='^'; i++;
    for( ; i<hi; i++ ) s+= '-';
    if( i<=hi ) s+= '^';
    s += '\n';
    throw new IllegalArgumentException(s);
  }

  // To avoid a class-circularity hang, we need to force other members of the
  // cluster to load the Exec & AST classes BEFORE trying to execute code
  // remotely, because e.g. ddply runs functions on all nodes.
  private static boolean _inited;       // One-shot init
  static void cluster_init() {
    if( _inited ) return;
    new MRTask() {
      @Override public void setupLocal() {
        new ASTPlus(); // Touch a common class to force loading
      }
    }.doAllNodes();
    _inited = true;
  }
}


class IllegalASTException extends IllegalArgumentException { IllegalASTException(String s) {super(s);} }
