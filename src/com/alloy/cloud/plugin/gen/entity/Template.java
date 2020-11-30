package com.alloy.cloud.plugin.gen.entity;

/**
 * 模板信息类
 *
 * @author tn_kec
 * @version 1.0.0
 * @since 2019/07/19 14:50
 */

public class Template {
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板代码
     */
    private String code;

    public Template(String name, String code){
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
