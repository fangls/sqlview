package com.fang.sqlview;

import cn.hutool.core.io.FileUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/23
 */
public class SQLUtilTest {

    public static void main(String[] args) {
        String sql = FileUtil.readUtf8String("/Users/fangliangsheng/Downloads/gh_marigold (5).sql");
        long start = System.currentTimeMillis();
        List<SQLStatement> result = SQLUtils.parseStatements(sql, "mysql");

        Map<String, SQLCreateTableStatement> createTableStatementMap = new HashMap<>();
        result.stream()
                .filter(a -> a instanceof SQLCreateTableStatement)
                .map(a -> (SQLCreateTableStatement) a)
                .forEach(a -> createTableStatementMap.put(SQLUtilTest.replace(a.getName().getSimpleName()), a));

        result.stream()
                .filter(a -> a instanceof SQLAlterTableStatement)
                .map(a -> (SQLAlterTableStatement) a)
                .filter(a -> createTableStatementMap.get(SQLUtilTest.replace(a.getTableName())) != null)
                .forEach(a -> createTableStatementMap.get(SQLUtilTest.replace(a.getTableName())).apply(a));

        System.out.println(result);

    }

    public static String replace(String name) {
        return name.replace("`", "");
    }
}
