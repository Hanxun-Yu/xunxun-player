package org.crashxun.player.widget.xunxun.menu;

import java.util.Arrays;
import java.util.List;

public class MenuBean {
    public String menuID;
    public String menuName;
    public String superMenuID;//if null then rootMenu
    public List<MenuItemBean> items;

    public static class MenuItemBean {
        enum ItemType {
            activity, checkbox, radiobutton, menu
        }
        public String itemID;
        public String itemName;
        public ItemType itemType;
        public String itemIcon;
        /**
         * ActivityParams
         * 0.package 1.activity
         * CheckboxParams
         * 0.action 1.checked
         * RadiobtnParams
         * 0.action 1.checked 2.radioID
         * MenuParams
         * 0.childMenuID
         */
        public String[] itemParams;

        @Override
        public String toString() {
            return "MenuItemBean{" +
                    "itemID='" + itemID + '\'' +
                    ", itemName='" + itemName + '\'' +
                    ", itemType=" + itemType +
                    ", itemIcon='" + itemIcon + '\'' +
                    ", itemParams=" + Arrays.toString(itemParams) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MenuBean{" +
                "menuID='" + menuID + '\'' +
                ", menuName='" + menuName + '\'' +
                ", superMenuID='" + superMenuID + '\'' +
                ", items=" + items +
                '}';
    }
}