package com.fang.sqlview.repository.information;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
public interface ColumnsRepository extends CrudRepository<Columns, String> {

    List<Columns> findByTableSchemaAndTableName(String tableSchema, String tableName);

}
