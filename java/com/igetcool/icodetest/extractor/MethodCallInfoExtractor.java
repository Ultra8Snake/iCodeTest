package com.igetcool.icodetest.extractor;

import com.igetcool.icodetest.models.MethodCallInfo;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodCallInfoExtractor {

    /**
     * 从方法体中提取特定字段的方法调用信息。
     *
     * @param methodBody 方法体的文本内容
     * @param field      要提取方法调用信息的字段对应的 PsiField 对象
     * @return 如果找到方法调用，返回包含方法调用信息的 MethodCallInfo 对象；否则返回 null
     */
    public static MethodCallInfo extract(
            String methodBody,
            PsiField field
    ) {
        Pattern pattern = Pattern.compile(
                field.getName() +
                        "\\s*\\.\\s*" +                // 匹配点和空白字符
                        "(\\w+)" +                     // 匹配方法名（\w 包括字母、数字、下划线，加号确保至少有一个字符）
                        "\\s*\\(" +                    // 匹配空白字符和左括号
                        "(.*?)" +                      // 匹配参数列表，非贪婪模式
                        "\\)"                          // 匹配右括号
        );
        Matcher matcher = pattern.matcher(methodBody);
        MethodCallInfo result = new MethodCallInfo(field.getName());
        boolean found = false;
        while (matcher.find()) {
            found = true; // 标记至少找到一个匹配项
            String methodInvokeText = matcher.group();
            String methodName = extractMethodName(methodInvokeText);
            String methodArgs = extractMethodArguments(methodInvokeText);
            List<String> argNameList = new ArrayList<>();
            // 去除每个元素的空白字符
            Collections.addAll(argNameList,
                    Arrays
                            .stream(
                                    methodArgs.split(",")
                            )
                            .map(String::trim)
                            .toArray(String[]::new)
            );
            result.setMethodName(methodName);
            // 获取字段声明所在类
//            PsiClass fieldClass = field.getContainingClass();
            // 获取字段类型
            PsiType fieldType = field.getType();
            PsiClass fieldClass = PsiUtil.resolveClassInType(fieldType);
            if (fieldClass != null && fieldType instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) fieldType;
                PsiType[] superTypes = classType.getSuperTypes();
                for (PsiType superType : superTypes) {
                    if (superType instanceof PsiClassType) {
                        PsiClassType su = (PsiClassType) superType;
                        // 获取与该类型相关联的 PsiSubstitutor
                        PsiSubstitutor substitutor = su.resolveGenerics().getSubstitutor();
                        // 获取所有方法
                        PsiMethod[] methods = fieldClass.getAllMethods();
                        for (PsiMethod fieldMethod : methods) {
                            String name = fieldMethod.getName();
                            if (fieldMethod.getName().equals(methodName)
                                    && fieldMethod.getParameterList().getParameters().length == argNameList.size()) {
                                // 使用 substitutor 替换方法返回类型中的泛型参数
                                PsiType returnType = substitutor.substitute(fieldMethod.getReturnType());
                                result.setReturnType(returnType);
                                PsiParameterList parameterList = fieldMethod.getParameterList();
                                Map<String, PsiType> params = new LinkedHashMap<>();
                                for (PsiParameter parameter : parameterList.getParameters()) {
                                    // 使用 substitutor 替换参数类型中的泛型参数
                                    PsiType paramType = substitutor.substitute(parameter.getType());
                                    params.put(parameter.getName(), paramType);
                                }
                                result.setArgsNameAndTypes(params);
                            }
                        }
                    }
                }
            }
        }
        return found ? result : null;
    }

    /**
     * 从方法调用字符串中提取方法名。
     *
     * @param methodInvoke 包含方法调用的字符串
     * @return 提取的方法名
     */
    private static String extractMethodName(String methodInvoke) {
        return MethodMetaInfoExtractor.extractMethodName(methodInvoke);
    }

    /**
     * 从方法调用字符串中提取方法参数。
     *
     * @param methodInvoke 包含方法调用的字符串
     * @return 提取的方法参数字符串
     */
    private static String extractMethodArguments(String methodInvoke) {
        return MethodMetaInfoExtractor.extractMethodArguments(methodInvoke);
    }
}
