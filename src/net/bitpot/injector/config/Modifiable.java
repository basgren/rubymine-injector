package net.bitpot.injector.config;

/**
 * Class that has the only one function - to store and provide access for "modified" flag.
 */
public class Modifiable
{
    private boolean modified = false;

    protected void modified()
    {
        modified = true;
    }

    protected boolean isModified()
    {
        return modified;
    }

    protected void resetModified()
    {
        modified = false;
    }
}
