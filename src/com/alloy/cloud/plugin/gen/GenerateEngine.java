package com.alloy.cloud.plugin.gen;

import com.alloy.cloud.plugin.gen.config.GlobalConfig;
import com.alloy.cloud.plugin.gen.config.MappingConfig;
import com.alloy.cloud.plugin.gen.entity.Selected;
import com.alloy.cloud.plugin.gen.entity.TableInfo;
import com.alloy.cloud.plugin.gen.entity.Template;
import com.alloy.cloud.plugin.gen.service.GenerateCodeService;
import com.alloy.cloud.plugin.gen.service.TableInfoService;
import com.alloy.cloud.plugin.gen.ui.TabSelectGenerateForm;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.fileTemplates.impl.UrlUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.util.ExceptionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成引擎
 *
 * @author tn_kec
 * @since 2019/07/19
 */
public class GenerateEngine {
    private List<TableInfo> tableInfos;
    private MappingConfig mappingConfig;
    private AnActionEvent event;
    private TableInfoService tableInfoService;

    public GenerateEngine(AnActionEvent event) {
        this.event = event;
        this.tableInfoService = TableInfoService.getInstance();
    }

    public void start() {
        //创建选择表单并打开
        TabSelectGenerateForm form = new TabSelectGenerateForm(event.getProject(), this);
        form.open();
    }

    public void generate(Selected selected) {
        this.mappingConfig = new MappingConfig(selected.getDatabase());
        parseTableInfo();
        // 构建JavaClass对象
        tableInfoService.buildJavaClasses(tableInfos, selected, loadTemplates(selected));
        // 生成代码
        GenerateCodeService generateCodeService = GenerateCodeService.getInstance();
        generateCodeService.generateCode(tableInfos);
    }

    /**
     * 加载模板对象
     */
    private List<Template> loadTemplates(Selected selected) {
        List<Template> templates = new ArrayList<>(selected.getObjects().size());
        for (String object : selected.getObjects()) {
            String templatePath;
            if ("oracle".equals(selected.getDatabase())) {
                templatePath = GlobalConfig.TEMPLATE_ORACLE_PATH + object + ".vm";
            } else {
                templatePath = GlobalConfig.TEMPLATE_DEFAULT_PATH + object + ".vm";
            }
            Template template = new Template(object, readTemplate(templatePath));
            templates.add(template);
        }
        return templates;
    }


    /**
     * 把数据表元素转换成表对象
     */
    private void parseTableInfo() {
        //获取选中的所有表
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        List<DbTable> dbTables = new ArrayList<>(psiElements.length);
        for (PsiElement element : psiElements) {
            if (element instanceof DbTable) {
                dbTables.add((DbTable) element);
            }
        }

        //得到tableInfo对象
        this.tableInfos = tableInfoService.getTableInfoByDbTable(dbTables, mappingConfig);
    }

    /**
     * 读取模板文件
     *
     * @param templatePath 模板路径
     * @return 模板文件内容
     */
    private static String readTemplate(String templatePath) {
        try {
            return UrlUtil.loadText(GenerateEngine.class.getResource(templatePath)).replace("\r", "");
        } catch (IOException e) {
            ExceptionUtil.rethrow(e);
        }
        return "";
    }
}
