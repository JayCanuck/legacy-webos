
package com.threerings.jpkg.ar;

import com.threerings.jpkg.UnixStandardPermissions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Jason Robitaille
 */
public class PalmScript implements ArchiveEntry {

    public PalmScript(File data, int longNameOffset)
    {
        _data = data;
        _longNameOffset = longNameOffset;
    }

    // from ArchiveEntry
    public InputStream getInputStream ()
    {
        try {
            return new FileInputStream(_data);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // from ArchiveEntry
    public long getSize ()
    {
        return _data.length();
    }

    // from ArchiveEntry
    public String getPath ()
    {
        return "/" + _longNameOffset;
    }

    // from ArchiveEntry
    public int getUserId ()
    {
        return UnixStandardPermissions.ROOT_USER.getId();
    }

    // from ArchiveEntry
    public int getGroupId ()
    {
        return UnixStandardPermissions.ROOT_GROUP.getId();
    }

    // from ArchiveEntry
    public int getMode ()
    {
        return UnixStandardPermissions.EXECUTABLE_FILE_MODE;
    }

    /**
     * Return the string data as a byte array encoded for an {@link Archive}
     */
    private byte[] dataAsBytes ()
    {
        long length = _data.length();
        byte[] bytes = new byte[(int)length];
        try {
            InputStream is = new FileInputStream(_data);
            int offset = 0;
            int numRead = 0;
            while ((offset<bytes.length) &&
                    ((numRead=is.read(bytes, offset, bytes.length-offset)) >= 0)) {
                offset += numRead;
            }
        } catch (Exception ex) {}
        return bytes;
    }

    private final File _data;

    private final int _longNameOffset;
}
