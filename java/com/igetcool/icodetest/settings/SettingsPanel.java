package com.igetcool.icodetest.settings;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.utils.I18nUtils;
import com.intellij.ide.util.PropertiesComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * SettingsPanel 类提供了 iCodeTest 插件设置的用户界面。
 * 它继承自 JPanel 并包含用于配置插件行为的各种组件，例如单选按钮、文本字段和文本区域。
 * 用户可以通过这个面板来修改插件的设置，并通过应用或重置按钮来保存或撤销更改。
 */
public class SettingsPanel extends JPanel {

    // JUnit 版本选择的单选按钮
    private JRadioButton jUnit4RadioButton;
    private JRadioButton jUnit5RadioButton;

    // 请求风格选择的单选按钮
    private JRadioButton styleCallRadio;
    private JRadioButton styleMockRadio;

    // 包名文本字段
    private JTextField packageNameTextField;
    // 类名文本字段
    private JTextField classNameTextField;
    // JUnit 4 的类体文本区域
    private JTextArea classBodyTextArea4;
    // JUnit 5 的类体文本区域
    private JTextArea classBodyTextArea5;

    /**
     * SettingsPanel 构造函数。
     * 初始化用户界面组件并设置事件监听器。
     */
    public SettingsPanel() {
        initUI();
        // 首次打开时，根据当前设置的 jUnit 版本来控制文本区域的显示
        setTextAreaVisibilityBasedOnJUnitVersion();
        initRadioButtonListeners();
    }

    private void setTextAreaVisibilityBasedOnJUnitVersion() {
        String currentVersion = getPropertyValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED, SettingsManager.INSTANCE.getJUnitVersion());
        setVersionRadioButtonSelection(currentVersion);
        if (Constants.DEFAULT_VERSION_JUNIT_4.equals(currentVersion)) {
            classBodyTextArea4.setVisible(true);
            classBodyTextArea5.setVisible(false);
        } else if (Constants.DEFAULT_VERSION_JUNIT_5.equals(currentVersion)) {
            classBodyTextArea4.setVisible(false);
            classBodyTextArea5.setVisible(true);
        }
    }

    private void initUI() {
        // 创建按钮组，确保单选
        ButtonGroup buttonGroup = new ButtonGroup();

        JLabel versionLabel = new JLabel(I18nUtils.chooseJUnitVersion());
        jUnit4RadioButton = new JRadioButton(Constants.DEFAULT_VERSION_JUNIT_4);
        jUnit4RadioButton.setSelected(true); // 默认选中 jUnit4
        jUnit5RadioButton = new JRadioButton(Constants.DEFAULT_VERSION_JUNIT_5);
        buttonGroup.add(jUnit4RadioButton);
        buttonGroup.add(jUnit5RadioButton);

        ButtonGroup typeButtonGroup = new ButtonGroup();

        JLabel typeLabel = new JLabel(I18nUtils.chooseRequestStyle());
        styleCallRadio = new JRadioButton(Constants.DEFAULT_REQUEST_STYLE_CALL);
        styleCallRadio.setSelected(true); // 默认选中 MethodCall
        styleMockRadio = new JRadioButton(Constants.DEFAULT_REQUEST_STYLE_MOCK);
        typeButtonGroup.add(styleCallRadio);
        typeButtonGroup.add(styleMockRadio);

        JLabel templatePkgLabel = new JLabel(I18nUtils.commonTemplatePackage());
        packageNameTextField = new JTextField(SettingsManager.INSTANCE.getCommonPackageName(), 60);

        JLabel templateClsLabel = new JLabel(I18nUtils.commonTemplateClass());
        classNameTextField = new JTextField(SettingsManager.INSTANCE.getCommonClassName(), 60);

        JLabel templateTextLabel = new JLabel(I18nUtils.commonTemplateBody());
        classBodyTextArea4 = new JTextArea(20, 60);
        classBodyTextArea4.setText(SettingsManager.INSTANCE.getCommonClassBody4());
        classBodyTextArea5 = new JTextArea(20, 60);
        classBodyTextArea5.setText(SettingsManager.INSTANCE.getCommonClassBody5());

        // 使用BoxLayout布局管理器，按垂直方向排列组件
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel junitRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        junitRadioPanel.add(versionLabel);
        junitRadioPanel.add(jUnit4RadioButton);
        junitRadioPanel.add(jUnit5RadioButton);

        JPanel styleRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styleRadioPanel.add(typeLabel);
        styleRadioPanel.add(styleCallRadio);
        styleRadioPanel.add(styleMockRadio);

        JPanel commonPkgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commonPkgPanel.add(templatePkgLabel);
        commonPkgPanel.add(packageNameTextField);

        JPanel commonClsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commonClsPanel.add(templateClsLabel);
        commonClsPanel.add(classNameTextField);

        JPanel commonTextPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commonTextPanel.add(templateTextLabel);
        commonTextPanel.add(classBodyTextArea4);
        commonTextPanel.add(classBodyTextArea5);

        // 将单选按钮面板和文本域面板添加到主面板
        add(junitRadioPanel);
        add(styleRadioPanel);
        add(commonPkgPanel);
        add(commonClsPanel);
        add(commonTextPanel);
    }

    /**
     * 检查当前设置是否已修改。
     * 比较 UI 组件中的值与 PropertiesComponent 中存储的值，以确定是否有修改。
     *
     * @return 如果有修改，则返回 true。
     */
    public boolean isModified() {
        // 获取当前的属性值
        String currentVersionSelected = getPropertyValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED, SettingsManager.INSTANCE.getJUnitVersion());
        String currentStyleSelected = getPropertyValue(Constants.SETTINGS_PLUGIN_MOCKS_SELECTED, SettingsManager.INSTANCE.getRequestStyle());
        String packageNameValue = getPropertyValue(Constants.SETTINGS_PLUGIN_PACKAGE_NAME, SettingsManager.INSTANCE.getCommonPackageName());
        String classNameValue = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_NAME, SettingsManager.INSTANCE.getCommonClassName());
        String classBodyValue4 = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY4, SettingsManager.INSTANCE.getCommonClassBody4());
        String classBodyValue5 = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY5, SettingsManager.INSTANCE.getCommonClassBody5());

        // 比较当前属性值与UI组件的值是否一致
        boolean isVersionRadioButtonModified = !currentVersionSelected.equals(
                jUnit5RadioButton.isSelected() ? Constants.DEFAULT_VERSION_JUNIT_5 : Constants.DEFAULT_VERSION_JUNIT_4
        );
        boolean isTypeRadioButtonModified = !currentStyleSelected.equals(
                styleMockRadio.isSelected() ? Constants.DEFAULT_REQUEST_STYLE_MOCK : Constants.DEFAULT_REQUEST_STYLE_CALL
        );
        boolean isPackageNameModified = !packageNameValue.equals(classNameTextField.getText());
        boolean isClassNameModified = !classNameValue.equals(classNameTextField.getText());
        boolean isClassBody4Modified = !classBodyValue4.equals(classBodyTextArea4.getText());
        boolean isClassBody5Modified = !classBodyValue5.equals(classBodyTextArea5.getText());

        // 返回是否有任何修改
        return isVersionRadioButtonModified
                || isTypeRadioButtonModified
                || isPackageNameModified
                || isClassNameModified
                || isClassBody4Modified
                || isClassBody5Modified;
    }

    /**
     * 应用当前设置。
     * 将 UI 组件中的值保存到 PropertiesComponent 中。
     */
    public void apply() {
        setPropertyValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED,
                jUnit5RadioButton.isSelected() ? Constants.DEFAULT_VERSION_JUNIT_5 : Constants.DEFAULT_VERSION_JUNIT_4
        );
        setPropertyValue(Constants.SETTINGS_PLUGIN_MOCKS_SELECTED,
                styleMockRadio.isSelected() ? Constants.DEFAULT_REQUEST_STYLE_MOCK : Constants.DEFAULT_REQUEST_STYLE_CALL
        );
        setPropertyValue(Constants.SETTINGS_PLUGIN_PACKAGE_NAME, packageNameTextField.getText());
        setPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_NAME, classNameTextField.getText());
        setPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY4, classBodyTextArea4.getText());
        setPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY5, classBodyTextArea5.getText());
    }

    /**
     * 重置设置到默认值。
     * 从 PropertiesComponent 中读取值并更新 UI 组件。
     */
    public void reset() {
        String storedVersion = getPropertyValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED, SettingsManager.INSTANCE.getJUnitVersion());
        String storedStyle = getPropertyValue(Constants.SETTINGS_PLUGIN_MOCKS_SELECTED, SettingsManager.INSTANCE.getRequestStyle());
        String packageNameValue = getPropertyValue(Constants.SETTINGS_PLUGIN_PACKAGE_NAME, SettingsManager.INSTANCE.getCommonPackageName());
        String classNameValue = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_NAME, SettingsManager.INSTANCE.getCommonClassName());
        String classBodyValue4 = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY4, SettingsManager.INSTANCE.getCommonClassBody4());
        String classBodyValue5 = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY5, SettingsManager.INSTANCE.getCommonClassBody5());

        setVersionRadioButtonSelection(storedVersion);
        setTypeRadioButtonSelection(storedStyle);
        packageNameTextField.setText(packageNameValue);
        classNameTextField.setText(classNameValue);
        classBodyTextArea4.setText(classBodyValue4);
        classBodyTextArea5.setText(classBodyValue5);
    }

    private void setVersionRadioButtonSelection(String version) {
        jUnit4RadioButton.setSelected(Constants.DEFAULT_VERSION_JUNIT_4.equals(version));
        jUnit5RadioButton.setSelected(Constants.DEFAULT_VERSION_JUNIT_5.equals(version));
    }

    private void setTypeRadioButtonSelection(String style) {
        styleCallRadio.setSelected(Constants.DEFAULT_REQUEST_STYLE_CALL.equals(style));
        styleMockRadio.setSelected(Constants.DEFAULT_REQUEST_STYLE_MOCK.equals(style));
    }

    // 用于获取属性值
    private String getPropertyValue(String propertyName, String defaultValue) {
        return PropertiesComponent.getInstance().getValue(propertyName, defaultValue);
    }

    // 用于设置属性值
    private void setPropertyValue(String propertyName, String propertyValue) {
        PropertiesComponent.getInstance().setValue(propertyName, propertyValue);
    }

    private void initRadioButtonListeners() {
        ItemListener radioButtonListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (e.getSource() == jUnit4RadioButton) {
                        classBodyTextArea4.setVisible(true);
                        classBodyTextArea5.setVisible(false);
                    } else if (e.getSource() == jUnit5RadioButton) {
                        classBodyTextArea4.setVisible(false);
                        classBodyTextArea5.setVisible(true);
                    }
                }
            }
        };
        jUnit4RadioButton.addItemListener(radioButtonListener);
        jUnit5RadioButton.addItemListener(radioButtonListener);
    }
}