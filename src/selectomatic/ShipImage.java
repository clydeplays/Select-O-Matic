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
    private final String m_name;
    
    /**
     * An object which represents the ship image and its name.
     * @param filePath - the File object that represents the image itself.
     * @param shipName - the ship's name, used to display the name of the ship in the GUI.
     */
    public ShipImage(File filePath, String shipName) {
        m_file = filePath;
        m_name = shipName;
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
     * an extension (such as ".jpg" or ".png" at the end).
     * @return the filename as a String.
     */
    public String getName() {
        return m_name;
    }
}
