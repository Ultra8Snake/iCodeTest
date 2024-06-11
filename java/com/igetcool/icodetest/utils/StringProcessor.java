package com.igetcool.icodetest.utils;

/**
 * 字符串处理工具类
 */
public class StringProcessor {

    /**
     * 将字符串的第一个字符转换为大写。
     * <p>
     * 如果输入的字符串为null或空字符串，方法将直接返回原始字符串。
     * 这个方法对于给定的字符串，只修改其第一个字符的大小写，其余部分保持不变。
     *
     * @param input 需要转换的原始字符串。
     * @return 返回第一个字符大写后的字符串。
     */
    public static String toCapitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * 将字符串的第一个字符转换为小写。
     * <p>
     * 如果输入的字符串为null或空字符串，方法将直接返回原始字符串。
     * 这个方法对于给定的字符串，只修改其第一个字符的大小写，其余部分保持不变。
     *
     * @param input 需要转换的原始字符串。
     * @return 返回第一个字符小写后的字符串。
     */
    public static String toLowercaseFirst(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }
}
