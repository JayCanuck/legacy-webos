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

import java.util.regex.Pattern;

/**
 * Holds and parses the Debian package name.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Package">Debian Policy Manual</a>
 */
public class PackageName
    implements ControlFileData
{
    /**
     * Construct a new package name.
     * ControlDataInvalidException will be throw if the name is invalid.
     * @throws ControlDataInvalidException
     */
    public PackageName (String name)
        throws ControlDataInvalidException
    {
        _name = validateName(name);
    }

    // from ControlFileData
    public String getField ()
    {
        return "Package";
    }

    // from ControlFileData
    public String getFieldValue ()
    {
        return _name;
    }

    /**
     * Validate the supplied package name.
     */
    private String validateName (String name)
        throws ControlDataInvalidException
    {
        if (name.length() < 2) {
            throw new ControlDataInvalidException(
                "Package name must be at least 2 characters long. name=[" + name + "]");
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new ControlDataInvalidException(
                "Package name must match the pattern. name=[" + name + "] pattern=[" + NAME_PATTERN.pattern() + "]");
        }

        return name;
    }

    /**
     * The regex against which the package name must match.
     * From: <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Package">Debian Policy Manual</a>
     * "Package names must consist only of lower case letters (a-z), digits (0-9),
     * plus (+) and minus (-) signs, and periods (.). They must be at least two characters long
     * and must start with an alphanumeric character."
     */
    private static final Pattern NAME_PATTERN =
        Pattern.compile("[\\p{Lower}\\p{Digit}][\\p{Lower}\\p{Digit}+-.]+");

    /** The package name. */
    private final String _name;
}
