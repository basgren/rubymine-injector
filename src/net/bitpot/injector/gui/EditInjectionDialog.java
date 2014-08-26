package net.bitpot.injector.gui;

import com.intellij.openapi.ui.DialogWrapper;
import net.bitpot.injector.config.InjectionInfo;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class EditInjectionDialog extends DialogWrapper
{
    private InjectionInfo injection = null;
    private InjectionInfo injectionCopy = null;
    private boolean modified = false;

    private JPanel rootPane;
    private JTextField shortcutEd;
    private JTextField templateEd;



    protected EditInjectionDialog(InjectionInfo info, Component parent)
    {
        this(info, parent, "Edit template");
    }

    protected EditInjectionDialog(InjectionInfo info, Component parent, String caption)
    {
        super(parent, false);
        setTitle(caption);

        injection = info;
        injectionCopy = new InjectionInfo();
        injectionCopy.assign(injection);


        init();
        initData();
        shortcutEd.requestFocus();
    }


    @Override
    public JComponent getPreferredFocusedComponent()
    {
        return shortcutEd;
    }


    @Override
    protected void dispose()
    {
        super.dispose();

        injection = null;
        injectionCopy = null;
    }

    @Override
    protected JComponent createCenterPanel()
    {
        return rootPane;
    }


    private void initData()
    {
        shortcutEd.setText(injection.getShortcut());
        templateEd.setText(injection.getTemplate());
    }


    private void applyData()
    {
        injection.setShortcut(shortcutEd.getText());
        injection.setTemplate(templateEd.getText());
    }


    public boolean isModified()
    {
        return modified;
    }

   /**
   * This method is invoked by default implementation of "OK" action. It just closes dialog
   * with <code>OK_EXIT_CODE</code>. This is convenient place to override functionality of "OK" action.
   * Note that the method does nothing if "OK" action isn't enabled.
   */
    @Override
    protected void doOKAction()
    {
        modified = !(injection.getShortcut().equals(injectionCopy.getShortcut()) &&
                     injection.getTemplate().equals(injectionCopy.getTemplate()));

        applyData();

        // Call inherited handler at the end as it will invoke dispose method.
        super.doOKAction();
    }


}
