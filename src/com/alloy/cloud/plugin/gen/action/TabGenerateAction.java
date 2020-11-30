package com.alloy.cloud.plugin.gen.action;

import com.alloy.cloud.plugin.gen.GenerateEngine;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

/**
 * 根据数据表生成代码
 *
 * @author tn_kec
 * @since 2019-10-22
 */
public class TabGenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        //判断是否选中了表元素
        if (!selected(event))
            return;

        //创建代码生成引擎
        new GenerateEngine(event).start();
    }

    /**
     * 判断右键是否选在表控件
     *
     * @param event
     * @return
     */
    private boolean selected(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return false;

        //获取选中的PSI元素
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
        if (psiElement instanceof DbTable) {
            DbTable selectDbTable = (DbTable) psiElement;
            if (selectDbTable == null)
                return false;
        }

        //获取选中的所有表
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) return false;

        boolean flag = false;//标记是否选中了表
        for (PsiElement element : psiElements) {
            if (!(element instanceof DbTable)) {
                continue;
            }
            flag = true;
            break;
        }

        //没有选中任何表
        if (!flag) return false;

        return true;
    }
}
