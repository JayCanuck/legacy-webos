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

import java.util.ArrayList;
import java.util.List;

import com.threerings.jpkg.webos.ControlFileData;

/**
 * A generic class which holds {@link ControlFileDependency} objects. These can be used to describe
 * various Debian package dependency relationships, such as Depends, Conflicts, and Replaces.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-relationships.html#s-binarydeps">Debian Policy Manual</a>
 */
public abstract class DependencyContainer
    implements ControlFileData
{
    /**
     * Returns the number of dependencies defined.
     */
    public int size ()
    {
        return _dependencies.size();
    }

    /**
     * Concrete classes may us this to add a generic dependency object to this list of dependencies.
     */
    protected void add (ControlFileDependency dependency)
    {
        _dependencies.add(dependency);
    }

    // from ControlFileData
    public abstract String getField ();

    // from ControlFileData
    public String getFieldValue ()
    {
        final StringBuilder builder = new StringBuilder();
        for (final ControlFileDependency dependency : _dependencies) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(dependency.asString());
        }
        return builder.toString();
    }

    /** The list of dependencies. */
    private final List<ControlFileDependency> _dependencies = new ArrayList<ControlFileDependency>();
}
