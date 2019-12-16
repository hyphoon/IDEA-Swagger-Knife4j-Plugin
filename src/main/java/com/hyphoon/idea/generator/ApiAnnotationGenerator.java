package com.hyphoon.idea.generator;

import com.intellij.psi.*;

import java.util.Iterator;
import java.util.List;

/**
 * Api注解的生成器
 *
 * @author Leo (HyphoonLee@tom.com)
 * @date 2019-12-13
 */
public class ApiAnnotationGenerator extends AbstractGenerator {

    public ApiAnnotationGenerator(PsiClass ownerClass, List<PsiMember> memberToGenerate) {
        super(ownerClass, memberToGenerate);
    }

    protected void generateCode() {
        this.addClassAnnotation(this.ownerClass);
        Iterator memberIterator = this.memberToGenerate.iterator();

        while (memberIterator.hasNext()) {
            PsiMember member = (PsiMember) memberIterator.next();
            PsiMethod method = (PsiMethod) member;
            PsiParameterList parameterList = method.getParameterList();
            PsiParameter[] parameters = parameterList.getParameters();
            if (parameters.length > 0) {
                String annotationStatement = "@ApiImplicitParams({\n";

                for (int i = 0; i < parameters.length; ++i) {
                    PsiParameter parameter = parameters[i];
                    annotationStatement = annotationStatement + "@ApiImplicitParam(name = \"" + parameter.getName() + "\", value = \"\", dataType = \"" + this.convertSwaggerDataType(parameter.getType().getPresentableText()) + "\", paramType=\"\", required = " + this.getRequired(parameter) + ", example=\"\")";
                    if (i < parameters.length - 1) {
                        annotationStatement = annotationStatement + ",";
                    }
                    annotationStatement = annotationStatement + "\n";
                }

                annotationStatement = annotationStatement + "})";
                this.addAnnotation(this.ownerClass, method, "io.swagger.annotations.ApiImplicitParams", annotationStatement);
                this.addImportStatement(this.ownerClass, "io.swagger.annotations.ApiImplicitParam");
            }
            this.addAnnotation(this.ownerClass, method, "io.swagger.annotations.ApiOperation", "@ApiOperation(value = \"\", notes = \"\")");
        }
        return;
    }

    /**
     * 添加类级别注解
     *
     * @param ownerClass
     */
    private void addClassAnnotation(PsiClass ownerClass) {
        String newAnnotationClassName = "io.swagger.annotations.Api";
        if (ownerClass.getAnnotation(newAnnotationClassName) == null) {
            String comment = fetchFieldComment(ownerClass);
            String newAnnotationStatement = "@Api(tags = \"" + (comment != null ? comment : "") + "\")\n";
            this.addAnnotation(ownerClass, ownerClass, newAnnotationClassName, newAnnotationStatement);
        }
    }

    /**
     * Java -> JS 类型转换
     *
     * @param javaType
     * @return
     */
    private String convertSwaggerDataType(String javaType) {
        byte memberIterator = -1;
        switch (javaType) {
            case "String":
                memberIterator = 0;
                break;
            case "Integer":
                memberIterator = 1;
                break;
            case "int":
                memberIterator = 2;
                break;
            case "Long":
                memberIterator = 3;
                break;
            case "long":
                memberIterator = 4;
                break;
            case "Byte":
                memberIterator = 5;
                break;
            case "byte":
                memberIterator = 6;
                break;
            case "Short":
                memberIterator = 7;
                break;
            case "short":
                memberIterator = 8;
                break;
            case "Double":
                memberIterator = 9;
                break;
            case "double":
                memberIterator = 10;
                break;
            case "Float":
                memberIterator = 11;
                break;
            case "float":
                memberIterator = 12;
                break;
            case "Boolean":
                memberIterator = 13;
                break;
            case "boolean":
                memberIterator = 14;
                break;
            case "List":
                memberIterator = 15;
                break;
            case "MultipartFile":
                memberIterator = 16;
                break;
        }

        switch (memberIterator) {
            case 0:
                return "string";
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return "integer";
            case 9:
            case 10:
            case 11:
            case 12:
                return "number";
            case 13:
            case 14:
                return "boolean";
            case 15:
                return "array";
            case 16:
                return "file";
            default:
                return "";
        }
    }

    private String getRequired(PsiParameter parameter) {
        PsiAnnotation annotation = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
        return annotation != null ? annotation.findAttributeValue("required").getText() : "true";
    }

}
