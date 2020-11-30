package com.alloy.cloud.plugin.gen;

import com.alloy.cloud.plugin.gen.entity.TableInfo;
import com.alloy.cloud.plugin.gen.ui.TabSelectGenerateForm;
import com.intellij.database.psi.DbTable;

import java.util.List;

public class TabGenerator {

    /**
     * 选中的数据库元素
     */
    private List<DbTable> dbTables;

    /**
     * 选中的数据库对象
     */
    private List<TableInfo> tableInfos;

    /**
     * 设置生成代码路径的表单
     */
    private TabSelectGenerateForm tabSelectGenerateForm;

    public List<DbTable> getDbTables() {
        return dbTables;
    }

    public void setDbTables(List<DbTable> dbTables) {
        this.dbTables = dbTables;
    }

    public List<TableInfo> getTableInfos() {
        return tableInfos;
    }

    public void setTableInfos(List<TableInfo> tableInfos) {
        this.tableInfos = tableInfos;
    }

    public TabSelectGenerateForm getTabSelectGenerateForm() {
        return tabSelectGenerateForm;
    }

    public void setTabSelectGenerateForm(TabSelectGenerateForm tabSelectGenerateForm) {
        this.tabSelectGenerateForm = tabSelectGenerateForm;
    }

    public void start() {
        //打开代码生成器选择窗口
        tabSelectGenerateForm.open();
    }
}
