package net.bitpot.injector;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializer;
import net.bitpot.injector.config.ApplicationConfig;
import net.bitpot.injector.gui.SettingsDialog;
import net.bitpot.injector.handlers.EditorHook;
import net.bitpot.injector.handlers.TypedKeyHook;
import net.bitpot.injector.handlers.impl.RHTMLTypingHandler;
import net.bitpot.injector.handlers.impl.RubyStringTypingHandler;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Registers global handlers which is necessary for Injector work.
 */

@State(
  name="InjectorSettings",
  storages= {
    @Storage(
      id="other",
      file = "$APP_CONFIG$/injector.xml"
    )}
)
public class ApplicationInjector implements ApplicationComponent, Configurable,
        PersistentStateComponent<org.jdom.Element>
{
    @SuppressWarnings("unused")
    private static Logger log = Logger.getInstance(ApplicationInjector.class.getName());

    private static ApplicationInjector instance = null;

    private ApplicationConfig appConfig;


    //private Map<String, EditorActionHandler> handlersMap = new HashMap<String, EditorActionHandler>();
    private List<EditorHook> hooks = new ArrayList<EditorHook>();

    private SettingsDialog settingsDlg;





    public ApplicationInjector()
    {
        appConfig = new ApplicationConfig();
    }



    public static ApplicationInjector getInstance()
    {
        return instance;
    }


    public ApplicationConfig getConfig()
    {
        return appConfig;
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


    private void setHandler(EditorHook handler)
    {
        hooks.add(handler);
        handler.setHandler();
    }


    private void restoreAllHandlers()
    {
        for(int i = hooks.size() - 1; i >= 0; i--)
            hooks.get(i).restoreHandler();

        hooks.clear();
    }


  /*  public static void executeOriginalTypedAction(@NotNull final Editor editor, final char c, @NotNull DataContext dataContext)
    {
        typedKeyHandler.executeOriginal(editor, c, dataContext);
    }  */


    public static TypedKeyHook getTypedKeyHandler()
    {
        //return typedKeyHandler;
        return TypedKeyHook.getInstance();
    }


    public void disposeComponent()
    {
        TypedKeyHook.unbindHook();
        //if (typedKeyHandler != null)
        //    typedKeyHandler.unregisterHandler();

        restoreAllHandlers();
    }


    /**
     * Method is called when backspace called just after
     * @param editor af
     */
    /*public void onRestoreShortcutChar(Editor editor)
    {
        //typedKeyHandler.restoreShortcutChar(editor);
    }  */

   /*
    public SettingsDialog getSettingsDialog()
    {
        createComponent();
        return settingsDlg;
    }   */

    /* ==============================================================
     *             Configurable interface implementation
     * ==============================================================
     */

    /**
     * Returns the user-visible name of the settings component.
     *
     * @return the visible name of the component.
     */
    @Nls
    @Override
    public String getDisplayName()
    {
        return "Injector";
    }


    @Override
    public String getHelpTopic()
    {
        return null;
    }

    /**
     * Returns the user interface component for editing the configuration.
     *
     * @return the component instance.
     */
    @Override
    public JComponent createComponent()
    {
        if (settingsDlg == null)
            settingsDlg = new SettingsDialog(this.getConfig());

        return settingsDlg.getRootComponent();
    }

    /**
     * Checks if the settings in the user interface component were modified by the user and
     * need to be saved.
     *
     * @return true if the settings were modified, false otherwise.
     */
    @Override
    public boolean isModified()
    {
        // Delegate isModified to Injector Settings dialog as it provides all Application component settings
        return settingsDlg != null && settingsDlg.isModified();
    }

    /**
     * Store the settings from configurable to other components.
     */
    @Override
    public void apply() throws ConfigurationException
    {
        if (settingsDlg != null) {
            // Get data from form to component
            appConfig.assign(settingsDlg.getData());

            // Reset modified flag or it will always return true.
            settingsDlg.resetModified();
        }
    }


    /**
     * Load settings from other components to configurable.
     */
    @Override
    public void reset()
    {
        if (settingsDlg != null)
        {
            // Reset form data from component
            settingsDlg.setData(this.getConfig());
        }
    }

    /**
     * Disposes the Swing components used for displaying the configuration.
     */
    @Override
    public void disposeUIResources()
    {
        settingsDlg = null;
    }



    // ===========================================================
    //  PersistentStateComponent<ApplicationConfig> implementation
    // ===========================================================

    /**
     * @return a component state. All properties and public fields are serialized. Only values, which differ
     * from default (i.e. the value of newly instantiated class) are serialized.
     * @see XmlSerializer
     */
    @Override
    public Element getState()
    {
        return appConfig.saveToXML();
    }

    /**
     * This method is called when new component state is loaded. A component should expect this method
     * to be called at any moment of its lifecycle. The method can and will be called several times, if
     * config files were externally changed while IDEA running.
     * @param elem loaded component state
     * @see com.intellij.util.xmlb.XmlSerializerUtil#copyBean(Object, Object)
     */
    @Override
    public void loadState(Element elem)
    {
        //log.debug("State loaded from: " + elem.toString());
        //appConfig = config;
        appConfig.loadFromXml(elem);
    }
}
