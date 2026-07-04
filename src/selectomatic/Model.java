package selectomatic;

import core.util.file.FileTools;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * The data model that tracks what ship is next.
 * @author Clyde Plays
 */
public class Model {
    
    private List<ShipImage> m_shipImages;
    private List<ShipImage> m_backup;
    private Stack<ShipImage> m_history;
    
    /** True if the files should be randomized, false otherwise */
    private boolean m_randomize = false;
    
        /** A random number generator. */
    private static final Random m_random = new Random();

    
    /**
     * A class that represents the backing data model of the Select-O-Matic application.
     * @param listFile - the file which lists the image files to use for the slide show.
     * @param randomize -  true if the images should be randomized, false otherwise 
     * @throws java.io.IOException if an error occurs interacting with the files / file system.
     */
    public Model(String listFile, boolean randomize) throws IOException {
        if (listFile == null) {
            throw new IllegalArgumentException("Parameter 'listFile' must not be null.");
        }
        if (listFile.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'listFile' must not be empty.");
        }
        
        // Init Values
        m_shipImages = new ArrayList<>();
        m_backup = new ArrayList<>();
        m_history = new Stack<>();
        m_randomize = randomize;

        
        // Read in Ship Names From Files and populate the backup & ship images lists.
        if (listFile.equals("all")) {
            File dataDir = new File("./data/");
            File[] allFiles = dataDir.listFiles();
            for (File file : allFiles) {
                if (!file.getAbsolutePath().endsWith(".properties")){
                   m_shipImages.addAll(readImageFile(file));
                }
            }
        } else {
            m_shipImages.addAll(readImageFile(new File(listFile)));
        }
        
        // Backup a Copy of the ShipImages list so we can cycle back through the list.
        m_backup = new ArrayList<>(m_shipImages);
    }
    
    /**
     * Gets and returns a random ShipImage from the Model.
     * @return 
     */
    public ShipImage getNextShip() {
        
        // If the list of ships to choose from is empty, repopulate it.
        if (m_shipImages.isEmpty()) {
            System.out.println("--- LOOP:  Finished all ships in list, repopulating from backup.");
            m_shipImages.addAll(m_backup);
        }
        
        // Select a next ship.
        int index = 0;
        if (m_randomize){
            //System.out.println("Getting next random ship.");
            index = m_random.nextInt(m_shipImages.size());    
        } else {
            //System.out.println("Getting next sequential ship.");
        }
        
        // Get the ship at t he selected index.
        ShipImage selected = m_shipImages.get(index);
        
        // Update selection history and remove the selected ship from the 
        // list of selectable ships.
        m_history.push(selected);
        m_shipImages.remove(index);
        
        // Return the selected ship.
        return selected;
    }
    
    /**
     * Returns the most recently selected ship.  If no ship was selected,
     * returns null.
     * 
     * @return The most recently selected ShipImage or null if none have been
     * selected.
     */
    public ShipImage getPreviousShip() {
        if(m_history.isEmpty()) {
            // If no history, return null.
            return null;
        } else {
            // ...otherwise, return the most recently selected ship.
            // This COULD be done with a single ShipImage item, but this
            // feature may expand and the Stack will be useful if so.
            return m_history.peek();
        }
    }
    
    /**
     * Given a File with names of images, read in all of the images and return a
     * ShipImage object.
     * 
     * @param file
     * @return
     * @throws IOException 
     */
    private List<ShipImage> readImageFile(File file) throws IOException {
        
       // Ship Image List
       List<ShipImage> images = new ArrayList<>();
        
       // Read in the Data File
       List<String> imageStrings = new ArrayList<>();
       imageStrings.addAll(FileTools.readAllLinesFromFile(file.getAbsolutePath()));

       // Construct and add a ShipImage to the list.
       for (String s : imageStrings) {
           ShipImage si = new ShipImage(new File("./media/pics/" + s), s);
           //ShipImage si = new ShipImage(new File("./media/pics/" + s + ".jpg"), s);
           images.add(si);
       }
       
       return images;
        
    }
}