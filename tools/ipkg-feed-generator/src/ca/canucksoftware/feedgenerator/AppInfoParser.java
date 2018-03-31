
package ca.canucksoftware.feedgenerator;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.json.JSONObject;

public class AppInfoParser {
    private File dataTar;
    private String id;
    private String title;
    private String vendor;

    public AppInfoParser(File tar, String appid) {
        dataTar = tar;
        id = appid;
        loadAppInfo();
    }

    private void loadAppInfo() {
        try {
            TarInputStream tar = new TarInputStream(new GZIPInputStream(
                    new FileInputStream(dataTar)));
            TarEntry entry = tar.getNextEntry();
            String target = "usr/palm/applications/" + id + "/appinfo.json";
            while(entry != null) {
                String name = entry.getName();
                if(name.equals("./" + target) || name.equals("/" + target) ||
                        name.equals(target)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(tar));
                    String text = "";
                    String line = br.readLine();
                    while(line!=null) {
                        text += line.trim();
                        line = br.readLine();
                    }
                    JSONObject json = new JSONObject(text);
                    title = json.getString("title");
                    vendor = json.getString("vendor");
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

    public String getTitle() { return title; }

    public String getVendor() { return vendor; }
}
