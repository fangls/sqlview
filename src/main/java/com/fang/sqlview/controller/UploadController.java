package com.fang.sqlview.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import com.fang.sqlview.common.Constant;
import com.fang.sqlview.domain.MDContent;
import com.fang.sqlview.domain.UploadJsonResult;
import com.fang.sqlview.service.DataBaseStructureService;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
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
    @Autowired
    private Engine engine;

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

            ByteArrayInputStream inputStream = IoUtil.toStream(content.getBytes());

//            POIFSFileSystem poifs = new POIFSFileSystem();
//            DirectoryEntry directory = poifs.getRoot();
//            directory.createDocument("WordDocument", inputStream);

            OutputStream outputStream = response.getOutputStream();
            response.reset();

            response.setHeader("Content-disposition", "attachment; filename=database.docx");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/x-download");

            //poifs.writeFilesystem(outputStream);

            outputStream.write(content.getBytes());
            inputStream.close();
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
}
