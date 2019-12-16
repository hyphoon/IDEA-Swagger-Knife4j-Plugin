package com.hyphoon.idea.generator;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 生成器抽象父类
 *
 * @author Leo (HyphoonLee@tom.com)
 * @date 2019-12-16
 */
public abstract class AbstractGenerator {

    /**
     * HTML 的标签
     */
    private static final Pattern htmlPattern = Pattern.compile("^<.*>$");

    protected final PsiClass ownerClass;
    protected final List<PsiMember> memberToGenerate;
    protected final PsiElementFactory elementFactory;

    public AbstractGenerator(PsiClass ownerClass, List<PsiMember> memberToGenerate) {
        this.ownerClass = ownerClass;
        this.memberToGenerate = memberToGenerate;
        this.elementFactory = JavaPsiFacade.getElementFactory(ownerClass.getProject());
    }

    /**
     * 发起并执行代码生成
     */
    public void generate() {
        WriteCommandAction.writeCommandAction(this.ownerClass.getProject()).run(this::generateCode);
    }

    /**
     * 生成代码的具体过程
     */
    protected abstract void generateCode();


    /**
     * 增加类的 import
     * @param ownerClass
     * @param importClassName
     */
    protected void addImportStatement(PsiClass ownerClass, String importClassName) {
        PsiImportList importList = ((PsiJavaFileImpl) ownerClass.getContext()).getImportList();
        String packageName = importClassName.substring(0, importClassName.lastIndexOf("."));
        if (importList.findSingleClassImportStatement(importClassName) == null && importList.findOnDemandImportStatement(packageName) == null) {
            PsiClass importClass = this.elementFactory.createTypeByFQClassName(importClassName).resolve();
            PsiImportStatement importStatement = this.elementFactory.createImportStatement(importClass);
            ((PsiJavaFileImpl) ownerClass.getParent()).getImportList().add(importStatement);
        }
    }

    /**
     * 增加注解
     *
     * @param ownerClass
     * @param docCommentOwner
     * @param annotationFullName
     * @param annotationStatement
     */
    protected void addAnnotation(PsiClass ownerClass, PsiDocCommentOwner docCommentOwner, String annotationFullName, String annotationStatement) {
        if (docCommentOwner.getAnnotation(annotationFullName) == null) {
            this.addImportStatement(ownerClass, annotationFullName);
            PsiAnnotation newAnnotation = this.elementFactory.createAnnotationFromText(annotationStatement, docCommentOwner);
            docCommentOwner.getModifierList().addBefore(newAnnotation, docCommentOwner.getModifierList().getFirstChild());
        }
    }


    /**
     * 获取字段上的注释
     *
     * @param documentedElement
     * @return
     */
    protected String fetchFieldComment(PsiJavaDocumentedElement documentedElement) {
        if (documentedElement == null) {
            return null;
        }
        PsiDocComment psiDocComment = documentedElement.getDocComment();
        if (psiDocComment != null) {
            PsiDocToken token = null;
            // 取第一行非空注释
            for (PsiElement tmpEle : psiDocComment.getDescriptionElements()) {
                if (tmpEle instanceof PsiDocToken) {
                    PsiDocToken tmpToken = (PsiDocToken) tmpEle;
                    String text = tmpToken.getText();
                    if (text != null && text.trim().length() > 0) {
                        // 过滤掉单纯的 html 行
                        if (!htmlPattern.matcher(text.trim()).matches()) {
                            token = tmpToken;
                            break;
                        }
                    }
                }
            }
            if (token != null) {
                String rst = token.getText().trim();
                return rst;
            }
        }
        // 没有注释
        return null;
    }

    /**
     * 获取注解的属性
     *
     * @param modifierListOwner
     * @param annotationFullName
     * @param attributeName
     * @return
     */
    protected String getAnnotationAttributeValueText(PsiModifierListOwner modifierListOwner, String annotationFullName, String attributeName) {
        PsiAnnotation annotation = modifierListOwner.getAnnotation(annotationFullName);
        if (annotation != null) {
            PsiAnnotationMemberValue value = annotation.findAttributeValue(attributeName);
            if (value != null) {
                return value.getText().trim();
            }
        }
        return null;
    }
}
