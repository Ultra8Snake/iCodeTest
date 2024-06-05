package com.igetcool.icodetest.layout;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.ClassTextEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;

import java.util.HashSet;
import java.util.Set;

public class DefaultLayout<T> extends Layout<T> {

    @Override
    public String format(T t) {
        ClassTextEvent classTextEvent = (ClassTextEvent) t;
        final ClassMetaInfo classMetaInfo = classTextEvent.getClassMetaInfo();
        final Set<PsiType> testClassImportSet = classTextEvent.getTestClassImportSet();
        return fullImport(testClassImportSet, classMetaInfo) +
                createClassDeclaration(
                        classMetaInfo.getFinalClassName(),
                        String.format("%s%s",
                                classTextEvent.getTestClassFieldText(),
                                classTextEvent.getTestClassMethodText()
                        )
                );
    }

    private String fullImport(Set<PsiType> testClassImportSet, ClassMetaInfo classMetaInfo) {
        final StringBuilder result = new StringBuilder();
        result.append(createPackageDeclaration(classMetaInfo.getPackageName()))
                .append(
                        createImportDeclaration(
                                String.format("%s.%s",
                                        SettingsManager.INSTANCE.getCommonPackageName(),
                                        SettingsManager.INSTANCE.getCommonClassName()
                                )
                        )
                )
                .append(createImportDeclaration(classMetaInfo.getQualifiedClassName()))
                .append(SettingsManager.INSTANCE.getJUnitVersion().equals(Constants.DEFAULT_VERSION_JUNIT_4)
                        ? Constants.JUNIT_4_TEST_PACKAGE
                        : Constants.JUNIT_5_TEST_PACKAGE)
                .append(getDefaultImports());

        Set<String> filterImportSet = new HashSet<>();
        for (PsiType importClass : testClassImportSet) {
            filterImportSet.add(createImportDeclaration(importClass));
        }
        for (String importStr : filterImportSet) {
            result.append(importStr);
        }
        return result.toString();
    }

    public static String createPackageDeclaration(String packageName) {
        return "package " + packageName + ";\n";
    }

    public static String getDefaultImports() {
        return String.format(Constants.DEFAULT_IMPORT_PACKAGE,
                SettingsManager.INSTANCE.getCommonPackageName(),
                SettingsManager.INSTANCE.getCommonClassName()
        );
    }

    public static String createImportDeclaration(String className) {
        if (className == null || className.isEmpty()
                || className.startsWith("java.lang")
                || className.startsWith("java.util")) {
            return "";
        }
        return "import " + className + ";\n";
    }

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

    public static String createClassDeclaration(String finalClassName, String finalClassBody) {
        String classDeclaration = "\n" + "public class " + finalClassName;
        return classDeclaration + " extends " + SettingsManager.INSTANCE.getCommonClassName() + " {\n\n" + finalClassBody + "\n\n} ";
    }
}
