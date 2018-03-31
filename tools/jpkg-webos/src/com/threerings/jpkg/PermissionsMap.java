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
package com.threerings.jpkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Maps arbitrary permissions onto associated paths.
 */
public class PermissionsMap
{
    /**
     * Add a {@link PathPermissions} object associated with a given path. This method guarantees that there
     * will only be one {@link PathPermissions} object applied to a given path. Calling this method more than
     * once for the same path will replace any permissions already set for that path. The path will
     * be normalized.
     * @throws InvalidPathException If the supplied path is invalid.
     */
    public void addPathPermissions (String path, PathPermissions permissions)
    {
        _permissions.put(PathUtils.normalize(path), permissions);
    }

    /**
     * Return a {@link Set} of {@link Entry} objects holding paths and the {@link PathPermissions} object
     * associated with that path.
     */
    public Set<Entry<String, PathPermissions>> getPermissions ()
    {
        return _permissions.entrySet();
    }

    /**
     * Return a {@link PathPermissions} object for the supplied path if it exists, null otherwise.
     */
    public PathPermissions getPathPermissions (String path)
    {
        return _permissions.get(path);
    }

    @Override // from Object
    public String toString ()
    {
        final StringBuilder builder = new StringBuilder();
        for (final Entry<String, PathPermissions> entry : getPermissions()) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append("path=[").append(entry.getKey()).append("], permission=[").append(entry.getValue()).append("].");
        }
        return builder.toString();
    }

    /** Mapping of path to the permissions for that path. */
    Map<String, PathPermissions> _permissions = new HashMap<String, PathPermissions>();
}
