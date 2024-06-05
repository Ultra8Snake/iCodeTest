package com.igetcool.icodetest.settings;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.constants.Constants;
import com.intellij.ide.util.PropertiesComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SettingsPanel extends JPanel {

    private JRadioButton jUnit4RadioButton;
    private JRadioButton jUnit5RadioButton;

    private JRadioButton styleCallRadio;
    private JRadioButton styleMockRadio;

    private JTextField packageNameTextField;
    private JTextField classNameTextField;
    private JTextArea classBodyTextArea4;
    private JTextArea classBodyTextArea5;

    public SettingsPanel() {
        initUI();
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
        ButtonGroup buttonGroup = new ButtonGroup();

        JLabel versionLabel = new JLabel("选择JUnit的版本: ");
        jUnit4RadioButton = new JRadioButton(Constants.DEFAULT_VERSION_JUNIT_4);
        jUnit4RadioButton.setSelected(true);
        jUnit5RadioButton = new JRadioButton(Constants.DEFAULT_VERSION_JUNIT_5);
        buttonGroup.add(jUnit4RadioButton);
        buttonGroup.add(jUnit5RadioButton);

        ButtonGroup typeButtonGroup = new ButtonGroup();

        JLabel typeLabel = new JLabel("选择请求的风格: ");
        styleCallRadio = new JRadioButton(Constants.DEFAULT_REQUEST_STYLE_CALL);
        styleCallRadio.setSelected(true);
        styleMockRadio = new JRadioButton(Constants.DEFAULT_REQUEST_STYLE_MOCK);
        typeButtonGroup.add(styleCallRadio);
        typeButtonGroup.add(styleMockRadio);

        JLabel templatePkgLabel = new JLabel("公共模板（包名）：");
        packageNameTextField = new JTextField(SettingsManager.INSTANCE.getCommonPackageName(), 60);

        JLabel templateClsLabel = new JLabel("公共模板（类名）：");
        classNameTextField = new JTextField(SettingsManager.INSTANCE.getCommonClassName(), 60);

        JLabel templateTextLabel = new JLabel("公共模板（主体）：");
        classBodyTextArea4 = new JTextArea(20, 60);
        classBodyTextArea4.setText(SettingsManager.INSTANCE.getCommonClassBody4());
        classBodyTextArea5 = new JTextArea(20, 60);
        classBodyTextArea5.setText(SettingsManager.INSTANCE.getCommonClassBody5());

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

        add(junitRadioPanel);
        add(styleRadioPanel);
        add(commonPkgPanel);
        add(commonClsPanel);
        add(commonTextPanel);
    }

    public boolean isModified() {
        String currentVersionSelected = getPropertyValue(Constants.SETTINGS_PLUGIN_JUNIT_SELECTED, SettingsManager.INSTANCE.getJUnitVersion());
        String currentStyleSelected = getPropertyValue(Constants.SETTINGS_PLUGIN_MOCKS_SELECTED, SettingsManager.INSTANCE.getRequestStyle());
        String packageNameValue = getPropertyValue(Constants.SETTINGS_PLUGIN_PACKAGE_NAME, SettingsManager.INSTANCE.getCommonPackageName());
        String classNameValue = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_NAME, SettingsManager.INSTANCE.getCommonClassName());
        String classBodyValue4 = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY4, SettingsManager.INSTANCE.getCommonClassBody4());
        String classBodyValue5 = getPropertyValue(Constants.SETTINGS_PLUGIN_CLASS_BODY5, SettingsManager.INSTANCE.getCommonClassBody5());

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

        return isVersionRadioButtonModified
                || isTypeRadioButtonModified
                || isPackageNameModified
                || isClassNameModified
                || isClassBody4Modified
                || isClassBody5Modified;
    }

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

    private String getPropertyValue(String propertyName, String defaultValue) {
        return PropertiesComponent.getInstance().getValue(propertyName, defaultValue);
    }

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