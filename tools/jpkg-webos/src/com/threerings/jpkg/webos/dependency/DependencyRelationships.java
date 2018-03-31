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
 * Holds the valid dependency relationships supported by Debian packaging system.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-relationships.html#s-depsyntax">Debian Policy Manual</a>
 */
public enum DependencyRelationships
{
    /**
     * The strictly earlier relationship, indicating the dependency's version must be "lesser" than
     * and not equal to the declared version.
     */
    STRICTLY_EARLIER ("<<"),
    /**
     * The strictly earlier relationship, indicating the dependency's version must be "lesser" than or
     * equal to the declared version.
     */
    EARLIER_OR_EQUAL ("<="),
    /**
     * The exactly equal relationship, indicating the dependency's version must be equal to the
     * declared version.
     */
    EXACTLY_EQUAL ("="),
    /**
     * The later or equal relationship, indicating the dependency's version must be "greater" than or
     * equal to the declared version.
     */
    LATER_OR_EQUAL (">="),
    /**
     * The strictly later relationship, indicating the dependency's version must be "greater" than
     * and not equal to the declared version.
     */
    STRICTLY_LATER (">>");

    DependencyRelationships (String operator)
    {
        _operator = operator;
    }

    /**
     * Returns the string representation of this dependency relationship.
     */
    public String getOperator ()
    {
        return _operator;
    }

    private final String _operator;
}
