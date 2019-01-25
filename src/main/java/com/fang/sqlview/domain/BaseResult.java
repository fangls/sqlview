package com.fang.sqlview.domain;

import lombok.Data;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/25
 */
@Data
public class BaseResult {

    private boolean success = true;

    private String message;

}
