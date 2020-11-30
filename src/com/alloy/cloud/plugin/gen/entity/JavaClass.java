package com.alloy.cloud.plugin.gen.entity;

import java.util.List;

/**
 * 生成Java文件对象
 *
 * @author tn_kec
 * @since 2019/07/19 14:50
 */
public class JavaClass {
    private String name;//代码名称（同模名称）
    private Template template;//代码模板
    private String generatePath;//生成代码路径
    private String author;//作者
    private String entityPackagePath;//实体路径
    private String dtoPackagePath;//实体路径
    private String moduleName;//模块名称
    private String packagePath;//包路径
    private List<String> imports;//要引入的包
    private String className;//类名称
    private String fileName;//文件名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getGeneratePath() {
        return generatePath;
    }

    public void setGeneratePath(String generatePath) {
        this.generatePath = generatePath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEntityPackagePath() {
        return entityPackagePath;
    }

    public void setEntityPackagePath(String entityPackagePath) {
        this.entityPackagePath = entityPackagePath;
    }

    public String getDtoPackagePath() {
        return dtoPackagePath;
    }

    public void setDtoPackagePath(String dtoPackagePath) {
        this.dtoPackagePath = dtoPackagePath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
