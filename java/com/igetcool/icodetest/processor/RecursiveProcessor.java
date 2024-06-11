package com.igetcool.icodetest.processor;

import com.igetcool.icodetest.enums.OperateType;
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

public class RecursiveProcessor extends AbstractProcessor {

    @Override
    public OperateType operateType() {
        return OperateType.RECURSIVE;
    }

    @Override
    public void process(@NotNull Project project, @NotNull Editor editor) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = fileDocumentManager.getFile(editor.getDocument());
        if (virtualFile == null) {
            return;
        }
        // 获取 VirtualFile 所在的目录
        VirtualFile virtualDir = virtualFile.getParent();
        if (virtualDir == null) {
            return;
        }
        // 转换 VirtualFile 目录到 PsiDirectory
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualDir);
        if (psiDirectory == null) {
            return;
        }
        // 获取 PSI 文件列表（此处为当前文件所在目录的所有文件包括子目录文件）
        List<PsiJavaFile> allFiles = findAllFiles(psiDirectory);
        doProcess(project, allFiles, OperateType.RECURSIVE);
    }

    /**
     * 在指定的目录及其子目录下查找所有的Java文件。
     * 该方法递归地搜索给定目录及其所有子目录，并将找到的Java文件收集到列表中返回。
     *
     * @param directory 要搜索的起始目录。
     * @return 包含所有找到的PsiJavaFile对象的列表。
     */
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
