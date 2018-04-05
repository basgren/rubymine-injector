package net.bitpot.injector.config;

import java.util.Vector;

/**
 *
 */
public class InjectionList extends Vector<InjectionInfo> implements Assignable<InjectionList>
{
    private int totalShortcutUsageCount = 0;
    private int totalKeypressesSavedCount = 0;

    void addInjection(String shortcut, String template)
    {
        InjectionInfo info = new InjectionInfo(shortcut, template, this);
        add(info);
    }


    @Override
    public boolean add(InjectionInfo injectionInfo)
    {
        injectionInfo.setParentList(this);
        return super.add(injectionInfo);
    }


    @Override
    public void assign(InjectionList source)
    {
        int originalSize = size();

        if (originalSize != source.size())
            this.setSize(source.size());

        if (originalSize < source.size())
        {
            for(int i = originalSize; i < source.size(); i++)
                set(i, new InjectionInfo());
        }

        InjectionInfo info;
        for(int i = 0; i < size(); i++)
        {
            info = get(i);
            info.assign(source.get(i));
            info.setParentList(this);
        }

        totalKeypressesSavedCount = source.totalKeypressesSavedCount;
        totalShortcutUsageCount = source.totalShortcutUsageCount;
    }


    public void injectionExpanded(InjectionInfo info)
    {
        totalShortcutUsageCount++;
        totalKeypressesSavedCount += info.getCleanTemplate().length() - info.getShortcut().length();
    }


    public int getTotalShortcutUsageCount()
    {
        return totalShortcutUsageCount;
    }

    public void setTotalShortcutUsageCount(int value)
    {
        this.totalShortcutUsageCount = value;
    }

    public int getTotalKeypressesSavedCount()
    {
        return totalKeypressesSavedCount;
    }

    public void setTotalKeypressesSavedCount(int value)
    {
        this.totalKeypressesSavedCount = value;
    }


}