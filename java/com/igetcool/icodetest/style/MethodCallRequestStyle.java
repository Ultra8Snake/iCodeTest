package com.igetcool.icodetest.style;

import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.MethodCallInfo;
import com.igetcool.icodetest.models.MethodCoreBase;
import com.igetcool.icodetest.models.MethodMetaInfo;
import com.igetcool.icodetest.utils.StringProcessor;
import com.igetcool.icodetest.utils.TypeClassifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;

import java.util.*;

/**
 * MethodCallRequestStyle 类实现了 AbstractRequestStyle 抽象类，并提供了基于方法调用的请求风格。
 * 此类用于生成基于方法调用的测试代码，包括测试类的字段和方法定义，以及方法调用的实现。
 */
public class MethodCallRequestStyle extends AbstractRequestStyle {
    /**
     * 获取请求风格的标识符。
     *
     * @return 返回请求风格的字符串表示，对于 MethodCallRequestStyle 总是返回 Constants.DEFAULT_REQUEST_STYLE_CALL。
     */
    @Override
    public String getStyle() {
        return Constants.DEFAULT_REQUEST_STYLE_CALL;
    }

    /**
     * 获取方法集合所需的导入。
     *
     * @param methodCoreBases 包含方法核心信息的列表
     * @return 返回一个包含所有必需导入的 PsiType 的集合
     */
    @Override
    public Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        Set<PsiType> importSet = new HashSet<>();
        importSet.addAll(originMethodImportSet(methodCoreBases));
        importSet.addAll(callMethodImportSet(methodCoreBases));
        return importSet;
    }

    /**
     * 生成测试类的字段定义。
     *
     * @param classFields   类的字段列表
     * @param classMetaInfo 类的元信息
     * @return 返回测试类字段定义的代码字符串
     */
    @Override
    public String generateTestClassField(List<PsiField> classFields, ClassMetaInfo classMetaInfo) {
        final StringBuilder result = new StringBuilder();
        result.append("\t@InjectMocks\n")
                .append(
                        String.format("\tprivate %s %s;\n",
                                classMetaInfo.getClassName(),
                                StringProcessor.toLowercaseFirst(classMetaInfo.getClassName()))
                );
        for (PsiField field : classFields) {
            String name = field.getType().getPresentableText();
            result.append("\t@Mock\n").append(String.format("\tprivate %s %s;\n", name, field.getName()));
        }
        return result.toString();
    }

    /**
     * 生成测试类的方法定义。
     *
     * @param methodCoreBases 包含方法核心信息的列表
     * @param classMetaInfo   类的元信息
     * @return 返回测试类方法定义的代码字符串
     */
    @Override
    public String generateTestClassMethod(List<MethodCoreBase> methodCoreBases, ClassMetaInfo classMetaInfo) {
        final StringBuilder result = new StringBuilder();
        for (MethodCoreBase methodCoreBase : methodCoreBases) {
            result.append("\t@Test\n");
            result.append("\tpublic void test")
                    .append(StringProcessor.toCapitalize(methodCoreBase.getMethodMetaInfo().getMethodName()))
                    .append("_")
                    .append(UUID.randomUUID().toString().replace("-", ""))
                    .append("() throws Exception {\n\n");
            result.append("\t\t// when ... thenReturn ...\n\n");
            for (MethodCallInfo methodCallInfo : methodCoreBase.getMethodCallInfos()) {
                result.append(allFieldObjectInstance(methodCallInfo));
            }
            result.append("\n");
            result.append(callMethod(methodCoreBase.getMethodMetaInfo(), classMetaInfo));
            result.append("\n");
            result.append("\t\t// Verify the results\n\n")
                    .append("\t\t// 断言对象为null或不为null: isNull() | isNotNull() \n")
                    .append("\t\t// 断言字符串、集合、数组或Iterable对象为空或不为空: isEmpty() | isNotEmpty() \n")
                    .append("\t\t// 断言两个对象相等: isEqualTo() \n")
                    .append("\t\t//assertThat(object).isNull();\n")
                    .append("\t\t//assertThat(object).isNotNull();\n")
                    .append("\t\t//assertThat(object).isEmpty();\n")
                    .append("\t\t//assertThat(object).isNotEmpty();\n")
                    .append("\t\t//assertThat(object).isEqualTo(object);\n\n");
            result.append("\t}\n");
        }
        return result.toString();
    }

    /**
     * 生成调用方法的代码。
     *
     * @param methodMetaInfo 方法的元信息
     * @param classMetaInfo  类的元信息
     * @return 返回调用方法的代码字符串
     */
    @Override
    public String callMethod(MethodMetaInfo methodMetaInfo, ClassMetaInfo classMetaInfo) {
        StringBuilder result = new StringBuilder();
        result.append("\t\t// Run the test\n\n");
        String className = classMetaInfo.getClassName();
        List<String> methodArgumentsTypeList = new ArrayList<>();
        Map<String, PsiType> argsNameAndTypes = methodMetaInfo.getArgsNameAndTypes();
        if (!argsNameAndTypes.isEmpty()) {
            for (Map.Entry<String, PsiType> entry : argsNameAndTypes.entrySet()) {
                PsiType eachParamPsiType = entry.getValue();
                // 如果不是自定义类型，直接取得类型的默认值即可
                if (TypeClassifier.notCustomType(eachParamPsiType)) {
                    methodArgumentsTypeList.add(
                            TypeClassifier.isStringType(eachParamPsiType) // String类型直接给 "0"
                                    ? "\"0\""
                                    : TypeClassifier.getDefaultValue(eachParamPsiType)
                    );
                } else {
                    if (TypeClassifier.isInterfaceOrAbstractClassOrEnum(eachParamPsiType)) {
                        // 如果是接口、抽象类或枚举，参数直接给“null”即可
                        methodArgumentsTypeList.add("null");
                    } else {
                        // 否则认为是自定义类型，自定义类型可以使用 new 关键字（TODO 风险：没有无参构造）
                        PsiClass psiClass = PsiUtil.resolveClassInType(eachParamPsiType);// 主要是为了取 类名的精简名字
                        if (psiClass != null) {
                            String instanceName = "arg" + psiClass.getName();
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
        final PsiType returnType = methodMetaInfo.getReturnType();
        PsiClass psiClass = PsiUtil.resolveClassInType(returnType);
        if (psiClass != null) {
            String returnTypeText = psiClass.getName();
            String returnTypeFieldText = "rr" + returnTypeText;
            result.append("\t\t").append(
                    String.format("%s %s = %s;\n",
                            returnTypeText,
                            returnTypeFieldText,
                            TypeClassifier.getDefaultValue(returnType)
                    )
            );
            result.append("\t\ttry {\n");
            result.append("\t\t\t").append(
                    String.format("%s = %s.%s(%s);",
                            returnTypeFieldText,
                            StringProcessor.toLowercaseFirst(className),
                            methodMetaInfo.getMethodName(),
                            String.join(",", methodArgumentsTypeList)
                    )
            ).append("\n");
            result.append("\t\t} catch (Throwable throwable) {\n");
            result.append("\t\t\t// 异常的处理\n");
            result.append("\t\t}\n");
        } else {
            result.append("\t\t").append(String.format("%s.%s(%s);",
                                    StringProcessor.toLowercaseFirst(className),
                                    methodMetaInfo.getMethodName(),
                                    String.join(",", methodArgumentsTypeList)
                            )
                    )
                    .append("\n");
        }
        return result.toString();
    }

    /**
     * 获取方法参数和返回类型所需的导入。
     *
     * @param methodCoreBases 包含方法核心信息的列表
     * @return 返回一个包含所有必需导入的 PsiType 的集合
     */
    private Set<PsiType> originMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        final Set<PsiType> result = new HashSet<>();
        for (MethodCoreBase methodCoreBase : methodCoreBases) {
            MethodMetaInfo methodMetaInfo = methodCoreBase.getMethodMetaInfo();
            Map<String, PsiType> argsNameAndTypes = methodMetaInfo.getArgsNameAndTypes();
            if (argsNameAndTypes == null) {
                continue;
            }
            for (Map.Entry<String, PsiType> entry : argsNameAndTypes.entrySet()) {
                result.add(entry.getValue());
            }
            PsiType methodReturnType = methodMetaInfo.getReturnType();
            result.add(methodReturnType);
        }
        return result;
    }

    /**
     * 获取调用方法时所需的导入。
     *
     * @param methodCoreBases 包含方法核心信息的列表
     * @return 返回一个包含所有必需导入的 PsiType 的集合
     */
    private Set<PsiType> callMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        final Set<PsiType> result = new HashSet<>();
        for (MethodCoreBase methodCoreBase : methodCoreBases) {
            List<MethodCallInfo> methodCallInfos = methodCoreBase.getMethodCallInfos();
            for (MethodCallInfo methodCallInfo : methodCallInfos) {
                Map<String, PsiType> argsNameAndTypes = methodCallInfo.getArgsNameAndTypes();
                if (argsNameAndTypes == null) {
                    continue;
                }
                for (Map.Entry<String, PsiType> entry : argsNameAndTypes.entrySet()) {
                    result.add(entry.getValue());
                }
                PsiType methodReturnType = methodCallInfo.getReturnType();
                result.add(methodReturnType);
            }
        }
        return result;
    }
}
