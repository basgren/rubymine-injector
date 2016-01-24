package net.bitpot.injector;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import net.bitpot.injector.config.ApplicationConfig;
import net.bitpot.injector.handlers.TypedKeyHook;
import net.bitpot.injector.handlers.impl.RHTMLTypingHandler;
import net.bitpot.injector.handlers.impl.RubyStringTypingHandler;
import org.jetbrains.annotations.NotNull;


/**
 * Registers global handlers which is necessary for Injector work.
 */

public class ApplicationInjector implements ApplicationComponent
{
    @SuppressWarnings("unused")
    private static Logger log = Logger.getInstance(ApplicationInjector.class.getName());

    private static ApplicationInjector instance = null;

    public static ApplicationInjector getInstance()
    {
        return instance;
    }

    public ApplicationConfig getConfig()
    {
        return InjectorOptionsProvider.getInstance().getConfig();
    }


    @NotNull
    public String getComponentName()
    {
        return "ApplicationInjector";
    }


    public void initComponent()
    {
        //log.debug("Initializing Injector...");
        // Deny two instances of plugin. Otherwise we will get a mess-up in editor when using Code Injector.
        if (instance != null)
            throw new RuntimeException("Code Injector plugin already initialized.");

        // Save plugin object for accessing in static methods.
        instance = this;

        //       ...=========================...
        // -----=====     Init handlers     =====-----

        // Bind editor typing handler
        TypedKeyHook.bindHook();
        TypedKeyHook.getInstance().addHandler(new RHTMLTypingHandler());
        TypedKeyHook.getInstance().addHandler(new RubyStringTypingHandler());
    }


    public static TypedKeyHook getTypedKeyHandler()
    {
        return TypedKeyHook.getInstance();
    }


    public void disposeComponent()
    {
        TypedKeyHook.unbindHook();
    }

}
