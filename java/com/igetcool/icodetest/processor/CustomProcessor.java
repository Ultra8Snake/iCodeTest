package com.igetcool.icodetest.processor;

import com.igetcool.icodetest.dialog.MethodListDialog;
import com.igetcool.icodetest.enums.OperateType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomProcessor extends AbstractProcessor {

    @Override
    public OperateType operateType() {
        return OperateType.CUSTOM;
    }


    @Override
    public void process(@NotNull Project project, @NotNull Editor editor) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = fileDocumentManager.getFile(editor.getDocument());
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
            MethodListDialog dialog = new MethodListDialog(
                    project,
                    editor.getDocument(),
                    psiJavaFile,
                    methods
            );
            dialog.show(this);
        }
    }
}
