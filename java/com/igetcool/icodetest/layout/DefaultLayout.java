package com.igetcool.icodetest.layout;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.event.DefaultTextEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 默认单元测试布局实现类，用于生成Java类的布局代码。
 */
public class DefaultLayout<T> extends Layout<T> {

    /**
     * 格式化并生成单元测试类的布局代码。
     *
     * @param t 包含类信息和测试代码的事件对象。
     * @return 格式化后的Java类布局代码字符串。
     */
    @Override
    public String format(T t) {
        DefaultTextEvent defaultTextEvent = (DefaultTextEvent) t;
        final ClassMetaInfo classMetaInfo = defaultTextEvent.getClassMetaInfo();
        final Set<PsiType> testClassImportSet = defaultTextEvent.getTestClassImportSet();
        return fullImport(testClassImportSet, classMetaInfo) +
                createClassDeclaration(
                        classMetaInfo.getFinalClassName(),
                        String.format("%s%s",
                                defaultTextEvent.getTestClassFieldText(),
                                defaultTextEvent.getTestClassMethodText()
                        )
                );
    }

    /**
     * 创建完整的导入声明字符串。
     * 该方法根据提供的测试类导入集合和类元信息构建一个包含所有必需导入的字符串。
     * 它包括包声明、公共类导入、JUnit版本相关的导入、默认类导入，以及测试类特定的导入。
     *
     * @param testClassImportSet 测试类需要导入的PsiType集合
     * @param classMetaInfo      包含类元数据信息的ClassMetaInfo对象
     * @return 构建的导入声明字符串
     */
    private String fullImport(Set<PsiType> testClassImportSet, ClassMetaInfo classMetaInfo) {
        final StringBuilder result = new StringBuilder();
        result.append(createPackageDeclaration(classMetaInfo.getPackageName()))
                // 导入测试类的公共类
                .append(
                        createImportDeclaration(
                                String.format("%s.%s",
                                        SettingsManager.INSTANCE.getCommonPackageName(),
                                        SettingsManager.INSTANCE.getCommonClassName()
                                )
                        )
                )
                .append(createImportDeclaration(classMetaInfo.getQualifiedClassName()))
                // 基于JUnit版本导入相关类
                .append(SettingsManager.INSTANCE.getJUnitVersion().equals(Constants.DEFAULT_VERSION_JUNIT_4)
                        ? Constants.JUNIT_4_TEST_PACKAGE
                        : Constants.JUNIT_5_TEST_PACKAGE)
                // 导入默认类
                .append(getDefaultImports());

        Set<String> filterImportSet = new HashSet<>();
        // PsiType 类型不同 但文本类型的“包+类”相同，会有重复的，需要过滤
        for (PsiType importClass : testClassImportSet) {
            filterImportSet.add(createImportDeclaration(importClass));
        }
        for (String importStr : filterImportSet) {
            result.append(importStr);
        }
        return result.toString();
    }

    /**
     * 创建一个Java包声明字符串。
     *
     * @param packageName 包名。
     * @return 包含包声明的字符串，例如 "package com.example;\n"。
     */
    public static String createPackageDeclaration(String packageName) {
        return "package " + packageName + ";\n";
    }

    /**
     * 获取默认导入的包，通常是Java标准库中不需要显式导入的包。
     *
     * @return 默认导入的包名字符串，例如 "import java.util.*;\n"。
     */
    public static String getDefaultImports() {
        return String.format(Constants.DEFAULT_IMPORT_PACKAGE,
                SettingsManager.INSTANCE.getCommonPackageName(),
                SettingsManager.INSTANCE.getCommonClassName()
        );
    }

    /**
     * 创建一个Java类导入声明字符串。
     *
     * @param className 需要导入的类的完整名称（包括包名）。
     * @return 如果类不是java.lang包下的类或基本类型，则返回包含类导入声明的字符串；否则返回空字符串。
     */
    public static String createImportDeclaration(String className) {
        if (className == null || className.isEmpty()
                || className.startsWith("java.lang")
                || className.startsWith("java.util")) {
            return "";
        }
        return "import " + className + ";\n";
    }

    /**
     * 创建一个Java类的导入声明字符串，根据PsiType类信息。
     *
     * @param psiType 需要导入的类的PsiType对象。
     * @return 如果PsiType表示的类不是基本类型或java.lang包下的类，则返回包含类导入声明的字符串；否则返回空字符串。
     */
    public static String createImportDeclaration(PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType
                || psiType.getPresentableText().startsWith("java.lang")
                || psiType.getPresentableText().startsWith("java.util")) {
            return "";
        } else {
            PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                if (qualifiedName == null
                        || qualifiedName.startsWith("java.lang")
                        || qualifiedName.startsWith("java.util")) {
                    return "";
                }
                return "import " + psiClass.getQualifiedName() + ";\n";
            }
            return "";
        }
    }

    /**
     * 创建一个带有注解的Java类声明字符串，用于生成用于测试的Java类。
     *
     * @param finalClassName 被测试的类的名称。
     * @param finalClassBody 类体的内容，包括类的字段、方法等。
     * @return 包含类声明和类体的完整字符串，例如 "public class ExampleTest extends WebMvcBase { ... }"。
     */
    public static String createClassDeclaration(String finalClassName, String finalClassBody) {
        // String annotation1 = "@ExtendWith(SpringExtension.class)\n";
        // String annotation2 = "@WebMvcTest(" + className + ".class)\n";
        String classDeclaration = /*annotation1 + annotation2 +*/
                "\n" + "public class " + finalClassName;
        return classDeclaration + " extends " + SettingsManager.INSTANCE.getCommonClassName() + " {\n\n" + finalClassBody + "\n\n} ";
    }
}
