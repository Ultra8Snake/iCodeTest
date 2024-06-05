package com.igetcool.icodetest.utils;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class TypeClassifier {

    public static boolean isStringType(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve();
            return psiClass != null && CommonClassNames.JAVA_LANG_STRING.equals(psiClass.getQualifiedName());
        }
        return false;
    }

    public static boolean isVoidOrJavaLangVoid(PsiType psiType) {
        if (isVoidType(psiType)) {
            return true;
        }
        else if (isJavaLangVoidType(psiType)) {
            return true;
        }
        return false;
    }

    public static boolean notVoidOrJavaLangVoid(PsiType psiType) {
        return !isVoidOrJavaLangVoid(psiType);
    }

    public static boolean isInterfaceOrAbstractClassOrEnum(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve();
            if (psiClass == null) {
                return false;
            }
            return psiClass.isInterface() || psiClass.hasModifierProperty(PsiModifier.ABSTRACT) || psiClass.isEnum();
        }
        return false;
    }

    public static boolean notInterfaceOrAbstractClassOrEnum(PsiType psiType) {
        return !isInterfaceOrAbstractClassOrEnum(psiType);
    }

    public static boolean isCustomType(PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType) {
            return false;
        }
        if (!(psiType instanceof PsiClassType)) {
            return false;
        }
        PsiClassType classType = (PsiClassType) psiType;
        PsiClass psiClass = classType.resolve();
        if (psiClass == null || psiClass.getQualifiedName() != null
                && psiClass.getQualifiedName().startsWith("java.lang")) {
            return false;
        }
        return true;
    }

    public static boolean notCustomType(PsiType psiType) {
        return !isCustomType(psiType);
    }

    public static String getDefaultValue(PsiType psiType) {
        return psiType.accept(new PsiTypeVisitor<String>() {
            @Override
            public String visitType(@NotNull PsiType type) {
                return "null";
            }

            @Override
            public String visitPrimitiveType(@NotNull PsiPrimitiveType primitiveType) {
                String typeName = primitiveType.getPresentableText();
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
                PsiType componentType = arrayType.getComponentType();
                String componentDefaultValue = componentType.accept(this);
                return "new " + componentType.getCanonicalText() + "[0]";
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
            }
        });
    }

    private static boolean isJavaLangVoidType(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) psiType;
            PsiClass psiClass = classType.resolve();
            return psiClass != null && CommonClassNames.JAVA_LANG_VOID.equals(psiClass.getQualifiedName());
        }
        return false;
    }

    private static boolean isVoidType(PsiType psiType) {
        return psiType instanceof PsiPrimitiveType && psiType.equalsToText("void");
    }
}
