package com.fang.sqlview.service.produce;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.fang.sqlview.common.Constant;
import com.fang.sqlview.domain.JavaEntityField;
import com.fang.sqlview.domain.TransferForm;
import com.fang.sqlview.domain.TransferJsonResult;
import com.fang.sqlview.service.SqlProcessFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description：java代码生成
 *
 * @author fangliangsheng
 * @date 2019/1/25
 */
@Service
public class JavaProduceService extends AbstractProduceService {

    @Override
    public File generate(TransferForm form) {
        List<JavaEntityField> entityFields = buildToObject(form.getSql(), form.getDbType());

        return createZip(entityFields, form.getCodeStyle());
    }

    private List<JavaEntityField> buildToObject(String sql, String dbType) {
        TransferJsonResult jsonResult = SqlProcessFactory.transfer(sql, dbType);

        return jsonResult.getTableList().stream().map(table -> {
            JavaEntityField entityField = new JavaEntityField();
            entityField.setTableComment(table.getTableComment());
            entityField.setTableName(table.getTableName());

            table.getColumnList().forEach(column -> {
                JavaEntityField.Field field = new JavaEntityField.Field();
                field.setComment(column.getColumnComment());
                field.setName(column.getColumnName());
                field.setType(column.getColumnType());
                field.setKey(column.getColumnKey() != null ? true : false);

                entityField.getFieldList().add(field);
            });
            return entityField;
        }).collect(Collectors.toList());
    }

    private File createZip(List<JavaEntityField> entityFields, List<String> codeStyle) {
        String srcPath = sqlPath + RandomUtil.randomString(8) + "/";
        createJavaFile(entityFields, srcPath, codeStyle);

        return ZipUtil.zip(srcPath);
    }

    private void createJavaFile(List<JavaEntityField> entityFields, String path, List<String> codeStyle) {
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_JAVACODE, "UTF-8");

        Map<String, Object> map = codeStyleMap(codeStyle);
        entityFields.forEach(field -> {
            map.put("data", field);

            try {
                FileUtil.mkdir(path);
                try (FileWriter writer = new FileWriter(new File(path + field.getClassName() + ".java"))) {
                    template.merge(new VelocityContext(map), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Map<String, Object> codeStyleMap(List<String> codeStyle) {
        Map<String, Object> map = new HashMap<>();
        if (codeStyle.contains("lombok")) {
            map.put("lombok", true);
        }

        if (codeStyle.contains("jpa")) {
            map.put("jpa", true);
        }
        return map;
    }

    public String codePreview(List<String> codeStyle){
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_CODEPREVIEW,"UTF-8");
        Map<String, Object> map = codeStyleMap(codeStyle);

        StringWriter writer = new StringWriter();
        template.merge(new VelocityContext(map), writer);
        return writer.toString();
    }
}
