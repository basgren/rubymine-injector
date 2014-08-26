package net.bitpot.injector.config;

import org.jdom.Element;

/**
 *
 */
public interface IXMLSerializable
{
    public boolean loadFromXML(Element elem);

    public void saveToXML(Element elem);
}
