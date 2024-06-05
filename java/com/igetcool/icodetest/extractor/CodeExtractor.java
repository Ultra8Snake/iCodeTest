package com.igetcool.icodetest.extractor;

import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.MethodMetaInfo;
import com.intellij.psi.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeExtractor {

    public static MethodMetaInfo extract(PsiMethod method) {
        MethodMetaInfo result = new MethodMetaInfo(method.getName());
        PsiParameterList parameterList = method.getParameterList();
        Map<String, PsiType> params = new LinkedHashMap<>();
        for (PsiParameter parameter : parameterList.getParameters()) {
            params.put(parameter.getName(), parameter.getType());
        }
        result.setArgsNameAndTypes(params);
        PsiType returnType = method.getReturnType();
        result.setReturnType(returnType);
        PsiCodeBlock body = method.getBody();
        if (body != null) {
            result.setMethodBody(body.getText());
        }
        getMethodRequestMappingUri(result, method);
        return result;
    }

    public static void getMethodRequestMappingUri(MethodMetaInfo methodMetaInfo, PsiMethod psiMethod) {
        PsiModifierList modifierList = psiMethod.getModifierList();
        for (PsiAnnotation annotation : modifierList.getAnnotations()) {
            PsiJavaCodeReferenceElement classReference = annotation.getNameReferenceElement();
            if (classReference != null) {
                PsiElement resolved = classReference.resolve();
                if (resolved instanceof PsiClass) {
                    String annotationQualifiedName = ((PsiClass) resolved).getQualifiedName();
                    if (Constants.ANNOTATION_TEXT_REQUEST_MAPPING.equals(annotationQualifiedName)
                            || Constants.ANNOTATION_TEXT_GET_MAPPING.equals(annotationQualifiedName)
                            || Constants.ANNOTATION_TEXT_POST_MAPPING.equals(annotationQualifiedName)) {
                        methodMetaInfo.setRequestType("get");
                        if (Constants.ANNOTATION_TEXT_POST_MAPPING.equals(annotationQualifiedName)) {
                            methodMetaInfo.setRequestType("post");
                        }
                        PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
                        if (value instanceof PsiLiteralExpression) {
                            Object valueObj = ((PsiLiteralExpression) value).getValue();
                            if (valueObj instanceof String) {
                                methodMetaInfo.setRequestUri((String) valueObj);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String extractMethodName(String methodInvoke) {
        String regex = "\\s*(\\w+)\\s*\\.\\s*(\\w+)\\s*\\(\\s*.*?\\s*\\)\\s*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodInvoke);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }

    public static String extractMethodArguments(String methodInvoke) {
        return methodInvoke.substring(
                methodInvoke.indexOf('(') + 1,
                methodInvoke.indexOf(')') + 1
        );
    }
}
