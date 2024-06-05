package com.igetcool.icodetest.style;

import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.MethodCallInfo;
import com.igetcool.icodetest.models.MethodCoreBase;
import com.igetcool.icodetest.models.MethodMetaInfo;
import com.igetcool.icodetest.utils.StringProcessor;
import com.igetcool.icodetest.utils.TypeClassifier;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.*;

public class MockMvcRequestStyle extends AbstractRequestStyle {
    @Override
    public String getStyle() {
        return Constants.DEFAULT_REQUEST_STYLE_MOCK;
    }

    @Override
    public Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        return new HashSet<>(callMethodImportSet(methodCoreBases));
    }

    @Override
    public String generateTestClassField(List<PsiField> classFields, ClassMetaInfo classMetaInfo) {
        final StringBuilder result = new StringBuilder();
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
            final MethodMetaInfo methodMetaInfo = methodCoreBase.getMethodMetaInfo();
            result.append("\t@Test").append("\n");
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
            result.append(callMethod(methodMetaInfo, classMetaInfo));
            result.append("\n");
            result.append("\t\t// Verify the results\n\n")
                    .append("\t\t// 断言对象为null或不为null: isNull() | isNotNull() \n")
                    .append("\t\t// 断言字符串、集合、数组或Iterable对象为空或不为空: isEmpty() | isNotEmpty() \n")
                    .append("\t\t// 断言两个对象相等: isEqualTo() \n")
                    .append("\t\t//assertThat(object).isNull();\n")
                    .append("\t\t//assertThat(object).isNotNull();\n")
                    .append("\t\t//assertThat(object).isEmpty();\n")
                    .append("\t\t//assertThat(object).isNotEmpty();\n")
                    .append("\t\tassertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());\n");
            result.append("\t}\n");
        }
        return result.toString();
    }

    @Override
    public String callMethod(MethodMetaInfo methodMetaInfo, ClassMetaInfo classMetaInfo) {
        StringBuilder result = new StringBuilder();
        String classRequestUri = classMetaInfo.getRequestMappingUri() == null ? "" : classMetaInfo.getRequestMappingUri();
        String requestMappingValue = classRequestUri + methodMetaInfo.getRequestUri();
        result.append("\t\t// Run the test\n\n")
                .append("\t\tfinal MockHttpServletResponse response = mockMvc.perform(")
                .append(methodMetaInfo.getRequestType())
                .append("(\"")
                .append(requestMappingValue)
                .append("\")\n");
        switch (methodMetaInfo.getArgsNameAndTypes().size()) {
            case 0:
                break;
            case 1:
                result.append(String.format("\t\t\t\t.content(\"%s\")\n", "content"));
                break;
            default:
                int i = 0;
                Map<String, PsiType> argsNameAndTypes = methodMetaInfo.getArgsNameAndTypes();
                for (Map.Entry<String, PsiType> entry : argsNameAndTypes.entrySet()) {
                    String value = String.format("\t\t\t\t.param(\"%s\", \"%s\")\n",
                            entry.getKey().trim(),
                            TypeClassifier.isStringType(entry.getValue())
                                    ? entry.getKey().trim()
                                    : TypeClassifier.getDefaultValue(entry.getValue()));
                    result.append(value);
                }
                break;
        }
        result.append("\t\t\t\t.accept(MediaType.APPLICATION_JSON))\n")
                .append("\t\t.andReturn().getResponse();\n\n");
        return result.toString();
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
