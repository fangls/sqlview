package com.fang.sqlview.domain;

import cn.hutool.core.util.StrUtil;
import com.fang.sqlview.common.MysqlDateTypeToJavaObjectEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/24
 */
@Data
public class JavaEntityField {

    private String tableName;

    private String className;

    private String tableComment;

    private List<Field> fieldList = new ArrayList<>();

    @Data
    public static class Field{
        private String name;

        private String methodName;

        private String type;

        private String comment;

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
