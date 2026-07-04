package selectomatic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class View {

    private Model m_model;

    private final JFrame m_frame;
    private final JLabel m_label;
    private final JLabel m_image;
    private final JLabel m_counter;

    private final Map<Integer, JCheckBox> tierBoxes = new LinkedHashMap<>();
    private final Map<Nation, JCheckBox> nationBoxes = new LinkedHashMap<>();
    private final Map<ShipClass, JCheckBox> classBoxes = new LinkedHashMap<>();

    // ---------------- LRU IMAGE CACHE ----------------
    private final Map<ShipImage, ImageIcon> m_cache =
            new LinkedHashMap<ShipImage, ImageIcon>(32, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<ShipImage, ImageIcon> eldest) {
                    return size() > 25;
                }
            };

    public View() {
        m_frame = new JFrame();
        m_frame.setIconImage(new ImageIcon("./media/appicon.png").getImage());
        m_label = new JLabel();
        m_image = new JLabel();
        m_counter = new JLabel();
    }

    public void setup(Model dataModel, String frameTitle) throws IOException {

        m_model = dataModel;

        // ---------------- HEADER ----------------
        m_label.setText(" Select-O-Matic!");
        m_label.setHorizontalAlignment(SwingConstants.CENTER);
        m_label.setIcon(new ImageIcon("./media/clyde.png"));

        Font font;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            font = Font.createFont(Font.TRUETYPE_FONT,
                    new File("./media/WarHeliosCondCBold.ttf"))
                    .deriveFont(72f)
                    .deriveFont(Font.BOLD);
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            font = m_label.getFont().deriveFont(72f).deriveFont(Font.BOLD);
        }

        m_label.setFont(font);

        // ---------------- COUNTER ----------------
        m_counter.setHorizontalAlignment(SwingConstants.CENTER);
        m_counter.setFont(new Font("SansSerif", Font.BOLD, 16));
        updateCounter();

        // ---------------- FRAME ----------------
        m_frame.setTitle(frameTitle);
        m_frame.setPreferredSize(new Dimension(1280, 840));
        m_frame.setResizable(false);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---------------- IMAGE PANEL ----------------
        JPanel imagePanel = new JPanel(new BorderLayout());
        m_image.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(m_image, BorderLayout.CENTER);

        // ---------------- FILTER PANEL ----------------
        JPanel filterPanel = buildFilterPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ship", imagePanel);
        tabs.addTab("Filters", filterPanel);

        // ---------------- LAYOUT ----------------
        JPanel north = new JPanel(new BorderLayout());
        north.add(m_label, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(m_counter);

        m_frame.setLayout(new BorderLayout());
        m_frame.add(north, BorderLayout.NORTH);
        m_frame.add(tabs, BorderLayout.CENTER);
        m_frame.add(south, BorderLayout.SOUTH);

        // ---------------- SPACEBAR ACTION ----------------
        InputMap im = m_frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = m_frame.getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("SPACE"), "NEXT_SHIP");

        am.put("NEXT_SHIP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ShipImage si = m_model.getNextShip();

                // 🔒 If exhausted under current filter set
                if (si == null) {

                    // force model to rebuild using CURRENT filters
                    applyFilters();

                    si = m_model.getNextShip();

                    // if STILL null → no ships match filters at all
                    if (si == null) {
                        return;
                    }
                }

                showShip(si);
                updateCounter();
            }
        });

        m_frame.pack();
        m_frame.setVisible(true);
    }

    // =====================================================
    // DISPLAY IMAGE (WITH CACHE)
    // =====================================================

    private void showShip(ShipImage si) {

        if (si == null) return;

        ImageIcon icon = m_cache.get(si);

        if (icon == null) {
            try {
                Image img = ImageIO.read(si.getFile());
                Image scaled = img.getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);
                m_cache.put(si, icon);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        m_image.setIcon(icon);

        String name = si.getName();
        String[] parts = name.split("\\.");
        m_label.setText("  " + parts[0]);
    }

    // =====================================================
    // FILTER PANEL
    // =====================================================

    private JPanel buildFilterPanel() {

        JPanel root = new JPanel(new BorderLayout());
        JPanel columns = new JPanel(new java.awt.GridLayout(1, 3));

        Font headerFont = new Font("SansSerif", Font.BOLD, 20);

        JPanel tierPanel = new JPanel();
        tierPanel.setLayout(new javax.swing.BoxLayout(tierPanel, javax.swing.BoxLayout.Y_AXIS));

        JLabel tierLabel = new JLabel("Tiers");
        tierLabel.setFont(headerFont);
        tierPanel.add(tierLabel);

        for (int i = 1; i <= 11; i++) {
            JCheckBox cb;
            if (i == 11) {
                cb = new JCheckBox("★", true);
            } else {
                cb = new JCheckBox(String.valueOf(i), true);
            }
            tierBoxes.put(i, cb);
            tierPanel.add(cb);
        }

        JPanel nationPanel = new JPanel();
        nationPanel.setLayout(new javax.swing.BoxLayout(nationPanel, javax.swing.BoxLayout.Y_AXIS));

        JLabel nationLabel = new JLabel("Nations");
        nationLabel.setFont(headerFont);
        nationPanel.add(nationLabel);

        for (Nation n : Nation.values()) {
            JCheckBox cb = new JCheckBox(n.getName(), true);
            nationBoxes.put(n, cb);
            nationPanel.add(cb);
        }

        JPanel classPanel = new JPanel();
        classPanel.setLayout(new javax.swing.BoxLayout(classPanel, javax.swing.BoxLayout.Y_AXIS));

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

        JPanel buttons = new JPanel();

        javax.swing.JButton all = new javax.swing.JButton("Select All");
        javax.swing.JButton none = new javax.swing.JButton("Select None");

        all.addActionListener(e -> setAll(true));
        none.addActionListener(e -> setAll(false));

        buttons.add(all);
        buttons.add(none);

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
    // FILTER LOGIC
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
}