
package ca.canucksoftware.themebuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * @author Jason
 */
public class PatchEntry {
    public File file;
    public ArrayList<File> subpatches;
    public ArrayList<String> paths;
    public String category;

    public PatchEntry(File file, String category) {
        this.file = file;
        this.category = category;
        subpatches = new ArrayList<File>();
        paths = new ArrayList<String>();
    }

    public PatchEntry() { this(null, null); }
    
    private String getFilename() {
        String name = file.getName();
        int index = name.lastIndexOf("\\");
        if(index>-1) {
            name = name.substring(index+1, name.length());
        } else {
            index = name.lastIndexOf("/");
            if(index>-1)
                name = name.substring(index+1, name.length());
        }
        return name;
    }
    public void splitPatch() {
        subpatches.clear();
        paths.clear();
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            File curr = null;
            BufferedWriter bw = null;
            String line = br.readLine();
            while(line!=null) {
                System.out.println(line);
                if(line.startsWith("---")) {
                    if(bw!=null) {
                        bw.flush();
                        bw.close();
                    }
                    curr = new File(tmpFilePath, getFilename().replace(".patch", "") + "-" + (subpatches.size()+1) +
                            ".patch");
                    if(curr.exists()) {
                        curr.delete();
                    }
                    subpatches.add(curr);
                    if(curr.exists()) {
                        curr.delete();
                    }
                    bw = new BufferedWriter(new FileWriter(curr));
                    bw.write(line + "\n");
                    line = br.readLine();
                    paths.add(line.substring(line.indexOf("/")));
                }
                if(line.startsWith("+") || line.startsWith("-") || line.startsWith(" ") || line.startsWith("@") || line.startsWith("\\")) {
                    if(bw!=null) {
                        bw.write(line + "\n");
                    }
                } else {
                    if(bw!=null) {
                        bw.flush();
                        bw.close();
                        bw = null;
                    }
                }
                line = br.readLine();
            }
            if(bw!=null) {
                bw.flush();
                bw.close();
                bw = null;
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public String zipResourcePrefix() {
        return "patches/" + category + getFilename().replace(".patch", "") + "/";
    }
    public String zipResourcePath() {
        return "patches/" + category + getFilename();
    }
    public String toString() {
        return file.getPath() + " " + zipResourcePath() + " " + category;
    }
}
