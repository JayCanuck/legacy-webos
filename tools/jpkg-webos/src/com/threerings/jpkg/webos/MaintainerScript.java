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
 * Modified March 19, 2010 by Jason Robitaille:
 *      -- updated filename output to include "./" prefix
 */
package com.threerings.jpkg.webos;

import java.io.IOException;
import java.io.InputStream;

/**
 * Holds Debian maintainer script types and the {@link InputStream} with the script content.
 */
public interface MaintainerScript
{
    /**
     * The types of Debian maintainer scripts and the file names they should have.
     */
    public enum Type {
        /** The preinst maintainer script. */
        PREINST ("preinst"),
        /** The postinst maintainer script. */
        POSTINST ("postinst"),
        /** The prerm maintainer script. */
        PRERM ("prerm"),
        /** The postrm maintainer script. */
        POSTRM ("postrm");

        Type (String name) {
            _name = name;
        }

        /**
         * Returns the filename used for this maintainer script in the control tar file.
         */
        public String getFilename ()
        {
            return "./" + _name;
        }

        /** The filename for this maintainer script in the control tar file. */
        private final String _name;
    }

    /**
     * Returns the {@link Type} of script this instance is.
     */
    public Type getType ();

    /**
     * Returns the {@link InputStream} with the script contents for this type.
     */
    public InputStream getStream () throws IOException;

    /**
     * Returns the number of bytes of data in the {@link InputStream}.
     */
    public long getSize ();
}
