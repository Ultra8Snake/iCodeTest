package com.igetcool.icodetest.models;

import com.intellij.psi.PsiType;

import java.util.Map;

public class MethodCallInfo {

    private final String fieldName;

    private String methodName;

    private Map<String, PsiType> argsNameAndTypes;

    private PsiType returnType;

    private String methodBody;

    public MethodCallInfo(String fieldName) {
        this.fieldName = fieldName;
    }


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, PsiType> getArgsNameAndTypes() {
        return argsNameAndTypes;
    }

    public void setArgsNameAndTypes(Map<String, PsiType> argsNameAndTypes) {
        this.argsNameAndTypes = argsNameAndTypes;
    }

    public PsiType getReturnType() {
        return returnType;
    }

    public void setReturnType(PsiType returnType) {
        this.returnType = returnType;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }

    public String getFieldName() {
        return fieldName;
    }
}