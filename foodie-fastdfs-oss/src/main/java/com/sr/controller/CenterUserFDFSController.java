package com.sr.controller;

import com.google.common.base.Splitter;
import com.sr.pojo.Users;
import com.sr.pojo.vo.UserVO;
import com.sr.resource.FileResource;
import com.sr.service.IFdfsService;
import com.sr.service.center.ICenterUserService;
import com.sr.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author shirui
 * @date 2020/2/23
 */
@RestController
@RequestMapping("/userinfo/fdfs")
@Slf4j
public class CenterUserFDFSController {

    private IFdfsService iFdfsService;

    private FileResource fileResource;

    private ICenterUserService iCenterUserService;

    private RedisOperator redisOperator;

    @Autowired
    public void setiFdfsService(IFdfsService iFdfsService) {
        this.iFdfsService = iFdfsService;
    }

    @Autowired
    public void setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
    }

    @Autowired
    public void setiCenterUserService(ICenterUserService iCenterUserService) {
        this.iCenterUserService = iCenterUserService;
    }

    @Autowired
    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @PostMapping("/uploadFace")
    public ServerResponse uploadFace(String userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 文件路径
        String path = "";
        // 开始上传文件
        if (file != null){
            // 获得文件上传的文件名称
            String fileName = file.getOriginalFilename();

            if (StringUtils.isNotBlank(fileName)){
                // 文件重命名 face.png ["face", "png"]
                List<String> fileNameList = Splitter.on(".").splitToList(fileName);

                // 获取文件的后缀名
                String suffix = fileNameList.get(fileNameList.size() - 1);

                if (!"png".equalsIgnoreCase(suffix) &&
                        !"jpg".equalsIgnoreCase(suffix) &&
                        !"jpeg".equalsIgnoreCase(suffix)){
                    return ServerResponse.createByErrorMessage("图片格式不正确");
                }

                path = iFdfsService.uploadOSS(file, userId, suffix);

                if (StringUtils.isNotBlank(path)){
                    Users users = iCenterUserService.updateUserFace(userId, path);
                    return getServerResponse(request, response, users);
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("文件不能为空");
        }

        return ServerResponse.createByErrorMessage("头像上传失败");
    }

    private ServerResponse getServerResponse(HttpServletRequest request, HttpServletResponse response, Users userResult) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set("redis_user_token:" + userResult.getId(), uniqueToken);

        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(userResult, userVo);
        userVo.setUserUniqueToken(uniqueToken);

        CookieUtils.setCookie(request, response, "user", JSONUtil.objToString(userVo), true);

        return ServerResponse.createBySuccess(userVo);
    }
}
