package com.igetcool.icodetest.creator;

import com.igetcool.icodetest.enums.OperateType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class FixedUnitTestCreator extends AnAction {

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
            Document document = editor.getDocument();
            PsiDocumentManager.getInstance(project).commitDocument(document);
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
        AnActionProcessor.process(project, (PsiJavaFile) psiFile, OperateType.FIXED);
    }

}
