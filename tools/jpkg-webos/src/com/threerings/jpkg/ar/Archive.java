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
 */
package com.threerings.jpkg.ar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Formatter;

import org.apache.commons.io.IOUtils;

/**
 * Creates and works with Unix ar(1) archives.
 * @see <a href="http://en.wikipedia.org/wiki/Ar_%28Unix%29">The Wikipedia ar (Unix) entry</a>
 */
public class Archive
{
    /** The character encoding all text added to the archive will be translated into. */
    public static final String CHAR_ENCODING = "ASCII";

    /** The magic header which starts every ar archive. */
    public static final byte[] AR_MAGIC;

    public static final byte[] AR_GNU_HEADER;

    public static final byte[] AR_PALM_POSTINST;
    public static final byte[] AR_PALM_PRERM;

    /** The byte used to pad data. */
    public static final byte[] PADDING;

    /** The length of the header that appears before every file in the archive. */
    public static final int FILE_HEADER_LENGTH = 60;

    /** Initialize the AR_MAGIC header and PADDING byte arrays. */
    static {
        try {
            AR_MAGIC = "!<arch>\012".getBytes(CHAR_ENCODING);
            PADDING = "\012".getBytes(CHAR_ENCODING);
            AR_GNU_HEADER = "//                                              42        `\n".getBytes(CHAR_ENCODING);
            AR_PALM_POSTINST = "pmPostInstall.script/\012".getBytes(CHAR_ENCODING);
            AR_PALM_PRERM = "pmPreRemove.script/\012".getBytes(CHAR_ENCODING);
        } catch (final UnsupportedEncodingException uee) {
            throw new RuntimeException("Failed to initialize static byte arrays.", uee);
        }
    }

    /**
     * Construct a new archive at the supplied {@link File} path. If the path already exists and is
     * an ar(1) archive, open the archive for appending.
     * @throws InvalidMagicException If the file being appended to is an invalid ar(1) archive.
     * @throws IOException If any exception occurs during file i/o.
     */
    public Archive (File path, File postinst, File prerm)
            throws InvalidMagicException, IOException {
        this(path);
        _postinst = postinst;
        _prerm = prerm;
        _output.write(AR_GNU_HEADER);
        if(_postinst!=null) {
            _output.write(AR_PALM_POSTINST);
        }
        if(_prerm!=null) {
            _output.write(AR_PALM_PRERM);
        }
    }

    /**
     * Construct a new archive at the supplied {@link File} path. If the path already exists and is
     * an ar(1) archive, open the archive for appending.
     * @throws InvalidMagicException If the file being appended to is an invalid ar(1) archive.
     * @throws IOException If any exception occurs during file i/o.
     */
    public Archive (File path)
        throws InvalidMagicException, IOException
    {
        _path = path;
        _postinst = null;
        _prerm = null;

        // if the file already exists, and has content, verify it has the correct ar header.
        if (_path.exists() && _path.length() > 0) {
            InputStream input = null;
            try {
                input = new FileInputStream(_path);
                final byte[] header = new byte[AR_MAGIC.length];
                input.read(header, 0, AR_MAGIC.length);
                if (!Arrays.equals(header, AR_MAGIC)) {
                    throw new InvalidMagicException("Archive header is invalid: " + Arrays.toString(header));
                }

            } finally {
                IOUtils.closeQuietly(input);
            }
            _output = new FileOutputStream(_path, true);

        // otherwise create the file if necessary and write out the header.
        } else {
            if (!_path.exists()) {
                if (!_path.createNewFile()) {
                    throw new IOException("Unable to create archive file at " + _path.getAbsolutePath());
                }
            }

            _output = new FileOutputStream(_path);
            _output.write(AR_MAGIC);
        }
    }

    /**
     * Append the contents of the supplied {@link ArchiveEntry} to this archive.
     * @param entry The {@link ArchiveEntry} to add to the archive.
     * @throws PathnameTooLongException If the path name is too long for an ar(1) archive.
     * @throws PathnameInvalidException If the path name is invalid, e.g. contains a space.
     * @throws DataTooLargeException If the data contained in the entry is too large for an ar(1) archive.
     * @throws IOException If any exception occurs during data i/o.
     */
    public void appendEntry (ArchiveEntry entry)
        throws PathnameInvalidException, PathnameTooLongException, DataTooLargeException, IOException
    {
        // ar(1) only supports storing the file size as an integer. throw an exception if the
        // data is too large.
        if (entry.getSize() > Integer.MAX_VALUE) {
            throw new DataTooLargeException("Data being added to the archive is too large. " +
                "path=[" + entry.getPath() + "], size=[" + entry.getSize() + "].");
        }

        // add the entry header to the archive
        addEntryHeader(entry);

        // append the entry data to the archive
        final byte[] buffer = new byte[1024];
        int len;
        final InputStream input = entry.getInputStream();
        try {
            while ((len = input.read(buffer)) > 0) {
                _output.write(buffer, 0, len);
            }

        } finally {
            IOUtils.closeQuietly(input);
        }

        // pad the data section if necessary
        if (entry.getSize() % 2 != 0) {
            _output.write(PADDING);
        }
    }

    /**
     * Add a file header to the archive for the given ArchiveEntry.
     */
    private void addEntryHeader (ArchiveEntry entry)
        throws IOException, PathnameTooLongException, PathnameInvalidException
    {
        if (entry.getPath().getBytes(CHAR_ENCODING).length > 16) {
            throw new PathnameTooLongException("The supplied path name is too long: " + entry.getPath());
        }

        if (entry.getPath().contains(" ")) {
            throw new PathnameInvalidException("The path name cannot contain spaces: " + entry.getPath());
        }

        // set the file mtime to now
        final int mtime = (int)(System.currentTimeMillis() / 1000L);

        final StringBuffer buffer = new StringBuffer();
        final Formatter formatter = new Formatter(buffer);

        /*
         * Common file header format:
         * All data is stored in an ASCII representation
         * Fields 0-15:  Name        ASCII   (null-terminated)
         * Fields 16-27: Mod date    Integer (seconds since epoch)
         * Fields 28-33: Owner UID   Integer (gid)
         * Fields 34-39: Owner GID   Integer (uid)
         * Fields 40-47: File mode   Integer (octal)
         * Fields 48-57: File size   Integer (bytes)
         * Fields 58-59: File magic  Constant (\140\012)
         */
        formatter.format("%-16s%-12s%-6s%-6s%-8o%-10s%s%s",
            entry.getPath(), mtime, entry.getUserId(), entry.getGroupId(), entry.getMode(), entry.getSize(), '\140', '\012');

        _output.write(buffer.toString().getBytes(CHAR_ENCODING));
    }

    /**
     * Make sure the {@link OutputStream} gets closed.
     */
    @Override
    public void finalize ()
        throws Throwable
    {
        if(_postinst!=null && _prerm!=null) {
            appendEntry(new PalmScript(_postinst, 0));
            appendEntry(new PalmScript(_prerm, AR_PALM_POSTINST.length));
        } else {
            if(_postinst!=null) {
                appendEntry(new PalmScript(_postinst, 0));
            } else if(_prerm!=null) {
                appendEntry(new PalmScript(_prerm, 0));
            }
        }
        try {
            _output.close();

        } finally {
            super.finalize();
        }
    }

    /** The path to the archive being operated upon. */
    private final File _path;

    private File _postinst;
    private File _prerm;

    /** The output stream used to append data and files to this archive. */
    private final OutputStream _output;
}
