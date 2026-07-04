package selectomatic;

/**
 *
 * @author Clyde Plays
 */
public enum Nation {
    ASIA("Pan Asia"),
    COM("Commonwealth"),
    EURO("Pan Europe"),
    ESP("Spain"),
    FRA("France"),
    GER("Germany"),
    ITA("Italy"),
    JPN("Japan"),
    NETH("The Netherlands"),
    PANAM("Pan America"),
    USA("United States of America"),
    UK("United Kingdom"),
    USSR("Union of Soviet Socialistic Republics");
    
    /** The long form name of the country*/
    private final String m_name;
    
    /**
     * Creates a new 'Nation'
     * @param name the name of the nation in words.
     */
    private Nation(String name){
        m_name = name;
    }
    
    /**
     * Returns the name of the nation in words.
     * @return the name of the nation in words.
     */
    public String getName() {
        return m_name;
    }
}
