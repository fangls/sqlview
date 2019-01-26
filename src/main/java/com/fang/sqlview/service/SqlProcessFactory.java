package com.fang.sqlview.service;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.fang.sqlview.common.MyUtils;
import com.fang.sqlview.domain.TransferJsonResult;

import java.util.*;

/**
 * Description：sql处理的工厂
 *
 * @author fangliangsheng
 * @date 2019/1/25
 */
public class SqlProcessFactory {

    /**
     * 将sql脚本转换为结构化的java对象
     *
     * @param sql
     * @return
     */
    public static TransferJsonResult transfer(String sql, String dbType) {
        Collection<SQLCreateTableStatement> createTableStatements = createSQLStatement(sql, dbType);
        return convert(createTableStatements);
    }

    /**
     * 将sql脚本解析为SQLCreateTableStatement对象
     * 只保留SQLCreateTableStatement类型的脚本，其他类型的去除
     *
     * @param sqlStr
     * @return
     */
    private static Collection<SQLCreateTableStatement> createSQLStatement(String sqlStr, String dbType) {
        List<SQLStatement> result = SQLUtils.parseStatements(sqlStr, dbType);

        LinkedHashMap<String, SQLCreateTableStatement> createTableStatementMap = new LinkedHashMap<>();
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

    /**
     * 将SQLCreateTableStatement结构的对象转换为自定义的结构
     *
     * @param createTableStatements
     * @return
     */
    private static TransferJsonResult convert(Collection<SQLCreateTableStatement> createTableStatements) {
        TransferJsonResult result = new TransferJsonResult();
        createTableStatements.stream().forEach(a -> {
            TransferJsonResult.Table table = new TransferJsonResult.Table();
            table.setTableName(a.getTableSource().getName().getSimpleName());
            table.setTableComment(a.getComment() != null ? a.getComment().toString() : "");

            result.getTableList().add(table);

            a.getTableElementList().forEach(c -> {
                TransferJsonResult.Column column = new TransferJsonResult.Column();

                if (c instanceof SQLColumnDefinition) {
                    SQLColumnDefinition columnDefinition = (SQLColumnDefinition) c;
                    column.setColumnName(MyUtils.sqlFiledReplace(columnDefinition.getName().getSimpleName()));
                    column.setColumnComment(columnDefinition.getComment() != null ? columnDefinition.getComment().toString() : "");
                    column.setColumnType(columnDefinition.getDataType().toString());

                    long count = columnDefinition.getConstraints().stream().filter(d -> d instanceof SQLNotNullConstraint).count();
                    if (count > 0) {
                        column.setIsNullable("否");
                    }

                    table.getColumnList().add(column);
                } else if (c instanceof MySqlPrimaryKey) {
                    MySqlPrimaryKey primaryKey = (MySqlPrimaryKey) c;
                    primaryKey.getColumns().forEach(p -> {
                        String key = MyUtils.sqlFiledReplace(p.getExpr().toString());
                        Optional<TransferJsonResult.Column> keyColumn = table.getColumnList().stream()
                                .filter(m -> m.getColumnName().equals(key))
                                .findFirst();

                        if (keyColumn.isPresent()) {
                            keyColumn.get().setColumnKey("是");
                        }
                    });
                }
            });
        });

        return result;
    }
}
