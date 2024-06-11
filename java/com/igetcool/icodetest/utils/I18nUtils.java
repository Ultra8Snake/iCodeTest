package com.igetcool.icodetest.utils;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * I18nProcessor 类提供了国际化（i18n）处理功能。
 * 它允许通过键值获取国际化后的字符串，并支持带参数的格式化字符串。
 */
public class I18nUtils extends DynamicBundle {

    @NonNls
    private static final String BUNDLE_NAME = "Messages";
    private static final I18nUtils INSTANCE = new I18nUtils();

    private I18nUtils() {
        super(BUNDLE_NAME);
    }

    /**
     * 获取国际化消息。
     * 根据提供的键从资源文件中获取对应的国际化消息。
     *
     * @param key 资源文件中的键。
     * @return 对应键的国际化消息字符串。
     */
    public @NotNull String message(@NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    public static String chooseJUnitVersion() {
        return INSTANCE.message("Settings.Choose.Junit.Version");
    }

    public static String chooseRequestStyle() {
        return INSTANCE.message("Settings.Common.Request.Style");
    }

    public static String commonTemplatePackage() {
        return INSTANCE.message("Settings.Common.Template.Package");
    }

    public static String commonTemplateClass() {
        return INSTANCE.message("Settings.Common.Template.Class");
    }

    public static String commonTemplateBody() {
        return INSTANCE.message("Settings.Common.Template.Body");
    }

    public static String selectMethod(String className) {
        return INSTANCE.message("Dialog.Select.Method", className);
    }

    public static String overrideAll() {
        return INSTANCE.message("Dialog.Override.All");
    }

    public static String overrideFile() {
        return INSTANCE.message("Dialog.Override.File");
    }

    public static String overrideFileExists(String fileName) {
        return INSTANCE.message("Dialog.Override.File.Exists", fileName);
    }

    public static String confirm() {
        return INSTANCE.message("Dialog.Confirm");
    }

    public static String cancel() {
        return INSTANCE.message("Dialog.Cancel");
    }

    public static String fixedAction() {
        return INSTANCE.message("Text.FixedUnitTestAction");
    }

    public static String customAction() {
        return INSTANCE.message("Text.CustomUnitTestAction");
    }

    public static String recursiveAction() {
        return INSTANCE.message("Text.RecursiveUnitTestAction");
    }

}
