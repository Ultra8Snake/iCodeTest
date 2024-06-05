package com.igetcool.icodetest.style;

import com.igetcool.icodetest.models.MethodCallInfo;
import com.igetcool.icodetest.utils.StringProcessor;
import com.igetcool.icodetest.utils.TypeClassifier;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.*;

public abstract class AbstractRequestStyle implements RequestStyle {

    public Set<PsiType> getFieldImportSet(List<PsiField> classFields) {
        final Set<PsiType> result = new HashSet<>();
        for (PsiField field : classFields) {
            result.add(field.getType());
        }
        return result;
    }

    public String allFieldObjectInstance(MethodCallInfo methodCallInfo) {
        final StringBuilder result = new StringBuilder();
        List<String> methodArgumentsTypeList = new ArrayList<>();
        Map<String, PsiType> argsNameAndTypes = methodCallInfo.getArgsNameAndTypes();
        if (argsNameAndTypes != null && !argsNameAndTypes.isEmpty()) {
            for (Map.Entry<String, PsiType> entry : argsNameAndTypes.entrySet()) {
                PsiType eachParamPsiType = entry.getValue();
                if (TypeClassifier.notCustomType(eachParamPsiType)) {
                    methodArgumentsTypeList.add(TypeClassifier.getDefaultValue(eachParamPsiType));
                } else {
                    if (TypeClassifier.isInterfaceOrAbstractClassOrEnum(eachParamPsiType)) {
                        methodArgumentsTypeList.add("null");
                    } else {
                        PsiClass psiClass = PsiUtil.resolveClassInType(eachParamPsiType);
                        if (psiClass != null) {
                            String instanceName = StringProcessor.toLowercaseFirst(psiClass.getName());
                            methodArgumentsTypeList.add(instanceName);
                            result.append(fieldObjectInstance(eachParamPsiType, instanceName));
                        } else {
                            methodArgumentsTypeList.add("null");
                        }
                    }
                }
            }
        }
        String instanceName = "";
        PsiType methodReturnType = methodCallInfo.getReturnType();
        if (methodReturnType == null || TypeClassifier.isVoidOrJavaLangVoid(methodReturnType)) {
            result.append(
                    String.format("\t\t// when(%s(%s)).thenReturn(%s);\n",
                            methodCallInfo.getFieldName() + "." + methodCallInfo.getMethodName(),
                            String.join(",", methodArgumentsTypeList),
                            instanceName
                    )
            );
            return result.toString();
        }
        if (TypeClassifier.notCustomType(methodReturnType)) {
            instanceName = TypeClassifier.getDefaultValue(methodReturnType);
        } else {
            if (TypeClassifier.isInterfaceOrAbstractClassOrEnum(methodReturnType)) {
                instanceName = "null";
            } else {
                PsiClass psiClass = PsiUtil.resolveClassInType(methodReturnType);
                if (psiClass != null) {
                    instanceName = StringProcessor.toLowercaseFirst(psiClass.getName());
                    result.append(fieldObjectInstance(methodReturnType, instanceName));
                } else {
                    instanceName = "null";
                }
            }
        }
        result.append(
                String.format("\t\twhen(%s(%s)).thenReturn(%s);\n",
                        methodCallInfo.getFieldName() + "." + methodCallInfo.getMethodName(),
                        String.join(",", methodArgumentsTypeList),
                        instanceName
                )
        );
        return result.toString();
    }

    public String fieldObjectInstance(PsiType psiType, String simpleName) {
        if (TypeClassifier.notCustomType(psiType)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (psiClass != null) {
            result.append(
                    String.format("\t\t%s %s = new %s();\n",
                            psiClass.getName(),
                            simpleName,
                            psiClass.getName()
                    )
            );
            for (PsiMethod method : psiClass.getMethods()) {
                if (method.getName().startsWith("set")) {
                    PsiParameterList parameterList = method.getParameterList();
                    for (PsiParameter parameter : parameterList.getParameters()) {
                        result.append(
                                String.format("\t\t%s.%s(%s);\n",
                                        simpleName,
                                        method.getName(),
                                        TypeClassifier.getDefaultValue(parameter.getType())
                                )
                        );
                    }
                }
            }
        }
        return result.toString();
    }
}
