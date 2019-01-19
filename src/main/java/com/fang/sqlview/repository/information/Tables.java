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
import java.sql.Timestamp;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "TABLES")
@IdClass(Tables.PrimaryKey.class)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tables {

    @Id
    private String tableSchema;

    @Id
    private String tableName;
    private String tableCatalog;
    private String tableType;
    private String engine;
    private Long version;
    private String rowFormat;
    private Long tableRows;
    private Long avgRowLength;
    private Long dataLength;
    private Long maxDataLength;
    private Long indexLength;
    private Long dataFree;
    private Long autoIncrement;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp checkTime;
    private String tableCollation;
    private Long checksum;
    private String createOptions;
    private String tableComment;

    @Data
    public static class PrimaryKey implements Serializable {
        private String tableSchema;
        private String tableName;
    }
}
