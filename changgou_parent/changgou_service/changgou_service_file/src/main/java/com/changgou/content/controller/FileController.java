package com.changgou.content.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**接收用户上传文件
 * @author Alan
 * @version 1.0
 * @date 2019/11/12 21:26
 */
@RestController
@CrossOrigin//解决js跨域访问问题
public class FileController {
    @RequestMapping("upload")
    public Result upload(MultipartFile file) throws IOException {
        //包装文件上传对象
        FastDFSFile dfsFile=new FastDFSFile(
                file.getOriginalFilename(),//获取原文件名
                file.getBytes(),
                //此方法用于获取文件的后缀名getFilenameExtension(),不包含“.”例如 ".jpg"==>后缀名为jpg
                StringUtils.getFilenameExtension(file.getOriginalFilename())
        );
        //调用fastDFS上传文件
        String[] upload= FastDFSClient.upload(dfsFile);
        //返回上传结果
       // String url="http://192.168.211.132:8080/"+upload[0]+"/"+upload[1];
        //调用工具类获取url
        String url=FastDFSClient.getTrackerUrl()+upload[0]+"/"+upload[1];
        return new Result(true, StatusCode.OK,url);
    }
}
