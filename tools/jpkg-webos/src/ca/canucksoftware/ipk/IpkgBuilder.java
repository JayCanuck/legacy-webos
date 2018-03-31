/*
 * IpkgBuilder.java
 *
 * Copyright (c) 2010 Jason Robitaille
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright owner nor the names of contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ca.canucksoftware.ipk;

import com.threerings.jpkg.PackageBuilder;
import com.threerings.jpkg.webos.*;
import com.threerings.jpkg.webos.dependency.PackageDependency;
import java.io.BufferedReader;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * Ipkg builder class that can create webOS-compatible ipk files
 * @author Jason Robitaille
 */
public class IpkgBuilder {
    private File source;
    private File baseTemp;
    private File tempDir;
    private File destIpkg;
    private String name;
    private String id;
    private String version;
    private String author;
    private String email;
    private String arch;
    private ArrayList<String> depends;
    private String json;
    private File postinst;
    private File prerm;
    private File pmPostInstall;
    private File pmPreRemove;

    /**
     * Construct an IpkgBuilder object with the supplied data. The created ipk
     * file's name will be auto-generated.
     * @param dir Directory of items to be added to the ipk file.
     * @param path Filepath where the directory're contents will placed in. For
     * example, if you want a directory to have it's contents install to
     * "/media/cryptofs/apps/com.example.app/", then that would be the path
     * variable.
     */
    public IpkgBuilder(File dir, String path) {
        this(dir, path, null);
    }

    /**
     * Construct a fully populated IpkgBuilder with all required fields.
     * @param dir Directory of items to be added to the ipk file.
     * @param path Filepath where the directory're contents will placed in.
     * @param ipkg Secified file destination for the ipk file.
     */
    public IpkgBuilder(File dir, String path, File ipkg) {
        source = dir;
        baseTemp = new File(source.getParentFile(), "temp_ipk_dir");
        tempDir = new File(baseTemp, getFilepath(path));
        destIpkg = ipkg;
        name = "This is an unidentified ipkg.";
        id = "unknown.package";
        version = "1.0.0";
        author = "N/A";
        email = "nobody@example.com";
        depends = new ArrayList<String>();
        json = null;
        postinst = null;
        prerm = null;
        pmPostInstall = null;
        pmPreRemove = null;
        arch = "all";
    }

    private String getFilepath(String path) {
        if(!path.startsWith("/")) {
            path = "/" + path;
        }
        if(!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    /**
     * Sets the package name of the resulting ipkg
     */
    public void setPackageName(String name) {
        this.name = name;
    }

    /**
     * Sets the package ID for the resulting ipkg. IDs are unique per package
     * on webOS devices and are created from reverse DNS naming conventions.
     */
    public void setPackageID(String id) {
        this.id = id;
    }

    /**
     * Sets the package version of the resulting ipkg in N.N.N(-N) format.
     */
    public void setPackageVersion(String version) {
        this.version = version;
    }

    /**
     * Sets the package author's name.
     */
    public void setPackageAuthor(String author) {
        this.author = author;
        this.email = null;
    }
    
    /**
     * Sets the package author's name and author's email address.
     */
    public void setPackageAuthor(String author, String email) {
        this.author = author;
        this.email = email;
    }

    /**
     * Sets the package's source field from a standard JSONObject.
     * @see <a href="http://www.json.org/javadoc/org/json/JSONObject.html">JSONObject</a>
     * @see <a href="http://www.webos-internals.org/wiki/Packaging_Standards">WebOS-Internals Packaging Standards</a>
     */
    public void setPackageSource(JSONObject json) {
        setPackageSource(json.toString());
    }
    
    /**
     * Sets the package's source field from a basic string.
     */
    public void setPackageSource(String source) {
        this.json = source;
    }

    /**
     * Attempts to parse a specified JSON file for package id, package name,
     * package version, and package author. Follows the Palm webOS appinfo.json
     * guidelines.
     * @see <a href="http://developer.palm.com/index.php?option=com_content&view=article&id=1748&Itemid=43">Palm webOS appinfo.json</a>
     */
    public void parseFromJsonFile(File json) {
        try {
            String s = readFile(json);
            if(s.trim().length()!=0) {
                JSONObject jsonO = new JSONObject(s);
                if(jsonO.has("title")) {
                    name = jsonO.getString("title");
                }
                if(jsonO.has("id")) {
                    id = jsonO.getString("id");
                }
                if(jsonO.has("version")) {
                    version = jsonO.getString("version");
                }
                if(jsonO.has("vendor")) {
                    author = jsonO.getString("vendor");
                }
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void readFromControlFile(File control) {
    	try {
            BufferedReader input = new BufferedReader(new FileReader(control));
            String line = input.readLine();
            while(line!=null) {
                line = line.trim();
                if(line.length()>0)
                    if(line.startsWith("Package")) {
                    	setPackageID(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Description")) {
                    	setPackageName(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Version")) {
                    	setPackageVersion(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Architecture")) {
                    	setArch(line.substring(line.indexOf(":")+2));
                    } else if(line.startsWith("Depends")) {
                        String[] tokens = line.substring(line.indexOf(":")+2)
                                .split(",");
                        for(int i=0; i<tokens.length; i++) {
                            depends.add(tokens[i].trim());
                        }
                    } else if(line.startsWith("Maintainer")) {
                    	String[] tokens = line.split("<");
                    	if(tokens.length==1) {
                    		setPackageAuthor(line);
                    	} else {
                    		setPackageAuthor(tokens[0].trim(),
                    				tokens[1].replaceAll(">", "").trim());
                    	}
                    } else if(line.startsWith("Source")) {
                    	setPackageSource(line.substring(line.indexOf(":")+2));
                    }
                line = input.readLine();
            }
        } catch(Exception e) {}
    }

    private String readFile(File f) throws IOException {
        String out = "";
        String line = null;
        BufferedReader br = new BufferedReader(new FileReader(f));
        line = br.readLine();
        while(line!=null) {
            out += line.trim();
            line = br.readLine();
            if(line!=null) {
                out += " ";
            }
        }
        br.close();
        return out;
    }

    /**
     * Specifies the package's post-install script.
     */
    public void setPostinst(File script) {
        postinst = script;
    }

    /**
     * Specifies the package's pre-removal script.
     */
    public void setPrerm(File script) {
        prerm = script;
    }

    /**
     * Specifies the package's palm-style post-install script.
     */
    public void setPalmPostinst(File script) {
        pmPostInstall = script;
    }

    /**
     * Specifies the package's palm-style pre-removal script.
     */
    public void setPalmPrerm(File script) {
        pmPreRemove = script;
    }

    /**
     * Sets the target achitecture of this package.
     * @param architecture A valid architecture: "all", "armv6", "armv7" or "i686"
     */
    public void setArch(String architecture) {
        arch = architecture;
    }

    /**
     * Sets the dependencies for this package.
     * @param depends Array of package IDs of packages that need too be
     * installed on the user's device before this package can be installed.
     */
    public void setDepends(String[] depends) {
        this.depends = null;
        this.depends = new ArrayList<String>(Arrays.asList(depends));
    }

   /* private String getFilename(File f) {
        String result = f.getName();
        if(result.lastIndexOf("/")!=-1) {
            result = result.substring(result.lastIndexOf("/")+1);
        }
        if(result.lastIndexOf("\\")!=-1) {
            result = result.substring(result.lastIndexOf("\\")+1);
        }
        return result;
    }*/

    private void copyDir(File src, File dest) throws IOException {
	if (src.isDirectory()) 	{
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String list[] = src.list();
            for (int i = 0; i < list.length; i++) {
                File dest1 = new File(dest, list[i]);
                File src1 = new File(src, list[i]);
                copyDir(src1 , dest1);
            }
	} else {
            if(!dest.getParentFile().isDirectory()) {
                dest.getParentFile().mkdirs();
            }
            copyFile(src, dest);
	}
    }

    private void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
             // magic number for Windows, 64Mb - 32Kb)
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            while (position < size)
                position += inChannel.transferTo(position, maxCount, outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    private boolean deleteDirectory(File path) {
        if(path.exists()) {
            File[] curr = path.listFiles();
            for(int i=0; i<curr.length; i++) {
                if(curr[i].isDirectory()) {
                    deleteDirectory(curr[i]);
                } else {
                    curr[i].delete();
                }
            }
        }
        return(path.delete());
    }

    /**
     * Builds the ipk file. Any left out information will either be omitted or
     * filled in by default values.
     */
    public void build() {
        try {
            //create package info
            PackageInfo pInfo;
            PackageName pName = new PackageName(id);
            PackageVersion pVer = new PackageVersion(version);
            PackageDescription pDesc = new PackageDescription(name);
            PackageMaintainer pMain = new PackageMaintainer(author, email);
            PackageArchitecture pArch = new PackageArchitecture(arch);
            PackageSection pSect = new PackageSection("misc");
            PackagePriority pPrior = PackagePriority.OPTIONAL;
            if(json==null) {
                json = "";
            }
            PackageSource pSource = new PackageSource(json);
                pInfo = new PackageInfo(pName, pVer, pArch, pMain,
                        pDesc, pSect, pPrior, pSource);
            if(postinst!=null) {
                pInfo.setMaintainerScript(new FileMaintainerScript(
                        MaintainerScript.Type.POSTINST, postinst));
            }
            if(prerm!=null) {
                pInfo.setMaintainerScript(new FileMaintainerScript(
                        MaintainerScript.Type.PRERM, prerm));
            }
            if(pmPostInstall!=null) {
                pInfo.setPalmScript(pmPostInstall, MaintainerScript.Type.POSTINST);
            }
            if(pmPreRemove!=null) {
                pInfo.setPalmScript(pmPreRemove, MaintainerScript.Type.PRERM);
            }
            for(int i=0; i<depends.size(); i++) {
                pInfo.addDependency(new PackageDependency(depends.get(i)));
            }
            //get destination ipkg file if not specified
            if(destIpkg==null) {
                destIpkg = new File(source.getParentFile(), id + "_" +
                        version + "_all.ipk");
            }
            if(destIpkg.exists()) {
                destIpkg.delete();
            }
            //copy files
            copyDir(source, tempDir);
            //build ipkg file
            PackageBuilder builder = new WebOSPackageBuilder(pInfo);
            builder.write(destIpkg, baseTemp);
            deleteDirectory(baseTemp);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
