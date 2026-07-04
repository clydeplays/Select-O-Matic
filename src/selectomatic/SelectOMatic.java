package selectomatic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Select-O-Matic:  The Random Ship Selector
 * @author Clyde Plays
 */
public class SelectOMatic {
    
    /** The version and software name, used in the JFrame header.  */
    private static final String SW_VERSION = "Select-O-Matic v1.6a - by Clyde Plays";
    
    /** The backing model for this application. */
    private static Model m_dataModel;
    
    /** The display */
    private static View m_view;

    /**
     * Main Method.
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException ex) {
            Logger.getAnonymousLogger().log(Level.FINER, "Error setting look and feel.", ex);
        } catch (IllegalAccessException ex) {
            Logger.getAnonymousLogger().log(Level.FINER, "Illegal access when setting look and feel.", ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getAnonymousLogger().log(Level.FINER, "Unsupported Look and Feel provided by UIManager.", ex);
        }      
        
        if (args.length != 2){
            System.out.println("Must supply two command line arguments.\nCorrect Usage:\n   java -jar SelectOMatic.jar <path-to-an-options-file> <r or o for 'random' or 'ordered'> \n"
                    + "   OR\n   java -jar SelectOMatic.jar all <r or o for 'random' or 'ordered'>\n   (which uses all files in the data directory (except for the properties file)).");
            System.exit(0);
        }

        // Initialize the Data Model and Get the file options
        final String listFile = args[0];
        
        // Determine if the images should be randomized or not
        final String ordering = args[1];
        boolean randomize = false;
        if(ordering.equals("r")){
            System.out.println("---Selection Mode:  RANDOM");
            randomize = true;
        } else {
            System.out.println("---Selection Mode:  ORDERED");
        }
        
        // Create the model and view, and start the app.
        m_dataModel = new Model(listFile, randomize);
        m_view = new View();
        m_view.setup(m_dataModel, SW_VERSION);
    }
}