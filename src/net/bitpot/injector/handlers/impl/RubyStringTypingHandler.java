package net.bitpot.injector.handlers.impl;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import net.bitpot.injector.config.InjectionInfo;
import net.bitpot.injector.handlers.AbstractTypingHandler;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class RubyStringTypingHandler extends AbstractTypingHandler
{
    private static Logger log = Logger.getInstance(RubyStringTypingHandler.class.getName());

    @Override
    public boolean processCapturedChars(Editor editor, String typedSequence, DataContext dataContext)
    {
        // Firstly check if we're interested in this string. More complex check will be further.
        InjectionInfo stringInjection = getConfig().getStringInjection();
        String shortcut = stringInjection.getShortcut();
        if (getConfig().isIgnoreShortcutCase())
            shortcut = shortcut.toLowerCase();

        // Exit if char sequence is not we're looking for
        if (!shortcut.startsWith(typedSequence))
            return false;

        // We get here if typed chars match the beginning of the shortcut. But we don't want to proceed if
        // shortcut is not fully matched.
        if (typedSequence.length() < shortcut.length())
            return true;

        // Check document if injection is allowed
        Document doc = editor.getDocument();
        Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        if (project == null)
            return false;


        // Get file currently open in editor.
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(doc);
        //PsiDocumentManager.getInstance(project).
        if (psiFile == null)
            return false;

        // Firstly try to detect, if there's quotes beside cursor, as sometimes when user types so fast
        // PSI parser have no time to complete parsing, so PSI becomes unreliable for this task
        CharSequence docChars = doc.getCharsSequence();
        int seqBeginOffset = editor.getCaretModel().getOffset() - typedSequence.length();
        int leftQuoteOffs = seqBeginOffset - 1;
        int rightQuoteOffs = editor.getCaretModel().getOffset();

        // Check characters besides cursor
        char leftChar = docChars.charAt(leftQuoteOffs);
        char rightChar = docChars.charAt(rightQuoteOffs);

        boolean singleQuoteDetected = (leftChar == '\'') && (rightChar == '\'');
        boolean doubleQuoteDetected = (leftChar == '"') && (rightChar == '"');

        // Use strings to avoid instanceof checks.
        boolean isSingleQuoted = false, isDoubleQuoted = false;

        //PsiElement elem = psiFile.findElementAt(editor.getCaretModel().getOffset() - 1);
        if (singleQuoteDetected || doubleQuoteDetected)
        {
            isSingleQuoted = singleQuoteDetected;
            isDoubleQuoted = doubleQuoteDetected;
        }
        else
        {
            // If quotes are not detected "manually", try to detect them using PSI.
            // Here we assume that PSI is successfully parsed at this moment.

            // Get current PsiElement. If it cannot be retrieved, disallow injection.
            PsiElement elem = psiFile.findElementAt(seqBeginOffset);
            if (elem == null)
            {
                //log.debug("Cannot find PSI element");
                return false;
            }

            PsiElement parentElem = elem.getParent();
            if (parentElem == null)
            {
                //log.debug("Cannot find PSI parent");
                return false;
            }

            String parentName = parentElem.toString();

            // log.debug("Parent name:" + parentName);
            // log.debug("Element name: " + elem.toString());

            // Exit if we're inside #{} - need to replace by some other check
            // "Expression subtitution" is a name of PSI element that represents '#{}'
            if (parentName.equals("Expression subtitution"))
            {
                //log.debug("Parent name is not 'Expression substitution'");
                return false;
            }

            PsiElement child = parentElem.getFirstChild();
            if ((child != null) && parentName.equals("Single quoted like string"))
            {
                isSingleQuoted = true;
                leftQuoteOffs = child.getTextOffset();
            }

            child = parentElem.getLastChild();
            if ((child != null) && parentName.equals("Double quoted like string"))
            {
                isDoubleQuoted = true;
                rightQuoteOffs = child.getTextOffset();
            }
        }


        // Here we have information about what kind of quotes we have and what are their positions.
        if (isDoubleQuoted)
        {
            stringInjection.expandTemplate(editor);
            // log.debug("Template expanded");

            // We don't need char sequence anymore, so we can return false.
            return false;
        }
        else if (isSingleQuoted)
        {
            // Here we come only when string is single quoted.
            if (!getConfig().isForceDoubleQuotesOnInjection())
                return false;

            // Replace single quotes on double quotes.
            doc.replaceString(leftQuoteOffs, leftQuoteOffs + 1, "\"");

            // Also we should find single quote as when using several shortcut chars, psi structure can
            // be in previous state too.
            int offs = rightQuoteOffs;
            CharSequence text = editor.getDocument().getCharsSequence();
            while (offs < text.length() && text.charAt(offs) != '\'')
                offs++;

            if (offs < text.length())
                doc.replaceString(offs, offs + 1, "\"");

            stringInjection.expandTemplate(editor);

        }
        else {
            log.debug("No single or double quotes found");
        }


        return false;
    }


    @Override
    public boolean isFileSupported(@NotNull VirtualFile file)
    {
        String fileType = file.getFileType().getName();
        return (fileType.equals("Ruby") || fileType.equals("RHTML"));
    }
}