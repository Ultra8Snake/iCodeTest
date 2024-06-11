package com.igetcool.icodetest.dialog;

import com.igetcool.icodetest.enums.OperateType;
import com.igetcool.icodetest.processor.AbstractProcessor;
import com.igetcool.icodetest.utils.I18nUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * CustomMethodListPopup 类提供了一个自定义的方法列表弹出窗口。
 * 该弹出窗口用于显示一个 Java 类中的所有公共方法，并允许用户选择一个或多个方法。
 * 用户的选择将被用于后续的自定义操作，例如生成测试代码。
 */
public class MethodListDialog {

    private final Project project;
    private final Document document;
    private final PsiJavaFile psiJavaFile;
    private final String className;
    private final List<PsiMethod> methods;

    public MethodListDialog(Project project,
                            Document document,
                            PsiJavaFile psiJavaFile,
                            List<PsiMethod> methods
    ) {
        this.project = project;
        this.document = document;
        this.psiJavaFile = psiJavaFile;
        this.className = psiJavaFile.getName();
        this.methods = methods;
    }

    /**
     * 显示方法列表弹出窗口。
     */
    public void show(AbstractProcessor processor) {
        JBList<PsiMethod> methodList = new JBList<>(new DefaultListModel<>());
        methodList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DefaultListModel<PsiMethod> model = (DefaultListModel<PsiMethod>) methodList.getModel();
        for (PsiMethod method : methods) {
            model.addElement(method);
        }
        ListPopup popup = JBPopupFactory.getInstance()
                .createListPopup(new BaseListPopupStep<>(I18nUtils.selectMethod(className), methods) {
                    @Override
                    public @Nullable PopupStep<?> onChosen(PsiMethod selectedValue, boolean finalChoice) {
                        if (!finalChoice) {
                            return PopupStep.FINAL_CHOICE;
                        }
                        return doFinalStep(() -> {
                            System.out.println("selectedValue.getName() = " + selectedValue.getName());
                            SwingUtilities.invokeLater(() -> {
                                processor.doProcess(project, psiJavaFile, selectedValue.getName(), OperateType.CUSTOM);
                            });
                        });
                    }

                    @Override
                    public @NotNull String getTextFor(PsiMethod value) {
                        final StringBuilder result = new StringBuilder(value.getName());
                        result.append("(");
                        PsiParameterList parameterList = value.getParameterList();
                        PsiParameter[] parameters = parameterList.getParameters();
                        if (parameters.length > 0) {
                            for (PsiParameter parameter : parameters) {
                                result.append(parameter.getType().getPresentableText()).append(",");
                            }
                            result.deleteCharAt(result.length() - 1);
                        }
                        result.append(")");
                        return result.toString();
                    }
                });
        popup.showInCenterOf(EditorFactory.getInstance().getEditors(document, project)[0].getComponent());
    }
}