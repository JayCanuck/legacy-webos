/*
 * WebOSThemeBuilderView.java
 */

package ca.canucksoftware.themebuilder;

import java.awt.AlphaComposite;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.TimerTask;
import java.util.Timer;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * The application's main frame.
 */
public class MainView extends FrameView {
    private boolean pageLoaded;
    private File zipOutDir;
    private Image bg;
    private Timer t;
    private ZipTheme theme;
    private VersionTheme currVersion;
    private String currCategory;
    private List<FileEntry> currFiles;
    private List<IconEntry> currIcons;
    private List<PatchEntry> currPatches;
    private File prevDir;

    public MainView(SingleFrameApplication app) {
        super(app);
        URL imgURL = getClass().getResource("resources/icon.png");
        getFrame().setIconImage(new ImageIcon(imgURL).getImage());
        URL bgURL = getClass().getResource("resources/bg.jpg");
        bg = new ImageIcon(bgURL).getImage();
        pageLoaded = false;
        initComponents();
        zipOutDir = null;
        theme = new ZipTheme();
        String prevDirStr = Preferences.userRoot().get("prevDir", null);
        prevDir = null;
        if(prevDirStr!=null) {
            prevDir = new File(prevDirStr);
            if(!prevDir.exists()) {
                prevDir = null;
            }
        }
        currCategory = "--all--";
        t = new Timer();
        jButton6.setContentAreaFilled(false);
        jButton6.setBorderPainted(false);
        jButton6.setEnabled(false);
        jButton6.setIcon(null);
        LeftEllipsisRenderer ler = new LeftEllipsisRenderer();
        jTable1.getColumn("File").setCellRenderer(ler);
        jTable1.getColumn("Destination on Device").setCellRenderer(ler);
        jTable2.getColumn("Image File").setCellRenderer(ler);
        jTable2.getColumn("Application ID").setCellRenderer(ler);
        jTable3.getColumn("Patch File").setCellRenderer(ler);
        WindowListener wl = new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                Preferences.userRoot().put("prevDir", prevDir.getAbsolutePath());
                if(zipOutDir!=null && zipOutDir.exists()) {
                    deleteDirectory(zipOutDir);
                    if(zipOutDir.exists()) {
                        zipOutDir.delete();
                    }
                }
                System.exit(0);
            }
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        };
        super.getFrame().addWindowListener(wl);
        t.schedule(new DoDelayedLoad(), 50);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
            aboutBox = new AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        WebOSThemeBuilderApp.getApplication().show(aboutBox);
    }

    private File loadFileChooser(javax.swing.filechooser.FileFilter ff, boolean isSave) {
        File result;
        JFileChooser fc = new JFileChooser(); //Create a file chooser
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        if(prevDir!=null)
            fc.setCurrentDirectory(prevDir);
        fc.setFileFilter(ff);
        if(!isSave) {
            fc.setDialogTitle("");
            if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
            } else {
                result = null;
            }
        } else {
            fc.setSelectedFile(new File(prevDir, theme.id + ".zip"));
            fc.setDialogTitle("Save As...");
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
            } else {
                result = null;
            }
        }
        if(result!=null)
            prevDir = result.getParentFile();
        return result;
    }

    private void updateSSList() {
        LinkedList<String> names = new LinkedList<String>();
        for(int i=0; i<theme.screenshots.size(); i++) {
            names.add(getFilename(theme.screenshots.get(i)));
        }
        jList2.setListData(names.toArray());
    }

    private String getFilename(File f) {
        return getFilename(f.getName());
    }
    
    private String getFilename(String f) {
        String result = f;
        if(result.lastIndexOf("/")!=-1) {
            result = result.substring(result.lastIndexOf("/")+1);
        }
        if(result.lastIndexOf("\\")!=-1) {
            result = result.substring(result.lastIndexOf("\\")+1);
        }
        return result;
    }

    public boolean deleteDirectory(File path) {
        if(path.exists()) {
            File[] curr = path.listFiles();
            for(int i=0; i<curr.length; i++) {
                if(curr[i].isDirectory()) {
                    deleteDirectory(curr[i]);
                } else {
                    curr[i].delete();
                }
            }
        }
        return(path.delete());
    }

    private void changeState(boolean val) {
        pageLoaded = val;
        jTextField1.setEnabled(val);
        jTextField2.setEnabled(val);
        jTextField3.setEnabled(val);
        jTextField4.setEnabled(val);
        jTextField7.setEnabled(val);
        jTextArea1.setEnabled(val);
        jList2.setEnabled(val);
        jButton2.setEnabled(val);
        jButton1.setEnabled(val);
        jTextField5.setEnabled(val);
        jTextField6.setEnabled(val);
        jButton4.setEnabled(val);
        jComboBox1.setEnabled(val);
        jComboBox2.setEnabled(val);
        jButton6.setEnabled(val);
        jButton7.setEnabled(val);
        jButton5.setEnabled(val);
        jButton3.setEnabled(val);
        jTabbedPane1.setEnabled(val);
        if(val) {
            jButton3.setText("Build Theme Zip");
        } else {
            jButton3.setText("Please wait...");
        }
        getRootPane().requestFocus();
    }

    private boolean infoGood() {
        if(jTextField1.getText().length()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme name.");
            return false;
        }
        if(jTextField2.getText().length()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme version.");
            return false;
        }
        if(jTextField3.getText().length()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme creator.");
            return false;
        }
        if(jTextField4.getText().length()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme homepage URL.");
            return false;
        }
        if(jTextField7.getText().length()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme theme ID.");
            return false;
        }
        if(jTextArea1.getText().length()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme description.");
            return false;
        }
        if(jList2.getModel().getSize()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing screenshots.\nAt least 1 screenshot" +
                    " is required.");
            return false;
        }
        if(theme.devices.size()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing compatible device.\nAt least 1 compatable device" +
                    " is required.");
        }
        if(theme.controller.listVersions().size()==0) {
            JOptionPane.showMessageDialog(mainPanel, "ERROR: Missing theme data.\nAt least 1 compatable webOS version" +
                    " is required.");
            return false;
        }
        return true;
    }

    private void reset() {
        changeState(false);
        if(zipOutDir!=null && zipOutDir.exists()) {
            deleteDirectory(zipOutDir);
            if(zipOutDir.exists()) {
                zipOutDir.delete();
            }
        }
        jTabbedPane1.setSelectedIndex(0);
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jComboBox1.removeAllItems();
        jComboBox2.setSelectedIndex(0);
        jTextArea1.setText("");
        jList2.setListData(new Object[] {});
        theme.reset();
        currCategory = "--all--";
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        while(dtm.getRowCount()>0)
            dtm.removeRow(0);
        dtm = (DefaultTableModel) jTable2.getModel();
        while(dtm.getRowCount()>0)
            dtm.removeRow(0);
        dtm = (DefaultTableModel) jTable3.getModel();
        while(dtm.getRowCount()>0)
            dtm.removeRow(0);
        changeState(true);
    }

    private void loadThemeMetaData() {
        if(theme.name.trim().length()>0)
            jTextField1.setText(theme.name);
        if(theme.version.trim().length()>0)
            jTextField2.setText(theme.version);
        if(theme.creator.trim().length()>0)
            jTextField3.setText(theme.creator);
        if(theme.website.trim().length()>0)
            jTextField4.setText(theme.website);
        if(theme.donateURL.trim().length()>0)
            jTextField6.setText(theme.donateURL);
        if(theme.id.trim().length()>0)
            jTextField7.setText(theme.id);
        if(theme.description.trim().length()>0)
            jTextArea1.setText(theme.description);
        if(theme.screenshots.size()>0)
            updateSSList();
    }

    private void loadTheme() {
        jComboBox1.removeAllItems();
        List<String> versions = theme.controller.list(false);
        for(int i=0; i<versions.size(); i++) {
            jComboBox1.addItem("webOS " + versions.get(i));
        }
        if(versions.size()>0) {
            jComboBox1.setSelectedIndex(0);
            currVersion = theme.controller.getVersion(versions.get(0));
            loadVersion(false);
        }
    }

    private void loadVersion(boolean onlyCategoryChanged) {
        if(theme.controller.listVersions().size()==0) {
            return;
        }
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        while(dtm.getRowCount()>0) {
            dtm.removeRow(0);
        }
        currFiles = currVersion.filesByCategory(currCategory);
        for(int i=0; i<currFiles.size(); i++) {
            if(!(currCategory.equals("wallpapers") && i==0)) {
                dtm.addRow(new Object[] {currFiles.get(i).file.getPath(), currFiles.get(i).dest});
            }
        }
        if(!onlyCategoryChanged) {
            if(currVersion.wallpaper!=null) {
                jTextField5.setText(currVersion.wallpaper.getPath());
                jTextField5.setCaretPosition(jTextField5.getText().length());
            } else {
                jTextField5.setText("");
            }
            dtm = (DefaultTableModel) jTable2.getModel();
            while(dtm.getRowCount()>0) {
                dtm.removeRow(0);
            }
            currIcons = currVersion.icons;
            for(int i=0; i<currIcons.size(); i++) {
                dtm.addRow(new Object[] {currIcons.get(i).image.getPath(), currIcons.get(i).appID});
            }
        }
        dtm = (DefaultTableModel) jTable3.getModel();
        while(dtm.getRowCount()>0) {
            dtm.removeRow(0);
        }
        currPatches = currVersion.patchesByCategory(currCategory);
        for(int i=0; i<currPatches.size(); i++) {
            dtm.addRow(new Object[] {currPatches.get(i).file.getPath()});
        }

    }

    private void loadCompatManager() {
        JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
        compatBox = new CompatibleDevicesVersions(mainFrame, theme);
        compatBox.setLocationRelativeTo(mainFrame);
        WebOSThemeBuilderApp.getApplication().show(compatBox);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new ImagePanel(bg);
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new TranslucentPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLayeredPane3 = new TranslucentPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLayeredPane5 = new javax.swing.JLayeredPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLayeredPane6 = new javax.swing.JLayeredPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        jLayeredPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLayeredPane2.setName("jLayeredPane2"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ca.canucksoftware.themebuilder.WebOSThemeBuilderApp.class).getContext().getResourceMap(MainView.class);
        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setBounds(10, 40, 70, 20);
        jLayeredPane2.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setName("jTextField4"); // NOI18N
        jTextField4.setBounds(110, 100, 250, 22);
        jLayeredPane2.add(jTextField4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane3.setViewportView(jTextArea1);

        jScrollPane3.setBounds(370, 65, 250, 85);
        jLayeredPane2.add(jScrollPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setBackground(resourceMap.getColor("jButton2.background")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.setBounds(850, 40, 30, 30);
        jLayeredPane2.add(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setBounds(370, 35, 100, 30);
        jLayeredPane2.add(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton1.setBackground(resourceMap.getColor("jButton1.background")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.setBounds(850, 80, 30, 30);
        jLayeredPane2.add(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setBounds(370, 10, 80, 20);
        jLayeredPane2.add(jLabel11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.setName("jList2"); // NOI18N
        jScrollPane2.setViewportView(jList2);

        jScrollPane2.setBounds(640, 40, 200, 110);
        jLayeredPane2.add(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(10, 10, 90, 20);
        jLayeredPane2.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setBounds(10, 70, 80, 20);
        jLayeredPane2.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField7.setBackground(resourceMap.getColor("jTextField7.background")); // NOI18N
        jTextField7.setName("jTextField7"); // NOI18N
        jTextField7.setBounds(450, 10, 170, 22);
        jLayeredPane2.add(jTextField7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setBounds(640, 10, 190, 30);
        jLayeredPane2.add(jLabel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setBounds(10, 100, 80, 20);
        jLayeredPane2.add(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N
        jTextField3.setBounds(110, 70, 200, 22);
        jLayeredPane2.add(jTextField3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setBounds(10, 130, 100, 20);
        jLayeredPane2.add(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.setBounds(110, 10, 200, 22);
        jLayeredPane2.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.setBounds(110, 40, 80, 22);
        jLayeredPane2.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField6.setName("jTextField6"); // NOI18N
        jTextField6.setBounds(110, 130, 250, 22);
        jLayeredPane2.add(jTextField6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setBounds(10, 10, 900, 160);
        jLayeredPane1.add(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton3.setBackground(resourceMap.getColor("jButton3.background")); // NOI18N
        jButton3.setFont(resourceMap.getFont("jButton3.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.setBounds(380, 620, 170, 30);
        jLayeredPane1.add(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLayeredPane3.setName("jLayeredPane3"); // NOI18N

        jTabbedPane1.setBackground(resourceMap.getColor("jTabbedPane1.background")); // NOI18N
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.setOpaque(true);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jLayeredPane4.setBackground(resourceMap.getColor("jLayeredPane4.background")); // NOI18N
        jLayeredPane4.setName("jLayeredPane4"); // NOI18N
        jLayeredPane4.setOpaque(true);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Destination on Device"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFillsViewportHeight(true);
        jTable1.setName("jTable1"); // NOI18N
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
        jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N

        jScrollPane1.setBounds(20, 10, 760, 280);
        jLayeredPane4.add(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab("Files to Replace", jLayeredPane4);

        jLayeredPane5.setBackground(resourceMap.getColor("jLayeredPane5.background")); // NOI18N
        jLayeredPane5.setName("jLayeredPane5"); // NOI18N
        jLayeredPane5.setOpaque(true);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Image File", "Application ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setFillsViewportHeight(true);
        jTable2.setName("jTable2"); // NOI18N
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable2);
        jTable2.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable2.columnModel.title0")); // NOI18N
        jTable2.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable2.columnModel.title1")); // NOI18N

        jScrollPane4.setBounds(20, 10, 760, 280);
        jLayeredPane5.add(jScrollPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab("App Icons", jLayeredPane5);

        jLayeredPane6.setBackground(resourceMap.getColor("jLayeredPane6.background")); // NOI18N
        jLayeredPane6.setName("jLayeredPane6"); // NOI18N
        jLayeredPane6.setOpaque(true);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Patch File"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setFillsViewportHeight(true);
        jTable3.setName("jTable3"); // NOI18N
        jTable3.getTableHeader().setReorderingAllowed(false);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTable3);
        jTable3.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable3.columnModel.title0")); // NOI18N

        jScrollPane5.setBounds(20, 10, 760, 280);
        jLayeredPane6.add(jScrollPane5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab("Patches", jLayeredPane6);

        jTabbedPane1.setBounds(20, 90, 800, 330);
        jLayeredPane3.add(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton6.setBackground(resourceMap.getColor("jButton6.background")); // NOI18N
        jButton6.setIcon(resourceMap.getIcon("jButton6.icon")); // NOI18N
        jButton6.setIconTextGap(0);
        jButton6.setMargin(new java.awt.Insets(0, 2, 2, 2));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jButton6.setBounds(840, 250, 40, 40);
        jLayeredPane3.add(jButton6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jSeparator2.setBounds(20, 50, 860, 20);
        jLayeredPane3.add(jSeparator2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setBounds(370, 10, 90, 30);
        jLayeredPane3.add(jLabel9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox2.setBackground(resourceMap.getColor("jComboBox2.background")); // NOI18N
        jComboBox2.setMaximumRowCount(14);
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--All--", "App Icons", "App Launcher", "Applications", "Boot Logo", "Enyo Widgets", "Exhibition", "Just Type", "Keyboard", "Lock Screen", "Quick Launcher", "Status Bar", "System Menus", "Wallpapers" }));
        jComboBox2.setName("jComboBox2"); // NOI18N
        jComboBox2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBox2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        jComboBox2.setBounds(460, 15, 130, 20);
        jLayeredPane3.add(jComboBox2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setBounds(20, 60, 150, 20);
        jLayeredPane3.add(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField5.setName("jTextField5"); // NOI18N
        jTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField5MousePressed(evt);
            }
        });
        jTextField5.setBounds(170, 60, 630, 22);
        jLayeredPane3.add(jTextField5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox1.setBackground(resourceMap.getColor("jComboBox1.background")); // NOI18N
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBox1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBox1PopupMenuWillBecomeVisible(evt);
            }
        });
        jComboBox1.setBounds(200, 15, 140, 20);
        jLayeredPane3.add(jComboBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setBounds(20, 10, 180, 30);
        jLayeredPane3.add(jLabel10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton4.setBackground(resourceMap.getColor("jButton4.background")); // NOI18N
        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton4.setBounds(640, 10, 240, 30);
        jLayeredPane3.add(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton5.setBackground(resourceMap.getColor("jButton5.background")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jButton5.setBounds(840, 190, 40, 40);
        jLayeredPane3.add(jButton5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton7.setBackground(resourceMap.getColor("jButton7.background")); // NOI18N
        jButton7.setIcon(resourceMap.getIcon("jButton7.icon")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jButton7.setBounds(840, 130, 40, 40);
        jLayeredPane3.add(jButton7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBounds(10, 180, 900, 430);
        jLayeredPane1.add(jLayeredPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 919, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ca.canucksoftware.themebuilder.WebOSThemeBuilderApp.class).getContext().getActionMap(MainView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    //tab changed
    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(pageLoaded) {
            jLayeredPane1.requestFocus();
            if(jTabbedPane1.getSelectedIndex()==2) {
                jLabel9.setVisible(true);
                jComboBox2.setVisible(true);
                jComboBox2.setEnabled(true);
                jButton6.setContentAreaFilled(true);
                jButton6.setBorderPainted(true);
                jButton6.setEnabled(true);
                jButton6.setText("");
                jButton6.setIcon(new ImageIcon(getClass().getResource("resources/new.png")));
            } else {
                if(jTabbedPane1.getSelectedIndex()==1) {
                    jLabel9.setVisible(false);
                    jComboBox2.setEnabled(false);
                    jComboBox2.setVisible(false);
                } else {
                    jLabel9.setVisible(true);
                    jComboBox2.setVisible(true);
                    jComboBox2.setEnabled(true);
                }
                jButton6.setContentAreaFilled(false);
                jButton6.setBorderPainted(false);
                jButton6.setEnabled(false);
                jButton6.setIcon(null);
                jButton6.setText(" ");
            }
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    //add screenshot
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        File f = loadFileChooser(new SSChooseFilter(), false);
        jLayeredPane1.requestFocus();
        if(f!=null) {
            theme.screenshots.add(f);
            updateSSList();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    //remove screenshot
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jLayeredPane1.requestFocus();
        theme.screenshots.remove(jList2.getSelectedIndex());
        updateSSList();
    }//GEN-LAST:event_jButton1ActionPerformed

    //select wallpaper
    private void jTextField5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField5MousePressed
        if(pageLoaded && theme.controller.listVersions().size()==0) {
            loadCompatManager();
            loadTheme();
            if(theme.controller.listVersions().size()==0) {
                return;
            }
        }
        File f = loadFileChooser(new WallpaperChooseFilter(), false);
        jLayeredPane1.requestFocus();
        if(f!=null) {
            currVersion.wallpaper = f;
            jTextField5.setText(f.getPath());
            jTextField5.setCaretPosition(jTextField5.getText().length());
        }
    }//GEN-LAST:event_jTextField5MousePressed

    //create new patch
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if(pageLoaded && theme.controller.listVersions().size()==0) {
            loadCompatManager();
            loadTheme();
            if(theme.controller.listVersions().size()==0) {
                return;
            }
        }
        JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
        createPatchBox = new CreatePatch(mainFrame, currVersion.version, theme.controller.list(false), currCategory);
        createPatchBox.setLocationRelativeTo(mainFrame);
        createPatchBox.prevDir = prevDir;
        WebOSThemeBuilderApp.getApplication().show(createPatchBox);
        if(createPatchBox.patch!=null) {
            if(currVersion.version.equals(createPatchBox.version)) {
                DefaultTableModel dtm = (DefaultTableModel) jTable3.getModel();
                PatchEntry entry = new PatchEntry(createPatchBox.patch, createPatchBox.category);
                currVersion.patches.add(entry);
                if(!currCategory.equals("--all--")) {
                    currPatches.add(entry);
                }
                dtm.addRow(new Object[] {createPatchBox.patch.getPath()});
            } else {
                theme.controller.getVersion(createPatchBox.version).patches
                        .add(new PatchEntry(createPatchBox.patch, createPatchBox.category));
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    //add file, icon, or patch
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if(pageLoaded && theme.controller.listVersions().size()==0) {
            loadCompatManager();
            loadTheme();
            if(theme.controller.listVersions().size()==0) {
                return;
            }
        }
        JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
        DefaultTableModel dtm;
        if(jTabbedPane1.getSelectedIndex()==0) { //file add
            fileBox = new AddFile(mainFrame, null, currVersion.version, theme.controller.list(false), currCategory);
            fileBox.setLocationRelativeTo(mainFrame);
            fileBox.prevDir = prevDir;
            WebOSThemeBuilderApp.getApplication().show(fileBox);
            if(fileBox.item!=null) {
                if(currVersion.version.equals(fileBox.version)) {
                    dtm = (DefaultTableModel) jTable1.getModel();
                    currVersion.files.add(fileBox.item);
                    if(!currCategory.equals("--all--")) {
                        if(currCategory.equals(bulkBox.category)) {
                            currFiles.add(fileBox.item);
                            dtm.addRow(new Object[] {fileBox.item.file.getPath(), fileBox.item.dest});
                        }
                    } else {
                        dtm.addRow(new Object[] {fileBox.item.file.getPath(), fileBox.item.dest});
                    }
                } else {
                    theme.controller.getVersion(fileBox.version).files.add(fileBox.item);
                }
            }
        } else if(jTabbedPane1.getSelectedIndex()==1) { // icon add
            iconBox = new AddIcon(mainFrame, null, currVersion.version, theme.controller.list(false));
            iconBox.setLocationRelativeTo(mainFrame);
            iconBox.prevDir = prevDir;
            WebOSThemeBuilderApp.getApplication().show(iconBox);
            if(iconBox.item!=null) {
                if(currVersion.version.equals(iconBox.version)) {
                    dtm = (DefaultTableModel) jTable2.getModel();
                    currVersion.icons.add(iconBox.item);
                    dtm.addRow(new Object[] {iconBox.item.image.getPath(), iconBox.item.appID});
                } else {
                    theme.controller.getVersion(iconBox.version).icons.add(iconBox.item);
                }
            }
        } else if(jTabbedPane1.getSelectedIndex()==2) { //patch add
            patchBox = new AddPatch(mainFrame, null, currVersion.version, theme.controller.list(false), currCategory);
            patchBox.setLocationRelativeTo(mainFrame);
            patchBox.prevDir = prevDir;
            WebOSThemeBuilderApp.getApplication().show(patchBox);
            if(patchBox.item!=null) {
                if(currVersion.version.equals(patchBox.version)) {
                    dtm = (DefaultTableModel) jTable3.getModel();
                    currVersion.patches.add(patchBox.item);
                    if(!currCategory.equals("--all--")) {
                        if(currCategory.equals(bulkBox.category)) {
                            currPatches.add(patchBox.item);
                            dtm.addRow(new Object[] {patchBox.item.file.getPath()});
                        }
                    } else {
                        dtm.addRow(new Object[] {patchBox.item.file.getPath()});
                    }
                } else {
                    theme.controller.getVersion(patchBox.version).patches.add(patchBox.item);
                }
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    //remove file, icon, or patch
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if(pageLoaded && theme.controller.listVersions().size()==0) {
            loadCompatManager();
            loadTheme();
            if(theme.controller.listVersions().size()==0) {
                return;
            }
        }
        DefaultTableModel dtm;
        int i;
        if(jTabbedPane1.getSelectedIndex()==0) { //file remove
            dtm = (DefaultTableModel) jTable1.getModel();
            i = jTable1.getSelectedRow();
            if(i>-1) {
                dtm.removeRow(i);
                if(!currCategory.equals("--all--")) {
                    currVersion.files.remove(currFiles.get(i));
                }
                currFiles.remove(i);
            }
        } else if(jTabbedPane1.getSelectedIndex()==1) { // icon remove
            dtm = (DefaultTableModel) jTable2.getModel();
            i = jTable2.getSelectedRow();
            if(i>-1) {
                dtm.removeRow(i);
                currIcons.remove(i);
            }
        } else if(jTabbedPane1.getSelectedIndex()==2) { //patch remove
            dtm = (DefaultTableModel) jTable3.getModel();
            i = jTable3.getSelectedRow();
            if(i>-1) {
                dtm.removeRow(i);
                if(!currCategory.equals("--all--")) {
                    currVersion.patches.remove(currPatches.get(i));
                }
                currPatches.remove(i);
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    //build zip theme
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        //zip file theme build
        if(infoGood()) {
            theme.id = jTextField7.getText();
            File zip = loadFileChooser(new ZipChooseFilter(), true);
            if(zip!=null) {
                theme.name = jTextField1.getText();
                theme.version = jTextField2.getText();
                theme.creator = jTextField3.getText();
                theme.website = jTextField4.getText();
                theme.donateURL = jTextField6.getText();
                theme.description = jTextArea1.getText();
                if(!zip.getName().endsWith(".zip")) {
                    try {
                        zip = new File(zip.getCanonicalPath() + ".zip");
                    } catch(Exception e) {}
                }
                changeState(false);
                theme.buildZip(zip);
                if(zip.exists()) {
                    JOptionPane.showMessageDialog(mainPanel, "Theme zip built!");
                }
                changeState(true);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    //open zip theme
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        File open = loadFileChooser(new ZipChooseFilter(), false);
        if(open!=null) {
            this.changeState(false);
            t.schedule(new DoZipOpen(open), 50);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    //bulk add
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if(pageLoaded && theme.controller.listVersions().size()==0) {
            loadCompatManager();
            loadTheme();
            if(theme.controller.listVersions().size()==0) {
                return;
            }
        }

        JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
        bulkBox = new BulkAdd(mainFrame, currVersion.version, theme.controller.list(false), currCategory);
        bulkBox.setLocationRelativeTo(mainFrame);
        bulkBox.prevDir = prevDir;
        WebOSThemeBuilderApp.getApplication().show(bulkBox);
        if(bulkBox.files!=null) {
            if(currVersion.version.equals(bulkBox.version)) {
                DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
                for(int i=0; i<bulkBox.files.size(); i++) {
                    FileEntry fe = new FileEntry(bulkBox.dest + getFilename(bulkBox.files.get(i)),
                            bulkBox.files.get(i), bulkBox.category);
                    currVersion.files.add(fe);
                    if(!currCategory.equals("--all--")) {
                        if(currCategory.equals(bulkBox.category)) {
                            currFiles.add(fe);
                            dtm.addRow(new Object[] {fe.file.getPath(), fe.dest});
                        }
                    } else {
                        dtm.addRow(new Object[] {fe.file.getPath(), fe.dest});
                    }
                }
            } else {
                VersionTheme otherVer = theme.controller.getVersion(bulkBox.version);
                for(int i=0; i<bulkBox.files.size(); i++) {
                    FileEntry fe = new FileEntry(bulkBox.dest + getFilename(bulkBox.files.get(i)),
                            bulkBox.files.get(i), bulkBox.category);
                    otherVer.files.add(fe);
                }
            }
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    //double-click to edit file entry
    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int index = jTable1.getSelectedRow();
        if(evt.getClickCount()>=2  && index>-1) {
            JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
            fileBox = new AddFile(mainFrame, currFiles.get(index), currVersion.version,
                    theme.controller.list(false), currCategory);
            fileBox.setLocationRelativeTo(mainFrame);
            fileBox.prevDir = prevDir;
            WebOSThemeBuilderApp.getApplication().show(fileBox);
            if(fileBox.item!=null) {
                DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
                if(currVersion.version.equals(fileBox.version))  {
                    if(!currCategory.equals("--all--")) {
                        if(currCategory.equals(fileBox.item.category)) {
                            dtm.setValueAt(fileBox.item.file.getPath(), index, 0);
                            dtm.setValueAt(fileBox.item.dest, index, 1);
                        } else {
                            currFiles.remove(fileBox.item);
                            dtm.removeRow(index);
                        }
                    } else {
                        dtm.setValueAt(fileBox.item.file.getPath(), index, 0);
                        dtm.setValueAt(fileBox.item.dest, index, 1);
                    }
                } else {
                    dtm.removeRow(index);
                    currVersion.files.remove(fileBox.item);
                    if(!currCategory.equals("--all--")) {
                        currFiles.remove(fileBox.item);
                    }
                    theme.controller.getVersion(fileBox.version).files.add(fileBox.item);
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    //double-click to edit icon entry
    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int index = jTable2.getSelectedRow();
        if(evt.getClickCount()>=2  && index>-1) {
            JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
            iconBox = new AddIcon(mainFrame, currVersion.icons.get(index), currVersion.version,
                    theme.controller.list(false));
            iconBox.setLocationRelativeTo(mainFrame);
            iconBox.prevDir = prevDir;
            WebOSThemeBuilderApp.getApplication().show(iconBox);
            if(iconBox.item!=null) {
                DefaultTableModel dtm = (DefaultTableModel) jTable2.getModel();
                if(currVersion.version.equals(iconBox.version))  {
                    dtm.setValueAt(iconBox.item.image.getPath(), index, 0);
                    dtm.setValueAt(iconBox.item.appID, index, 1);
                } else {
                    dtm.removeRow(index);
                    currVersion.icons.remove(iconBox.item);
                    theme.controller.getVersion(iconBox.version).icons.add(iconBox.item);
                }
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

    //double-click to edit patch entry
    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int index = jTable3.getSelectedRow();
        if(evt.getClickCount()>=2  && index>-1) {
            JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
            patchBox = new AddPatch(mainFrame, currPatches.get(index), currVersion.version,
                    theme.controller.list(false), currCategory);
            patchBox.setLocationRelativeTo(mainFrame);
            patchBox.prevDir = prevDir;
            WebOSThemeBuilderApp.getApplication().show(patchBox);
            if(patchBox.item!=null) {
                DefaultTableModel dtm = (DefaultTableModel) jTable3.getModel();
                if(currVersion.version.equals(patchBox.version))  {
                    if(!currCategory.equals("--all--")) {
                        if(currCategory.equals(patchBox.item.category)) {
                            dtm.setValueAt(patchBox.item.file.getPath(), index, 0);
                        } else {
                            dtm.removeRow(index);
                            currPatches.remove(patchBox.item);
                        }
                    } else {
                        dtm.setValueAt(patchBox.item.file.getPath(), index, 0);
                    }
                    
                } else {
                    dtm.removeRow(index);
                    currVersion.patches.remove(patchBox.item);
                    if(!currCategory.equals("--all--")) {
                        currPatches.remove(patchBox.item);
                    }
                    theme.controller.getVersion(patchBox.version).patches.add(patchBox.item);
                }
            }
        }
    }//GEN-LAST:event_jTable3MouseClicked

    //reset
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        reset();
        loadTheme();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    //show compatible devices/versions manager if needed
    private void jComboBox1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBox1PopupMenuWillBecomeVisible
        new Thread() {
            @Override
            public void run() {
                if(pageLoaded && theme.controller.listVersions().size()==0) {
                    getFrame().requestFocus();
                    loadCompatManager();
                    loadTheme();
                }
            }
        }.start();
    }//GEN-LAST:event_jComboBox1PopupMenuWillBecomeVisible

    //compatible devices/versions manager
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        loadCompatManager();
        loadTheme();
    }//GEN-LAST:event_jButton4ActionPerformed

    //changed version
    private void jComboBox1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBox1PopupMenuWillBecomeInvisible
        new Thread() {
            @Override
            public void run() {
                if(pageLoaded && theme.controller.listVersions().size()!=0) {
                    currCategory = "--all--";
                    jComboBox2.setSelectedIndex(0);
                    currVersion = theme.controller.getVersion(((String) jComboBox1.getSelectedItem()).replace("webOS ", ""));
                    loadVersion(false);
                }
            }
        }.start();
    }//GEN-LAST:event_jComboBox1PopupMenuWillBecomeInvisible

    //changed category
    private void jComboBox2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBox2PopupMenuWillBecomeInvisible
        new Thread() {
            @Override
            public void run() {
                if(pageLoaded) {
                    currCategory = ((String) jComboBox2.getSelectedItem()).toLowerCase().replaceAll(" ", "_");
                    loadVersion(true);
                }
            }
        }.start();

    }//GEN-LAST:event_jComboBox2PopupMenuWillBecomeInvisible

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane5;
    private javax.swing.JLayeredPane jLayeredPane6;
    private javax.swing.JList jList2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    // End of variables declaration//GEN-END:variables

    private XmlImport xmlBox;
    private CompatibleDevicesVersions compatBox;
    private BulkAdd bulkBox;
    private JDialog aboutBox;
    private AddFile fileBox;
    private AddIcon iconBox;
    private CreatePatch createPatchBox;
    private AddPatch patchBox;

    private class DoDelayedLoad extends TimerTask  {
        public void run() {
            loadTheme();
            pageLoaded=true;
        }
    }

    private class DoZipOpen extends TimerTask  {
        private File zip;
        public DoZipOpen(File f) {
            zip = f;
        }
        public void run() {
            try {
                pageLoaded = false;
                reset();
                zipOutDir = theme.extractTheme(zip);
                if(!theme.isNewFormat(zipOutDir)) {
                    JFrame mainFrame = WebOSThemeBuilderApp.getApplication().getMainFrame();
                    xmlBox = new XmlImport(mainFrame);
                    xmlBox.setLocationRelativeTo(mainFrame);
                    WebOSThemeBuilderApp.getApplication().show(xmlBox);
                    if(xmlBox.version == null) {
                        return;
                    }
                    theme.loadFromXML(zipOutDir, xmlBox.devices, xmlBox.version);
                } else {
                    theme.loadFromJSON(zipOutDir);
                }
                loadThemeMetaData();
                changeState(true);
                loadTheme();
            } catch(Exception e) {
                changeState(true);
                e.printStackTrace();
            }
            System.out.println(theme);
            pageLoaded = true;
            
        }
    }

    private class ImagePanel extends JPanel {

        private Image img;

        public ImagePanel(String img) {
            this(new ImageIcon(img).getImage());
        }

        public ImagePanel(Image img) {
            this.img = img;
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setLayout(null);
        }

        @Override public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }

    private class TranslucentPanel extends JLayeredPane {
        BufferedImage image = null;
        @Override
        public void paint(Graphics g) {
            if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
                try{this.wait(500);}catch(Exception e){}
                image = (BufferedImage) createImage(getWidth(), getHeight());
            }
            Graphics2D g2 = image.createGraphics();
            g2.setClip(g.getClip());
            g2.dispose();
            g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcOver.derive(0.75f));
            g2.drawImage(image, 0, 0, null);
            super.paint(g2);
            jTextField1.repaint();
            jTextField2.repaint();
            jTextField3.repaint();
            jTextField4.repaint();
            jTextField6.repaint();
            jTextField7.repaint();
            jTextArea1.repaint();
            jList2.repaint();
            jButton2.repaint();
            jButton1.repaint();
            jTextField5.repaint();
            jTabbedPane1.repaint();
            jButton6.repaint();
            jButton7.repaint();
            jButton5.repaint();
            jComboBox1.repaint();
            jComboBox2.repaint();
            jButton4.repaint();
        }
    }

    private class WallpaperChooseFilter extends javax.swing.filechooser.FileFilter {
        private final String[] okFileExtensions = new String[] {".jpg", ".jpeg", ".png"};

        public boolean accept(File f) {
            for (String extension : okFileExtensions)
                if (f.getName().toLowerCase().endsWith(extension) || f.isDirectory())
                    return true;
            return false;
        }

        public String getDescription() {
            return "Wallpaper Files";
        }
    }

    private class SSChooseFilter extends javax.swing.filechooser.FileFilter {
        private final String[] okFileExtensions = new String[] {".jpg", ".jpeg", ".png"};

        public boolean accept(File f) {
            for (String extension : okFileExtensions)
                if (f.getName().toLowerCase().endsWith(extension) || f.isDirectory())
                    return true;
            return false;
        }

        public String getDescription() {
            return "Screenshot Files";
        }
    }

    private class ZipChooseFilter extends javax.swing.filechooser.FileFilter {
        private final String[] okFileExtensions = new String[] {".zip"};

        public boolean accept(File f) {
            for (String extension : okFileExtensions)
                if (f.getName().toLowerCase().endsWith(extension) || f.isDirectory())
                    return true;
            return false;
        }

        public String getDescription() {
            return "Zip Files";
        }
    }
}
