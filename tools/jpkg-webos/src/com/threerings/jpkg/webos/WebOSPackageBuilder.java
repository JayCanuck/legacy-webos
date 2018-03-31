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
 * Modified March 18, 2010 by Jason Robitaille:
 *      -- renamed WebOSPackageBuilder
 */
package com.threerings.jpkg.webos;

import java.io.File;
import java.io.IOException;

import com.threerings.jpkg.PackageBuilder;
import com.threerings.jpkg.PackageBuilderException;
import com.threerings.jpkg.PackageTarFile;
import com.threerings.jpkg.ar.Archive;
import com.threerings.jpkg.ar.ArchiveEntry;
import com.threerings.jpkg.ar.ArchiveException;
import com.threerings.jpkg.ar.ArchiveStringEntry;

/**
 * Creates Debian package files.
 */
public class WebOSPackageBuilder
    implements PackageBuilder
{
    public WebOSPackageBuilder (PackageInfo info)
    {
        _info = info;
    }

    // from PackageBuilder
    public void write (File dest, File destroot)
        throws PackageBuilderException, IOException
    {
        if (dest == null) throw new IllegalArgumentException("The destination cannot be null.");
        if (destroot == null) throw new IllegalArgumentException("The destroot cannot be null.");

        PackageTarFile dataTar = null;
        try {
            // create the temporary data.tar.gz file in the destination location, which we assume
            // has enough available space to construct both the data.tar.gz and the package.
            dataTar = new PackageTarFile(dest.getParentFile(), _info.getPermissionsMap());
            dataTar.addDirectory(destroot);
            dataTar.close();

            // Overwrite any file at the destination location.
            if (dest.exists()) {
                if (!dest.delete()) {
                    throw new PackageBuilderException(
                        "Unable to overwrite existing package destination. path=[" + dest.getAbsolutePath() + "].");
                }
            }

            // create the ar(1) archive which is the package itself
            try {
                final Archive archive;
                File postinst = _info.getPalmScript(MaintainerScript.Type.POSTINST);
                File prerm = _info.getPalmScript(MaintainerScript.Type.PRERM);
                if(postinst!=null || prerm!=null) {
                    archive = new Archive(dest, postinst, prerm);
                } else {
                    archive = new Archive(dest);
                }
                
                // add the standard header to the package
                final ArchiveEntry entry = new ArchiveStringEntry(AR_MAGIC_CONTENTS, AR_MAGIC_FILE);
                archive.appendEntry(entry);

                // add the control.tar.gz file to the package
                final ControlFile control = new ControlFile(_info, dataTar);
                archive.appendEntry(control);

                // add the data.tar.gz file to the package
                archive.appendEntry(dataTar);

                archive.finalize();
            } catch (final ArchiveException ae) {
                throw new PackageBuilderException(ae);

            } catch (final ScriptDataTooLargeException sdtle) {
                throw new PackageBuilderException(sdtle);
            } catch (final Throwable e) {}

        } finally {
            dataTar.delete();
        }
    }

    /** Data used for the package magic file. */
    private static final String AR_MAGIC_FILE = "debian-binary/";
    private static final String AR_MAGIC_CONTENTS = "2.0\n";

    /** The meta information used to create this package. */
    private final PackageInfo _info;
}
