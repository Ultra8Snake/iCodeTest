package com.igetcool.icodetest.boot;

import com.igetcool.icodetest.constants.Constants;
import com.intellij.ide.util.PropertiesComponent;

public enum SettingsManager {

    INSTANCE;

    private String jUnitVersion;
    private String requestStyle;
    private String commonPackageName;
    private String commonClassName;
    private String commonClassBody4;
    private String commonClassBody5;

    SettingsManager() {
        this.jUnitVersion = Constants.DEFAULT_VERSION_JUNIT_4;
        this.requestStyle = Constants.DEFAULT_REQUEST_STYLE_CALL;
        this.commonPackageName = Constants.DEFAULT_COMMON_PACKAGE_NAME;
        this.commonClassName = Constants.DEFAULT_COMMON_CLASS_NAME;
        this.commonClassBody4 = Constants.DEFAULT_COMMON_CLASS_BODY_4;
        this.commonClassBody5 = Constants.DEFAULT_COMMON_CLASS_BODY_5;
        System.out.println("\nFirst Time SettingsManager ToString(): " + this.toString());
    }

    public void loadSettings() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        try {
            jUnitVersion = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED, Constants.DEFAULT_VERSION_JUNIT_4);
            requestStyle = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_MOCKS_SELECTED, Constants.DEFAULT_REQUEST_STYLE_CALL);
            commonPackageName = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_PACKAGE_NAME, Constants.DEFAULT_COMMON_PACKAGE_NAME);
            commonClassName = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_CLASS_NAME, Constants.DEFAULT_COMMON_CLASS_NAME);
            commonClassBody4 = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_CLASS_BODY4, Constants.DEFAULT_COMMON_CLASS_BODY_4);
            commonClassBody5 = propertiesComponent.getValue(Constants.SETTINGS_PLUGIN_CLASS_BODY5, Constants.DEFAULT_COMMON_CLASS_BODY_5);

            System.out.println("\nOnce Again SettingsManager ToString(): " + this.toString());
        } catch (Exception e) {
            e.printStackTrace();
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

    public String getCommonClassBody() {
        return SettingsManager.INSTANCE.getJUnitVersion().equals(Constants.DEFAULT_VERSION_JUNIT_4)
                ? getCommonClassBody4()
                : getCommonClassBody5();
    }
}
