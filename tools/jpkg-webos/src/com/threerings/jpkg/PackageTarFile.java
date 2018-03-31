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
 * Modified March 18, 2010 by Jason Robitaille:
 *      -- added a fix to adjust for Windows filepath format
 *          (replaces all back-slashes with forward-slashes and removes
 *           leading drive name and leading slash)
 *      -- hardcoded File.separator to '/'
 */
package com.threerings.jpkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import com.threerings.jpkg.ar.ArchiveEntry;
import javax.swing.JOptionPane;

/**
 * A wrapper around TarOutputStream to handle adding files from a destroot into a tar file.
 * Every regular file will have its md5 checksum recorded and the total amount of file data in
 * kilobytes will be stored.
 */
public class PackageTarFile
    implements ArchiveEntry
{
    /**
     * Convenience constructor to create {@link PackageTarFile} with an empty {@link PermissionsMap}.
     * @see PackageTarFile#PackageTarFile(File, PermissionsMap)
     */
    public PackageTarFile (File safeTemp)
        throws IOException
    {
        this(safeTemp, new PermissionsMap());
    }

    /**
     * Initialize a PackageTar file.
     * @param safeTemp A location with enough free space to hold the tar data.
     * @param permissions A {@link PermissionsMap} will be used to manipulate entries before being
     * added to the tar file.
     * @throws IOException If the tar file cannot be initialized due to i/o errors.
     */
    public PackageTarFile (File safeTemp, PermissionsMap permissions)
        throws IOException
    {
        _permissions = permissions;
        _tar = File.createTempFile("jpkgtmp", ".tar.gz", safeTemp);

        _tarOut = new TarOutputStream(new GZIPOutputStream(new FileOutputStream(_tar)));
        _tarOut.setLongFileMode(TarOutputStream.LONGFILE_GNU);
    }

    /**
     * Add the contents of the supplied directory to the tar file. The root of the directory path
     * will be stripped from all entries being added to the tar file.
     * e.g. If /root is supplied: /root/directory/file.txt -> directory/file.txt
     */
    public void addDirectory (File directory)
        throws IOException
    {
        final DestrootWalker walker = new DestrootWalker(directory, this);
        walker.walk();
    }

    /**
     * Add directories and files to the tar archive without stripping a leading path.
     * @see PackageTarFile#addFile(File, String)
     */
    public void addFile (File file)
        throws DuplicatePermissionsException, IOException
    {
        addFile(file, NO_STRIP_PATH);
    }

    /**
     * Add directories and files to the tar archive. All file paths are treated as absolute paths.
     * @param file The {@link File} to add to the tar archive.
     * @param stripPath The path to stripped from the start of any entry path before adding it to
     * the tar file.
     * @throws InvalidPathException If the supplied stripPath path cannot be normalized.
     * @throws DuplicatePermissionsException If more than one permission in the defined
     * {@link PermissionsMap} maps to the file being added.
     * @throws IOException If any i/o exceptions occur when appending the file data.
     */
    public void addFile (File file, String stripPath)
        throws DuplicatePermissionsException, IOException
    {
        String path = stripPath.replaceAll("\\\\", "/");
        if(path.indexOf("/")>-1) {
            path = path.substring(path.indexOf("/")+1);
        }
        // normalize the strip path and remove any leading /'s
        final String normalizedStripPath = PathUtils.stripLeadingSeparators(PathUtils.normalize(path));
        
        // use the file to initialize the TarEntry, and then override various properties.
        final TarEntry entry = new TarEntry(file.getAbsoluteFile());

        // normalize the entry path
        entry.setName(PathUtils.normalize(entry.getName()));
        // if the entry path includes the strip path, remove it.
        final String entryPath = entry.getName();
        if (entryPath.startsWith(normalizedStripPath)) {
            final String stripped = entryPath.substring(normalizedStripPath.length());
            // be extra sure that the modified path has no leading separators so that the entry
            // does not expand into the root.
            entry.setName(PathUtils.stripLeadingSeparators(stripped));
        }

        // set standard permission modes
        if (file.isDirectory()) {
            // set the entry size to 0 if this is a directory
            entry.setSize(0);
            entry.setMode(UnixStandardPermissions.STANDARD_DIR_MODE);

        } else if (file.isFile()) {
            entry.setMode(UnixStandardPermissions.STANDARD_FILE_MODE);
        }

        // configure the permissions in the entry.
        setEntryPermissions(entry, file);

        // verify that the directory entries have trailing /'s which may have been removed
        // during normalization
        if (file.isDirectory()) {
            final StringBuffer currentPath = new StringBuffer(entry.getName());
            if (currentPath.charAt(currentPath.length() - 1) != '/') {
                currentPath.append('/');
            }
            entry.setName(currentPath.toString());
        }

        entry.setName("./" + entry.getName());

        // write out the tar entry header.
        _tarOut.putNextEntry(entry);

        // insert the file data into the tar and calculate the md5 checksum for any regular file.
        if (file.isFile()) {
            handleRegularFile(file, entry);
        }

        _tarOut.closeEntry();
    }

    /**
     * Closes the tar file. This must be called to create a valid tar file.
     */
    public void close ()
        throws IOException
    {
        _tarOut.close();
    }

    /**
     * Deletes the tar file. Returns true if the file was deleted, false otherwise.
     */
    public boolean delete ()
    {
        return _tar.delete();
    }

    /**
     * Return the map of tar entry paths to md5 checksums for regular files added to this tar file.
     */
    public Map<String, String> getMd5s ()
    {
        return _md5s;
    }

    /**
     * Return the total amount of file data added to this tar file, in kilobytes.
     * If the supplied data is less than a kilobyte, 1 is returned.
     */
    public long getTotalDataSize ()
    {
        return _totalSize;
    }

    // from ArchiveEntry
    public InputStream getInputStream ()
        throws IOException
    {
        return new FileInputStream(_tar);
    }

    // from ArchiveEntry
    public long getSize ()
    {
        return _tar.length();
    }

    // from ArchiveEntry
    public String getPath ()
    {
        return AR_DATA_FILE;
    }

    // from ArchiveEntry
    public int getUserId ()
    {
        return UnixStandardPermissions.ROOT_USER.getId();
    }

    // from ArchiveEntry
    public int getGroupId ()
    {
        return UnixStandardPermissions.ROOT_GROUP.getId();
    }

    // from ArchiveEntry
    public int getMode ()
    {
        return UnixStandardPermissions.STANDARD_FILE_MODE;
    }

    /**
     * Set the permissions in the TarEntry, applying any matches from the PermissionsMap.
     * @throws DuplicatePermissionsException
     */
    private void setEntryPermissions (TarEntry entry, File file)
        throws DuplicatePermissionsException
    {
        // default permissions to root
        entry.setNames(UnixStandardPermissions.ROOT_USER.getName(),
            UnixStandardPermissions.ROOT_GROUP.getName());
        entry.setIds(UnixStandardPermissions.ROOT_USER.getId(),
            UnixStandardPermissions.ROOT_GROUP.getId());

        // apply any permissions map to this entry to modify permissions. the first permission
        // encountered will be applied. if any additional permissions are encountered that also
        // match, a DuplicatePermissionsException will be thrown.
        // NOTE: this should eventually be built into a tree that maps the filesystem and allows
        // for nested recursive permissions to override earlier recursive permissions in a
        // reasonable manner.
        // TODO: Use Google collections to filter the permissions list down to the one applicable
        // permission, or throw the exception if more than one is found.
        boolean permissionFound = false;
        for (final Entry<String, PathPermissions> permEntry : _permissions.getPermissions()) {
            // since the entries in the tar file have any leading / stripped, the permission paths
            // must also have the / stripped.
            final String permPath = PathUtils.stripLeadingSeparators(permEntry.getKey());

            final String entryPath = entry.getName();
            final PathPermissions permissions = permEntry.getValue();

            // if the permission path does not match, continue to the next permission
            if (!permissionMatches(permPath, entryPath, permissions.isRecursive())) {
                continue;
            }

            // if we have already applied a permission to this path, throw an exception.
            if (permissionFound) {
                throw new DuplicatePermissionsException(
                    "A permission already mapped to this file. Refusing to apply another. path=[" + file.getAbsolutePath() + "].");
            }

            // apply the permission to the entry.
            entry.setNames(permissions.getUser(), permissions.getGroup());
            entry.setIds(permissions.getUid(), permissions.getGid());
            entry.setMode(permissions.getMode());
            permissionFound = true;
        }
    }

    /**
     * Handles adding a regular file {@link File} object to the tar file. This includes
     * calculating and recording the md5 checksum of the file data.
     */
    private void handleRegularFile (File file, TarEntry entry)
        throws FileNotFoundException, IOException
    {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream input = null;
            try {
                input = new FileInputStream(file);
                final byte[] buf = new byte[1024];
                int len;
                while ((len = input.read(buf)) > 0) {
                    _tarOut.write(buf, 0, len);
                    md.update(buf, 0, len);
                }

            } finally {
                IOUtils.closeQuietly(input);
            }

            _md5s.put(entry.getName(), new String(Hex.encodeHex(md.digest())));

        } catch (final NoSuchAlgorithmException nsa) {
            throw new RuntimeException("md5 algorthm not found.", nsa);
        }

        // record the kilobyte size of this file in the total file data count
        _totalSize += bytesToKilobytes(file.length());
    }

    /**
     * Convert bytes into kilobytes. If the supplied bytes are less than a kilobyte, 1 is returned.
     */
    private long bytesToKilobytes (long bytes)
    {
        if (bytes <= FileUtils.ONE_KB) {
            return 1;
        }
        return bytes / FileUtils.ONE_KB;
    }

    /**
     * Determine if the supplied permission path matches the supplied entry path.
     */
    private boolean permissionMatches (String permPath, String entryPath, boolean recursive)
    {
        // don't match if this permission path is not part of the entry.
        if (!entryPath.startsWith(permPath)) {
            return false;
        }

        // match if the permission matches the path exactly.
        if ((permPath.equals(entryPath))) {
            return true;
        }

        // or match if the permission is recursive, and the entry is inside of the recursive path.
        if (recursive && entryPath.charAt(permPath.length()) == '/') {
            return true;
        }

        // otherwise the permission did not match.
        return false;
    }

    /** The name of the data file in the Debian package. */
    private static final String AR_DATA_FILE = "data.tar.gz/";

    /** Used to indicate that the file being added should have nothing stripped from its path. */
    private static final String NO_STRIP_PATH = "";

    /** The PermissionsMap to be applied to this tar file. */
    private final PermissionsMap _permissions;

    /** An md5 map for every regular file entry in the tar file. */
    private final Map<String, String> _md5s = new HashMap<String, String>();

    /** The amount of file data added to the tar file, stored in kilobytes. */
    private long _totalSize;

    /** The file location of the tar file. */
    private final File _tar;

    /** Used to write the tar file to the file system. */
    private final TarOutputStream _tarOut;
}
