package com.fang.sqlview.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Engine;
import com.fang.sqlview.domain.UploadJsonResult;
import com.fang.sqlview.service.DataBaseStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

    @PostMapping("/api/upload")
    @ResponseBody
    public UploadJsonResult upload(MultipartFile file){
        String dataBaseName = RandomUtil.randomString(8);

        Map<String, String> map = new HashMap<>();
        map.put("oid", dataBaseName);

        try {
            UploadJsonResult result = dataBaseStructureService.process(dataBaseName, file.getInputStream());
            result.setId(dataBaseName);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PostMapping("/api/word")
    public void downWord(HttpServletResponse response, String id){
        try {
            String content = dataBaseStructureService.createWord(id);

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
        return dataBaseStructureService.codePreview(codeStyle);
    }

    @PostMapping("/api/code")
    public void downCode(HttpServletResponse response, String id, String codeStyle){
        try {
            File zipFile = dataBaseStructureService.createJavaCodeZip(id, codeStyle);

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
