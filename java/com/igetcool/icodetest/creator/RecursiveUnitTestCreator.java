package com.igetcool.icodetest.creator;

import com.igetcool.icodetest.enums.OperateType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecursiveUnitTestCreator extends AnAction {
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
        VirtualFile virtualDir = virtualFile.getParent();
        if (virtualDir == null) {
            return;
        }
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualDir);
        if (psiDirectory == null) {
            return;
        }
        List<PsiJavaFile> allFiles = findAllFiles(psiDirectory);
        allFiles.forEach(file -> System.out.println("file: " + file.getVirtualFile().getPath()));
        AnActionProcessor.process(project, allFiles, OperateType.RECURSIVE);
    }

    private List<PsiJavaFile> findAllFiles(PsiDirectory directory) {
        PsiFile[] files = directory.getFiles();
        List<PsiJavaFile> filesList = new ArrayList<>();
        for (PsiFile psiFile : files) {
            if (psiFile instanceof PsiJavaFile) {
                filesList.add((PsiJavaFile) psiFile);
            }
        }
        PsiDirectory[] subDirs = directory.getSubdirectories();
        for (PsiDirectory subDir : subDirs) {
            filesList.addAll(findAllFiles(subDir));
        }
        return filesList;
    }

}
