package com.igetcool.icodetest.models;

import java.util.List;

public class MethodCoreBase {

    private final MethodMetaInfo methodMetaInfo;
    private final List<MethodCallInfo> methodCallInfos;

    public MethodCoreBase(MethodMetaInfo methodMetaInfo, List<MethodCallInfo> methodCallInfos) {
        this.methodMetaInfo = methodMetaInfo;
        this.methodCallInfos = methodCallInfos;
    }

    public MethodMetaInfo getMethodMetaInfo() {
        return methodMetaInfo;
    }

    public List<MethodCallInfo> getMethodCallInfos() {
        return methodCallInfos;
    }
}
