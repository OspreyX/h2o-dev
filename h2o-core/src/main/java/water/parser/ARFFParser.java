package water.parser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import water.fvec.Vec;

class ARFFParser extends CsvParser {
  private static final byte GUESS_SEP = ParseSetup.GUESS_SEP;

  ARFFParser(ParseSetup ps) { super(ps); }

  /** Try to parse the bytes as ARFF format  */
  static ParseSetup guessSetup(byte[] bits, byte sep, boolean singleQuotes, String[] columnNames, String[] naStrings) {
    if (columnNames != null) throw new UnsupportedOperationException("ARFFParser doesn't accept columnNames.");

    // Parse all lines starting with @ until EOF or @DATA
    boolean have_data = false;
    int offset = 0;
    String[][] data;
    String[] labels;
    String[][] domains;
    String[] headerlines = new String[0];
    byte[] ctypes;

    // header section
    ArrayList<String> header = new ArrayList<>();
    while (offset < bits.length) {
      int lineStart = offset;
      while (offset < bits.length && !CsvParser.isEOL(bits[offset])) ++offset;
      int lineEnd = offset;
      ++offset;
      // For Windoze, skip a trailing LF after CR
      if ((offset < bits.length) && (bits[offset] == CsvParser.CHAR_LF)) ++offset;
      if (bits[lineStart] == '#') continue; // Ignore      comment lines
      if (bits[lineStart] == '%') continue; // Ignore ARFF comment lines
      if (lineEnd > lineStart) {
        String str = new String(bits, lineStart, lineEnd - lineStart, Charset.defaultCharset()).trim();
        if (str.equalsIgnoreCase("@DATA")) {
          if (!CsvParser.isEOL(bits[offset])) {
            have_data = true; //more than just the header
          }
          break;
        }
        String[] tok = determineTokens(str, CHAR_SPACE, singleQuotes);
        if (tok.length > 0 && tok[0].equalsIgnoreCase("@RELATION")) continue; // Ignore name of dataset
        if (!str.isEmpty()) header.add(str);
      }
    }
    if (header.size() == 0)
      return new ParseSetup(false, 0, new String[]{"No data!"}, ParserType.AUTO, GUESS_SEP, false, ParseSetup.NO_HEADER, 0, null);
    headerlines = header.toArray(headerlines);

    // process header
    final int nlines = headerlines.length;
    int ncols = nlines;
    data = new String[ncols][];
    labels = new String[ncols];
    domains = new String[ncols][];
    ctypes = new byte[ncols];
    for (int i=0; i<ncols; ++i) {
      data[i] = headerlines[i].split("\\s+");
      if (!data[i][0].equalsIgnoreCase("@ATTRIBUTE")) {
        return new ParseSetup(false,1, new String[]{"Expected line to start with @ATTRIBUTE."},ParserType.ARFF, GUESS_SEP,singleQuotes,ParseSetup.NO_HEADER,ncols,data);
      } else {
        if (data[i].length != 3 ) {
          return new ParseSetup(false,1, new String[]{"Expected @ATTRIBUTE to be followed by <attribute-name> <datatype>"},ParserType.ARFF, GUESS_SEP,singleQuotes,ParseSetup.NO_HEADER,ncols,data);
        }
        labels[i] = data[i][1];
        String type = data[i][2];
        domains[i] = null;
        if (type.equalsIgnoreCase("NUMERIC") || type.equalsIgnoreCase("REAL") || type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("INT")) {
          ctypes[i] = Vec.T_NUM;
          continue;
        }
        else if (type.equalsIgnoreCase("DATE") || type.equalsIgnoreCase("TIME")) {
          ctypes[i] = Vec.T_TIME;
          continue;
        }
        else if (type.equalsIgnoreCase("ENUM")) {
          ctypes[i] = Vec.T_ENUM;
          continue;
        }
        else if (type.equalsIgnoreCase("STRING")) {
          ctypes[i] = Vec.T_STR;
          continue;
        }
        else if (type.equalsIgnoreCase("UUID")) { //extension of ARFF
          ctypes[i] = Vec.T_UUID;
          continue;
        }
        else if (type.equalsIgnoreCase("RELATIONAL")) {
          throw new UnsupportedOperationException("Relational ARFF format is not supported.");
        }
        else if (type.startsWith("{") && type.endsWith("}")) {
          domains[i] = data[i][2].replaceAll("[{}]", "").split(",");
          if (domains[i][0].length() > 0) {
            // case of {A,B,C} (valid list of factors)
            ctypes[i] = Vec.T_ENUM;
            continue;
          }
        }

        // only get here if data is invalid ARFF
        return new ParseSetup(false,1, new String[]{"Unexpected line."},ParserType.ARFF, GUESS_SEP,singleQuotes,ParseSetup.NO_HEADER,ncols,data);
      }
    }

    // data section (for preview)
    if (have_data) {
      String[] datalines = new String[0];
      ArrayList<String> datablock = new ArrayList<>();
      while (offset < bits.length) {
        int lineStart = offset;
        while (offset < bits.length && !CsvParser.isEOL(bits[offset])) ++offset;
        int lineEnd = offset;
        ++offset;
        // For Windoze, skip a trailing LF after CR
        if ((offset < bits.length) && (bits[offset] == CsvParser.CHAR_LF)) ++offset;
        if (bits[lineStart] == '#') continue; // Ignore      comment lines
        if (bits[lineStart] == '%') continue; // Ignore ARFF comment lines
        if (lineEnd > lineStart) {
          String str = new String(bits, lineStart, lineEnd - lineStart).trim();
          if (!str.isEmpty()) datablock.add(str);
        }
      }
      if (datablock.size() == 0)
        return new ParseSetup(false, 0, new String[]{"No data!"}, ParserType.AUTO, GUESS_SEP, false, ParseSetup.NO_HEADER, 0, null);
      datalines = datablock.toArray(datalines);

      // process data section
      int nlines2 = Math.min(10, datalines.length);
      data = new String[nlines2][];

      // First guess the field separator by counting occurrences in first few lines
      if (nlines2 == 1) {
        if (sep == GUESS_SEP) {
          if (datalines[0].split(",").length > 2) sep = (byte) ',';
          else if (datalines[0].split(" ").length > 2) sep = ' ';
          else
            return new ParseSetup(false, 1, new String[]{"Failed to guess separator."}, ParserType.CSV, GUESS_SEP, singleQuotes, ParseSetup.NO_HEADER, ncols, data);
        }
        data[0] = determineTokens(datalines[0], sep, singleQuotes);
        ncols = (ncols > 0) ? ncols : data[0].length;
        labels = null;
      } else {                    // 2 or more lines
        if (sep == GUESS_SEP) {   // first guess the separator
          sep = guessSeparator(datalines[0], datalines[1], singleQuotes);
          if (sep == GUESS_SEP && nlines2 > 2) {
            sep = guessSeparator(datalines[1], datalines[2], singleQuotes);
            if (sep == GUESS_SEP) sep = guessSeparator(datalines[0], datalines[2], singleQuotes);
          }
          if (sep == GUESS_SEP) sep = (byte) ' '; // Bail out, go for space
        }

        for (int i = 0; i < nlines2; ++i) {
          data[i] = determineTokens(datalines[i], sep, singleQuotes);
        }
      }
    }

    // Return the final setup
    return new ParseSetup( true, 0, null, ParserType.ARFF, sep, singleQuotes, ParseSetup.NO_HEADER, ncols, labels, ctypes, domains, naStrings, data);
  }

}
