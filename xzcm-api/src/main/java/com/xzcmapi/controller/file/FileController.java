package com.xzcmapi.controller.file;

import com.google.common.collect.Lists;
import com.mchange.util.Base64Encoder;
import com.mongodb.gridfs.GridFSDBFile;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.FileMaterial;
import com.xzcmapi.entity.UploadFile;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.DeleteFileModel;
import com.xzcmapi.model.UnZipCaseFileRequest;
import com.xzcmapi.service.ActivityService;
import com.xzcmapi.service.FileService;
import com.xzcmapi.service.UploadFileCridFsService;
import com.xzcmapi.repository.UploadFileRepository;
import com.xzcmapi.util.HeaderUtil;
import com.xzcmapi.util.ImageUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/fileUploadController")
@Api(value = "", description = "文件上传")
public class FileController extends BaseController {

    @Autowired
    private UploadFileRepository uploadFileRepository;
    @Autowired
    private UploadFileCridFsService uploadFileCridFsService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private FileService fileService;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(FileController.class);


//    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/uploadFileGrid", method = RequestMethod.POST, headers = {"content-type=multipart/mixed", "content-type=multipart/form-data"}, consumes = {"multipart/form-data"})
    @ResponseBody
    @ApiOperation(value = "Grid方式上传文件", notes = "返回JSON data 为UploadFile 对象")
    public ResponseResult uploadFileGrid(HttpServletRequest request,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam("type") @ApiParam("文件类型 1--选手图片多张图片压缩成.zip格式的压缩文件上传 0--其他")Integer type
                                         ) throws Exception {
        try {
//            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            if (Objects.isNull(file)) {
                throw new RuntimeException("MultipartFile是空的");
            }
            UploadFile uploadFile = uploadFileCridFsService.uploadFile(file,type,0L);
            return new ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,uploadFile);
        } catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/uploadImage")
    @ApiOperation(value = "上传图片文件")
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile file,
                                 @RequestParam(name = "fileType",required = false) String fileType) {
        try {
//            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            JSONObject jsonObject = fileService.fileupload(file,fileType);
            return new ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,jsonObject);
        } catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE,INTERNAL_ERROR,null);
        }

    }


    @GetMapping("/processImages")
    @ApiOperation(value = "把图片文件转到FastDfs")
    public ResponseResult processImages() {
        JSONObject jo = fileService.processImages();
        return new ResponseResult(false, SUCCESS_SAVE_MESSAGE, SUCCESS_CODE, jo);
    }

//    @PostMapping("/unZipCaseFile")
//    @ResponseBody
//    @ApiOperation(value = "上传压缩文件，后台进行解压缩", notes = "返回的为文件记录对象")
//    public ResponseEntity<UploadFile> unZipCaseFile(@RequestBody UnZipCaseFileRequest request,
//                                                    @RequestHeader(value = "authorization") String authorization) throws Exception {
//        if (StringUtils.isBlank(request.getUploadFile())) {
//            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("文件是空的", "")).body(null);
//        }
//        if (StringUtils.isBlank(request.getBatchNum())) {
//            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("批次号是空的", "")).body(null);
//        }
//        UploadFile uploadFile = uploadFileRepository.findOne(request.getUploadFile());
//        return ResponseEntity.ok(uploadFile);
//    }

    @GetMapping("/getAllUploadFileByIdList")
    @ResponseBody
    @ApiOperation(value = "查询文件信息", notes = "查询文件信息")
    public ResponseResult<List<UploadFile>> getAllUploadFileByIds(@RequestParam(required = false) @ApiParam(value = "文件id集合") List<String> fileIds) throws Exception {
        try{
            List<UploadFile> uploadFiles = new ArrayList<>();
            if(fileIds != null){
                uploadFiles = Lists.newArrayList(uploadFileRepository.findAll(fileIds));
            }else {
                uploadFiles = Lists.newArrayList(uploadFileRepository.findAll());
            }
            return new ResponseResult(false
                    ,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,uploadFiles);
        }catch (Exception e){
            return new ResponseResult(true,
                    FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,e.getMessage());
        }
    }

    /**
     * 在线显示文件
     *
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    @ApiOperation(value = "在线显示文件", notes = "在线显示文件")
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) throws IOException {
        UploadFile file = uploadFileCridFsService.getFileById(id);
        if (file != null) {
            StringBuffer stringBuffer = new StringBuffer();
            GridFSDBFile gridFSDBFile = uploadFileCridFsService.getFileContent(id);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            gridFSDBFile.writeTo(os);
            boolean judgeImage = ImageUtil.judgeImage(file.getType());
            byte[] bytes = os.toByteArray();
            if(judgeImage){
                BASE64Encoder encoder = new BASE64Encoder();
                String result = encoder.encode(bytes);
                stringBuffer.append("data:image/")
                        .append(file.getType())
                        .append(";base64,")
                        .append(result);
                return ResponseEntity.ok().body(stringBuffer.toString());
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + new String(file.getRealName().getBytes("UTF-8"), "ISO-8859-1") + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/".concat(file.getType()))
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .header("Connection", "close")
                    .body(bytes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }
    }

    /**
     * 下载文件
     *
     * @param id
     * @return
     */
    @GetMapping("/file/{id}")
    @ResponseBody
    @ApiOperation(value = "下载文件", notes = "下载文件")
    public ResponseEntity<Object> downFile(@PathVariable String id) throws IOException {
        UploadFile file = uploadFileCridFsService.getFileById(id);
        if (file != null) {
            GridFSDBFile gridFSDBFile = uploadFileCridFsService.getFileContent(id);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            gridFSDBFile.writeTo(os);
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + file.getRealName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .header("Connection", "close")
                    .body(os.toByteArray());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not found");
        }
    }

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    @ApiOperation(value = "删除文件", notes = "删除文件")
    public ResponseResult<String> deleteFile(@PathVariable String id) {
        try {
            uploadFileCridFsService.removeFile(id);
            return new ResponseResult(false,
                    SUCCESS_DELETE_MESSAGE,SUCCESS_CODE,null);
        } catch (Exception e) {
            return new ResponseResult(true,
                    FAILURE_DELETE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/deleteAll")
    @ResponseBody
    @ApiOperation(value = "一键清除图片",notes = "一键清除图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult<String> deleteAll(HttpServletRequest request) {
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            List<FileMaterial> deleteModel = activityService.getDeleteModel(userId);
            for (FileMaterial fileMaterial : deleteModel) {
                uploadFileCridFsService.removeFile(fileMaterial.getFileUrl());
            }
            return new ResponseResult(false,
                    SUCCESS_DELETE_MESSAGE,SUCCESS_CODE,null);
        } catch (Exception e) {
            return new ResponseResult(true,
                    FAILURE_DELETE_MESSAGE,INTERNAL_ERROR,null);
        }
    }


//    @PostMapping("/synchronized")
//    @ResponseBody
//    @ApiOperation(value = "同步mongodb数据",notes = "同步mongodb数据")
//    public ResponseResult<String> synchronizedMongo(HttpServletRequest request) {
//        try {
//            fileService.synchronizedMongo();
//            return new ResponseResult(false,
//                    SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,null);
//        } catch (Exception e) {
//            return new ResponseResult(true,
//                    FAILURE_DELETE_MESSAGE,INTERNAL_ERROR,null);
//        }
//    }

}
