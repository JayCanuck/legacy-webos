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
 *      -- changed control file location in tar to "./control"
 *          (from "control")
 *      -- md5sums file no long added to tar
 */
package com.threerings.jpkg.webos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import javax.mail.internet.InternetHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import com.threerings.jpkg.PackageTarFile;
import com.threerings.jpkg.UnixStandardPermissions;
import com.threerings.jpkg.ar.ArchiveEntry;

/**
 * Handles the creation of the Debian package control.tar.gz file.
 */
public class ControlFile
    implements ArchiveEntry
{
    /**
     * Construct a new ControlFile which creates the contents of control.tar.gz entry in the
     * Debian package.
     * @param info The fully populated package meta data.
     * @param dataTar The fully populated {@link PackageTarFile} represented by this control file.
     * @throws IOException If any i/o exceptions occur during the control file creation.
     * @throws ScriptDataTooLargeException If any maintainer script is too large to be added to the tar file.
     */
    public ControlFile (PackageInfo info, PackageTarFile dataTar)
        throws IOException, ScriptDataTooLargeException
    {
        _controlData = createTarArray(info, dataTar);
    }

    // from ArchiveEntry
    public InputStream getInputStream ()
    {
        return new ByteArrayInputStream(_controlData);
    }

    // from ArchiveEntry
    public long getSize ()
    {
        return _controlData.length;
    }

    // from ArchiveEntry
    public String getPath ()
    {
        return AR_CONTROL_FILE;
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
     * Create the control.tar.gz file as a byte array.
     */
    private byte[] createTarArray (PackageInfo info, PackageTarFile dataTar)
        throws IOException, ScriptDataTooLargeException
    {
        // this file will never be big, so do all the work in memory.
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final TarOutputStream controlTar = new TarOutputStream(new GZIPOutputStream(output));
        controlTar.setLongFileMode(TarOutputStream.LONGFILE_GNU);

        try {
            // construct the tar file.
            addControlFile(controlTar, info, dataTar);
            //addMd5Sums(controlTar, dataTar);
            addMaintainerScripts(controlTar, info);

        } finally {
            controlTar.close();
        }

        return output.toByteArray();
    }

    /**
     * Add the control file to the tar file.
     */
    private void addControlFile (TarOutputStream tar, PackageInfo info, PackageTarFile dataTar)
        throws IOException
    {
        // setup the RFC822 formatted header used for package metadata.
        final InternetHeaders headers = info.getControlHeaders();

        final StringBuilder controlFile = new StringBuilder();
        @SuppressWarnings("unchecked")
        final
        Enumeration<String> en = headers.getAllHeaderLines();
        while (en.hasMoreElements())
        {
            controlFile.append(en.nextElement()).append('\n');
        }

        final TarEntry entry = standardEntry(CONTROL_FILE, UnixStandardPermissions.STANDARD_FILE_MODE, controlFile.length());
        tar.putNextEntry(entry);
        IOUtils.write(controlFile.toString(), tar);
        tar.closeEntry();
    }

    /**
     * Add the maintainer scripts to the tar file.
     */
    private void addMaintainerScripts (TarOutputStream tar, PackageInfo info)
        throws IOException, ScriptDataTooLargeException
    {
        for (final MaintainerScript script : info.getMaintainerScripts().values()) {
            if (script.getSize() > Integer.MAX_VALUE) {
                throw new ScriptDataTooLargeException(
                    "The script data is too large for the tar file. script=[" + script.getType().getFilename() + "].");
            }

            final TarEntry entry = standardEntry(script.getType().getFilename(), UnixStandardPermissions.EXECUTABLE_FILE_MODE, (int)script.getSize());
            tar.putNextEntry(entry);
            IOUtils.copy(script.getStream(), tar);
            tar.closeEntry();
        }
    }

    /**
     * Returns a TarEntry object with correct default values.
     */
    private TarEntry standardEntry (String name, int mode, int size)
    {
        final TarEntry entry = new TarEntry(name);
        entry.setNames(UnixStandardPermissions.ROOT_USER.getName(), UnixStandardPermissions.ROOT_GROUP.getName());
        entry.setIds(UnixStandardPermissions.ROOT_USER.getId(), UnixStandardPermissions.ROOT_GROUP.getId());
        entry.setSize(size);
        entry.setMode(mode);

        return entry;
    }

    /** The name of the control file in the Debian package. */
    private static final String AR_CONTROL_FILE = "control.tar.gz/";

    /** Constants for entries in the control file. */
    private static final String CONTROL_FILE = "./control";

    /** The control.tar.gz data is held in this byte array after creation. */
    private final byte[] _controlData;
}
