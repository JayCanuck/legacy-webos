/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.canucksoftware.themebuilder;

/**
 *
 * @author Jason
 */
public class AliasVersion {
    public String version;
    public String realVersion;

    public AliasVersion() {
        version = null;
        realVersion = null;
    }
    public AliasVersion(String ver, String real) {
        version = ver;
        realVersion = real;
    }
    @Override
    public String toString() {
        return version + " -> " + realVersion;
    }
}
