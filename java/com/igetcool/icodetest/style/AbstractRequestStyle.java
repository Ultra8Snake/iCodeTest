package com.igetcool.icodetest.style;

import com.igetcool.icodetest.models.MethodCallInfo;
import com.igetcool.icodetest.utils.StringProcessor;
import com.igetcool.icodetest.utils.TypeClassifier;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.*;

/**
 * AbstractRequestStyle 类提供了一个抽象的请求风格实现。
 * 它实现了 RequestStyle 接口，并为所有生成测试代码的请求风格提供了一个基础架构。
 * 此类包括了一些公共的方法实现，这些方法可以被所有具体的请求风格实现所复用。
 */
public abstract class AbstractRequestStyle implements RequestStyle {

    /**
     * 获取类字段所引用的所有导入。
     * 此方法返回一个包含所有字段类型的集合，这些类型定义了所需的导入。
     *
     * @param classFields 类的字段列表
     * @return 包含所有字段类型的集合
     */
    public Set<PsiType> getFieldImportSet(List<PsiField> classFields) {
        final Set<PsiType> result = new HashSet<>();
        for (PsiField field : classFields) {
            result.add(field.getType());
        }
        return result;
    }

    /**
     * 生成所有字段的对象实例，并为其设置默认值。
     * 此方法基于给定的方法调用信息，为每个参数生成一个对象实例，并设置默认值。
     *
     * @param methodCallInfo 方法调用信息
     * @return 生成的代码字符串
     */
    public String allFieldObjectInstance(MethodCallInfo methodCallInfo) {
        final StringBuilder result = new StringBuilder();
        List<String> methodArgumentsTypeList = new ArrayList<>();
        // MethodCallInfo-> 处理调用的参数类型
        Map<String, PsiType> argsNameAndTypes = methodCallInfo.getArgsNameAndTypes();
        if (argsNameAndTypes != null && !argsNameAndTypes.isEmpty()) {
            for (Map.Entry<String, PsiType> entry : argsNameAndTypes.entrySet()) {
                PsiType eachParamPsiType = entry.getValue();
                // 如果不是自定义类型，直接取得类型的默认值即可
                if (TypeClassifier.notCustomType(eachParamPsiType)) {
                    methodArgumentsTypeList.add(TypeClassifier.getDefaultValue(eachParamPsiType));
                } else {
                    if (TypeClassifier.isInterfaceOrAbstractClassOrEnum(eachParamPsiType)) {
                        // 如果是接口、抽象类或枚举，参数直接给“null”即可
                        methodArgumentsTypeList.add("null");
                    } else {
                        // 否则认为是自定义类型，自定义类型可以使用 new 关键字（TODO 风险：没有无参构造）
                        PsiClass psiClass = PsiUtil.resolveClassInType(eachParamPsiType);// 主要是为了取 类名的精简名字
                        if (psiClass != null) {
                            String instanceName = StringProcessor.toLowercaseFirst(psiClass.getName()) + "Mock";
                            methodArgumentsTypeList.add(instanceName);
                            result.append(fieldObjectInstance(eachParamPsiType, instanceName));
                        } else {
                            // 不应该出现的情况，防止丢参数，给一个默认值（TODO 风险：上述判断不严谨会出现此情况）
                            methodArgumentsTypeList.add("null");
                        }
                    }
                }
            }
        }
        // MethodCallInfo-> 处理调用的返回类型
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
                // 如果是接口或抽象类，返回值直接给“null”即可
                instanceName = "null";
            } else {
                // 否则认为是自定义类型，自定义类型可以使用 new 关键字（TODO 风险：没有无参构造）
                PsiClass psiClass = PsiUtil.resolveClassInType(methodReturnType);// 主要是为了取 类名的精简名字
                if (psiClass != null) {
                    instanceName = StringProcessor.toLowercaseFirst(psiClass.getName()) + "Mock";
                    result.append(fieldObjectInstance(methodReturnType, instanceName));
                } else {
                    // 不应该出现的情况，防止丢参数，给一个默认值（TODO 风险：上述判断不严谨会出现此情况）
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

    /**
     * 为特定类型创建一个实例，并为其所有 setter 方法设置默认值。
     * 此方法首先为指定类型创建一个新实例，然后为该类中所有以 "set" 开头的方法设置默认值。
     *
     * @param psiType    需要创建实例的类型
     * @param simpleName 实例的变量名
     * @return 返回包含实例化和 setter 方法调用的代码字符串
     */
    public String fieldObjectInstance(PsiType psiType, String simpleName) {
        if (TypeClassifier.notCustomType(psiType)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (psiClass != null) {
            result.append(
                    String.format("\t\t%s %s = new %s();\n",
                            psiClass.getName(), // 类的简单名称
                            simpleName,         // 实例变量名
                            psiClass.getName()  // 再次使用类的简单名称
                    )
            );
            for (PsiMethod method : psiClass.getMethods()) {
                if (method.getName().startsWith("set")) {
                    PsiParameterList parameterList = method.getParameterList();
                    for (PsiParameter parameter : parameterList.getParameters()) {
                        result.append(
                                String.format("\t\t%s.%s(%s);\n",
                                        simpleName,            // 实例变量名
                                        method.getName(),      // 方法名
                                        TypeClassifier.getDefaultValue(parameter.getType()) // 参数类型的默认值
                                )
                        );
                    }
                }
            }
        }
        return result.toString();
    }
}
