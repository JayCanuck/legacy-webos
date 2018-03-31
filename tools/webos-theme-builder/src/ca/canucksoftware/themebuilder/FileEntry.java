package ca.canucksoftware.themebuilder;

import java.io.File;

/**
 *
 * @author Jason
 */
public class FileEntry {
    public String dest;
    public File file;
    public String category;
    
    public FileEntry() {
        dest = null;
        file = null;
    }
    public FileEntry(String dest, File file, String category) {
        this.dest = dest;
        this.file = file;
        this.category = category;
    }
    public String zipResourcePath() {
        return "files/" + category + dest;
    }
    public String toString() {
        return file.getPath() + " " + zipResourcePath() + " " + category;
    }
}
