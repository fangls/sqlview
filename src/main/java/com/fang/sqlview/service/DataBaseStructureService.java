package com.fang.sqlview.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.fang.sqlview.repository.information.Columns;
import com.fang.sqlview.repository.information.ColumnsRepository;
import com.fang.sqlview.repository.information.Tables;
import com.fang.sqlview.repository.information.TablesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

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
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TablesRepository tablesRepository;
    @Autowired
    private ColumnsRepository columnsRepository;

    private static final String TABLE_HEAD = "|序号|表名|描述|";
    private static final String TABLE_COL = "|--|--|--|";
    private static final String COLUMN_HEAD = "|序号|字段|描述|类型|可空|主键|";
    private static final String COLUMN_COL = "|--|--|--|--|--|--|";

    public String process(String dataBaseName, InputStream inputStream) {
        try {
            createDataBase(dataBaseName, inputStream);

            return readDataStructure(dataBaseName);
        } catch (Exception e) {
            e.printStackTrace();
            dropSchema(dataBaseName);
        } finally {
            dropSchema(dataBaseName);
        }

        return "";
    }

    /**
     * 创建随机名的数据库，并执行上传的sql
     *
     * @param dataBaseName 8位随机数据库名称
     * @param inputStream  上传的文件
     */
    public void createDataBase(String dataBaseName, InputStream inputStream) {
        String userSql = IoUtil.read(inputStream).toString();

        try {
            StringBuilder sql = new StringBuilder();

            sql.append("CREATE SCHEMA " + dataBaseName + ";");
            sql.append("USE " + dataBaseName + ";");
            sql.append(userSql);

            Resource scripts = new ByteArrayResource(sql.toString().getBytes());
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(scripts);
            populator.setSqlScriptEncoding("UTF-8");
            populator.setIgnoreFailedDrops(true);

            DatabasePopulatorUtils.execute(populator, jdbcTemplate.getDataSource());
        } catch (ScriptStatementFailedException e) {
            e.printStackTrace();

        }
    }

    public String readDataStructure(String dataBaseName) {
        //表汇总
        StringBuilder tableString = new StringBuilder("## 数据库表\r");
        StringBuilder columnString = new StringBuilder("");

        tableString.append(TABLE_HEAD).append("\r");
        tableString.append(TABLE_COL).append("\r");

        List<Tables> tables = tablesRepository.findByTableSchema(dataBaseName);
        for (int i = 0; i < tables.size(); i++) {
            Tables table = tables.get(i);
            tableString.append(formatTable(i, table)).append("\r");

            //数据库表
            columnString.append(StrUtil.format("## {}. {} {}",i+1, table.getTableName(), table.getTableComment())).append("\r");
            columnString.append(COLUMN_HEAD).append("\r");
            columnString.append(COLUMN_COL).append("\r");
            List<Columns> columns = columnsRepository.findByTableSchemaAndTableName(dataBaseName, table.getTableName());
            for (int j = 0; j < columns.size(); j++) {
                Columns column = columns.get(j);
                columnString.append(formatColumn(j, column)).append("\r");
            }
        }

        return tableString.append(columnString).toString();
    }

    private String formatTable(int i, Tables table){
        return StrUtil.format("|{}|{}|{}|",
                i+1,
                table.getTableName(),
                table.getTableComment());
    }

    private String formatColumn(int j, Columns column){
        return StrUtil.format("|{}|{}|{}|{}|{}|{}|",
                j+1,
                column.getColumnName(),
                column.getColumnComment(),
                column.getColumnType(),
                column.getIsNullable().equals("YES") ? "是" : "否",
                column.getColumnKey().equals("PRI") ? "是" : "");
    }

    private void dropSchema(String dataBaseName) {
        jdbcTemplate.execute("DROP SCHEMA " + dataBaseName + ";");
    }
}
