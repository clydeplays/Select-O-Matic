package selectomatic;

import core.util.file.FileTools;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;


public class Model {

    private List<ShipImage> m_shipImages;
    private List<ShipImage> m_backup;
    private final Stack<ShipImage> m_history;

    private boolean m_randomize = false;
    private static final Random m_random = new Random();

    // ================= FILTER STATE =================
    private Set<Integer> m_selectedTiers = new HashSet<>();
    private Set<Nation> m_selectedNations = new HashSet<>();
    private Set<ShipClass> m_selectedClasses = new HashSet<>();

    public Model(String listFile, boolean randomize) throws IOException {

        m_shipImages = new ArrayList<>();
        m_backup = new ArrayList<>();
        m_history = new Stack<>();
        m_randomize = randomize;

        if (listFile.equals("all")) {
            File dataDir = new File("./data/");
            File[] allFiles = dataDir.listFiles();

            for (File file : allFiles) {
                if (!file.getAbsolutePath().endsWith(".properties")) {
                    m_shipImages.addAll(readImageFile(file));
                }
            }
        } else {
            m_shipImages.addAll(readImageFile(new File(listFile)));
        }

        m_backup = new ArrayList<>(m_shipImages);
    }

    // =====================================================
    // FILTER UPDATES
    // =====================================================

    public void setSelectedTiers(Set<Integer> tiers) {
        m_selectedTiers = (tiers == null) ? new HashSet<>() : new HashSet<>(tiers);
        applyFilters();
    }

    public void setSelectedNations(Set<Nation> nations) {
        m_selectedNations = (nations == null) ? new HashSet<>() : new HashSet<>(nations);
        applyFilters();
    }

    public void setSelectedClasses(Set<ShipClass> classes) {
        m_selectedClasses = (classes == null) ? new HashSet<>() : new HashSet<>(classes);
        applyFilters();
    }

    // =====================================================
    // CORE FILTER LOGIC
    // =====================================================

    private void applyFilters() {

        List<ShipImage> filtered = new ArrayList<>();

        for (ShipImage si : m_backup) {

            boolean tierOk = m_selectedTiers.isEmpty() || m_selectedTiers.contains(si.getTier());
            boolean nationOk = m_selectedNations.isEmpty() || m_selectedNations.contains(si.getNation());
            boolean classOk = m_selectedClasses.isEmpty() || m_selectedClasses.contains(si.getShipClass());

            if (tierOk && nationOk && classOk) {
                filtered.add(si);
            }
        }

        m_shipImages = filtered;
    }

    // =====================================================
    // SHIP SELECTION
    // =====================================================

    public ShipImage getNextShip() {

        if (m_shipImages.isEmpty()) {
            System.out.println("Refilling using active filters.");

            applyFilters(); // rebuild from backup using CURRENT filters

            // Safety check: if filters exclude everything
            if (m_shipImages.isEmpty()) {
                System.out.println("No ships match current filters.");
                return null;
            }
        }

        int index = m_randomize
                ? m_random.nextInt(m_shipImages.size())
                : 0;

        ShipImage selected = m_shipImages.get(index);

        m_history.push(selected);
        m_shipImages.remove(index);

        return selected;
    }

    public ShipImage getPreviousShip() {
        return m_history.isEmpty() ? null : m_history.peek();
    }
    
    
    public List<ShipImage> getSnapshot() {
        return new ArrayList<>(m_shipImages);
    }

    // =====================================================
    // COUNTERS (NEW)
    // =====================================================

    public int getAvailableShipCount() {
        return m_shipImages.size();
    }

    public int getTotalShipCount() {
        return m_backup.size();
    }

    // =====================================================
    // FILE LOADING
    // =====================================================

    private List<ShipImage> readImageFile(File file) throws IOException {

        List<ShipImage> images = new ArrayList<>();
        List<String> imageStrings = FileTools.readAllLinesFromFile(file.getAbsolutePath());

        for (String s : imageStrings) {
            ShipImage si = new ShipImage(new File("./media/pics/" + s), s);
            images.add(si);
        }

        return images;
    }
}