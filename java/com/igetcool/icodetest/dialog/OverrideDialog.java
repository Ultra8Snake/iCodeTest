package com.igetcool.icodetest.dialog;

import com.igetcool.icodetest.utils.I18nUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;

/**
 * CustomOverrideDialog 类提供了一个自定义的对话框，用于在文件操作中询问用户是否覆盖已存在的文件。
 * 此类继承自 IntelliJ IDEA 的 DialogWrapper，用于创建图形用户界面中的模态对话框。
 * 对话框包含一个提示信息，询问用户是否要覆盖已存在的文件，并可选择性地提供一个复选框以覆盖所有冲突的文件。
 */
public class OverrideDialog extends DialogWrapper {

    /**
     * 要检查的最终类名。
     */
    private final String finalClassName;

    /**
     * 对话框中的复选框，用于选择是否覆盖所有文件。
     */
    private final JCheckBox checkBox;

    /**
     * 一个标志，指示是否显示复选框。
     * 显示复选按钮 true：显示 false：不显示
     */
    private final boolean showOverrideAll;

    /**
     * 用于存储复选框的状态，表示用户是否选择覆盖所有文件。
     */
    private boolean overrideAll;

    /**
     * 构造函数，初始化自定义覆盖对话框。
     *
     * @param project         当前的 IntelliJ IDEA 项目对象。
     * @param finalClassName  要检查是否存在的文件名。
     * @param showOverrideAll 一个布尔值，指示是否在对话框中显示“覆盖所有”的复选框。
     */
    public OverrideDialog(Project project, String finalClassName, boolean showOverrideAll) {
        super(project, true); // true 表示模态对话框
        this.finalClassName = finalClassName;
        this.showOverrideAll = showOverrideAll;
        checkBox = new JCheckBox(I18nUtils.overrideAll());
        checkBox.setSelected(false);
        checkBox.addItemListener(e -> overrideAll = checkBox.isSelected());
        setTitle(I18nUtils.overrideFile());
        init();
    }

    /**
     * 创建并返回对话框的中心面板。
     * 中心面板包含一个提示标签和一个可选的复选框。
     *
     * @return 对话框的中心面板。
     */
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1)); // 使用GridLayout排列组件
        // 添加提示标签
        JLabel label = new JLabel(I18nUtils.overrideFileExists(finalClassName));
        panel.add(label);
        if (showOverrideAll) {
            // 添加复选框
            panel.add(checkBox);
        }
        return panel;
    }

    /**
     * 创建并返回对话框的南面板，包含确认和取消按钮。
     *
     * @return 对话框的南面板。
     */
    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton(I18nUtils.confirm());
        JButton cancelButton = new JButton(I18nUtils.cancel());
        confirmButton.addActionListener(e -> {
            if (checkBox.isSelected()) {
                overrideAll = true;
            }
            doOKAction();
        });
        cancelButton.addActionListener(e -> doCancelAction());
        southPanel.add(confirmButton);
        southPanel.add(cancelButton);
        return southPanel;
    }

    /**
     * 检查用户是否选择覆盖所有文件。
     *
     * @return 如果用户选择覆盖所有，则返回 true。
     */
    public boolean isOverrideAll() {
        return overrideAll;
    }
}