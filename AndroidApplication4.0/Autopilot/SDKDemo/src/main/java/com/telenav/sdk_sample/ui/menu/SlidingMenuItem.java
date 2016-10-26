package com.telenav.sdk_sample.ui.menu;

/**
 * Defines the structure of a list item from the sliding menu of
 * Created by AlexandraV on 23.09.2015.
 */
public class SlidingMenuItem {

    public static final int ITEM_SUBTITLE_TYPE = 0;

    public static final int ITEM_RADIO_TYPE = 1;

    public static final int ITEM_CHECKBOX_TYPE = 2;

    public static final int ITEM_MAP_DATA_PATH = 3;

    public static final int ITEM_IP_ADDRESS = 4;

    private int menuItemId;

    private String menuItemLabel;

    private boolean menuItemSelected;

    private int menuItemType;

    public SlidingMenuItem(int menuItemId, String menuItemLabel, int menuItemType, boolean menuItemSelected){
        this.menuItemId = menuItemId;
        this.menuItemLabel = menuItemLabel;
        this.menuItemType = menuItemType;
        this.menuItemSelected = menuItemSelected;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemLabel() {
        return menuItemLabel;
    }

    public void setMenuItemLabel(String menuItemLabel) {
        this.menuItemLabel = menuItemLabel;
    }

    public boolean isMenuItemSelected() {
        return menuItemSelected;
    }

    public void setMenuItemSelected(boolean menuItemSelected) {
        this.menuItemSelected = menuItemSelected;
    }

    public int getMenuItemType() {
        return menuItemType;
    }

    public void setMenuItemType(int menuItemType) {
        this.menuItemType = menuItemType;
    }
}
