package net.bitpot.injector;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * Useful routines.
 */
public class Utils
{

    /**
     * Checks if injections are allowed in specified file.
     *
     * @param file Virtual file to check
     * @return True if injections are allowed in specified file, false otherwise.
     */
    public static boolean isInjectionAllowedInFile(VirtualFile file)
    {
        if (file == null)
            return false;

        String ext = file.getExtension();
        if (ext == null)
            return false;

        ext = ext.toLowerCase();

        return Config.getFileExtensions().containsKey(ext);
    }

}
