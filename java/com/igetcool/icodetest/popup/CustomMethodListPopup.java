package com.igetcool.icodetest.popup;

import com.igetcool.icodetest.creator.AnActionProcessor;
import com.igetcool.icodetest.enums.OperateType;
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

public class CustomMethodListPopup {

    private final Project project;
    private final Document document;
    private final PsiJavaFile psiJavaFile;
    private final String className;
    private final List<PsiMethod> methods;

    private ListPopup popup;

    public CustomMethodListPopup(Project project,
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

    public void show() {
        JBList<PsiMethod> methodList = new JBList<>(new DefaultListModel<>());
        methodList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DefaultListModel<PsiMethod> model = (DefaultListModel<PsiMethod>) methodList.getModel();
        for (PsiMethod method : methods) {
            model.addElement(method);
        }
        popup = JBPopupFactory
                .getInstance()
                .createListPopup(
                        new BaseListPopupStep<>(
                                String.format("请选择 <%s> 方法", className),
                                methods
                        ) {
                            @Override
                            public @Nullable PopupStep<?> onChosen(PsiMethod selectedValue, boolean finalChoice) {
                                if (!finalChoice) {
                                    return PopupStep.FINAL_CHOICE;
                                }
                                return doFinalStep(() -> {
                                    System.out.println("selectedValue.getName() = " + selectedValue.getName());
                                    SwingUtilities.invokeLater(() -> {
                                        AnActionProcessor.process(project, psiJavaFile, selectedValue.getName(), OperateType.CUSTOM);
                                    });
                                });
                            }

                            @Override
                            public @NotNull String getTextFor(PsiMethod value) {
                                StringBuilder sb = new StringBuilder(value.getName());
                                sb.append("(");
                                PsiParameterList parameterList = value.getParameterList();
                                PsiParameter[] parameters = parameterList.getParameters();
                                if (parameters.length > 0) {
                                    for (PsiParameter parameter : parameters) {
                                        sb.append(parameter.getType().getPresentableText()).append(",");
                                    }
                                    sb.deleteCharAt(sb.length() - 1);
                                }
                                sb.append(")");
                                return sb.toString();
                            }
                        });
        popup.showInCenterOf(EditorFactory.getInstance().getEditors(document, project)[0].getComponent());
    }

    private void closePopup() {
        popup.cancel();
    }
}