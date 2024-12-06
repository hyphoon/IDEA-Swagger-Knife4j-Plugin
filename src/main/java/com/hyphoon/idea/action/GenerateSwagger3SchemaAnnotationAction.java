package com.hyphoon.idea.action;

import com.hyphoon.idea.generator.SchemaAnnotationGenerator;
import com.hyphoon.idea.ui.FieldSelectDialog;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 支持 Swagger v3 的 Schema 注解 Action
 *
 * @author Leo (HyphoonLee@tom.com)
 * @date 2023-05-24
 */
public class GenerateSwagger3SchemaAnnotationAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = this.getPsiClass(e);
        if (psiClass == null) {
            return;
        }
        FieldSelectDialog dialog = new FieldSelectDialog(psiClass, "io.swagger.v3.oas.annotations.media.Schema");
        dialog.show();
        if (dialog.isOK()) {
            List<PsiMember> members = dialog.getFields();
            if (members.size() > 0) {
                (new SchemaAnnotationGenerator(psiClass, members)).generate();
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = this.getPsiClass(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    /**
     * 获取 PsiClass
     *
     * @param e
     * @return
     */
    private PsiClass getPsiClass(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (psiFile != null && editor != null) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);
            return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        } else {
            return null;
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
