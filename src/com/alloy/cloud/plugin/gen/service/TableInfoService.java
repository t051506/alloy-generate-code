package com.alloy.cloud.plugin.gen.service;

import com.alloy.cloud.plugin.gen.config.GlobalConfig;
import com.alloy.cloud.plugin.gen.config.MappingConfig;
import com.alloy.cloud.plugin.gen.entity.*;
import com.alloy.cloud.plugin.gen.util.StringUtil;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.containers.JBIterable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author tn_kec
 * @since 2019/07/20
 */
public class TableInfoService {
    public TableInfoService() {
    }

    public static TableInfoService getInstance() {
        return ServiceManager.getService(TableInfoService.class);
    }

    /**
     * 通过DbTable获取TableInfo
     *
     * @param dbTables 原始表对象
     * @return 表信息对象
     */
    public List<TableInfo> getTableInfoByDbTable(List<DbTable> dbTables, MappingConfig mappingConfig) {
        List<TableInfo> tableInfos = new ArrayList<>(dbTables.size());
        dbTables.forEach(dbTable -> tableInfos.add(getTableInfoByDbTable(dbTable, mappingConfig)));
        return tableInfos;
    }

    public TableInfo getTableInfoByDbTable(DbTable dbTable, MappingConfig mappingConfig) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setDbTable(dbTable);// 设置原属对象
        tableInfo.setName(StringUtil.toUpperHump(dbTable.getName().toLowerCase()));// 设置类名
        tableInfo.setComment(processTabComment(dbTable.getComment()));// 设置注释

        parseColumns(tableInfo, dbTable, mappingConfig);
        buildBaseColumnSql(tableInfo);
        buildDtoBaseColumnSql(tableInfo);
        buildBaseValuesSql(tableInfo);

        return tableInfo;
    }

    private String processTabComment(String comment) {
        if (StringUtil.isEmpty(comment)) {
            return null;
        }
        if (comment.endsWith("表")) {
            return comment.substring(0, comment.length() - 1);
        }
        return comment;
    }

    /**
     * 解析所有的列
     *
     * @param tableInfo
     * @param dbTable
     */
    private void parseColumns(TableInfo tableInfo, DbTable dbTable, MappingConfig mappingConfig) {
        // 处理所有列
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dbTable);
        List<ColumnInfo> fullColumns = new ArrayList<>(columns.size());
        ColumnInfo pkColumn = null;
        List<ColumnInfo> otherColumns = new ArrayList<>(columns.size() - 1);

        for (DasColumn column : columns) {
            //如果是公共列则跳过
            if (isCommonColumn(column)) continue;

            ColumnInfo columnInfo = toColumnInfo(column, mappingConfig);
            fullColumns.add(columnInfo);
            if (pkColumn == null && DasUtil.isPrimary(column)) {
                /**
                 * 这里只支持表只有一个主键的情况联合主键只会取第一个
                 */
                pkColumn = columnInfo;
            } else {
                otherColumns.add(columnInfo);
            }
        }

        //如果表表没有主键则选用第一个字段作为主键
        if (pkColumn == null) {
            pkColumn = fullColumns.get(0);
            otherColumns.remove(0);
        }
        tableInfo.setFullColumn(fullColumns);// 设置所有列
        tableInfo.setPkColumn(pkColumn);// 设置主键列
        tableInfo.setOtherColumn(otherColumns); // 设置其他列
    }

    private boolean isCommonColumn(DasColumn column) {
        for (String c : GlobalConfig.COMMON_COLUMNS) {
            if (c.equalsIgnoreCase(column.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换成ColumnInfo对象
     *
     * @param column
     * @return
     */
    private ColumnInfo toColumnInfo(DasColumn column, MappingConfig mappingConfig) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setObj(column);// 原始列对象
        columnInfo.setType(getColumnType(column.getDataType().getSpecification(), mappingConfig));// 列类型
        columnInfo.setName(StringUtil.toLowerHump(column.getName().toLowerCase()));// 列名
        columnInfo.setComment(processComment(column.getComment()));// 列注释
        columnInfo.setExt(new LinkedHashMap<>()); // 扩展项

        return columnInfo;
    }

    /**
     * 通过映射获取对应的java类型类型名称
     *
     * @param typeName 列类型
     * @return java类型
     */
    private String getColumnType(String typeName, MappingConfig mappingConfig) {
        for (Mapping mapping : mappingConfig.getMappings()) {
            // 不区分大小写进行类型转换
            if (Pattern.compile(mapping.getColumnType(), Pattern.CASE_INSENSITIVE).matcher(typeName).matches()) {
                return mapping.getJavaType();
            }
        }
        // 没找到直接返回Object
        return "java.lang.Object";
    }

    private String processComment(String comment) {
        if (comment == null) return null;
        return comment.replace("\r", "").replace("\n", "");
    }

    /**
     * 构建基本字段
     *
     * @param tableInfo
     */
    private void buildBaseColumnSql(TableInfo tableInfo) {
        StringBuilder baseSql = new StringBuilder();
        tableInfo.getFullColumn().forEach(columnInfo -> {
            String field = columnInfo.getObj().getName().toLowerCase();
            baseSql.append(field).append(",");
        });

        //添加公共字段
        for (String c : GlobalConfig.COMMON_COLUMNS) {
            baseSql.append(c).append(",");
        }

        baseSql.delete(baseSql.length() - 1, baseSql.length());
        tableInfo.setBaseColumnSql(baseSql.toString());
    }

    /**
     * 构建Dto基本字段
     *
     * @param tableInfo
     */
    private void buildDtoBaseColumnSql(TableInfo tableInfo) {
        StringBuilder baseSql = new StringBuilder();
        tableInfo.getFullColumn().forEach(columnInfo -> {
            String field = columnInfo.getObj().getName().toLowerCase();
            baseSql.append("t1.").append(field).append(",");
        });

        baseSql.delete(baseSql.length() - 1, baseSql.length());
        tableInfo.setDtoBaseColumnSql(baseSql.toString());
    }

    private void buildBaseValuesSql(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        tableInfo.getFullColumn().forEach(columnInfo -> {
            sb.append("#{").append(columnInfo.getName()).append("},");
        });

        //添加公共字段
        for (String c : GlobalConfig.COMMON_COLUMNS) {
            sb.append("#{").append(StringUtil.toLowerHump(c)).append("},");
        }

        sb.delete(sb.length() - 1, sb.length());
        tableInfo.setBaseValuesSql(sb.toString());
    }

    /**
     * 构建TableInfo的JavaClass对象
     *
     * @param tableInfos
     * @param selected
     */
    public void buildJavaClasses(List<TableInfo> tableInfos, Selected selected, List<Template> templates) {
        tableInfos.forEach(tableInfo -> buildJavaClass(tableInfo, selected, templates));
    }

    public void buildJavaClass(TableInfo tableInfo, Selected selected, List<Template> templates) {
        List<JavaClass> javaClasses = new ArrayList<>(templates.size());
        for (Template template : templates) {
            javaClasses.add(toJavaClass(tableInfo, selected, template));
        }

        tableInfo.setJavaClasses(javaClasses);
    }

    private JavaClass toJavaClass(TableInfo tableInfo, Selected selected, Template template) {
        JavaClass javaClass = new JavaClass();
        javaClass.setName(template.getName());//代码名称
        javaClass.setTemplate(template);//代码模板
        javaClass.setAuthor(selected.getAuthor());//作者
        javaClass.setClassName(generateClassName(tableInfo, template));//类名称
        javaClass.setModuleName(selected.getModule());
        javaClass.setImports(countImports(tableInfo, javaClass, selected.getPack()));
        javaClass.setEntityPackagePath(selected.getPack() + ".entity." + tableInfo.getName());
        javaClass.setDtoPackagePath(selected.getPack() + ".dto." + tableInfo.getName() + "DTO");
        javaClass.setPackagePath(processPackageName(javaClass, selected.getPack()));
        javaClass.setGeneratePath(processGeneratePath(javaClass, selected.getPath()));
        javaClass.setFileName(javaClass.getTemplate().getName().equals("mapper.xml") ? javaClass.getClassName() + ".xml" : javaClass.getClassName() + ".java");

        return javaClass;
    }

    private String generateClassName(TableInfo tableInfo, Template template) {
        if ("entity.java".equals(template.getName())) {
            return tableInfo.getName();
        } else if ("mapper.java".equals(template.getName())) {
            return tableInfo.getName() + "Mapper";
        } else if ("mapper.xml".equals(template.getName())) {
            return tableInfo.getName() + "Mapper";
        } else if ("service.java".equals(template.getName())) {
            return tableInfo.getName() + "Service";
        } else if ("controller.java".equals(template.getName())) {
            return tableInfo.getName() + "Controller";
        } else if ("dto.java".equals(template.getName())) {
            return tableInfo.getName() + "DTO";
        }else if ("serviceImpl.java".equals(template.getName())) {
            return tableInfo.getName() + "ServiceImpl";
        }
        return "";
    }

    private List<String> countImports(TableInfo tableInfo, JavaClass javaClass, String packageName) {
        if ("entity.java".equals(javaClass.getName()) || javaClass.getName().equals("dto.java")) {
            return countEntityImports(tableInfo);
        } else if ("mapper.java".equals(javaClass.getName())) {
            return countMapperImports(tableInfo, packageName);
        } else if ("service.java".equals(javaClass.getName())) {
            return countServiceImports(tableInfo, packageName);
        } else if ("controller.java".equals(javaClass.getName())) {
            return countControllerImports(tableInfo, packageName);
        }else if ("serviceImpl.java".equals(javaClass.getName())) {
            return countServiceImplImports(tableInfo, packageName);
        }
        return null;
    }

    private List<String> countEntityImports(TableInfo tableInfo) {
        Set<String> imports = new HashSet<>(3);

        for (ColumnInfo columnInfo : tableInfo.getFullColumn()) {
            if ("java.time.LocalDateTime".equals(columnInfo.getType())
                    || "java.math.BigDecimal".equals(columnInfo.getType())
                    || "java.time.LocalDate".equals(columnInfo.getType())){
                imports.add(columnInfo.getType());
            }
        }
        return new ArrayList<>(imports);
    }

    private List<String> countServiceImports(TableInfo tableInfo, String packageName) {
        List<String> imports = new ArrayList<>(2);
        imports.add(packageName + ".mapper." + tableInfo.getName() + "Mapper");
        imports.add(packageName + ".entity." + tableInfo.getName());
        return imports;
    }

    private List<String> countMapperImports(TableInfo tableInfo, String packageName) {
        List<String> imports = new ArrayList<>(1);
        imports.add(packageName + ".entity." + tableInfo.getName());
        return imports;
    }

    private List<String> countControllerImports(TableInfo tableInfo, String packageName) {
        List<String> imports = new ArrayList<>(3);
        imports.add(String.format("%s.entity.%s", packageName, tableInfo.getName()));
        imports.add(String.format("%s.dto.%sDTO", packageName, tableInfo.getName()));
        imports.add(String.format("%s.service.%sService", packageName, tableInfo.getName()));
        return imports;
    }

    private List<String> countServiceImplImports(TableInfo tableInfo,String packageName){
        List<String> imports = new ArrayList<>(1);
        imports.add(String.format("%s.entity.%s", packageName, tableInfo.getName()));
        imports.add(String.format("%s.service.%sService", packageName, tableInfo.getName()));
        return imports;
    }

    private String processPackageName(JavaClass javaClass, String packageName) {
        if ("entity.java".equals(javaClass.getName())) {
            return packageName + ".entity";
        } else if ("mapper.java".equals(javaClass.getName())) {
            return packageName + ".mapper";
        } else if ("mapper.xml".equals(javaClass.getName())) {
            return packageName + ".mapper";
        } else if ("service.java".equals(javaClass.getName())) {
            return packageName + ".service";
        } else if ("controller.java".equals(javaClass.getName())) {
            return packageName + ".controller.api.v1";
        } else if ("dto.java".equals(javaClass.getName())) {
            return packageName + ".dto";
        }else if ("serviceImpl.java".equals(javaClass.getName())) {
            return packageName + ".service.impl";
        }
        return "";
    }

    private String processGeneratePath(JavaClass javaClass, String path) {
        if ("entity.java".equals(javaClass.getName())) {
            return path + "/entity";
        } else if ("mapper.java".equals(javaClass.getName())) {
            return path + "/mapper";
        } else if ("mapper.xml".equals(javaClass.getName())) {
            return path.substring(0, path.indexOf("/src/")) + "/src/main/resources/mapper";
        } else if ("service.java".equals(javaClass.getName())) {
            return path + "/service";
        } else if ("controller.java".equals(javaClass.getName())) {
            return path + "/controller/api/v1";
        } else if ("dto.java".equals(javaClass.getName())) {
            return path + "/dto";
        }else if ("serviceImpl.java".equals(javaClass.getName())) {
            return path + "/service/impl";
        }
        return "";
    }
}
