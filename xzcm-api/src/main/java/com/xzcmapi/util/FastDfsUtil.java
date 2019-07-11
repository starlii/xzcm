package com.xzcmapi.util;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;


public  class  FastDfsUtil {

     
    public String local_filename = "/Users/liuyuqi/Downloads/webwxgetmsgimg2.jpeg";
    public static void main(String[] args) throws UnknownHostException {
        FastDfsUtil dfs = new FastDfsUtil();
        String s = "test.png";
        System.out.println(s.indexOf("."));
//        dfs.testUpload();
    }

    public static String upload(String group, String path) {
        String result = null;
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
            System.out.println("ClientGlobal.configInfo(): " + ClientGlobal.configInfo());
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = null;
            storageServer = trackerClient.getStoreStorage(trackerServer,group==null?"group1": group);
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);

            String fileIds[] = storageClient.upload_file(path, "jpg", null);

            System.out.println(fileIds.length);
            System.out.println("组名：" + fileIds[0]);
            System.out.println("路径: " + fileIds[1]);
            if(fileIds.length>1){
                result = fileIds[1];
            }

        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } catch (MyException e) { 
            e.printStackTrace(); 
        }
        return result;
    }
 

    public void download() {
        try {

            ClientGlobal.initByProperties("fastdfs-client.properties");
 
            TrackerClient tracker = new TrackerClient(); 
            TrackerServer trackerServer = tracker.getConnection(); 
            StorageServer storageServer = null;
 
            StorageClient storageClient = new StorageClient(trackerServer, storageServer); 
            byte[] b = storageClient.download_file("group1", "M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"); 
            System.out.println(b); 
            IOUtils.write(b, new FileOutputStream("D:/"+UUID.randomUUID().toString()+".conf"));
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    }
     

    public void testGetFileInfo(){ 
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
 
            TrackerClient tracker = new TrackerClient(); 
            TrackerServer trackerServer = tracker.getConnection(); 
            StorageServer storageServer = null;
 
            StorageClient storageClient = new StorageClient(trackerServer, storageServer); 
            FileInfo fi = storageClient.get_file_info("group1", "M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"); 
            System.out.println(fi.getSourceIpAddr()); 
            System.out.println(fi.getFileSize()); 
            System.out.println(fi.getCreateTimestamp()); 
            System.out.println(fi.getCrc32()); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
     

    public void testGetFileMate(){ 
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
 
            TrackerClient tracker = new TrackerClient(); 
            TrackerServer trackerServer = tracker.getConnection(); 
            StorageServer storageServer = null;
 
            StorageClient storageClient = new StorageClient(trackerServer, 
                    storageServer); 
            NameValuePair nvps [] = storageClient.get_metadata("group1", "M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"); 
            for(NameValuePair nvp : nvps){ 
                System.out.println(nvp.getName() + ":" + nvp.getValue()); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
     

    public void testDelete(){ 
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
 
            TrackerClient tracker = new TrackerClient(); 
            TrackerServer trackerServer = tracker.getConnection(); 
            StorageServer storageServer = null;
 
            StorageClient storageClient = new StorageClient(trackerServer, 
                    storageServer); 
            int i = storageClient.delete_file("group1", "M00/00/00/wKgRcFV_08OAK_KCAAAA5fm_sy874.conf"); 
            System.out.println( i==0 ? "删除成功" : "删除失败:"+i); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    }
}