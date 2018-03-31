/*
 * Jpkg - Java library and tools for operating system package creation.
 *
 * Copyright (c) 2007 Three Rings Design, Inc.
 * All rights reserved.
 * Copyright (c) 2004, Regents of the University of California
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
 *      -- added Windows filepath backslash-to-slash conversion
 */
package com.threerings.jpkg;

import java.io.File;

/**
 * Small utility class for working with file paths.
 */
public class PathUtils
{
    /**
     * Normalize a path by removing all relative attributes, such as ./.. and stripping
     * any separators from the start and end of the path.
     */
    public static String normalize (String path)
    {
        //account for Windows systems
        path = path.replace(File.separatorChar, '/');

        /* Create a buffer in which we can normalize the Path. */
        final StringBuilder trimPath = new StringBuilder();

        /* Remove any leading or trailing whitespace from the Path. */
        trimPath.append(path.trim());

        /* Determine the length of the Path. */
        int len = trimPath.length();
        int lastSlash = -1;
        int lastLastSlash = -1;

        for (int i = 0; i < len; i++) {
            assert len == trimPath.length();

            /* Remove any double slashes created by concatenating partial
             * normalized paths together. */
            if (i < len - 1 &&
                    trimPath.charAt(i) == '/' &&
                    trimPath.charAt(i + 1) == '/')
            {
                trimPath.deleteCharAt(i);
                len--;
                i--;
                continue;
            }

            /* Remove ./ */
            if (i < len - 1 &&
                    i == lastSlash + 1 &&
                    trimPath.charAt(i) == '.' &&
                    trimPath.charAt(i + 1) == '/')
            {
                trimPath.delete(i, i + 2);
                len -= 2;
            }

            /* Remove xxx/../ and ../../[xxx] */
            if (i < len - 2 &&
                    i == lastSlash + 1 &&
                    trimPath.charAt(i) == '.' &&
                    trimPath.charAt(i + 1) == '.' &&
                    trimPath.charAt(i + 2) == '/')
            {
                if (lastLastSlash >= 0) {
                    trimPath.delete(lastLastSlash, i + 2);
                    len -= (i + 2 - lastLastSlash);
                    i = lastLastSlash;
                    lastSlash = trimPath.lastIndexOf("/", lastLastSlash - 1);
                } else {
                    /* First ../ at start of path. Attempted traversal beyond
                     * the path -- we strip off the '..' and leave '/' */
                    trimPath.delete(i, i + 2);
                    len -= (i + 2);
                    lastSlash = 0;
                    lastLastSlash = 0;
                }
            }

            if (trimPath.charAt(i) == '/') {
                lastLastSlash = lastSlash;
                lastSlash = i;
            }
        }
        assert len == trimPath.length();

        /* If the normalized Path is empty, return an empty string. */
        if (len == 0) {
            return "";
        }

        /* Remove any trailing '/' if it exists. */
        if (trimPath.charAt(len - 1) == '/') {
            trimPath.deleteCharAt(len - 1);
        }

        /* Return the resulting normalized Path. */
        return trimPath.toString();
    }

    /**
     * Strip any leading path separators from the start of the path.
     */
    public static String stripLeadingSeparators (String path) {
        if (path.length() == 0) {
            return path;
        }

        if (path.charAt(0) != '/') {
            return path;
        }

        int ii;
        for (ii = 0; ii < path.length(); ii++) {
            if (path.charAt(ii) != '/') {
                break;
            }
        }

        return path.substring(ii);
    }
}
