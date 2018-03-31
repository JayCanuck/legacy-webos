
package ca.canucksoftware.feedgenerator;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class ControlParser {
    private File controlTar;
    private String id;
    private String version;
    private String section;
    private String arch;
    private String maintain;
    private String descript;
    private String source;
    private String depends;

    public ControlParser(File tar) {
        controlTar = tar;
        id = "";
        version = "1.0.0";
        section = "misc";
        arch = "all";
        maintain = "";
        descript = "";
        source = "";
        depends = null;
        loadControl();
    }

    private void loadControl() {
        try {
            TarInputStream tar = new TarInputStream(new GZIPInputStream(
                    new FileInputStream(controlTar)));
            TarEntry entry = tar.getNextEntry();
            while(entry != null) {
                String name = entry.getName();
                if(name.equals("./control") || name.equals("/control") ||
                        name.equals("control")) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(tar));
                    String line = br.readLine();
                    while(line!=null) {
                        parseControlLine(line);
                        line = br.readLine();
                    }
                    br.close();
                    break;
                }
                entry = tar.getNextEntry();
            }
            tar.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseControlLine(String line) {
        if(line.startsWith("Package:")) {
            id = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Version:")) {
            version = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Section:")) {
            section = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Architecture:")) {
            arch = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Maintainer:")) {
            maintain = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Description:")) {
            descript = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Source:")) {
            source = line.substring(line.indexOf(":")+2).trim();
        } else if(line.startsWith("Depends:")) {
            depends = line.substring(line.indexOf(":")+2).trim();
        }
    }

    public boolean validPackage() { return (id.trim().length()>0); }

    public boolean palmPackaged() {
        return (descript.equals("This is a webOS application.") ||
                maintain.equals("N/A <nobody@example.com>"));
    }

    public String getId() { return id; }

    public String getVersion() { return version; }

    public String getSection() { return section; }

    public String getArchitecture() { return arch; }

    public String getMaintainer() { return maintain; }

    public String getDescription() { return descript; }

    public String getSource() { return source; }

    public String getDepends() { return depends; }
}
