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
 *      -- added "i686", "armv7", and "armv6" architectures
 *      -- removed unneeded architectures
 *      -- renamed WebOSArchitectures
 */
package com.threerings.jpkg.webos;
/**
 * Known Debian architectures used by the Debian packaging system.
 * Generated from Debian dpkg-architecture version 1.13.25. on 2008-02-26T19:13:50-08:00.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Architecture">Debian Policy Manual</a>
 */
public enum WebOSArchitectures
{
    /** Indicates a package available for building on any architecture. */
    ANY ("any"),
    /** Indicates an architecture-independent package. */
    ALL ("all"),
    /** Indicates a source package. */
    SOURCE ("source"),

    /** The i686 architecture. */
    I686 ("i686"),
    /** The arm architecture. */
    ARMV6 ("armv6"),
    /** The arm architecture. */
    ARMV7 ("armv7");

    WebOSArchitectures (String name)
    {
        _name = name;
    }

    /** Returns the string name for this architecture */
    public String getName ()
    {
        return _name;
    }

    private final String _name;
}
