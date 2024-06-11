package com.igetcool.icodetest.boot;

import com.igetcool.icodetest.constants.Constants;
import com.intellij.ide.util.PropertiesComponent;

/**
 * SettingsManager是一个单例枚举类，用于管理应用程序的设置。
 * 它提供了一个集中的方式来访问和修改应用程序的配置属性。
 * 此类使用PropertiesComponent来从持久化存储中加载和保存设置。
 */
public enum SettingsManager {

    INSTANCE;

    /**
     * JUnit版本设置。
     * 用于指定当前使用的JUnit版本。
     */
    private String jUnitVersion;

    /**
     * 请求调用风格设置。
     * 用于指定生成单元测试调用的风格。
     */
    private String requestStyle;

    /**
     * 公共包名设置。
     * 用于指定生成代码时使用的公共包名。
     */
    private String commonPackageName;

    /**
     * 公共类名设置。
     * 用于指定生成代码时使用的公共类名。
     */
    private String commonClassName;

    /**
     * 公共类体4设置。
     * 用于指定JUnit 4版本时的公共类体内容。
     */
    private String commonClassBody4;

    /**
     * 公共类体5设置。
     * 用于指定JUnit 5版本时的公共类体内容。
     */
    private String commonClassBody5;

    SettingsManager() {
        this.jUnitVersion = Constants.DEFAULT_VERSION_JUNIT_4;
        this.requestStyle = Constants.DEFAULT_REQUEST_STYLE_CALL;
        this.commonPackageName = Constants.DEFAULT_COMMON_PACKAGE_NAME;
        this.commonClassName = Constants.DEFAULT_COMMON_CLASS_NAME;
        this.commonClassBody4 = Constants.DEFAULT_COMMON_CLASS_BODY_4;
        this.commonClassBody5 = Constants.DEFAULT_COMMON_CLASS_BODY_5;
    }

    /**
     * 加载设置。
     * 从PropertiesComponent加载设置，如果设置不存在，则使用默认值。
     */
    public void loadSettings() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        try {
            jUnitVersion = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED, Constants.DEFAULT_VERSION_JUNIT_4);
            requestStyle = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_MOCKS_SELECTED, Constants.DEFAULT_REQUEST_STYLE_CALL);
            commonPackageName = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_PACKAGE_NAME, Constants.DEFAULT_COMMON_PACKAGE_NAME);
            commonClassName = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_CLASS_NAME, Constants.DEFAULT_COMMON_CLASS_NAME);
            commonClassBody4 = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_CLASS_BODY4, Constants.DEFAULT_COMMON_CLASS_BODY_4);
            commonClassBody5 = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_CLASS_BODY5, Constants.DEFAULT_COMMON_CLASS_BODY_5);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load settings", e);
        }
    }

    public String getJUnitVersion() {
        return jUnitVersion;
    }

    public String getRequestStyle() {
        return requestStyle;
    }

    public String getCommonPackageName() {
        return commonPackageName;
    }

    public String getCommonClassName() {
        return commonClassName;
    }

    public String getCommonClassBody4() {
        return commonClassBody4;
    }

    public String getCommonClassBody5() {
        return commonClassBody5;
    }

    /**
     * 获取公共类体。
     * 根据当前设置的JUnit版本返回相应的公共类体内容。
     *
     * @return 公共类体内容
     */
    public String getCommonClassBody() {
        return SettingsManager.INSTANCE.getJUnitVersion().equals(Constants.DEFAULT_VERSION_JUNIT_4)
                ? getCommonClassBody4()
                : getCommonClassBody5();
    }
}
