package water.api;

import water.DKV;
import water.exceptions.H2OIllegalArgumentException;
import water.fvec.Frame;
import water.parser.ValueString;
import water.rapids.Env;
import water.util.Log;
import water.util.PrettyPrint;

class RapidsHandler extends Handler {

  public RapidsV1 isEvaluated(int version, RapidsV1 rapids) {
    if (rapids == null) return null;
    if (rapids.ast_key == null) throw new IllegalArgumentException("No key supplied to getKey.");
    boolean isEval = false;
    if ((DKV.get(rapids.ast_key.key()))!=null) {
      isEval = true;
    }
    rapids.evaluated = isEval;
    return rapids;
  }


  public RapidsV1 exec(int version, RapidsV1 rapids) {
    if (rapids == null) return null;
    Throwable e = null;
    Env env = null;
    try {
      //learn all fcns
      if(rapids.funs != null) {
        for (String f : rapids.funs) {
          water.rapids.Exec.new_func(f);
        }
      }
      if (rapids.ast == null || rapids.ast.equals("")) return rapids;
      env = water.rapids.Exec.exec(rapids.ast);
      StringBuilder sb = env._sb;
      if( sb.length()!=0 ) sb.append("\n");
      if (env.isAry()) {
        Frame fr = env.popAry();
        if (fr.numRows() == 1 && fr.numCols() == 1) {
          if (fr.anyVec().isEnum()) {
            rapids.string = fr.anyVec().domain()[(int)fr.anyVec().at(0)];
            sb.append(rapids.string);
          } else {
            rapids.scalar = fr.anyVec().at(0);
            sb.append(Double.toString(rapids.scalar));
            rapids.string = null;
          }
        } else {
          rapids.key = new KeyV1.FrameKeyV1(fr._key);
          rapids.num_rows = fr.numRows();
          rapids.num_cols = fr.numCols();
          rapids.col_names = fr.names();
          rapids.string = null;
          String[][] head = rapids.head = new String[Math.min(200, fr.numCols())][(int) Math.min(100, fr.numRows())];
          for (int r = 0; r < head[0].length; ++r) {
            for (int c = 0; c < head.length; ++c) {
              if (fr.vec(c).isNA(r))
                head[c][r] = "";
              else if (fr.vec(c).isUUID())
                head[c][r] = PrettyPrint.UUID(fr.vec(c).at16l(r), fr.vec(c).at16h(r));
              else if (fr.vec(c).isString())
                head[c][r] = String.valueOf(fr.vec(c).atStr(new ValueString(), r));
              else
                head[c][r] = String.valueOf(fr.vec(c).at(r));
            }
          }
        }
        //TODO: colSummary  cols = new Inspect2.ColSummary[num_cols];
      } else if (env.isNum()) {
        rapids.scalar = env.popDbl();
        sb.append(Double.toString(rapids.scalar));
        rapids.string = null;
      } else if (env.isStr()) {
        rapids.string = env.popStr();
        sb.append(rapids.string);
      }
      rapids.result = sb.toString();
      return rapids;
    }
    catch( IllegalArgumentException pe ) { e=pe;}
    catch( Throwable e2 ) { Log.err(e=e2); }
    finally {
      if (env != null) {
        try {env.remove_and_unlock(); }
        catch (Exception xe) { Log.err("env.remove_and_unlock() failed", xe); }
      }
    }
    if( e!=null ) e.printStackTrace();
    if( e!=null ) rapids.error = e.getMessage() == null ? e.toString() : e.getMessage();
    if( e!=null && e instanceof ArrayIndexOutOfBoundsException) rapids.error = e.toString();
    if( e!=null )
      throw new H2OIllegalArgumentException(rapids.error);
    return rapids;
  }
}
