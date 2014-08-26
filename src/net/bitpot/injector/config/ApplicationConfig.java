package net.bitpot.injector.config;

import com.intellij.openapi.diagnostic.Logger;
import org.jdom.DataConversionException;
import org.jdom.Element;

import java.util.List;

/**
 * Application wide settings.
 */
public class ApplicationConfig extends Modifiable implements
                                Assignable<ApplicationConfig>
                                //Cloneable
{
    private final static int XML_DATA_FORMAT_VERSION = 1;

    @SuppressWarnings("unused")
    private static Logger log = Logger.getInstance(ApplicationConfig.class.getName());

    private InjectionList injections = new InjectionList();

    private boolean stringInjectionEnabled = true;
    private boolean forceDoubleQuotesOnInjection = true;

    private InjectionInfo stringInjection = new InjectionInfo();

    private boolean ignoreShortcutCase = true;

    private boolean statsVisible = false;



    public ApplicationConfig()
    {
        // This constructor can be called by IDE for its own reasons after calling getState() of owning
        // ApplicationInjector, so no bulk initializations should be done here.
        initDefaults();

        resetModified();
    }

    public boolean isStatsVisible()
    {
        return statsVisible;
    }

    public void setStatsVisible(boolean value)
    {
        statsVisible = value;
    }

    /**
     * Initializes injections map if necessary.
     */
    private void initDefaults()
    {
        if (injections.isEmpty())
        {
            injections.addInjection("%%", "<%- | -%>");
            injections.addInjection("%=", "<%= | -%>");
            injections.addInjection("%-", "<% | %>");
            injections.addInjection("##", "<%# | -%>");
            injections.addInjection("%i", "<%- if | -%>");
            injections.addInjection("%u", "<%- unless | -%>");
            injections.addInjection("%l", "<%- else -%>");
            injections.addInjection("%e", "<%- end -%>");
            injections.addInjection("%f", "<%- for | -%>");
            injections.addInjection("%t", "<%= t('|') -%>");
            injections.addInjection("%a", "<%= asset_path('|') %>");
            injections.addInjection("%d", "<%# TODO: | -%>");
        }


        stringInjection.setShortcut("##");
        stringInjection.setTemplate("#{|}");
    }


    public InjectionList getInjections()
    {
        return injections;
    }



    /**
     * Supports several versions loading
     * @param elem Element from which data should be loaded.
     */
    public void loadFromXml(Element elem)
    {
        int version;
        try {
            version = elem.getAttribute("version").getIntValue();
        } catch (DataConversionException e) { return; }

        switch (version)
        {
        case 1: loadFromXml_v1(elem); break;
        }

        resetModified();
    }




    /**
     * Version 1 of XML config file.
     * @param root Root element
     */
    private void loadFromXml_v1(Element root)
    {
        Element injList = root.getChild("injections");

        injections.clear();

        if (injList == null)
            return;

        ignoreShortcutCase = XmlTools.getBoolAttr(injList, "ignore-shortcut-case", true);

        List tplList = injList.getChildren("template");
        InjectionInfo info;
        for(Object o: tplList)
        {
            info = new InjectionInfo();

            if (info.loadFromXML((Element)o))
                injections.add(info);
        }

        Element stringInjectionElem = root.getChild("string-inject");
        if (stringInjectionElem != null)
        {
            stringInjection.setShortcut(stringInjectionElem.getAttributeValue("shortcut", "##"));
            forceDoubleQuotesOnInjection = XmlTools.getBoolAttr(stringInjectionElem, "force-double-quotes", true);
            stringInjection.setShortcutUsageCount(XmlTools.getIntAttr(stringInjectionElem, "usages", 0));
            stringInjection.setKeypressesSavedCount(XmlTools.getIntAttr(stringInjectionElem, "keypresses-saved", 0));
        }


        Element totalStatsElem = root.getChild("total-stats");
        if (totalStatsElem != null)
        {
            injections.setTotalShortcutUsageCount(XmlTools.getIntAttr(totalStatsElem, "usages", 0));
            injections.setTotalKeypressesSavedCount(XmlTools.getIntAttr(totalStatsElem, "keypresses-saved", 0));
        }

        XmlParams xmlParams = new XmlParams(root);

        statsVisible = xmlParams.getParam("show-stats", false);
    }


    /**
     * Saves state to config.
     * @return Element with serialized data.
     */
    public Element saveToXML()
    {
        XElement root = new XElement("config");
        root.setIntAttr("version", XML_DATA_FORMAT_VERSION);

        XElement injList = root.appendChild("injections");
        injList.setBoolAttr("ignore-shortcut-case", ignoreShortcutCase);

        for (InjectionInfo info : injections)
        {
            info.saveToXML(injList.appendChild("template"));
        }

        XElement stringElem = root.appendChild("string-inject");
        stringElem.setAttribute("shortcut", stringInjection.getShortcut());
        stringElem.setAttribute("force-double-quotes", Boolean.toString(forceDoubleQuotesOnInjection));
        stringElem.setAttribute("usages", String.valueOf(stringInjection.getShortcutUsageCount()));
        stringElem.setAttribute("keypresses-saved", String.valueOf(stringInjection.getKeypressesSavedCount()));

        XElement totalStatsElem = root.appendChild("total-stats");
        totalStatsElem.setIntAttr("usages", injections.getTotalShortcutUsageCount());
        totalStatsElem.setIntAttr("keypresses-saved", injections.getTotalKeypressesSavedCount());

        XmlParams xmlParams = new XmlParams(root);
        xmlParams.setParam("show-stats", statsVisible);

        resetModified();

        return root;
    }




    public void setStringInjectionEnabled(boolean value)
    {
        stringInjectionEnabled = value;
    }

    public boolean isStringInjectionEnabled()
    {
        return stringInjectionEnabled;
    }


    public InjectionInfo getStringInjection()
    {
        return stringInjection;
    }


    public void setForceDoubleQuotesOnInjection(boolean value)
    {
        forceDoubleQuotesOnInjection = value;
    }

    public boolean isForceDoubleQuotesOnInjection()
    {
        return forceDoubleQuotesOnInjection;
    }

    @Override
    public void assign(ApplicationConfig source)
    {
        stringInjectionEnabled = source.stringInjectionEnabled;
        forceDoubleQuotesOnInjection = source.forceDoubleQuotesOnInjection;
        statsVisible = source.statsVisible;

        injections.assign(source.injections);
        stringInjection.assign(source.getStringInjection());
    }

    public boolean isIgnoreShortcutCase()
    {
        return ignoreShortcutCase;
    }

    public void setIgnoreShortcutCase(boolean ignoreShortcutCase)
    {
        this.ignoreShortcutCase = ignoreShortcutCase;
    }
}