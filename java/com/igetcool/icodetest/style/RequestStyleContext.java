package com.igetcool.icodetest.style;

import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.MethodCoreBase;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.List;
import java.util.Set;

public class RequestStyleContext {

    private final RequestStyle requestStyle;

    public RequestStyleContext(RequestStyle requestStyle) {
        this.requestStyle = requestStyle;
    }

    public String getStyle() {
        return requestStyle.getStyle();
    }

    public Set<PsiType> getFieldImportSet(List<PsiField> psiFields) {
        return requestStyle.getFieldImportSet(psiFields);
    }

    public Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        return requestStyle.getMethodImportSet(methodCoreBases);
    }

    public String generateTestClassField(List<PsiField> classFields, ClassMetaInfo classMetaInfo) {
        return requestStyle.generateTestClassField(classFields, classMetaInfo);
    }

    public String generateTestClassMethod(List<MethodCoreBase> methodCoreBases, ClassMetaInfo classMetaInfo) {
        return requestStyle.generateTestClassMethod(methodCoreBases, classMetaInfo);
    }

}
