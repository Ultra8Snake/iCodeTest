package com.igetcool.icodetest.models.event;

import com.igetcool.icodetest.models.ClassMetaInfo;
import com.intellij.psi.PsiType;

import java.util.Set;

public class DefaultTextEvent {

    private final ClassMetaInfo classMetaInfo;
    private final Set<PsiType> testClassImportSet;
    private final String testClassFieldText;
    private final String testClassMethodText;
    private final String includeMethodName;  // 选中的方法，同时用于生成单独的测试文件

    public DefaultTextEvent(
            ClassMetaInfo classMetaInfo,
            Set<PsiType> testClassImportSet,
            String testClassFieldText,
            String testClassMethodText,
            String includeMethodName
    ) {
        this.classMetaInfo = classMetaInfo;
        this.testClassImportSet = testClassImportSet;
        this.testClassFieldText = testClassFieldText;
        this.testClassMethodText = testClassMethodText;
        this.includeMethodName = includeMethodName;
    }

    public ClassMetaInfo getClassMetaInfo() {
        return classMetaInfo;
    }

    public Set<PsiType> getTestClassImportSet() {
        return testClassImportSet;
    }

    public String getTestClassFieldText() {
        return testClassFieldText;
    }

    public String getTestClassMethodText() {
        return testClassMethodText;
    }

    public String getIncludeMethodName() {
        return includeMethodName;
    }
}
