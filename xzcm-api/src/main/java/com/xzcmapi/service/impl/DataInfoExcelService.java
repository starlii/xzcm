package com.xzcmapi.service.impl;

import com.mongodb.gridfs.GridFSDBFile;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.Constant;
import com.xzcmapi.entity.*;
import com.xzcmapi.exception.DataException;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.FileMaterialMapper;
import com.xzcmapi.mapper.PlayerMapper;
import com.xzcmapi.model.DataImportRecord;
import com.xzcmapi.service.ActivityService;
import com.xzcmapi.service.PlayerService;
import com.xzcmapi.service.UploadFileCridFsService;
import com.xzcmapi.util.BeanUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Service("dataInfoExcelService")
public class DataInfoExcelService {

    private final Logger logger = LoggerFactory.getLogger(DataInfoExcelService.class);

    @Autowired
    private UploadFileCridFsService uploadFileCridFsService;

    @Autowired
    private ParseExcelService parseExcelService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private FileMaterialMapper fileMaterialMapper;
    @Autowired
    private PlayerMapper playerMapper;

    /**
     * 导入数据存放
     */
    private CopyOnWriteArrayList<DataInfoExcel> dataInfoExcelList = new CopyOnWriteArrayList();

    /**
     * 严重错误信息存放
     */
    private CopyOnWriteArrayList<RowError> forceErrorList = new CopyOnWriteArrayList();

    /**
     * 提醒错误信息存放
     */
    private CopyOnWriteArrayList<RowError> promptErrorList = new CopyOnWriteArrayList();


    public JSONObject importExcelData(DataImportRecord dataImportRecord, Long userId) throws Exception {
        JSONObject result = new JSONObject();
        String fileId = dataImportRecord.getFileId();
        Long activityId = dataImportRecord.getActivityId();
        if(fileId == null || activityId == null){
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR);
        }
        //获取上传的文件
        GridFSDBFile gridFSDBFile = uploadFileCridFsService.getFileContent(fileId);
        if(gridFSDBFile == null)
            throw new DataException("文件不存在！");

        Activity activity = activityService.findById(activityId);
        if(activity == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，活动不存在！");

        if (!Constant.EXCEL_TYPE_XLS.equals(gridFSDBFile.getContentType()) && !Constant.EXCEL_TYPE_XLSX.equals(gridFSDBFile.getContentType())) {
            throw new XzcmBaseRuntimeException(CodeMessage.FILETYPEERROR);
        }

        //获取sheet
        Sheet excelSheets = parseExcelService.getExcelSheets(gridFSDBFile);

        //通过模板配置解析Excel数据
        List<TemplateExcelInfo> templateExcelInfoList = null;

        dataInfoExcelList.clear();
        forceErrorList.clear();
        promptErrorList.clear();
        parseExcelService.parseSheet(excelSheets, 0, 0, DataInfoExcel.class, dataImportRecord, templateExcelInfoList, dataInfoExcelList, forceErrorList, promptErrorList);

        if(!forceErrorList.isEmpty()){
            result.put("forceErrorList",forceErrorList);
            return result;
        }

        Long lastestNum = playerMapper.getLastestNum(activityId);
        lastestNum = lastestNum == null?1:++lastestNum;
        List<Player> players = new ArrayList<>();
        Integer playerNum =  0;
        Example example = new Example(FileMaterial.class);
        example.createCriteria().andEqualTo("type",1)
                .andEqualTo("relatedId",null).andEqualTo("batchNum",dataImportRecord.getBatchNum());
        List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example);
        int index = 0;
        int fileSize = fileMaterials != null && !fileMaterials.isEmpty()?fileMaterials.size():0;
        for (DataInfoExcel dataInfoExcel : dataInfoExcelList) {
//            Example playerEx = new Example(Player.class);
//            playerEx.createCriteria().andLike("phone","%"+dataInfoExcel.getPhone()+"%").andEqualTo("activityId",activityId);
//            List<Player> dbplayers = playerService.selectByExample(playerEx);
//            if(dbplayers != null && !dbplayers.isEmpty())
//                continue;
            Player player = new Player();
            if(fileMaterials != null&&!fileMaterials.isEmpty()){
//                int index = (int) (Math.random() * fileMaterials.size());
                if(fileMaterials.size() >= dataInfoExcelList.size()){
                    player.setImage(fileMaterials.get(index++).getFileUrl());
                }else{
                    if(--fileSize >= 0){
                        player.setImage(fileMaterials.get(fileSize).getFileUrl());
                    }
                }
            }

            BeanUtil.copyPropertiesIgnoreNull(dataInfoExcel,player);
            player.setCreator(userId);
            player.setCreateTime(new Date());
            player.setSchedulStatus(1);
            player.setLockStatus(1);
            player.setVoteStatus(0);
            player.setApprovalStatus(0);
            player.setStar(1);
            player.setPlayerDesc(dataInfoExcel.getPlayerDesc());
            player.setActivityId(activityId);
            player.setNum(lastestNum++);
            players.add(player);
        }
        if(!players.isEmpty())
            playerNum = playerService.saveList(players);

        //update activity
        activity.setUpdateTime(new Date());
        activity.setUpdater(userId);
        activity.setActivityPlayers(activity.getActivityPlayers() == null ? 0:activity.getActivityPlayers()+playerNum);
        activityService.updateSelective(activity);

        //delete file
        uploadFileCridFsService.removeFile(dataImportRecord.getFileId());
        result.put("promptErrorList",promptErrorList);
        return result;
    }
}
