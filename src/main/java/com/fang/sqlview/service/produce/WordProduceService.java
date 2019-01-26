package com.fang.sqlview.service.produce;

import cn.hutool.core.util.RandomUtil;
import com.fang.sqlview.common.Constant;
import com.fang.sqlview.domain.TransferForm;
import com.fang.sqlview.domain.TransferJsonResult;
import com.fang.sqlview.service.SqlProcessFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description：word生成
 *
 * @author fangliangsheng
 * @date 2019/1/25
 */
@Service
public class WordProduceService extends AbstractProduceService{

    @Override
    public File generate(TransferForm form) {
        TransferJsonResult jsonResult = SqlProcessFactory.transfer(form.getSql(), form.getDbType());

        return createWordFile(jsonResult);
    }

    private File createWordFile(TransferJsonResult jsonResult) {
        Template template = velocityEngine.getTemplate(Constant.TEMPLATE_WORD, "UTF-8");

        Map<String, Object> map = new HashMap<>();
        map.put("jsonResult", jsonResult);

        String wordPath = sqlPath + RandomUtil.randomString(8) + ".docx";
        File wordFile = new File(wordPath);
        try {

            try (FileWriter writer = new FileWriter(wordFile)){
                template.merge(new VelocityContext(map), writer);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return wordFile;
    }
}
