package com.igetcool.icodetest.actions;

import com.igetcool.icodetest.enums.OperateType;
import com.igetcool.icodetest.processor.ProcessorContext;
import com.igetcool.icodetest.utils.I18nUtils;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 当前类自选方法快速生成单元测试
 */
public class CustomUnitTestAnAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        String text = I18nUtils.customAction();
        e.getPresentation().setText(text);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        ProcessorContext.create(OperateType.CUSTOM).process(project, editor);
    }

}
