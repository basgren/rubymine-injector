package net.bitpot.injector.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import net.bitpot.injector.ProjectInjector;

/**
 * Created by IntelliJ IDEA.
 * User: Basil Gren
 * Date: 23.04.11
 * Time: 14:37
 * Switches
 */
public class EnableAction extends ToggleAction
{
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getInstance(EnableAction.class.getName());


    private ProjectInjector getProjectComponent(AnActionEvent e)
    {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null)
        {
            //log.debug("Project is null");
            return null;
        }

        ProjectInjector inj = project.getComponent(ProjectInjector.class);
        if (inj == null)
        {
            //log.debug("Injector is null");
            return null;
        }

        return inj;
    }


    @Override
    public boolean isSelected(AnActionEvent e)
    {
        ProjectInjector inj = getProjectComponent(e);
        return inj != null && inj.isEnabled();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state)
    {
        ProjectInjector inj = getProjectComponent(e);
        if (inj == null)
        {
            //log.debug("Injector is null");
            return;
        }

        inj.setEnabled(state);
    }
}
