package com.bookshop.service.impl;

import com.bookshop.service.IFileService;
import com.bookshop.util.FTPUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/5/18.
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {

//    private Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg-->
        String fileExtensionName = "";
        String uploadFileName = UUID.randomUUID().toString();
        if (StringUtils.contains(fileName, ".")) {
            fileExtensionName = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, ".") + 1);
            uploadFileName = new StringBuilder(uploadFileName).append(".").append(fileExtensionName).toString();
        }

        log.info("开始上传文件,上传文件的文件名:{}, 上传的路径:{}, 新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            //上传文件成功
            //将targetFile上传到我们的FTP服务器
            boolean isSuccess = FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //上传完毕之后,删除upload下面的文件
            targetFile.delete();
            if (!isSuccess) {
                return null;
            }
        } catch (IOException e) {
            log.error("上传文件异常", e);
            return null;
        }

        return targetFile.getName();
    }
}
