/*
 * Jpkg - Java library and tools for operating system package creation.
 *
 * Copyright (c) 2007 Three Rings Design, Inc.
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
 *
 *
 * Modified January 16, 2010 by Jason Robitaille:
 *      -- Added more contructors
 *      -- Added support for an optional source field
 */
package com.threerings.jpkg.webos;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.internet.InternetHeaders;

import com.threerings.jpkg.PathPermissions;
import com.threerings.jpkg.PermissionsMap;
import com.threerings.jpkg.webos.dependency.DependencyAlternatives;
import com.threerings.jpkg.webos.dependency.PackageConflict;
import com.threerings.jpkg.webos.dependency.PackageConflicts;
import com.threerings.jpkg.webos.dependency.PackageDependencies;
import com.threerings.jpkg.webos.dependency.PackageDependency;
import com.threerings.jpkg.webos.dependency.PackageReplacement;
import com.threerings.jpkg.webos.dependency.PackageReplacements;
import java.io.File;

/**
 * Stores meta information needed to create a new Debian package.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html">Debian Policy Manual</a>
 */
public class PackageInfo
{
    /** The default package section. */
    public static final PackageSection DEFAULT_SECTION = new PackageSection("misc");

    /** The default package priority. */
    public static final PackagePriority DEFAULT_PRIORITY = PackagePriority.OPTIONAL;

    /**
     * Construct a {@link PackageInfo} object with the supplied data.
     * Default values will be set for the package section and priority.
     * @see PackageInfo#DEFAULT_SECTION
     * @see PackageInfo#DEFAULT_PRIORITY
     */
    public PackageInfo (PackageName name, PackageVersion version, PackageArchitecture architecture,
                        PackageMaintainer maintainer, PackageDescription description)
    {
        this(name, version, architecture, maintainer, description, DEFAULT_SECTION, DEFAULT_PRIORITY, null);
    }

    public PackageInfo (PackageName name, PackageVersion version, PackageArchitecture architecture,
                        PackageMaintainer maintainer, PackageDescription description,
                        PackageSource source)
    {
        this(name, version, architecture, maintainer, description, DEFAULT_SECTION, DEFAULT_PRIORITY, source);
    }

    public PackageInfo (PackageName name, PackageVersion version, PackageArchitecture architecture,
                        PackageMaintainer maintainer, PackageDescription description,
                        PackageSection section, PackagePriority priority)
    {
        this(name, version, architecture, maintainer, description, DEFAULT_SECTION, DEFAULT_PRIORITY, null);
    }

    /**
     * Construct a fully populated {@link PackageInfo} with all required fields.
     */
    public PackageInfo (PackageName name, PackageVersion version, PackageArchitecture architecture,
                        PackageMaintainer maintainer, PackageDescription description,
                        PackageSection section, PackagePriority priority, PackageSource source)
    {
        _name = name;
        _version = version;
        _section = section;
        _priority = priority;
        _architecture = architecture;
        _maintainer = maintainer;
        _description = description;
        _source = source;
        _pmPostInstall = null;
        _pmPreRemove = null;

        assertValidFields();
    }

    /**
     * Add a {@link PathPermissions} object associated with a given path.
     * @see PermissionsMap#addPathPermissions(String, PathPermissions)
     * @throws InvalidPathException If the supplied path is invalid.
     */
    public void addPathPermissions (String path, PathPermissions permissions)
    {
        _permissions.addPathPermissions(path, permissions);
    }

    /**
     * Add a package dependency for this package.
     */
    public void addDependency (PackageDependency dependency)
    {
        _dependencies.addDependency(dependency);
    }

    /**
     * Add a dependency alternative for this package.
     */
    public void addDependencyAlternative (DependencyAlternatives alternative)
    {
        _dependencies.addAlternative(alternative);
    }

    /**
     * Add a package conflict for this package.
     */
    public void addConflict (PackageConflict conflict)
    {
        _conflicts.addConflict(conflict);
    }

    /**
     * Add a package replacement for this package.
     */
    public void addReplacement (PackageReplacement replacement)
    {
        _replacements.addReplacement(replacement);
    }

    /**
     * Add the defined package data to an RFC822 formatted header formatted for a Debian package
     * control file.
     * Order of fields determined from:
     * <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-binarycontrolfiles">Debian Policy Manual</a>
     */
    public InternetHeaders getControlHeaders ()
    {
        final InternetHeaders headers = new InternetHeaders();

        headers.addHeader(_name.getField(), _name.getFieldValue());
        headers.addHeader(_version.getField(), _version.getFieldValue());

        headers.addHeader(_section.getField(), _section.getFieldValue());
        headers.addHeader(_priority.getField(), _priority.getFieldValue());
        headers.addHeader(_architecture.getField(), _architecture.getFieldValue());
        if (_dependencies.size() > 0) {
            headers.addHeader(_dependencies.getField(), _dependencies.getFieldValue());
        }
        if (_conflicts.size() > 0) {
            headers.addHeader(_conflicts.getField(), _conflicts.getFieldValue());
        }
        if (_replacements.size() > 0) {
            headers.addHeader(_replacements.getField(), _replacements.getFieldValue());
        }
        headers.addHeader(_maintainer.getField(), _maintainer.getFieldValue());
        headers.addHeader(_description.getField(), _description.getFieldValue());
        if(_source!=null) {
            headers.addHeader(_source.getField(), _source.getFieldValue());
        }

        return headers;
    }

    /**
     * Return the PermissionsMap modifying the package referred to by this PackageInfo.
     */
    public PermissionsMap getPermissionsMap ()
    {
        return _permissions;
    }

    /**
     * Sets a maintainer script for this package, replacing any existing script for that type.
     */
    public void setMaintainerScript (MaintainerScript script)
    {
        _scripts.put(script.getType(), script);
    }

     public void setPalmScript (File script, MaintainerScript.Type type)
    {
        if(type == MaintainerScript.Type.POSTINST) {
            _pmPostInstall = script;
        } else if(type == MaintainerScript.Type.PRERM) {
            _pmPreRemove = script;
        }
    }

    public File getPostinst() {
        FileMaintainerScript fms = (FileMaintainerScript) _scripts.get(
                MaintainerScript.Type.POSTINST);
        File result = null;
        if(fms!=null) {
            result = fms.getFile();
        }
        return result;
    }

    public File getPrerm() {
        FileMaintainerScript fms = (FileMaintainerScript) _scripts.get(
                MaintainerScript.Type.PRERM);
        File result = null;
        if(fms!=null) {
            result = fms.getFile();
        }
        return result;
    }

    public File getPalmScript(MaintainerScript.Type type) {
        File result = null;
        if(type == MaintainerScript.Type.POSTINST) {
            result = _pmPostInstall;
        } else if(type == MaintainerScript.Type.PRERM) {
            result = _pmPreRemove;
        }
        return result;
    }

    /**
     * Returns the MaintainerScripts defined for this package.
     */
    public Map<MaintainerScript.Type, MaintainerScript> getMaintainerScripts ()
    {
        return _scripts;
    }

    @Override // from Object
    public String toString ()
    {
        final StringBuilder builder = new StringBuilder();
        @SuppressWarnings("unchecked")
        final
        Enumeration<String> en = getControlHeaders().getAllHeaderLines();
        while (en.hasMoreElements())
        {
            builder.append(en.nextElement()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Validates that none of the required fields were null.
     */
    private void assertValidFields ()
    {
        if (_name == null) throw new IllegalArgumentException("The PackageName cannot be null.");
        if (_version == null) throw new IllegalArgumentException("The PackageVersion cannot be null.");
        if (_section == null) throw new IllegalArgumentException("The PackageSection cannot be null.");
        if (_priority == null) throw new IllegalArgumentException("The PackagePriority cannot be null.");
        if (_architecture == null) throw new IllegalArgumentException("The PackageArchitecture cannot be null.");
        if (_maintainer == null) throw new IllegalArgumentException("The PackageMaintainer cannot be null.");
        if (_description == null) throw new IllegalArgumentException("The PackageDescription cannot be null.");
    }

    /** The package name. */
    private final PackageName _name;

    /** The package version. */
    private final PackageVersion _version;

    /** The package section, e.g. "web". */
    private final PackageSection _section;

    /** The package priority, e.g. "optional". */
    private final PackagePriority _priority;

    /** The package architecture, e.g. "i386". */
    private final PackageArchitecture _architecture;

    /** A list of packages this package depends on. */
    private final PackageDependencies _dependencies = new PackageDependencies();

    /** A list of packages this package conflicts with. */
    private final PackageConflicts _conflicts = new PackageConflicts();

    /** A list of packages this package replaces. */
    private final PackageReplacements _replacements = new PackageReplacements();

    /** Permissions to apply to paths contained within this package. */
    private final PermissionsMap _permissions = new PermissionsMap();

    /** The package maintainer, e.g. "Package maintainer &lt;maintainer@corp.com&gt;". */
    private final PackageMaintainer _maintainer;

    /** A description for this package. */
    private final PackageDescription _description;

    /** A json source field for this package. */
    private final PackageSource _source;

    /** Palm-style  */
    private File _pmPostInstall;
    private File _pmPreRemove;

    /** The maintainer scripts to run with this package. */
    private final Map<MaintainerScript.Type, MaintainerScript> _scripts =
        new TreeMap<MaintainerScript.Type, MaintainerScript>();
}
