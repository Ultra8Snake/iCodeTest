package com.igetcool.icodetest.extractor;

import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.MethodMetaInfo;
import com.intellij.psi.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CodeExtractor 类提供了从Psi元素中提取代码元数据的功能。
 */
public class MethodMetaInfoExtractor {

    /**
     * 从给定的PsiMethod对象中提取方法的元信息。
     *
     * @param method 要提取信息的PsiMethod对象
     * @return 包含方法元信息的MethodMetaInfo对象
     */
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

    /**
     * 从给定的PsiMethod对象中提取@RequestMapping相关的注解值。
     *
     * @param methodMetaInfo 用于存储提取信息的MethodMetaInfo对象
     * @param psiMethod      要提取信息的PsiMethod对象
     */
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

    /**
     * 从给定的字符串中提取方法名。
     *
     * @param methodInvoke 包含方法调用的字符串
     * @return 提取的方法名，如果没有找到，则返回null
     */
    public static String extractMethodName(String methodInvoke) {
        String regex = "\\s*(\\w+)\\s*\\.\\s*(\\w+)\\s*\\(\\s*.*?\\s*\\)\\s*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodInvoke);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }

    /**
     * 从给定的字符串中提取方法的参数。
     *
     * @param methodInvoke 包含方法调用的字符串
     * @return 提取的方法参数字符串
     */
    public static String extractMethodArguments(String methodInvoke) {
        return methodInvoke.substring(
                methodInvoke.indexOf('(') + 1,
                methodInvoke.indexOf(')') + 1
        );
    }
}
