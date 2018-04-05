package net.bitpot.injector.config;

import org.jdom.Element;

/**
 *
 */
interface IXMLSerializable
{
    boolean loadFromXML(Element elem);
    void saveToXML(Element elem);
}
