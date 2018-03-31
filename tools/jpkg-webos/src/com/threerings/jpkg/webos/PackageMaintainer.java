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
 * Modified January 15, 2010 by Jason Robitaille:
 *      -- changed verification routines
 *      -- made email address optional
 */
package com.threerings.jpkg.webos;

import java.util.regex.Pattern;

/**
 * Holds and parses the Debian package maintainer.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Maintainer">Debian Policy Manual</a>
 */
public class PackageMaintainer
    implements ControlFileData
{
    /**
     * Construct a new PackageMaintainer. The maintainer's name will be verified to not
     * contain a period.
     * @throws ControlDataInvalidException
     */
    public PackageMaintainer (String name) throws ControlDataInvalidException {
        this(name, null);
    }

    /**
     * Construct a new PackageMaintainer. The address will be parsed and an exception thrown
     * if it is not a valid RFC822 address. The maintainer's name will also be verified to not
     * contain a period.
     * @throws ControlDataInvalidException
     */
    public PackageMaintainer (String name, String address)
        throws ControlDataInvalidException
    {
        _name = validateName(name);
        if(address!=null) {
            _address = validateEmail(address);
        } else {
            _address = address;
        }
    }

    // from ControlFileData
    public String getField ()
    {
        return "Maintainer";
    }

    // from ControlFileData
    public String getFieldValue ()
    {
        String result = null;
        if(_address!=null) {
            result = _name + " <" + _address.toString() + ">";
        } else {
            result = _name;
        }
        return result;
    }

    /**
     * Validates the maintainer's name. If it contains a period, throw an exception.
     * From: <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Maintainer">Debian Policy Manual</a>
     * "If the maintainer's name contains a full stop then the whole field will not work directly
     * as an email address due to a misfeature in the syntax specified in RFC822; a program using
     * this field as an address must check for this and correct the problem if necessary
     * (for example by putting the name in round brackets and moving it to the end, and bringing
     * the email address forward)."
     */
    private String validateName (String name)
        throws ControlDataInvalidException
    {
        //if (name.contains(".")) {
        //    throw new ControlDataInvalidException(
        //        "Maintainer name may not contain a period. name=[" + name + "].");
        //}

        return name;
    }

    /**
     * Validate the email address is RFC822 complaint.
     * @return the String email converted into an Address.
     */
    public String validateEmail(String address) throws ControlDataInvalidException {
        if (!(Pattern.matches("[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})", address.toLowerCase()))) {
            throw new ControlDataInvalidException("Email is not a valid RFC822 address. email=[" + address + "]");
        }
        return address;
    }

    /** The maintainer's name. */
    private final String _name;

    /** The maintainer's RFC822 formatted email address. */
    private final String _address;
}
