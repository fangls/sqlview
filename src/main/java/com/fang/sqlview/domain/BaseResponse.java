package com.fang.sqlview.domain;

import lombok.Data;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/26
 */
@Data
public class BaseResponse<T> {

    public BaseResponse(T data) {
        this.data = data;
    }

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

    private T data;

}
