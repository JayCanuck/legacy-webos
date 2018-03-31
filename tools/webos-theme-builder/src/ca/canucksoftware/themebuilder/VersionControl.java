
package ca.canucksoftware.themebuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jason
 */
public class VersionControl {
    private LinkedList<VersionTheme> versions;
    private LinkedList<AliasVersion> aliases;

    public VersionControl() {
        versions = new LinkedList<VersionTheme>();
        aliases = new LinkedList<AliasVersion>();
    }

    public VersionTheme addVersion(String version) {
        VersionTheme newVersion = new VersionTheme(version);
        versions.add(newVersion);
        return newVersion;
    }

    public AliasVersion addAlias(String version, String realVersion) {
        AliasVersion alias = new AliasVersion(version, realVersion);
        aliases.add(alias);
        return alias;
    }

    public List<String> list() { return list(true); }

    public List<String> list(boolean includeAliases) {
        LinkedList<String> allVersions = new LinkedList<String>();
        for(int i=0; i<versions.size(); i++) {
            allVersions.add(versions.get(i).version);
        }
        if(includeAliases) {
            for(int i=0; i<aliases.size(); i++) {
                allVersions.add(aliases.get(i).toString());
            }
        }
        Collections.sort(allVersions);
        return allVersions;
    }

    public VersionTheme getVersion(String version) {
        VersionTheme result = null;
        for(int i=0; i<versions.size(); i++) {
            if(versions.get(i).version.equals(version)) {
                result = versions.get(i);
            }
        }
        return result;
    }
    
    public LinkedList<VersionTheme> listVersions() { return versions; }
    public LinkedList<AliasVersion> listAliases() { return aliases; }

    public void removeVersion(String version) {
        for(int i=0; i<versions.size(); i++) {
            if(versions.get(i).version.equals(version)) {
                versions.remove(i);
                break;
            }
        }
    }

    public void removeAlias(String alias) {
        for(int i=0; i<aliases.size(); i++) {
            if(aliases.get(i).toString().equals(alias)) {
                aliases.remove(i);
                break;
            }
        }
    }
    public String toString() {
        String output = "Aliases:\n";
        for(int i=0; i<aliases.size(); i++) {
            output += "-" + aliases.get(i) + "\n";
        }
        output += "Versions:\n";
        for(int i=0; i<versions.size(); i++) {
            output += "-" + versions.get(i) + "\n";
        }
        return output;
    }
}
