package net.bitpot.injector.widget;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import net.bitpot.injector.Config;
import net.bitpot.injector.ProjectInjector;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Basil Gren
 * Date: 28.04.11
 * Time: 0:40
 */
public class InjectorWidget extends JPanel implements CustomStatusBarWidget//, StatusBarWidget.IconPresentation
{
    public static final String WIDGET_ID = "Injector.StatusBarWidget";

    private StatusBar statusBar;
    private ProjectInjector projectInjector;


    private JLabel myIconLabel;



    public InjectorWidget(ProjectInjector projectComp)
    {
        projectInjector = projectComp;

        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(new WidgetBorder());

        myIconLabel = new JLabel();
        Dimension dim = new Dimension(16,16);
        myIconLabel.setPreferredSize(dim);
        myIconLabel.setMaximumSize(dim);
        myIconLabel.setMinimumSize(dim);
        add(myIconLabel, BorderLayout.CENTER);


        updateIcon();


        myIconLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
                else if (e.getButton() == MouseEvent.BUTTON1)
                    leftButtonClicked();
            }
        });

    }


    private void showPopupMenu(Component invoker, int x, int y)
    {
        ActionGroup group = (ActionGroup)ActionManager.getInstance().getAction("Injector.Popup");
        ActionManager.getInstance().createActionPopupMenu(ActionPlaces.UNKNOWN, group).getComponent().show(invoker, x, y);
    }


    private void leftButtonClicked()
    {
        // Here we shouldn't call projectInjector.setEnabled directly as we need
        // to update action state as well. So we should invoke action that toggles
        // state of Injector.
        ToggleAction act = (ToggleAction)(ActionManager.getInstance().getAction("Injector.Enable"));
        if (act == null)
        {
            return;
        }

        // Here we should create AnActionEvent instance to invoke action.
        AnActionEvent actEvt = new AnActionEvent(null,
                DataManager.getInstance().getDataContext(statusBar.getComponent()),
                ActionPlaces.UNKNOWN, act.getTemplatePresentation(), ActionManager.getInstance(), 0);
        act.actionPerformed(actEvt);
    }


    @NotNull
    @Override
    public String ID()
    {
        return WIDGET_ID;
    }

    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type)
    {
        return null;
    }

    @Override
    public void install(@NotNull StatusBar statusBar)
    {
        this.statusBar = statusBar;
    }

    @Override
    public void dispose()
    {
        myIconLabel = null;
        statusBar = null;
        projectInjector = null;
    }

    public void update()
    {
        if (statusBar != null)
            statusBar.updateWidget(WIDGET_ID);

        updateIcon();
    }



    private void updateIcon()
    {
        if (projectInjector == null)
            return;

        Icon icon;

        if (projectInjector.isEnabled())
            //icon = projectInjector.isInjectionAllowedInFile() ? Config.INJECTOR_TAG_ICON : Config.INJECTOR_ICON;
            icon = Config.INJECTOR_ICON;
        else
            icon = Config.INJECTOR_OFF_ICON;

        myIconLabel.setIcon(icon);
    }



    @Override
    public JComponent getComponent()
    {
        return this;
    }
}
