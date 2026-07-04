package selectomatic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Clyde Plays
 */
public class View {

    /** The Label. */
    private final JLabel m_label;
    
    /** The Image. */
    private final JLabel m_image;
    
    /** A window to contain it all. */
    private final JFrame m_frame;
    
    /** Initialize view variables. */
    public View(){
        m_label = new JLabel();
        m_image = new JLabel();
        m_frame = new JFrame();
    }
    
    /**
     * Create and initialize all of the components of the application.
     * @param dataModel the backing data model that contains the ships.
     * @param frameTitle the title to put in the JFrame window.
     * @throws IOException files are involved, may throw an error while we set up and read pics, fonts, etc.
     */
    public void setup(Model dataModel, String frameTitle) throws IOException {
        // --- Initialize the JLabel
        m_label.setText(" Select-O-Matic!");
        m_label.setHorizontalAlignment(JLabel.CENTER);
        m_label.setVerticalAlignment(JLabel.CENTER);
        m_label.setIcon(new ImageIcon("./media/clyde.png"));
        
        // --- Set the Font
        Font font;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            font = Font.createFont(Font.TRUETYPE_FONT, new File("./media/WarHeliosCondCBold.ttf"));
            font = font.deriveFont(72.0f).deriveFont(Font.BOLD);
            ge.registerFont(font);
        } catch (IOException|FontFormatException e) {
            // This is lame, and poor error handling, but this is a dirt-simple app.  Shrug.
            e.printStackTrace();
            font = m_label.getFont().deriveFont(72.0f).deriveFont(Font.BOLD);
        }
        m_label.setFont(font);
        
        // Read Application Icon Image File
        ImageIcon appIcon = new ImageIcon(ImageIO.read(new File("./media/appicon.png")));
        
        // Lay it out in the Frame.
        m_frame.setTitle(frameTitle);
        m_frame.setIconImage(appIcon.getImage());
        m_frame.setPreferredSize(new Dimension(1280, 840));
        m_frame.setResizable(false);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        // Set Keybindings
        String nextShipKey = "Next Ship";
        m_label.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true ), nextShipKey);
        AbstractAction nextShip = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        // Get the Next ShipImage
                        ShipImage si = dataModel.getNextShip();
                        String name = si.getName();
                        System.out.println("Name:  " + name);
                        String parts[] = name.split("\\.");
                        m_label.setText("  " + parts[0]);
                        try {
                            ImageIcon icon = new ImageIcon(ImageIO.read(si.getFile()));
                            Image img = icon.getImage();
                            Image scaled_img = img.getScaledInstance(1280, 720,  java.awt.Image.SCALE_SMOOTH);
                            icon = new ImageIcon(scaled_img);
                            m_image.setIcon(icon);
                        } catch (IOException ex) {
                            Logger.getLogger(SelectOMatic.class.getName()).log(Level.SEVERE,"Error finding ship image file.", ex);
                        }
                    }
                });
            }
        };

        // Set Action map on label
        m_label.getActionMap().put(nextShipKey, nextShip);
        
        // Layout Components
        m_frame.setLayout(new BorderLayout());
        m_frame.add(m_label, BorderLayout.PAGE_START);
        m_frame.add(m_image, BorderLayout.CENTER);
        m_frame.pack();
        m_frame.setVisible(true);
    }
    
}
