package com.fang.sqlview.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/25
 */
@Data
public class TransferForm {

    @NotEmpty(message = "sql不能为空")
    private String sql;

    private List<String> codeStyle;

}
