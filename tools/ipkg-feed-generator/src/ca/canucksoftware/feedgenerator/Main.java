
package ca.canucksoftware.feedgenerator;

import ca.canucksoftware.ipkg.Archive;
import ca.canucksoftware.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Jason Robitaille
 */
public class Main {
    private static File directory;
    private static File packages;

    public static void main(String[] args) {
        try {
            directory = FileUtils.appDirectory();
            //try {
            //    System.setErr(new PrintStream(new FileOutputStream(new File(
            //            directory, "feed.log"), true)));
            //} catch (FileNotFoundException e) {}
            packages = new File(directory, "Packages");
            processFiles();
            gzipFeed();
        } catch(Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processFiles() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(packages));
        File[] items = directory.listFiles();
        for(int i=0; i<items.length; i++) {
            if(items[i].getName().toLowerCase().endsWith(".ipk")) {
                writeEntry(bw, items[i]);
            }
        }
        bw.flush();
        bw.close();
    }

    private static void writeEntry(BufferedWriter bw, File f) throws FileNotFoundException,
            IOException {
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        File controlTar = new File(tmpFilePath, "control.tar.gz");
        if(controlTar.exists()) {
            controlTar.delete();
        }
        Archive ipk = new Archive(f);
        ipk.extract("control.tar.gz", controlTar);
        ControlParser control = new ControlParser(controlTar);
        if(control.validPackage()) {
            bw.write("Package: " + control.getId() + "\n");
            bw.write("Version: " + control.getVersion() + "\n");
            if(control.getDepends()!=null) {
                bw.write("Depends: " + control.getDepends() + "\n");
            }
            bw.write("Section: " + control.getSection() + "\n");
            bw.write("Architecture: " + control.getArchitecture() + "\n");
            bw.write("MD5Sum: " + FileUtils.getMD5Sum(f) + "\n");
            bw.write("Size: " + f.length() + "\n");
            bw.write("Filename: " + FileUtils.getFilename(f) + "\n");
            if(control.palmPackaged()) {
                File dataTar = new File(tmpFilePath, "data.tar.gz");
                if(dataTar.exists()) {
                    dataTar.delete();
                }
                ipk.extract("data.tar.gz", dataTar);
                AppInfoParser appinfo = new AppInfoParser(dataTar, control.getId());
                bw.write("Description: " + appinfo.getTitle() + "\n");
                bw.write("Maintainer: " + appinfo.getVendor() + "\n");
            } else {
                bw.write("Description: " + control.getDescription() + "\n");
                bw.write("Maintainer: " + control.getMaintainer() + "\n");
            }
            bw.write("Source: " + control.getSource() + "\n");
            bw.write("\n\n");
        }
    }
    
    private static void gzipFeed() throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(packages);
        GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(
                new File(directory, "Packages.gz")));
        byte[] buffer = new byte[1024];
        int length = 0;
        while((length = fis.read(buffer)) > 0) {
            gos.write(buffer, 0, length);
        }
        fis.close();
        gos.flush();
        gos.finish();
        gos.close();
    }
}
