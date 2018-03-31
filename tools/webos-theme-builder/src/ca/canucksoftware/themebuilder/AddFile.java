
package ca.canucksoftware.themebuilder;

import javax.swing.JFileChooser;
import java.io.File;
import java.util.List;

public class AddFile extends javax.swing.JDialog {
    public FileEntry item;
    public String version;
    public File prevDir;

    public AddFile(java.awt.Frame parent, FileEntry curr, String currVersion, List<String> versions, String currCategory) {
        super(parent);
        initComponents();
        if(curr!=null) {
            setTitle("Edit Entry");
            jButton2.setText("OK");
            item = curr;
            jTextField1.setText(item.file.getPath());
            jTextField2.setText(item.dest);
            for(int i=0; i<jComboBox1.getItemCount(); i++) {
                String cat = ((String) jComboBox1.getItemAt(i)).toLowerCase().replaceAll(" ", "_");
                if(cat.equals(curr.category)) {
                    jComboBox1.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            for(int i=0; i<jComboBox1.getItemCount(); i++) {
                String cat = ((String) jComboBox1.getItemAt(i)).toLowerCase().replaceAll(" ", "_");
                if(cat.equals(currCategory)) {
                    jComboBox1.setSelectedIndex(i);
                    break;
                }
            }
            item = new FileEntry();
        }
        version = currVersion;
        for(int i=0; i<versions.size(); i++) {
            jComboBox2.addItem("webOS " + versions.get(i));
        }
        jComboBox2.setSelectedItem("webOS " + version);
        getContentPane().requestFocus();
    }

    private String formatDest(String path) {
        String result = null;
        result = path.replace("\\", "/");
        if(!result.startsWith("/"))
            result = "/" + result;
        return result;
    }

    private File loadFileChooser() {
        JFileChooser fc = new JFileChooser(); //Create a file chooser
        if(prevDir!=null)
            fc.setCurrentDirectory(prevDir);
        fc.setAcceptAllFileFilterUsed(true);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("");
        if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
            prevDir = fc.getSelectedFile().getParentFile();
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }

    public void closeAddContent() {
        dispose();
    }

    /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ca.canucksoftware.themebuilder.WebOSThemeBuilderApp.class).getContext().getResourceMap(AddFile.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setBackground(resourceMap.getColor("transfer.background")); // NOI18N
        setForeground(resourceMap.getColor("transfer.foreground")); // NOI18N
        setIconImage(null);
        setModal(true);
        setName("transfer"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        jLayeredPane2.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("jLayeredPane2.border.lineColor"))); // NOI18N
        jLayeredPane2.setName("jLayeredPane2"); // NOI18N

        jButton3.setFont(resourceMap.getFont("jButton3.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.setBounds(260, 140, 79, 25);
        jLayeredPane2.add(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
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
        jTextField1.setBounds(130, 10, 300, 22);
        jLayeredPane2.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox1.setMaximumRowCount(13);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "App Icons", "App Launcher", "Applications", "Boot Logo", "Enyo Widgets", "Exhibition", "Just Type", "Keyboard", "Lock Screen", "Quick Launcher", "Status Bar", "System Menus", "Wallpapers" }));
        jComboBox1.setSelectedIndex(2);
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.setBounds(130, 70, 180, 22);
        jLayeredPane2.add(jComboBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jComboBox2.setName("jComboBox2"); // NOI18N
        jComboBox2.setBounds(130, 100, 180, 22);
        jLayeredPane2.add(jComboBox2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setBounds(20, 70, 110, 20);
        jLayeredPane2.add(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setBounds(20, 10, 50, 20);
        jLayeredPane2.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.setBounds(130, 40, 300, 22);
        jLayeredPane2.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setBounds(20, 100, 110, 20);
        jLayeredPane2.add(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.setBounds(100, 140, 140, 25);
        jLayeredPane2.add(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setBounds(20, 40, 100, 20);
        jLayeredPane2.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setBounds(10, 10, 450, 180);
        jLayeredPane1.add(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    }//GEN-LAST:event_formWindowClosed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(jTextField1.getText().length()>0 && jTextField2.getText().length()>0) {
            item.dest = formatDest(jTextField2.getText().trim());
            item.file = new File(jTextField1.getText());
            item.category = ((String) jComboBox1.getSelectedItem()).toLowerCase().replaceAll(" ", "_");
            version = ((String) jComboBox2.getSelectedItem()).replace("webOS ", "");
            closeAddContent();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        closeAddContent();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
    }//GEN-LAST:event_jTextField1MouseClicked

    private void jTextField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MousePressed
        File tmp = loadFileChooser();
        if(tmp!=null) {
            jTextField1.setText(tmp.getPath());
        }
        getContentPane().requestFocus();
    }//GEN-LAST:event_jTextField1MousePressed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}