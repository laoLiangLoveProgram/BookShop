package com.bookshop.service.impl;

import com.bookshop.service.IFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * Created by Administrator on 2017/5/18.
 */
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg-->
        String fileExtensionName = "";
        String uploadFileName = UUID.randomUUID().toString();
        if(StringUtils.contains(fileName, ".")){
            fileExtensionName = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, "."));
            uploadFileName = new StringBuilder(uploadFileName).append(fileExtensionName).toString();
        }

        logger.info("开始上传文件,上传文件的文件名:{}, 上传的路径:{}, 新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }


        return null;
    }

    public static void main(String[] args) {

        String str = "abc.jpg";
        System.out.println(StringUtils.substring(str, StringUtils.lastIndexOf(str, ".")));
        str = "abc.";
        System.out.println(StringUtils.substring(str, StringUtils.lastIndexOf(str, ".")));
        str = "abc";
        System.out.println(StringUtils.substring(str, StringUtils.lastIndexOf(str, ".")));

    }
}
