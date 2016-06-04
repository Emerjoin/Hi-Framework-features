package mz.co.hi.web.js;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Mario Junior.
 */
public class Minifier {


    private static Logger logger = Logger.getLogger(Minifier.class.getName());


    private static class YuiCompressorErrorReporter implements ErrorReporter {


        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger.log(Level.WARNING, message);
            } else {
                logger.log(Level.WARNING, line + ':' + lineOffset + ':' + message);
            }
        }

        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger.log(Level.SEVERE, message);
            } else {
                logger.log(Level.SEVERE, line + ':' + lineOffset + ':' + message);
            }
        }

        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }


    public static void compressJavaScript(String inputFilename, String outputFilename, Options o) throws IOException {
        Reader in = null;
        Writer out = null;
        try {
            in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);

            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
            in.close(); in = null;

            out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
            compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);


        } finally {

            try {


                in.close();


            }catch (Exception ex){


            }


            try {

                out.close();


            }catch (Exception ex){


            }
        }
    }


}
