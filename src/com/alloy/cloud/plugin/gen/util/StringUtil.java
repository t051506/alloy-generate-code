package com.alloy.cloud.plugin.gen.util;

import java.util.Random;

/**
 * 字符串工具类
 *
 * @author tn_kec
 * @since 2019-10-12
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 首字母小写
     *
     * @param s
     * @return
     */
    public static String firstLowerCase(String s) {
        if (s == null || s.length() == 0)
            return s;

        if (s.charAt(0) >= 65 && s.charAt(0) <= 90) {
            char[] chars = s.toCharArray();
            chars[0] += 32;
            return String.valueOf(chars);
        }

        return s;
    }

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    public static String firstUpperCase(String s) {
        if (s == null || s.length() == 0)
            return s;

        if (s.charAt(0) >= 97 && s.charAt(0) <= 122) {
            char[] chars = s.toCharArray();
            chars[0] -= 32;
            return String.valueOf(chars);
        }

        return s;
    }

    /**
     * 生成序列号
     *
     * @return
     */
    public static String serial() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        // 正负号生成
        if (random.nextFloat() > 0.5F) {
            builder.append("-");
        }
        // 首位不能为0
        builder.append(random.nextInt(9) + 1);
        // 生成剩余位数
        do {
            builder.append(random.nextInt(10));
        } while (builder.length() < 18);
        // 加上结束符号
        builder.append("L");
        return builder.toString();
    }

    /**
     * 下划线中横线命名转大驼峰命名（属性名）
     *
     * @param s 字符串
     * @return 结果
     */
    public static String toUpperHump(String s) {
        if (isEmpty(s))
            return s;
        int size = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '_')
                size++;
        }
        if (size == 0) {
            return firstUpperCase(s);
        }

        char[] chars = new char[s.length() - size];
        boolean upperCase = true;
        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                upperCase = true;
                continue;
            }
            if (upperCase && c >= 97 && c <= 122) {
                chars[index++] = (char) (c - 32);
            } else {
                chars[index++] = c;
            }
            upperCase = false;
        }
        return new String(chars);
    }

    /**
     * 下划线中横线命名转小驼峰命名（属性名）
     *
     * @param s 字符串
     * @return 结果
     */
    public static String toLowerHump(String s) {
        return firstLowerCase(toUpperHump(s));
    }
}
