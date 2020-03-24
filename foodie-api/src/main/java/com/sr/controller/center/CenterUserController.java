package com.sr.controller.center;

import com.google.common.base.Splitter;
import com.sr.pojo.Users;
import com.sr.pojo.bo.center.CenterUserB0;
import com.sr.pojo.vo.center.UsersVO;
import com.sr.resource.FileUpload;
import com.sr.service.center.ICenterUserService;
import com.sr.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author shirui
 * @date 2020/2/23
 */
@Api(value = "用户信息接口", tags = {"用户信心相关接口"})
@RestController
@RequestMapping("/userInfo")
public class CenterUserController {

    private ICenterUserService iCenterUserService;

    private FileUpload fileUpload;

    @Autowired
    public void setiCenterUserService(ICenterUserService iCenterUserService) {
        this.iCenterUserService = iCenterUserService;
    }

    @Autowired
    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public ServerResponse uploadFace(
            @ApiParam(name = "useId", value = "用户Id", required = true)
            @RequestParam("userId") String userId,
            @ApiParam(name = "file", value = "file", required = true)
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response){

        // 定义头像保存的地址
        String fileSpace = fileUpload.getImageUserFaceLocation();
        // 在路径上位每一个用户增加一个userId，用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        // 开始上传文件
        if (file != null){
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;

            try{
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

                    // 文件名称重命名，覆盖式上传。增量式：额外拼接当前时间
                    String newFileName = StringUtils.join("face-", userId, ".", suffix);

                    // 上传的头像最终保存的位置
                    String finalFacePath = StringUtils.join(fileSpace, uploadPathPrefix, File.separator, newFileName);

                    // 用于提供给web服务方位的地址
                    uploadPathPrefix = StringUtils.join(uploadPathPrefix, "/", newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null){
                        // 创建文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    // 文件输出保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("文件不能为空");
        }

        // 获取图片服务地址
        String imageServerUrl = fileUpload.getImageServerUrl();

        // 由于浏览器可能存在缓存的情况，所以在这里，需要加上时间戳来保证更新后的图片可以及时刷新
        String finalUserFaceUrl = StringUtils.join(imageServerUrl,
                                                    uploadPathPrefix,
                                                    "?t=",
                                                    DateUtil.getCurrentDateString(DateUtil.DATE_PATTER));

        // 更新用户头像到数据库
        Users userResult = iCenterUserService.updateUserFace(userId, finalUserFaceUrl);

        UsersVO usersVo = UsersVO.builder().id(userResult.getId())
                            .nickname(userResult.getNickname())
                            .realname(userResult.getRealname())
                            .face(userResult.getFace())
                            .username(userResult.getUsername())
                            .sex(userResult.getSex())
                            .build();

        CookieUtils.setCookie(request, response, "user", JsonUtil.objToString(usersVo), true);

        // TODO 后续要改，增加令牌token， 整合redis，分布式会话

        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户信心", httpMethod = "POST")
    @PostMapping("/update")
    public ServerResponse update(
            @ApiParam(name = "userId", value = "userId", required = true)
            @RequestParam("userId") String useId,
            @RequestBody @Valid CenterUserB0 centerUserBo,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response){

        // 判断BindingResult是否保存错误的验证信息，如果有，则直接返回
        if (result.hasErrors()){
            return ServerResponse.createByError(BindingResultUtil.getErrors(result));
        }

        Users userResult = iCenterUserService.updateUserInfo(useId, centerUserBo);

        UsersVO usersVo = UsersVO.builder().id(userResult.getId())
                .nickname(userResult.getNickname())
                .realname(userResult.getRealname())
                .face(userResult.getFace())
                .username(userResult.getUsername())
                .sex(userResult.getSex())
                .build();

        CookieUtils.setCookie(request, response, "user", JsonUtil.objToString(usersVo), true);

        return ServerResponse.createBySuccess();
    }
}
