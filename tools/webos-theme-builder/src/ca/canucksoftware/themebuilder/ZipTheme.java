
package ca.canucksoftware.themebuilder;

import com.twicom.qdparser.Element;
import com.twicom.qdparser.TaggedElement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Jason
 */
public class ZipTheme {
    public String name;
    public String version;
    public String creator;
    public String website;
    public String donateURL;
    public String id;
    public String description;
    public ArrayList<File> screenshots;
    public ArrayList<String> devices;
    public VersionControl controller;

    public ZipTheme() {
        reset();
    }

    public String toString() {
        String output = "Name: " + name + "\n";
        output += "Version: " + version + "\n";
        output += "Creator: " + creator + "\n";
        output += "Website: " + website + "\n";
        output += "Donation URL: " + donateURL + "\n";
        output += "ID: " + id + "\n";
        output += "Description: " + version + "\n";
        output += "Screenshots:\n";
        for(int i=0; i<screenshots.size(); i++) {
            output += "\t" + screenshots.get(i) + "\n";
        }
        output += controller + "\n";
        return output;
    }

    public void reset() {
        name = "";
        version = "";
        creator = "";
        website = "";
        donateURL = "";
        id = "";
        description = "";
        screenshots = new ArrayList<File>();
        devices = new ArrayList<String>();
        controller = new VersionControl();
    }

    public File generateXML() {
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        File xml = new File(tmpFilePath, "theme.xml");
        if(xml.exists()) {
            xml.delete();
        }
        XMLHandler theme = XMLHandler.createNewXML(xml, "data");
        TaggedElement info, previews, content, iconset, wp;
        info = (TaggedElement)Element.newElement("<themeinfo></themeinfo>");
        content = (TaggedElement)Element.newElement("<themelist></themelist>");
        theme.getRoot().add(info);
        theme.getRoot().add(content);
        info.add((TaggedElement)Element.newElement("<name>" + encodeXml(name) + "</name>"));
        info.add((TaggedElement)Element.newElement("<version>" + encodeXml(version) + "</version>"));
        info.add((TaggedElement)Element.newElement("<creator>" + encodeXml(creator) + "</creator>"));
        info.add((TaggedElement)Element.newElement("<description>" + encodeXml(description) + "</description>"));
        info.add((TaggedElement)Element.newElement("<website>" + encodeXml(website) + "</website>"));
        previews = (TaggedElement)Element.newElement("<screenshots></screenshots>");
        info.add(previews);
        for(int i=0; i<screenshots.size(); i++) {
            if(screenshots.get(i).exists()) {
                previews.add((TaggedElement)Element.newElement("<image>" + id + "/screenshots/" + (i+1) +
                        fileExt(screenshots.get(i)) + "</image>"));
            }
        }
        VersionTheme firstTheme = controller.getVersion(controller.list(false).get(0));
        if(firstTheme.wallpaper!=null) {
            wp = (TaggedElement)Element.newElement("<wallpaper></wallpaper>");
            content.add(wp);
            wp.add((TaggedElement)Element.newElement("<image>" + id + "/" +firstTheme.version + "/wallpaper" +
                    fileExt(firstTheme.wallpaper) + "</image>"));
        }
        for(int i=0; i<firstTheme.files.size(); i++) {
            if(firstTheme.files.get(i).file!=null && firstTheme.files.get(i).file.exists()) {
                TaggedElement file = (TaggedElement)Element.newElement("<file></file>");
                content.add(file);
                file.add((TaggedElement)Element.newElement("<filename>" + id + "/" + firstTheme.version + "/" +
                        firstTheme.files.get(i).zipResourcePath() + "</filename>"));
                file.add((TaggedElement)Element.newElement("<destination>" + firstTheme.files.get(i).dest +
                        "</destination>"));
            }
        }
        for(int i=0; i<firstTheme.patches.size(); i++) {
            if(firstTheme.patches.get(i).file!=null && firstTheme.patches.get(i).file.exists()) {
                TaggedElement patch = (TaggedElement)Element.newElement("<patch></patch>");
                content.add(patch);
                patch.add((TaggedElement)Element.newElement("<diff>" + id + "/" + firstTheme.version + "/" +
                        firstTheme.patches.get(i).zipResourcePath() + "</diff>"));
            }
        }
        if(firstTheme.icons.size()>0) {
            iconset = (TaggedElement)Element.newElement("<iconset></iconset>");
            content.add(iconset);
            for(int i=0; i<firstTheme.icons.size(); i++) {
                if(firstTheme.icons.get(i).image!=null && firstTheme.icons.get(i).image.exists()) {
                    TaggedElement icon = (TaggedElement)Element.newElement("<icon></icon>");
                    iconset.add(icon);
                    icon.add((TaggedElement)Element.newElement("<appid>" + firstTheme.icons.get(i).appID +
                            "</appid>"));
                    icon.add((TaggedElement)Element.newElement("<image>" + id + "/" + firstTheme.version + "/" +
                            firstTheme.icons.get(i).zipResourcePath() + "</image>"));
                }
            }
        }
        theme.updateFile();
        return xml;
    }

    private String encodeXml(String text) {
        return text.replaceAll("&", "&amp;");
    }

    private String decodeXml(String text) {
        return text.replaceAll("&amp;", "&");
    }

    public File generateJSON() {
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        File json = new File(tmpFilePath, "theme.json");
        if(json.exists()) {
            json.delete();
        }
        JSONObject content = new JSONObject();
        try {
            content.put("name", name);
            content.put("version", version);
            content.put("creator", creator);
            content.put("description", description);
            content.put("website", website);
            if(donateURL!=null) {
                content.put("donations", donateURL);
            }
            ArrayList<String> ss = new ArrayList<String>();
            for(int i=0; i<screenshots.size(); i++) {
                if(screenshots.get(i).exists()) {
                    ss.add("screenshots/" + (i+1) + fileExt(screenshots.get(i)));
                }
            }
            content.put("screenshots", new JSONArray(ss));
            content.put("devices", new JSONArray(devices));
            JSONObject themeData = new JSONObject();
            List<VersionTheme> versions = controller.listVersions();
            for(int i=0; i<versions.size(); i++) {
                JSONObject currVer = new JSONObject();
                List<String> categories = versions.get(i).categories();
                for(int j=0; j<categories.size(); j++) {
                    JSONObject currCat = new JSONObject();
                    String catStr = categories.get(j);
                    List<JSONObject> images = new ArrayList<JSONObject>();
                    List<JSONObject> patches = new ArrayList<JSONObject>();
                    List<JSONObject> sounds = new ArrayList<JSONObject>();
                    if(catStr.equals("wallpapers")) {
                        JSONObject wallpaper = new JSONObject();
                        if(versions.get(i).wallpaper!=null) {
                            wallpaper.put("path", "/media/internal/wallpapers/wallpaper.jpg");
                            wallpaper.put("file", versions.get(i).version + "/wallpaper" +
                                    fileExt(versions.get(i).wallpaper));
                        }
                        images.add(wallpaper);
                    } else if(catStr.equals("app_icons")) {
                        for(int k=0; k<versions.get(i).icons.size(); k++) {
                            JSONObject currFile = new JSONObject();
                            currFile.put("path", versions.get(i).icons.get(k).dest());
                            currFile.put("file", versions.get(i).version +  "/" +
                                    versions.get(i).icons.get(k).zipResourcePath());
                            images.add(currFile);
                        }
                    }
                    List<FileEntry> catFiles = versions.get(i).filesByCategory(catStr);
                    for(int k=0; k<catFiles.size(); k++) {
                        JSONObject currFile = new JSONObject();
                        currFile.put("path", catFiles.get(k).dest);
                        currFile.put("file", versions.get(i).version + "/" + catFiles.get(k).zipResourcePath());
                        if(catFiles.get(k).dest.endsWith(".mp3") || catFiles.get(k).dest.endsWith(".wav")) {
                            sounds.add(currFile);
                        } else {
                            images.add(currFile);
                        }
                    }
                    List<PatchEntry> catPatches = versions.get(i).patchesByCategory(catStr);
                    for(int k=0; k<catPatches.size(); k++) {
                        catPatches.get(k).splitPatch();
                        for(int l=0; l<catPatches.get(k).subpatches.size(); l++) {
                            JSONObject currFile = new JSONObject();
                            currFile.put("path", catPatches.get(k).paths.get(l));
                            currFile.put("file", versions.get(i).version + "/" +
                                    catPatches.get(k).zipResourcePrefix() + (l+1) + ".patch");
                            patches.add(currFile);
                        }
                    }
                    if(images.size()>0) {
                        currCat.put("images", new JSONArray(images));
                    }
                    if(patches.size()>0) {
                        currCat.put("patches", new JSONArray(patches));
                    }
                    if(sounds.size()>0) {
                        currCat.put("sounds", new JSONArray(sounds));
                    }
                    if(currCat.length()>0) {
                        currVer.put(catStr, currCat);
                    }
                }
                if(currVer.length()>0) {
                    themeData.put(versions.get(i).version, currVer);
                }
            }
            List<AliasVersion> aliases = controller.listAliases();
            for(int i=0; i<aliases.size(); i++) {
                    JSONObject currAlias = new JSONObject();
                    currAlias.put("alias", aliases.get(i).realVersion);
                    themeData.put(aliases.get(i).version, currAlias);
            }
            content.put("themedata", themeData);
        }catch(Exception e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(json));
            bw.write(content.toString(4));
            bw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public File buildZip(File out) {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out));
            File xml = generateXML();
            addToZip(zos, xml, "theme.xml");
            File json = generateJSON();
            addToZip(zos, json, id + "/theme.json");
            for(int i=0; i<screenshots.size(); i++) {
                if(screenshots.get(i).exists()) {
                    addToZip(zos, screenshots.get(i), id + "/screenshots/" + (i+1) + fileExt(screenshots.get(i)));
                }
            }
            List<VersionTheme> versions = controller.listVersions();
            for(int i=0; i<versions.size(); i++) {
                if(versions.get(i).wallpaper!=null && versions.get(i).wallpaper.exists()) {
                    addToZip(zos, versions.get(i).wallpaper, id + "/" +versions.get(i).version + "/wallpaper" +
                            fileExt(versions.get(i).wallpaper));
                }
                for(int j=0; j<versions.get(i).icons.size(); j++) {
                    if(versions.get(i).icons.get(j).image.exists()) {
                        addToZip(zos, versions.get(i).icons.get(j).image, id + "/" + versions.get(i).version +
                                "/" + versions.get(i).icons.get(j).zipResourcePath());
                    }
                }
                for(int j=0; j<versions.get(i).files.size(); j++) {
                    if(versions.get(i).files.get(j).file.exists()) {
                        addToZip(zos, versions.get(i).files.get(j).file, id + "/" + versions.get(i).version +
                                "/" + versions.get(i).files.get(j).zipResourcePath());
                    }
                }
                for(int j=0; j<versions.get(i).patches.size(); j++) {
                    if(versions.get(i).patches.get(j).file.exists()) {
                        addToZip(zos, versions.get(i).patches.get(j).file, id + "/" + versions.get(i).version +
                                "/" + versions.get(i).patches.get(j).zipResourcePath());
                        for(int k=0; k<versions.get(i).patches.get(j).subpatches.size(); k++) {
                            if(versions.get(i).patches.get(j).subpatches.get(k).exists()) {
                                addToZip(zos, versions.get(i).patches.get(j).subpatches.get(k),
                                        id + "/" + versions.get(i).version + "/" +
                                        versions.get(i).patches.get(j).zipResourcePrefix() + (k+1) + ".patch");
                            }
                        }
                    }
                }
            }
            zos.flush();
            zos.close();
            if(xml.exists()) {
                xml.delete();
            }
            if(json.exists()) {
                json.delete();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    private void addToZip(ZipOutputStream out, File f, String s) throws Exception {
        byte[] buf = new byte[2048];
        FileInputStream in = new FileInputStream(f);
        out.putNextEntry(new ZipEntry(s));
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.closeEntry();
        in.close();
    }

    private String getFilename(File f) {
        String filename = f.getName();
        int index = filename.lastIndexOf("\\");
        if(index>-1) {
            filename = filename.substring(index+1, filename.length());
        } else {
            index = filename.lastIndexOf("/");
            if(index>-1)
                filename = filename.substring(index+1, filename.length());
        }
        return filename;
    }

    private String fileExt(File f) {
        String ext = "";
        int index = f.getName().lastIndexOf(".");
        if(index>=0) {
            ext = f.getName().substring(index);
        }
        return ext;
    }

    public File extractTheme(File zip) throws Exception {
        final int BUFFER = 2048;
        int count;
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        File outDir = new File(tmpFilePath, getFilename(zip) + System.currentTimeMillis());
        byte data[] = new byte[BUFFER];
        outDir.mkdirs();
        ZipFile zipF = new ZipFile(zip);
        Enumeration entries = zipF.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            if(entry.isDirectory()) {
                File dir = new File(outDir, entry.getName());
                dir.mkdirs();
            } else {
                InputStream is = zipF.getInputStream(entry);
                File out = new File(outDir, entry.getName());
                if(!out.getParentFile().isDirectory()) {
                    out.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(out);
                while ((count = is.read(data,0,BUFFER)) != -1) {
                     fos.write(data,0,count);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        }
        zipF.close();
        return outDir;
    }

    public boolean isNewFormat(File extractedDir) {
        return new File(extractedDir.listFiles(new DirFilter())[0], "theme.json").exists();
    }

    public void loadFromXML(File extractedDir, ArrayList<String> devices, String version) {
        reset();
        try {
            int i;
            File curr;
            File xml = new File(extractedDir, "theme.xml");
            if(xml.exists()) {
                XMLHandler theme = new XMLHandler(xml);
                TaggedElement info = theme.getRoot().find("themeinfo");
                name = theme.getContent(info.find("name"));
                this.version = theme.getContent(info.find("version"));
                creator = theme.getContent(info.find("creator"));
                website = theme.getContent(info.find("website"));
                description = theme.getContent(info.find("description"));
                TaggedElement ss = info.find("screenshots");
                for(i=0; i<ss.elements(); i++) {
                    curr = new File(extractedDir, theme.getContent(ss.getChild(i)));
                    if(curr.exists()) {
                        screenshots.add(curr);
                    }
                }
                this.devices = devices;
                VersionTheme verTheme = controller.addVersion(version);
                TaggedElement content = theme.getRoot().find("themelist");
                for(i=0; i<content.elements(); i++) {
                    TaggedElement currEle = (TaggedElement) content.getChild(i);
                    if(currEle.getTag().equalsIgnoreCase("file")) {
                        Element src = currEle.find("filename");
                        Element dest = currEle.find("destination");
                        if(src!=null && dest !=null) {
                            curr = new File(extractedDir, theme.getContent(src));
                            if(curr.exists()) {
                                verTheme.files.add(new FileEntry(theme.getContent(dest), curr, "applications"));
                            }
                        }
                    } else if(currEle.getTag().equalsIgnoreCase("wallpaper")) {
                        Element wp = currEle.find("image");
                        if(wp!=null) {
                            curr = new File(extractedDir, theme.getContent(wp));
                            if(curr.exists()) {
                                verTheme.wallpaper = curr;
                            }
                        }
                    } else if(currEle.getTag().equalsIgnoreCase("iconset")) {
                        for(int j=0; j<currEle.elements(); j++) {
                            TaggedElement currIcon = (TaggedElement) currEle.getChild(j);
                            Element src = currIcon.find("image");
                            Element id = currIcon.find("appid");
                            if(src!=null && id!=null) {
                                curr = new File(extractedDir, theme.getContent(src));
                                if(curr.exists()) {
                                    verTheme.icons.add(new IconEntry(theme.getContent(id), curr));
                                }
                            }
                        }
                    } else if(currEle.getTag().equalsIgnoreCase("patch")) {
                        Element diff = currEle.find("diff");
                        if(diff!=null) {
                            curr = new File(extractedDir, theme.getContent(diff));
                            if(curr.exists()) {
                                verTheme.patches.add(new PatchEntry(curr, "applications"));
                            }
                        }
                    }
                }
                xml.delete();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromJSON(File extractedDir) {
        reset();
        try {
            File curr;
            extractedDir = extractedDir.listFiles(new DirFilter())[0];
            id = getFilename(extractedDir);
            File json = new File(extractedDir, "theme.json");
            if(json.exists()) {
                String jsonStr = "";
                try{
                    BufferedReader br = new BufferedReader(new FileReader(json));
                    String line = br.readLine();
                    while(line!=null) {
                        jsonStr += line;
                        line = br.readLine();
                    }
                    br.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                JSONObject theme = new JSONObject(jsonStr);
                name = theme.getString("name");
                version = theme.getString("version");
                creator = theme.getString("creator");
                website = theme.getString("website");
                if(theme.has("donations")) {
                    donateURL = theme.getString("donations");
                }
                description = theme.getString("description");
                JSONArray jsonDevices = theme.getJSONArray("devices");
                for(int i=0; i<jsonDevices.length(); i++) {
                    devices.add(jsonDevices.getString(i));
                }
                JSONArray jsonSS = theme.getJSONArray("screenshots");
                for(int i=0; i<jsonSS.length(); i++) {
                    curr = new File(extractedDir, jsonSS.getString(i));
                    if(curr.exists()) {
                        screenshots.add(curr);
                    }
                }
                JSONObject themeData = theme.getJSONObject("themedata");
                Iterator iter1 = themeData.keys();
                while(iter1.hasNext()) {
                    String verStr = (String) iter1.next();
                    JSONObject verContent = themeData.getJSONObject(verStr);
                    if(verContent.has("alias")) {
                        controller.addAlias(verStr, verContent.getString("alias"));
                    } else {
                        VersionTheme verTheme = controller.addVersion(verStr);
                        Iterator iter2 = verContent.keys();
                        while(iter2.hasNext()) {
                            String catStr = (String) iter2.next();
                            JSONObject catContent = verContent.getJSONObject(catStr);
                            if(catContent.has("images")) {
                                JSONArray images = catContent.getJSONArray("images");
                                for(int i=0; i<images.length(); i++) {
                                    JSONObject currObj = images.getJSONObject(i);
                                    if(catStr.equals("wallpapers") && i==0) {
                                        if(currObj.has("file")) { //may be empty
                                            curr = new File(extractedDir, currObj.getString("file"));
                                            if(curr.exists()) {
                                                verTheme.wallpaper = curr;
                                            }
                                        }
                                    } else if(catStr.equals("app_icons")) {
                                        curr = new File(extractedDir, currObj.getString("file"));
                                        String dest = currObj.getString("path");
                                        if(dest.startsWith("/usr/palm/applications/")) {
                                            String appid = dest.replace("/usr/palm/applications/", "");
                                            String iconName = appid.substring(appid.indexOf("/")+1);
                                            if(iconName.equals("icon.png")) {
                                                appid = appid.substring(0, appid.indexOf("/"));
                                                if(curr.exists()) {
                                                    verTheme.icons.add(new IconEntry(appid, curr));
                                                }
                                            } else {
                                                if(curr.exists()) {
                                                    verTheme.files.add(new FileEntry(dest, curr, catStr));
                                                }
                                            }
                                        } else {
                                            if(curr.exists()) {
                                                verTheme.files.add(new FileEntry(dest, curr, catStr));
                                            }
                                        }
                                    } else {
                                        curr = new File(extractedDir, currObj.getString("file"));
                                        if(curr.exists()) {
                                            String dest = currObj.getString("path");
                                            verTheme.files.add(new FileEntry(dest, curr, catStr));
                                        }
                                    }
                                }
                            }
                            if(catContent.has("sounds")) {
                                JSONArray sounds = catContent.getJSONArray("sounds");
                                for(int i=0; i<sounds.length(); i++) {
                                    JSONObject currObj = sounds.getJSONObject(i);
                                    curr = new File(extractedDir, currObj.getString("file"));
                                    if(curr.exists()) {
                                        String dest = currObj.getString("path");
                                        verTheme.files.add(new FileEntry(dest, curr, catStr));
                                    }
                                }
                            }
                            if(catContent.has("patches")) {
                                JSONArray patches = catContent.getJSONArray("patches");
                                for(int i=0; i<patches.length(); i++) {
                                    JSONObject currObj = patches.getJSONObject(i);
                                    String filepath = currObj.getString("file");
                                    String testpath = filepath.substring(0, filepath.lastIndexOf("/")) + ".patch";
                                    curr = new File(extractedDir, testpath);
                                    if(curr.exists()) {
                                        verTheme.patches.add(new PatchEntry(curr, catStr));
                                    } else {
                                        curr = new File(extractedDir, filepath);
                                        if(curr.exists()) {
                                            verTheme.patches.add(new PatchEntry(curr, catStr));
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class DirFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }
}
