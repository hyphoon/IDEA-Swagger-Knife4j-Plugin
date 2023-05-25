package com.hyphoon.idea.generator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;

import java.util.Iterator;
import java.util.List;

/**
 * 支持 Swagger v3 的 Schema注解的生成器
 *
 * @author Leo (HyphoonLee@tom.com)
 * @date 2023-05-24
 */
public class SchemaAnnotationGenerator extends AbstractGenerator {

    public SchemaAnnotationGenerator(PsiClass ownerClass, List<PsiMember> memberToGenerate) {
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
                    max = getAnnotationAttributeValueText(field, "jakarta.validation.constraints.Size", "max");
                    if (max == null) {
                        max = getAnnotationAttributeValueText(field, "jakarta.persistence.Column", "length");
                    }
                }
                StringBuffer sb = new StringBuffer();
                sb.append("@Schema(description = \"");
                if (comment != null) {
                    sb.append(comment);
                }
                if (max != null) {
                    sb.append(" (").append(max).append(")");
                }
                sb.append("\")");
                this.addAnnotation(this.ownerClass, (PsiField) member, "io.swagger.v3.oas.annotations.media.Schema", sb.toString());
            }
        }
    }

    /**
     * 添加类级别注解
     *
     * @param ownerClass
     */
    private void addClassAnnotation(PsiClass ownerClass) {
        String newAnnotationClassName = "io.swagger.v3.oas.annotations.media.Schema";
        if (ownerClass.getAnnotation(newAnnotationClassName) == null) {
            String comment = fetchFieldComment(ownerClass);
            String newAnnotationStatement = "@Schema(description = \"" + (comment != null ? comment : "") + "\")\n";
            this.addAnnotation(ownerClass, ownerClass, newAnnotationClassName, newAnnotationStatement);
        }
    }

}
