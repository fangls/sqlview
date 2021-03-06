package com.fang.sqlview.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Description：返回结果基类
 *
 * @author fangliangsheng
 * @date 2019/1/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult {

    /**
     * 处理结果
     * 为false时message有值
     */
    private boolean success = true;

    /**
     * 错误信息
     * success为false时有值
     */
    private String message;

}
