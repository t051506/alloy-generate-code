package com.alloy.cloud.plugin.gen.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 渲染模板工具
 *
 * @author tn_kec
 * @since 20191022
 */
public class VmUtil {

    /**
     * 首字母小写
     *
     * @param s
     * @return
     */
    public String firstLowerCase(String s) {
        return StringUtil.firstLowerCase(s);
    }

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    public String firstUpperCase(String s) {
        return StringUtil.firstUpperCase(s);
    }

    /**
     * 字符串转小写
     *
     * @param s
     * @return
     */
    public String lowerCase(String s) {
        if (s == null)
            return null;
        return s.toLowerCase();
    }

    /**
     * 字符串转大写
     *
     * @param s
     * @return
     */
    public String upperCase(String s) {
        if (s == null)
            return null;
        return s.toUpperCase();
    }

    /**
     * 字符串转成大驼峰
     *
     * @param s
     * @return
     */
    public static String toUpperHump(String s) {
        return StringUtil.toUpperHump(s);
    }

    /**
     * 字符串转成小驼峰
     *
     * @param s
     * @return
     */
    public static String toLowerHump(String s) {
        return StringUtil.toLowerHump(s);
    }

    /**
     * 开始时间
     *
     * @return
     */
    public String getSince() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(LocalDateTime.now());
    }

    public String serial() {
        return StringUtil.serial();
    }

    /**
     * 根据类型获取基础类型
     *
     * @param type
     * @return
     */
    public String getBaseType(String type) {
        if (StringUtil.isEmpty(type)) {
            return null;
        }

        int index = type.lastIndexOf(".");
        if (index > -1) {
            return type.substring(index + 1);
        }
        return type;
    }
}
