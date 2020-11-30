package com.alloy.cloud.plugin.gen.service;

import com.alloy.cloud.plugin.gen.entity.JavaClass;
import com.alloy.cloud.plugin.gen.entity.TableInfo;
import com.alloy.cloud.plugin.gen.util.VelocityUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateCodeService {
    public GenerateCodeService() {
    }

    public static GenerateCodeService getInstance() {
        return ServiceManager.getService(GenerateCodeService.class);
    }

    public void generateCode(List<TableInfo> tableInfos) {
        tableInfos.forEach(tableInfo ->
                tableInfo.getJavaClasses().forEach(javaClass -> generateCode(tableInfo, javaClass)));
        //刷新整个项目
        VirtualFileManager.getInstance().syncRefresh();
    }

    private void generateCode(TableInfo tableInfo, JavaClass javaClass) {
        // 开始生成
        String code = VelocityUtil.generate(javaClass.getTemplate().getCode(), toParams(tableInfo, javaClass));

        // 创建目录
        File dir = new File(javaClass.getGeneratePath());
        if (!dir.exists()) {
            // 提示创建目录
            if (!MessageDialogBuilder.yesNo("GenerateCode", "Directory " + javaClass.getPackagePath() + " Not Found, Confirm Create?").isYes()) {
                return;
            }
            if (!dir.mkdirs()) {
                Messages.showWarningDialog("Directory Create Failure!", "GenerateCode");
                return;
            }
        }
        File file = new File(dir, javaClass.getFileName());
        // 提示是否覆盖文件
        if (file.exists()) {
            if (!MessageDialogBuilder.yesNo("GenerateCode", "File " + file.getName() + " Exists, Confirm Continue?").isYes()) {
                return;
            }
        }
        // 保存文件
        try {
            FileUtil.writeToFile(file, code, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> toParams(TableInfo tableInfo, JavaClass javaClass) {
        Map<String, Object> params = new HashMap<>();
        params.put("tab", toTableParams(tableInfo));
        params.put("jc", toJavaClassParams(javaClass));

        return params;
    }

    private Map<String, Object> toTableParams(TableInfo tableInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("dbTable", tableInfo.getDbTable());
        params.put("name", tableInfo.getName());
        params.put("comment", tableInfo.getComment());
        params.put("fullColumn", tableInfo.getFullColumn());
        params.put("pkColumn", tableInfo.getPkColumn());
        params.put("otherColumn", tableInfo.getOtherColumn());
        params.put("baseColumnSql", tableInfo.getBaseColumnSql());
        params.put("dtoBaseColumnSql", tableInfo.getDtoBaseColumnSql());
        params.put("baseValuesSql", tableInfo.getBaseValuesSql());

        return params;
    }

    private Map<String, Object> toJavaClassParams(JavaClass javaClass) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", javaClass.getName());
        params.put("author", javaClass.getAuthor());
        params.put("entityPackagePath", javaClass.getEntityPackagePath());
        params.put("dtoPackagePath", javaClass.getDtoPackagePath());
        params.put("moduleName", javaClass.getModuleName());
        params.put("imports", javaClass.getImports());
        params.put("packagePath", javaClass.getPackagePath());
        params.put("className", javaClass.getClassName());
        params.put("fileName", javaClass.getFileName());

        return params;
    }
}
