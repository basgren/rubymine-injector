package net.bitpot.injector.config;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides more convenient acces to XML-stored params.
 */
public class XmlParams
{
    private Element parent;

    private Map<String, String> paramsHash = null;


    public XmlParams(Element parent)
    {
        this.parent = parent;
    }

    public void setParam(String param, String value)
    {
        Element elem = new Element("param");
        elem.setAttribute("name", param);
        elem.setText(value);
        parent.addContent(elem);
    }

    public void setParam(String param, boolean value)
    {
        setParam(param, Boolean.toString(value));
    }

    public String getParam(String param, String defValue)
    {
        initParams();

        if (!paramsHash.containsKey(param))
            return defValue;

        return paramsHash.get(param);
    }


    public boolean getParam(String param, boolean defValue)
    {
        initParams();

        if (!paramsHash.containsKey(param))
            return defValue;

        return Boolean.parseBoolean(paramsHash.get(param));
    }


    private void initParams()
    {
        if (paramsHash != null)
            return;

        paramsHash = new HashMap<String, String>();
        List list = parent.getChildren("param");

        for(Object obj: list)
        {
            Element elem = (Element)obj;
            Attribute attr = elem.getAttribute("name");
            if (attr == null)
                continue;

            paramsHash.put(attr.getValue(), elem.getText());
        }
    }


}
