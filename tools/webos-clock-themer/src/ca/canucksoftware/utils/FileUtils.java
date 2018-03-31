
package ca.canucksoftware.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jdesktop.application.Application;

/**
 *
 * @author Jason
 */
public class FileUtils {
    public static File appDirectory() {
        File app, dir;
        try {
            app = new File(Application.getInstance()
                    .getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            dir = app.getParentFile();
        } catch(Exception e) {
            app = new File(Application.getInstance().getClass().getProtectionDomain()
                    .getCodeSource().getLocation()
                    .getPath().replaceAll("%20", " "));
            dir = app.getParentFile();
        }
        return dir;
    }

    public static void copy(File from, File to) throws IOException {
        FileInputStream fis  = new FileInputStream(from);
        FileOutputStream fos = new FileOutputStream(to);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }
    
    public static boolean delete(File path) {
        if(path.exists()) {
            File[] curr = path.listFiles();
            for(int i=0; i<curr.length; i++) {
                if(curr[i].isDirectory()) {
                    delete(curr[i]);
                } else {
                    curr[i].delete();
                }
            }
        }
        return(path.delete());
    }

    public static String getFilename(File file) {
        return getFilename(file.getName());
    }

    public static String getFilename(String file) {
        String name = file;
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

    public static String formatFilesize(File file) {
        return formatFilesize(file.length());
    }

    public static String formatFilesize(long size) {
        String result = "";
        if(size<1000) { //bytes
                result = size + "B";
        } else if((size/1024)<1000) { //kilobytes
                result = roundNicely(((double)size)/1024.0) + "KB";
        } else {
                result = roundNicely((((double)size)/1024.0)/1024.0) + "MB";
        }
        return result;
    }

    private static String roundNicely(double dbl) {
        long rounded = Math.round((dbl*100.0));
        String result = String.valueOf(((double)rounded)/100.0);
        if(dbl<10) {
                result = String.valueOf(Math.round(dbl*100.0)/100.0);
        } else if(dbl<100) {
                result = String.valueOf(Math.round(dbl*10.0)/10.0);
        } else {
                result = String.valueOf(((long)dbl));
        }
        if(result.endsWith(".0")) {
            result = result.replace(".0", "");
        }
        return result;
    }
}
