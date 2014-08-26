package net.bitpot.injector.handlers.checkers;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;

/**
 * Created by IntelliJ IDEA.
 * User: Basil Gren
 * Date: 30.04.11
 * Time: 22:42
 * Provides ruby-specific checks for injection.
 */
public class RubyInjectionChecker extends CustomChecker
{
    private static final String tagOpen = "<%";
    private static final String tagClose = "%>";


    public static boolean isCursorInInjectedCode(Editor editor, LogicalPosition pos)
    {
        Document doc = editor.getDocument();
        CharSequence docChars = doc.getCharsSequence();
        int offset = editor.logicalPositionToOffset(pos);

        // TODO: Now this function returns true if cursor is within '<%>' string.

        // Possible situations:
        // 1. <%  | %>  ==> true
        // 2. <%  %> | <%  %> ==> false

        // If there's no tag opening before cursor, it's guranteed than we're not in injected code.
        int tagOpenLeftPos = posBackward(docChars, tagOpen, offset);
        if (tagOpenLeftPos < 0)
            return false;

        // Check situation 2.
        int tagCloseLeftPos = posBackward(docChars, tagClose, offset - tagClose.length() + 1);
        if ((tagCloseLeftPos >= 0) && (tagCloseLeftPos > tagOpenLeftPos))
            return false;

        // We get here only if we have one tag opening before cursor.
        int tagCloseRightPos = posForward(docChars, tagClose, offset - tagClose.length() + 1);

        return tagCloseRightPos >= 0;
    }
}
