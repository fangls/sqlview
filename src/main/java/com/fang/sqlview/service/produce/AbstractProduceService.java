package com.fang.sqlview.service.produce;

import com.fang.sqlview.domain.TransferForm;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * Description：抽象生产服务
 * 用于定义word生成、java代码生成相关的公共服务
 * @author fangliangsheng
 * @date 2019/1/25
 */
public abstract class AbstractProduceService {

    @Autowired
    protected VelocityEngine velocityEngine;
    @Value("${sql.path}")
    protected String sqlPath;

    public abstract File generate(TransferForm form);

}
