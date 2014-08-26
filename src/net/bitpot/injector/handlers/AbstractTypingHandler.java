package net.bitpot.injector.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import net.bitpot.injector.ApplicationInjector;
import net.bitpot.injector.config.ApplicationConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract handler that can receive typed key events.
 */
public abstract class AbstractTypingHandler
{
    private ApplicationConfig appConfig = ApplicationInjector.getInstance().getConfig();


    protected ApplicationConfig getConfig()
    {
        return appConfig;
    }

    /**
     * Method is called after some visible character was typed in editor.
     *
     * @param editor Editor in which event is invoked.
     * @param typedSequence Sequence of characters which was typed before.
     * @param dataContext Data context.
     * @return Method should return true if handler needs current typedSequence to be preserved till next call.
     *         In this case when method is called next time, typedSequence will contain previous characters plus
     *         new one that was typed.
     *         If handler is not interested in current typedSequence, it should return false.
     */
    public abstract boolean processCapturedChars(Editor editor, String typedSequence, DataContext dataContext);


    /**
     * This method can be overridden in descendants to filter unsupported files. In case when file is unsupported
     * by handler, events won't be sent to this handler.
     *
     * @param file File which is currently edited.
     * @return Method returns true if file is supported, false otherwise.
     */
    public boolean isFileSupported(@NotNull VirtualFile file)
    {
        return true;
    }

}
