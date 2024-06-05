package com.igetcool.icodetest.extractor;

import com.igetcool.icodetest.models.MethodCallInfo;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodCallInfoExtractor {

    public static MethodCallInfo extract(
            String methodBody,
            PsiField field
    ) {
        Pattern pattern = Pattern.compile(
                field.getName() +
                        "\\s*\\.\\s*" +
                        "(\\w+)" +
                        "\\s*\\(" +
                        "(.*?)" +
                        "\\)"
        );
        Matcher matcher = pattern.matcher(methodBody);
        MethodCallInfo result = new MethodCallInfo(field.getName());
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String methodInvokeText = matcher.group();
            String methodName = extractMethodName(methodInvokeText);
            String methodArgs = extractMethodArguments(methodInvokeText);
            List<String> argNameList = new ArrayList<>();
            Collections.addAll(argNameList,
                    Arrays
                            .stream(
                                    methodArgs.split(",")
                            )
                            .map(String::trim)
                            .toArray(String[]::new)
            );
            result.setMethodName(methodName);
            PsiType fieldType = field.getType();
            PsiClass fieldClass = PsiUtil.resolveClassInType(fieldType);
            if (fieldClass != null && fieldType instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) fieldType;
                PsiType[] superTypes = classType.getSuperTypes();
                for (PsiType superType : superTypes) {
                    if (superType instanceof PsiClassType) {
                        PsiClassType su = (PsiClassType) superType;
                        PsiSubstitutor substitutor = su.resolveGenerics().getSubstitutor();
                        PsiMethod[] methods = fieldClass.getAllMethods();
                        for (PsiMethod fieldMethod : methods) {
                            String name = fieldMethod.getName();
                            if (fieldMethod.getName().equals(methodName)
                                    && fieldMethod.getParameterList().getParameters().length == argNameList.size()) {
                                PsiType returnType = substitutor.substitute(fieldMethod.getReturnType());
                                result.setReturnType(returnType);
                                PsiParameterList parameterList = fieldMethod.getParameterList();
                                Map<String, PsiType> params = new LinkedHashMap<>();
                                for (PsiParameter parameter : parameterList.getParameters()) {
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

    private static String extractMethodName(String methodInvoke) {
        return CodeExtractor.extractMethodName(methodInvoke);
    }

    private static String extractMethodArguments(String methodInvoke) {
        return CodeExtractor.extractMethodArguments(methodInvoke);
    }
}
