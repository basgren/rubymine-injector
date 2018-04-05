package net.bitpot.injector;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Contains all important settings for injector plugin.
 */
public class Config
{
    // Load icons using IconLoader. We must use absolute icon path with leading slash (/)
    public static final Icon INJECTOR_ICON = IconLoader.getIcon("/net/bitpot/injector/icons/injection.png");
    public static final Icon INJECTOR_OFF_ICON = IconLoader.getIcon("/net/bitpot/injector/icons/injection-grey.png");
    //public static final Icon INJECTOR_TAG_ICON = IconLoader.getIcon("/net/bitpot/injector/icons/injection-tag.png");
}
