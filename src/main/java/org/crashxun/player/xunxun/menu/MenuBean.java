package org.crashxun.player.xunxun.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBean {
    public String menuID;
    public String menuName;
    public String superMenuID;//if null then rootMenu
    public List<MenuItemBean> items = new ArrayList<>();

    public MenuItemBean getItemByID(String id) {
        MenuItemBean ret = null;
        if(!items.isEmpty()) {
            for(MenuItemBean item : items) {
                if(item.itemID.equals(id)) {
                    ret = item;
                    break;
                }
            }
        }
        return ret;
    }

    public static class MenuItemBean {
        public enum ItemType {
            activity, checkbox, radiobutton, menu
        }

        public String itemID;
        public String itemName;
        public ItemType itemType;
        public String itemIcon;
        /**
         * ActivityParams : 0.package 1.activity
         * <p>
         * CheckboxParams : 0.action 1.checked
         * <p>
         * RadiobtnParams : 0.action 1.checked 2.radioID
         * <p>
         * MenuParams :  0.childMenuID
         */
        public String[] itemParams;
        public List<Map<String, String>> itemParamsKV = new ArrayList<>();


        @Override
        public String toString() {
            return "MenuItemBean{" +
                    "itemID='" + itemID + '\'' +
                    ", itemName='" + itemName + '\'' +
                    ", itemType=" + itemType +
                    ", itemIcon='" + itemIcon + '\'' +
                    ", itemParams=" + Arrays.toString(itemParams) +
                    ", itemParamsKV=" + itemParamsKV +
                    '}';
        }

        public static class Builder {
            MenuItemBean ret;

            public Builder() {
                ret = new MenuItemBean();
            }


            public Builder setItemID(String itemID) {
                ret.itemID = itemID;
                return this;
            }

            public Builder setItemName(String itemName) {
                ret.itemName = itemName;
                return this;
            }

            public Builder setItemType(ItemType itemType) {
                checkHasSetParams();
                ret.itemType = itemType;
                return this;
            }

            public Builder setItemIcon(String itemIcon) {
                ret.itemIcon = itemIcon;
                return this;
            }

            public Builder setActiviyParam(String packageName, String activityName) {
                checkIsItemTypeMatch(ItemType.activity);
                ret.itemParams = new String[]{packageName, activityName};
                return this;
            }

            public Builder setCheckboxParam(String action, boolean checked) {
                checkIsItemTypeMatch(ItemType.checkbox);
                ret.itemParams = new String[]{action, String.valueOf(checked)};
                return this;
            }

            public Builder setRadioButtonParam(String action, boolean checked, String radioID) {
                checkIsItemTypeMatch(ItemType.radiobutton);
                ret.itemParams = new String[]{action, String.valueOf(checked), radioID};
                return this;
            }

            public Builder setMenuParam(String childID) {
                checkIsItemTypeMatch(ItemType.menu);
                ret.itemParams = new String[]{childID};
                return this;
            }

            public Builder addParamKV(int mapIndex, String key, String value) {
                while (ret.itemParamsKV.size() <= mapIndex) {
                    ret.itemParamsKV.add(new HashMap<String, String>());
                }
                ret.itemParamsKV.get(mapIndex).put(key,value);
                return this;
            }

            public MenuItemBean build() {
                return ret;
            }

            private void checkIsItemTypeMatch(ItemType type) {
                if (ret.itemType == null)
                    throw new IllegalStateException("Please set itemType first!");

                if (ret.itemType != type)
                    throw new IllegalArgumentException("ItemType not match !!! cur:" + ret.itemType.name() + " tar:" + type.name());
            }

            private void checkHasSetParams() {
                if (ret.itemParams != null)
                    throw new IllegalStateException("Can't change ItemType after setting ItemParams ");
            }

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