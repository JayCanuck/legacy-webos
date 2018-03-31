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
 * Holds and parses the Debian package description.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Description">Debian Policy Manual</a>
 */
public class PackageDescription
    implements ControlFileData
{
    /**
     * Construct a new PackageDescription with the supplied text for the brief initial part of the
     * description. This is recommended to be under 80 characters. If the string contains any
     * new lines or tabs, a ControlDataInvalidException will be thrown.
     * @throws ControlDataInvalidException
     */
    public PackageDescription (String description)
        throws ControlDataInvalidException
    {
        _shortDesc = validateWhitespace(description);
        _extendedDesc = new StringBuilder();
    }

    // from ControlFileData
    public String getField ()
    {
        return "Description";
    }

    // from ControlFileData
    public String getFieldValue ()
    {
        if (_extendedDesc.length() > 0) {
            return _shortDesc + "\n" + _extendedDesc.toString();

        } else {
            return _shortDesc;
        }
    }

    /**
     * Append a line of text to the extended package description. This line will be word wrapped by
     * any Debian tool displaying the package description.
     * If the line contains any new lines or tabs, a ControlDataInvalidException will be thrown.
     */
    public void addParagraph (String line)
        throws ControlDataInvalidException
    {
        validateWhitespace(line);
        addNewLineIfNeeded();
        // new paragraphs must begin with a blank space.
        _extendedDesc.append(' ').append(line);
    }

    /**
     * Append a line of text to the extended package description. This line will be NOT be word
     * wrapped by any Debian tool displaying the package description.
     * If the line contains any new lines or tabs, a ControlDataInvalidException will be thrown.
     */
    public void addVerbatimParagraph (String line)
        throws ControlDataInvalidException
    {
        validateWhitespace(line);
        addNewLineIfNeeded();
        // new verbatim lines must begin with two blank spaces.
        _extendedDesc.append(' ').append(' ').append(line);
    }

    /**
     * Append a blank new line to the extended description.
     */
    public void addBlankLine ()
    {
        addNewLineIfNeeded();
        _extendedDesc.append(" .");
    }

    /**
     * Validates that the supplied string does not contain spaces or tabs.
     */
    private String validateWhitespace (String text)
        throws ControlDataInvalidException
    {
        if (text.contains("\n") || text.contains("\t")) {
            throw new ControlDataInvalidException(
                "Description text cannot contain newlines or tabs. text=[" + text + "].");
        }
        return text;
    }

    /**
     * Adds a new line character to the extended description if needed.
     */
    private void addNewLineIfNeeded ()
    {
        if (_extendedDesc.length() > 0) {
            _extendedDesc.append("\n");
        }
    }

    /** The package short description, should be less than 80 characters. */
    private final String _shortDesc;

    /** The package extended description. */
    private final StringBuilder _extendedDesc;
}
