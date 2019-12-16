package com.hyphoon.idea.generator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;

import java.util.Iterator;
import java.util.List;

/**
 * Model注解的生成器
 *
 * @author Leo (HyphoonLee@tom.com)
 * @date 2019-12-13
 */
public class ModelAnnotationGenerator extends AbstractGenerator {

    public ModelAnnotationGenerator(PsiClass ownerClass, List<PsiMember> memberToGenerate) {
        super(ownerClass, memberToGenerate);
    }

    protected void generateCode() {
        this.addClassAnnotation(this.ownerClass);
        Iterator memberIterator = this.memberToGenerate.iterator();
        while (memberIterator.hasNext()) {
            PsiMember member = (PsiMember) memberIterator.next();
            if (member instanceof PsiField) {
                PsiField field = (PsiField) member;
                // 获取 JavaDoc
                String comment = fetchFieldComment(field);
                // String 类型的尝试获取长度
                String max = null;
                if (field.getType().getPresentableText().equals("String")) {
                    max = getAnnotationAttributeValueText(field, "javax.validation.constraints.Size", "max");
                    if (max == null) {
                        max = getAnnotationAttributeValueText(field, "javax.persistence.Column", "length");
                    }
                }
                StringBuffer sb = new StringBuffer();
                sb.append("@ApiModelProperty(value = \"");
                if (comment != null) {
                    sb.append(comment);
                }
                if (max != null) {
                    sb.append(" (").append(max).append(")");
                }
                sb.append("\")");
                this.addAnnotation(this.ownerClass, (PsiField) member, "io.swagger.annotations.ApiModelProperty", sb.toString());
            }
        }
    }

    /**
     * 添加类级别注解
     *
     * @param ownerClass
     */
    private void addClassAnnotation(PsiClass ownerClass) {
        String newAnnotationClassName = "io.swagger.annotations.ApiModel";
        if (ownerClass.getAnnotation(newAnnotationClassName) == null) {
            String comment = fetchFieldComment(ownerClass);
            String newAnnotationStatement = "@ApiModel(value = \"" + (comment != null ? comment : "") + "\", description = \"\")\n";
            this.addAnnotation(ownerClass, ownerClass, newAnnotationClassName, newAnnotationStatement);
        }
    }

}
