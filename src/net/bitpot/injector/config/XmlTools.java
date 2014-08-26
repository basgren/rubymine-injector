package net.bitpot.injector.config;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 * Provides basic Xml routines.
 */
public class XmlTools
{
    public static boolean getBoolAttr(Element elem, String attrName, boolean defaultValue)
    {
        Attribute attr = elem.getAttribute(attrName);
        if (attr != null)
        {
            try { return attr.getBooleanValue(); }
            catch (DataConversionException e) { return defaultValue; }
        }
        return defaultValue;
    }


    public static int getIntAttr(Element elem, String attrName, int defaultValue)
    {
        Attribute attr = elem.getAttribute(attrName);
        if (attr != null)
        {
            try { return attr.getIntValue(); }
            catch (DataConversionException e) { return defaultValue; }
        }
        return defaultValue;
    }
}
