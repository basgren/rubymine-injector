package net.bitpot.injector.config;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 * Provides some more routines for basic Element
 */
public class XElement extends Element
{

    protected XElement()
    {
        super();
    }

    public XElement(String name)
    {
        super(name);
    }

    public XElement appendChild(String name)
    {
        XElement elem = new XElement(name);
        addContent(elem);
        return elem;
    }


    public void setIntAttr(String attr, int value)
    {
        setAttribute(attr, String.valueOf(value));
    }

    public int getIntAttr(String attr, int defaultValue)
    {
        Attribute a = getAttribute(attr);
        if (a == null)
            return defaultValue;

        try { return a.getIntValue(); }
        catch (DataConversionException e)
            { return defaultValue; }
    }

    public void setBoolAttr(String attrName, boolean value)
    {
        this.setAttribute(attrName, Boolean.toString(value));
    }
}
