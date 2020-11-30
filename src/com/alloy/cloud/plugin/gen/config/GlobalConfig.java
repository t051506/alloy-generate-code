package com.alloy.cloud.plugin.gen.config;

/**
 * 全局配置信息
 *
 * @author tn_kec
 * @since 2019/07/18 16:43
 */
public interface GlobalConfig {
    /**
     * 默认编码
     */
    String ENCODE = "UTF-8";
    /**
     * 生成对象
     */
    String[] GENERATE_OBJECTS = new String[]{"entity.java", "mapper.java", "mapper.xml", "service.java", "controller.java", "dto.java","serviceImpl.java"};
    /**
     * 表公共列（字段）
     */
    String[] COMMON_COLUMNS = new String[]{"obj_version", "create_by", "create_time", "update_by", "update_time","is_delete"};
    /**
     * 默认模板地址
     */
    String TEMPLATE_DEFAULT_PATH = "/template/Default/";
    /**
     * Oracle模板地址
     */
    String TEMPLATE_ORACLE_PATH = "/template/Oracle/";
}
