package net.bitpot.injector.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;

/**
 *
 */
public abstract class EditorHook extends EditorActionHandler
{
    private String actionID = null;
    private EditorActionHandler prevHandler = null;


    public EditorHook(String actionID)
    {
        EditorActionManager manager = EditorActionManager.getInstance();
        prevHandler = manager.getActionHandler(actionID);
        this.actionID = actionID;
    }

    public void setHandler()
    {
        EditorActionManager manager = EditorActionManager.getInstance();
        manager.setActionHandler(actionID, this);
    }



    public void restoreHandler()
    {
        if (prevHandler != null)
        {
            EditorActionManager manager = EditorActionManager.getInstance();
            manager.setActionHandler(actionID, prevHandler);

            prevHandler = null;
        }
    }

    public void callPrevHandler(Editor editor, DataContext dataContext)
    {
        prevHandler.execute(editor, dataContext);
    }
}
