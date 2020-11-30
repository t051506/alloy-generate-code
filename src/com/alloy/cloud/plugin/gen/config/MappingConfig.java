package com.alloy.cloud.plugin.gen.config;

import com.alloy.cloud.plugin.gen.entity.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置表到实体BEAN的映射关系
 *
 * @author tn_kec
 * @since 2019-07-18
 */
public class MappingConfig {
    private String database;

    public MappingConfig(String database) {
        this.database = database;
    }

    public List<Mapping> getMappings() {
        if (database.equals("mysql")) {
            return getMysqlMappings();
        } else if (database.equals("oracle")) {
            return getOracleMappings();
        } else {
            throw new RuntimeException(database + " database is not supported");
        }
    }

    private static List<Mapping> getMysqlMappings() {
        List<Mapping> mappings = new ArrayList<>();

        mappings.add(new Mapping("varchar(\\(\\d+\\))?", "java.lang.String"));
        mappings.add(new Mapping("char(\\(\\d+\\))?", "java.lang.String"));
        mappings.add(new Mapping("text", "java.lang.String"));
        mappings.add(new Mapping("decimal(\\(\\d+\\))?", "java.lang.Integer"));
        mappings.add(new Mapping("decimal(\\(\\d+,\\d+\\))?", "java.math.BigDecimal"));
        mappings.add(new Mapping("integer", "java.lang.Integer"));
        mappings.add(new Mapping("int(\\(\\d+\\))?", "java.lang.Integer"));
        mappings.add(new Mapping("int4", "java.lang.Integer"));
        mappings.add(new Mapping("int8", "java.lang.Long"));
        mappings.add(new Mapping("tinyint(\\(\\d+\\))?", "java.lang.Integer"));
        mappings.add(new Mapping("bigint(\\(\\d+\\))?", "java.lang.Long"));
        mappings.add(new Mapping("datetime", "java.time.LocalDateTime"));
        mappings.add(new Mapping("timestamp", "java.time.LocalDateTime"));
        mappings.add(new Mapping("boolean", "java.lang.Boolean"));
        return mappings;
    }

    private static List<Mapping> getOracleMappings() {
        List<Mapping> mappings = new ArrayList<>();

        mappings.add(new Mapping("char(\\(\\d+\\))?", "java.lang.String"));
        mappings.add(new Mapping("nchar(\\(\\d+\\))?", "java.lang.String"));
        mappings.add(new Mapping("varchar2(\\(\\d+\\))?", "java.lang.String"));
        mappings.add(new Mapping("nvarchar2(\\(\\d+\\))?", "java.lang.String"));
        mappings.add(new Mapping("long", "java.lang.String"));
        mappings.add(new Mapping("number(\\(\\d\\))?", "java.lang.Integer"));
        mappings.add(new Mapping("number(\\(\\d+\\))?", "java.lang.Long"));
        mappings.add(new Mapping("number(\\(\\d+,\\d+\\))?", "java.math.BigDecimal"));
        mappings.add(new Mapping("float(\\(\\d+,\\d+\\))?", "java.math.BigDecimal"));
        mappings.add(new Mapping("number\\(\\*\\)", "java.lang.Integer"));
        mappings.add(new Mapping("integer", "java.lang.Integer"));
        mappings.add(new Mapping("smallint", "java.lang.Integer"));
        mappings.add(new Mapping("date", "java.time.LocalDateTime"));
        mappings.add(new Mapping("timestamp", "java.time.LocalDateTime"));
        mappings.add(new Mapping("boolean", "java.lang.Boolean"));
        mappings.add(new Mapping("clob", "java.lang.String"));
        return mappings;
    }
}
