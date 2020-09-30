package com.example.costi.ardubrid;

public class PaternModel
{
    private String title;
    private String subtitle1;
    private String subtitle2;
    private int icon;

    public PaternModel(String title, String subtitle1, String subtitle2, int icon)
    {
        this.title = title;
        this.subtitle1 = subtitle1;
        this.subtitle2 = subtitle2;
        this.icon = icon;
    }

    public String getTitle()
    {
        return title;
    }

    public String getFirstSubtitle()
    {
        return subtitle1;
    }

    public String getSecondSubtitle()
    {
        return subtitle2;
    }

    public int getIconID()
    {
        return icon;
    }

}
