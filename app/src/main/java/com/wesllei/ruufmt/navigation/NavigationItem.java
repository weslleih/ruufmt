package com.wesllei.ruufmt.navigation;

/**
 * Created by wesllei on 10/05/15.
 */
public class NavigationItem {

    private int title;
    private int color;
    private int icon;
    private int id;
    private int type;

    public static final int TYPE_MAIN = 0;
    public static final int TYPE_FOOTER = 1;
    private int colorDark;

    public NavigationItem(int title, int color, int colorDark, int icon, int type) {
        this.title = title;
        this.color = color;
        this.icon = icon;
        this.type = type;
        this.colorDark = colorDark;
    }

    public int geTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    public int getIcon() {
        return icon;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getType() {

        return type;
    }

    public int getColorDark() {
        return colorDark;
    }
}
