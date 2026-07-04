package selectomatic;

import core.util.file.FileTools;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The data model that tracks what ship is next.
 * Now supports filtering by tier, nation, and ship class.
 */
public class Model {

    /** All ships loaded from disk (never modified). */
    private final List<ShipImage> m_allShips;

    /** Ships currently eligible based on filters. */
    private List<ShipImage> m_availableShips;

    /** Ships already shown (prevents repeats until reset). */
    private List<ShipImage> m_remainingShips;

    /** History stack (last shown ships). */
    private final Stack<ShipImage> m_history;

    /** Filters */
    private Set<Integer> m_selectedTiers;
    private Set<Nation> m_selectedNations;
    private Set<ShipClass> m_selectedClasses;

    private final Random m_random = new Random();

    /**
     * Constructor
     * @param listFile
     * @param randomize
     * @throws java.io.IOException
     */
    public Model(String listFile, boolean randomize) throws IOException {

        m_allShips = new ArrayList<>();
        m_availableShips = new ArrayList<>();
        m_remainingShips = new ArrayList<>();
        m_history = new Stack<>();

        // Load ships
        if (listFile.equals("all")) {
            File dataDir = new File("./data/");
            File[] allFiles = dataDir.listFiles();

            if (allFiles != null) {
                for (File file : allFiles) {
                    if (!file.getAbsolutePath().endsWith(".properties")) {
                        m_allShips.addAll(readImageFile(file));
                    }
                }
            }
        } else {
            m_allShips.addAll(readImageFile(new File(listFile)));
        }

        // Init filters (ALL enabled by default)
        m_selectedTiers = new HashSet<>();
        for (int i = 1; i <= 11; i++) {
            m_selectedTiers.add(i);
        }

        m_selectedNations = EnumSet.allOf(Nation.class);
        m_selectedClasses = EnumSet.allOf(ShipClass.class);

        // Build initial pool
        rebuildAvailableShips();
    }

    /**
     * Apply filtering logic and rebuild available ship list.
     */
    private void rebuildAvailableShips() {

        m_availableShips = m_allShips.stream()
                .filter(s -> m_selectedTiers.contains(s.getTier()))
                .filter(s -> m_selectedNations.contains(s.getNation()))
                .filter(s -> m_selectedClasses.contains(s.getShipClass()))
                .collect(Collectors.toList());

        // Reset remaining pool whenever filters change
        m_remainingShips = new ArrayList<>(m_availableShips);
    }

    /**
     * Update filters
     * @param tiers
     */
    public void setSelectedTiers(Set<Integer> tiers) {
        m_selectedTiers = new HashSet<>(tiers);
        rebuildAvailableShips();
    }

    public void setSelectedNations(Set<Nation> nations) {
        m_selectedNations = EnumSet.copyOf(nations);
        rebuildAvailableShips();
    }

    public void setSelectedClasses(Set<ShipClass> classes) {
        m_selectedClasses = EnumSet.copyOf(classes);
        rebuildAvailableShips();
    }

    /**
     * Get next ship (filtered + no-repeat until exhausted)
     * @return 
     */
    public ShipImage getNextShip() {

        if (m_availableShips.isEmpty()) {
            return null;
        }

        // Reset cycle if needed
        if (m_remainingShips.isEmpty()) {
            m_remainingShips = new ArrayList<>(m_availableShips);
            System.out.println("Filter cycle complete. Resetting available ships.");
        }

        int index = m_random.nextInt(m_remainingShips.size());
        ShipImage selected = m_remainingShips.get(index);

        m_remainingShips.remove(index);
        m_history.push(selected);

        return selected;
    }

    /**
     * Returns last selected ship
     */
    public ShipImage getPreviousShip() {
        if (m_history.isEmpty()) {
            return null;
        }
        return m_history.peek();
    }

    /**
     * Read ship list file and convert into ShipImage objects
     */
    private List<ShipImage> readImageFile(File file) throws IOException {

        List<ShipImage> images = new ArrayList<>();

        List<String> imageStrings = new ArrayList<>();
        imageStrings.addAll(FileTools.readAllLinesFromFile(file.getAbsolutePath()));

        for (String s : imageStrings) {
            ShipImage si = new ShipImage(new File("./media/pics/" + s), s);
            images.add(si);
        }

        return images;
    }
}