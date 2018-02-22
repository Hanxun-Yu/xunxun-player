package org.crashxun.player.xunxun.samba;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;

/**
 * XML配置信息读写类
 * Created by Lizhuang on 2016/11/28.
 */

public class SharedPreferencesUtil {

    //存储的sharedpreferences文件名
    private static final String FILE_NAME = "setting";

    /**
     * 保存数据到文件
     *
     * @param context
     * @param key
     * @param data
     */
    public static void saveData(Context context,String fileName, String key, Object data) {
        try {
            //利用java反射机制将XML文件自定义存储
            Field field;
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(context);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = new File(context.getObbDir().getPath());
            // 修改mPreferencesDir变量的值
            field.set(obj, file);

            String type = data.getClass().getSimpleName();
            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if ("Integer".equals(type)) {
                editor.putInt(key, (Integer) data);
            } else if ("Boolean".equals(type)) {
                editor.putBoolean(key, (Boolean) data);
            } else if ("String".equals(type)) {
                editor.putString(key, (String) data);
            } else if ("Float".equals(type)) {
                editor.putFloat(key, (Float) data);
            } else if ("Long".equals(type)) {
                editor.putLong(key, (Long) data);
            }

            editor.commit();
        } catch (Exception e) {
            Log.e("SharedPreferencesUtil", "XML配置文件保存操作异常" + e.getMessage());
            //CommFunc.ToastPromptMsg("XML配置文件保存操作失败");
        }
    }

    /**
     * 从文件中读取数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData(Context context, String fileName,String key, Object defValue) {
        try {
            //利用java反射机制将XML文件自定义存储
            Field field;
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(context);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = new File(context.getObbDir().getPath());
            // 修改mPreferencesDir变量的值
            field.set(obj, file);

            String type = defValue.getClass().getSimpleName();
            SharedPreferences sharedPreferences = context.getSharedPreferences
                    (fileName, Context.MODE_PRIVATE);

            //defValue为为默认值，如果当前获取不到数据就返回它
            if ("Integer".equals(type)) {
                return sharedPreferences.getInt(key, (Integer) defValue);
            } else if ("Boolean".equals(type)) {
                return sharedPreferences.getBoolean(key, (Boolean) defValue);
            } else if ("String".equals(type)) {
                return sharedPreferences.getString(key, (String) defValue);
            } else if ("Float".equals(type)) {
                return sharedPreferences.getFloat(key, (Float) defValue);
            } else if ("Long".equals(type)) {
                return sharedPreferences.getLong(key, (Long) defValue);
            }

            return null;
        } catch (Exception e) {
            return defValue;
        }
    }
}