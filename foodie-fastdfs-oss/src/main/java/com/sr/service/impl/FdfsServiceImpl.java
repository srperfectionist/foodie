package com.sr.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.sr.resource.FileResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.sr.service.IFdfsService;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author shirui
 * @date 2020/11/27
 */
@Service("iFdfsService")
public class FdfsServiceImpl implements IFdfsService {

    private FastFileStorageClient fastFileStorageClient;

    private FileResource fileResource;

    @Autowired
    public void setFastFileStorageClient(FastFileStorageClient fastFileStorageClient) {
        this.fastFileStorageClient = fastFileStorageClient;
    }

    @Autowired
    public void setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
    }

    @Override
    public String upload(MultipartFile file, String fileExtName) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        String path = StringUtils.join(fileResource.getHost(), storePath.getFullPath());
        return path;
    }

    @Override
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws IOException {
        // 构建ossClient
        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(),
                                                     fileResource.getAccessKeyId(),
                                                     fileResource.getAccessKeySecret());

        InputStream inputStream = file.getInputStream();

        String objectName = StringUtils.join(fileResource.getObjectName(), "/", userId, "/", userId, ".", fileExtName);

        ossClient.putObject(fileResource.getBucketName(), objectName, inputStream);
        ossClient.shutdown();

        String path = StringUtils.join(fileResource.getOssHost(), objectName);
        return path;
    }
}
