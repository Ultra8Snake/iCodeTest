package com.igetcool.icodetest.models;

public class CommonTextEvent {

    private String absolutePath;
    private String qualifiedName;
    private String className;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
