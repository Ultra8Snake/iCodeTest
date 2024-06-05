package com.igetcool.icodetest.models;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class ClassMetaInfo {

    private String absolutePath;
    private String packageName;
    private String className;
    private String fileName;
    private String packageDirectory;
    private String qualifiedClassName;
    private String requestMappingUri;
    private List<PsiField> classFields;
    private List<PsiMethod> classMethods;

    private String finalAbsolutePath;
    private String finalClassName;
    private String finalFullPath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageDirectory() {
        return packageDirectory;
    }

    public void setPackageDirectory(String packageDirectory) {
        this.packageDirectory = packageDirectory;
    }

    public String getQualifiedClassName() {
        return qualifiedClassName;
    }

    public void setQualifiedClassName(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    public String getRequestMappingUri() {
        return requestMappingUri;
    }

    public void setRequestMappingUri(String requestMappingUri) {
        this.requestMappingUri = requestMappingUri;
    }

    public List<PsiField> getClassFields() {
        return classFields;
    }

    public void setClassFields(List<PsiField> classFields) {
        this.classFields = classFields;
    }

    public List<PsiMethod> getClassMethods() {
        return classMethods;
    }

    public void setClassMethods(List<PsiMethod> classMethods) {
        this.classMethods = classMethods;
    }

    public String getFinalAbsolutePath() {
        return finalAbsolutePath;
    }

    public void setFinalAbsolutePath(String finalAbsolutePath) {
        this.finalAbsolutePath = finalAbsolutePath;
    }

    public String getFinalClassName() {
        return finalClassName;
    }

    public void setFinalClassName(String finalClassName) {
        this.finalClassName = finalClassName;
    }

    public String getFinalFullPath() {
        return finalFullPath;
    }

    public void setFinalFullPath(String finalFullPath) {
        this.finalFullPath = finalFullPath;
    }
}
