package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common/")
@Api(tags = "上传图片文件的controller")
@Slf4j
public class commonController {

    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("alioss上传图片文件")
    public Result<String> upload(MultipartFile file) {
        // 提取文件的后缀名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'),originalFilename.length());

        // UUID重新命名
        String new_file_name =  "intern/" + UUID.randomUUID().toString() + "." + suffix; //默认都存储在intern文件夹中

        //上传文件
        String uploade_file_path = null;
        try {
            uploade_file_path = aliOssUtil.upload(file.getBytes(), new_file_name);
        } catch (IOException e) {
            log.error("上传文件时出现异常");
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

        return Result.success(uploade_file_path);
    }
}
