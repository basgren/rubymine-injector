package net.bitpot.injector.gui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import net.bitpot.injector.ApplicationInjector;
import net.bitpot.injector.config.ApplicationConfig;
import net.bitpot.injector.config.InjectionInfo;
import net.bitpot.injector.config.InjectionList;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Settings form content. Can be displayed both in a separate dialog and in IDE settings dialog.
 */
public class SettingsDialog extends DialogWrapper
{


    private JTable templatesTbl;
    private JButton addTemplateBtn;
    private JButton deleteTemplateBtn;
    private JPanel rootComponent;
    private JButton editTemplateBtn;
    private JButton copyTemplateBtn;
    private JCheckBox convertToInterpolatedChk;
    private JTextField stringInjectionShortcutEd;
    private JCheckBox ignoreShortcutCaseChk;
    private JCheckBox showStatsChk;
    private JLabel totalInjectionsLbl;
    private JPanel totalStatsPnl;
    private JLabel keypressesSavedLbl;


    // Will store modified flag to avoid complex modification checks.
    private boolean modified = false;


    private InjectionListTableModel tableModel;

    // Copy of config that will be edited.
    private ApplicationConfig configCopy = null;


    public SettingsDialog(ApplicationConfig config)
    {
        super(true);
        setTitle("Injector Settings");



        init();

        stringInjectionShortcutEd.setDocument(new JTextFieldLimit(2));

        setData(config);
        initHandlers();
    }

    private void initHandlers()
    {
        templatesTbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if ((e.getButton() == 1) && (e.getClickCount() == 2))
                {
                    editCurrentSelection();
                }
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (templatesTbl.rowAtPoint(e.getPoint()) < 0)
                    templatesTbl.getSelectionModel().clearSelection();
            }
        });

        addTemplateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addNewTemplate(); }
        });

        copyTemplateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { copyCurrentSelection(); }
        });

        editTemplateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { editCurrentSelection(); }
        });

        deleteTemplateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteCurrentSelection(); }
        });

        showStatsChk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateStatsVisibility(); }
        });
    }

    private void updateStatsVisibility()
    {
        tableModel.setShowStats(showStatsChk.isSelected());
        totalStatsPnl.setVisible(showStatsChk.isSelected());
        tableModel.fireTableStructureChanged();
    }

    private void deleteCurrentSelection()
    {
        int modelRow = templatesTbl.convertRowIndexToModel(templatesTbl.getSelectedRow());
        if (modelRow < 0)
            return;

        int result = Messages.showOkCancelDialog(rootComponent, "Do you really want to delete this template?",
                                    "Confirm Delete",
                                    Messages.getQuestionIcon());

        if (result != DialogWrapper.OK_EXIT_CODE)
            return;

        configCopy.getInjections().remove(modelRow);
        tableModel.fireTableDataChanged();
        modified = true;
    }

    private void addNewTemplate()
    {
        addNewTemplate("", "", "Add template");
    }

    private void addNewTemplate(String shortcut, String template, String caption)
    {
        InjectionInfo info = new InjectionInfo(shortcut, template, null);

        EditInjectionDialog dlg = new EditInjectionDialog(info, rootComponent, caption);
        dlg.show();

        if (dlg.isOK())
        {
            configCopy.getInjections().add(info);
            tableModel.fireTableDataChanged();
            modified = true;
        }
    }



    private void copyCurrentSelection()
    {
        int modelRow = templatesTbl.convertRowIndexToModel(templatesTbl.getSelectedRow());
        if (modelRow < 0)
            return;

        InjectionInfo sourceInfo = configCopy.getInjections().get(modelRow);
        addNewTemplate(sourceInfo.getShortcut(), sourceInfo.getTemplate(), "Add template");
    }




    private void editCurrentSelection()
    {
        int modelRow = templatesTbl.convertRowIndexToModel(templatesTbl.getSelectedRow());
        if (modelRow < 0)
            return;

        InjectionInfo info = configCopy.getInjections().get(modelRow);

        EditInjectionDialog dlg = new EditInjectionDialog(info, rootComponent);
        dlg.show();

        if (dlg.isOK())
        {
            tableModel.fireTableRowsUpdated(modelRow, modelRow);
            if (dlg.isModified())
                modified = true;
        }

    }


    @Override
    protected JComponent createCenterPanel()
    {
        return rootComponent;
    }



    public void setData(ApplicationConfig config)
    {
        // Use config copy to edit settings.
        configCopy = new ApplicationConfig();
        configCopy.assign(config);

        InjectionList injections = configCopy.getInjections();

        // Setup table.
        tableModel = new InjectionListTableModel(injections);
        templatesTbl.setModel(tableModel);


        convertToInterpolatedChk.setSelected(configCopy.isForceDoubleQuotesOnInjection());
        stringInjectionShortcutEd.setText(configCopy.getStringInjection().getShortcut());
        ignoreShortcutCaseChk.setSelected(configCopy.isIgnoreShortcutCase());

        totalInjectionsLbl.setText(Integer.toString(injections.getTotalShortcutUsageCount()));
        keypressesSavedLbl.setText(Integer.toString(injections.getTotalKeypressesSavedCount()));

        showStatsChk.setSelected(configCopy.isStatsVisible());

        updateStatsVisibility();


        resetModified();
    }


    public ApplicationConfig getData()
    {
        configCopy.setForceDoubleQuotesOnInjection(convertToInterpolatedChk.isSelected());
        configCopy.getStringInjection().setShortcut(stringInjectionShortcutEd.getText());
        configCopy.setIgnoreShortcutCase(ignoreShortcutCaseChk.isSelected());

        configCopy.setStatsVisible(showStatsChk.isSelected());

        return configCopy;
    }


    /**
     * Used by application component to detect if options should be saved after OK is pressed, or if Apply button
     * should be enabled. This method is invoked by IDE when dialog is opened as part of IDE Settings dialog.
     *
     * @return True if any parameter was changed, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean isModified()
    {
        if (modified)
            return true;

        // Other more simple modified flag checks go here.
        return false;
    }

    /**
     * Clears modified flag.
     */
    public void resetModified()
    {
        modified = false;
    }


    // Method returns the root component of the form
    public JComponent getRootComponent()
    {
        return rootComponent;
    }




    private class InjectionListTableModel extends AbstractTableModel
    {
        private InjectionList data = null;
        private boolean showStats = false;

        public InjectionListTableModel(InjectionList injections)
        {
            data = injections;
        }


        public void setShowStats(boolean value)
        {
            showStats = value;
        }


        @Override
        public int getRowCount()
        {
            return data.size();
        }

        @Override
        public int getColumnCount()
        {
            return showStats ? 4 : 2;
        }

        @Override
        public String getColumnName(int columnIndex)
        {

            switch (columnIndex)
            {
            case 0: return "Shortcut";
            case 1: return "Template";
            case 2: return "Times used";
            case 3: return "Keypresses saved";
            default: return "Unknown";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            InjectionInfo info = data.get(templatesTbl.convertRowIndexToModel(rowIndex));
            switch (columnIndex)
            {
            case 0: return info.getShortcut();
            case 1: return info.getTemplate();
            case 2: return info.getShortcutUsageCount();
            case 3: return info.getKeypressesSavedCount();
            }

            return "?";
        }



    }


    /**
     * Testing dialog
     * @param args launch arguments
     */
    public static void main(String[] args)
    {
        final ApplicationInjector injector = new ApplicationInjector();

        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                SettingsDialog dialog = new SettingsDialog(injector.getConfig());

                //dialog.setVisible(true);
                dialog.show();

                System.exit(0);
            }
        });
    }
}
