package com.alloy.cloud.plugin.gen.entity;

import java.util.List;

/**
 * 用户选择的参数
 *
 * @author tn_kec
 * @since 20191023
 */
public class Selected {
    /**
     * 作者
     */
    private String author;
    /**
     * 模块名称
     */
    private String module;
    /**
     * 数据库
     */
    private String database;
    /**
     * 包路径
     */
    private String pack;
    /**
     * 路径
     */
    private String path;
    /**
     * 需要生成代码的对象
     */
    private List<String> objects;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }
}
