package com.fang.sqlview.repository.information;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "COLUMNS")
@IdClass(Columns.PrimaryKey.class)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Columns {

    @Id
    private String tableSchema;
    @Id
    private String tableName;
    @Id
    private String columnName;
    private String tableCatalog;
    private Long ordinalPosition;
    private String columnDefault;
    private String isNullable;
    private String dataType;
    private Long characterMaximumLength;
    private Long characterOctetLength;
    private Long numericPrecision;
    private Long numericScale;
    private Long datetimePrecision;
    private String characterSetName;
    private String collationName;
    private String columnType;
    private String columnKey;
    private String extra;
    private String privileges;
    private String columnComment;
    private String generationExpression;
    private Long srsId;

    @Data
    public static class PrimaryKey implements Serializable {
        private String tableSchema;
        private String tableName;
        private String columnName;
    }

    public String getIsNullable() {
        return this.isNullable.equals("YES") ? "是" : "否";
    }

    public String getColumnKey() {
        return this.columnKey.equals("PRI") ? "是" : "";
    }

    public String getColumnType() {
        return this.columnType = this.columnType.replace("unsigned","");
    }

    public String getColumnComment() {
        return columnComment!=null? columnComment.replaceAll("\n","<br/>") : "";
    }
}
