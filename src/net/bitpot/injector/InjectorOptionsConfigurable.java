package net.bitpot.injector;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Disposer;
import net.bitpot.injector.config.ApplicationConfig;
import net.bitpot.injector.gui.SettingsDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class InjectorOptionsConfigurable implements Configurable, Disposable {

    private SettingsDialog settingsDlg;

    @Nls
    @Override
    public String getDisplayName() {
        return "Injector";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        ApplicationConfig appConfig = InjectorOptionsProvider.getInstance().getConfig();
        settingsDlg = new SettingsDialog(appConfig);

        return settingsDlg.getRootComponent();
    }

    @Override
    public boolean isModified() {
        // Delegate isModified to Injector Settings dialog as it provides all
        // Application component settings
        return settingsDlg != null && settingsDlg.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        ApplicationConfig appConfig = InjectorOptionsProvider.getInstance().getConfig();

        // Get data from form to component
        appConfig.assign(settingsDlg.getData());

        // Reset modified flag or it will always return true.
        settingsDlg.resetModified();
    }

    @Override
    public void reset() {
        ApplicationConfig appConfig = InjectorOptionsProvider.getInstance().getConfig();

        // Reset form data from component
        settingsDlg.setData(appConfig);
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    @Override
    public void dispose() {
        settingsDlg = null;
    }
}
