package selectomatic;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class View {

    private Model m_model;

    private final JLabel m_label;
    private final JLabel m_image;
    private final JLabel m_counter;
    private final JFrame m_frame;

    private final Map<Integer, JCheckBox> tierBoxes = new LinkedHashMap<>();
    private final Map<Nation, JCheckBox> nationBoxes = new LinkedHashMap<>();
    private final Map<ShipClass, JCheckBox> classBoxes = new LinkedHashMap<>();

    public View() {
        m_label = new JLabel();
        m_image = new JLabel();
        m_counter = new JLabel();
        m_frame = new JFrame();
    }

    public void setup(Model dataModel, String frameTitle) throws IOException {

        // ================= MODEL =================
        m_model = dataModel;

        // ================= TITLE =================
        m_label.setText(" Select-O-Matic!");
        m_label.setHorizontalAlignment(JLabel.CENTER);
        m_label.setIcon(new ImageIcon("./media/clyde.png"));

        Font font;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            font = Font.createFont(Font.TRUETYPE_FONT,
                    new File("./media/WarHeliosCondCBold.ttf"))
                    .deriveFont(72f)
                    .deriveFont(Font.BOLD);
            ge.registerFont(font);
        } catch (Exception e) {
            font = m_label.getFont().deriveFont(72f).deriveFont(Font.BOLD);
        }
        m_label.setFont(font);

        // ================= COUNTER =================
        m_counter.setHorizontalAlignment(JLabel.CENTER);
        m_counter.setFont(new Font("SansSerif", Font.BOLD, 16));
        updateCounter();

        // ================= FRAME =================
        ImageIcon appIcon = new ImageIcon(ImageIO.read(new File("./media/appicon.png")));

        m_frame.setTitle(frameTitle);
        m_frame.setIconImage(appIcon.getImage());
        m_frame.setPreferredSize(new Dimension(1280, 840));
        m_frame.setResizable(false);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= IMAGE TAB =================
        JPanel imagePanel = new JPanel(new BorderLayout());
        m_image.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(m_image, BorderLayout.CENTER);

        // ================= FILTER TAB =================
        JPanel filterPanel = buildFilterPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ship", imagePanel);
        tabs.addTab("Filters", filterPanel);

        // ================= LAYOUT =================

        JPanel north = new JPanel(new BorderLayout());
        north.add(m_label, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(m_counter);

        m_frame.setLayout(new BorderLayout());
        m_frame.add(north, BorderLayout.NORTH);
        m_frame.add(tabs, BorderLayout.CENTER);
        m_frame.add(south, BorderLayout.SOUTH);

        // ================= SPACEBAR =================
        InputMap im = m_frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = m_frame.getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("SPACE"), "NEXT_SHIP");

        am.put("NEXT_SHIP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextShip();
                updateCounter();
            }
        });

        // ================= SHOW =================
        m_frame.pack();
        m_frame.setVisible(true);
    }

    // =====================================================
    // FILTER PANEL
    // =====================================================

    private JPanel buildFilterPanel() {

        JPanel root = new JPanel(new BorderLayout());
        JPanel columns = new JPanel(new GridLayout(1, 3));

        Font headerFont = new Font("SansSerif", Font.BOLD, 16);

        // ---------------- TIER ----------------
        JPanel tierPanel = new JPanel();
        tierPanel.setLayout(new BoxLayout(tierPanel, BoxLayout.Y_AXIS));

        JLabel tierLabel = new JLabel("Tiers");
        tierLabel.setFont(headerFont);
        tierPanel.add(tierLabel);

        for (int i = 1; i <= 11; i++) {
            JCheckBox cb = new JCheckBox(String.valueOf(i), true);
            tierBoxes.put(i, cb);
            tierPanel.add(cb);
        }

        // ---------------- NATION ----------------
        JPanel nationPanel = new JPanel();
        nationPanel.setLayout(new BoxLayout(nationPanel, BoxLayout.Y_AXIS));

        JLabel nationLabel = new JLabel("Nations");
        nationLabel.setFont(headerFont);
        nationPanel.add(nationLabel);

        for (Nation n : Nation.values()) {
            JCheckBox cb = new JCheckBox(n.getName(), true);
            nationBoxes.put(n, cb);
            nationPanel.add(cb);
        }

        // ---------------- CLASS ----------------
        JPanel classPanel = new JPanel();
        classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.Y_AXIS));

        JLabel classLabel = new JLabel("Classes");
        classLabel.setFont(headerFont);
        classPanel.add(classLabel);

        for (ShipClass sc : ShipClass.values()) {
            JCheckBox cb = new JCheckBox(sc.getName(), true);
            classBoxes.put(sc, cb);
            classPanel.add(cb);
        }

        columns.add(tierPanel);
        columns.add(nationPanel);
        columns.add(classPanel);

        // ---------------- BUTTONS ----------------
        JPanel buttons = new JPanel();

        JButton all = new JButton("Select All");
        JButton none = new JButton("Select None");

        all.addActionListener(e -> setAll(true));
        none.addActionListener(e -> setAll(false));

        buttons.add(all);
        buttons.add(none);

        // ---------------- LISTENER ----------------
        ItemListener listener = e -> {
            applyFilters();
            updateCounter();
        };

        tierBoxes.values().forEach(cb -> cb.addItemListener(listener));
        nationBoxes.values().forEach(cb -> cb.addItemListener(listener));
        classBoxes.values().forEach(cb -> cb.addItemListener(listener));

        root.add(columns, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    // =====================================================
    // FILTER ACTIONS
    // =====================================================

    private void setAll(boolean state) {

        tierBoxes.values().forEach(cb -> cb.setSelected(state));
        nationBoxes.values().forEach(cb -> cb.setSelected(state));
        classBoxes.values().forEach(cb -> cb.setSelected(state));

        applyFilters();
        updateCounter();
    }

    private void applyFilters() {

        Set<Integer> tiers = new HashSet<>();
        for (Map.Entry<Integer, JCheckBox> e : tierBoxes.entrySet()) {
            if (e.getValue().isSelected()) tiers.add(e.getKey());
        }

        Set<Nation> nations = new HashSet<>();
        for (Map.Entry<Nation, JCheckBox> e : nationBoxes.entrySet()) {
            if (e.getValue().isSelected()) nations.add(e.getKey());
        }

        Set<ShipClass> classes = new HashSet<>();
        for (Map.Entry<ShipClass, JCheckBox> e : classBoxes.entrySet()) {
            if (e.getValue().isSelected()) classes.add(e.getKey());
        }

        m_model.setSelectedTiers(tiers);
        m_model.setSelectedNations(nations);
        m_model.setSelectedClasses(classes);
    }

    // =====================================================
    // COUNTER
    // =====================================================

    private void updateCounter() {
        m_counter.setText(
                "Ships Available: "
                + m_model.getAvailableShipCount()
                + " / "
                + m_model.getTotalShipCount()
        );
    }

    // =====================================================
    // DISPLAY
    // =====================================================

    private void showNextShip() {

        ShipImage si = m_model.getNextShip();

        if (si == null) {
            m_label.setText("No ships match the selected filters.");
            m_image.setIcon(null);
            return;
        }

        String name = si.getName();
        String[] parts = name.split("\\.");
        m_label.setText("  " + parts[0]);

        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(si.getFile()));
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
            m_image.setIcon(new ImageIcon(scaled));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}