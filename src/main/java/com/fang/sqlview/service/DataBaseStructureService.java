package com.fang.sqlview.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.fang.sqlview.common.Constant;
import com.fang.sqlview.common.MyUtils;
import com.fang.sqlview.domain.JavaEntityField;
import com.fang.sqlview.domain.UploadJsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
@Slf4j
@Service
public class DataBaseStructureService {

    @Autowired
    private VelocityEngine velocityEngine;
    @Value("${sql.path}")
    private String sqlPath;

    public UploadJsonResult process(String dataBaseName, InputStream inputStream){
        String sqlStr = IoUtil.read(inputStream).toString();
        FileUtil.writeUtf8String(sqlStr, sqlPath+dataBaseName+".sql");

        Collection<SQLCreateTableStatement> createTableStatements = createSQLStatement(sqlStr);
        return convert(createTableStatements);
    }

    private Collection<SQLCreateTableStatement> createSQLStatement(String sqlStr){
        List<SQLStatement> result = SQLUtils.parseStatements(sqlStr, "mysql");

        Map<String, SQLCreateTableStatement> createTableStatementMap = new HashMap<>();
        result.stream()
                .filter(a -> a instanceof SQLCreateTableStatement)
                .map(a -> (SQLCreateTableStatement) a)
                .forEach(a -> createTableStatementMap.put(MyUtils.sqlFiledReplace(a.getName().getSimpleName()), a));

        result.stream()
                .filter(a -> a instanceof SQLAlterTableStatement)
                .map(a -> (SQLAlterTableStatement) a)
                .filter(a -> createTableStatementMap.get(MyUtils.sqlFiledReplace(a.getTableName())) != null)
                .forEach(a -> createTableStatementMap.get(MyUtils.sqlFiledReplace(a.getTableName())).apply(a));

        return createTableStatementMap.values();
    }

    private UploadJsonResult convert(Collection<SQLCreateTableStatement> createTableStatements){
        UploadJsonResult result = new UploadJsonResult();
        createTableStatements.stream().forEach(a->{
            UploadJsonResult.Table table = new UploadJsonResult.Table();
            table.setTableName(a.getTableSource().getName().getSimpleName());
            table.setTableComment(a.getComment()!=null? a.getComment().toString() : "");

            result.getTableList().add(table);

            a.getTableElementList().forEach(c->{
                UploadJsonResult.Column column = new UploadJsonResult.Column();

                if (c instanceof SQLColumnDefinition){
                    SQLColumnDefinition columnDefinition = (SQLColumnDefinition)c;
                    column.setColumnName(MyUtils.sqlFiledReplace(columnDefinition.getName().getSimpleName()));
                    column.setColumnComment(columnDefinition.getComment()!=null ? columnDefinition.getComment().toString() : "");
                    column.setColumnType(columnDefinition.getDataType().toString());

                    long count = columnDefinition.getConstraints().stream().filter(d->d instanceof SQLNotNullConstraint).count();
                    if (count > 0){
                        column.setIsNullable("否");
                    }

                    table.getColumnList().add(column);
                }else if(c instanceof MySqlPrimaryKey){
                    MySqlPrimaryKey primaryKey = (MySqlPrimaryKey)c;
                    primaryKey.getColumns().forEach(p->{
                        String key = MyUtils.sqlFiledReplace(p.getExpr().toString());
                        Optional<UploadJsonResult.Column> keyColumn = table.getColumnList().stream()
                                .filter(m->m.getColumnName().equals(key))
                                .findFirst();

                        if (keyColumn.isPresent()){
                            keyColumn.get().setColumnKey("是");
                        }
                    });
                }
            });
        });
        return result;
    }

    /**
     * word生成
     * @param fileId
     * @return
     */
    public String createWord(String fileId){
        UploadJsonResult jsonResult = readFileToObject(fileId);

        return renderWordTemplate(jsonResult);
    }

    public File createJavaCodeZip(String fileId, String codeStyle){
        List<JavaEntityField> entityFields = createJavaCode(fileId);

        return createJavaCode(entityFields,codeStyle);
    }

    public List<JavaEntityField> createJavaCode(String fileId){
        UploadJsonResult jsonResult = readFileToObject(fileId);

        return jsonResult.getTableList()
                .stream()
                .map(table -> {
                    JavaEntityField entityField = new JavaEntityField();
                    entityField.setTableComment(table.getTableComment());
                    entityField.setTableName(table.getTableName());

                    table.getColumnList().forEach(column -> {
                        JavaEntityField.Field field = new JavaEntityField.Field();
                        field.setComment(column.getColumnComment());
                        field.setName(column.getColumnName());
                        field.setType(column.getColumnType());
                        field.setKey(column.getColumnKey()!=null ? true : false);

                        entityField.getFieldList().add(field);
                    });
                    return entityField;
                }).collect(Collectors.toList());
    }

    public File createJavaCode(List<JavaEntityField> entityFields,String codeStyle){
        String srcPath = sqlPath + RandomUtil.randomString(8) + "/";
        renderJavaCode(entityFields, srcPath, codeStyle);

        return ZipUtil.zip(srcPath);
    }

    private UploadJsonResult readFileToObject(String fileId){
        String sqlStr = FileUtil.readUtf8String(sqlPath + fileId + ".sql");

        Collection<SQLCreateTableStatement> createTableStatements = createSQLStatement(sqlStr);
        return convert(createTableStatements);
    }

    public String renderWordTemplate(UploadJsonResult jsonResult){
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_WORD,"UTF-8");

        Map<String, Object> map = new HashMap<>();
        map.put("jsonResult", jsonResult);

        StringWriter writer = new StringWriter();
        template.merge(new VelocityContext(map), writer);
        return writer.toString();
    }

    public String codePreview(String codeStyle){
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_CODEPREVIEW,"UTF-8");
        Map<String, Object> map = codeStyleMap(codeStyle);

        StringWriter writer = new StringWriter();
        template.merge(new VelocityContext(map), writer);
        return writer.toString();
    }

    private Map<String,Object> codeStyleMap(String codeStyle){
        Map<String, Object> map = new HashMap<>();
        if (codeStyle.contains("lombok")){
            map.put("lombok", true);
        }

        if (codeStyle.contains("jpa")){
            map.put("jpa", true);
        }
        return map;
    }

    public void renderJavaCode(List<JavaEntityField> entityFields, String path,String codeStyle){
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_JAVACODE,"UTF-8");

        Map<String, Object> map = codeStyleMap(codeStyle);
        entityFields.forEach(field->{
            map.put("data", field);

            try {
                FileUtil.mkdir(path);
                try(FileWriter writer = new FileWriter(new File(path + field.getClassName() + ".java"))){
                    template.merge(new VelocityContext(map), writer);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        });

    }
}
