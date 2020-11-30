package com.alloy.cloud.plugin.gen.entity;


/**
 * 类型隐射信息
 *
 * @author tn_kec
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class Mapping {
    /**
     * 列类型
     */
    private String columnType;
    /**
     * java类型
     */
    private String javaType;

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public Mapping(String columnType, String javaType) {
        this.columnType = columnType;
        this.javaType = javaType;
    }
}
