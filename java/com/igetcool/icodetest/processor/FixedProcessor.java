package com.igetcool.icodetest.processor;

import com.igetcool.icodetest.enums.OperateType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

public class FixedProcessor extends AbstractProcessor {

    @Override
    public OperateType operateType() {
        return OperateType.FIXED;
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
        doProcess(project, (PsiJavaFile) psiFile, OperateType.FIXED);
    }

}
