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
 * Contains various standard Unix permissions.
 */
public enum UnixStandardPermissions
{
    /** The standard Unix root user information. */
    ROOT_USER ("root", 0),
    /** The standard Unix root group information. */
    ROOT_GROUP ("wheel", 0);

    UnixStandardPermissions (String name, int id)
    {
        _name = name;
        _id = id;
    }

    /** The standard Unix filesystem permissions for a normal file. */
    public static final int STANDARD_FILE_MODE = 0100644;

    /** The standard Unix filesystem permissions for an executable file. */
    public static final int EXECUTABLE_FILE_MODE = 0100755;

    /** The standard Unix filesystem permissions for a directory. */
    public static final int STANDARD_DIR_MODE = 040755;

    /**
     * The user or group name for this permission.
     */
    public String getName ()
    {
        return _name;
    }

    /**
     * The user or group id for this permission.
     */
    public int getId ()
    {
        return _id;
    }

    private final String _name;
    private final int _id;
}
