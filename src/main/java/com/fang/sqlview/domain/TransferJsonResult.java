package com.fang.sqlview.domain;

import com.fang.sqlview.common.MyUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：sql转换json对象
 *
 * @author fangliangsheng
 * @date 2019/1/23
 */
@Data
public class TransferJsonResult extends BaseResult {

    private String id;
    private List<Table> tableList = new ArrayList<>();

    @Data
    public static class Table{
        /**
         * 表名称
         */
        private String tableName;

        /**
         * 表注释
         */
        private String tableComment;

        /**
         * 列集合
         */
        private List<Column> columnList = new ArrayList<>();

        public String getTableName() {
            return MyUtils.sqlFiledReplace(tableName);
        }

        public String getTableComment() {
            return MyUtils.sqlFiledReplace(tableComment);
        }
    }

    @Data
    public static class Column{
        /**
         * 列名称
         */
        private String columnName;
        /**
         * 列注释
         */
        private String columnComment;
        /**
         * 数据类型
         */
        private String columnType;
        /**
         * 是否为空
         */
        private String isNullable = "是";
        /**
         * 是否主键
         */
        private String columnKey;

        public String getColumnName() {
            return MyUtils.sqlFiledReplace(columnName);
        }

        public String getColumnComment() {
            return MyUtils.sqlFiledReplace(columnComment);
        }
    }

}
