package net.bitpot.injector.config;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * Info about injection
 */
public class InjectionInfo implements Assignable<InjectionInfo>, IXMLSerializable
{
    private String shortcut;
    private String template;

    private String cleanTemplate = null;
    private int cursorPos;


    // How many times shortcut was used.
    private int shortcutUsageCount;

    // Number of keypresses that user avoided due to template expansion
    private int keypressesSavedCount;

    private InjectionList parentList;


    public InjectionInfo()
    {
        this("", "", null);
    }


    public InjectionInfo(String shortcut, String template, @Nullable InjectionList parentList)
    {
        resetStats();

        this.shortcut = shortcut;
        setTemplate(template);

        this.parentList = parentList;
    }

    public String getShortcut()
    {
        return shortcut;
    }

    public void setShortcut(String shortcut)
    {
        this.shortcut = shortcut;
    }



    /**
     * Sets template as it should be represented for user (with pipe (|) as cursor and escaped pipes)
     * @param template New template.
     */
    public void setTemplate(String template)
    {
        this.template = template;
        cleanTemplate = null;
    }


    /**
     * Template as it should be represented for user.
     * @return Template
     */
    public String getTemplate()
    {
        return template;
    }


    /**
     * Returns clean template as it should be inserted in editor. getCursorPos should be used to determine
     * the position of cursor in the template.
     *
     * @return Clean template.
     */
    public String getCleanTemplate()
    {
        compileTemplate();
        return cleanTemplate;
    }


    /**
     * Returns cursor position in template.
     * @return Cursor position in template.
     */
    public int getCursorPos()
    {
        compileTemplate();
        return cursorPos;
    }



    private void compileTemplate()
    {
        if (cleanTemplate != null)
            return;

        // Removes pipe, determines position of cursor.
        String[] parts = template.split("(?<!\\\\)\\|", 2);

        String leftPart = parts[0];
        String rightPart = (parts.length > 1) ? parts[1] : "";

        leftPart = leftPart.replaceAll("\\\\\\|", "|");
        rightPart = rightPart.replaceAll("\\\\\\|", "|");
        cursorPos = leftPart.length();

        cleanTemplate = leftPart + rightPart;
    }


    @Override
    public void assign(InjectionInfo source)
    {
        shortcut = source.shortcut;
        template = source.template;

        cleanTemplate = source.cleanTemplate;
        cursorPos = source.cursorPos;

        keypressesSavedCount = source.keypressesSavedCount;
        shortcutUsageCount = source.shortcutUsageCount;
    }



    /**
     * Replaces last characters before current cursor position by template, specified in InjectionInfo object.
     * Method doesn't check whether replaced characters equals template shortcut or not, but shortcut string length
     * determines how many characters before cursor should be replaced.
     *
     * @param editor Editor in which replacement should be done.
     */
    public void expandTemplate(Editor editor)
    {
        Document doc = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();

        int replacePos = caretModel.getOffset() - getShortcut().length();

        String cleanTemplate = getCleanTemplate();
        doc.replaceString(replacePos, caretModel.getOffset(), cleanTemplate);
        caretModel.moveToOffset(replacePos + getCursorPos());

        // Stats counting.
        shortcutUsageCount++;
        keypressesSavedCount += cleanTemplate.length() - shortcut.length();

        if (parentList != null)
            parentList.injectionExpanded(this);
    }



    public int getShortcutUsageCount()
    {
        return shortcutUsageCount;
    }

    public void setShortcutUsageCount(int value)
    {
        shortcutUsageCount = value;
    }


    public int getKeypressesSavedCount()
    {
        return keypressesSavedCount;
    }

    public void setKeypressesSavedCount(int value)
    {
        this.keypressesSavedCount = value;
    }


    public void resetStats()
    {
        shortcutUsageCount = 0;
        keypressesSavedCount = 0;
    }

    public void setParentList(InjectionList parent)
    {
        this.parentList = parent;
    }

    @Override
    public boolean loadFromXML(Element elem)
    {
        String shortcut = elem.getAttributeValue("shortcut");
        String template = elem.getText();
        if (shortcut == null || shortcut.trim().isEmpty() || template.trim().isEmpty())
            return false;

        setShortcut(shortcut);
        setTemplate(template);
        setShortcutUsageCount(XmlTools.getIntAttr(elem, "usages", 0));
        setKeypressesSavedCount(XmlTools.getIntAttr(elem, "keypresses-saved", 0));

        return true;
    }

    @Override
    public void saveToXML(Element elem)
    {
        elem.setAttribute("shortcut", getShortcut())
            .setAttribute("usages", String.valueOf(getShortcutUsageCount()))
            .setAttribute("keypresses-saved", String.valueOf(getKeypressesSavedCount()))
            .setText(getTemplate());
    }
}
