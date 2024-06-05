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

public class MethodCallRequestStyle extends AbstractRequestStyle {

    @Override
    public String getStyle() {
        return Constants.DEFAULT_REQUEST_STYLE_CALL;
    }

    @Override
    public Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        Set<PsiType> importSet = new HashSet<>();
        importSet.addAll(originMethodImportSet(methodCoreBases));
        importSet.addAll(callMethodImportSet(methodCoreBases));
        return importSet;
    }

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
                if (TypeClassifier.notCustomType(eachParamPsiType)) {
                    methodArgumentsTypeList.add(
                            TypeClassifier.isStringType(eachParamPsiType)
                                    ? "\"0\""
                                    : TypeClassifier.getDefaultValue(eachParamPsiType)
                    );
                } else {
                    if (TypeClassifier.isInterfaceOrAbstractClassOrEnum(eachParamPsiType)) {
                        methodArgumentsTypeList.add("null");
                    } else {
                        PsiClass psiClass = PsiUtil.resolveClassInType(eachParamPsiType);
                        if (psiClass != null) {
                            String instanceName = "arg" + psiClass.getName();
                            methodArgumentsTypeList.add(instanceName);
                            result.append(fieldObjectInstance(eachParamPsiType, instanceName));
                        } else {
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
