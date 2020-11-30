package com.sr.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author shirui
 * @date 2020/11/27
 */
public interface IFdfsService {

    String upload(MultipartFile file, String fileExtName) throws IOException;

    String uploadOSS(MultipartFile file, String userId, String fileExtName) throws IOException;
}
