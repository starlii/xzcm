package com.xzcmapi.service;

import com.xiaoleilu.hutool.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService{

    JSONObject fileupload(MultipartFile file, String fileType);

    JSONObject fileupload(InputStream in, String name);

    void synchronizedMongo();

    void remove(String path);

    JSONObject processImages();
}
