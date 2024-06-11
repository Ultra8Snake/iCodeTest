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

/**
 * ClassMetaInfoExtractor 类提供了从 IntelliJ IDEA 的 PSI (Program Structure Interface) 树中提取类元信息的功能。
 * 它能够分析 Java 文件并提取出包括包名、类名、文件名、目录结构等在内的元数据，
 * 并将这些信息封装成 ClassMetaInfo 对象，以便于后续的处理和使用。
 */
public class ClassMetaInfoExtractor {

    /**
     * 从给定的 PsiJavaFile 和相关上下文中提取类元信息。
     *
     * @param psiJavaFile         当前要处理的 PsiJavaFile 对象
     * @param requestStyleContext 请求风格上下文，用于确定如何提取方法
     * @param includeMethodName   如果指定，则只包含该名称的方法
     * @return 一个 ClassMetaInfo 对象，其中包含提取的类元信息
     */
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

    /**
     * 从 PsiJavaFile 中提取所有带有 @Autowired 或 @Resource 注解的字段。
     *
     * @param psiJavaFile 要分析的 PsiJavaFile 对象
     * @return 一个包含所有相关字段的 List<PsiField>
     */
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

    /**
     * 从指定的PsiJavaFile中提取所有带有@RequestMapping、@GetMapping或@PostMapping注解的方法。
     * 该方法会返回一个映射，其中键是方法名，值是PsiMethod对象。
     * 这个方法可以用于快速检索和分析Spring框架中的请求映射方法。
     *
     * @param psiJavaFile 要分析的PsiJavaFile对象，代表Java源文件
     * @return 一个Map，包含所有带有指定注解的方法名和对应的PsiMethod对象
     * @throws IllegalArgumentException 如果传入的project或psiJavaFile为null，则抛出此异常
     */
    public static List<PsiMethod> filterPsiClassMethods(@NotNull PsiJavaFile psiJavaFile, RequestStyleContext requestStyleContext) {
        return filterPsiClassMethods(psiJavaFile, requestStyleContext, null);
    }

    /**
     * 从指定的PsiJavaFile中提取所有带有@RequestMapping、@GetMapping或@PostMapping注解的方法。
     * 该方法会返回一个映射，其中键是方法名，值是PsiMethod对象。
     * 这个方法可以用于快速检索和分析Spring框架中的请求映射方法。
     *
     * @param psiJavaFile       要分析的PsiJavaFile对象，代表Java源文件
     * @param includeMethodName 要生成的方法名称
     * @return 一个Map，包含所有带有指定注解的方法名和对应的PsiMethod对象
     * @throws IllegalArgumentException 如果传入的project或psiJavaFile为null，则抛出此异常
     */
    public static List<PsiMethod> filterPsiClassMethods(@NotNull PsiJavaFile psiJavaFile, RequestStyleContext requestStyleContext, String includeMethodName) {
        String style = requestStyleContext.getStyle();
        List<PsiMethod> result = new ArrayList<>();
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                if (Objects.equals(Constants.DEFAULT_REQUEST_STYLE_CALL, style)) {
                    // 检查是否是 抽象方法 和 私有方法
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

    /**
     * 从文件名中移除文件扩展名。
     * <p>
     * 该方法查找文件名中最后一个点（'.'）的位置，并移除点之后的部分。
     * 如果文件名不包含点或者点是最后一个字符，则返回原文件名。
     *
     * @param fileName 包含扩展名的文件名称
     * @return 移除扩展名后的文件名
     */
    private static String removeExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     * 从路径中移除源代码根目录的前缀。
     * <p>
     * 该方法用于处理文件路径，移除指定的源代码根目录前缀。
     * 这通常用于将项目特定的源代码目录结构转换为更通用的包结构表示。
     *
     * @param path 包含源代码根目录前缀的路径
     * @return 移除源代码根目录前缀后的路径，或者如果路径不包含前缀，则返回null
     */
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

    /**
     * 从给定的PsiJavaFile中提取@RequestMapping注解的value属性值。
     * 此方法遍历PsiJavaFile中的所有类定义，并检查每个类是否带有@RequestMapping注解。
     * 如果找到该注解，并且其value属性是一个字符串字面量，则返回该字符串值。
     * 如果没有找到该注解，或者value属性不是字符串字面量，则返回null。
     *
     * @param psiJavaFile 要检查的PsiJavaFile对象，代表Java源文件
     * @return 如果找到并成功提取value属性，则返回该属性的字符串值；否则返回null
     * @throws IllegalArgumentException 如果传入的psiJavaFile为null，则抛出此异常
     */
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

    /**
     * 检查指定的PsiMember是否具有@Autowired或@Resource注解。
     *
     * @param psiMember 要检查的PsiMember对象
     * @return 如果PsiMember具有@Autowired或@Resource注解中的任何一个，则返回true，否则返回false。
     */
    private static boolean hasAutowiredOrResourceAnnotation(@NotNull PsiMember psiMember) {
        boolean hasAutowired = hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_AUTOWIRED);
        boolean hasResource = hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_RESOURCE);
        return hasAutowired || hasResource;
    }

    /**
     * 检查指定的PsiMember是否具有@RequestMapping、@GetMapping或@PostMapping注解。
     *
     * @param psiMember 要检查的PsiMember对象
     * @return 如果PsiMember具有@RequestMapping、@GetMapping或@PostMapping注解中的任何一个，则返回true，否则返回false。
     */
    private static boolean hasRequestMappingAnnotation(@NotNull PsiMember psiMember) {
        return hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_REQUEST_MAPPING)
                || hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_GET_MAPPING)
                || hasAnnotation(psiMember, Constants.ANNOTATION_TEXT_POST_MAPPING);
    }

    /**
     * 检查指定的PsiMember是否具有指定的注解。
     *
     * @param psiMember               要检查的PsiMember对象
     * @param annotationQualifiedName 注解的完全限定名
     * @return 如果PsiMember具有指定的注解，则返回true，否则返回false。
     */
    private static boolean hasAnnotation(@NotNull PsiMember psiMember, @NotNull String annotationQualifiedName) {
        PsiModifierList modifierList = psiMember.getModifierList();
        return modifierList != null && modifierList.findAnnotation(annotationQualifiedName) != null;
    }

    /**
     * 根据类的绝对路径确定测试类的目录路径。
     * <p>
     * 这个方法接受一个类的绝对路径作为参数，并基于此路径来构造测试类文件的目标目录路径。
     * 它通过截取绝对路径中表示源代码目录的部分，并将其替换为测试代码目录的相应部分来实现。
     * 例如，它会将路径中的"/src/main/java/"替换为"/src/test/java/"，确保测试类与源类在项目结构中保持一致的层次。
     * 这个方法假设传入的绝对路径遵循Maven标准项目结构。
     *
     * @param absolutePath 类的绝对路径字符串。
     * @return 测试类文件应该存放的目录路径字符串。
     */
    private static String getDirectoryPath(String absolutePath) {
        return absolutePath
                .substring(0, absolutePath.lastIndexOf("/"))
                .replace(
                        Constants.SRC_MAIN_JAVA, Constants.SRC_TEST_JAVA
                );
    }

}
