package com.fang.sqlview.service;

import cn.hutool.core.io.IoUtil;
import com.fang.sqlview.common.Constant;
import com.fang.sqlview.repository.information.Columns;
import com.fang.sqlview.repository.information.ColumnsRepository;
import com.fang.sqlview.repository.information.Tables;
import com.fang.sqlview.repository.information.TablesRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * 上传处理流程
     * 1、执行脚本到数据库
     * 2、读取数据机构
     * 3、生成md格式
     * @param dataBaseName
     * @param inputStream
     * @return
     */
    public String process(String dataBaseName, InputStream inputStream) {
        try {
            createDataBase(dataBaseName, inputStream);

            Map<Tables, List<Columns>> dataBaseMap = readDataStructure(dataBaseName);

            return renderMarkdown(dataBaseMap);
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

    public Map<Tables, List<Columns>> readDataStructure(String dataBaseName) {
        //表汇总
        Map<Tables, List<Columns>> dataBaseMap = new LinkedHashMap<>();

        List<Tables> tables = tablesRepository.findByTableSchema(dataBaseName);
        tables.forEach(table->{
            //列
            List<Columns> columns = columnsRepository.findByTableSchemaAndTableNameOrderByOrdinalPosition(dataBaseName, table.getTableName());
            dataBaseMap.put(table, columns);
        });

        return dataBaseMap;
    }

    private void dropSchema(String dataBaseName) {
        jdbcTemplate.execute("DROP SCHEMA " + dataBaseName + ";");
    }

    /**
     * 使用velocity生成markdown格式内容
     * @param dataBaseMap
     * @return
     */
    public String renderMarkdown(Map<Tables, List<Columns>> dataBaseMap){
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_MARKDOWN,"UTF-8");

        Map<String, Object> map = new HashMap<>();
        map.put("dataBaseMap", dataBaseMap);

        StringWriter writer = new StringWriter();
        template.merge(new VelocityContext(map), writer);
        return writer.toString();
    }

    public String codePreview(String codeStyle){
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_CODEPREVIEW,"UTF-8");
        Map<String, Object> map = new HashMap<>();
        if (codeStyle.contains("lombok")){
            map.put("lombok", true);
        }

        if (codeStyle.contains("jpa")){
            map.put("jpa", true);
        }

        StringWriter writer = new StringWriter();
        template.merge(new VelocityContext(map), writer);
        return writer.toString();
    }
}
