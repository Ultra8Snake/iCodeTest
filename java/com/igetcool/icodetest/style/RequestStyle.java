package com.igetcool.icodetest.style;

import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.MethodCoreBase;
import com.igetcool.icodetest.models.MethodMetaInfo;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.List;
import java.util.Set;

public interface RequestStyle {

    String getStyle();

    Set<PsiType> getFieldImportSet(List<PsiField> classFields);

    Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases);

    String callMethod(MethodMetaInfo methodMetaInfo, ClassMetaInfo classMetaInfo);

    String generateTestClassField(List<PsiField> classFields, ClassMetaInfo classMetaInfo);

    String generateTestClassMethod(List<MethodCoreBase> methodCoreBases, ClassMetaInfo classMetaInfo);
}
