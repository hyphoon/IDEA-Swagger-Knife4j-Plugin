package com.hyphoon.idea.ui;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段选择对话框
 *
 * @author Leo (HyphoonLee@tom.com)
 * @date 2019-12-11
 */
public class FieldSelectDialog  extends DialogWrapper {
    private final LabeledComponent<JPanel> component;
    private final JList memberList;
    private final PsiClass ownerClass;
    private final String[] ignoreAnnotations;

    public FieldSelectDialog(PsiClass ownerClass, String... ignoreAnnotations) {
        super(ownerClass.getProject());
        this.ownerClass = ownerClass;
        this.ignoreAnnotations = ignoreAnnotations;
        this.setTitle("Generate Swagger And Knife4j Annotation");
        String className = this.ownerClass.getName();
        List<PsiField> fieldList = new ArrayList<>();
        for (PsiField f : ownerClass.getFields()) {
            if (!ignore(f)) {
                fieldList.add(f);
            }
        }
        CollectionListModel list = new CollectionListModel(fieldList.toArray());
        this.memberList = new JBList(list);
        this.memberList.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.memberList);
        decorator.disableAddAction();
        decorator.disableRemoveAction();
        decorator.disableUpDownActions();
        JPanel panel = decorator.createPanel();
        this.component = LabeledComponent.create(panel, "Select fields to generate annotation");
        this.init();
    }

    protected JComponent createCenterPanel() {
        return this.component;
    }

    public List<PsiMember> getFields() {
        return this.memberList.getSelectedValuesList();
    }

    /**
     * 判断是否该忽略 （已经有注解）
     *
     * @param field
     * @return
     */
    private boolean ignore(PsiField field) {
        if (ignoreAnnotations == null) {
            return false;
        }
        for (String ia : ignoreAnnotations) {
            PsiAnnotation annotation = field.getAnnotation(ia);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }
}
