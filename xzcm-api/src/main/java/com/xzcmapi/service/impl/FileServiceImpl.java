package com.xzcmapi.service.impl;

import com.mongodb.gridfs.GridFSDBFile;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xzcmapi.common.Constant;
import com.xzcmapi.entity.Activity;
import com.xzcmapi.entity.FileMaterial;
import com.xzcmapi.entity.Player;
import com.xzcmapi.entity.UploadFile;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.FileMaterialMapper;
import com.xzcmapi.service.ActivityService;
import com.xzcmapi.service.FileService;
import com.xzcmapi.service.PlayerService;
import com.xzcmapi.service.UploadFileCridFsService;
import com.xzcmapi.util.FastDfsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Transactional
@Service
@Slf4j
public class FileServiceImpl implements FileService {


    // 上传文件地址
    @Value("${file.dir.upload}")
    private String uploadPath;

    // 导入文件地址
    @Value("${file.dir.import}")
    private String importPath;

    // 导出文件地址
    @Value("${file.dir.export}")
    private String exportPath;

    // 文件服务地址
    @Value("${file.url}")
    private String fileUrl;

    public static final String SUFIXX = "blizzard/";

    @Autowired
    private FileMaterialMapper fileMaterialMapper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UploadFileCridFsService uploadFileCridFsService;


    @Override
    public JSONObject fileupload(InputStream in, String realName) {
        String type = FilenameUtils.getExtension(realName);
        //使用 uuid 作为filename
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString();
        //生成文件的绝对路径
        String absTempPath = new File(uploadPath).getAbsolutePath();
        JSONObject obj = new JSONObject();
        if (in != null) {
            try {
                //以json形式返回url，在富文本中显示
                obj.put("error", 0);
                obj.put("fileName",fileName);
                obj.put("realName",realName);
                obj.put("format",type);
                obj.put("size",in.available());
                String fullName = fileName + (Objects.isNull(type) ? "" : ("." + type));
                obj.put("localUrl",absTempPath + File.separatorChar + fullName);
                File dest = new File(absTempPath + File.separatorChar + fullName);
                FileUtils.copyInputStreamToFile(in,dest);
                obj.put("outUrl",fileUrl + "/image/"+ fullName);
                return obj;
            } catch (Exception e) {
                obj.put("error", 1);
                obj.put("message", "图片文件上传出错或上传图片超过1M");
                return obj;
            }
        } else {
            obj.put("error", 1);
            obj.put("message", "上传图片文件为空");
            return obj;
        }
    }

    @Override
    public JSONObject fileupload(MultipartFile file, String fileType) {
        JSONObject jsonObject;
        jsonObject = upload(file,uploadPath);

        return jsonObject;
    }

    private JSONObject upload(MultipartFile file, String filePath){
        //使用 uuid 作为filename
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString();
        //生成文件的绝对路径
        String absTempPath = new File(filePath).getAbsolutePath();
        //上传文件的文件名
        String realName = file.getOriginalFilename();
        //上传文件的扩展名
        String format;
        if(realName.lastIndexOf(".") != -1){
            format = realName.substring(realName.lastIndexOf(".") + 1);
        }else {
            format = null;
        }
        JSONObject obj = new JSONObject();
        if (!file.isEmpty()) {
            try {
                //以json形式返回url，在富文本中显示
                obj.put("error", 0);
                obj.put("fileName",fileName);
                obj.put("realName",realName);
                obj.put("format",format);
                obj.put("size",file.getSize());
                String fullName = fileName + (Objects.isNull(format) ? "" : ("." + format));
                obj.put("localUrl",absTempPath + File.separatorChar + fullName);
                File dest = new File(absTempPath + File.separatorChar + fullName);
                file.transferTo(dest);
                obj.put("outUrl",fileUrl + "/image/"+ fullName);
                return obj;
            } catch (Exception e) {
                obj.put("error", 1);
                obj.put("message", "图片文件上传出错或上传图片超过1M");
                return obj;
            }
        } else {
            obj.put("error", 1);
            obj.put("message", "上传图片文件为空");
            return obj;
        }
    }

    @Override
    public void synchronizedMongo() {
        log.info("开始同步mongodb图片数据到服务器...");
        try {
            Long startTime = System.currentTimeMillis();
            Example example = new Example(FileMaterial.class);
            example.createCriteria().andIn("type",Arrays.asList(1,4));
            List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example);
            log.info("查询出需要同步的选手和活动图片数据数量为: {}",fileMaterials.size());
            Integer count = 0;
            for (FileMaterial fileMaterial : fileMaterials) {
                String fileUrl = fileMaterial.getFileUrl();
                if(fileUrl != null && !fileUrl.contains("/image")){
                    String id = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
                    UploadFile file = uploadFileCridFsService.getFileById(id);
                    if(file != null){
                        log.info("同步{}图片信息:",file.getRealName());
                        GridFSDBFile gridFSDBFile = uploadFileCridFsService.getFileContent(id);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        gridFSDBFile.writeTo(os);
                        log.info("mongodb中的文件: {}",file.toString());
                        JSONObject fileupload = fileupload(os, SUFIXX, file.getRealName(), file.getSize(), file.getType());
                        os.flush();
                        os.close();
                        String outUrl = fileupload.get("outUrl").toString();
                        log.info("同步之后的文件信息: {}",fileupload.toString());
                        if(outUrl != null){
                            fileMaterial.setFileUrl(outUrl);
                            fileMaterial.setMongoFileId(fileUrl);
                            int i = fileMaterialMapper.updateByPrimaryKeySelective(fileMaterial);
                            count += i;
                        }
                        os.close();
                    }
                }
            }

            log.info("同步活动表和选手表中图片素材 -- 开始");
            List<Activity> all = activityService.findAll();
            for (Activity activity : all) {
                if(activity != null && activity.getActivityImage() != null){
                    String fileUrl = activity.getActivityImage();
                    if(fileUrl != null && !fileUrl.contains("/image")){
                        String id = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
                        UploadFile file = uploadFileCridFsService.getFileById(id);
                        if(file != null){
                            log.info("同步{}图片信息:",file.getRealName());
                            GridFSDBFile gridFSDBFile = uploadFileCridFsService.getFileContent(id);
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            gridFSDBFile.writeTo(os);
                            log.info("mongodb中的文件: {}",file.toString());
                            JSONObject fileupload = fileupload(os, SUFIXX, file.getRealName(), file.getSize(), file.getType());
                            os.flush();
                            os.close();
                            String outUrl = fileupload.get("outUrl").toString();
                            log.info("同步之后的文件信息: {}",fileupload.toString());
                            if(outUrl != null){
                                activity.setActivityImage(outUrl);
                                int i = activityService.updateSelective(activity);
                                count += i;
                            }
                            os.close();
                        }
                    }
                }
            }
            List<Player> players = playerService.findAll();
            for (Player player : players) {
                if(player != null && player.getImage() != null){
                    String fileUrl = player.getImage();
                    if(fileUrl != null && !fileUrl.contains("/image")){
                        String id = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
                        UploadFile file = uploadFileCridFsService.getFileById(id);
                        if(file != null){
                            log.info("同步{}图片信息:",file.getRealName());
                            GridFSDBFile gridFSDBFile = uploadFileCridFsService.getFileContent(id);
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            gridFSDBFile.writeTo(os);
                            log.info("mongodb中的文件: {}",file.toString());
                            JSONObject fileupload = fileupload(os, SUFIXX, file.getRealName(), file.getSize(), file.getType());
                            os.flush();
                            os.close();
                            String outUrl = fileupload.get("outUrl").toString();
                            log.info("同步之后的文件信息: {}",fileupload.toString());
                            if(outUrl != null){
                                player.setImage(outUrl);
                                int i = playerService.updateSelective(player);
                                count += i;
                            }
                            os.close();
                        }
                    }
                }
            }
            log.info("同步活动表和选手表中图片素材 -- 结束");


            Long endTime = System.currentTimeMillis();
            log.info("同步数据完成，总共同步了{}张图片",count);
            log.info("同步数据耗时:{}",endTime - startTime);
        }catch (Exception e){
         log.error("同步数据出错，错误信息为: {}",e.getMessage());
         e.printStackTrace();
        }
    }

    public String getSufixx(){
        List<String> strings = Constant.directoryName();
        for (String string : strings) {
            String absTempPath = new File(uploadPath+File.separator+SUFIXX+string+"/").getAbsolutePath();
            File direct = new File(absTempPath);
            if(direct.isDirectory()){
                int length = Objects.requireNonNull(direct.listFiles()).length;
                if(length < 3000){
                    return string;
                }
            }
        }
        return "";
    }

    public JSONObject fileupload(ByteArrayOutputStream os,String direct, String realName,Long size ,String type) {
        JSONObject obj = new JSONObject();
        try{
            //使用 uuid 作为filename
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString();
            //生成文件的绝对路径
            String sufixx = getSufixx();
            String absTempPath = new File(uploadPath+File.separator + direct+ sufixx +"/").getAbsolutePath();
            if (os != null) {
                try {
                    //以json形式返回url，在富文本中显示
                    obj.put("error", 0);
                    obj.put("fileName",fileName);
                    obj.put("realName",realName);
                    obj.put("format",type);
                    obj.put("size",size);
                    String fullName = fileName + (Objects.isNull(type) ? "" : ("." + type));
                    obj.put("localUrl",absTempPath + File.separatorChar + fullName);
                    File dest = new File(absTempPath + File.separatorChar + fullName);
                    OutputStream fileOut = new FileOutputStream(dest);
                    os.writeTo(fileOut);
                    obj.put("outUrl",fileUrl + "/image/" + direct + sufixx + "/" + fullName);
                    log.info("服务器图片对象对象 {}",obj.toString());
                    return obj;
                } catch (Exception e) {
                    obj.put("error", 1);
                    obj.put("message", "图片文件上传出错或上传图片超过1M");
                    log.error("服务器存储出错，对象 {}",obj.toString());
                    log.error("错误信息为{}",e.getMessage());
                    return obj;
                }
            } else {
                obj.put("error", 1);
                obj.put("message", "上传图片文件为空");
                return obj;
            }
        }catch (Exception e){
            log.error("保存文件到服务器出错:{}",e.getMessage());
            e.printStackTrace();
            return obj;
        }
    }

    @Override
    public void remove(String path) {
        log.info("删除文件路径为：{}",path);
        if(path == null)
            throw new XzcmBaseRuntimeException("文件删除失败，文件路径为空！");
        try{
            String absPath = new File(uploadPath).getAbsolutePath();
            File dest = new File(absPath + File.separator + path);
            if(dest.exists()){
                if(!dest.isFile())
                    throw new XzcmBaseRuntimeException("目标文件不是一个文件");
                boolean delete = dest.delete();
            }else {
                throw new XzcmBaseRuntimeException("文件删除失败，文件不存在！");
            }
        }catch (Exception e){
            log.error("文件删除失败{}",e.getMessage());
        }
    }

    @Override
    public JSONObject processImages(){
        List<Player> players = playerService.findAll();
        int i = 0;
        for( Player p :players){
            String img = p.getImage();
            int lastSlash = img.lastIndexOf("/");
            img = img.substring(lastSlash+1);
            System.out.println(img);
            i++;
            if(i==10){
                break;
            }
            String absTempPath = new File(uploadPath).getAbsolutePath()+File.separator+img;
            log.info("WTF：{}", absTempPath);
            if(img.indexOf(".")>0){
                File f = new File(absTempPath);
                if(f.exists() && f.isFile()) {
                    String url = FastDfsUtil.upload("group1", absTempPath + File.separator + img);
                    p.setFastImage("/group1/" + url);
                    playerService.update(p);
                }
            }
        }
        return new JSONObject("hallo");
    }
}
