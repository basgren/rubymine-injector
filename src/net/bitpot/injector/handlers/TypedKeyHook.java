package net.bitpot.injector.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import net.bitpot.injector.ApplicationInjector;
import net.bitpot.injector.ProjectInjector;
import net.bitpot.injector.config.ApplicationConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Application-wide hook that intercepts character typing.
 */
public class TypedKeyHook implements TypedActionHandler
{
    @SuppressWarnings("unused")
    private static Logger log = Logger.getInstance(TypedKeyHook.class.getName());
    //private static Logger log = Logger.getInstance(TypedKeyHook.class.getName());

    // ------------ NEW IMPLEMENTATION --------------

    // This string will contain string that user has typed. This string will contain only parts of strings that are
    // listed in injections array.
    private String typedSequence = "";


    private static TypedKeyHook instance = null;
    private static TypedActionHandler originalHandler = null;


    private Vector<AbstractTypingHandler> registeredHandlers = new Vector<AbstractTypingHandler>();
    private volatile int sequenceResetLock = 0;

    ApplicationConfig config;


    // Will contain enabled handlers for every project.
    private Map<Project, Vector<AbstractTypingHandler>> enabledTypingHandlers =
            new HashMap<Project, Vector<AbstractTypingHandler>>();


    /**
     * Creates instance of typed key hook and binds itself to editor.
     */
    private TypedKeyHook()
    {
        config = ApplicationInjector.getInstance().getConfig();

        // Register ProjectManagerListener to update enabledTypingHandlers upon project open/close.
        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter()
        {
            @Override
            public void projectOpened(Project project) { addEnabledHandlersList(project); }
            @Override
            public void projectClosed(Project project) { removeEnabledHandlersList(project); }
        });
    }



    private void removeEnabledHandlersList(Project project)
    {
        if (!enabledTypingHandlers.containsKey(project))
            return;

        enabledTypingHandlers.remove(project);
    }

    private void addEnabledHandlersList(Project project)
    {
        enabledTypingHandlers.put(project, new Vector<AbstractTypingHandler>());
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER,
                new FileEditorManagerAdapter()
                {
                    @Override
                    public void selectionChanged(FileEditorManagerEvent event)
                    {
                        Editor editor = event.getManager().getSelectedTextEditor();
                        if (editor == null)
                            return;

                        // We must update enabled handlers for a project..
                        updateEnabledTypingHandlers(editor.getProject(), event.getNewFile());
                    }
                });
    }


    /**
     * Builds a list of typing handlers that can be executed for specified file (filetype is supported).
     *
     * @param project Project for which list of enabled handlers should be updated.
     * @param file File which will be checked on compatibility with all registered handlers.
     */
    private void updateEnabledTypingHandlers(Project project, VirtualFile file)
    {
        TypedKeyHook hook = TypedKeyHook.getInstance();
        Vector<AbstractTypingHandler> handlersList = enabledTypingHandlers.get(project);
        if (handlersList == null)
            return;

        handlersList.clear();

        // Null file is passed when switching from one task to another (Alt + Shift + T)
        // We shouldn't execute any handler if we have no file
        if (file == null)
            return;

        //log.debug("Updating handlers for file type: " + file.getFileType().getName());
        for (AbstractTypingHandler handler : hook.getRegisteredHandlers())
        {
            if (handler.isFileSupported(file))
                handlersList.add(handler);
        }
    }


    /**
     * Returns an instance of typed key hook. If instance is not exist, creates it. Hook is bound upon its creation.
     * @return Instance to typed key handler.
     */
    public static TypedKeyHook getInstance()
    {
        if (instance == null)
            instance = new TypedKeyHook();

        return instance;
    }


    /**
     * Should be called when the hooks need to be bound to the editor.
     */
    public static void bindHook()
    {
        // Firstly get typed event.
        TypedAction action = EditorActionManager.getInstance().getTypedAction();

        // Bind itself only if we're not bound yet.
        TypedKeyHook handler = getInstance();
        if (action.getHandler() == handler)
            return;

        // Save original handler to restore when unbinding our hook.
        originalHandler = action.getHandler();
        action.setupHandler(handler);
    }

    /**
     * Should be called when hook should be unbound from editor.
     */
    public static void unbindHook()
    {
        TypedAction action = EditorActionManager.getInstance().getTypedAction();

        // Unlink handler only if it was previously bound.
        TypedKeyHook handler = getInstance();
        if (action.getHandler() == handler)
        {
            action.setupHandler(originalHandler);
            originalHandler = null;
        }
    }


    public void addHandler(AbstractTypingHandler handler)
    {
        registeredHandlers.add(handler);
    }


    /**
     * Returns list of handlers that should be executed for currently selected file (it's detected by type)
     *
     * @param project Project for which we should take enabled handlers.
     * @return List of handlers.
     */
    @Nullable
    private Vector<AbstractTypingHandler> getHandlersForCurrentFileType(Project project)
    {
        if (!enabledTypingHandlers.containsKey(project))
            return null;

        // Now we'll just return list of all handlers - for debug.
        return enabledTypingHandlers.get(project);
    }

    public Vector<AbstractTypingHandler> getRegisteredHandlers()
    {
        return registeredHandlers;
    }


    // -----------------------------------------------



    // ----------------------------------------------------------

    public void resetTypedSequence()
    {
        if (sequenceResetLock > 0)
            return;

        typedSequence = "";
    }


    /**
     * Main key handler.
     * @param editor Editor
     * @param c Visible character typed by user
     * @param dataContext Data context.
     */
    public void execute(@NotNull final Editor editor, final char c, @NotNull DataContext dataContext)
    {
        // Don't call handlers if injector is disabled.
        final ProjectInjector injector = ProjectInjector.getProjectComponent(dataContext);
        if ((injector == null) || !injector.isEnabled())
        {
            // Call default handler and exit, if no suitable key was pressed.
            originalHandler.execute(editor, c, dataContext);
            return;
        }

        boolean resetSequence = true;

        try
        {
            lockSequenceReset();

            // Capture char
            typedSequence = typedSequence + c;
            if (config.isIgnoreShortcutCase())
                typedSequence = typedSequence.toLowerCase();

            // We need to call original handler to preserve original editor behavior. For example, if
            // cursor was beyond the end of line, default handler will pad character with necessary number of spaces.
            originalHandler.execute(editor, c, dataContext);

            Vector<AbstractTypingHandler> handlers = getHandlersForCurrentFileType(editor.getProject());
            if (handlers != null)
            {
                for(AbstractTypingHandler handler: handlers)
                {
                    // Invoke handlers methods. If handler returns true, then it needs to keep current
                    // char sequence - possibly, it has found something to do with the sequence.
                    if (handler.processCapturedChars(editor, typedSequence, dataContext))
                        resetSequence = false;
                }
            }
        }
        finally
        {
            unlockSequenceReset();

            // Now we need to reset typed sequence if necessary.
            if (resetSequence)
                resetTypedSequence();
        }


    }

    private void lockSequenceReset()
    {
        sequenceResetLock++;
    }

    private void unlockSequenceReset()
    {
        if (sequenceResetLock > 0)
            sequenceResetLock--;
    }
}