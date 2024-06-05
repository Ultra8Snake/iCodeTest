package com.igetcool.icodetest.creator;

import com.igetcool.icodetest.popup.CustomMethodListPopup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomUnitTestCreator extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = null;
        if (editor != null) {
            virtualFile = fileDocumentManager.getFile(editor.getDocument());
        }
        if (virtualFile == null) {
            return;
        }
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile == null) {
            return;
        }
        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        List<PsiMethod> methods = new ArrayList<>();
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            PsiMethod[] classMethods = psiClass.getMethods();
            methods.addAll(Arrays.asList(classMethods));
            new CustomMethodListPopup(project, editor.getDocument(), psiJavaFile, methods).show();
        }
    }

}
