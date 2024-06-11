package com.igetcool.icodetest.style;

import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.MethodCoreBase;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.List;
import java.util.Set;

/**
 * RequestStyleContext 类封装了与特定请求风格相关的所有操作。
 * 它通过持有一个 RequestStyle 接口的实例来委托方法调用，从而提供了一种灵活的方式来处理不同类型的请求风格。
 * 这个类作为请求风格的上下文，可以在代码生成过程中保持一致的请求风格行为。
 */
public class RequestStyleContext {

    /**
     * 代表当前请求风格的策略对象。
     */
    private final RequestStyle requestStyle;

    /**
     * 使用给定的请求风格策略对象构造 RequestStyleContext。
     *
     * @param requestStyle 一个实现了 RequestStyle 接口的对象，它定义了请求风格特定的行为。
     */
    public RequestStyleContext(RequestStyle requestStyle) {
        this.requestStyle = requestStyle;
    }

    /**
     * 获取当前请求风格的标识符。
     *
     * @return 当前请求风格的字符串表示。
     */
    public String getStyle() {
        return requestStyle.getStyle();
    }

    /**
     * 根据提供的字段集合获取所需的导入集合。
     *
     * @param psiFields 字段集合
     * @return 返回一个包含所有必需导入的 PsiType 的集合
     */
    public Set<PsiType> getFieldImportSet(List<PsiField> psiFields) {
        return requestStyle.getFieldImportSet(psiFields);
    }

    /**
     * 根据提供的方法核心信息集合获取所需的导入集合。
     *
     * @param methodCoreBases 方法核心信息集合
     * @return 返回一个包含所有必需导入的 PsiType 的集合
     */
    public Set<PsiType> getMethodImportSet(List<MethodCoreBase> methodCoreBases) {
        return requestStyle.getMethodImportSet(methodCoreBases);
    }

    /**
     * 生成测试类的字段定义。
     *
     * @param classFields   类的字段列表
     * @param classMetaInfo 类的元信息
     * @return 返回测试类字段定义的代码字符串
     */
    public String generateTestClassField(List<PsiField> classFields, ClassMetaInfo classMetaInfo) {
        return requestStyle.generateTestClassField(classFields, classMetaInfo);
    }

    /**
     * 生成测试类的方法定义。
     *
     * @param methodCoreBases 包含方法核心信息的列表
     * @param classMetaInfo   类的元信息
     * @return 返回测试类方法定义的代码字符串
     */
    public String generateTestClassMethod(List<MethodCoreBase> methodCoreBases, ClassMetaInfo classMetaInfo) {
        return requestStyle.generateTestClassMethod(methodCoreBases, classMetaInfo);
    }

}
