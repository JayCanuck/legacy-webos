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
package com.threerings.jpkg.webos.dependency;

/**
 * Holds a single Debian package dependency, such as a Depends, Conflicts, or Replaces dependency.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-relationships.html#s-binarydeps">Debian Policy Manual</a>
 */
public abstract class AbstractDependency
    implements ControlFileDependency
{
    /**
     * Construct a new package dependency. The target package will depend on the supplied package's
     * name, but not a specific version of that package.
     * NOTE: No validation is performed on the package name.
     */
    public AbstractDependency (String name)
    {
        _dependency = name;
    }

    /**
     * Construct a new package dependency. The target package will depend on the supplied package's
     * name, as well as the supplied relationship to the supplied version,such as equals to
     * version 1.1.
     * NOTE: No validation is performed on the package name or package version.
     */
    public AbstractDependency (String name, String version, DependencyRelationships relationship)
    {
        _dependency = name + " (" + relationship.getOperator() + " " + version + ")";
    }

    // from ControlFileDependency
    public String asString ()
    {
        return _dependency;
    }

    /** The dependency expressed as a string. */
    private final String _dependency;
}
