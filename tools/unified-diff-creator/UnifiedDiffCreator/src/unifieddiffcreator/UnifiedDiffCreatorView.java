/*
 * UnifiedDiffCreatorView.java
 */

package unifieddiffcreator;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Container;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
import bmsi.util.*;
import net.iharder.dnd.*;

/**
 * The application's main frame.
 */
public class UnifiedDiffCreatorView extends FrameView {
    private File patch;
    private File item1;
    private File item2;
    private String path;
    private Point btnBase1;
    private Point btnBase2;
    private boolean expanded;

    public UnifiedDiffCreatorView(SingleFrameApplication app) {
        super(app);
        initComponents();
        item1 = null;
        item2 = null;
        path = null;
        patch = null;
        btnBase1 = jButton2.getLocation();
        btnBase2 = jButton4.getLocation();
        expanded = false;
        jTextField5.setDocument(new VersionFilter(VersionFilter.NUMERIC));
        jTextField6.setDocument(new VersionFilter(VersionFilter.FLOAT));
        getFrame().setSize(getFrame().getWidth(), getFrame().getHeight()-190);
        Point p1 = new Point(jButton2.getX(), jLayeredPane3.getY());
        Point p2 = new Point(jButton4.getX(), jLayeredPane3.getY());
        jLayeredPane3.setVisible(false);
        jButton2.setLocation(p1);
        jButton4.setLocation(p2);
        new FileDrop(jLayeredPane1, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    if(files[0]!=null) {
                        item1 = files[0];
                        jTextField1.setText(item1.getPath());
                    }
                }
            }
        });
        new FileDrop(jLayeredPane2, new FileDrop.Listener() {
            public void  filesDropped(File[] files ) {
                if(files!=null && files.length>0) {
                    if(files[0]!=null) {
                        item2 = files[0];
                        jTextField2.setText(item2.getPath());
                    }
                }
            }
        });
        getRootPane().requestFocus();
    }

    private String formatDest(String path) {
        String result = null;
        result = path.replace("\\", "/");
        if(!result.startsWith("/"))
            result = "/" + result;
        return result;
    }

    private File loadFileChooser(javax.swing.filechooser.FileFilter ff, boolean isSave) {
        File result;
        JFileChooser fc = new JFileChooser(); //Create a file chooser
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        disableNewFolderButton(fc);
        fc.setFileFilter(ff);
        if(!isSave) {
            fc.setDialogTitle("");
            if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
            } else {
                result = null;
            }
        } else {
            fc.setDialogTitle("Save As...");
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                result = fc.getSelectedFile();
            } else {
                result = null;
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

    private List<String> fileToLines(File f) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            line = in.readLine();
            while (line != null) {
                lines.add(line);
                line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
            lines = null;
        }
        return lines;
    }

    private File linesToFile(List<String> lines, File f) {
        if(f.exists())
            f.delete();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            for(int i=0; i<lines.size(); i++)
                out.write(lines.get(i) + "\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            f = null;
        }
        return f;
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
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLabel1 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(unifieddiffcreator.UnifiedDiffCreatorApp.class).getContext().getResourceMap(UnifiedDiffCreatorView.class);
        jLayeredPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jLayeredPane3.border.title"))); // NOI18N
        jLayeredPane3.setName("jLayeredPane3"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(10, 30, 90, 20);
        jLayeredPane3.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setName("jTextField4"); // NOI18N
        jTextField4.setBounds(100, 30, 350, 22);
        jLayeredPane3.add(jTextField4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField5.setName("jTextField5"); // NOI18N
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField5KeyTyped(evt);
            }
        });
        jTextField5.setBounds(100, 60, 130, 22);
        jLayeredPane3.add(jTextField5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setBounds(10, 60, 90, 20);
        jLayeredPane3.add(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setBounds(10, 90, 270, 20);
        jLayeredPane3.add(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField6.setName("jTextField6"); // NOI18N
        jTextField6.setBounds(310, 90, 140, 22);
        jLayeredPane3.add(jTextField6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setBounds(250, 60, 60, 20);
        jLayeredPane3.add(jLabel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField7.setName("jTextField7"); // NOI18N
        jTextField7.setBounds(310, 60, 140, 22);
        jLayeredPane3.add(jTextField7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setName("jTextField8"); // NOI18N
        jTextField8.setBounds(10, 140, 440, 22);
        jLayeredPane3.add(jTextField8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setBounds(10, 110, 100, 30);
        jLayeredPane3.add(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField3.setName("jTextField3"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setBounds(10, 10, 100, 20);
        jLayeredPane1.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setFocusable(false);
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField1MousePressed(evt);
            }
        });
        jTextField1.setBounds(120, 10, 330, 22);
        jLayeredPane1.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setName("jLayeredPane2"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setBounds(10, 10, 100, 20);
        jLayeredPane2.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField2.setFocusable(false);
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField2MousePressed(evt);
            }
        });
        jTextField2.setBounds(120, 10, 330, 22);
        jLayeredPane2.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(398, 398, 398)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                            .addComponent(jLayeredPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MousePressed
        File tmp = loadFileChooser(new PatchableChooseFilter(), false);
        if(tmp!=null) {
            item2 = tmp;
            jTextField2.setText(item2.getPath());
        }
        getRootPane().requestFocus();
}//GEN-LAST:event_jTextField2MousePressed

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked

}//GEN-LAST:event_jTextField1MouseClicked

    private void jTextField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MousePressed
        File tmp = loadFileChooser(new PatchableChooseFilter(), false);
        if(tmp!=null) {
            item1 = tmp;
            jTextField1.setText(item1.getPath());
        }
        getRootPane().requestFocus();
}//GEN-LAST:event_jTextField1MousePressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        path = formatDest(jTextField3.getText().trim());
        if(path.length()>1 && item1!=null && item2!=null) {
            patch = loadFileChooser(new PatchChooseFilter(), true);
            try {
                if(patch!=null) {
                    if(!patch.getName().endsWith(".patch")) {
                        patch = new File(patch.getCanonicalPath() + ".patch");
                    }
                    UnifiedDiff uni = new UnifiedDiff(item1, item2);
                    String name = null;
                    String ver = null;
                    String dev = null;
                    String desc = null;
                    String tmp = jTextField4.getText().trim();
                    if(tmp.length()>0)
                        name = tmp;
                    tmp = jTextField5.getText().trim();
                    String tmp2 = jTextField6.getText().trim();
                    if(tmp.length()>0 && tmp2.length()>0)
                        ver = tmp2 + "-" + tmp;
                    else if(tmp.length()>0 && tmp2.length()==0)
                        ver = tmp;
                    else if(tmp.length()==0 && tmp2.length()>0)
                        ver = tmp2 + "-1";
                    tmp = jTextField7.getText().trim();
                    if(tmp.length()>0)
                        dev = tmp;
                    tmp = jTextField8.getText().trim();
                    if(tmp.length()>0)
                        desc = tmp;
                    uni.addMeta(name, ver, dev, desc);
                    patch = uni.createDiff(patch, path);
                    if(patch!=null) {
                        JOptionPane.showMessageDialog(mainPanel, "Patch created successfully!");
                    }
                }
            } catch(Exception e) {
                patch = null;
            }
        }
        getRootPane().requestFocus();
}//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(expanded) {//Dimension
            getFrame().setSize(getFrame().getWidth(), getFrame().getHeight()-190);
            Point p1 = new Point(jButton2.getX(), jLayeredPane3.getY());
            Point p2 = new Point(jButton4.getX(), jLayeredPane3.getY());
            jLayeredPane3.setVisible(false);
            jButton2.setLocation(p1);
            jButton4.setLocation(p2);
            jButton3.setText("⇓");
            expanded = false;
        } else {
            getFrame().setSize(getFrame().getWidth(), getFrame().getHeight()+190);
            jLayeredPane3.setVisible(true);
            jButton2.setLocation(btnBase1);
            jButton4.setLocation(btnBase2);
            jButton3.setText("⇑");
            expanded = true;
        }
        getRootPane().requestFocus();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyTyped
    }//GEN-LAST:event_jTextField5KeyTyped

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        item1 = null;
        item2 = null;
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        getRootPane().requestFocus();
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

    class PatchChooseFilter extends javax.swing.filechooser.FileFilter {
        private final String okFileExtension = ".patch";

        public boolean accept(File f) {
            if (f.getName().toLowerCase().endsWith(okFileExtension) || f.isDirectory())
                return true;
            return false;
        }

        public String getDescription() {
            return "Patch Files";
        }
    }
    class PatchableChooseFilter extends javax.swing.filechooser.FileFilter {
        private final String[] okFileExtensions = new String[] {".html", ".css", ".js", ".json", ".conf", ".txt"};

        public boolean accept(File f) {
            for (String extension : okFileExtensions)
                if (f.getName().toLowerCase().endsWith(extension) || f.isDirectory())
                    return true;
            return false;
        }

        public String getDescription() {
            return "Patchable Files";
        }
    }

    public class VersionFilter extends PlainDocument {
        public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String ALPHA = LOWERCASE + UPPERCASE;
        public static final String NUMERIC = "0123456789";
        public static final String FLOAT = NUMERIC + ".";
        public static final String ALPHA_NUMERIC = ALPHA + NUMERIC;

        protected String acceptedChars = null;
        protected boolean negativeAccepted = false;

        public VersionFilter() {
            this(ALPHA_NUMERIC);
        }
        public VersionFilter(String acceptedchars) {
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
