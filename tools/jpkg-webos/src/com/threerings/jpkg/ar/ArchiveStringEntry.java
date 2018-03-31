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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.threerings.jpkg.UnixStandardPermissions;

/**
 * An {@link ArchiveEntry} where the entry data is a held in a string.
 */
public class ArchiveStringEntry
    implements ArchiveEntry
{
    /**
     * Construct an {@link ArchiveStringEntry} with the supplied string as the entry data and the
     * supplied path name as the entry path in the archive. The entry will be owned by the root
     * user and group and have standard permissions.
     * @param data The string data for the entry.
     * @param path The path name in the archive.
     * @see UnixStandardPermissions#ROOT_USER
     * @see UnixStandardPermissions#ROOT_GROUP
     * @see UnixStandardPermissions#STANDARD_FILE_MODE
     */
    public ArchiveStringEntry (String data, String path)
    {
        _data = data;
        _path = path;
    }

    // from ArchiveEntry
    public InputStream getInputStream ()
    {
        return new ByteArrayInputStream(dataAsBytes());
    }

    // from ArchiveEntry
    public long getSize ()
    {
        return dataAsBytes().length;
    }

    // from ArchiveEntry
    public String getPath ()
    {
        return _path;
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
     * Return the string data as a byte array encoded for an {@link Archive}
     */
    private byte[] dataAsBytes ()
    {
        return _data.getBytes();
    }

    /** The entry data, contained in a String. */
    private final String _data;

    /** The entry path name. */
    private final String _path;
}
