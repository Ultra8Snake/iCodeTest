package com.igetcool.icodetest.style;

import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.MethodCoreBase;
import com.igetcool.icodetest.models.MethodMetaInfo;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.List;
import java.util.Set;

/**
 * RequestStyle 接口定义了一组方法，用于根据不同的请求风格生成测试代码。
 * 实现此接口的类将提供特定风格的测试代码生成逻辑，包括获取请求风格、
 * 确定字段和方法所需的导入、调用方法以及生成测试类字段和方法的定义。
 */
public interface RequestStyle {

    /**
     * 获取当前请求风格的标识符。
     * 该标识符用于区分不同的请求风格，例如 "call" 或 "mock"。
     *
     * @return 请求风格的字符串表示。
     */
    String getStyle();

    /**
     * 根据类的字段集合确定所需的导入。
     * 此方法分析给定的字段集合，并返回一个包含所有必需导入的PsiType的集合。
     *
     * @param classFields 类的字段集合
     * @return 一个包含所有必需导入的PsiType的集合。
     */
    Set<PsiType> getFieldImportSet(List<PsiField> classFields);

    /**
     * 根据方法集合确定所需的导入。
     * 此方法分析给定的方法集合，并返回一个包含所有必需导入的PsiType的集合。
     *
     * @param methodCoreBases 测试方法的核心信息集合
     * @return 一个包含所有必需导入的PsiType的集合。
     */
    Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases);

    /**
     * 生成方法调用的代码字符串。
     * 根据提供的方法元信息和类元信息，生成调用方法的代码字符串。
     *
     * @param methodMetaInfo 方法的元信息
     * @param classMetaInfo  类的元信息
     * @return 方法调用的代码字符串。
     */
    String callMethod(MethodMetaInfo methodMetaInfo, ClassMetaInfo classMetaInfo);

    /**
     * 生成测试类的字段定义。
     * 根据提供的字段集合和类元信息，生成测试类中字段的定义。
     *
     * @param classFields   类的字段集合
     * @param classMetaInfo 类的元信息
     * @return 测试类字段定义的代码字符串。
     */
    String generateTestClassField(List<PsiField> classFields, ClassMetaInfo classMetaInfo);

    /**
     * 生成测试类的方法定义。
     * 根据提供的方法集合和类元信息，生成测试类中的方法定义。
     *
     * @param methodCoreBases 测试方法的核心信息集合
     * @param classMetaInfo   类的元信息
     * @return 测试类方法定义的代码字符串。
     */
    String generateTestClassMethod(List<MethodCoreBase> methodCoreBases, ClassMetaInfo classMetaInfo);
}
