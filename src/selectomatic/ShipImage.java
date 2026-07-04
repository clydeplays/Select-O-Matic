package selectomatic;

import java.io.File;

/**
 * A very simple object to represent an image of a Ship.
 * @author Clyde Plays
 */
public class ShipImage {
    
    /** A File to represent a ship picture.  */
    private final File m_file;
    
    /** The Name of the ship to display above the image in the app. */
    private final Integer m_tier;
    
    /** The Name of the ship to display above the image in the app. */
    private final Nation m_nation;
    
    /** The Name of the ship to display above the image in the app. */
    private final ShipClass m_class;
    
    /** The Name of the ship to display above the image in the app. */
    private final String m_name;
        
    /**
     * An object which represents the ship image and its name.
     * @param filePath - the File object that represents the image itself.
     * @param shipName - the ship's name, used to display the name of the ship in the GUI.
     */
    public ShipImage(File filePath, String shipName) {
        m_file = filePath;
        
        // Parse Shipname
        String[] parts = shipName.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("shipName did not have correct number of parts - expected format was: 'T#,Nation,Class,Name' but value was:  " + shipName);
        }
        
        // Initialize the values.
        m_tier = Integer.valueOf(parts[0]);
        m_nation = Nation.valueOf(parts[1]);
        m_class = ShipClass.valueOf(parts[2]);
        m_name = parts[3];
    }
    
    
    /**
     * Returns the image File object.
     * @return the filename as a File.
     */
    public File getFile() {
        return m_file;
    }
    
    /**
     * Returns the filename as a String.  The file only has the name of the ship and
     * includes the file extension (such as ".jpg" or ".png" at the end).
     * @return the filename as a String.
     */
    public String getName() {
        return m_name;
    }
    
    public int getTier() {
        return m_tier;
    }
    
    public Nation getNation() {
        return m_nation;
    }
    
    public ShipClass getShipClass() {
        return m_class;
    }
}
