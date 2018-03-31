
package ca.canucksoftware.themebuilder;

import javax.swing.JColorChooser;
import java.awt.Color;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

/**
 *
 * @author Jason Robitaille
 */
public class PatchData {
    public final String CARRIER_PATCH = "resources/custom-carrier-string.patch";
    public final String TEXTCOLOUR_PATCH = "resources/change-top-bar-text-colour.patch";
    public final String OPACITY_PATCH = "resources/launcher-opacity.patch";
    public String carrierString;
    public Color textColour;
    public int opacity;

    public PatchData() {
        carrierString = null;
        textColour = Color.WHITE;
        opacity = 100;
    }

    public boolean parsePatch(File f, String patch) {
        String s = null;
        boolean result = false;
        if(patch.equalsIgnoreCase(CARRIER_PATCH)) {
            s = findReturn(f, "this.carrierText = \"");
            if(s!=null) {
                carrierString = s.substring(s.indexOf("\"")+1, s.lastIndexOf("\""));
                result = true;
            }
        } else if(patch.equalsIgnoreCase(TEXTCOLOUR_PATCH)) {
            s = findReturn(f, "color:");
            if(s!=null) {
                s = s.substring(s.indexOf(":")+1, s.indexOf(";")).trim();
                textColour = Color.decode(s);
                result = true;
            }
        } else if(patch.equalsIgnoreCase(OPACITY_PATCH)) {
            s = findReturn(f, "opacity:");
            if(s!=null) {
                s = s.substring(s.indexOf(":")+1, s.indexOf(";")).trim();
                opacity = (int)(Double.parseDouble(s)*100);
                result = true;
            }
        }
        return result;
    }

    public void setColour() {
        Color c = JColorChooser.showDialog(null, "Select A Colour", Color.WHITE);
        if(c!=null) {
            textColour = c;
        }
    }

    public LinkedList<String> list() {
        LinkedList<String> files = new LinkedList<String>();
        if(carrierString!=null) {
            if(carrierString.length()>0) {
                files.add(CARRIER_PATCH.substring(CARRIER_PATCH.lastIndexOf("/")+1));
            }
        }
        if(textColour!=null) {
            if(textColour!=Color.WHITE) {
                files.add(TEXTCOLOUR_PATCH.substring(TEXTCOLOUR_PATCH.lastIndexOf("/")+1));
            }
        }
        if(opacity!=100) {
            files.add(OPACITY_PATCH.substring(OPACITY_PATCH.lastIndexOf("/")+1));
        }
        return files;
    }

    public void removePatch(String patch) {
        if(patch.equalsIgnoreCase(CARRIER_PATCH.substring(CARRIER_PATCH.lastIndexOf("/")+1))) {
            carrierString = null;
        } else if(patch.equalsIgnoreCase(TEXTCOLOUR_PATCH.substring(TEXTCOLOUR_PATCH.lastIndexOf("/")+1))) {
            textColour = Color.WHITE;
        } else if(patch.equalsIgnoreCase(OPACITY_PATCH.substring(OPACITY_PATCH.lastIndexOf("/")+1))) {
            opacity = 100;
        }
    }

    public LinkedList<File> getPatches() {
        LinkedList<File> patches = new LinkedList<File>();
        if(carrierString!=null) {
            if(carrierString.length()>0) {
                patches.add(findReplace(extractFromJar(CARRIER_PATCH), "/*Edit Here*/",
                        "+\t\t\tthis.carrierText = \"" + carrierString + "\";\n"));
            }
        }
        if(textColour!=null) {
            if(textColour!=Color.WHITE) {
                String colHex = getHexVal();
                patches.add(findReplace(extractFromJar(TEXTCOLOUR_PATCH), "/*Edit Here*/",
                        "+\t\tcolor: " + colHex + ";\n"));
            }
        }
        if(opacity!=100) {
            String opacVal = (((double)opacity)/100) + "";
            patches.add(findReplace(extractFromJar(OPACITY_PATCH), "/*Edit Here*/",
                        "+\topacity: " + opacVal + ";\n"));
        }
        return patches;
    }

    public String getHexVal() {
        return "#" + Integer.toHexString((textColour.getRGB() & 0xffffff) | 0x1000000).substring(1).toUpperCase();
    }

    private File extractFromJar(String filename) {
        try {
            String tmpFilePath = System.getProperty("java.io.tmpdir");
            File efile = new File(tmpFilePath, filename.substring(filename.lastIndexOf("/")+1));
            if(efile.exists())
                efile.delete();
            InputStream in = new BufferedInputStream(super.getClass().getResourceAsStream(filename));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));
            byte[] buffer = new byte[2048];
            for (;;)  {
                int nBytes = in.read(buffer);
                if (nBytes <= 0)
                    break;
                out.write(buffer, 0, nBytes);
            }
            out.flush();
            out.close();
            in.close();
            return efile;
        } catch (Exception e) {
            return null;
        }
    }

    private File findReplace(File f, String find, String replace) {
        File result = null;
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        try {
            result = new File(tmpFilePath, "output");
            BufferedReader br = new BufferedReader(new FileReader(f));
            BufferedWriter bw = new BufferedWriter(new FileWriter(result));
            String line = br.readLine();
            while(line!=null) {
                line += "\n";
                if(line.contains(find)) {
                    line = replace;
                }
                bw.write(line);
                line = br.readLine();
            }
            bw.flush();
            bw.close();
            br.close();
            f.delete();
            result.renameTo(f);
            result = f;
        } catch(Exception e) {
            result = null;
        }
        return result;
    }

    private String findReturn(File f, String find) {
        String result = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while(line!=null) {
                line += "\n";
                if(line.contains(find)) {
                    result = line;
                }
                line = br.readLine();
            }
            br.close();
        } catch(Exception e) {
            result = null;
        }
        return result;
    }
}
