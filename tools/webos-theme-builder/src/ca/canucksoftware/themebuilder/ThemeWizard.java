
package ca.canucksoftware.themebuilder;

import javax.swing.JFileChooser;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Container;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.util.LinkedList;
import java.io.File;

public class ThemeWizard extends javax.swing.JDialog {
    public File prevDir;
    public LinkedList<IconEntry> icons;
    public LinkedList<FileEntry> files;
    public LinkedList<File> patches;
    public PatchData pData;
    public File wallpaper;

    public ThemeWizard(java.awt.Frame parent, File dir, LinkedList<IconEntry> i, LinkedList<FileEntry> f,
            LinkedList<File> p, PatchData pd, File wp) {
        super(parent);
        initComponents();
        prevDir = dir;
        icons = i;
        files = f;
        patches = p;
        pData = pd;
        wallpaper = wp;
        loadBasic();
        loadIcons();
        loadCalendar();
        loadEmoticons();
        loadSystem();
        loadPatches();
        getContentPane().requestFocus();
    }

    private void loadBasic() {
        FileEntry curr = null;
        if(wallpaper!=null) {
            jTextField1.setText(wallpaper.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/palm-logo.png"))!=null) {
            jTextField4.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/palm-logo-bright.png"))!=null) {
            jTextField3.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.musicplayer/images/background.png"))!=null) {
            jTextField13.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.photos/images/background.png"))!=null) {
            jTextField14.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.phone/images/backdrop-phone.png"))!=null) {
            jTextField72.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.videoplayer/images/background.png"))!=null) {
            jTextField15.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.browser/images/background-bookmarks-grid.png"))
                !=null) {
            jTextField12.setText(curr.file.getPath());
        }
    }

    private void loadIcons() {
        IconEntry curr;
        if((curr=getIcon("com.palm.app.backup"))!=null) {
            jTextField2.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.bluetooth"))!=null) {
            jTextField16.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.calculator"))!=null) {
            jTextField17.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.calendar"))!=null) {
            jTextField18.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.camera"))!=null) {
            jTextField19.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.clock"))!=null) {
            jTextField20.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.contacts"))!=null) {
            jTextField21.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.dateandtime"))!=null) {
            jTextField22.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.deviceinfo"))!=null) {
            jTextField23.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.devmodeswitcher"))!=null) {
            jTextField24.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.docviewer"))!=null) {
            jTextField25.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.email"))!=null) {
            jTextField26.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.help"))!=null) {
            jTextField27.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.maps"))!=null) {
            jTextField28.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.messaging"))!=null) {
            jTextField29.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.musicplayer"))!=null) {
            jTextField30.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.notes"))!=null) {
            jTextField31.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.phone"))!=null) {
            jTextField32.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.photos"))!=null) {
            jTextField33.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.screenlock"))!=null) {
            jTextField34.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.soundsandalerts"))!=null) {
            jTextField35.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.pdfviewer"))!=null) {
            jTextField36.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.tasks"))!=null) {
            jTextField37.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.updates"))!=null) {
            jTextField38.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.videoplayer.launcher"))!=null) {
            jTextField39.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.browser"))!=null) {
            jTextField2.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.wifi"))!=null) {
            jTextField40.setText(curr.image.getPath());
        }
        if((curr=getIcon("com.palm.app.youtube"))!=null) {
            jTextField73.setText(curr.image.getPath());
        }
    }

    private void loadCalendar() {
        FileEntry curr;
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-1.png"))!=null) {
            jTextField41.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-2.png"))!=null) {
            jTextField42.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-3.png"))!=null) {
            jTextField44.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-4.png"))!=null) {
            jTextField43.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-5.png"))!=null) {
            jTextField45.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-6.png"))!=null) {
            jTextField46.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-7.png"))!=null) {
            jTextField48.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-8.png"))!=null) {
            jTextField47.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-9.png"))!=null) {
            jTextField49.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-10.png"))!=null) {
            jTextField50.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-11.png"))!=null) {
            jTextField52.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-12.png"))!=null) {
            jTextField51.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-13.png"))!=null) {
            jTextField53.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-14.png"))!=null) {
            jTextField60.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-15.png"))!=null) {
            jTextField54.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-16.png"))!=null) {
            jTextField61.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-17.png"))!=null) {
            jTextField55.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-18.png"))!=null) {
            jTextField62.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-19.png"))!=null) {
            jTextField56.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-20.png"))!=null) {
            jTextField63.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-21.png"))!=null) {
            jTextField57.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-22.png"))!=null) {
            jTextField64.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-23.png"))!=null) {
            jTextField58.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-24.png"))!=null) {
            jTextField65.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-25.png"))!=null) {
            jTextField59.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-26.png"))!=null) {
            jTextField66.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-27.png"))!=null) {
            jTextField67.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-28.png"))!=null) {
            jTextField68.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-29.png"))!=null) {
            jTextField69.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-30.png"))!=null) {
            jTextField70.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-31.png"))!=null) {
            jTextField71.setText(curr.file.getPath());
        }
    }

    private void loadEmoticons() {
        FileEntry curr;
        if((curr=getFile("/usr/palm/emoticons/emoticon-angry.png"))!=null) {
            jTextField74.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-confused.png"))!=null) {
            jTextField97.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-cool.png"))!=null) {
            jTextField75.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-cry.png"))!=null) {
            jTextField96.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-embarrassed.png"))!=null) {
            jTextField76.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-eww.png"))!=null) {
            jTextField95.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-footinmouth.png"))!=null) {
            jTextField77.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-frown.png"))!=null) {
            jTextField94.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-gasp.png"))!=null) {
            jTextField78.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-grin.png"))!=null) {
            jTextField93.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-heart.png"))!=null) {
            jTextField79.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-innocent.png"))!=null) {
            jTextField92.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-kiss.png"))!=null) {
            jTextField80.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-laugh.png"))!=null) {
            jTextField91.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-naughty.png"))!=null) {
            jTextField81.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-neutral.png"))!=null) {
            jTextField90.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-sick.png"))!=null) {
            jTextField82.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-smile.png"))!=null) {
            jTextField89.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-undecided.png"))!=null) {
            jTextField83.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-wink.png"))!=null) {
            jTextField88.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/emoticons/emoticon-yuck.png"))!=null) {
            jTextField84.setText(curr.file.getPath());
        }
    }

    private void loadSystem() {
        FileEntry curr;
        if((curr=getFile("/usr/palm/sysmgr/images/screen-lock-wallpaper-mask.png"))!=null) {
            jTextField5.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/screen-lock-target-scrim.png"))!=null) {
            jTextField6.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/quick_launch_bg.png"))!=null) {
            jTextField7.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/quick_launch_stash.png"))!=null) {
            jTextField8.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/quick_launch_highlight.png"))!=null) {
            jTextField9.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/lib/luna/system/luna-applauncher/images/scrim.png"))!=null) {
            jTextField98.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/lib/luna/system/luna-applauncher/images/launcher-page-fade-top.png"))!=null) {
            jTextField99.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/lib/luna/system/luna-applauncher/images/launcher-page-fade-bottom.png"))!=null) {
            jTextField87.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/drive-mode-fullscreen.png"))!=null) {
            jTextField10.setText(curr.file.getPath());
        }
        if((curr=getFile("/usr/palm/sysmgr/images/media-sync-fullscreen.png"))!=null) {
            jTextField11.setText(curr.file.getPath());
        }
    }

    private void loadPatches() {
        if(pData.carrierString==null || pData.carrierString.length()==0) {
            jTextField85.setText("");
        } else {
            jTextField85.setText(pData.carrierString);
        }
        jTextField86.setText(pData.getHexVal());
        jSlider1.setValue(pData.opacity);
    }

    private FileEntry getFile(String dest) {
        FileEntry fe = null;
        for(int i=0; i<files.size(); i++) {
            fe = files.get(i);
            if(fe.dest.equalsIgnoreCase(dest)) {
                break;
            }
        }
        if(fe!=null) {
            if(!fe.dest.equalsIgnoreCase(dest)) {
                fe = null;
            }
        }
        return fe;
    }

    private IconEntry getIcon(String id) {
        IconEntry ie = null;
        for(int i=0; i<icons.size(); i++) {
            ie = icons.get(i);
            if(ie.appID.equalsIgnoreCase(id)) {
                break;
            }
        }
        if(ie!=null) {
            if(!ie.appID.equalsIgnoreCase(id)) {
                ie = null;
            }
        }
        return ie;
    }

    private String formatDest(String path) {
        String result = null;
        result = path.replace("\\", "/");
        if(!result.startsWith("/"))
            result = "/" + result;
        if(!result.endsWith("/"))
            result = result + "/";
        return result;
    }

    private File loadFileChooser(javax.swing.filechooser.FileFilter ff) {
        JFileChooser fc = new JFileChooser(); //Create a file chooser
        if(prevDir!=null)
            fc.setCurrentDirectory(prevDir);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(ff);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("");
        disableNewFolderButton(fc);
        if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
            prevDir = fc.getSelectedFile().getParentFile();
            return fc.getSelectedFile();
        } else {
            return null;
        }
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

    private void setFile(javax.swing.JTextField field, String dest) {
        File f = loadFileChooser(new PNGChooseFilter());
        FileEntry fe = getFile(dest);
        if(f!=null) {
            if(fe!=null) {
                fe.file = f;
            } else {
                fe = new FileEntry();
                fe.dest = dest;
                fe.file = f;
                files.add(fe);
            }
            field.setText(f.getPath());
        } else {
            field.setText("");
            if(fe!=null) {
                files.remove(fe);
            }
        }
    }

    private void setIcon(javax.swing.JTextField field, String id) {
        File f = loadFileChooser(new PNGChooseFilter());
        IconEntry ie = getIcon(id);
        if(f!=null) {
            if(ie!=null) {
                ie.image = f;
            } else {
                ie = new IconEntry();
                ie.appID = id;
                ie.image = f;
                icons.add(ie);
            }
            field.setText(f.getPath());
        } else {
            field.setText("");
            if(ie!=null) {
                icons.remove(ie);
            }
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jTextField72 = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jLabel23 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jTextField38 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextField73 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jTextField100 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jTextField21 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jTextField34 = new javax.swing.JTextField();
        jTextField33 = new javax.swing.JTextField();
        jTextField27 = new javax.swing.JTextField();
        jTextField35 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField39 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jTextField23 = new javax.swing.JTextField();
        jTextField40 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel104 = new javax.swing.JLabel();
        jTextField30 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jTextField37 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLayeredPane5 = new javax.swing.JLayeredPane();
        jLabel47 = new javax.swing.JLabel();
        jTextField53 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jTextField50 = new javax.swing.JTextField();
        jTextField44 = new javax.swing.JTextField();
        jTextField49 = new javax.swing.JTextField();
        jTextField42 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jTextField67 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jTextField52 = new javax.swing.JTextField();
        jTextField70 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextField69 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jTextField51 = new javax.swing.JTextField();
        jTextField62 = new javax.swing.JTextField();
        jTextField45 = new javax.swing.JTextField();
        jTextField63 = new javax.swing.JTextField();
        jTextField65 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jTextField60 = new javax.swing.JTextField();
        jTextField54 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jTextField58 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jTextField68 = new javax.swing.JTextField();
        jTextField64 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jTextField66 = new javax.swing.JTextField();
        jTextField61 = new javax.swing.JTextField();
        jTextField56 = new javax.swing.JTextField();
        jTextField47 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jTextField55 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jTextField43 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jTextField57 = new javax.swing.JTextField();
        jTextField59 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jTextField71 = new javax.swing.JTextField();
        jTextField46 = new javax.swing.JTextField();
        jLayeredPane6 = new javax.swing.JLayeredPane();
        jTextField91 = new javax.swing.JTextField();
        jTextField96 = new javax.swing.JTextField();
        jTextField76 = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jTextField81 = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jTextField74 = new javax.swing.JTextField();
        jTextField78 = new javax.swing.JTextField();
        jTextField79 = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jTextField94 = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        jTextField83 = new javax.swing.JTextField();
        jTextField75 = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jTextField93 = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jTextField97 = new javax.swing.JTextField();
        jTextField82 = new javax.swing.JTextField();
        jTextField92 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jTextField95 = new javax.swing.JTextField();
        jTextField89 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jTextField90 = new javax.swing.JTextField();
        jTextField80 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jTextField77 = new javax.swing.JTextField();
        jTextField84 = new javax.swing.JTextField();
        jTextField88 = new javax.swing.JTextField();
        jLabel76 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLayeredPane9 = new javax.swing.JLayeredPane();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jLabel98 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jTextField99 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jTextField87 = new javax.swing.JTextField();
        jTextField98 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLayeredPane10 = new javax.swing.JLayeredPane();
        jLabel100 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jTextField86 = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel99 = new javax.swing.JLabel();
        jTextField85 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ca.canucksoftware.themebuilder.WebOSThemeBuilderApp.class).getContext().getResourceMap(ThemeWizard.class);
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

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jLayeredPane3.setName("jLayeredPane3"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setToolTipText(resourceMap.getString("jLabel12.toolTipText")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setBounds(20, 380, 190, 20);
        jLayeredPane3.add(jLabel12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setToolTipText(resourceMap.getString("jLabel13.toolTipText")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setBounds(20, 180, 190, 20);
        jLayeredPane3.add(jLabel13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField3.setName("jTextField3"); // NOI18N
        jTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField3MousePressed(evt);
            }
        });
        jTextField3.setBounds(210, 130, 430, 22);
        jLayeredPane3.add(jTextField3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField15.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField15.setName("jTextField15"); // NOI18N
        jTextField15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField15MousePressed(evt);
            }
        });
        jTextField15.setBounds(210, 330, 430, 22);
        jLayeredPane3.add(jTextField15, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField72.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField72.setName("jTextField72"); // NOI18N
        jTextField72.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField72MousePressed(evt);
            }
        });
        jTextField72.setBounds(210, 280, 430, 22);
        jLayeredPane3.add(jTextField72, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel73.setText(resourceMap.getString("jLabel73.text")); // NOI18N
        jLabel73.setToolTipText(resourceMap.getString("jLabel73.toolTipText")); // NOI18N
        jLabel73.setName("jLabel73"); // NOI18N
        jLabel73.setBounds(20, 280, 150, 20);
        jLayeredPane3.add(jLabel73, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setToolTipText(resourceMap.getString("jLabel3.toolTipText")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setBounds(20, 130, 140, 20);
        jLayeredPane3.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField13.setName("jTextField13"); // NOI18N
        jTextField13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField13MousePressed(evt);
            }
        });
        jTextField13.setBounds(210, 180, 430, 22);
        jLayeredPane3.add(jTextField13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField12.setName("jTextField12"); // NOI18N
        jTextField12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField12MousePressed(evt);
            }
        });
        jTextField12.setBounds(210, 380, 430, 22);
        jLayeredPane3.add(jTextField12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField4.setName("jTextField4"); // NOI18N
        jTextField4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField4MousePressed(evt);
            }
        });
        jTextField4.setBounds(210, 80, 430, 22);
        jLayeredPane3.add(jTextField4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField14.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField14.setName("jTextField14"); // NOI18N
        jTextField14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField14MousePressed(evt);
            }
        });
        jTextField14.setBounds(210, 230, 430, 22);
        jLayeredPane3.add(jTextField14, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(resourceMap.getString("jLabel4.toolTipText")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setBounds(20, 80, 110, 20);
        jLayeredPane3.add(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(resourceMap.getString("jLabel1.toolTipText")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setBounds(20, 30, 110, 20);
        jLayeredPane3.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setToolTipText(resourceMap.getString("jLabel15.toolTipText")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setBounds(20, 330, 190, 20);
        jLayeredPane3.add(jLabel15, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setToolTipText(resourceMap.getString("jLabel14.toolTipText")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setBounds(20, 230, 190, 20);
        jLayeredPane3.add(jLabel14, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField1MousePressed(evt);
            }
        });
        jTextField1.setBounds(210, 30, 430, 22);
        jLayeredPane3.add(jTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab(resourceMap.getString("jLayeredPane3.TabConstraints.tabTitle"), jLayeredPane3); // NOI18N

        jLayeredPane4.setName("jLayeredPane4"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N
        jLabel23.setBounds(340, 140, 80, 20);
        jLayeredPane4.add(jLabel23, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel40.setText(resourceMap.getString("jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N
        jLabel40.setBounds(20, 440, 80, 20);
        jLayeredPane4.add(jLabel40, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField38.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField38.setName("jTextField38"); // NOI18N
        jTextField38.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField38MousePressed(evt);
            }
        });
        jTextField38.setBounds(430, 380, 210, 22);
        jLayeredPane4.add(jTextField38, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel37.setText(resourceMap.getString("jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N
        jLabel37.setBounds(20, 350, 80, 20);
        jLayeredPane4.add(jLabel37, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField29.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField29.setName("jTextField29"); // NOI18N
        jTextField29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField29MousePressed(evt);
            }
        });
        jTextField29.setBounds(100, 260, 210, 22);
        jLayeredPane4.add(jTextField29, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel38.setText(resourceMap.getString("jLabel38.text")); // NOI18N
        jLabel38.setName("jLabel38"); // NOI18N
        jLabel38.setBounds(340, 380, 80, 20);
        jLayeredPane4.add(jLabel38, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setBounds(20, 50, 80, 20);
        jLayeredPane4.add(jLabel16, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField17.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField17.setName("jTextField17"); // NOI18N
        jTextField17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField17MousePressed(evt);
            }
        });
        jTextField17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField17ActionPerformed(evt);
            }
        });
        jTextField17.setBounds(100, 80, 210, 22);
        jLayeredPane4.add(jTextField17, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField24.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField24.setName("jTextField24"); // NOI18N
        jTextField24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField24MousePressed(evt);
            }
        });
        jTextField24.setBounds(430, 170, 210, 22);
        jLayeredPane4.add(jTextField24, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N
        jLabel24.setBounds(20, 170, 80, 20);
        jLayeredPane4.add(jLabel24, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setBounds(340, 170, 80, 20);
        jLayeredPane4.add(jLabel25, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField73.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField73.setName("jTextField73"); // NOI18N
        jTextField73.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField73MousePressed(evt);
            }
        });
        jTextField73.setBounds(430, 440, 210, 22);
        jLayeredPane4.add(jTextField73, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setBounds(340, 80, 80, 20);
        jLayeredPane4.add(jLabel19, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel39.setText(resourceMap.getString("jLabel39.text")); // NOI18N
        jLabel39.setName("jLabel39"); // NOI18N
        jLabel39.setBounds(20, 380, 80, 20);
        jLayeredPane4.add(jLabel39, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel74.setText(resourceMap.getString("jLabel74.text")); // NOI18N
        jLabel74.setName("jLabel74"); // NOI18N
        jLabel74.setBounds(340, 440, 90, 20);
        jLayeredPane4.add(jLabel74, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField20.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField20.setName("jTextField20"); // NOI18N
        jTextField20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField20MousePressed(evt);
            }
        });
        jTextField20.setBounds(430, 110, 210, 22);
        jLayeredPane4.add(jTextField20, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setBounds(340, 50, 80, 20);
        jLayeredPane4.add(jLabel17, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField28.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField28.setName("jTextField28"); // NOI18N
        jTextField28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField28MousePressed(evt);
            }
        });
        jTextField28.setBounds(430, 230, 210, 22);
        jLayeredPane4.add(jTextField28, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N
        jLabel35.setBounds(340, 320, 90, 20);
        jLayeredPane4.add(jLabel35, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField32.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField32.setName("jTextField32"); // NOI18N
        jTextField32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField32MousePressed(evt);
            }
        });
        jTextField32.setBounds(430, 290, 210, 22);
        jLayeredPane4.add(jTextField32, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField100.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField100.setName("jTextField100"); // NOI18N
        jTextField100.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField100MousePressed(evt);
            }
        });
        jTextField100.setBounds(430, 410, 210, 22);
        jLayeredPane4.add(jTextField100, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setBounds(120, 10, 410, 16);
        jLayeredPane4.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setBounds(20, 80, 80, 20);
        jLayeredPane4.add(jLabel18, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N
        jLabel33.setBounds(340, 290, 80, 20);
        jLayeredPane4.add(jLabel33, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField19.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField19.setName("jTextField19"); // NOI18N
        jTextField19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField19MousePressed(evt);
            }
        });
        jTextField19.setBounds(100, 110, 210, 22);
        jLayeredPane4.add(jTextField19, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField21.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField21.setName("jTextField21"); // NOI18N
        jTextField21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField21MousePressed(evt);
            }
        });
        jTextField21.setBounds(100, 140, 210, 22);
        jLayeredPane4.add(jTextField21, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N
        jLabel28.setBounds(20, 230, 80, 20);
        jLayeredPane4.add(jLabel28, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N
        jLabel29.setBounds(340, 230, 80, 20);
        jLayeredPane4.add(jLabel29, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N
        jLabel26.setBounds(20, 200, 80, 20);
        jLayeredPane4.add(jLabel26, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N
        jLabel36.setBounds(340, 350, 80, 20);
        jLayeredPane4.add(jLabel36, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setBounds(20, 110, 80, 20);
        jLayeredPane4.add(jLabel20, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel41.setText(resourceMap.getString("jLabel41.text")); // NOI18N
        jLabel41.setName("jLabel41"); // NOI18N
        jLabel41.setBounds(20, 410, 80, 20);
        jLayeredPane4.add(jLabel41, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField16.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField16.setName("jTextField16"); // NOI18N
        jTextField16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField16MousePressed(evt);
            }
        });
        jTextField16.setBounds(430, 50, 210, 22);
        jLayeredPane4.add(jTextField16, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField34.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField34.setName("jTextField34"); // NOI18N
        jTextField34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField34MousePressed(evt);
            }
        });
        jTextField34.setBounds(430, 320, 210, 22);
        jLayeredPane4.add(jTextField34, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField33.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField33.setName("jTextField33"); // NOI18N
        jTextField33.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField33MousePressed(evt);
            }
        });
        jTextField33.setBounds(100, 320, 210, 22);
        jLayeredPane4.add(jTextField33, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField27.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField27.setName("jTextField27"); // NOI18N
        jTextField27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField27MousePressed(evt);
            }
        });
        jTextField27.setBounds(100, 230, 210, 22);
        jLayeredPane4.add(jTextField27, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField35.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField35.setName("jTextField35"); // NOI18N
        jTextField35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField35MousePressed(evt);
            }
        });
        jTextField35.setBounds(100, 350, 210, 22);
        jLayeredPane4.add(jTextField35, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField36.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField36.setName("jTextField36"); // NOI18N
        jTextField36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField36MousePressed(evt);
            }
        });
        jTextField36.setBounds(430, 350, 210, 22);
        jLayeredPane4.add(jTextField36, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N
        jLabel27.setBounds(340, 200, 80, 20);
        jLayeredPane4.add(jLabel27, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField39.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField39.setName("jTextField39"); // NOI18N
        jTextField39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField39MousePressed(evt);
            }
        });
        jTextField39.setBounds(100, 410, 210, 22);
        jLayeredPane4.add(jTextField39, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N
        jLabel30.setBounds(20, 260, 80, 20);
        jLayeredPane4.add(jLabel30, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N
        jLabel31.setBounds(340, 260, 80, 20);
        jLayeredPane4.add(jLabel31, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField31.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField31.setName("jTextField31"); // NOI18N
        jTextField31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField31MousePressed(evt);
            }
        });
        jTextField31.setBounds(100, 290, 210, 22);
        jLayeredPane4.add(jTextField31, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField23.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField23.setName("jTextField23"); // NOI18N
        jTextField23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField23MousePressed(evt);
            }
        });
        jTextField23.setBounds(100, 170, 210, 22);
        jLayeredPane4.add(jTextField23, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField40.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField40.setName("jTextField40"); // NOI18N
        jTextField40.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField40MousePressed(evt);
            }
        });
        jTextField40.setBounds(100, 440, 210, 22);
        jLayeredPane4.add(jTextField40, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setBounds(20, 140, 80, 20);
        jLayeredPane4.add(jLabel22, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField18.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField18.setName("jTextField18"); // NOI18N
        jTextField18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField18MousePressed(evt);
            }
        });
        jTextField18.setBounds(430, 80, 210, 22);
        jLayeredPane4.add(jTextField18, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField26.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField26.setName("jTextField26"); // NOI18N
        jTextField26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField26MousePressed(evt);
            }
        });
        jTextField26.setBounds(430, 200, 210, 22);
        jLayeredPane4.add(jTextField26, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField2MousePressed(evt);
            }
        });
        jTextField2.setBounds(100, 50, 210, 22);
        jLayeredPane4.add(jTextField2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel104.setText(resourceMap.getString("jLabel104.text")); // NOI18N
        jLabel104.setName("jLabel104"); // NOI18N
        jLabel104.setBounds(340, 410, 80, 20);
        jLayeredPane4.add(jLabel104, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField30.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField30.setName("jTextField30"); // NOI18N
        jTextField30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField30MousePressed(evt);
            }
        });
        jTextField30.setBounds(430, 260, 210, 22);
        jLayeredPane4.add(jTextField30, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField25.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField25.setName("jTextField25"); // NOI18N
        jTextField25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField25MousePressed(evt);
            }
        });
        jTextField25.setBounds(100, 200, 210, 22);
        jLayeredPane4.add(jTextField25, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setBounds(340, 110, 80, 20);
        jLayeredPane4.add(jLabel21, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField22.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField22.setName("jTextField22"); // NOI18N
        jTextField22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField22MousePressed(evt);
            }
        });
        jTextField22.setBounds(430, 140, 210, 22);
        jLayeredPane4.add(jTextField22, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N
        jLabel32.setBounds(20, 290, 80, 20);
        jLayeredPane4.add(jLabel32, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField37.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField37.setName("jTextField37"); // NOI18N
        jTextField37.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField37MousePressed(evt);
            }
        });
        jTextField37.setBounds(100, 380, 210, 22);
        jLayeredPane4.add(jTextField37, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N
        jLabel34.setBounds(20, 320, 80, 20);
        jLayeredPane4.add(jLabel34, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab(resourceMap.getString("jLayeredPane4.TabConstraints.tabTitle"), jLayeredPane4); // NOI18N

        jLayeredPane5.setName("jLayeredPane5"); // NOI18N

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText(resourceMap.getString("jLabel47.text")); // NOI18N
        jLabel47.setName("jLabel47"); // NOI18N
        jLabel47.setBounds(340, 60, 80, 20);
        jLayeredPane5.add(jLabel47, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField53.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField53.setName("jTextField53"); // NOI18N
        jTextField53.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField53MousePressed(evt);
            }
        });
        jTextField53.setBounds(100, 180, 210, 22);
        jLayeredPane5.add(jTextField53, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel57.setText(resourceMap.getString("jLabel57.text")); // NOI18N
        jLabel57.setName("jLabel57"); // NOI18N
        jLabel57.setBounds(20, 270, 80, 20);
        jLayeredPane5.add(jLabel57, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField50.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField50.setName("jTextField50"); // NOI18N
        jTextField50.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField50MousePressed(evt);
            }
        });
        jTextField50.setBounds(430, 120, 210, 22);
        jLayeredPane5.add(jTextField50, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField44.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField44.setName("jTextField44"); // NOI18N
        jTextField44.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField44MousePressed(evt);
            }
        });
        jTextField44.setBounds(100, 30, 210, 22);
        jLayeredPane5.add(jTextField44, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField49.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField49.setName("jTextField49"); // NOI18N
        jTextField49.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField49MousePressed(evt);
            }
        });
        jTextField49.setBounds(100, 120, 210, 22);
        jLayeredPane5.add(jTextField49, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField42.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField42.setName("jTextField42"); // NOI18N
        jTextField42.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField42MousePressed(evt);
            }
        });
        jTextField42.setBounds(430, 0, 210, 22);
        jLayeredPane5.add(jTextField42, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setText(resourceMap.getString("jLabel59.text")); // NOI18N
        jLabel59.setName("jLabel59"); // NOI18N
        jLabel59.setBounds(20, 330, 80, 20);
        jLayeredPane5.add(jLabel59, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel72.setText(resourceMap.getString("jLabel72.text")); // NOI18N
        jLabel72.setName("jLabel72"); // NOI18N
        jLabel72.setBounds(20, 450, 80, 20);
        jLayeredPane5.add(jLabel72, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText(resourceMap.getString("jLabel46.text")); // NOI18N
        jLabel46.setName("jLabel46"); // NOI18N
        jLabel46.setBounds(20, 60, 80, 20);
        jLayeredPane5.add(jLabel46, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField67.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField67.setName("jTextField67"); // NOI18N
        jTextField67.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField67MousePressed(evt);
            }
        });
        jTextField67.setBounds(100, 390, 210, 22);
        jLayeredPane5.add(jTextField67, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText(resourceMap.getString("jLabel55.text")); // NOI18N
        jLabel55.setName("jLabel55"); // NOI18N
        jLabel55.setBounds(20, 210, 80, 20);
        jLayeredPane5.add(jLabel55, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField48.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField48.setName("jTextField48"); // NOI18N
        jTextField48.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField48MousePressed(evt);
            }
        });
        jTextField48.setBounds(100, 90, 210, 22);
        jLayeredPane5.add(jTextField48, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel68.setText(resourceMap.getString("jLabel68.text")); // NOI18N
        jLabel68.setName("jLabel68"); // NOI18N
        jLabel68.setBounds(20, 390, 80, 20);
        jLayeredPane5.add(jLabel68, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel67.setText(resourceMap.getString("jLabel67.text")); // NOI18N
        jLabel67.setName("jLabel67"); // NOI18N
        jLabel67.setBounds(340, 360, 80, 20);
        jLayeredPane5.add(jLabel67, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText(resourceMap.getString("jLabel51.text")); // NOI18N
        jLabel51.setName("jLabel51"); // NOI18N
        jLabel51.setBounds(340, 120, 80, 20);
        jLayeredPane5.add(jLabel51, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setText(resourceMap.getString("jLabel71.text")); // NOI18N
        jLabel71.setName("jLabel71"); // NOI18N
        jLabel71.setBounds(340, 420, 80, 20);
        jLayeredPane5.add(jLabel71, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField52.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField52.setName("jTextField52"); // NOI18N
        jTextField52.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField52MousePressed(evt);
            }
        });
        jTextField52.setBounds(100, 150, 210, 22);
        jLayeredPane5.add(jTextField52, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField70.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField70.setName("jTextField70"); // NOI18N
        jTextField70.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField70MousePressed(evt);
            }
        });
        jTextField70.setBounds(430, 420, 210, 22);
        jLayeredPane5.add(jTextField70, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setText(resourceMap.getString("jLabel58.text")); // NOI18N
        jLabel58.setName("jLabel58"); // NOI18N
        jLabel58.setBounds(20, 300, 80, 20);
        jLayeredPane5.add(jLabel58, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel66.setText(resourceMap.getString("jLabel66.text")); // NOI18N
        jLabel66.setName("jLabel66"); // NOI18N
        jLabel66.setBounds(340, 330, 80, 20);
        jLayeredPane5.add(jLabel66, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText(resourceMap.getString("jLabel49.text")); // NOI18N
        jLabel49.setName("jLabel49"); // NOI18N
        jLabel49.setBounds(20, 90, 80, 20);
        jLayeredPane5.add(jLabel49, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField69.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField69.setName("jTextField69"); // NOI18N
        jTextField69.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField69MousePressed(evt);
            }
        });
        jTextField69.setBounds(100, 420, 210, 22);
        jLayeredPane5.add(jTextField69, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel65.setText(resourceMap.getString("jLabel65.text")); // NOI18N
        jLabel65.setName("jLabel65"); // NOI18N
        jLabel65.setBounds(340, 300, 80, 20);
        jLayeredPane5.add(jLabel65, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField41.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField41.setName("jTextField41"); // NOI18N
        jTextField41.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField41MousePressed(evt);
            }
        });
        jTextField41.setBounds(100, 0, 210, 22);
        jLayeredPane5.add(jTextField41, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField51.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField51.setName("jTextField51"); // NOI18N
        jTextField51.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField51MousePressed(evt);
            }
        });
        jTextField51.setBounds(430, 150, 210, 22);
        jLayeredPane5.add(jTextField51, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField62.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField62.setName("jTextField62"); // NOI18N
        jTextField62.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField62MousePressed(evt);
            }
        });
        jTextField62.setBounds(430, 240, 210, 22);
        jLayeredPane5.add(jTextField62, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField45.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField45.setName("jTextField45"); // NOI18N
        jTextField45.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField45MousePressed(evt);
            }
        });
        jTextField45.setBounds(100, 60, 210, 22);
        jLayeredPane5.add(jTextField45, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField63.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField63.setName("jTextField63"); // NOI18N
        jTextField63.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField63MousePressed(evt);
            }
        });
        jTextField63.setBounds(430, 270, 210, 22);
        jLayeredPane5.add(jTextField63, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField65.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField65.setName("jTextField65"); // NOI18N
        jTextField65.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField65MousePressed(evt);
            }
        });
        jTextField65.setBounds(430, 330, 210, 22);
        jLayeredPane5.add(jTextField65, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel52.setText(resourceMap.getString("jLabel52.text")); // NOI18N
        jLabel52.setName("jLabel52"); // NOI18N
        jLabel52.setBounds(340, 150, 80, 20);
        jLayeredPane5.add(jLabel52, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField60.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField60.setName("jTextField60"); // NOI18N
        jTextField60.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField60MousePressed(evt);
            }
        });
        jTextField60.setBounds(430, 180, 210, 22);
        jLayeredPane5.add(jTextField60, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField54.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField54.setName("jTextField54"); // NOI18N
        jTextField54.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField54MousePressed(evt);
            }
        });
        jTextField54.setBounds(100, 210, 210, 22);
        jLayeredPane5.add(jTextField54, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N
        jLabel53.setBounds(20, 150, 80, 20);
        jLayeredPane5.add(jLabel53, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText(resourceMap.getString("jLabel42.text")); // NOI18N
        jLabel42.setName("jLabel42"); // NOI18N
        jLabel42.setBounds(20, 0, 80, 20);
        jLayeredPane5.add(jLabel42, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField58.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField58.setName("jTextField58"); // NOI18N
        jTextField58.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField58MousePressed(evt);
            }
        });
        jTextField58.setBounds(100, 330, 210, 22);
        jLayeredPane5.add(jTextField58, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel60.setText(resourceMap.getString("jLabel60.text")); // NOI18N
        jLabel60.setName("jLabel60"); // NOI18N
        jLabel60.setBounds(20, 360, 80, 20);
        jLayeredPane5.add(jLabel60, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel64.setText(resourceMap.getString("jLabel64.text")); // NOI18N
        jLabel64.setName("jLabel64"); // NOI18N
        jLabel64.setBounds(340, 270, 80, 20);
        jLayeredPane5.add(jLabel64, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText(resourceMap.getString("jLabel48.text")); // NOI18N
        jLabel48.setName("jLabel48"); // NOI18N
        jLabel48.setBounds(340, 90, 80, 20);
        jLayeredPane5.add(jLabel48, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField68.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField68.setName("jTextField68"); // NOI18N
        jTextField68.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField68MousePressed(evt);
            }
        });
        jTextField68.setBounds(430, 390, 210, 22);
        jLayeredPane5.add(jTextField68, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField64.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField64.setName("jTextField64"); // NOI18N
        jTextField64.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField64MousePressed(evt);
            }
        });
        jTextField64.setBounds(430, 300, 210, 22);
        jLayeredPane5.add(jTextField64, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText(resourceMap.getString("jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N
        jLabel44.setBounds(340, 30, 80, 20);
        jLayeredPane5.add(jLabel44, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText(resourceMap.getString("jLabel56.text")); // NOI18N
        jLabel56.setName("jLabel56"); // NOI18N
        jLabel56.setBounds(20, 240, 80, 20);
        jLayeredPane5.add(jLabel56, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText(resourceMap.getString("jLabel50.text")); // NOI18N
        jLabel50.setName("jLabel50"); // NOI18N
        jLabel50.setBounds(20, 120, 80, 20);
        jLayeredPane5.add(jLabel50, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setText(resourceMap.getString("jLabel61.text")); // NOI18N
        jLabel61.setName("jLabel61"); // NOI18N
        jLabel61.setBounds(340, 180, 80, 20);
        jLayeredPane5.add(jLabel61, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText(resourceMap.getString("jLabel43.text")); // NOI18N
        jLabel43.setName("jLabel43"); // NOI18N
        jLabel43.setBounds(340, 0, 80, 20);
        jLayeredPane5.add(jLabel43, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField66.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField66.setName("jTextField66"); // NOI18N
        jTextField66.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField66MousePressed(evt);
            }
        });
        jTextField66.setBounds(430, 360, 210, 22);
        jLayeredPane5.add(jTextField66, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField61.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField61.setName("jTextField61"); // NOI18N
        jTextField61.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField61MousePressed(evt);
            }
        });
        jTextField61.setBounds(430, 210, 210, 22);
        jLayeredPane5.add(jTextField61, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField56.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField56.setName("jTextField56"); // NOI18N
        jTextField56.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField56MousePressed(evt);
            }
        });
        jTextField56.setBounds(100, 270, 210, 22);
        jLayeredPane5.add(jTextField56, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField47.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField47.setName("jTextField47"); // NOI18N
        jTextField47.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField47MousePressed(evt);
            }
        });
        jTextField47.setBounds(430, 90, 210, 22);
        jLayeredPane5.add(jTextField47, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setText(resourceMap.getString("jLabel54.text")); // NOI18N
        jLabel54.setName("jLabel54"); // NOI18N
        jLabel54.setBounds(20, 180, 80, 20);
        jLayeredPane5.add(jLabel54, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel62.setText(resourceMap.getString("jLabel62.text")); // NOI18N
        jLabel62.setName("jLabel62"); // NOI18N
        jLabel62.setBounds(340, 210, 80, 20);
        jLayeredPane5.add(jLabel62, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField55.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField55.setName("jTextField55"); // NOI18N
        jTextField55.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField55MousePressed(evt);
            }
        });
        jTextField55.setBounds(100, 240, 210, 22);
        jLayeredPane5.add(jTextField55, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText(resourceMap.getString("jLabel45.text")); // NOI18N
        jLabel45.setName("jLabel45"); // NOI18N
        jLabel45.setBounds(20, 30, 80, 20);
        jLayeredPane5.add(jLabel45, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel69.setText(resourceMap.getString("jLabel69.text")); // NOI18N
        jLabel69.setName("jLabel69"); // NOI18N
        jLabel69.setBounds(340, 390, 80, 20);
        jLayeredPane5.add(jLabel69, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField43.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField43.setName("jTextField43"); // NOI18N
        jTextField43.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField43MousePressed(evt);
            }
        });
        jTextField43.setBounds(430, 30, 210, 22);
        jLayeredPane5.add(jTextField43, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel63.setText(resourceMap.getString("jLabel63.text")); // NOI18N
        jLabel63.setName("jLabel63"); // NOI18N
        jLabel63.setBounds(340, 240, 80, 20);
        jLayeredPane5.add(jLabel63, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField57.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField57.setName("jTextField57"); // NOI18N
        jTextField57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField57MousePressed(evt);
            }
        });
        jTextField57.setBounds(100, 300, 210, 22);
        jLayeredPane5.add(jTextField57, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField59.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField59.setName("jTextField59"); // NOI18N
        jTextField59.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField59MousePressed(evt);
            }
        });
        jTextField59.setBounds(100, 360, 210, 22);
        jLayeredPane5.add(jTextField59, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel70.setText(resourceMap.getString("jLabel70.text")); // NOI18N
        jLabel70.setName("jLabel70"); // NOI18N
        jLabel70.setBounds(20, 420, 80, 20);
        jLayeredPane5.add(jLabel70, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField71.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField71.setName("jTextField71"); // NOI18N
        jTextField71.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField71MousePressed(evt);
            }
        });
        jTextField71.setBounds(100, 450, 210, 22);
        jLayeredPane5.add(jTextField71, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField46.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField46.setName("jTextField46"); // NOI18N
        jTextField46.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField46MousePressed(evt);
            }
        });
        jTextField46.setBounds(430, 60, 210, 22);
        jLayeredPane5.add(jTextField46, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab(resourceMap.getString("jLayeredPane5.TabConstraints.tabTitle"), jLayeredPane5); // NOI18N

        jLayeredPane6.setName("jLayeredPane6"); // NOI18N

        jTextField91.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField91.setName("jTextField91"); // NOI18N
        jTextField91.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField91MousePressed(evt);
            }
        });
        jTextField91.setBounds(430, 220, 210, 22);
        jLayeredPane6.add(jTextField91, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField96.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField96.setName("jTextField96"); // NOI18N
        jTextField96.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField96MousePressed(evt);
            }
        });
        jTextField96.setBounds(430, 70, 210, 22);
        jLayeredPane6.add(jTextField96, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField76.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField76.setName("jTextField76"); // NOI18N
        jTextField76.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField76MousePressed(evt);
            }
        });
        jTextField76.setBounds(110, 100, 210, 22);
        jLayeredPane6.add(jTextField76, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel94.setText(resourceMap.getString("jLabel94.text")); // NOI18N
        jLabel94.setName("jLabel94"); // NOI18N
        jLabel94.setBounds(340, 250, 80, 20);
        jLayeredPane6.add(jLabel94, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel80.setText(resourceMap.getString("jLabel80.text")); // NOI18N
        jLabel80.setName("jLabel80"); // NOI18N
        jLabel80.setBounds(20, 220, 80, 20);
        jLayeredPane6.add(jLabel80, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField81.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField81.setName("jTextField81"); // NOI18N
        jTextField81.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField81MousePressed(evt);
            }
        });
        jTextField81.setBounds(110, 250, 210, 22);
        jLayeredPane6.add(jTextField81, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel84.setText(resourceMap.getString("jLabel84.text")); // NOI18N
        jLabel84.setName("jLabel84"); // NOI18N
        jLabel84.setBounds(20, 100, 90, 20);
        jLayeredPane6.add(jLabel84, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel85.setText(resourceMap.getString("jLabel85.text")); // NOI18N
        jLabel85.setName("jLabel85"); // NOI18N
        jLabel85.setBounds(20, 70, 80, 20);
        jLayeredPane6.add(jLabel85, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField74.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField74.setName("jTextField74"); // NOI18N
        jTextField74.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField74MousePressed(evt);
            }
        });
        jTextField74.setBounds(110, 40, 210, 22);
        jLayeredPane6.add(jTextField74, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField78.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField78.setName("jTextField78"); // NOI18N
        jTextField78.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField78MousePressed(evt);
            }
        });
        jTextField78.setBounds(110, 160, 210, 22);
        jLayeredPane6.add(jTextField78, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField79.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField79.setName("jTextField79"); // NOI18N
        jTextField79.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField79MousePressed(evt);
            }
        });
        jTextField79.setBounds(110, 190, 210, 22);
        jLayeredPane6.add(jTextField79, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel92.setText(resourceMap.getString("jLabel92.text")); // NOI18N
        jLabel92.setName("jLabel92"); // NOI18N
        jLabel92.setBounds(340, 190, 80, 20);
        jLayeredPane6.add(jLabel92, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField94.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField94.setName("jTextField94"); // NOI18N
        jTextField94.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField94MousePressed(evt);
            }
        });
        jTextField94.setBounds(430, 130, 210, 22);
        jLayeredPane6.add(jTextField94, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel90.setText(resourceMap.getString("jLabel90.text")); // NOI18N
        jLabel90.setName("jLabel90"); // NOI18N
        jLabel90.setBounds(340, 130, 80, 20);
        jLayeredPane6.add(jLabel90, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField83.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField83.setName("jTextField83"); // NOI18N
        jTextField83.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField83MousePressed(evt);
            }
        });
        jTextField83.setBounds(110, 310, 210, 22);
        jLayeredPane6.add(jTextField83, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField75.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField75.setName("jTextField75"); // NOI18N
        jTextField75.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField75MousePressed(evt);
            }
        });
        jTextField75.setBounds(110, 70, 210, 22);
        jLayeredPane6.add(jTextField75, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel87.setText(resourceMap.getString("jLabel87.text")); // NOI18N
        jLabel87.setName("jLabel87"); // NOI18N
        jLabel87.setBounds(340, 40, 80, 20);
        jLayeredPane6.add(jLabel87, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel82.setText(resourceMap.getString("jLabel82.text")); // NOI18N
        jLabel82.setName("jLabel82"); // NOI18N
        jLabel82.setBounds(20, 160, 80, 20);
        jLayeredPane6.add(jLabel82, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel95.setText(resourceMap.getString("jLabel95.text")); // NOI18N
        jLabel95.setName("jLabel95"); // NOI18N
        jLabel95.setBounds(340, 280, 80, 20);
        jLayeredPane6.add(jLabel95, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField93.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField93.setName("jTextField93"); // NOI18N
        jTextField93.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField93MousePressed(evt);
            }
        });
        jTextField93.setBounds(430, 160, 210, 22);
        jLayeredPane6.add(jTextField93, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel78.setText(resourceMap.getString("jLabel78.text")); // NOI18N
        jLabel78.setName("jLabel78"); // NOI18N
        jLabel78.setBounds(20, 280, 80, 20);
        jLayeredPane6.add(jLabel78, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField97.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField97.setName("jTextField97"); // NOI18N
        jTextField97.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField97MousePressed(evt);
            }
        });
        jTextField97.setBounds(430, 40, 210, 22);
        jLayeredPane6.add(jTextField97, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField82.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField82.setName("jTextField82"); // NOI18N
        jTextField82.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField82MousePressed(evt);
            }
        });
        jTextField82.setBounds(110, 280, 210, 22);
        jLayeredPane6.add(jTextField82, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField92.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField92.setName("jTextField92"); // NOI18N
        jTextField92.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField92MousePressed(evt);
            }
        });
        jTextField92.setBounds(430, 190, 210, 22);
        jLayeredPane6.add(jTextField92, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel83.setText(resourceMap.getString("jLabel83.text")); // NOI18N
        jLabel83.setName("jLabel83"); // NOI18N
        jLabel83.setBounds(20, 130, 90, 20);
        jLayeredPane6.add(jLabel83, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField95.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField95.setName("jTextField95"); // NOI18N
        jTextField95.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField95MousePressed(evt);
            }
        });
        jTextField95.setBounds(430, 100, 210, 22);
        jLayeredPane6.add(jTextField95, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField89.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField89.setName("jTextField89"); // NOI18N
        jTextField89.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField89MousePressed(evt);
            }
        });
        jTextField89.setBounds(430, 280, 210, 22);
        jLayeredPane6.add(jTextField89, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel79.setText(resourceMap.getString("jLabel79.text")); // NOI18N
        jLabel79.setName("jLabel79"); // NOI18N
        jLabel79.setBounds(20, 250, 80, 20);
        jLayeredPane6.add(jLabel79, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel91.setText(resourceMap.getString("jLabel91.text")); // NOI18N
        jLabel91.setName("jLabel91"); // NOI18N
        jLabel91.setBounds(340, 160, 80, 20);
        jLayeredPane6.add(jLabel91, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel86.setText(resourceMap.getString("jLabel86.text")); // NOI18N
        jLabel86.setName("jLabel86"); // NOI18N
        jLabel86.setBounds(20, 40, 80, 20);
        jLayeredPane6.add(jLabel86, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel89.setText(resourceMap.getString("jLabel89.text")); // NOI18N
        jLabel89.setName("jLabel89"); // NOI18N
        jLabel89.setBounds(340, 100, 80, 20);
        jLayeredPane6.add(jLabel89, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField90.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField90.setName("jTextField90"); // NOI18N
        jTextField90.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField90MousePressed(evt);
            }
        });
        jTextField90.setBounds(430, 250, 210, 22);
        jLayeredPane6.add(jTextField90, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField80.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField80.setName("jTextField80"); // NOI18N
        jTextField80.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField80MousePressed(evt);
            }
        });
        jTextField80.setBounds(110, 220, 210, 22);
        jLayeredPane6.add(jTextField80, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel81.setText(resourceMap.getString("jLabel81.text")); // NOI18N
        jLabel81.setName("jLabel81"); // NOI18N
        jLabel81.setBounds(20, 190, 80, 20);
        jLayeredPane6.add(jLabel81, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel96.setText(resourceMap.getString("jLabel96.text")); // NOI18N
        jLabel96.setName("jLabel96"); // NOI18N
        jLabel96.setBounds(340, 310, 90, 20);
        jLayeredPane6.add(jLabel96, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel77.setText(resourceMap.getString("jLabel77.text")); // NOI18N
        jLabel77.setName("jLabel77"); // NOI18N
        jLabel77.setBounds(20, 310, 90, 20);
        jLayeredPane6.add(jLabel77, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel93.setText(resourceMap.getString("jLabel93.text")); // NOI18N
        jLabel93.setName("jLabel93"); // NOI18N
        jLabel93.setBounds(340, 220, 80, 20);
        jLayeredPane6.add(jLabel93, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField77.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField77.setName("jTextField77"); // NOI18N
        jTextField77.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField77MousePressed(evt);
            }
        });
        jTextField77.setBounds(110, 130, 210, 22);
        jLayeredPane6.add(jTextField77, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField84.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField84.setName("jTextField84"); // NOI18N
        jTextField84.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField84MousePressed(evt);
            }
        });
        jTextField84.setBounds(110, 340, 210, 22);
        jLayeredPane6.add(jTextField84, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField88.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField88.setName("jTextField88"); // NOI18N
        jTextField88.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField88MousePressed(evt);
            }
        });
        jTextField88.setBounds(430, 310, 210, 22);
        jLayeredPane6.add(jTextField88, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel76.setText(resourceMap.getString("jLabel76.text")); // NOI18N
        jLabel76.setName("jLabel76"); // NOI18N
        jLabel76.setBounds(20, 340, 80, 20);
        jLayeredPane6.add(jLabel76, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel88.setText(resourceMap.getString("jLabel88.text")); // NOI18N
        jLabel88.setName("jLabel88"); // NOI18N
        jLabel88.setBounds(340, 70, 80, 20);
        jLayeredPane6.add(jLabel88, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab(resourceMap.getString("jLayeredPane6.TabConstraints.tabTitle"), jLayeredPane6); // NOI18N

        jLayeredPane9.setName("jLayeredPane9"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setToolTipText(resourceMap.getString("jLabel9.toolTipText")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setBounds(20, 200, 190, 20);
        jLayeredPane9.add(jLabel9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField7.setName("jTextField7"); // NOI18N
        jTextField7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField7MousePressed(evt);
            }
        });
        jTextField7.setBounds(210, 120, 430, 22);
        jLayeredPane9.add(jTextField7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField11.setName("jTextField11"); // NOI18N
        jTextField11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField11MousePressed(evt);
            }
        });
        jTextField11.setBounds(210, 400, 430, 22);
        jLayeredPane9.add(jTextField11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel98.setText(resourceMap.getString("jLabel98.text")); // NOI18N
        jLabel98.setToolTipText(resourceMap.getString("jLabel98.toolTipText")); // NOI18N
        jLabel98.setName("jLabel98"); // NOI18N
        jLabel98.setBounds(20, 320, 180, 20);
        jLayeredPane9.add(jLabel98, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField10.setName("jTextField10"); // NOI18N
        jTextField10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField10MousePressed(evt);
            }
        });
        jTextField10.setBounds(210, 360, 430, 22);
        jLayeredPane9.add(jTextField10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField99.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField99.setName("jTextField99"); // NOI18N
        jTextField99.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField99MousePressed(evt);
            }
        });
        jTextField99.setBounds(210, 280, 430, 22);
        jLayeredPane9.add(jTextField99, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField5.setName("jTextField5"); // NOI18N
        jTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField5MousePressed(evt);
            }
        });
        jTextField5.setBounds(210, 40, 430, 22);
        jLayeredPane9.add(jTextField5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setName("jTextField8"); // NOI18N
        jTextField8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField8MousePressed(evt);
            }
        });
        jTextField8.setBounds(210, 160, 430, 22);
        jLayeredPane9.add(jTextField8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setToolTipText(resourceMap.getString("jLabel8.toolTipText")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setBounds(20, 160, 180, 20);
        jLayeredPane9.add(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setToolTipText(resourceMap.getString("jLabel6.toolTipText")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setBounds(20, 80, 180, 20);
        jLayeredPane9.add(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setToolTipText(resourceMap.getString("jLabel11.toolTipText")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setBounds(20, 400, 180, 20);
        jLayeredPane9.add(jLabel11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setToolTipText(resourceMap.getString("jLabel10.toolTipText")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setBounds(20, 360, 180, 20);
        jLayeredPane9.add(jLabel10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel103.setText(resourceMap.getString("jLabel103.text")); // NOI18N
        jLabel103.setToolTipText(resourceMap.getString("jLabel103.toolTipText")); // NOI18N
        jLabel103.setName("jLabel103"); // NOI18N
        jLabel103.setBounds(20, 280, 180, 20);
        jLayeredPane9.add(jLabel103, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel102.setText(resourceMap.getString("jLabel102.text")); // NOI18N
        jLabel102.setToolTipText(resourceMap.getString("jLabel102.toolTipText")); // NOI18N
        jLabel102.setName("jLabel102"); // NOI18N
        jLabel102.setBounds(20, 240, 180, 20);
        jLayeredPane9.add(jLabel102, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField87.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField87.setName("jTextField87"); // NOI18N
        jTextField87.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField87MousePressed(evt);
            }
        });
        jTextField87.setBounds(210, 320, 430, 22);
        jLayeredPane9.add(jTextField87, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField98.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField98.setName("jTextField98"); // NOI18N
        jTextField98.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField98MousePressed(evt);
            }
        });
        jTextField98.setBounds(210, 240, 430, 22);
        jLayeredPane9.add(jTextField98, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setToolTipText(resourceMap.getString("jLabel7.toolTipText")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setBounds(20, 120, 180, 20);
        jLayeredPane9.add(jLabel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setToolTipText(resourceMap.getString("jLabel5.toolTipText")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setBounds(20, 40, 190, 20);
        jLayeredPane9.add(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField9.setName("jTextField9"); // NOI18N
        jTextField9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField9MousePressed(evt);
            }
        });
        jTextField9.setBounds(210, 200, 430, 22);
        jLayeredPane9.add(jTextField9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField6.setName("jTextField6"); // NOI18N
        jTextField6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField6MousePressed(evt);
            }
        });
        jTextField6.setBounds(210, 80, 430, 22);
        jLayeredPane9.add(jTextField6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab(resourceMap.getString("jLayeredPane9.TabConstraints.tabTitle"), jLayeredPane9); // NOI18N

        jLayeredPane10.setName("jLayeredPane10"); // NOI18N

        jLabel100.setFont(resourceMap.getFont("jLabel100.font")); // NOI18N
        jLabel100.setText(resourceMap.getString("jLabel100.text")); // NOI18N
        jLabel100.setName("jLabel100"); // NOI18N
        jLabel100.setBounds(310, 260, 70, 20);
        jLayeredPane10.add(jLabel100, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel97.setText(resourceMap.getString("jLabel97.text")); // NOI18N
        jLabel97.setName("jLabel97"); // NOI18N
        jLabel97.setBounds(60, 130, 200, 20);
        jLayeredPane10.add(jLabel97, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel101.setFont(resourceMap.getFont("jLabel101.font")); // NOI18N
        jLabel101.setText(resourceMap.getString("jLabel101.text")); // NOI18N
        jLabel101.setName("jLabel101"); // NOI18N
        jLabel101.setBounds(380, 260, 90, 20);
        jLayeredPane10.add(jLabel101, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);

        jSlider1.setPaintTicks(true);
        jSlider1.setToolTipText(resourceMap.getString("jSlider1.toolTipText")); // NOI18N
        jSlider1.setValue(100);
        jSlider1.setName("jSlider1"); // NOI18N
        jSlider1.setOpaque(false);
        jSlider1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSlider1PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(13, 13, 13))
        );

        jPanel1.setBounds(260, 220, 270, 40);
        jLayeredPane10.add(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField86.setName("jTextField86"); // NOI18N
        jTextField86.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField86MousePressed(evt);
            }
        });
        jTextField86.setBounds(260, 130, 170, 22);
        jLayeredPane10.add(jTextField86, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel75.setText(resourceMap.getString("jLabel75.text")); // NOI18N
        jLabel75.setName("jLabel75"); // NOI18N
        jLabel75.setBounds(60, 50, 200, 20);
        jLayeredPane10.add(jLabel75, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setIconTextGap(0);
        jButton2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.setBounds(440, 120, 40, 40);
        jLayeredPane10.add(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel99.setText(resourceMap.getString("jLabel99.text")); // NOI18N
        jLabel99.setName("jLabel99"); // NOI18N
        jLabel99.setBounds(60, 220, 200, 20);
        jLayeredPane10.add(jLabel99, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTextField85.setText(resourceMap.getString("jTextField85.text")); // NOI18N
        jTextField85.setName("jTextField85"); // NOI18N
        jTextField85.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField85KeyTyped(evt);
            }
        });
        jTextField85.setBounds(260, 50, 170, 22);
        jLayeredPane10.add(jTextField85, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPane1.addTab(resourceMap.getString("jLayeredPane10.TabConstraints.tabTitle"), jLayeredPane10); // NOI18N

        jTabbedPane1.setBounds(10, 10, 660, 510);
        jLayeredPane2.add(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jButton3.setFont(resourceMap.getFont("jButton3.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.setBounds(300, 530, 79, 25);
        jLayeredPane2.add(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setBounds(10, 10, 680, 570);
        jLayeredPane1.add(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    }//GEN-LAST:event_formWindowClosed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        closeAddContent();
}//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MousePressed
        File f = loadFileChooser(new WPChooseFilter());
        if(f!=null) {
            jTextField1.setText(f.getPath());
            wallpaper = f;
        } else {
            jTextField1.setText("");
            wallpaper = null;
        }
    }//GEN-LAST:event_jTextField1MousePressed

    private void jTextField4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField4MousePressed
        setFile(jTextField4, "/usr/palm/sysmgr/images/palm-logo.png");
    }//GEN-LAST:event_jTextField4MousePressed

    private void jTextField3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField3MousePressed
        setFile(jTextField3, "/usr/palm/sysmgr/images/palm-logo-bright.png");
    }//GEN-LAST:event_jTextField3MousePressed

    private void jTextField13MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField13MousePressed
        setFile(jTextField13, "/usr/palm/applications/com.palm.app.musicplayer/images/background.png");
    }//GEN-LAST:event_jTextField13MousePressed

    private void jTextField14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField14MousePressed
        setFile(jTextField14, "/usr/palm/applications/com.palm.app.photos/images/background.png");
    }//GEN-LAST:event_jTextField14MousePressed

    private void jTextField72MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField72MousePressed
        setFile(jTextField72, "/usr/palm/applications/com.palm.app.phone/images/backdrop-phone.png");
    }//GEN-LAST:event_jTextField72MousePressed

    private void jTextField15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField15MousePressed
        setFile(jTextField15, "/usr/palm/applications/com.palm.app.videoplayer.launcher/images/background.png");
    }//GEN-LAST:event_jTextField15MousePressed

    private void jTextField12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField12MousePressed
        setFile(jTextField12, "/usr/palm/applications/com.palm.app.browser/images/background-bookmarks-grid.png");
    }//GEN-LAST:event_jTextField12MousePressed

    private void jTextField2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MousePressed
        setIcon(jTextField2, "com.palm.app.backup");
    }//GEN-LAST:event_jTextField2MousePressed

    private void jTextField16MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField16MousePressed
        setIcon(jTextField16, "com.palm.app.bluetooth");
    }//GEN-LAST:event_jTextField16MousePressed

    private void jTextField17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField17ActionPerformed
    }//GEN-LAST:event_jTextField17ActionPerformed

    private void jTextField17MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField17MousePressed
        setIcon(jTextField17, "com.palm.app.calculator");
    }//GEN-LAST:event_jTextField17MousePressed

    private void jTextField18MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField18MousePressed
        setIcon(jTextField18, "com.palm.app.calendar");
    }//GEN-LAST:event_jTextField18MousePressed

    private void jTextField19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField19MousePressed
        setIcon(jTextField19, "com.palm.app.camera");
    }//GEN-LAST:event_jTextField19MousePressed

    private void jTextField20MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField20MousePressed
        setIcon(jTextField20, "com.palm.app.clock");
    }//GEN-LAST:event_jTextField20MousePressed

    private void jTextField21MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField21MousePressed
        setIcon(jTextField21, "com.palm.app.contacts");
    }//GEN-LAST:event_jTextField21MousePressed

    private void jTextField22MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField22MousePressed
        setIcon(jTextField22, "com.palm.app.dateandtime");
    }//GEN-LAST:event_jTextField22MousePressed

    private void jTextField23MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField23MousePressed
        setIcon(jTextField23, "com.palm.app.deviceinfo");
    }//GEN-LAST:event_jTextField23MousePressed

    private void jTextField24MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField24MousePressed
        setIcon(jTextField24, "com.palm.app.devmodeswitcher");
    }//GEN-LAST:event_jTextField24MousePressed

    private void jTextField25MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField25MousePressed
        setIcon(jTextField25, "com.palm.app.docviewer");
    }//GEN-LAST:event_jTextField25MousePressed

    private void jTextField26MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField26MousePressed
        setIcon(jTextField26, "com.palm.app.email");
    }//GEN-LAST:event_jTextField26MousePressed

    private void jTextField27MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField27MousePressed
        setIcon(jTextField27, "com.palm.app.help");
    }//GEN-LAST:event_jTextField27MousePressed

    private void jTextField28MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField28MousePressed
        setIcon(jTextField28, "com.palm.app.maps");
    }//GEN-LAST:event_jTextField28MousePressed

    private void jTextField29MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField29MousePressed
        setIcon(jTextField29, "com.palm.app.messaging");
    }//GEN-LAST:event_jTextField29MousePressed

    private void jTextField30MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField30MousePressed
        setIcon(jTextField30, "com.palm.app.musicplayer");
    }//GEN-LAST:event_jTextField30MousePressed

    private void jTextField31MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField31MousePressed
        setIcon(jTextField31, "com.palm.app.notes");
    }//GEN-LAST:event_jTextField31MousePressed

    private void jTextField32MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField32MousePressed
        setIcon(jTextField32, "com.palm.app.phone");
    }//GEN-LAST:event_jTextField32MousePressed

    private void jTextField33MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField33MousePressed
        setIcon(jTextField33, "com.palm.app.photos");
    }//GEN-LAST:event_jTextField33MousePressed

    private void jTextField34MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField34MousePressed
        setIcon(jTextField34, "com.palm.app.screenlock");
    }//GEN-LAST:event_jTextField34MousePressed

    private void jTextField35MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField35MousePressed
        setIcon(jTextField35, "com.palm.app.soundsandalerts");
    }//GEN-LAST:event_jTextField35MousePressed

    private void jTextField36MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField36MousePressed
        setIcon(jTextField36, "com.palm.app.pdfviewer");
    }//GEN-LAST:event_jTextField36MousePressed

    private void jTextField37MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField37MousePressed
        setIcon(jTextField37, "com.palm.app.tasks");
    }//GEN-LAST:event_jTextField37MousePressed

    private void jTextField38MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField38MousePressed
        setIcon(jTextField38, "com.palm.app.updates");
    }//GEN-LAST:event_jTextField38MousePressed

    private void jTextField39MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField39MousePressed
        setIcon(jTextField39, "com.palm.app.videoplayer.launcher");
    }//GEN-LAST:event_jTextField39MousePressed

    private void jTextField40MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField40MousePressed
        setIcon(jTextField40, "com.palm.app.wifi");
    }//GEN-LAST:event_jTextField40MousePressed

    private void jTextField73MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField73MousePressed
        setIcon(jTextField73, "com.palm.app.youtube");
    }//GEN-LAST:event_jTextField73MousePressed

    private void jTextField41MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField41MousePressed
        setFile(jTextField41, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-1.png");
    }//GEN-LAST:event_jTextField41MousePressed

    private void jTextField42MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField42MousePressed
        setFile(jTextField42, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-2.png");
    }//GEN-LAST:event_jTextField42MousePressed

    private void jTextField44MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField44MousePressed
        setFile(jTextField44, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-3.png");
    }//GEN-LAST:event_jTextField44MousePressed

    private void jTextField43MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField43MousePressed
        setFile(jTextField43, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-4.png");
    }//GEN-LAST:event_jTextField43MousePressed

    private void jTextField45MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField45MousePressed
        setFile(jTextField45, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-5.png");
    }//GEN-LAST:event_jTextField45MousePressed

    private void jTextField46MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField46MousePressed
        setFile(jTextField46, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-6.png");
    }//GEN-LAST:event_jTextField46MousePressed

    private void jTextField48MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField48MousePressed
        setFile(jTextField48, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-7.png");
    }//GEN-LAST:event_jTextField48MousePressed

    private void jTextField47MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField47MousePressed
        setFile(jTextField47, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-8.png");
    }//GEN-LAST:event_jTextField47MousePressed

    private void jTextField49MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField49MousePressed
        setFile(jTextField49, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-9.png");
    }//GEN-LAST:event_jTextField49MousePressed

    private void jTextField50MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField50MousePressed
        setFile(jTextField50, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-10.png");
    }//GEN-LAST:event_jTextField50MousePressed

    private void jTextField52MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField52MousePressed
        setFile(jTextField52, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-11.png");
    }//GEN-LAST:event_jTextField52MousePressed

    private void jTextField51MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField51MousePressed
        setFile(jTextField51, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-12.png");
    }//GEN-LAST:event_jTextField51MousePressed

    private void jTextField53MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField53MousePressed
        setFile(jTextField53, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-13.png");
    }//GEN-LAST:event_jTextField53MousePressed

    private void jTextField60MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField60MousePressed
        setFile(jTextField60, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-14.png");
    }//GEN-LAST:event_jTextField60MousePressed

    private void jTextField54MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField54MousePressed
        setFile(jTextField54, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-15.png");
    }//GEN-LAST:event_jTextField54MousePressed

    private void jTextField61MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField61MousePressed
        setFile(jTextField61, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-16.png");
    }//GEN-LAST:event_jTextField61MousePressed

    private void jTextField55MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField55MousePressed
        setFile(jTextField55, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-17.png");
    }//GEN-LAST:event_jTextField55MousePressed

    private void jTextField62MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField62MousePressed
        setFile(jTextField62, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-18.png");
    }//GEN-LAST:event_jTextField62MousePressed

    private void jTextField56MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField56MousePressed
        setFile(jTextField56, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-19.png");
    }//GEN-LAST:event_jTextField56MousePressed

    private void jTextField63MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField63MousePressed
        setFile(jTextField63, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-20.png");
    }//GEN-LAST:event_jTextField63MousePressed

    private void jTextField57MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField57MousePressed
        setFile(jTextField57, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-21.png");
    }//GEN-LAST:event_jTextField57MousePressed

    private void jTextField64MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField64MousePressed
        setFile(jTextField64, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-22.png");
    }//GEN-LAST:event_jTextField64MousePressed

    private void jTextField58MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField58MousePressed
        setFile(jTextField58, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-23.png");
    }//GEN-LAST:event_jTextField58MousePressed

    private void jTextField65MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField65MousePressed
        setFile(jTextField65, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-24.png");
    }//GEN-LAST:event_jTextField65MousePressed

    private void jTextField59MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField59MousePressed
        setFile(jTextField59, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-25.png");
    }//GEN-LAST:event_jTextField59MousePressed

    private void jTextField66MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField66MousePressed
        setFile(jTextField66, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-26.png");
    }//GEN-LAST:event_jTextField66MousePressed

    private void jTextField67MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField67MousePressed
        setFile(jTextField67, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-27.png");
    }//GEN-LAST:event_jTextField67MousePressed

    private void jTextField68MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField68MousePressed
        setFile(jTextField68, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-28.png");
    }//GEN-LAST:event_jTextField68MousePressed

    private void jTextField69MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField69MousePressed
        setFile(jTextField69, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-29.png");
    }//GEN-LAST:event_jTextField69MousePressed

    private void jTextField70MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField70MousePressed
        setFile(jTextField70, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-30.png");
    }//GEN-LAST:event_jTextField70MousePressed

    private void jTextField71MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField71MousePressed
        setFile(jTextField71, "/usr/palm/applications/com.palm.app.calendar/images/launcher/icon-31.png");
    }//GEN-LAST:event_jTextField71MousePressed

    private void jTextField74MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField74MousePressed
        setFile(jTextField74, "/usr/palm/emoticons/emoticon-angry.png");
    }//GEN-LAST:event_jTextField74MousePressed

    private void jTextField97MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField97MousePressed
        setFile(jTextField97, "/usr/palm/emoticons/emoticon-confused.png");
    }//GEN-LAST:event_jTextField97MousePressed

    private void jTextField75MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField75MousePressed
        setFile(jTextField75, "/usr/palm/emoticons/emoticon-cool.png");
    }//GEN-LAST:event_jTextField75MousePressed

    private void jTextField96MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField96MousePressed
        setFile(jTextField96, "/usr/palm/emoticons/emoticon-cry.png");
    }//GEN-LAST:event_jTextField96MousePressed

    private void jTextField76MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField76MousePressed
        setFile(jTextField76, "/usr/palm/emoticons/emoticon-embarrassed.png");
    }//GEN-LAST:event_jTextField76MousePressed

    private void jTextField95MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField95MousePressed
        setFile(jTextField95, "/usr/palm/emoticons/emoticon-eww.png");
    }//GEN-LAST:event_jTextField95MousePressed

    private void jTextField77MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField77MousePressed
        setFile(jTextField77, "/usr/palm/emoticons/emoticon-footinmouth.png");
    }//GEN-LAST:event_jTextField77MousePressed

    private void jTextField94MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField94MousePressed
        setFile(jTextField94, "/usr/palm/emoticons/emoticon-frown.png");
    }//GEN-LAST:event_jTextField94MousePressed

    private void jTextField78MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField78MousePressed
       setFile(jTextField78, "/usr/palm/emoticons/emoticon-gasp.png");
    }//GEN-LAST:event_jTextField78MousePressed

    private void jTextField93MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField93MousePressed
        setFile(jTextField93, "/usr/palm/emoticons/emoticon-grin.png");
    }//GEN-LAST:event_jTextField93MousePressed

    private void jTextField79MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField79MousePressed
        setFile(jTextField79, "/usr/palm/emoticons/emoticon-heart.png");
    }//GEN-LAST:event_jTextField79MousePressed

    private void jTextField92MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField92MousePressed
        setFile(jTextField92, "/usr/palm/emoticons/emoticon-innocent.png");
    }//GEN-LAST:event_jTextField92MousePressed

    private void jTextField80MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField80MousePressed
        setFile(jTextField80, "/usr/palm/emoticons/emoticon-kiss.png");
    }//GEN-LAST:event_jTextField80MousePressed

    private void jTextField91MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField91MousePressed
        setFile(jTextField91, "/usr/palm/emoticons/emoticon-laugh.png");
    }//GEN-LAST:event_jTextField91MousePressed

    private void jTextField81MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField81MousePressed
        setFile(jTextField81, "/usr/palm/emoticons/emoticon-naughty.png");
    }//GEN-LAST:event_jTextField81MousePressed

    private void jTextField90MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField90MousePressed
        setFile(jTextField90, "/usr/palm/emoticons/emoticon-neutral.png");
    }//GEN-LAST:event_jTextField90MousePressed

    private void jTextField82MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField82MousePressed
        setFile(jTextField82, "/usr/palm/emoticons/emoticon-sick.png");
    }//GEN-LAST:event_jTextField82MousePressed

    private void jTextField89MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField89MousePressed
        setFile(jTextField89, "/usr/palm/emoticons/emoticon-smile.png");
    }//GEN-LAST:event_jTextField89MousePressed

    private void jTextField83MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField83MousePressed
        setFile(jTextField83, "/usr/palm/emoticons/emoticon-undecided.png");
    }//GEN-LAST:event_jTextField83MousePressed

    private void jTextField88MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField88MousePressed
        setFile(jTextField88, "/usr/palm/emoticons/emoticon-wink.png");
    }//GEN-LAST:event_jTextField88MousePressed

    private void jTextField84MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField84MousePressed
        setFile(jTextField84, "/usr/palm/emoticons/emoticon-yuck.png");
    }//GEN-LAST:event_jTextField84MousePressed

    private void jTextField5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField5MousePressed
        setFile(jTextField5, "/usr/palm/sysmgr/images/screen-lock-wallpaper-mask.png");
    }//GEN-LAST:event_jTextField5MousePressed

    private void jTextField6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField6MousePressed
        setFile(jTextField6, "/usr/palm/sysmgr/images/screen-lock-target-scrim.png");
    }//GEN-LAST:event_jTextField6MousePressed

    private void jTextField7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField7MousePressed
        setFile(jTextField7, "/usr/palm/sysmgr/images/quick_launch_bg.png");
    }//GEN-LAST:event_jTextField7MousePressed

    private void jTextField8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField8MousePressed
        setFile(jTextField8, "/usr/palm/sysmgr/images/quick_launch_stash.png");
    }//GEN-LAST:event_jTextField8MousePressed

    private void jTextField9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField9MousePressed
        setFile(jTextField9, "/usr/palm/sysmgr/images/quick_launch_highlight.png");
    }//GEN-LAST:event_jTextField9MousePressed

    private void jTextField10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField10MousePressed
        setFile(jTextField10, "/usr/palm/sysmgr/images/drive-mode-fullscreen.png");
    }//GEN-LAST:event_jTextField10MousePressed

    private void jTextField11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField11MousePressed
        setFile(jTextField11, "/usr/palm/sysmgr/images/media-sync-fullscreen.png");
    }//GEN-LAST:event_jTextField11MousePressed

    private void jSlider1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSlider1PropertyChange
        if(pData!=null) {
            pData.opacity = jSlider1.getValue();
        }
        jLabel101.setText(jSlider1.getValue() + "%");
        getContentPane().requestFocus();
}//GEN-LAST:event_jSlider1PropertyChange

    private void jTextField86MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField86MousePressed
        pData.setColour();
        jTextField86.setText(pData.getHexVal());
        getContentPane().requestFocus();
    }//GEN-LAST:event_jTextField86MousePressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        pData.setColour();
        jTextField86.setText(pData.getHexVal());
        getContentPane().requestFocus();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField85KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField85KeyTyped
        String s = jTextField85.getText();
        if(s.length()>0) {
            pData.carrierString = s;
        }
    }//GEN-LAST:event_jTextField85KeyTyped

    private void jTextField87MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField87MousePressed
        setFile(jTextField87, "/usr/lib/luna/system/luna-applauncher/images/launcher-page-fade-bottom.png");
        getContentPane().requestFocus();
    }//GEN-LAST:event_jTextField87MousePressed

    private void jTextField98MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField98MousePressed
        setFile(jTextField98, "/usr/lib/luna/system/luna-applauncher/images/scrim.png");
        getContentPane().requestFocus();
    }//GEN-LAST:event_jTextField98MousePressed

    private void jTextField99MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField99MousePressed
        setFile(jTextField99, "/usr/lib/luna/system/luna-applauncher/images/launcher-page-fade-top.png");
        getContentPane().requestFocus();
    }//GEN-LAST:event_jTextField99MousePressed

    private void jTextField100MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField100MousePressed
        setIcon(jTextField100, "com.palm.app.browser");
    }//GEN-LAST:event_jTextField100MousePressed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane10;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane5;
    private javax.swing.JLayeredPane jLayeredPane6;
    private javax.swing.JLayeredPane jLayeredPane9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField100;
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
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField52;
    private javax.swing.JTextField jTextField53;
    private javax.swing.JTextField jTextField54;
    private javax.swing.JTextField jTextField55;
    private javax.swing.JTextField jTextField56;
    private javax.swing.JTextField jTextField57;
    private javax.swing.JTextField jTextField58;
    private javax.swing.JTextField jTextField59;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField60;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField63;
    private javax.swing.JTextField jTextField64;
    private javax.swing.JTextField jTextField65;
    private javax.swing.JTextField jTextField66;
    private javax.swing.JTextField jTextField67;
    private javax.swing.JTextField jTextField68;
    private javax.swing.JTextField jTextField69;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField70;
    private javax.swing.JTextField jTextField71;
    private javax.swing.JTextField jTextField72;
    private javax.swing.JTextField jTextField73;
    private javax.swing.JTextField jTextField74;
    private javax.swing.JTextField jTextField75;
    private javax.swing.JTextField jTextField76;
    private javax.swing.JTextField jTextField77;
    private javax.swing.JTextField jTextField78;
    private javax.swing.JTextField jTextField79;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField80;
    private javax.swing.JTextField jTextField81;
    private javax.swing.JTextField jTextField82;
    private javax.swing.JTextField jTextField83;
    private javax.swing.JTextField jTextField84;
    private javax.swing.JTextField jTextField85;
    private javax.swing.JTextField jTextField86;
    private javax.swing.JTextField jTextField87;
    private javax.swing.JTextField jTextField88;
    private javax.swing.JTextField jTextField89;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTextField90;
    private javax.swing.JTextField jTextField91;
    private javax.swing.JTextField jTextField92;
    private javax.swing.JTextField jTextField93;
    private javax.swing.JTextField jTextField94;
    private javax.swing.JTextField jTextField95;
    private javax.swing.JTextField jTextField96;
    private javax.swing.JTextField jTextField97;
    private javax.swing.JTextField jTextField98;
    private javax.swing.JTextField jTextField99;
    // End of variables declaration//GEN-END:variables

    class WPChooseFilter extends javax.swing.filechooser.FileFilter {
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

    class PNGChooseFilter extends javax.swing.filechooser.FileFilter {
        private final String[] okFileExtensions = new String[] {".png"};

        public boolean accept(File f) {
            for (String extension : okFileExtensions)
                if (f.getName().toLowerCase().endsWith(extension) || f.isDirectory())
                    return true;
            return false;
        }

        public String getDescription() {
            return "PNG Files";
        }
    }
}
