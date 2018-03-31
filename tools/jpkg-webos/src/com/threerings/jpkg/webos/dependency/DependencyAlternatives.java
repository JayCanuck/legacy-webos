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

import java.util.Arrays;
import java.util.List;

/**
 * Holds a list of Dependency objects representing a list of packages that a given package must
 * depend on only one of.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-relationships.html#s-depsyntax">Debian Policy Manual</a>
 */
public class DependencyAlternatives
    implements ControlFileDependency
{
    /**
     * Construct a varargs list of alternative dependencies for a package to depend on.
     * @see #DependencyAlternatives(List)
     */
    public DependencyAlternatives (PackageDependency...dependencies)
    {
        this(Arrays.asList(dependencies));
    }

    /**
     * Construct a list of alternative dependencies for a package to depend on. The list indicates
     * that at least one of the packages must be installed in order for the dependency to be fulfilled.
     */
    public DependencyAlternatives (List<PackageDependency> dependencies)
    {
        _dependencies = dependencies;
    }

    // from ControlFileDependency
    public String asString ()
    {
        final StringBuilder builder = new StringBuilder();
        for (final PackageDependency dependency : _dependencies) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(dependency.asString());
        }
        return builder.toString();
    }

    /** The list of dependencies held by this alternatives class. */
    private final List<PackageDependency> _dependencies;
}
