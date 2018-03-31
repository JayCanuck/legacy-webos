/*
 * ThemeBuilderView.java
 */

package ca.canucksoftware.clockthemebuilder;

import ca.canucksoftware.ipk.IpkgBuilder;
import ca.canucksoftware.utils.FileUtils;
import ca.canucksoftware.utils.JarResource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import net.iharder.dnd.FileDrop;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The application's main frame.
 */
public class ThemeBuilderView extends FrameView {
    private final String ANALOG = "analog";
    private final String CLASSIC = "manualanalog";
    private final String DIGITAL = "digital";
    private final String NEON = "neon";
    private final String CLOCK_ICON = "http://www.webos-internals.org/images/8/8d/" +
            "Icon_WebOSInternals_Patches_Clock.png";
    private File icon;
    private ArrayList<File> images;
    private File baseHTML;
    private File baseJS;
    private File baseCSS;

    public ThemeBuilderView(SingleFrameApplication app) {
        super(app);
        URL iconURL = getClass().getResource("resources/clock2.png");
        getFrame().setIconImage(new ImageIcon(iconURL).getImage());
        initComponents();
        icon = null;
        baseHTML = null;
        baseJS = null;
        baseCSS = null;
        images = new ArrayList<File>(0);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel11.setVisible(false);
        jTextField5.setVisible(false);
        jTextField9.setVisible(false);
        jTextField10.setVisible(false);
        jButton6.setVisible(false);
        jButton7.setVisible(false);
        jButton3.setVisible(false);
        jScrollPane2.setSize(140, 220);
        jList1.setSize(140, 220);
        jTextField1.setDocument(new DocumentFilter(DocumentFilter.ALPHA_NUMERIC +
                DocumentFilter.SYMBOLS + DocumentFilter.PERIOD + DocumentFilter.SPACE));
        jTextField2.setDocument(new DocumentFilter(DocumentFilter.ALPHA_NUMERIC +
                DocumentFilter.SYMBOLS));
        jTextField3.setDocument(new DocumentFilter(DocumentFilter.FLOAT));
        jTextField4.setDocument(new DocumentFilter(DocumentFilter.ALPHA_NUMERIC +
                DocumentFilter.SYMBOLS + DocumentFilter.PERIOD + DocumentFilter.SPACE));
        FileDrop drop1 = new FileDrop(jList1, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    for(int i=0; i<files.length; i++) {
                        if(files[i].getName().toLowerCase().endsWith(".png") ||
                                files[i].getName().toLowerCase().endsWith(".bmp") ||
                                files[i].getName().toLowerCase().endsWith(".jpg") ||
                                files[i].getName().toLowerCase().endsWith(".jpeg") ||
                                files[i].getName().toLowerCase().endsWith(".gif")) {
                            images.add(files[i]);
                        }
                    }
                    displayImageList();
                }
            }
        });
        FileDrop drop2 = new FileDrop(jTextField5, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    if(files[0].getName().toLowerCase().endsWith(".html")) {
                        baseHTML = files[0];
                        jTextField5.setText(baseHTML.getAbsolutePath());
                    }
                }
            }
        });
        FileDrop drop3 = new FileDrop(jTextField9, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    if(files[0].getName().toLowerCase().endsWith(".js")) {
                        baseJS = files[0];
                        jTextField9.setText(baseJS.getAbsolutePath());
                    }
                }
            }
        });
        FileDrop drop4 = new FileDrop(jTextField10, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    if(files[0].getName().toLowerCase().endsWith(".css")) {
                        baseCSS = files[0];
                        jTextField10.setText(baseCSS.getAbsolutePath());
                    }
                }
            }
        });
        FileDrop drop5 = new FileDrop(jTextField11, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    if(files[0].getName().toLowerCase().endsWith(".png")) {
                        icon = files[0];
                        jTextField11.setText(icon.getAbsolutePath());
                    }
                }
            }
        });
        if(ThemeBuilderApp.args!=null) {
            if(ThemeBuilderApp.args.length>0) {
                if(ThemeBuilderApp.args[0].equalsIgnoreCase("-c")) {
                    FileDrop drop6 = new FileDrop(jLayeredPane2, new FileDrop.Listener() {
                        public void  filesDropped(File[] files ) {
                            if(files!=null && files.length>0) {
                                if(FileUtils.getFilename(files[0]).equalsIgnoreCase("control")) {
                                    loadControlInfo(files[0]);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
    
    private void loadControlInfo(File control) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(control));
            String line = input.readLine();
            while(line!=null) {
                line = line.trim();
                if(line.length()>0)
                    if(line.startsWith("Package")) {
                    	jTextField2.setText(line.substring(line.indexOf(":")+1).trim()
                                .replaceFirst("ca.canucksoftware.clock.", ""));
                    } else if(line.startsWith("Description")) {
                    	jTextField1.setText(line.substring(line.indexOf(":")+1).trim());
                    } else if(line.startsWith("Version")) {
                    	jTextField3.setText(line.substring(line.indexOf(":")+1).trim());
                    } else if(line.startsWith("Maintainer")) {
                    	jTextField4.setText(line.substring(line.indexOf(":")+1).trim());
                    } else if(line.startsWith("Source")) {
                        String source = line.substring(line.indexOf(":")+1).trim();
                    	if(source.startsWith("{") && source.endsWith("}")) {
                            JSONObject json = new JSONObject(source);
                            if(json.has("FullDescription")) {
                                jTextArea1.setText(json.getString("FullDescription").replaceAll("<br>", "\n"));
                            }
                            if(json.has("Screenshots")) {
                                JSONArray ss = json.getJSONArray("Screenshots");
                                if(ss.length()>0) {
                                    jTextField6.setText(ss.getString(0));
                                }
                            }
                            if(json.has("Homepage")) {
                                jTextField7.setText(json.getString("Homepage"));
                            }
                            if(json.has("License")) {
                                jTextField8.setText(json.getString("License"));
                            }
                        }
                    }
                line = input.readLine();
            }
            input.close();
        } catch(Exception e) {}
    }

    private File loadFileChooser(javax.swing.filechooser.FileFilter ff, String saveName) {
        File result;
        JFileChooser fc = new JFileChooser(); //Create a file chooser
        fc.setMultiSelectionEnabled(false);
        if(ff!=null) {
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(ff);
        } else {
            fc.setAcceptAllFileFilterUsed(true);
        }
        if(saveName==null) {
            fc.setDialogTitle("");
            if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
            } else {
                result = null;
            }
        } else {
            fc.setDialogTitle("Save As...");
            fc.setSelectedFile(new File(saveName));
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
            } else {
                result = null;
            }
        }
        return result;
    }

    private void displayImageList() {
        Object[] names = new Object[images.size()];
        for(int i=0; i<names.length; i++) {
            names[i] = FileUtils.getFilename(images.get(i));
        }
        jList1.setListData(names);
    }

    private String getBase() {
        String result = "-";
        int index = jComboBox1.getSelectedIndex();
        if(index==0) {
            result = ANALOG;
        } else if(index==1) {
            result = CLASSIC;
        } else if(index==2) {
            result = DIGITAL;
        }
        return result;
    }

    private boolean okToGo() {
        boolean result = false;
        if(jTextField1.getText().trim().length()>0 &&
                jTextField2.getText().trim().length()>0 &&
                jTextField3.getText().trim().length()>0 &&
                jTextField4.getText().trim().length()>0) {
            String code = jTextField2.getText().trim();
            if(!code.equalsIgnoreCase(ANALOG) && !code.equalsIgnoreCase(CLASSIC) &&
                    !code.equalsIgnoreCase(DIGITAL) && !code.equalsIgnoreCase(NEON)) {
                int index = jComboBox1.getSelectedIndex();
                if((index!=3) || (index==3 && baseHTML!=null && baseJS!=null &&
                        baseCSS!=null)) {
                    if(icon!=null) {
                        result = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: A clock theme icon is required");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Custom base requires " +
                            "an HTML file, JS file, and CSS file");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error: Invalid codename");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error: Theme info incomplete");
        }
        return result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField8 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton7 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        jLayeredPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Clock Theme Info", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jLayeredPane2.setName("jLayeredPane2"); // NOI18N

        jLabel3.setFont(jLabel3.getFont());
        jLabel3.setText("Version:");
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setBounds(10, 80, 80, 20);
        jLayeredPane2.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setText("Codename:");
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setBounds(10, 50, 80, 20);
        jLayeredPane2.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel4.setFont(jLabel4.getFont());
        jLabel4.setText("Developer:");
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setBounds(10, 110, 80, 20);
        jLayeredPane2.add(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField3.setName("jTextField3"); // NOI18N
        jTextField3.setBounds(90, 80, 160, 20);
        jLayeredPane2.add(jTextField3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField4.setName("jTextField4"); // NOI18N
        jTextField4.setBounds(90, 110, 160, 20);
        jLayeredPane2.add(jTextField4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setText("Name:");
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(10, 20, 50, 20);
        jLayeredPane2.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.setBounds(90, 20, 160, 20);
        jLayeredPane2.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.setBounds(90, 50, 160, 20);
        jLayeredPane2.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setBounds(10, 10, 262, 142);
        jLayeredPane1.add(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Optional Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jLayeredPane3.setName("jLayeredPane3"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 11));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jScrollPane1.setBounds(90, 20, 160, 80);
        jLayeredPane3.add(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField8.setName("jTextField8"); // NOI18N
        jTextField8.setBounds(90, 170, 160, 20);
        jLayeredPane3.add(jTextField8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel8.setFont(jLabel8.getFont());
        jLabel8.setText("License:");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setBounds(10, 170, 80, 20);
        jLayeredPane3.add(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel6.setFont(jLabel6.getFont());
        jLabel6.setText("<html>Screenshot<br>URL:");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setBounds(10, 100, 80, 40);
        jLayeredPane3.add(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel7.setFont(jLabel7.getFont());
        jLabel7.setText("Homepage:");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setBounds(10, 140, 80, 20);
        jLayeredPane3.add(jLabel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setText("Description:");
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setBounds(10, 20, 80, 20);
        jLayeredPane3.add(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField6.setName("jTextField6"); // NOI18N
        jTextField6.setBounds(90, 110, 160, 20);
        jLayeredPane3.add(jTextField6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField7.setName("jTextField7"); // NOI18N
        jTextField7.setBounds(90, 140, 160, 20);
        jLayeredPane3.add(jTextField7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBounds(10, 160, 262, 202);
        jLayeredPane1.add(jLayeredPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setText("Create IPK File");
        jButton2.setFocusable(false);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.setBounds(290, 330, 130, 30);
        jLayeredPane1.add(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton1.setText("Reset");
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.setBounds(430, 330, 100, 30);
        jLayeredPane1.add(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Clock Theme Contents", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jLayeredPane4.setName("jLayeredPane4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setName("jList1"); // NOI18N
        jScrollPane2.setViewportView(jList1);

        jScrollPane2.setBounds(80, 80, 140, 140);
        jLayeredPane4.add(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton7.setText("...");
        jButton7.setFocusable(false);
        jButton7.setIconTextGap(0);
        jButton7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jButton7.setBounds(225, 254, 25, 22);
        jLayeredPane4.add(jButton7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel11.setFont(jLabel11.getFont());
        jLabel11.setText("CSS File:");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setBounds(10, 280, 70, 20);
        jLayeredPane4.add(jLabel11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton4.setText("+");
        jButton4.setFocusable(false);
        jButton4.setIconTextGap(0);
        jButton4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton4.setBounds(225, 80, 25, 23);
        jLayeredPane4.add(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel9.setFont(jLabel9.getFont());
        jLabel9.setText("HTML File:");
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setBounds(10, 230, 70, 20);
        jLayeredPane4.add(jLabel9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton3.setText("...");
        jButton3.setFocusable(false);
        jButton3.setIconTextGap(0);
        jButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.setBounds(225, 279, 25, 22);
        jLayeredPane4.add(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Analog", "Classic", "Digital", "None (custom base)" }));
        jComboBox1.setFocusable(false);
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jComboBox1.setBounds(80, 20, 170, 20);
        jLayeredPane4.add(jComboBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton5.setText("‒");
        jButton5.setFocusable(false);
        jButton5.setIconTextGap(0);
        jButton5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jButton5.setBounds(225, 110, 25, 23);
        jLayeredPane4.add(jButton5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField9.setEditable(false);
        jTextField9.setName("jTextField9"); // NOI18N
        jTextField9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField9MousePressed(evt);
            }
        });
        jTextField9.setBounds(80, 255, 140, 20);
        jLayeredPane4.add(jTextField9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField10.setEditable(false);
        jTextField10.setName("jTextField10"); // NOI18N
        jTextField10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField10MousePressed(evt);
            }
        });
        jTextField10.setBounds(80, 280, 140, 20);
        jLayeredPane4.add(jTextField10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel13.setFont(jLabel13.getFont());
        jLabel13.setText("Icon:");
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setBounds(10, 50, 70, 20);
        jLayeredPane4.add(jLabel13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton6.setText("...");
        jButton6.setFocusable(false);
        jButton6.setIconTextGap(0);
        jButton6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jButton6.setBounds(225, 229, 25, 22);
        jLayeredPane4.add(jButton6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel12.setFont(jLabel12.getFont());
        jLabel12.setText("Base:");
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setBounds(10, 20, 70, 20);
        jLayeredPane4.add(jLabel12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel10.setFont(jLabel10.getFont());
        jLabel10.setText("JS File:");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setBounds(10, 255, 70, 20);
        jLayeredPane4.add(jLabel10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField5.setEditable(false);
        jTextField5.setName("jTextField5"); // NOI18N
        jTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField5MousePressed(evt);
            }
        });
        jTextField5.setBounds(80, 230, 140, 20);
        jLayeredPane4.add(jTextField5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel14.setFont(jLabel14.getFont());
        jLabel14.setText("Images:");
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setBounds(10, 80, 70, 20);
        jLayeredPane4.add(jLabel14, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField11.setEditable(false);
        jTextField11.setName("jTextField11"); // NOI18N
        jTextField11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField11MousePressed(evt);
            }
        });
        jTextField11.setBounds(80, 50, 140, 20);
        jLayeredPane4.add(jTextField11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton8.setText("...");
        jButton8.setFocusable(false);
        jButton8.setIconTextGap(0);
        jButton8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jButton8.setBounds(225, 49, 25, 22);
        jLayeredPane4.add(jButton8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane4.setBounds(280, 10, 262, 312);
        jLayeredPane1.add(jLayeredPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
        );

        jProgressBar1.setName("jProgressBar1"); // NOI18N

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if(jComboBox1.getSelectedIndex()==3) {
            jList1.setSize(140, 140);
            jScrollPane2.setSize(140, 140);
            jLabel9.setVisible(true);
            jLabel10.setVisible(true);
            jLabel11.setVisible(true);
            jTextField5.setVisible(true);
            jTextField9.setVisible(true);
            jTextField10.setVisible(true);
            jButton6.setVisible(true);
            jButton7.setVisible(true);
            jButton3.setVisible(true);
        } else {
            if(jComboBox1.getSelectedIndex()==1) {
                JOptionPane.showMessageDialog(mainPanel, "NOTICE: Themes created with the " +
                        "Classic base will only work on webOS 2.0 and higher");
            }
            baseHTML = null;
            baseJS = null;
            baseCSS = null;
            jTextField5.setText("");
            jTextField9.setText("");
            jTextField10.setText("");
            jLabel9.setVisible(false);
            jLabel10.setVisible(false);
            jLabel11.setVisible(false);
            jTextField5.setVisible(false);
            jTextField9.setVisible(false);
            jTextField10.setVisible(false);
            jButton6.setVisible(false);
            jButton7.setVisible(false);
            jButton3.setVisible(false);
            jScrollPane2.setSize(140, 220);
            jList1.setSize(140, 220);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        File f = loadFileChooser(new CustomFilter(
                new String[] {".png", ".bmp", ".jpg", ".jpeg", ".gif"}, "Image Files"), null);
        if(f!=null) {
            images.add(f);
            displayImageList();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int index = jList1.getSelectedIndex();
        if(index>-1) {
            images.remove(index);
            displayImageList();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        baseHTML = loadFileChooser(new CustomFilter(".html", "HTML Files"), null);
        if(baseHTML!=null) {
            jTextField5.setText(baseHTML.getAbsolutePath());
        } else {
            jTextField5.setText("");
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField5MousePressed
        jButton6ActionPerformed(null);
    }//GEN-LAST:event_jTextField5MousePressed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        baseJS = loadFileChooser(new CustomFilter(".js", "JS Files"), null);
        if(baseJS!=null) {
            jTextField9.setText(baseJS.getAbsolutePath());
        } else {
            jTextField9.setText("");
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jTextField9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField9MousePressed
        jButton7ActionPerformed(null);
    }//GEN-LAST:event_jTextField9MousePressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        baseCSS = loadFileChooser(new CustomFilter(".css", "CSS Files"), null);
        if(baseCSS!=null) {
            jTextField10.setText(baseCSS.getAbsolutePath());
        } else {
            jTextField10.setText("");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField10MousePressed
        jButton3ActionPerformed(null);
    }//GEN-LAST:event_jTextField10MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        images.clear();
        displayImageList();
        baseHTML = null;
        baseJS = null;
        baseCSS = null;
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextArea1.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        jTextField5.setText("");
        jTextField9.setText("");
        jTextField10.setText("");
        jTextField11.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(okToGo()) {
            String id = jTextField2.getText().trim().toLowerCase();
            String version = jTextField3.getText().trim();
            String filename = id + "_" + version + ".ipk";
            File out = loadFileChooser(new CustomFilter(".ipk", "Ipk Files"), filename);
            if(out!=null) {
                try {
                    if(!out.getName().endsWith(".ipk")) {
                        out = new File(out.getCanonicalPath() + ".ipk");
                    }
                    String name = jTextField1.getText().trim();
                    String developer = jTextField4.getText().trim();
                    String description = jTextArea1.getText().trim().replaceAll("\n", "<br>");
                    String screenshot = jTextField6.getText().trim();
                    String homepage = jTextField7.getText().trim();
                    String license = jTextField8.getText().trim();
                    String time = String.valueOf((System.currentTimeMillis()/1000));
                    String base = getBase();
                    String tmpFilePath = System.getProperty("java.io.tmpdir");
                    File baseDir = new File(tmpFilePath, "webos.clock.theme-" + System.currentTimeMillis());
                    if(baseDir.exists()) {
                        FileUtils.delete(baseDir);
                    }
                    baseDir.mkdirs();
                    FileUtils.copy(icon, new File(baseDir, "icon.png"));
                    JarResource script = new JarResource("resources/theme.js", ThemeBuilderApp.class);
                    script.extract(new File(baseDir, "theme.js"));
                    script = new JarResource("resources/postinst", ThemeBuilderApp.class);
                    script.prepend("ID=" + id + "\nNAME=\"" + name + "\"\nBASE=" + base + "\n");
                    File postinst = script.extract();
                    script = new JarResource("resources/prerm", ThemeBuilderApp.class);
                    script.prepend("ID=" + id + "\nNAME=\"" + name + "\"\n");
                    File prerm = script.extract();
                    File filesDir = new File(baseDir, "files");
                    filesDir.mkdirs();
                    if(baseHTML!=null) {
                        FileUtils.copy(baseHTML, new File(filesDir, id + "-clock.html"));
                        FileUtils.copy(baseJS, new File(filesDir, id +"-clock-functions.js"));
                        FileUtils.copy(baseCSS, new File(filesDir, id +"-theme.css"));
                    }
                    File imagesDir = new File(filesDir, "images");
                    imagesDir.mkdirs();
                    for(int i=0; i<images.size(); i++) {
                        File curr = images.get(i);
                        FileUtils.copy(curr, new File(imagesDir, FileUtils.getFilename(curr)));
                    }
                    JSONObject source = new JSONObject();
                    if(jComboBox1.getSelectedIndex()==1) {
                        source.put("MinWebOSVersion", "2.0.0");
                    }
                    source.put("Title", name);
                    source.put("LastUpdated", time);
                    source.put("Type", "Theme");
                    source.put("Feed", "Clock Themes");
                    source.put("Category", "Clock");
                    if(homepage.length()>0) {
                        source.put("Homepage", homepage);
                    }
                    source.put("Icon", CLOCK_ICON);
                    source.put("FullDescription", description);
                    ArrayList<String> ssList = new ArrayList<String>();
                    ssList.add(screenshot);
                    JSONArray jsonSSList = new JSONArray(ssList);
                    source.put("Screenshots", jsonSSList);
                    if(license.length()>0) {
                        source.put("License", license);
                    }
                    String destDir = "/usr/palm/applications/ca.canucksoftware.clock." + id + "/";
                    IpkgBuilder ib = new IpkgBuilder(baseDir, destDir, out);
                    ib.setPackageName(name);
                    ib.setPackageID("ca.canucksoftware.clock." + id);
                    ib.setPackageVersion(version);
                    ib.setPackageAuthor(developer);
                    ib.setArch("all");
                    ib.setPackageSource(source);
                    ib.setPostinst(postinst);
                    ib.setPrerm(prerm);
                    ib.build();
                    JOptionPane.showMessageDialog(null, "Clock theme created successfully!");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField11MousePressed
        jButton8ActionPerformed(null);
    }//GEN-LAST:event_jTextField11MousePressed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        icon = loadFileChooser(new CustomFilter(".png", "PNG Files"), null);
        if(icon!=null) {
            jTextField11.setText(icon.getAbsolutePath());
        } else {
            jTextField11.setText("");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JList jList1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

    private class CustomFilter extends FileFilter {
        private String[] exts;
        private String label;
        public CustomFilter(String ext, String label) {
            this(new String[] {ext}, label);
        }
        public CustomFilter(String[] exts, String label) {
            super();
            this.exts = exts;
            this.label = label;
        }
        public boolean accept(File f) {
            boolean result = false;
            for(int i=0; i<exts.length; i++) {
                if(f.getName().toLowerCase().endsWith(exts[i]) || f.isDirectory()) {
                    result = true;
                    break;
                }
            }
            return result;
        }
        public String getDescription() {
            return label;
        }
    }

    private class DocumentFilter extends PlainDocument {
        public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String ALPHA = LOWERCASE + UPPERCASE;
        public static final String NUMERIC = "0123456789";
        public static final String PERIOD = ".";
        public static final String FLOAT = NUMERIC + PERIOD;
        public static final String SYMBOLS = "_-";
        public static final String SPACE = " ";
        public static final String ALPHA_NUMERIC = ALPHA + NUMERIC;
        protected String acceptedChars = null;
        protected boolean negativeAccepted = false;
        public DocumentFilter() {
            this(ALPHA_NUMERIC);
        }
        public DocumentFilter(String acceptedchars) {
            acceptedChars = acceptedchars;
        }
        public void setNegativeAccepted(boolean negativeaccepted) {
            if (acceptedChars.equals(NUMERIC) ||
                    acceptedChars.equals(FLOAT) ||
                    acceptedChars.equals(ALPHA_NUMERIC)){
                negativeAccepted = negativeaccepted;
                acceptedChars += "-";
            }
        }
        @Override
        public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
            if(str==null) return;
            if(acceptedChars.equals(UPPERCASE)) {
                str = str.toUpperCase();
            } else if (acceptedChars.equals(LOWERCASE)) {
                str = str.toLowerCase();
            }
            for(int i=0; i < str.length(); i++) {
                if(acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
                    return;
            }
            if(negativeAccepted && str.indexOf("-") != -1) {
                if(str.indexOf("-") != 0 || offset != 0 ) {
                    return;
                }
            }
            super.insertString(offset, str, attr);
        }
    }
}
