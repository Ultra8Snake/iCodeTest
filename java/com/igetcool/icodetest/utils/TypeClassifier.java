package com.igetcool.icodetest.utils;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * 类型处理工具类
 */
public class TypeClassifier {

    /**
     * 判断给定的PsiType是否表示String类型。
     *
     * @param psiType 要检查的PsiType对象
     * @return 如果PsiType是String类型，则返回true，否则返回false
     */
    public static boolean isStringType(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve();
            return psiClass != null && CommonClassNames.JAVA_LANG_STRING.equals(psiClass.getQualifiedName());
        }
        // 如果不是 PsiClassType 或者 PsiClass 不是 String 类型，则返回 false
        return false;
    }

    /**
     * 检查 PsiType 是否表示 void 或 Void 类型。
     *
     * @param psiType 要检查的 PsiType 对象
     * @return 如果是 void 或 Void 类型返回 true，否则返回 false
     */
    public static boolean isVoidOrJavaLangVoid(PsiType psiType) {
        // 检查是否是基本类型的void
        if (isVoidType(psiType)) {
            return true;
        }
        // 检查是否是PsiClassType且是java.lang.Void
        else if (isJavaLangVoidType(psiType)) {
            return true;
        }
        // 如果两者都不是，则返回false
        return false;
    }

    /**
     * 检查 PsiType 是否表示 void 或 Void 类型。
     *
     * @param psiType 要检查的 PsiType 对象
     * @return 如果不是 void 或 Void 类型返回 true，否则返回 false
     */
    public static boolean notVoidOrJavaLangVoid(PsiType psiType) {
        return !isVoidOrJavaLangVoid(psiType);
    }

    /**
     * 判断给定的PsiType是否表示一个接口、抽象类或枚举。
     *
     * @param psiType 需要判断的PsiType对象
     * @return 如果是接口、抽象类或枚举返回true，否则返回false
     */
    public static boolean isInterfaceOrAbstractClassOrEnum(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve(); // 解析PsiClass
            if (psiClass == null) {
                return false;
            }
            // 检查是否为接口、抽象类或枚举
            return psiClass.isInterface() || psiClass.hasModifierProperty(PsiModifier.ABSTRACT) || psiClass.isEnum();
        }
        return false;
    }

    /**
     * 判断给定的PsiType是否表示一个接口、抽象类或枚举。
     *
     * @param psiType 需要判断的PsiType对象
     * @return 如果不是接口、抽象类或枚举返回true，否则返回false
     */
    public static boolean notInterfaceOrAbstractClassOrEnum(PsiType psiType) {
        return !isInterfaceOrAbstractClassOrEnum(psiType);
    }

    /**
     * 判断给定的PsiType是否不是自定义类型。
     * 在此上下文中，自定义类型被定义为不是java.lang包下的类，
     * 也不是Java的基本数据类型。
     *
     * @param psiType 要检查的PsiType对象
     * @return 如果PsiType是自定义类型，则返回true；如果是自定义类型，则返回false
     */
    public static boolean isCustomType(PsiType psiType) {
        // 检查是否是基本数据类型
        if (psiType instanceof PsiPrimitiveType) {
            // 是基本数据类型，因此不是自定义类型
            return false;
        }
        // 检查是否是PsiClassType，即类类型
        if (!(psiType instanceof PsiClassType)) {
            // 不是类类型，可能是其他类型如数组类型、泛型类型等，我们认为它们不是自定义类型
            return false;
        }
        PsiClassType classType = (PsiClassType) psiType;
        PsiClass psiClass = classType.resolve(); // 解析PsiClass
        // 检查PsiClass是否为null或者在java.lang包下
        if (psiClass == null || psiClass.getQualifiedName() != null
                && psiClass.getQualifiedName().startsWith("java.lang")) {
            // 是java.lang包下的类或无法解析的类，因此不是自定义类型
            return false;
        }
        // 如果以上条件都不满足，那么我们可以认为这是一个自定义类型
        return true;
    }

    /**
     * 判断给定的PsiType是否不是自定义类型。
     * 在此上下文中，自定义类型被定义为不是java.lang包下的类，
     * 也不是Java的基本数据类型。
     *
     * @param psiType 要检查的PsiType对象
     * @return 如果PsiType不是自定义类型，则返回true；如果是自定义类型，则返回false
     */
    public static boolean notCustomType(PsiType psiType) {
        return !isCustomType(psiType);
    }

    /**
     * 获取给定类型的默认值的字符串表示形式。
     * 该方法为不同的数据类型提供了一个字符串形式的默认值，例如原始类型、字符串、集合、数组等。
     * 对于原始类型，返回其字面量的默认值；对于对象类型，通常返回"null"，除非是特定的类如字符串或集合。
     *
     * @param psiType 需要获取默认值的类型。
     * @return 返回类型的默认值的字符串表示形式。
     */
    public static String getDefaultValue(PsiType psiType) {
        return psiType.accept(new PsiTypeVisitor<String>() {
            @Override
            public String visitType(@NotNull PsiType type) {
                return "null"; // 默认为对象类型
            }

            @Override
            public String visitPrimitiveType(@NotNull PsiPrimitiveType primitiveType) {
                // 获取原始类型的名称
                String typeName = primitiveType.getPresentableText();
                // 根据原始类型的名称返回默认值
                switch (typeName) {
                    case "int":
                        return "0";
                    case "long":
                        return "0L";
                    case "short":
                        return "(short) 0";
                    case "byte":
                        return "(byte) 0";
                    case "double":
                        return "0.0";
                    case "float":
                        return "0.0f";
                    case "char":
                        return "'\\0'";
                    case "boolean":
                        return "false";
                }
                return "null";
            }

            @Override
            public String visitArrayType(@NotNull PsiArrayType arrayType) {
                // 处理数组类型的默认值
                PsiType componentType = arrayType.getComponentType();
                String componentDefaultValue = componentType.accept(this);
                return "new " + componentType.getCanonicalText() + "[0]"; // 返回空数组的初始化表达式
            }

            @Override
            public String visitClassType(@NotNull PsiClassType classType) {
                String className = classType.getCanonicalText();
                switch (className) {
                    case "java.lang.Integer":
                        return "0";
                    case "java.lang.Long":
                        return "0L";
                    case "java.lang.Short":
                        return "(short) 0";
                    case "java.lang.Byte":
                        return "(byte) 0";
                    case "java.lang.Double":
                        return "0.0";
                    case "java.lang.Float":
                        return "0.0f";
                    case "java.lang.Character":
                        return "'\\0'";
                    case "java.lang.Boolean":
                        return "false";
                    case "java.lang.String":
                        return "\"0\"";
                    case "java.util.Collection":
                        return "new ArrayList<>()";
                    default:
                        return "null";
                }
                // 对于其他类类型
            }

            // 可以覆盖其他 visit 方法，例如 visitEllipsisType 等
        });
    }

    private static boolean isJavaLangVoidType(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve();
            return psiClass != null && CommonClassNames.JAVA_LANG_VOID.equals(psiClass.getQualifiedName());
        }
        // 如果不是 PsiClassType 或者 PsiClass 不是 Void 类型，则返回 false
        return false;
    }

    private static boolean isVoidType(PsiType psiType) {
        return psiType instanceof PsiPrimitiveType && psiType.equalsToText("void");
    }
}
