package net.bitpot.injector.config;

import org.jdom.Element;

/**
 *
 */
interface IXMLSerializable
{
    public boolean loadFromXML(Element elem);

    public void saveToXML(Element elem);
}
