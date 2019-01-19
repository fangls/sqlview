package com.fang.sqlview;

import cn.hutool.core.io.FileUtil;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
public class CommonTest {

    public static void main(String[] args) throws Exception{

        BufferedInputStream inputStream = FileUtil.getInputStream("/Users/fangliangsheng/Downloads/data.html");

        POIFSFileSystem poifs = new POIFSFileSystem();
        DirectoryEntry directory = poifs.getRoot();
        DocumentEntry documentEntry = directory.createDocument("WordDocument", inputStream);

        FileOutputStream ostream = new FileOutputStream("/Users/fangliangsheng/Downloads/data2.docx");
        poifs.writeFilesystem(ostream);
        inputStream.close();
        ostream.close();

    }


}
