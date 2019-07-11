package com.xzcmapi.service.impl;

import com.mongodb.gridfs.GridFSDBFile;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.entity.FileMaterial;
import com.xzcmapi.entity.UploadFile;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.FileMaterialMapper;
import com.xzcmapi.repository.UploadFileRepository;
import com.xzcmapi.service.FileService;
import com.xzcmapi.service.UploadFileCridFsService;
import com.xzcmapi.util.ImageUtil;
import com.xzcmapi.util.XzcmDateUtil;
import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;

/**
 * @Description: GridFS文件服务器
 */
@Service("uploadFileCridFsServiceImpl")
public class UploadFileCridFsServiceImpl implements UploadFileCridFsService {
    private final Logger logger = LoggerFactory.getLogger(UploadFileCridFsServiceImpl.class);

    @Value("${gridfs.path}")
    private String path;
    @Value("${gridfs.localPath}")
    private String localPath;
    @Value("${file.dir.upload}")
    private String uploadPath;

    @Autowired
    UploadFileRepository uploadFileRepository;


    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    private FileMaterialMapper fileMaterialMapper;

    @Autowired
    private FileService fileService;

    /**
     *
     *
     *
     * 获取外网地址
     */
    private String getResAccessUrl(String fid) {
        String fileUrl = "http://".concat(path).concat("/fileUploadController/view/").concat(fid);
        return fileUrl;
    }

    /**
     * 获取内网地址
     */
    private String getLocalResAccessUrl(String fid) {
        String fileUrl = "http://".concat(localPath).concat("/fileUploadController/view/").concat(fid);
        return fileUrl;
    }

    @Override
    public UploadFile uploadFile(MultipartFile file,Integer type,Long userId) throws Exception {
        String realName =file.getOriginalFilename();
        if (realName.endsWith(".do")) {
            realName = realName.replaceAll(".do", "");
        }
        String originalFileName = file.getOriginalFilename();
        if (originalFileName.endsWith(".do")) {
            originalFileName = originalFileName.replaceAll(".do", "");
        }

        String batchNum = String.valueOf(System.currentTimeMillis());
        if (type == 1) {
            String fileType = checkFileType(realName);
            List<UploadFile> uploadFiles;
            if(fileType.equalsIgnoreCase("zip")){
                uploadFiles = unZipFiles(file,realName);
            }else{
                uploadFiles = unRarFiles(file,realName);
            }
            List<FileMaterial> fileMaterials = new ArrayList<>();
            for (UploadFile uploadFile : uploadFiles) {
                FileMaterial fileMaterial = new FileMaterial();
                fileMaterial.setCreateTime(new Date());
                fileMaterial.setCreator(userId);
                fileMaterial.setType(1);
                fileMaterial.setBatchNum(batchNum);
                fileMaterial.setFileUrl(uploadFile.getUrl());
                fileMaterials.add(fileMaterial);
            }
            if(!fileMaterials.isEmpty())
                fileMaterialMapper.insertList(fileMaterials);
        }
        UploadFile uploadFile = new UploadFile();
        if(type != 1){
            uploadFile =  uploadFile(userId, file.getSize(), realName, FilenameUtils.getExtension(originalFileName));
            boolean isImage = ImageUtil.judgeImage(realName);
            if(isImage){
                JSONObject fileupload = fileService.fileupload(file, FilenameUtils.getExtension(originalFileName));
                String outUrl = fileupload.get("outUrl").toString();
                uploadFile.setUrl(outUrl);
//                InputStream in = ImageUtil.thumbnailImage(realName,file.getInputStream(),250,250,false);
//                gridFsTemplate.store(in, uploadFile.getId(), uploadFile.getType());
            }else {
                gridFsTemplate.store(new ByteArrayInputStream(file.getBytes()), uploadFile.getId(), uploadFile.getType());
            }
        }else{
            uploadFile =  uploadFile(userId, file.getSize(), realName, FilenameUtils.getExtension(originalFileName),batchNum);
            gridFsTemplate.store(new ByteArrayInputStream(file.getBytes()), uploadFile.getId(), uploadFile.getType());
        }
        return uploadFile;
    }

    @Override
    public UploadFile uploadFile(File file){
        UploadFile uploadFile = new UploadFile();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            uploadFile = uploadFile(0L, file.length(), file.getName(), FilenameUtils.getExtension(file.getName()));
            gridFsTemplate.store(inputStream, uploadFile.getId(), uploadFile.getType());
            inputStream.close();
        }catch (Exception e){
            logger.error("上传文件失败");
            e.getMessage();
        }
        return uploadFile;
    }

    private String checkFileType(String name) {
        // 判断是否是zip文件
        int index = name.lastIndexOf(".");
        String str = name.substring(index + 1);
        if (!"zip".equalsIgnoreCase(str) && !"rar".equalsIgnoreCase(str)) {
            throw new XzcmBaseRuntimeException(CodeMessage.FILETYPEERROR,"请选择.zip或者.rar格式文件！");
        }
        return str;
    }

    public List<UploadFile> unzipAndSave(MultipartFile multiFile, String realName) throws Exception {
        List<UploadFile> uploadFiles = new ArrayList<>();
        Map<String,String> tempFiles = new HashMap<>();
        long start = System.currentTimeMillis();
        File file = File.createTempFile(realName.replace(".zip",""),".zip");
        multiFile.transferTo(file);
        File tempFileDirect = null;
        File tempFile = null;
        try(ZipArchiveInputStream inputStream = getZipFile(file)){
            ZipArchiveEntry entry = null;
            while ((entry = inputStream.getNextZipEntry()) != null) {
                if (!entry.isDirectory()) {
                    OutputStream os = null;
                    try {
                        tempFileDirect = new File(uploadPath);
                        if(!tempFileDirect.exists()){
                            boolean mkdir = tempFileDirect.mkdirs();
                            if (mkdir){
                                tempFile = new File(uploadPath+File.separator+entry.getName());
                                if(!tempFile.exists()){
                                    boolean newFile = tempFile.createNewFile();
                                    if(newFile){
                                        os = new BufferedOutputStream(new FileOutputStream(new File(tempFileDirect.getPath(), entry.getName())));
                                        IOUtils.copy(inputStream, os);
                                        String filePath = tempFileDirect.getPath()+File.separator+entry.getName();
                                        tempFiles.put(entry.getName(),filePath);
                                    }
                                }

                            }
                        }
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
            long end = System.currentTimeMillis();
            logger.info("解压完成，耗时：" + (end - start) +" ms");
        }catch (Exception e){
            logger.error("[unzip] 解压zip文件出错", e);
        }finally {
            file.deleteOnExit();
        }
        logger.info("获取解压后的临时文件，上传文件服务器并删除临时文件");
        if(!tempFiles.isEmpty()){
            Set<String> fileNames = tempFiles.keySet();
            for (String fileName : fileNames) {
                File temp = new File(tempFiles.get(fileName));
                FileInputStream fileIn = new FileInputStream(file);
                UploadFile uploadFile = uploadFile(0L, temp.length(), fileName, FilenameUtils.getExtension(fileName));
                gridFsTemplate.store(fileIn, uploadFile.getId(), uploadFile.getType());
                uploadFiles.add(uploadFile);
                temp.deleteOnExit();
            }
        }
        return uploadFiles;
    }

    private static ZipArchiveInputStream getZipFile(File zipFile) throws Exception {
        return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)),"UTF-8");
    }

    private UploadFile uploadFile(Long userId, long fileSize, String fileName, String fileExtName) throws Exception {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setCreateTime(XzcmDateUtil.getNowDateTime());
        uploadFile.setRealName(fileName);
        uploadFile.setName(fileName);
        uploadFile.setType(fileExtName);
        uploadFile.setSize(fileSize);
        uploadFile = uploadFileRepository.save(uploadFile);
        uploadFile.setLocalUrl(getLocalResAccessUrl(uploadFile.getId()));
        uploadFile.setUrl(getResAccessUrl(uploadFile.getId()));
        uploadFile.setCreator(userId.toString());
        return uploadFileRepository.save(uploadFile);
    }

    private UploadFile uploadFile(Long userId, long fileSize, String fileName, String fileExtName,String batchNum) throws Exception {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setCreateTime(XzcmDateUtil.getNowDateTime());
        uploadFile.setRealName(fileName);
        uploadFile.setName(fileName);
        uploadFile.setType(fileExtName);
        uploadFile.setSize(fileSize);
        uploadFile.setBatchNum(batchNum);
        uploadFile = uploadFileRepository.save(uploadFile);
        uploadFile.setLocalUrl(getLocalResAccessUrl(uploadFile.getId()));
        uploadFile.setUrl(getResAccessUrl(uploadFile.getId()));
        uploadFile.setCreator(userId.toString());
        return uploadFileRepository.save(uploadFile);
    }


    @Override
    public void removeFile(String id) {
        uploadFileRepository.delete(id);
        Query query = Query.query(GridFsCriteria.whereFilename().is(id));
        gridFsTemplate.delete(query);
    }

    @Override
    public UploadFile getFileById(String id) {
        return uploadFileRepository.findOne(id);
    }

    @Override
    public GridFSDBFile getFileContent(String id) {
        Query query = Query.query(GridFsCriteria.whereFilename().is(id));
        return gridFsTemplate.findOne(query);
    }

    private List<UploadFile> unZipFiles(MultipartFile file,String realName) throws IOException {
        File zipFile = null;
        zipFile = File.createTempFile(realName.replace(".zip","")+System.currentTimeMillis(),".zip");
//        String substring = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
//        zipFile = new File(File.separator+"data"+File.separator+"apps"
//                +File.separator+"tmp"+substring+realName);
//        if(!zipFile.exists()){
//            boolean newFile = zipFile.createNewFile();
//        }
        FileUtils.copyInputStreamToFile(file.getInputStream(), zipFile);
//        file.transferTo(zipFile);
        List<UploadFile> uploadFiles = new ArrayList<>();
        ZipFile zip = null;
        InputStream in = null;
        try {
            zip = new ZipFile(zipFile);
            for (Enumeration<? extends ZipEntry> entries = zip.getEntries();entries.hasMoreElements();){
                ZipArchiveEntry entry = (ZipArchiveEntry)entries.nextElement();
                in = zip.getInputStream(entry);
                UploadFile uploadFile = uploadFile(0L, entry.getSize(), entry.getName(), FilenameUtils.getExtension(entry.getName()));
//                in = ImageUtil.thumbnailImage(entry.getName(),in,250,250,false);
//                gridFsTemplate.store(in, uploadFile.getId(), uploadFile.getType());
                JSONObject fileupload = fileService.fileupload(in, entry.getName());
                uploadFile.setUrl(fileupload.get("outUrl").toString());
                uploadFiles.add(uploadFile);
            }
        } catch (Exception e) {
            logger.error("文件解压出错,{}",e.getMessage());
            e.printStackTrace();
        }finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            zipFile.deleteOnExit();
        }
        return uploadFiles;
    }

    private List<UploadFile> unRarFiles(MultipartFile file,String realName) throws IOException {
        File rarFile = null;
        rarFile = File.createTempFile(realName.replace(".rar","")+System.currentTimeMillis(),".rar");
        FileUtils.copyInputStreamToFile(file.getInputStream(), rarFile);
        List<UploadFile> uploadFiles = new ArrayList<>();
        Archive a = null;
        InputStream in = null;
        try {
            a = new Archive(rarFile);
            FileHeader fileHeader = a.nextFileHeader();
            while (fileHeader != null){
                String fileNameString = fileHeader.getFileNameString();
                String type = fileNameString.substring(fileNameString.lastIndexOf(".") + 1);
                File rar = File.createTempFile(fileNameString.replace(type,""),"."+type);
                FileOutputStream out = new FileOutputStream(rar);
                a.extractFile(fileHeader, out);
                out.flush();
                out.close();
                in = new FileInputStream(rar);
                UploadFile uploadFile = uploadFile(0L, rar.length(), rar.getName(), FilenameUtils.getExtension(rar.getName()));
//                in = ImageUtil.thumbnailImage(rar.getName(),in,250,250,false);
//                gridFsTemplate.store(in, uploadFile.getId(), uploadFile.getType());
                JSONObject fileupload = fileService.fileupload(in, rar.getName());
                uploadFile.setUrl(fileupload.get("outUrl").toString());
                uploadFiles.add(uploadFile);
                fileHeader = a.nextFileHeader();
                rar.deleteOnExit();
            }
        } catch (Exception e) {
            logger.error("文件解压出错,{}",e.getMessage());
            e.printStackTrace();
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            rarFile.deleteOnExit();
        }
        return uploadFiles;
    }

    public static void main(String[] args) {
        try {
            File file = new File("C:\\Users\\Desktop\\Desktop.zip");
            ZipArchiveInputStream zipFile = getZipFile(file);
            ZipArchiveEntry entry = null;
            OutputStream os = null;
            while ((entry = zipFile.getNextZipEntry()) != null) {
                if(!entry.isDirectory()){
                    os = new BufferedOutputStream(new FileOutputStream(new File("C:\\Users\\Desktop\\", entry.getName())));
                    IOUtils.copy(zipFile, os);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
