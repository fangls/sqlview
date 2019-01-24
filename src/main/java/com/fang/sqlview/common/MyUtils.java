package com.fang.sqlview.common;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/24
 */
public class MyUtils {

    public static String sqlFiledReplace(String str){
        if (str == null){
            return "";
        }
        return str.replace("`","")
                .replace("\"","")
                .replace("'","");
    }

}
