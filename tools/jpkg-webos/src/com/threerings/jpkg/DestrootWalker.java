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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.DirectoryWalker;

/**
 * Walks a given destroot appending all files and directories to a {@link PackageTarFile}.
 * Package private.
 * @see PackageTarFile
 */
class DestrootWalker extends DirectoryWalker
{
    /**
     * Construct a {@link DestrootWalker}.
     * @param destroot The {@link File} which is the root of the destroot.
     * @param tar The {@link PackageTarFile} which will have the destroot contents added to it.
     */
    public DestrootWalker (File destroot, PackageTarFile tar)
    {
        _destroot = destroot.getAbsoluteFile();
        _destrootPath = _destroot.getAbsolutePath();
        _tar = tar;
    }

    /**
     * Walk the destroot, adding the contents to the tar file. This method should not be called more
     * than once.
     * @throws IOException If any i/o error is encountered while walking the destroot.
     */
    public void walk ()
        throws IOException
    {
        walk(_destroot, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleDirectoryStart (File directory, int depth, Collection results)
        throws IOException
    {
        // no entry for the root directory
        if (depth == 0) {
            return;
        }

        addFile(directory);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleFile (File file, int depth, Collection results)
        throws IOException
    {
        addFile(file);
    }

    /**
     * Add a discovered {@link File} to the tar file.
     */
    private void addFile (File file)
        throws IOException
    {
        try {
            _tar.addFile(file, _destrootPath);
            
        // pass any encountered exception back as an IOException to conform to the DirectoryWalker
        // interface.
        } catch (final DuplicatePermissionsException dpe) {
            throw new IOException(dpe.getMessage());
        }
    }

    private final File _destroot;
    private final String _destrootPath;
    private final PackageTarFile _tar;
}
