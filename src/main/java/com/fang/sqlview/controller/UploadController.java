package com.fang.sqlview.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Engine;
import com.fang.sqlview.domain.TransferForm;
import com.fang.sqlview.domain.UploadJsonResult;
import com.fang.sqlview.service.DataBaseStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
@Controller
public class UploadController {

    @Autowired
    private DataBaseStructureService dataBaseStructureService;

    @GetMapping("/index.html")
    public String index(Model mode){
        return "index";
    }

    @PostMapping("/api/transfer")
    @ResponseBody
    public UploadJsonResult transfer(@RequestBody @Valid TransferForm form){
        UploadJsonResult result = dataBaseStructureService.transfer(form.getSql());
        return result;
    }

    @PostMapping("/api/word")
    public void downWord(HttpServletResponse response, @RequestBody @Valid TransferForm form){
        try {
            String content = dataBaseStructureService.createWord(form.getSql());

            OutputStream outputStream = response.getOutputStream();
            response.reset();

            response.setHeader("Content-disposition", "attachment; filename=database.docx");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/x-download");

            outputStream.write(content.getBytes());
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/api/code_preview")
    @ResponseBody
    public String codePreview(String codeStyle){
        return dataBaseStructureService.codePreview(Arrays.asList(codeStyle.split(",")));
    }

    @PostMapping("/api/code")
    public void downCode(HttpServletResponse response, @RequestBody @Valid TransferForm form){
        try {
            File zipFile = dataBaseStructureService.createJavaCodeZip(form);

            OutputStream outputStream = response.getOutputStream();
            response.reset();

            response.setHeader("Content-disposition", "attachment; filename=javacode.zip");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/x-download");

            outputStream.write(FileUtil.readBytes(zipFile));
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
