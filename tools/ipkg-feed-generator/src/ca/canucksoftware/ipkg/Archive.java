
package ca.canucksoftware.ipkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jason
 */
public class Archive {
    private static final int FILE_HEADER_LENGTH = 60;
    private final byte[] AR_MAGIC;
    private File archive;

    public Archive(File f) {
        archive = f;
        try {
            AR_MAGIC = "!<arch>\012".getBytes("ASCII");
        } catch(Exception e) {
            throw new RuntimeException("Failed to initialize static byte arrays.", e);
        }
    }

    public void extract(String entry, File out) throws FileNotFoundException, IOException {
        byte[] header = new byte[60];
        FileInputStream fis = new FileInputStream(archive);
        fis.skip(AR_MAGIC.length);
        while(fis.read(header)==60) {
            String headerText = new String(header);
            String name = headerText.substring(0, 16).trim();
            String sizeText = headerText.substring(48, 58).trim();
            int size = Integer.parseInt(sizeText);
            if(name.equals(entry) || name.equals(entry + "/")) { //this is the wanted entry
                byte[] buffer = new byte[1024];
                FileOutputStream fos = new FileOutputStream(out);
                int lengthRead = 0;
                int test = fis.read();
                if(test!=10) {
                    fos.write(test);
                }
                while((lengthRead = fis.read(buffer)) > 0) {
                    if(size>=1024) {
                        fos.write(buffer, 0, lengthRead);
                        size -= 1024;
                    } else {
                        if(lengthRead<size) {
                            System.err.print("Error: Archive entry " + name +  " is " +
                                    "truncated.");
                            fos.write(buffer, 0, lengthRead);
                        } else {
                            fos.write(buffer, 0, size);
                        }
                        break; //done reading entry
                    }
                }
                fos.flush();
                fos.close();
                break;
            } else {
                fis.skip(size);
            }
        }
        fis.close();
    }
}
