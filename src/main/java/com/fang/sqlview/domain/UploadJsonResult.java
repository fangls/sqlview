package com.fang.sqlview.domain;

import com.fang.sqlview.common.MyUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/23
 */
@Data
public class UploadJsonResult {

    private String id;
    private List<Table> tableList = new ArrayList<>();

    @Data
    public static class Table{
        private String num;

        private String tableName;

        private String tableComment;

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
        private String columnName;
        private String columnComment;
        private String columnType;
        private String isNullable = "是";
        private String columnKey;

        public String getColumnName() {
            return MyUtils.sqlFiledReplace(columnName);
        }

        public String getColumnComment() {
            return MyUtils.sqlFiledReplace(columnComment);
        }
    }

}
