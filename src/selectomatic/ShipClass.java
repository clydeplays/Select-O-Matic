package selectomatic;

/**
 *
 * @author Clyde Plays
 */
public enum ShipClass {
    BB("Battleship"),
    CA("Cruiser (Heavy)"),
    CL("Cruiser (Light)"),
    CV("Aircraft Carrier"),
    DD("Destroyer"),
    SS("Submarine");
    
    private final String m_name;
    
    /**
     * Creates a new 'ShipClass'
     * @param name the name of the shipclass in words.
     */
    private ShipClass(String name){
        m_name = name;
    }
    
    /**
     * Returns the name of the class in words.
     * @return the name of the class in words.
     */
    public String getName() {
        return m_name;
    }
}
