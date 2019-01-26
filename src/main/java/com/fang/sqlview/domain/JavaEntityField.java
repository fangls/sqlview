package com.fang.sqlview.domain;

import cn.hutool.core.util.StrUtil;
import com.fang.sqlview.common.MysqlDateTypeToJavaObjectEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：生成Java实体类的对象信息
 *
 * @author fangliangsheng
 * @date 2019/1/24
 */
@Data
public class JavaEntityField {
    /**
     * 表名称
     */
    private String tableName;

    /**
     * 类名称
     */
    private String className;

    /**
     * 表注释、类注释
     */
    private String tableComment;

    /**
     * 字段集合
     */
    private List<Field> fieldList = new ArrayList<>();

    @Data
    public static class Field{
        /**
         * 字段名称
         */
        private String name;

        /**
         * get、set方法名称
         */
        private String methodName;

        /**
         * 字段java数据类型
         */
        private String type;

        /**
         * 字段注释
         */
        private String comment;

        /**
         * 是否主键
         */
        private boolean key;

        public String getName() {
            return StrUtil.toCamelCase(name);
        }

        public String getType() {
            return MysqlDateTypeToJavaObjectEnum.getDataTypeEnum(type).getJavaObject();
        }

        public String getMethodName() {
            return StrUtil.upperFirst(StrUtil.toCamelCase(name));
        }
    }

    public String getClassName() {
        return StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
    }
}
