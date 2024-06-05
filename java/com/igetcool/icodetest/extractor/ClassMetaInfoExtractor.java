package com.igetcool.icodetest.extractor;

import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.style.RequestStyleContext;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassMetaInfoExtractor {

    public static ClassMetaInfo extract(@NotNull PsiJavaFile psiJavaFile, RequestStyleContext requestStyleContext, String includeMethodName) {
        String absolutePath = getAbsolutePath(psiJavaFile);
        if (absolutePath.isEmpty()) {
            return null;
        }
        File file = new File(absolutePath);
        if (!file.exists()) {
            return null;
        }
        String className = removeExtension(file.getName());
        if (className.isEmpty()) {
            return null;
        }
        String packageDirectory = removeSourceRootPrefix(file.getParent());
        if (packageDirectory == null) {
            return null;
        }
        String packageName = packageDirectory.replace(File.separator, ".");
        String qualifiedClassName = packageName + "." + className;
        String fileName = file.getName();
        String classRequestMappingUri = getClassRequestMappingUri(psiJavaFile);

        ClassMetaInfo classMetaInfo = new ClassMetaInfo();
        classMetaInfo.setAbsolutePath(absolutePath);
        classMetaInfo.setPackageName(packageName);
        classMetaInfo.setClassName(className);
        classMetaInfo.setFileName(fileName);
        classMetaInfo.setPackageDirectory(packageDirectory);
        classMetaInfo.setQualifiedClassName(qualifiedClassName);
        classMetaInfo.setRequestMappingUri(classRequestMappingUri);
        List<PsiField> psiFields = filterPsiClassFields(psiJavaFile);
        classMetaInfo.setClassFields(psiFields);
        List<PsiMethod> psiMethods = filterPsiClassMethods(psiJavaFile, requestStyleContext, includeMethodName);
        classMetaInfo.setClassMethods(psiMethods);

        classMetaInfo.setFinalAbsolutePath(getDirectoryPath(absolutePath));
        classMetaInfo.setFinalClassName(String.format("%sTest", className));
        String finalFullPath = classMetaInfo.getFinalAbsolutePath() + "/" + classMetaInfo.getFinalClassName() + ".java";
        classMetaInfo.setFinalFullPath(finalFullPath);
        return classMetaInfo;
    }

    public static List<PsiField> filterPsiClassFields(@NotNull PsiJavaFile psiJavaFile) {
        List<PsiField> result = new ArrayList<>();
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            for (PsiField field : psiClass.getFields()) {
                if (hasAutowiredOrResourceAnnotation(field)) {
                    result.add(field);
                }
            }
        }
        return result;
    }

    public static List<PsiMethod> filterPsiClassMethods(@NotNull PsiJavaFile psiJavaFile, RequestStyleContext requestStyleContext) {
        return filterPsiClassMethods(psiJavaFile, requestStyleContext, null);
    }

    public static List<PsiMethod> filterPsiClassMethods(@NotNull PsiJavaFile psiJavaFile, RequestStyleContext requestStyleContext, String includeMethodName) {
        String style = requestStyleContext.getStyle();
        List<PsiMethod> result = new ArrayList<>();
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                if (Objects.equals(Constants.DEFAULT_REQUEST_STYLE_CALL, style)) {
                    boolean isAbstract = psiMethod.hasModifierProperty(PsiModifier.ABSTRACT);
                    boolean isPrivate = psiMethod.hasModifierProperty(PsiModifier.PRIVATE);
                    if (!isAbstract && !isPrivate) {
                        if (includeMethodName == null) {
                            result.add(psiMethod);
                        } else {
                            if (psiMethod.getName().equals(includeMethodName)) {
                                result.add(psiMethod);
                            }
                        }
                    }
                } else {
                    if (hasRequestMappingAnnotation(psiMethod)) {
                        if (includeMethodName == null) {
                            result.add(psiMethod);
                        } else {
                            if (psiMethod.getName().equals(includeMethodName)) {
                                result.add(psiMethod);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static String removeExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    private static String removeSourceRootPrefix(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        if (path.contains(Constants.SRC_MAIN_JAVA + "/")) {
            return path.split(Constants.SRC_MAIN_JAVA + "/")[1];
        }
        return null;
    }

    private static String getAbsolutePath(@NotNull PsiJavaFile psiJavaFile) {
        return psiJavaFile.getVirtualFile().getPath();
    }

    public static String getClassRequestMappingUri(@NotNull PsiJavaFile psiJavaFile) {
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            PsiModifierList modifierList = psiClass.getModifierList();
            if (modifierList != null) {
                for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                    String annotationQualifiedName = annotation.getQualifiedName();
                    if (annotationQualifiedName != null
                            && Constants.ANNOTATION_TEXT_REQUEST_MAPPING.contains(annotationQualifiedName)) {
                        PsiAnnotationMemberValue valueMember = annotation.findAttributeValue("value");
                        if (valueMember instanceof PsiLiteralExpression) {
                            Object value = ((PsiLiteralExpression) valueMember).getValue();
                            if (value instanceof String) {
                                return (String) value;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean hasAutowiredOrResourceAnnotation(@NotNull PsiMember psiMember) {
        boolean hasAutowired = hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_AUTOWIRED);
        boolean hasResource = hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_RESOURCE);
        return hasAutowired || hasResource;
    }

    private static boolean hasRequestMappingAnnotation(@NotNull PsiMember psiMember) {
        return hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_REQUEST_MAPPING)
                || hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_GET_MAPPING)
                || hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_POST_MAPPING);
    }

    private static boolean hasAnnotation(@NotNull PsiMember psiMember, @NotNull String annotationQualifiedName) {
        PsiModifierList modifierList = psiMember.getModifierList();
        return modifierList != null && modifierList.findAnnotation(annotationQualifiedName) != null;
    }

    private static String getDirectoryPath(String absolutePath) {
        return absolutePath
                .substring(0, absolutePath.lastIndexOf("/"))
                .replace(
                        Constants.SRC_MAIN_JAVA, Constants.SRC_TEST_JAVA
                );
    }

}
