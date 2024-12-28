package com.mengxiao.sqlconvert.demos.controller;

import com.alibaba.fastjson.JSONObject;
import com.mengxiao.sqlconvert.demos.service.SqlConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SqlConvertController {
    @Autowired
    private SqlConvertService sqlConvertService;
    @PostMapping("/convert")
    public JSONObject convert(@RequestParam("file") MultipartFile file){
        String msg = sqlConvertService.convert(file);
        JSONObject result = new JSONObject();
        result.put("code",200);
        result.put("msg",msg);
        return result;
    }
}
