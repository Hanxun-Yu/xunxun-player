package org.crashxun.player.widget.xunxun.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MenuBean {
    public String menuID;
    public String menuName;
    public String superMenuID;//if null then rootMenu
    public List<MenuItemBean> items = new ArrayList<>();

    public static class MenuItemBean {
        public enum ItemType {
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
        public List<Map<String,String>> itemParmasKV = new ArrayList<>();

        @Override
        public String toString() {
            return "MenuItemBean{" +
                    "itemID='" + itemID + '\'' +
                    ", itemName='" + itemName + '\'' +
                    ", itemType=" + itemType +
                    ", itemIcon='" + itemIcon + '\'' +
                    ", itemParams=" + Arrays.toString(itemParams) +
                    ", itemParmasKV=" + itemParmasKV +
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