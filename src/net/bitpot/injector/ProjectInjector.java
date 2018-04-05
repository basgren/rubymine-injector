package net.bitpot.injector;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import net.bitpot.injector.config.ProjectConfig;
import net.bitpot.injector.widget.InjectorWidget;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


/**
 * Project component. It will show icon in status bar correctly for any number of open project windows.
 */
@State(
    name = "ProjectConfig",
    storages = {
            @Storage(StoragePathMacros.WORKSPACE_FILE)
    }
)

public class ProjectInjector implements ProjectComponent, Disposable, CaretListener,
        PersistentStateComponent<ProjectConfig>
{
    public static final int STATE_NORMAL = 0;
    public static final int STATE_JUST_INJECTED = 1;

    //private final static Logger log = Logger.getInstance(ProjectInjector.class.getName());


    private Project project;
    private InjectorWidget widget;

    // Map will contain each editor state
    private Map<Editor, EditorState> editorsStates = new HashMap<Editor, EditorState>();
    private ProjectConfig settings = new ProjectConfig();


    public ProjectInjector(Project project)
    {
        this.project = project;

        widget = new InjectorWidget(this);
    }

    @Override
    public void initComponent()
    {
        project.getMessageBus().connect(this).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER,
                new InjectorFileEditorManagerListener());

        final EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
        multicaster.addCaretListener(this, this);
    }

    @Override
    public void disposeComponent()
    {
        project = null;
        widget = null;
    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return "Injector.ProjectComponent";
    }


    /**
     * Returns project component depending on data context.
     *
     * @param dataContext Data context
     * @return Project instance or null if not found.
     */
    public static ProjectInjector getProjectComponent(DataContext dataContext)
    {
        Project prj = PlatformDataKeys.PROJECT.getData(dataContext);
        if (prj == null)
            return null;

        return prj.getComponent(ProjectInjector.class);
    }


    @Override
    public void projectOpened()
    {
        showInjectorStatusBarWidget();
    }

    @Override
    public void projectClosed()
    {
        hideInjectorStatusBarWidget();
    }


    public boolean isEnabled()
    {
        return settings.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        if (settings.enabled != enabled)
        {
            settings.enabled = enabled;
            updateWidget();
        }
    }


    private void showInjectorStatusBarWidget()
    {
        StatusBar bar = WindowManager.getInstance().getStatusBar(project);
        if (bar != null)
        {
            bar.addWidget(widget, "after Encoding");
            widget.update();
        }
    }


    private void hideInjectorStatusBarWidget()
    {
        StatusBar bar = WindowManager.getInstance().getStatusBar(project);
        if (bar != null)
            bar.removeWidget(InjectorWidget.WIDGET_ID);
    }


    private void updateWidget()
    {
        widget.update();
    }


    @Override
    public ProjectConfig getState()
    {
        return settings;
    }


    @Override
    public void loadState(ProjectConfig state)
    {
        settings = state;
        updateWidget();
    }


    private void setEditorState(Editor editor, int state)
    {
        EditorState edState = getEditorStateContainer(editor);

        if (edState.state != state)
        {
            edState.state = state;
            updateWidget();
        }
    }


    private EditorState getEditorStateContainer(Editor editor)
    {
        if (!editorsStates.containsKey(editor))
            editorsStates.put(editor, new EditorState(STATE_NORMAL));

        return editorsStates.get(editor);
    }


    @Override
    public void dispose()
    {
        // Do nothing?
    }


    /**
     * Caret position change handler.
     * @param e CaretEvent.
     */
    @Override
    public void caretPositionChanged(CaretEvent e)
    {
        // Set normal state of editor to avoid removing of inserted injection by pressing backspace.
        setEditorState(e.getEditor(), STATE_NORMAL);
        ApplicationInjector.getTypedKeyHandler().resetTypedSequence();
    }

    @Override
    public void caretAdded(CaretEvent caretEvent) {
        // Do nothing
    }

    @Override
    public void caretRemoved(CaretEvent caretEvent) {
        // Do nothing
    }


    /**
     * Provides some important actions on when file tab is changed in editor.
     */
    private class InjectorFileEditorManagerListener extends FileEditorManagerAdapter
    {
        /**
         * This event occurs when editor tab is changed.
         * @param event Event.
         */
        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event)
        {
            updateWidget();
        }
    }
}
