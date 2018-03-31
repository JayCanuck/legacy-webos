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


/**
 * Simple data class to contain permissions for paths used in a PermissionsMap.
 */
public class PathPermissions
{
    /**
     * Create a new permissions map which will be owned by the default user/group, e.g. root but
     * have the supplied file mode set.
     * @param mode the file permission mode, in represented octal, e.g. 0644.
     * @param recursive whether to apply this permission recursively down the path.
     */
    public PathPermissions (int mode, boolean recursive)
    {
        this(UnixStandardPermissions.ROOT_USER.getName(), UnixStandardPermissions.ROOT_GROUP.getName(),
            mode, recursive);
    }

    /**
     * Create a new permissions map with the given user and group owner.
     * @param mode the file permission mode, in represented octal, e.g. 0644.
     * @param recursive whether to apply this permission recursively down the path.
     */
    public PathPermissions (String user, String group, int mode, boolean recursive)
    {
        _user = user;
        _group = group;
        _uid = UnixStandardPermissions.ROOT_USER.getId();
        _gid = UnixStandardPermissions.ROOT_GROUP.getId();
        _mode = mode;
        _recursive = recursive;
    }

    /**
     * Create a new permissions map with the given uid and gid owner.
     * @param mode the file permission mode, represented in octal, e.g. 0644.
     * @param recursive whether to apply this permission recursively down the path.
     */
    public PathPermissions (int uid, int gid, int mode, boolean recursive)
    {
        _user = UnixStandardPermissions.ROOT_USER.getName();
        _group = UnixStandardPermissions.ROOT_GROUP.getName();
        _uid = uid;
        _gid = gid;
        _mode = mode;
        _recursive = recursive;
    }

    /**
     * The username to set as owner for this path.
     */
    public String getUser ()
    {
        return _user;
    }

    /**
     * The groupname to set as owner for this path.
     */
    public String getGroup ()
    {
        return _group;
    }

    /**
     * The user id to set as owner for this path.
     */
    public int getUid ()
    {
        return _uid;
    }

    /**
     * The group id to set as owner for this path.
     */
    public int getGid ()
    {
        return _gid;
    }

    /**
     * The file permission mode..
     */
    public int getMode ()
    {
        return _mode;
    }

    /**
     * Whether this permission map applies recursively down the path.
     */
    public boolean isRecursive ()
    {
        return _recursive;
    }

    @Override // from Object
    public String toString ()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("user=[").append(_user).append("], ");
        builder.append("group=[").append(_group).append("], ");
        builder.append("userId=[").append(_uid).append("], ");
        builder.append("groupId=[").append(_gid).append("], ");
        builder.append("mode=[").append(Integer.toOctalString(_mode)).append("], ");
        builder.append("recursive=[").append(_recursive).append("].");
        return builder.toString();
    }

    private final String _user;
    private final String _group;
    private final int _uid;
    private final int _gid;
    private final int _mode;
    private final boolean _recursive;
}
