package com.igetcool.icodetest.models;

import com.intellij.psi.PsiType;

import java.util.Map;

public class MethodMetaInfo {

    private final String methodName;

    private String requestUri;

    private String requestType;

    private Map<String, PsiType> argsNameAndTypes;

    private PsiType returnType;

    private String methodBody;

    public MethodMetaInfo(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
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

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}