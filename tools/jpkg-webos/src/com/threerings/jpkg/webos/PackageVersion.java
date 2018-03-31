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
 * Holds and parses the Debian package version.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Version">Debian Policy Manual</a>
 */
public class PackageVersion
    implements ControlFileData
{
    /**
     * Construct a fully populated PackageVersion object with the "upstream_version" field set
     * to the supplied value.
     * The supplied string may only contain alphanumeric characters, ".", "+", and "~".
     * The Debian version and epoch fields of the version field will not be set, meaning they
     * will have the default values assigned to them by the dpkg tools.
     * If the supplied string does not match these rules a ControlDataInvalidException will be
     * thrown.
     * @throws ControlDataInvalidException
     */
    public PackageVersion (String upstream_version)
        throws ControlDataInvalidException
    {
        this(upstream_version, DEFAULT_DEBIAN_VERSION, DEFAULT_EPOCH);
    }

    /**
     * Construct a fully populated PackageVersion object with the "upstream_version",
     * "debian_version", and "epoch" set.
     * The upstream version may only contain alphanumeric characters, ".", "+", "-", ":", and "~".
     * The Debian version may only contain alphanumeric characters, "+", ".", and "~".
     * It is conventional to start the Debian version at 1.
     * Epoch must be 0 or greater. 0 is a safe default and indicates no epoch.
     * If the supplied string does not match these rules a ControlDataInvalidException will be
     * thrown.
     * @throws ControlDataInvalidException
     */
    public PackageVersion (String upstream_version, String debian_version, int epoch)
        throws ControlDataInvalidException
    {
        _upstreamVersion = validateUpstreamVersion(upstream_version);
        _debianVersion = validateDebianVersion(debian_version);
        _epoch = validateEpoch(epoch);
    }

    // from ControlFileData
    public String getField ()
    {
        return "Version";
    }

    // from ControlFileData
    public String getFieldValue ()
    {
        final StringBuilder builder = new StringBuilder();
        if (_epoch != DEFAULT_EPOCH) {
            builder.append(_epoch).append(':');
        }
        builder.append(_upstreamVersion);
        if (!_debianVersion.equals(DEFAULT_DEBIAN_VERSION)) {
            builder.append('-').append(_debianVersion);
        }
        return builder.toString();
    }

    /**
     * Validate the supplied package upstream version.
     */
    private String validateUpstreamVersion (String version)
        throws ControlDataInvalidException
    {
        if (!UPSTREAM_PATTERN.matcher(version).matches()) {
            throw new ControlDataInvalidException(
                "Upstream version must match the pattern. version=[" + version + "] pattern=[" + UPSTREAM_PATTERN.pattern() + "]");
        }

        return version;
    }

    /**
     * Validate the supplied package Debian version.
     */
    private String validateDebianVersion (String version)
        throws ControlDataInvalidException
    {
        if (!DEBIAN_PATTERN.matcher(version).matches()) {
            throw new ControlDataInvalidException(
                "Debian version must match the pattern. version=[" + version + "] pattern=[" + DEBIAN_PATTERN.pattern() + "]");
        }

        return version;
    }

    /**
     * Validate the supplied package Epoch.
     */
    private int validateEpoch (int epoch)
        throws ControlDataInvalidException
    {
        if (!(epoch >= 0)) {
            throw new ControlDataInvalidException(
                "Epoch must be equal to or greater than 0. epoch=[" + epoch + "]");
        }

        return epoch;
    }

    /**
     * The regex against which the package upstream version must match.
     * From: <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Version">Debian Policy Manual</a>
     * "The upstream_version may contain only alphanumerics and the characters . + - : ~
     * (full stop, plus, hyphen, colon, tilde) and should start with a digit." If the Debian version
     * is set, the upstream version may not contain a "-" and if the epoch is set, it may not contain
     * a ":". We will simply never allow it to contain either of these characters for clarity.
     */
    private static final Pattern UPSTREAM_PATTERN = Pattern.compile("[\\p{Digit}][\\p{Alnum}.+~]*");

    /**
     * The regex against which the package upstream version must match.
     * From: <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Version">Debian Policy Manual</a>
     * "This part of the version number specifies the version of the Debian package based on the
     * upstream version. It may contain only alphanumerics and the characters + . ~
     * (plus, full stop, tilde) and is compared in the same way as the upstream_version is."
     */
    private static final Pattern DEBIAN_PATTERN = Pattern.compile("[\\p{Alnum}+.~]+");

    /** The Debian project recommended default value for the Debian version. */
    private static final String DEFAULT_DEBIAN_VERSION = "1";

    /** The default value for the epoch, indicating no epoch. */
    private static final int DEFAULT_EPOCH = 0;

    /** The package upstream version. */
    private final String _upstreamVersion;

    /** The package Debian version. */
    private final String _debianVersion;

    /** The package epoch. */
    private final int _epoch;
}
