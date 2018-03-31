
package ca.canucksoftware.themebuilder;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jason
 */
public class VersionTheme {
    public String version;
    public File wallpaper;
    public LinkedList<IconEntry> icons;
    public LinkedList<FileEntry> files;
    public LinkedList<PatchEntry> patches;

    public VersionTheme(String ver) {
        version = ver;
        wallpaper = null;
        icons = new LinkedList<IconEntry>();
        files = new LinkedList<FileEntry>();
        patches = new LinkedList<PatchEntry>();
    }

    public List<String> categories() {
        LinkedList<String> allCat = new LinkedList<String>();
        if(wallpaper!=null) {
            allCat.add("wallpapers");
        }
        if(icons.size()>0) {
            allCat.add("app_icons");
        }
        for(int i=0; i<files.size(); i++) {
            if(!allCat.contains(files.get(i).category)) {
                allCat.add(files.get(i).category);
            }
        }
        for(int i=0; i<patches.size(); i++) {
            if(!allCat.contains(patches.get(i).category)) {
                allCat.add(patches.get(i).category);
            }
        }
        return allCat;
    }

    public List<FileEntry> filesByCategory(String category) {
        LinkedList<FileEntry> filtered = new LinkedList<FileEntry>();
        if(category.equals("--all--")) {
            filtered = files;
        } else {
            for(int i=0; i<files.size(); i++) {
                if(files.get(i).category.equals(category)) {
                    filtered.add(files.get(i));
                }
            }
        }
        return filtered;
    }
    public List<PatchEntry> patchesByCategory(String category) {
        LinkedList<PatchEntry> filtered = new LinkedList<PatchEntry>();
        if(category.equals("--all--")) {
            filtered = patches;
        } else {
            for(int i=0; i<patches.size(); i++) {
                if(patches.get(i).category.equals(category)) {
                    filtered.add(patches.get(i));
                }
            }
        }
        return filtered;
    }
    public String toString() {
        String output = "webOS " + version + "\n";
        if(wallpaper!=null) {
            output += "\tWallpaper: " + wallpaper.getPath() + "\n";
        }
        output += "\tFiles:\n";
        for(int i=0; i<files.size(); i++) {
            output += "\t\t" + files.get(i) + "\n";
        }
        output += "\tIcons:\n";
        for(int i=0; i<icons.size(); i++) {
            output += "\t\t" + icons.get(i) + "\n";
        }
        output += "\tPatches:\n";
        for(int i=0; i<patches.size(); i++) {
            output += "\t\t" + patches.get(i) + "\n";
        }
        return output;
    }
}
