package com.igetcool.icodetest.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * SettingsConfigurable 类提供了 iCodeTest 插件的设置界面。
 * 它实现了 IntelliJ IDEA 的 Configurable 接口，允许用户通过图形界面配置插件的选项。
 * 此类负责创建设置表单，检测更改，以及应用或撤销这些更改。
 */
public class SettingsConfigurable implements Configurable {

    /**
     * 用于显示设置的面板。
     */
    private SettingsPanel settingsPanel;

    /**
     * 用于包装设置面板的滚动窗格。
     */
    private JScrollPane scrollPane;

    /**
     * 创建并返回设置界面的组件。
     * 如果 settingsPanel 尚未初始化，则创建一个新的实例，并将其包裹在滚动窗格中。
     *
     * @return 设置界面的组件
     */
    @Override
    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new SettingsPanel();
            // 将 settingsPanel 放入 JScrollPane 中
            scrollPane = new JBScrollPane(settingsPanel);
        }
        return scrollPane;
    }

    /**
     * 返回首选的焦点组件。
     * 此方法返回父类 Configurable 的 getPreferredFocusedComponent 方法的结果。
     *
     * @return 首选的焦点组件
     */
    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return Configurable.super.getPreferredFocusedComponent();
    }

    /**
     * 检查设置是否已被修改。
     * 如果 settingsPanel 报告已修改，则返回 true。
     *
     * @return 如果设置已修改，则返回 true
     */
    @Override
    public boolean isModified() {
        return settingsPanel.isModified();
    }

    /**
     * 应用设置更改。
     * 调用 settingsPanel 的 apply 方法以应用更改。
     *
     * @throws ConfigurationException 如果应用设置时发生错误
     */
    @Override
    public void apply() throws ConfigurationException {
        settingsPanel.apply();
    }

    /**
     * 重置设置到默认值。
     * 重置 settingsPanel 并将滚动条返回到顶部。
     */
    @Override
    public void reset() {
        // 确保滚动条在顶部
        if (scrollPane != null) {
            scrollPane.getVerticalScrollBar().setValue(0);
        }
        settingsPanel.reset();
    }

    /**
     * 释放 UI 资源。
     * 当设置界面不再需要时，此方法将被调用以释放相关资源。
     */
    @Override
    public void disposeUIResources() {
        // 重写此方法以确保组件被正确释放和重新创建
        settingsPanel = null;
        scrollPane = null;
    }

    /**
     * 返回设置界面的显示名称。
     * 此名称将用于标题栏或设置菜单中。
     *
     * @return 设置界面的显示名称
     */
    @Override
    public void cancel() {
        Configurable.super.cancel();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "iCodeTest";
    }
}