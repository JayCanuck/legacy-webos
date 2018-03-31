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
import java.io.IOException;
import java.io.InputStream;

/**
 * An object which represents data which can be added to an {@link Archive}.
 */
public interface ArchiveEntry
{
    /**
     * Returns an {@link InputStream} from the data contained in this entry.
     */
    public InputStream getInputStream () throws IOException;

    /**
     * Returns the size of the data contained in this entry in bytes. An {@link Archive} can only
     * store data whose size can be expressed in a 32 bit integer however the interface allows
     * size to be expressed as a long to support for example {@link File} objects. The
     * {@link Archive} class will guarantee that the size returned by this method can be converted
     * safely into a 32 bit integer.
     */
    public long getSize ();

    /**
     * Returns the path name used to identify this entry in the archive.
     * Must be 15 characters or less.
     */
    public String getPath ();

    /**
     * Returns the user id which will own this entry in the archive.
     */
    public int getUserId ();

    /**
     * Returns the group id which will own this entry in the archive.
     */
    public int getGroupId ();

    /**
     * Returns the file permissions mode (e.g. 0644) for this entry in the archive.
     */
    public int getMode ();
}
