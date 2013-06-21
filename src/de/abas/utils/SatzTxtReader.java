/*-----------------------------------------------------------------------------
 * Modul Name       : LayoutKontextUpdate.java
 * Verwendung       : 
 * Autor            : mg
 * Verantwortlich   : mg
 * Kontrolle        : 
 * Beratungspflicht : nein
 * Copyright        : (c)1990-2013 ABAS Software AG
 *
 *---------------------------------------------------------------------------*/
 
package de.abas.utils;


/**
 *
 * @author            mg
 * @version           1
 */
public class SatzTxtReader {

    private static String[] argv;
    private static int argc = 0;
    private static int argn = 0;

    private final String satzTxtFileName;
    private SatzTxt satzTxt;

    public static void main(String[] arguments) {

        String fileName = null;
        
        // ----------------------------------------------------------------------
        // Kommandozeilenargumente ermitteln
        // ----------------------------------------------------------------------
        argv = arguments;
        argc = argv.length;
        while (argn < argc) {
           String arg = argv[argn];
           if (arg.equals("-f")) {
               fileName = getArg("-f");
           }
           else if (arg.equals("-d")) {
              //debug = true;
           }
           else if (arg.equals("-?")) {
              usage("SatzTxtReader");
           }
           else {
              usage("[SatzTxtReader] invalid command line option: " + arg);
           }
           ++argn;
        }

        final SatzTxtReader reader = new SatzTxtReader(fileName);
        reader.readSatzTxt();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                createAndShowGUI(reader.getSatzTxt());
            }
        });
    }
    
    // ========== Private Methods =================================================================

    private SatzTxtReader(final String fileName) {
        satzTxtFileName = fileName;
    }
    
    private void readSatzTxt() {
        satzTxt = new SatzTxt(satzTxtFileName);  
        satzTxt.read();
    }
    
    private SatzTxt getSatzTxt() {
        return satzTxt;
    }
    
    private static void createAndShowGUI(final SatzTxt satzTxt) {
        final SatzTxtReaderMainFrame frame = new SatzTxtReaderMainFrame(satzTxt);
        frame.initializeMainFrame();
        frame.setVisible(true);
    }
    
    /**
     * Hilfsfunktion fuer getopt()
     */
    private static String getArg(final String option) {
       ++argn;
       if (argn < argc) {
          return argv[argn];
       }
       else {
          usage("option argument missing: " + option);
          return null;
       }
    }

    /**
     * Gibt eine usage-Meldung aus.
     */
    private static void usage(final String msg) {
       if (msg != null) {
          System.err.println(msg);
       }
       System.exit(1);
    }
}
