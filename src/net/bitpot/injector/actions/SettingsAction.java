package net.bitpot.injector.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import net.bitpot.injector.ApplicationInjector;
import net.bitpot.injector.gui.SettingsDialog;

/**
 * Action that shows injector settings.
 */
class SettingsAction extends AnAction
{
    public void actionPerformed(AnActionEvent e)
    {
        //openGlobalSettings(e);
        openSettingsDialog(e);
    }


    /**
     * Opens custom settings dialog. Much faster that IDE settings.
     * @param e Action event.
     */
    private void openSettingsDialog(AnActionEvent e)
    {
        // Other way to display settings is to open modal dialog. It will be much faster that opening the whole
        // settings dialog.
        ApplicationInjector injector = ApplicationInjector.getInstance();
        SettingsDialog dlg = new SettingsDialog(injector.getConfig());
        dlg.show();

        if (dlg.isOK())
        {
            // Apply changes made in dialog
            injector.getConfig().assign(dlg.getData());
        }
    }


    /**
     * Open IDE settings on page with Injector settings
     * @param e Action event
     */
    private void openGlobalSettings(AnActionEvent e)
    {
        Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            project = ProjectManager.getInstance().getDefaultProject();

        // Show settings described in settings showed in /platform/platform-resources/src/idea/PlatformActions.xml:
        // <action id="ShowSettings" class="com.intellij.ide.actions.ShowSettingsAction" icon="/general/ideOptions.png"/>

        // Displaying settings can be done through ShowSettingsUtil instance.
        ShowSettingsUtil settingsUtil = ShowSettingsUtil.getInstance();
        settingsUtil.showSettingsDialog(project, ApplicationInjector.getInstance());
    }
}
