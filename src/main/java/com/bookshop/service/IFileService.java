package com.bookshop.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2017/5/18.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
