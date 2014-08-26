package net.bitpot.injector.handlers.impl;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import net.bitpot.injector.config.InjectionInfo;
import net.bitpot.injector.config.InjectionList;
import net.bitpot.injector.handlers.AbstractTypingHandler;
import net.bitpot.injector.handlers.checkers.RubyInjectionChecker;
import org.jetbrains.annotations.NotNull;

/**
 * Handler of RHTML files. Processes typed character sequence and if some matches found, replaces
 * typed sequence by template.
 */
public class RHTMLTypingHandler extends AbstractTypingHandler
{
    /**
     *
     * @param typedSequence Captured character sequence
     * @return True must be returned to prevent reset of char sequence
     */
    public boolean processCapturedChars(Editor editor, String typedSequence, DataContext dataContext)
    {
        InjectionList injections = getConfig().getInjections();
        for (InjectionInfo info: injections)
        {
            String shortcut = info.getShortcut();
            if (getConfig().isIgnoreShortcutCase())
                shortcut = shortcut.toLowerCase();

            if (!shortcut.startsWith(typedSequence))
                continue;

            if (shortcut.length() == typedSequence.length())
            {
                // Now check if we're not in injected code. Better to do it here that check it
                // after every typed char.
                // Also we shouldn't rely on PSI here as sometimes parser fails to parse as quick as needed,
                // so our check can give false results.
                if (RubyInjectionChecker.isCursorInInjectedCode(editor, editor.getCaretModel().getLogicalPosition()))
                    return false;

                // Full match. Replacement needed.
                info.expandTemplate(editor);

                // After insertion we don't need to keep char sequence - return false.
                return false;
            }

            return true;
        }


        return false;
    }


    @Override
    public boolean isFileSupported(@NotNull VirtualFile file)
    {
        // Return true only if RHTML file is edited.
        String typeName = file.getFileType().getName();
        return typeName.equals("RHTML");
    }
}
