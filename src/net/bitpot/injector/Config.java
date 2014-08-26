package net.bitpot.injector;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.HashMap;

/**
 * Contains all important settings for injector plugin.
 */
public class Config
{
    private static Config config = new Config();


    // Load icons using IconLoader. We must use absolute icon path with leading slash (/)
    public static final Icon INJECTOR_ICON = IconLoader.getIcon("/net/bitpot/injector/icons/injection.png");
    public static final Icon INJECTOR_TAG_ICON = IconLoader.getIcon("/net/bitpot/injector/icons/injection-tag.png");
    public static final Icon INJECTOR_OFF_ICON = IconLoader.getIcon("/net/bitpot/injector/icons/injection-grey.png");

    // Hash map is selected as it's the fastest for lookup.
    private HashMap<String, Boolean> fileExtensions = new HashMap<String, Boolean>();






    private Config()
    {
        // Add allowed extensions to extensions set.
        fileExtensions.put("erb", true);
        fileExtensions.put("rhtml", true);

        // DEBUG:
        //fileExtensions.put("phtml", true);
    }



    public static HashMap<String, Boolean> getFileExtensions()
    {
        return config.fileExtensions;
    }

    /*
    public static void addAllowedFileExt(String extension)
    {
        config.fileExtensions.put(extension, true);
    } */
}
