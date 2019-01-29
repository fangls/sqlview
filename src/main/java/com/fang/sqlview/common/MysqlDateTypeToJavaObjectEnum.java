package com.fang.sqlview.common;

/**
 * Description：mysql数据类型与java对象类型的映射
 *
 * @author fangliangsheng
 * @date 2019/1/24
 */
public enum  MysqlDateTypeToJavaObjectEnum {

    VARCHAR("VARCHAR","String"),
    CHAR("CHAR","String"),
    BLOB("BLOB","byte[]"),
    TINYBLOB("TINYBLOB","byte[]"),
    MEDIUMBLOB("MEDIUMBLOB","byte[]"),
    TEXT("TEXT","String"),

    INTEGER("INTEGER","Integer"),
    INT("INT","Integer"),
    TINYINT("TINYINT","Boolean"),
    SMALLINT("SMALLINT","Integer"),
    MEDIUMINT("MEDIUMINT","Integer"),

    BIT("BIT","Boolean"),
    BIGINT("BIGINT","BigInteger"),
    FLOAT("FLOAT","Float"),
    DOUBLE("DOUBLE","Double"),
    DECIMAL("DECIMAL","BigDecimal"),

    DATE("DATE","Date"),
    TIME("TIME","Time"),
    DATETIME("DATETIME","Date"),
    TIMESTAMP("TIMESTAMP","Timestamp"),
    YEAR("YEAR","YEAR"),
    ;

    private String mysqlType;

    private String javaObject;

    MysqlDateTypeToJavaObjectEnum(String mysqlType, String javaObject) {
        this.mysqlType = mysqlType;
        this.javaObject = javaObject;
    }

    public static MysqlDateTypeToJavaObjectEnum getDataTypeEnum(String code) {
        for (MysqlDateTypeToJavaObjectEnum pte : MysqlDateTypeToJavaObjectEnum.values()) {
            if (code.toUpperCase().startsWith(pte.getMysqlType())) {
                return pte;
            }
        }
        return MysqlDateTypeToJavaObjectEnum.VARCHAR;
    }

    public String getMysqlType() {
        return mysqlType;
    }

    public String getJavaObject() {
        return javaObject;
    }
}
