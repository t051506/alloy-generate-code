package com.alloy.cloud.plugin.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.database.psi.DbTable;

import java.util.List;

/**
 * 表信息
 *
 * @author tn_kec
 * @since 2019/07/18 11:53
 */
public class TableInfo {
    /**
     * 原始对象
     */
    @JsonIgnore
    private DbTable dbTable;
    /**
     * 表名/Entity名
     */
    private String name;
    /**
     * 注释
     */
    private String comment;
    /**
     * 所有列
     */
    private List<ColumnInfo> fullColumn;
    /**
     * 主键列
     */
    private ColumnInfo pkColumn;
    /**
     * 其他列
     */
    private List<ColumnInfo> otherColumn;
    /**
     * 所有字段
     */
    private String baseColumnSql;
    /**
     * Dto字段
     */
    private String dtoBaseColumnSql;
    /**
     * 所有字段值
     */
    private String baseValuesSql;
    /**
     * java代码对象
     */
    private List<JavaClass> javaClasses;

    public DbTable getDbTable() {
        return dbTable;
    }

    public void setDbTable(DbTable dbTable) {
        this.dbTable = dbTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ColumnInfo> getFullColumn() {
        return fullColumn;
    }

    public void setFullColumn(List<ColumnInfo> fullColumn) {
        this.fullColumn = fullColumn;
    }

    public ColumnInfo getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(ColumnInfo pkColumn) {
        this.pkColumn = pkColumn;
    }

    public List<ColumnInfo> getOtherColumn() {
        return otherColumn;
    }

    public void setOtherColumn(List<ColumnInfo> otherColumn) {
        this.otherColumn = otherColumn;
    }

    public String getBaseColumnSql() {
        return baseColumnSql;
    }

    public void setBaseColumnSql(String baseColumnSql) {
        this.baseColumnSql = baseColumnSql;
    }

    public String getDtoBaseColumnSql() {
        return dtoBaseColumnSql;
    }

    public void setDtoBaseColumnSql(String dtoBaseColumnSql) {
        this.dtoBaseColumnSql = dtoBaseColumnSql;
    }

    public String getBaseValuesSql() {
        return baseValuesSql;
    }

    public void setBaseValuesSql(String baseValuesSql) {
        this.baseValuesSql = baseValuesSql;
    }

    public List<JavaClass> getJavaClasses() {
        return javaClasses;
    }

    public void setJavaClasses(List<JavaClass> javaClasses) {
        this.javaClasses = javaClasses;
    }
}
