package com.fang.sqlview.service;

import com.fang.sqlview.domain.TransferForm;
import com.fang.sqlview.domain.TransferJsonResult;
import com.fang.sqlview.service.produce.JavaProduceService;
import com.fang.sqlview.service.produce.WordProduceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
@Slf4j
@Service
public class DataBaseStructureService {

    @Autowired
    private WordProduceService wordProduceService;
    @Autowired
    private JavaProduceService javaProduceService;

    public TransferJsonResult transfer(TransferForm form){
        return SqlProcessFactory.transfer(form.getSql(), form.getDbType());
    }

    /**
     * word生成
     * @param form
     * @return
     */
    public File downWord(TransferForm form){
       return wordProduceService.generate(form);
    }

    public File downJava(TransferForm form){
        return javaProduceService.generate(form);
    }

    public String codePreview(List<String> codeStyle) {
        return javaProduceService.codePreview(codeStyle);
    }

}
