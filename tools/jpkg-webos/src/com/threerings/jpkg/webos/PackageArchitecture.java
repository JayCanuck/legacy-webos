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
package com.threerings.jpkg.webos;

/**
 * Holds and parses the Debian package architecture.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Architecture">Debian Policy Manual</a>
 */
public class PackageArchitecture
    implements ControlFileData
{
    /**
     * Construct a new PackageArchitecture using an architecture defined in the DebianArchitecture
     * enum.
     */
    public PackageArchitecture (WebOSArchitectures architecture)
    {
        _architecture = architecture.getName();
    }

    /**
     * Construct a new PackageArchitecture object with an arbitrary string representing the
     * architecture this package is to be installed on.
     * NOTE: Users of this class are strongly encouraged to use the constructor for this class
     * which uses the DebianArchitecture enum which was generated from the dpkg tools and should
     * contain an exhaustive list of valid Debian architectures.
     */
    public PackageArchitecture (String architecture)
    {
        _architecture = architecture;
    }

    // from ControlFileData
    public String getField ()
    {
        return "Architecture";
    }

    // from ControlFileData
    public String getFieldValue ()
    {
        return _architecture;
    }

    /** The package architecture. */
    private final String _architecture;
}
