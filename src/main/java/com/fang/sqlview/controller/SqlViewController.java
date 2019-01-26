package com.fang.sqlview.controller;

import cn.hutool.core.io.FileUtil;
import com.fang.sqlview.domain.BaseResponse;
import com.fang.sqlview.domain.BaseResult;
import com.fang.sqlview.domain.TransferForm;
import com.fang.sqlview.domain.TransferJsonResult;
import com.fang.sqlview.service.DataBaseStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
@Controller
public class SqlViewController {

    @Autowired
    private DataBaseStructureService dataBaseStructureService;

    @GetMapping("/index.html")
    public String index(Model mode){
        return "index";
    }

    @PostMapping("/api/transfer")
    @ResponseBody
    public TransferJsonResult transfer(@RequestBody @Valid TransferForm form){
        TransferJsonResult result = dataBaseStructureService.transfer(form);
        return result;
    }

    @PostMapping("/api/word")
    public void downWord(HttpServletResponse response, @RequestBody @Valid TransferForm form) throws Exception{

        File wordFile = dataBaseStructureService.downWord(form);

        OutputStream outputStream = response.getOutputStream();
        response.reset();

        response.setHeader("Content-disposition", "attachment; filename=database.docx");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/x-download");

        outputStream.write(FileUtil.readBytes(wordFile));
        outputStream.close();

    }

    @GetMapping("/api/code_preview")
    @ResponseBody
    public BaseResponse<String> codePreview(@RequestParam String codeStyle){
        String code = dataBaseStructureService.codePreview(Arrays.asList(codeStyle.split(",")));
        return new BaseResponse<String>(code);
    }

    @PostMapping("/api/code")
    public void downCode(HttpServletResponse response, @RequestBody @Valid TransferForm form) throws Exception{

        File zipFile = dataBaseStructureService.downJava(form);

        OutputStream outputStream = response.getOutputStream();
        response.reset();

        response.setHeader("Content-disposition", "attachment; filename=javacode.zip");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/x-download");

        outputStream.write(FileUtil.readBytes(zipFile));
        outputStream.close();

    }
}
