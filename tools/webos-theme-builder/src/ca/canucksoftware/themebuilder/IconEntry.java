package ca.canucksoftware.themebuilder;

import java.io.File;

/**
 *
 * @author Jason
 */
public class IconEntry {
    public String appID;
    public File image;
    
    public IconEntry() {
        appID = null;
        image = null;
    }
    public IconEntry(String appID, File image) {
        this.appID = appID;
        this.image = image;
    }
    public String dest() {
       return "/usr/palm/applications/" + appID + "/icon.png";
    }
    public String zipResourcePath() {
        return "files/app_icons" + dest();
    }
    public String toString() {
        return image.toString() + " " + zipResourcePath();
    }
}
