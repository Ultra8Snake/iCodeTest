package com.igetcool.icodetest.creator;

import com.igetcool.icodetest.appender.Appender;
import com.igetcool.icodetest.appender.CommonFileAppender;
import com.igetcool.icodetest.appender.DefaultFileAppender;
import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.constants.Constants;
import com.igetcool.icodetest.enums.OperateType;
import com.igetcool.icodetest.extractor.ClassMetaInfoExtractor;
import com.igetcool.icodetest.extractor.CodeExtractor;
import com.igetcool.icodetest.extractor.MethodCallInfoExtractor;
import com.igetcool.icodetest.models.*;
import com.igetcool.icodetest.popup.CustomOverrideDialog;
import com.igetcool.icodetest.style.MethodCallRequestStyle;
import com.igetcool.icodetest.style.MockMvcRequestStyle;
import com.igetcool.icodetest.style.RequestStyle;
import com.igetcool.icodetest.style.RequestStyleContext;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AnActionProcessor {

    private static final String MESSAGE_DIALOG_TITLE = "操作结果";

    public static void process(Project project, PsiJavaFile file, OperateType operateType) {
        process(project, Collections.singletonList(file), operateType);
    }

    public static void process(Project project, PsiJavaFile file, String methodName, OperateType operateType) {
        process(project, Collections.singletonList(file), methodName, operateType);
    }

    public static void process(Project project, List<PsiJavaFile> filesList, OperateType operateType) {
        process(project, filesList, null, operateType);
    }

    public static void process(Project project, List<PsiJavaFile> filesList, String methodName, OperateType operateType) {
        if (filesList == null || filesList.isEmpty()) {
            Messages.showMessageDialog(
                    project,
                    "非法参数：未找到要处理的文件",
                    MESSAGE_DIALOG_TITLE,
                    Messages.getInformationIcon()
            );
            return;
        }
        SettingsManager.INSTANCE.loadSettings();
        createCommonClassFile(filesList);
        doProcess(project, filesList, methodName, operateType);
    }

    private static void createCommonClassFile(List<PsiJavaFile> filesList) {
        PsiJavaFile psiFile = filesList.get(0);
        String commonPackagePath = SettingsManager.INSTANCE.getCommonPackageName().replace(".", "/");

        String commonAbsolutePath = psiFile.getVirtualFile().getPath().replaceAll(
                String.format("/%s.*", Constants.SRC_MAIN_JAVA),
                String.format("/%s/%s", Constants.SRC_TEST_JAVA, commonPackagePath)
        );
        String commonFullFilePath = String.format("%s/%s.java", commonAbsolutePath, SettingsManager.INSTANCE.getCommonClassName());
        if (Files.notExists(Path.of(commonFullFilePath))) {
            Project project = psiFile.getProject();
            Module module = ModuleUtil.findModuleForPsiElement(psiFile);
            if (module != null) {
                List<PsiClass> springBootClasses = findSpringBootApplicationClasses(project, module);
                if (springBootClasses.isEmpty()) {
                    List<PsiClass> springCloudClasses = findSpringCloudApplicationClasses(project, module);
                    if (springCloudClasses.isEmpty()) {
                        return;
                    }
                    springBootClasses.addAll(springCloudClasses);
                }
                PsiClass psiClass = springBootClasses.get(0);
                if (psiClass == null) {
                    return;
                }
                Appender appender = new CommonFileAppender(true);
                CommonTextEvent commonTextEvent = new CommonTextEvent();
                commonTextEvent.setAbsolutePath(commonAbsolutePath);
                commonTextEvent.setQualifiedName(psiClass.getQualifiedName());
                commonTextEvent.setClassName(psiClass.getName());
                appender.append(commonTextEvent);
            }
        }
    }

    private static void doProcess(Project project, List<PsiJavaFile> filesList, String includeMethodName, OperateType operateType) {
        final StringBuilder sb = new StringBuilder();
        RequestStyleContext requestStyleContext = getRequestStyleContext();
        boolean overrideAll = false;
        try {

            for (PsiJavaFile psiJavaFile : filesList) {
                ClassMetaInfo classMetaInfo = ClassMetaInfoExtractor.extract(psiJavaFile, requestStyleContext, includeMethodName);
                if (classMetaInfo == null) {
                    continue;
                }
                List<PsiField> classFields = classMetaInfo.getClassFields();
                if (classFields.isEmpty()) {
                    continue;
                }
                List<PsiMethod> classMethods = classMetaInfo.getClassMethods();
                if (classMethods.isEmpty()) {
                    continue;
                }
                List<MethodCoreBase> methodCoreBases = getMethodCoreBases(classMethods, classFields);
                if (methodCoreBases.isEmpty()) {
                    continue;
                }
                String finalFullPath = classMetaInfo.getFinalFullPath();
                if (finalFullPath == null) {
                    continue;
                }
                String finalClassName = classMetaInfo.getFinalClassName();
                if (finalClassName == null) {
                    continue;
                }

                if (operateType != OperateType.CUSTOM) {
                    if (Files.exists(Path.of(classMetaInfo.getFinalFullPath()))) {
                        if (!overrideAll) {
                            CustomOverrideDialog dialog = new CustomOverrideDialog(
                                    project,
                                    classMetaInfo.getFinalClassName(),
                                    operateType == OperateType.RECURSIVE
                            );
                            if (dialog.showAndGet()) {
                                if (dialog.isOverrideAll()) {
                                    overrideAll = true;
                                }
                                if (!dialog.isOK()) {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }

                final Set<PsiType> testClassImportSet = new HashSet<>(requestStyleContext.getFieldImportSet(classFields));
                testClassImportSet.addAll(
                        requestStyleContext.getMethodImportSet(methodCoreBases)
                );
                final String testClassFieldText = requestStyleContext.generateTestClassField(classFields, classMetaInfo);
                final String testClassMethodText = requestStyleContext.generateTestClassMethod(methodCoreBases, classMetaInfo);
                boolean defaultClassFileFlag = createDefaultClassFile(
                        classMetaInfo,
                        testClassImportSet,
                        testClassFieldText,
                        testClassMethodText,
                        operateType == OperateType.CUSTOM && includeMethodName != null && !includeMethodName.isEmpty() ? includeMethodName : null
                );
                if (defaultClassFileFlag) {
                    sb.append("成功").append(" -> ").append(classMetaInfo.getClassName()).append("\n");
                } else {
                    sb.append("失败").append(" -> ").append(classMetaInfo.getClassName()).append("\n");
                }
            }
        } catch (Throwable e) {
            Messages.showMessageDialog(
                    project,
                    "异常：" + e.getMessage(),
                    MESSAGE_DIALOG_TITLE,
                    Messages.getInformationIcon()
            );
            return;
        }
        if (!sb.toString().isEmpty()) {
            Messages.showMessageDialog(
                    project,
                    sb.toString(),
                    MESSAGE_DIALOG_TITLE,
                    Messages.getInformationIcon()
            );
        }
    }

    private static RequestStyleContext getRequestStyleContext() {
        RequestStyle requestStyle;
        if (Objects.equals(Constants.DEFAULT_REQUEST_STYLE_MOCK, SettingsManager.INSTANCE.getRequestStyle())) {
            requestStyle = new MockMvcRequestStyle();
        } else if (Objects.equals(Constants.DEFAULT_REQUEST_STYLE_CALL, SettingsManager.INSTANCE.getRequestStyle())) {
            requestStyle = new MethodCallRequestStyle();
        } else {
            throw new IllegalArgumentException("非法参数：Request的类型错误");
        }
        return new RequestStyleContext(requestStyle);
    }

    private static boolean createDefaultClassFile(
            ClassMetaInfo classMetaInfo,
            Set<PsiType> testClassImportSet,
            String testClassFieldText,
            String testClassMethodText,
            String includeMethodName
    ) {
        ClassTextEvent classTextEvent = new ClassTextEvent(
                classMetaInfo,
                testClassImportSet,
                testClassFieldText,
                testClassMethodText,
                includeMethodName
        );
        Appender appender = new DefaultFileAppender(true);
        return appender.append(classTextEvent);
    }

    public static List<MethodCoreBase> getMethodCoreBases(
            List<PsiMethod> classMethods,
            List<PsiField> classFields
    ) {
        List<MethodCoreBase> result = new ArrayList<>();
        for (PsiMethod method : classMethods) {
            MethodMetaInfo methodMetaInfo = CodeExtractor.extract(method);
            String methodBody = methodMetaInfo.getMethodBody();
            List<MethodCallInfo> methodCallInfos = new ArrayList<>();
            for (PsiField field : classFields) {
                MethodCallInfo methodCallInfo =
                        MethodCallInfoExtractor.extract(methodBody, field);
                if (methodCallInfo != null) {
                    methodCallInfos.add(methodCallInfo);
                }
            }
            if (!methodCallInfos.isEmpty()) {
                result.add(new MethodCoreBase(methodMetaInfo, methodCallInfos));
            }
        }
        return result;
    }

    public static List<PsiClass> findSpringBootApplicationClasses(
            @NotNull Project project,
            @NotNull com.intellij.openapi.module.Module module
    ) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiClass psiClass = psiFacade.findClass(
                Constants.ANNOTATION_TEXT_SPRING_BOOT_APPLICATION,
                GlobalSearchScope.allScope(project)
        );
        final List<PsiClass> result = new ArrayList<>();
        if (psiClass != null) {
            SearchScope searchScope = GlobalSearchScope.moduleScope(module);
            Query<PsiClass> query = AnnotatedElementsSearch.searchPsiClasses(psiClass, searchScope);
            query.forEach(new Processor<PsiClass>() {
                @Override
                public boolean process(PsiClass psiClass) {
                    result.add(psiClass);
                    System.out.println("Found class with @SpringBootApplication: " + psiClass.getQualifiedName());
                    return true;
                }
            });
        } else {
            System.out.println("SpringBootApplication annotation class not found.");
        }
        return result;
    }

    public static List<PsiClass> findSpringCloudApplicationClasses(
            @NotNull Project project,
            @NotNull com.intellij.openapi.module.Module module
    ) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        PsiClass psiClass = psiFacade.findClass(
                Constants.ANNOTATION_TEXT_SPRING_CLOUD_APPLICATION,
                GlobalSearchScope.allScope(project)
        );
        final List<PsiClass> result = new ArrayList<>();
        if (psiClass != null) {
            SearchScope searchScope = GlobalSearchScope.moduleScope(module);
            Query<PsiClass> query = AnnotatedElementsSearch.searchPsiClasses(
                    psiClass, searchScope
            );
            query.forEach(new Processor<PsiClass>() {
                @Override
                public boolean process(PsiClass psiClass) {
                    result.add(psiClass);
                    System.out.println("Found class with @SpringCloudApplication: " + psiClass.getQualifiedName());
                    return true;
                }
            });
        } else {
            System.out.println("SpringCloudApplication annotation class not found.");
        }
        return result;
    }

}
