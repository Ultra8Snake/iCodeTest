package com.igetcool.icodetest.popup;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;

public class CustomOverrideDialog extends DialogWrapper {

    private final String finalClassName;
    private final JCheckBox checkBox;
    private final boolean showOverrideAll;
    private boolean overrideAll;

    public CustomOverrideDialog(Project project, String finalClassName, boolean showOverrideAll) {
        super(project, true);
        this.finalClassName = finalClassName;
        this.showOverrideAll = showOverrideAll;
        checkBox = new JCheckBox("覆盖所有");
        checkBox.setSelected(false);
        checkBox.addItemListener(e -> overrideAll = checkBox.isSelected());
        setTitle("覆盖文件");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel label = new JLabel("文件 " + finalClassName + " 已存在，是否覆盖?");
        panel.add(label);
        if (showOverrideAll) {
            panel.add(checkBox);
        }
        return panel;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");
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

    public boolean isOverrideAll() {
        return overrideAll;
    }
}