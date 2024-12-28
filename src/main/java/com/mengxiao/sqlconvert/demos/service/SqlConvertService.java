package com.mengxiao.sqlconvert.demos.service;

import org.springframework.web.multipart.MultipartFile;

public interface SqlConvertService {
    String convert(MultipartFile file);
}
