/*
 * IpkPackagerView.java
 */

package ca.canucksoftware.ipkpackager;

import ca.canucksoftware.ipk.IpkgBuilder;
import java.awt.Component;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The application's main frame.
 */
public class IpkPackagerView extends FrameView {
    private boolean isExpanded;
    private File folder;
    private File postinst;
    private File prerm;
    private File pmPostInstall;
    private File pmPreRemove;
    private Timer t;
    private ArrayList<String> depends;
    private ArrayList<String> ssURLs;

    public IpkPackagerView(SingleFrameApplication app) {
        super(app);
        initComponents();
        folder=null;
        postinst = null;
        prerm = null;
        pmPostInstall = null;
        pmPreRemove = null;
        isExpanded = false;
        depends = new ArrayList<String>();
        ssURLs = new ArrayList<String>();
        jTextField5.setDocument(new DocumentFilter(DocumentFilter.ALPHA_NUMERIC));
        jTextField6.setDocument(new DocumentFilter(DocumentFilter.FLOAT));
        jTextField18.setDocument(new DocumentFilter(DocumentFilter.FLOAT));
        jTextField19.setDocument(new DocumentFilter(DocumentFilter.FLOAT));
        t = new Timer();
        t.schedule(new ResizeScreen(), 50);
        t.schedule(new DelayedLoad(), 50);
    }

    private File loadFileChooser(boolean dir, FileFilter filter, String text) {
        File result = null;
        JFileChooser fc = new JFileChooser(); //Create a file chooser
        disableNewFolderButton(fc);
        if(text!=null) {
            fc.setSelectedFile(new File(text));
        }
        if(dir) {
            fc.setDialogTitle("");
            File lastDir = new File(Preferences.userRoot().get("lastDir",
                        fc.getCurrentDirectory().getAbsolutePath()));
            fc.setCurrentDirectory(lastDir);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
                jTextField1.setText(result.getAbsolutePath());
                Preferences.userRoot().put("lastDir",
                            result.getParentFile().getAbsolutePath());
            }
        } else {
            File lastSaved = null;
            File lastSelected = null;
            if(filter!=null) {
                fc.setDialogTitle("Save As...");
                lastSaved = new File(Preferences.userRoot().get("lastSaved",
                        fc.getCurrentDirectory().getAbsolutePath()));
                fc.setCurrentDirectory(lastSaved);
                fc.setFileFilter(filter);
            } else {
                fc.setDialogTitle("");
                lastSelected = new File(Preferences.userRoot().get("lastSelected",
                        fc.getCurrentDirectory().getAbsolutePath()));
                fc.setCurrentDirectory(lastSelected);
                fc.setAcceptAllFileFilterUsed(true);
            }
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
                if(lastSaved!=null) {
                    Preferences.userRoot().put("lastSaved",
                            result.getParentFile().getAbsolutePath());
                }
                if(lastSelected!=null) {
                    Preferences.userRoot().put("lastSelected",
                            result.getParentFile().getAbsolutePath());
                }
            }
        }
        return result;
    }

    private void disableNewFolderButton(Container c) {
        int len = c.getComponentCount();
        for(int i=0; i<len; i++) {
            Component comp = c.getComponent(i);
            if(comp instanceof JButton) {
                JButton b = (JButton)comp;
                Icon icon = b.getIcon();
                if(icon != null && (icon == UIManager.getIcon("FileChooser.newFolderIcon")
                        || icon == UIManager.getIcon("FileChooser.upFolderIcon")))
                    b.setEnabled(false);
            } else if (comp instanceof Container) {
                disableNewFolderButton((Container)comp);
            }
        }
    }

    private boolean okToGo() {
        if(jTextField2.getText().length()==0) {
            jTextField2.setText("/");
        }
        return ((folder!=null) &&
                (jTextField3.getText().length()!=0) &&
                (jTextField5.getText().length()!=0) &&
                (jTextField7.getText().length()!=0) &&
                (jTextField6.getText().length()!=0));
    }

    private void setAuthor(IpkgBuilder ib) {
        String text = jTextField7.getText().trim();
        String[] tokens = text.split("\\s+");
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        for(int i=0; i<tokens.length; i++) {
            String email = tokens[i].replaceAll("<", "");
            email = email.replaceAll(">", "");
            Matcher m = p.matcher(email);
            if(m.matches()) {
                tokens[i] = "<" + email + ">";
            }
        }
        text = "";
        for(int i=0; i<tokens.length; i++) {
            text += tokens[i] + " ";
        }
        ib.setPackageAuthor(text.trim());
    }

    private String getFilepath() {
        String result = null;
        result = jTextField2.getText().replace("\\", "/");
        if(!result.startsWith("/"))
            result = "/" + result;
        if(!result.endsWith("/"))
            result = result + "/";
        return result;
    }

    private String getArch() {
        String result = "all";
        if(jComboBox5.getSelectedIndex()==1) {
            result = "armv6";
        } else if(jComboBox5.getSelectedIndex()==2) {
            result = "armv7";
        } else if(jComboBox5.getSelectedIndex()==3) {
            result = "i686";
        }
        return result;
    }

    private void setArch(String arch) {
        if(arch.equalsIgnoreCase("armv6")) {
            jComboBox5.setSelectedIndex(1);
        } else if(arch.equalsIgnoreCase("armv7")) {
            jComboBox5.setSelectedIndex(2);
        } else if(arch.equalsIgnoreCase("i686")) {
            jComboBox5.setSelectedIndex(3);
        } else {
            jComboBox5.setSelectedIndex(0);
        }
    }

    private String getType() {
        String result = "Application";
        if(jComboBox1.getSelectedIndex()==1) {
            result = "Linux Application";
        } else if(jComboBox1.getSelectedIndex()==2) {
            result = "Linux Daemon";
        } else if(jComboBox1.getSelectedIndex()==3) {
            result = "Patch";
        } else if(jComboBox1.getSelectedIndex()==4) {
            result = "Plugin";
        } else if(jComboBox1.getSelectedIndex()==5) {
            result = "Service";
        } else if(jComboBox1.getSelectedIndex()==6) {
            result = "Theme";
        }
        return result;
    }

    private void setType(String type) {
        if(type.equalsIgnoreCase("Linux Application")) {
            jComboBox1.setSelectedIndex(1);
        } else if(type.equalsIgnoreCase("Linux Daemon")) {
            jComboBox1.setSelectedIndex(2);
        } else if(type.equalsIgnoreCase("Patch")) {
            jComboBox1.setSelectedIndex(3);
        } else if(type.equalsIgnoreCase("Plugin")) {
            jComboBox1.setSelectedIndex(4);
        } else if(type.equalsIgnoreCase("Service")) {
            jComboBox1.setSelectedIndex(5);
        } else if(type.equalsIgnoreCase("Theme")) {
            jComboBox1.setSelectedIndex(6);
        } else {
            jComboBox1.setSelectedIndex(0);
        }
    }

    private String getFlag(javax.swing.JComboBox comboBox) {
        String result = null;
        if(comboBox.getSelectedIndex()==1) {
            result = "RestartJava";
        } else if(comboBox.getSelectedIndex()==2) {
            result = "RestartLuna";
        } else if(comboBox.getSelectedIndex()==3) {
            result = "RestartDevice";
        }
        return result;
    }

    private void setFlag(javax.swing.JComboBox comboBox, String flag) {
        if(flag.equals("RestartJava")) {
            comboBox.setSelectedIndex(1);
        } else if(flag.equals("RestartLuna")) {
            comboBox.setSelectedIndex(2);
        } else if(flag.equals("RestartDevice")) {
            comboBox.setSelectedIndex(2);
        } else {
            comboBox.setSelectedIndex(0);
        }
    }

    private void resetCaretPositions() {
        jTextField2.setCaretPosition(0);
        jTextField3.setCaretPosition(0);
        jTextField5.setCaretPosition(0);
        jTextField6.setCaretPosition(0);
        jTextField7.setCaretPosition(0);
        jTextField4.setCaretPosition(0);
        jTextArea2.setCaretPosition(0);
        jTextField9.setCaretPosition(0);
        jTextField12.setCaretPosition(0);
        jTextField11.setCaretPosition(0);
        jTextField10.setCaretPosition(0);
    }

    private String readFile(File f) throws IOException {
        String out = "";
        String line = null;
        BufferedReader br = new BufferedReader(new FileReader(f));
        line = br.readLine();
        while(line!=null) {
            out += line.trim();
            line = br.readLine();
            if(line!=null) {
                out += " ";
            }
        }
        br.close();
        return out;
    }

    private void loadFromJSON(File json) {
        try {
            String s = readFile(json);
            if(s.trim().length()!=0) {
                JSONObject jsonO = new JSONObject(s);
                if(jsonO.has("title")) {
                    jTextField3.setText(jsonO.getString("title"));
                }
                if(jsonO.has("id")) {
                    jTextField5.setText(jsonO.getString("id"));
                    jTextField2.setText("/usr/palm/applications/" +
                            jTextField5.getText() + "/");
                    jTextField2.setCaretPosition(0);
                }
                if(jsonO.has("version")) {
                    jTextField6.setText(jsonO.getString("version")
                            .replaceAll("-", ".").replaceAll("_", "."));
                }
                if(jsonO.has("vendor")) {
                    jTextField7.setText(jsonO.getString("vendor"));
                }
            }
        } catch(Exception e) {}
    }

    public void checkForControl(File dir) {
        File control = new File(dir, "control");
        File postInstall = new File(dir, "postinst");
        File preRemoval = new File(dir, "prerm");
        File palmPostInstall = new File(dir, "pmPostInstall.script");
        File palmPreRemoval = new File(dir, "pmPreRemove.script");
        if(control.exists()) {
            readFromControlFile(control);
            if(jTextField2.getText().length()==0) {
                jTextField2.setText("/");
            }
        }
        if(postInstall.exists()) {
            postinst = postInstall;
            jTextField13.setText(postinst.getAbsolutePath());
        }
        if(preRemoval.exists()) {
            prerm = preRemoval;
            jTextField14.setText(prerm.getAbsolutePath());
        }
        if(palmPostInstall.exists()) {
            pmPostInstall = palmPostInstall;
            jTextField15.setText(pmPostInstall.getAbsolutePath());
        }
        if(palmPreRemoval.exists()) {
            pmPreRemove = palmPreRemoval;
            jTextField16.setText(pmPreRemove.getAbsolutePath());
        }
    }

    public void readFromControlFile(File control) {
    	try {
            BufferedReader input = new BufferedReader(new FileReader(control));
            String line = input.readLine();
            while(line!=null) {
                line = line.trim();
                if(line.length()>0 && line.indexOf(":")!=line.length()-1) {
                    if(line.startsWith("Package")) {
                    	jTextField5.setText(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Description")) {
                    	jTextField3.setText(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Version")) {
                    	jTextField6.setText(line.substring(line.indexOf(":")+2)
                                .replaceAll("-", ".").replaceAll("_", "."));
                    } else if(line.startsWith("Architecture")) {
                    	setArch(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Maintainer")) {
                        jTextField7.setText(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Depends")) {
                        String[] tokens = line.substring(line.indexOf(":")+2)
                                .split(",");
                        for(int i=0; i<tokens.length; i++) {
                            tokens[i] = tokens[i].trim();
                            if(!depends.contains(tokens[i])) {
                                depends.add(tokens[i]);
                            }
                        }
                        jList2.setListData(depends.toArray());
                    } else if(line.startsWith("Source")) {
                    	JSONObject src = new JSONObject(line.substring(
                                line.indexOf(":")+2));
                        if(src.has("Icon")) {
                            jTextField4.setText(src.getString("Icon"));
                        }
                        if(src.has("Screenshots")) {
                            JSONArray ss = src.getJSONArray("Screenshots");
                            for(int i=0; i<ss.length(); i++) {
                                if(!ssURLs.contains(ss.getString(i))) {
                                    ssURLs.add(ss.getString(i));
                                }
                            }
                            jList1.setListData(ssURLs.toArray());
                        }
                        if(src.has("FullDescription")) {
                            jTextArea2.setText(src.getString("FullDescription")
                                    .replaceAll("<br>", "\n"));
                        }
                        if(src.has("Homepage")) {
                            jTextField9.setText(src.getString("Homepage"));
                        }
                        if(src.has("Type")) {
                            setType(src.getString("Type"));
                        }
                        if(src.has("Category")) {
                            jTextField12.setText(src.getString("Category"));
                        }
                        if(src.has("License")) {
                            jTextField11.setText(src.getString("License"));
                        }
                        if(src.has("Location")) {
                            jTextField10.setText(src.getString("Location"));
                        }
                        if(src.has("PostInstallFlags")) {
                            setFlag(jComboBox2, src.getString("PostInstallFlags"));
                        }
                        if(src.has("PostUpdateFlags")) {
                            setFlag(jComboBox3, src.getString("PostUpdateFlags"));
                        }
                        if(src.has("PostRemoveFlags")) {
                            setFlag(jComboBox4, src.getString("PostRemoveFlags"));
                        }
                    }
                }
                line = input.readLine();
            }
        } catch(Exception e) {}
    }

    private void swapEndlineCharacters(File f) {
        try {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while(line!=null) {
                sb.append(line+"\n");
                line = br.readLine();
            }
            br.close();
            BufferedWriter out=new BufferedWriter (new FileWriter(f));
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch(Exception e) {}
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
        jComboBox5 = new javax.swing.JComboBox();
        jTextField6 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jButton10 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jTextField15 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jTextField13 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton7 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jTextField14 = new javax.swing.JTextField();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLabel15 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jTextField12 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel24 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ca.canucksoftware.ipkpackager.IpkPackagerApp.class).getContext().getResourceMap(IpkPackagerView.class);
        jLayeredPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jLayeredPane2.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jLayeredPane2.border.titleFont"))); // NOI18N
        jLayeredPane2.setName("jLayeredPane2"); // NOI18N

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "all", "armv6  (Pixi/Pixi Plus)", "armv7  (Pre/Pre Plus/Pre 2)", "i686  (Emulator)" }));
        jComboBox5.setName("jComboBox5"); // NOI18N
        jComboBox5.setBounds(90, 140, 220, 20);
        jLayeredPane2.add(jComboBox5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
        jTextField6.setName("jTextField6"); // NOI18N
        jTextField6.setBounds(90, 80, 220, 20);
        jLayeredPane2.add(jTextField6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setName("jTextField5"); // NOI18N
        jTextField5.setBounds(90, 50, 220, 20);
        jLayeredPane2.add(jTextField5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setBounds(20, 80, 70, 20);
        jLayeredPane2.add(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N
        jTextField3.setBounds(90, 20, 220, 20);
        jLayeredPane2.add(jTextField3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(20, 110, 70, 20);
        jLayeredPane2.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setName("jTextField7"); // NOI18N
        jTextField7.setBounds(90, 110, 220, 20);
        jLayeredPane2.add(jTextField7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setBounds(20, 20, 70, 20);
        jLayeredPane2.add(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setBounds(20, 50, 70, 20);
        jLayeredPane2.add(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setBounds(20, 140, 70, 20);
        jLayeredPane2.add(jLabel21, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setBounds(10, 90, 330, 170);
        jLayeredPane1.add(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jLayeredPane4.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jLayeredPane4.border.titleFont"))); // NOI18N
        jLayeredPane4.setName("jLayeredPane4"); // NOI18N

        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setIconTextGap(0);
        jButton10.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jButton10.setBounds(303, 78, 20, 23);
        jLayeredPane4.add(jButton10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setBounds(10, 80, 80, 20);
        jLayeredPane4.add(jLabel20, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setIconTextGap(0);
        jButton9.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jButton9.setBounds(303, 108, 20, 23);
        jLayeredPane4.add(jButton9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setBounds(10, 110, 80, 20);
        jLayeredPane4.add(jLabel19, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N
        jLabel23.setBounds(10, 170, 80, 20);
        jLayeredPane4.add(jLabel23, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setBounds(10, 20, 60, 20);
        jLayeredPane4.add(jLabel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setIconTextGap(0);
        jButton11.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jButton11.setBounds(303, 138, 20, 23);
        jLayeredPane4.add(jButton11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField15.setName("jTextField15"); // NOI18N
        jTextField15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField15MousePressed(evt);
            }
        });
        jTextField15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField15ActionPerformed(evt);
            }
        });
        jTextField15.setBounds(90, 140, 210, 20);
        jLayeredPane4.add(jTextField15, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton6.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setIconTextGap(0);
        jButton6.setMargin(new java.awt.Insets(-1, -1, 0, 0));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jButton6.setBounds(290, 20, 30, 22);
        jLayeredPane4.add(jButton6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField13.setText(resourceMap.getString("jTextField13.text")); // NOI18N
        jTextField13.setName("jTextField13"); // NOI18N
        jTextField13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField13MousePressed(evt);
            }
        });
        jTextField13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField13ActionPerformed(evt);
            }
        });
        jTextField13.setBounds(90, 80, 210, 20);
        jLayeredPane4.add(jTextField13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField16.setName("jTextField16"); // NOI18N
        jTextField16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField16MousePressed(evt);
            }
        });
        jTextField16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });
        jTextField16.setBounds(90, 170, 210, 20);
        jLayeredPane4.add(jTextField16, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.setName("jList2"); // NOI18N
        jList2.setVisibleRowCount(3);
        jScrollPane4.setViewportView(jList2);

        jScrollPane4.setBounds(90, 20, 190, 50);
        jLayeredPane4.add(jScrollPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton7.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setIconTextGap(0);
        jButton7.setMargin(new java.awt.Insets(-1, -1, 0, 0));
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jButton7.setBounds(290, 50, 30, 22);
        jLayeredPane4.add(jButton7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setBounds(10, 140, 80, 20);
        jLayeredPane4.add(jLabel22, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setIconTextGap(0);
        jButton12.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jButton12.setBounds(303, 168, 20, 23);
        jLayeredPane4.add(jButton12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField14.setName("jTextField14"); // NOI18N
        jTextField14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField14MousePressed(evt);
            }
        });
        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });
        jTextField14.setBounds(90, 110, 210, 20);
        jLayeredPane4.add(jTextField14, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane4.setBounds(10, 260, 330, 200);
        jLayeredPane1.add(jLayeredPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jLayeredPane3.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jLayeredPane3.border.titleFont"))); // NOI18N
        jLayeredPane3.setName("jLayeredPane3"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setBounds(210, 330, 70, 30);
        jLayeredPane3.add(jLabel15, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setBounds(20, 150, 80, 20);
        jLayeredPane3.add(jLabel10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "RestartJava", "RestartLuna", "RestartDevice" }));
        jComboBox3.setName("jComboBox3"); // NOI18N
        jComboBox3.setBounds(280, 340, 120, 20);
        jLayeredPane3.add(jComboBox3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "RestartJava", "RestartLuna", "RestartDevice" }));
        jComboBox2.setName("jComboBox2"); // NOI18N
        jComboBox2.setBounds(100, 340, 100, 20);
        jLayeredPane3.add(jComboBox2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField12.setName("jTextField12"); // NOI18N
        jTextField12.setBounds(280, 270, 120, 20);
        jLayeredPane3.add(jTextField12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField10.setText(resourceMap.getString("jTextField10.text")); // NOI18N
        jTextField10.setName("jTextField10"); // NOI18N
        jTextField10.setBounds(280, 380, 120, 20);
        jLayeredPane3.add(jTextField10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setBounds(20, 270, 60, 20);
        jLayeredPane3.add(jLabel12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField19.setName("jTextField19"); // NOI18N
        jTextField19.setBounds(280, 430, 120, 20);
        jLayeredPane3.add(jTextField19, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N
        jLabel26.setBounds(210, 420, 70, 40);
        jLayeredPane3.add(jLabel26, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setBounds(210, 380, 70, 20);
        jLayeredPane3.add(jLabel17, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setBounds(210, 270, 70, 20);
        jLayeredPane3.add(jLabel13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setBounds(20, 370, 70, 30);
        jLayeredPane3.add(jLabel16, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setBounds(20, 60, 80, 30);
        jLayeredPane3.add(jLabel9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setBounds(20, 300, 60, 20);
        jLayeredPane3.add(jLabel14, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField17.setName("jTextField17"); // NOI18N
        jTextField17.setBounds(280, 300, 120, 20);
        jLayeredPane3.add(jTextField17, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setBounds(20, 30, 60, 20);
        jLayeredPane3.add(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField18.setName("jTextField18"); // NOI18N
        jTextField18.setBounds(100, 430, 100, 20);
        jLayeredPane3.add(jTextField18, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton5.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setIconTextGap(0);
        jButton5.setMargin(new java.awt.Insets(-1, -1, 0, 0));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jButton5.setBounds(370, 60, 30, 22);
        jLayeredPane3.add(jButton5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setBounds(20, 240, 60, 20);
        jLayeredPane3.add(jLabel11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField11.setText(resourceMap.getString("jTextField11.text")); // NOI18N
        jTextField11.setName("jTextField11"); // NOI18N
        jTextField11.setBounds(100, 300, 100, 20);
        jLayeredPane3.add(jTextField11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setBounds(20, 420, 80, 40);
        jLayeredPane3.add(jLabel25, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea2.setColumns(20);
        jTextArea2.setFont(resourceMap.getFont("jTextArea2.font")); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(4);
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jTextArea2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextArea2KeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(jTextArea2);

        jScrollPane3.setBounds(100, 150, 300, 80);
        jLayeredPane3.add(jScrollPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N
        jLabel24.setBounds(210, 300, 70, 20);
        jLayeredPane3.add(jLabel24, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField9.setText(resourceMap.getString("jTextField9.text")); // NOI18N
        jTextField9.setName("jTextField9"); // NOI18N
        jTextField9.setBounds(100, 240, 300, 20);
        jLayeredPane3.add(jTextField9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setBounds(20, 330, 70, 30);
        jLayeredPane3.add(jLabel18, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Application", "Linux App", "Daemon", "Patch", "Plugin", "Service", "Theme" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.setBounds(100, 270, 100, 20);
        jLayeredPane3.add(jComboBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setName("jList1"); // NOI18N
        jList1.setVisibleRowCount(4);
        jScrollPane2.setViewportView(jList1);

        jScrollPane2.setBounds(100, 60, 260, 70);
        jLayeredPane3.add(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setIconTextGap(0);
        jButton4.setMargin(new java.awt.Insets(-1, -1, 0, 0));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton4.setBounds(370, 90, 30, 22);
        jLayeredPane3.add(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setName("jTextField4"); // NOI18N
        jTextField4.setBounds(100, 30, 300, 20);
        jLayeredPane3.add(jTextField4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "RestartJava", "RestartLuna", "RestartDevice" }));
        jComboBox4.setName("jComboBox4"); // NOI18N
        jComboBox4.setBounds(100, 380, 100, 20);
        jLayeredPane3.add(jComboBox4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBounds(360, 10, 420, 490);
        jLayeredPane1.add(jLayeredPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setBounds(10, 10, 60, 20);
        jLayeredPane1.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setIconTextGap(0);
        jButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.setBounds(320, 8, 20, 23);
        jLayeredPane1.add(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jButton8.setBounds(70, 470, 130, 30);
        jLayeredPane1.add(jButton8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.setBounds(210, 470, 70, 30);
        jLayeredPane1.add(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.setBounds(120, 60, 220, 20);
        jLayeredPane1.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField1MousePressed(evt);
            }
        });
        jTextField1.setBounds(70, 10, 240, 20);
        jLayeredPane1.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField8.setBackground(resourceMap.getColor("jTextField8.background")); // NOI18N
        jTextField8.setEditable(false);
        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setName("jTextField8"); // NOI18N
        jTextField8.setBounds(10, 60, 110, 20);
        jLayeredPane1.add(jTextField8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setBounds(10, 40, 160, 20);
        jLayeredPane1.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setIconTextGap(0);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.setBounds(310, 460, 30, 30);
        jLayeredPane1.add(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        folder = null;
        postinst = null;
        prerm = null;
        pmPostInstall = null;
        pmPreRemove = null;
        depends.clear();
        ssURLs.clear();
        
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jComboBox5.setSelectedIndex(0);
        jTextField13.setText("");
        jTextField14.setText("");
        jTextField15.setText("");
        jTextField16.setText("");
        jList2.setListData(depends.toArray());

        jTextField4.setText("");
        jList1.setListData(ssURLs.toArray());
        jTextArea2.setText("");
        jTextField9.setText("");
        jComboBox1.setSelectedIndex(0);
        jTextField12.setText("");
        jTextField11.setText("");
        jTextField10.setText("");
        jComboBox2.setSelectedIndex(0);
        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jTextField17.setText("");
        jTextField18.setText("");
        jTextField19.setText("");

        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MousePressed
        File f = loadFileChooser(true, null, null);
        if(f!=null) {
            folder = f;
            checkForControl(folder);
            checkForControl(new File(folder, "CONTROL"));
            checkForControl(folder.getParentFile());
            checkForControl(new File(folder.getParentFile(), "CONTROL"));
            File json = new File(folder, "appinfo.json");
            if(json.exists()) {
                loadFromJSON(json);
            } else {
                File appDir = new File(folder, "usr/palm/applications");
                if(appDir.isDirectory()) {
                    File[] list = appDir.listFiles();
                    for(int i=0; i<list.length; i++) {
                        if(list[i].isDirectory()) {
                            json = new File(list[i], "appinfo.json");
                            if(json.exists()) {
                                loadFromJSON(json);
                                jTextField2.setText("/");
                                break;
                            }
                        }
                    }
                }
                
            }
            resetCaretPositions();
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jTextField1MousePressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        isExpanded = !isExpanded;
        if(isExpanded) {
            jButton2.setText("⇐");
            getFrame().setSize(getFrame().getWidth()+437, getFrame().getHeight());
        } else {
            jButton2.setText("⇒");
            getFrame().setSize(getFrame().getWidth()-437, getFrame().getHeight());
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTextField1MousePressed(null);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField13ActionPerformed
    }//GEN-LAST:event_jTextField13ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String input = JOptionPane.showInputDialog("Add a depends:");
        if(input!=null && input.length()>0) {
            depends.add(input);
            jList2.setListData(depends.toArray());
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        int i = jList2.getSelectedIndex();
        if(i>-1) {
            depends.remove(i);
            jList2.setListData(depends.toArray());
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String input = JOptionPane.showInputDialog("Add a screenshot URL:");
        if(input!=null && input.length()>0) {
            ssURLs.add(input);
            jList1.setListData(ssURLs.toArray());
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int i = jList1.getSelectedIndex();
        if(i>-1) {
            ssURLs.remove(i);
            jList1.setListData(ssURLs.toArray());
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if(okToGo()) {
            String arch = getArch();
            String filename = jTextField5.getText() + "_" + jTextField6.getText() + "_" + arch + ".ipk";
            File out = loadFileChooser(false, new IpkChooseFilter(), filename);
            if(out!=null) {
                try {
                    if(!out.getName().endsWith(".ipk")) {
                        out = new File(out.getCanonicalPath() + ".ipk");
                    }
                    IpkgBuilder ib = new IpkgBuilder(folder, getFilepath(), out);
                    String title = jTextField3.getText().trim();
                    ib.setPackageName(title);
                    ib.setPackageID(jTextField5.getText().trim());
                    ib.setPackageVersion(jTextField6.getText().trim());
                    setAuthor(ib);
                    ib.setArch(arch);
                    if(postinst!=null) {
                        swapEndlineCharacters(postinst);
                        ib.setPostinst(postinst);
                    }
                    if(prerm!=null) {
                        swapEndlineCharacters(prerm);
                        ib.setPrerm(prerm);
                    }
                    if(pmPostInstall!=null) {
                        swapEndlineCharacters(pmPostInstall);
                        ib.setPalmPostinst(pmPostInstall);
                    }
                    if(pmPreRemove!=null) {
                        swapEndlineCharacters(pmPreRemove);
                        ib.setPalmPrerm(pmPreRemove);
                    }
                    if(depends.size()>0) {
                        ib.setDepends(depends.toArray(new String[depends.size()]));
                    }
                    if(isExpanded) {
                        JSONObject source = new JSONObject();
                        source.put("Title", title);
                        String time = String.valueOf((System.currentTimeMillis()/1000));
                        source.put("LastUpdated", time);
                        String icon = jTextField4.getText().trim();
                        if(icon.length()>0) {
                            source.put("Icon", icon);
                        }
                        if(ssURLs.size()>0) {
                            source.put("Screenshots", new JSONArray(ssURLs));
                        }
                        String description = jTextArea2.getText().trim();
                        if(description.length()>0) {
                            description = description.replaceAll("\n", "<br>");
                            if(description.length()>4096) {
                                description = description.substring(0, 4096);
                            }
                            source.put("FullDescription", description);
                        }
                        String homepage = jTextField9.getText().trim();
                        if(homepage.length()>0) {
                            source.put("Homepage", homepage);
                        }
                        source.put("Type", getType());
                        String category = jTextField12.getText().trim();
                        if(category.length()>0) {
                            source.put("Category", category);
                        }
                        String license = jTextField11.getText().trim();
                        if(license.length()>0) {
                            source.put("License", license);
                        }
                        String locURL = jTextField10.getText().trim();
                        if(locURL.length()>0) {
                            source.put("Location", locURL);
                        }
                        String piFlag = getFlag(jComboBox2);
                        if(piFlag!=null) {
                            source.put("PostInstallFlags", piFlag);
                        }
                        String puFlag = getFlag(jComboBox3);
                        if(puFlag!=null) {
                            source.put("PostUpdateFlags", puFlag);
                        }
                        String prFlag = getFlag(jComboBox4);
                        if(prFlag!=null) {
                            source.put("PostRemoveFlags", prFlag);
                        }
                        String srcURL = jTextField17.getText().trim();
                        if(srcURL.length()>0) {
                            source.put("Source", srcURL);
                        }
                        String minVer = jTextField18.getText().trim();
                        if(minVer.length()>0) {
                            source.put("MinWebOSVersion", minVer);
                        }
                        String maxVer = jTextField19.getText().trim();
                        if(maxVer.length()>0) {
                            source.put("MaxWebOSVersion", maxVer);
                        }
                        ib.setPackageSource(source);
                    }
                    ib.build();
                    JOptionPane.showMessageDialog(mainPanel, "Package created successfully!");
                } catch(Exception e) {}
            }
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        jTextField14MousePressed(null);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        jTextField13MousePressed(null);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTextArea2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea2KeyTyped
        String text = jTextArea2.getText().replaceAll("\n", "<br>");
        if(text.length()>4096) {
            int caret = jTextArea2.getCaretPosition();
            text = text.substring(0, 4096);
            text = text.replaceAll("<br>", "\n");
            if(caret >= text.length()) {
                caret = text.length()-1;
            }
            jTextArea2.setText(text);
            jTextArea2.setCaretPosition(caret);
        }
    }//GEN-LAST:event_jTextArea2KeyTyped

    private void jTextField13MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField13MousePressed
        postinst = loadFileChooser(false, null, null);
        if(postinst!=null) {
            jTextField13.setText(postinst.getAbsolutePath());
        } else {
            jTextField13.setText("");
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jTextField13MousePressed

    private void jTextField14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField14MousePressed
        prerm = loadFileChooser(false, null, null);
        if(prerm!=null) {
            jTextField14.setText(prerm.getAbsolutePath());
        } else {
            jTextField14.setText("");
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jTextField14MousePressed

    private void jTextField15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField15MousePressed
        pmPostInstall = loadFileChooser(false, null, null);
        if(pmPostInstall!=null) {
            jTextField15.setText(pmPostInstall.getAbsolutePath());
        } else {
            jTextField15.setText("");
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jTextField15MousePressed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField15ActionPerformed
    }//GEN-LAST:event_jTextField15ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        jTextField15MousePressed(null);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jTextField16MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField16MousePressed
        pmPreRemove = loadFileChooser(false, null, null);
        if(pmPreRemove!=null) {
            jTextField16.setText(pmPreRemove.getAbsolutePath());
        } else {
            jTextField16.setText("");
        }
        t.schedule(new DelayedLoad(), 50);
    }//GEN-LAST:event_jTextField16MousePressed

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        jTextField16MousePressed(null);
    }//GEN-LAST:event_jButton12ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
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

    private class ResizeScreen extends TimerTask {
        public void run() {
            jButton2.setText("⇒");
            getFrame().setSize(getFrame().getWidth()-437, getFrame().getHeight());
        }
    }

    private class DelayedLoad extends TimerTask {
        public void run() {
            jLayeredPane1.requestFocus();
        }
    }

    private class IpkChooseFilter extends FileFilter {
        private final String okFileExtension = ".ipk";

        public boolean accept(File f) {
            if (f.getName().toLowerCase().endsWith(okFileExtension) || f.isDirectory())
                return true;
            return false;
        }

        public String getDescription() {
            return "Ipk Files";
        }
    }

    public class DocumentFilter extends PlainDocument {
        public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String ALPHA = LOWERCASE + UPPERCASE;
        public static final String NUMERIC = "0123456789";
        public static final String PERIOD = ".";
        public static final String FLOAT = NUMERIC + PERIOD;
        public static final String SYMBOLS = PERIOD + "_-";
        public static final String ALPHA_NUMERIC = ALPHA + NUMERIC + SYMBOLS;

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
            if (str == null) return;

            if (acceptedChars.equals(UPPERCASE))
                str = str.toUpperCase();
            else if (acceptedChars.equals(LOWERCASE))
                str = str.toLowerCase();

            for (int i=0; i < str.length(); i++) {
                if (acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
                    return;
            }

            if (negativeAccepted && str.indexOf("-") != -1) {
                if (str.indexOf("-") != 0 || offset != 0 ) {
                    return;
                }
            }

            super.insertString(offset, str, attr);
        }
    }
}
