package com.gjz.controller;

import com.gjz.Aspect.LogAnnotation;
import com.gjz.utils.QiniuUtils;
import com.gjz.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private QiniuUtils qiniuUtils;

    // 上传博客中图片或首图到七牛云cdn
//    @LogAnnotation(module = "uploadController", operation = "upload")
    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        boolean upload = qiniuUtils.upload(file, fileName);
        if (upload) {
            return Result.success(QiniuUtils.url + fileName);
        }
        return Result.fail(20001, "上传失败");
    }
}
