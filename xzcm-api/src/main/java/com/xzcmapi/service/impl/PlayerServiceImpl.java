package com.xzcmapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.ResponseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import com.xzcmapi.entity.*;
import com.xzcmapi.exception.PermissionException;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.*;
import com.xzcmapi.model.*;
import com.xzcmapi.service.*;
import com.xzcmapi.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
@Slf4j
public class PlayerServiceImpl extends BaseServiceImpl<Player> implements PlayerService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UploadFileCridFsService uploadFileCridFsService;

    @Autowired
    private UploadFileCridFsService uploadFileService;
    @Autowired
    private FileMaterialMapper fileMaterialMapper;
    @Autowired
    private VoteLogMapper voteLogMapper;
    @Autowired
    private GiftLogMapper giftLogMapper;
    @Autowired
    private RuleSettingMapper ruleSettingMapper;
    @Resource
    private ScheduleJobService scheduleJobService;
    @Autowired
    private SystemParamService systemParamService;
    @Autowired
    private ApplySettingMapper applySettingMapper;
    @Autowired
    private GiftSettingMapper giftSettingMapper;
    @Autowired
    private WeChatPaYService weChatPaYService;
    @Autowired
    private UserService userService;
    @Autowired
    private InterfaceSettingMapper interfaceSettingMapper;
    @Autowired
    private ActivitySettingMapper activitySettingMapper;

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${xzcm.url}")
    private String serverUrl;

    @Override
    public PageInfo<List<PlayerModel>> get(PlayerSearchModel searchModel, Long userId,Boolean isAll) {
        String search = searchModel.getSearch();
        String name = null;
        Long playerId = null;
        Long num = null;
        if(search != null){
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(search);
            if(isNum.matches()){
//                playerId = Long.valueOf(search);
                num = Long.valueOf(search);
                name = search;
            }else {
                name = search;
            }
        }
//        boolean isManager = roleService.checkUserManagerPermission(userId);
        Long activityId = searchModel.getActivityId();
        if(activityId == null && !isAll)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"activityId must not be null.");
//        if(!isManager)
//            throw new PermissionException();
        Integer page = searchModel.getPage();
        if(page == null)
            page=1;
        Integer size = searchModel.getSize();
        if(size == null)
            size = 10;
        String sort = searchModel.getSort();

        List<PlayerModel> playerModels = new ArrayList<>();
        if(isAll){
            User user = userService.findById(userId);
            PageHelper.startPage(page,size,sort);
            playerModels = playerMapper.getAll(name, playerId,searchModel.getApprovalStatus(),user.getRoleValue(),num);
        }else {
            PageHelper.startPage(page,size,sort);
            playerModels = playerMapper.get(name, playerId,searchModel.getApprovalStatus(),activityId,num,sort);
        }

        for (PlayerModel playerModel : playerModels) {
            Example example = new Example(FileMaterial.class);
            example.createCriteria().andEqualTo("type",1).andEqualTo("relatedId",playerModel.getPlayerId());
            List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example);
            List<String> images = new ArrayList<>();
            fileMaterials.forEach(it -> images.add(it.getFileUrl()));
            playerModel.setImages(images);
        }
        return new PageInfo(playerModels);
    }

    @Override
    public Player add(SavePlayerModel playerModel, Long userId) {
        Integer deviceType = playerModel.getDeviceType();
//        if(deviceType == null)
//            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"deviceType must be not null.");
        Long activityId = playerModel.getActivityId();
        String name = playerModel.getName();
        String phone = playerModel.getPhone();
        String declaration = playerModel.getDeclaration();
        String image = playerModel.getImage();
        List<String> images = playerModel.getImages();
        if(name == null || phone == null || declaration == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR);

//        Example example = new Example(Player.class);
//        example.createCriteria().andLike("phone","%"+phone+"%").andEqualTo("activityId",activityId);
//        List<Player> players = selectByExample(example);
//        if(players != null && !players.isEmpty())
//            throw new XzcmBaseRuntimeException(CodeMessage.REPEATREGISTRATION);

        Long lastestNum = playerMapper.getLastestNum(activityId);
        Player player = new Player();
        BeanUtil.copyPropertiesIgnoreNull(playerModel,player);
        player.setApprovalStatus(player.getApprovalStatus() == null?(deviceType == null?1:0):player.getApprovalStatus());
        player.setLockStatus(1);
        player.setVoteStatus(0);
        player.setManualVotes(playerModel.getVotes());
        player.setCreateTime(new Date());
        player.setCreator(userId);
        player.setStar(1);
        player.setSchedulStatus(1);
        player.setNum(lastestNum == null?1:(lastestNum+1));
        player.setImage(images!=null && !images.isEmpty()?images.get(0):null);

        //update activity player number
        Activity activity = activityService.findById(activityId);

        Integer approvalStatus = activity.getApprovalStatus();
        if(approvalStatus == 1 && deviceType == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动未开启，不允许报名！");
        Integer activityStatus = activity.getActivityStatus();
        if(activityStatus == 6 && deviceType == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动未开始，不允许报名！");
        activity.setUpdateTime(new Date());
        activity.setUpdater(userId);
        activity.setActivityPlayers((activity.getActivityPlayers()==null?0:activity.getActivityPlayers())+1);
        activityService.updateSelective(activity);
        Integer save = save(player);
        ApplySetting applySetting = new ApplySetting();
        applySetting.setActivityId(activityId);
        applySetting = applySettingMapper.selectOne(applySetting);
        if(applySetting == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"活动设置异常！");
        Integer miniImage = applySetting.getMiniImage();
        Integer maxImage = applySetting.getMaxImage();
        if(images != null && (images.size() < miniImage || images.size() > maxImage) && deviceType == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"选手图片数量需要在"+miniImage+"~"+maxImage+"之间！");
        if(images != null && !images.isEmpty()){
            List<FileMaterial> fileMaterials = new ArrayList<>();
            for (String im : images) {
                FileMaterial fileMaterial = new FileMaterial();
                fileMaterial.setType(1);
                fileMaterial.setCreateTime(new Date());
                fileMaterial.setCreator(userId);
                fileMaterial.setFileUrl(im);
                fileMaterial.setRelatedId(player.getId());
                fileMaterials.add(fileMaterial);
            }
            if(!fileMaterials.isEmpty())
                fileMaterialMapper.insertList(fileMaterials);
        }
        return player;
    }

    @Override
    public Integer update(PlayerModel playerModel, Long userId) {
        Long playerId = playerModel.getPlayerId();
//        boolean isManager = roleService.checkUserManagerPermission(userId);
//        if(!isManager)
//            throw new PermissionException();
        if(playerId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR);
        Player player = findById(playerId);
        if(player == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR);
        Long activityId = player.getActivityId();
        if(activityId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，选手没有关联活动！");
        List<String> images = playerModel.getImages();
        if(images != null && !images.isEmpty()){
            ApplySetting applySetting = new ApplySetting();
            applySetting.setActivityId(activityId);
            applySetting = applySettingMapper.selectOne(applySetting);
            if(applySetting == null)
                throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"活动设置异常！");
            Integer miniImage = applySetting.getMiniImage();
            Integer maxImage = applySetting.getMaxImage();
            if(images.size() < miniImage || images.size() > maxImage)
                throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"选手图片数量需要在"+miniImage+"~"+maxImage+"之间！");
            Example example = new Example(FileMaterial.class);
            example.createCriteria().andEqualTo("type",FileMaterial.Type.PLAYER.getValue())
                    .andEqualTo("relatedId",playerId);
            fileMaterialMapper.deleteByExample(example);
            List<FileMaterial> fileMaterials = new ArrayList<>();
            for (String im : images) {
                FileMaterial fileMaterial = new FileMaterial();
                fileMaterial.setType(FileMaterial.Type.PLAYER.getValue());
                fileMaterial.setCreateTime(new Date());
                fileMaterial.setCreator(userId);
                fileMaterial.setFileUrl(im);
                fileMaterial.setRelatedId(player.getId());
                fileMaterials.add(fileMaterial);
            }
            if(!fileMaterials.isEmpty())
                fileMaterialMapper.insertList(fileMaterials);
        }else{
            Example example = new Example(FileMaterial.class);
            example.createCriteria().andEqualTo("type",FileMaterial.Type.PLAYER.getValue())
                    .andEqualTo("relatedId",playerId);
            fileMaterialMapper.deleteByExample(example);
        }
        player.setImage(images != null && !images.isEmpty()?images.get(0):null);
        BeanUtil.copyPropertiesIgnoreNull(playerModel,player);
        Integer integer = updateSelective(player);
        if(images == null || images.isEmpty()){
            player.setImage(null);
            update(player);
        }

        //update today star player image if current player is today star
        Activity activity = activityService.findById(activityId);
        if(activity.getStar() != null && activity.getStar().equals(playerId)){
            Example example1 = new Example(FileMaterial.class);
            example1.createCriteria()
                    .andEqualTo("relatedId",activityId)
                    .andEqualTo("type",4)
                    .andEqualTo("star",0);
            List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example1);
            if(fileMaterials != null && !fileMaterials.isEmpty()){
                for (FileMaterial fileMaterial : fileMaterials) {
                    fileMaterial.setFileUrl(player.getImage());
                    fileMaterialMapper.updateByPrimaryKeySelective(fileMaterial);
                }
            }
        }
        return integer;
    }

    @Override
    public Integer update(BatchPlayerModel batchPlayerModel, Long userId,HttpServletRequest request) {
        Long playerId = batchPlayerModel.getPlayerId();
        Integer votes = batchPlayerModel.getVotes();
        Integer views = batchPlayerModel.getViews();
        BigDecimal gift = batchPlayerModel.getGift();
        if(playerId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"playerId is null");
//        Long isManager = roleService.getUserPermission(userId);
//        if(isManager == 3 && gift == null)
//            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR);
        Player player = findById(playerId);
        if(player == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"选手不存在！");
        player.setManualVotes((player.getManualVotes()==null?0:player.getManualVotes()) + (votes != null?votes:0));
        player.setGift((player.getGift()!=null?player.getGift():new BigDecimal(0)).add(gift != null?gift:new BigDecimal(0)));
        player.setViews((player.getViews()==null?0:player.getViews()) + (views != null?views:0));

        if(votes != null && votes > 0){
            VoteLog voteLog = new VoteLog();
            voteLog.setIp(HttpUtil.getClientIP(request)+" "+IpUtil.getCity(HttpUtil.getClientIP(request)));
//        voteLog.setName(name);
            voteLog.setRemark("投了"+votes+"票！");
            voteLog.setOpenId(null);
            voteLog.setCreateTime(new Date());
            voteLog.setCreator(0L);
            voteLog.setPlayerId(playerId);
            voteLogMapper.insert(voteLog);
            player.setViews((player.getViews()==null?0:player.getViews()) + votes);
        }

        Integer integer = updateSelective(player);
        return integer;
    }

    @Override
    public Integer delete(Long playerId, Long userId) {
//        boolean isManager = roleService.checkUserManagerPermission(userId);
//        if(!isManager)
//            throw new PermissionException();
        Player player = findById(playerId);
        if(player == null)
            throw new XzcmBaseRuntimeException("选手不存在！");
        scheduleJobService.deleteScheduleJobForAutoVote(playerId);
        activityService.deleteImages(playerId,2);
        Integer integer = deleteById(playerId);
        return integer;
    }

    @Override
    public UploadFile getExcelData(ExportDataModel exportDataModel, Long userId) {
        UploadFile uploadFile = new UploadFile();
        Long activityId = exportDataModel.getActivityId();
        if(activityId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"activityId must not be null.");
        List<ExcelModel> excelData = playerMapper.getExcel(activityId, exportDataModel.getPlayerIds());
        if (excelData != null && !excelData.isEmpty()) {
            Activity activity = activityService.findById(activityId);
            String[] titleList = new String[]{"编号", "姓名", "手机号码", "票数", "礼物", "参加时间", "排序/名次"};
            String[] proNames = new String[]{"playerId", "name", "phone", "votes", "gift", "createTime", "rank"};
            String filePath = FileUtils.getTempDirectoryPath().concat(File.separator).concat(DateTime.now().toString("yyyyMMddhhmmss") + "选手信息表.xls");
            File file = new File(filePath);
            try (HSSFWorkbook workbook = new HSSFWorkbook();
                 ByteArrayOutputStream out = new ByteArrayOutputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                HSSFSheet sheet = workbook.createSheet(activity.getActivityName() != null?activity.getActivityName():"选手信息");
                ExcelUtil.createExcel(workbook, sheet, excelData, titleList, proNames, 1, 0,activity.getActivityName() != null?activity.getActivityName():"选手信息");
                workbook.write(out);
                fileOutputStream.write(out.toByteArray());
                uploadFile = uploadFileCridFsService.uploadFile(file);
            }catch (Exception e){
                e.getMessage();
            }
        }
        return uploadFile;
    }

    @Override
    public Integer batchUpdate(BatchUpdateModel batchUpdateModel, Long userId) {
        String remark = batchUpdateModel.getRemark();
        List<Long> ids = batchUpdateModel.getIds();
        if(remark == null || ids.isEmpty()){
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"参数错误");
        }

        Integer sum = 0;
        for (Long id : ids) {
            Player byId = findById(id);
            byId.setRemark(remark);
            sum += updateSelective(byId);
        }
        return sum;
    }

    @Override
    public Integer star(Long playerId, Long userId) {
        Player player = findById(playerId);
        if (player == null) {
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"选手id不能为空！");
        }
        Long activityId = player.getActivityId();
        Example example1 = new Example(FileMaterial.class);
        example1.createCriteria()
                .andEqualTo("relatedId",activityId)
                .andEqualTo("type",4)
                .andEqualTo("star",0);
        List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example1);
        if(player.getStar() == 0){
            player.setStar(1);
            updateSelective(player);
            if(fileMaterials != null && !fileMaterials.isEmpty()){
                fileMaterials.forEach(it -> {
                    fileMaterialMapper.delete(it);
                });
            }
            return 1;
        }
        Activity activity = activityService.findById(activityId);
        if (activity == null) {
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"选手没有关联活动！");
        }
        Example example = new Example(Player.class);
        example.createCriteria().andEqualTo("star",0).andEqualTo("activityId",activityId);
        List<Player> players = selectByExample(example);
        for (Player player1 : players) {
            player1.setStar(1);
            updateSelective(player1);
        }
        player.setStar(0);
        updateSelective(player);

        if(fileMaterials != null && !fileMaterials.isEmpty()){
            fileMaterials.forEach(it -> {
                fileMaterialMapper.delete(it);
            });
        }
        FileMaterial fileMaterial = new FileMaterial();
        fileMaterial.setFileUrl(player.getImage());
        fileMaterial.setType(4);
        fileMaterial.setRelatedId(activityId);
        fileMaterial.setStar(0);
        fileMaterial.setCreateTime(new Date());
        fileMaterial.setCreator(userId);
        fileMaterialMapper.insert(fileMaterial);

        activity.setStar(playerId);
        activity.setUpdateTime(new Date());
        activity.setUpdater(userId);
        Integer integer = activityService.updateSelective(activity);
        return integer;
    }

    @Override
    public VotesModel getVotesByActivity(Long activityId) {

        return playerMapper.getVotesByActivity(activityId);
    }

    @Override
    public Integer oneClickAdded(PlayerSearchModel playerSearchModel, Long userId) {
        String search = playerSearchModel.getSearch();
        String name = null;
        Long playerId = null;
        Long num = null;
        if(search != null){
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(search);
            if(isNum.matches()){
//                playerId = Long.valueOf(search);
                num = Long.valueOf(search);
            }else {
                name = search;
            }
        }
        Integer result = 0;
//        boolean isManager = roleService.checkUserManagerPermission(userId);
        Long activityId = playerSearchModel.getActivityId();
        if(activityId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"activityId must not be null.");
//        if(!isManager)
//            throw new PermissionException();
        Integer page = playerSearchModel.getPage();
        if(page == null)
            page=1;
        Integer size = playerSearchModel.getSize();
        if(size == null)
            size = 10;
        String sort = playerSearchModel.getSort();
        PageHelper.startPage(page,size,sort);
        List<PlayerModel> playerModels = playerMapper.get(name, playerId,playerSearchModel.getApprovalStatus(),activityId,num,sort);
        for (PlayerModel playerModel : playerModels) {
            int ranVote = (int)(Math.random()*10);
            Player player = findById(playerModel.getPlayerId());
            player.setManualVotes((player.getManualVotes() == null?0:player.getManualVotes())+ranVote);
            result += updateSelective(player);
        }
        return result;
    }

    @Override
    public QrCodeModel getPlayerAccessLink(Long playerId, Long userId) {
        String domain = "";
        Map<String, String> basic = systemParamService.basic();
        String platformDomain = basic.get("platformDomain");
        if(platformDomain == null || platformDomain.equals(""))
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"平台域名配置异常！");
        domain = platformDomain.substring(platformDomain.indexOf("."),platformDomain.length())+"#/home";
        Player player = findById(playerId);
        Example example = new Example(FileMaterial.class);
        example.createCriteria().andEqualTo("type",3);
        List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example);
        if(fileMaterials != null && !fileMaterials.isEmpty()){
            for (FileMaterial fileMaterial : fileMaterials) {
                String fileUrl = fileMaterial.getFileUrl();
                String fileId = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
                uploadFileService.removeFile(fileId);
            }
            fileMaterialMapper.deleteByExample(example);
        }

        QrCodeModel qrCodeModel = new QrCodeModel();
        String randomDomain = StringRandom.getStringRandom(3);
        while ("www".equals(randomDomain)){
            randomDomain = StringRandom.getStringRandom(3);
        }
        Long activityId = player.getActivityId();
        if(activityId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"选手没有关联活动！");
        try{
            SystemParam systemParam = new SystemParam();
            systemParam.setKey("domain_"+activityId);
            systemParam = systemParamService.findOne(systemParam);
            String value = null;
            if(systemParam != null){
                value = systemParam.getValue();
                if(value.split(",").length > 0)
                    value = value.replace(",",".");
            }
            String finalDomain ="http://"+randomDomain + (value != null?("." + value):"") + domain + "?activityId=" + activityId + "&id=" + playerId;
            qrCodeModel.setAccessUrl(finalDomain);
            File qrFile = File.createTempFile("qrJpg",".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(qrFile);

            QrCodeCreateUtil.createQrCode(fileOutputStream,finalDomain,900,"JPEG");
            UploadFile uploadFile = uploadFileService.uploadFile(qrFile);
            qrCodeModel.setQrCodeUrl(uploadFile.getUrl());

            FileMaterial fileMaterial = new FileMaterial();
            fileMaterial.setFileUrl(uploadFile.getUrl());
            fileMaterial.setType(3);
            fileMaterial.setRelatedId(playerId);
            fileMaterial.setCreator(userId);
            fileMaterial.setCreateTime(new Date());
            fileMaterialMapper.insert(fileMaterial);


            qrFile.deleteOnExit();
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return qrCodeModel;
    }

    @Override
    public H5PlayerDetailModel getH5PlayerDetail(Long playerId) {
        H5PlayerDetailModel h5PlayerDetailModel = new H5PlayerDetailModel();
        if(playerId ==null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"playerId must not be null");
        Player player = findById(playerId);
        if(player != null){
            player.setIpAmount((player.getIpAmount() == null?0:player.getIpAmount()) + 1);
            h5PlayerDetailModel = playerMapper.getH5PlayerDetail(playerId,player.getActivityId());
            List<String> images = fileMaterialMapper.getImages(playerId, 1);
            if(player.getImage() != null && images.isEmpty()){
                images.add(player.getImage());
            }
            h5PlayerDetailModel.setImages(images);
            Integer gap = playerMapper.gapPre(h5PlayerDetailModel.getCurrentVotes(), player.getActivityId());
            h5PlayerDetailModel.setGapPrevious(gap != null?gap:0);
        }
        return h5PlayerDetailModel;
    }

    @Override
    public List<PlayerRankModel> getPlayerRank(Long activityId) {
        InterfaceSetting interfaceSetting = new InterfaceSetting();
        interfaceSetting.setActivityId(activityId);
        interfaceSetting = interfaceSettingMapper.selectOne(interfaceSetting);
        if(interfaceSetting != null && interfaceSetting.getLeaderboardNum() != null)
            return playerMapper.getPlayerRankModel(activityId,interfaceSetting.getLeaderboardNum());
        return playerMapper.getPlayerRankModelForAll(activityId);
    }

    @Override
    public List<VoteLog> getVoteLog(Long playerId, Long userId) {
        Example example = new Example(VoteLog.class);
        example.createCriteria().andEqualTo("playerId",playerId);
        return voteLogMapper.selectByExample(example);
    }

    @Override
    public List<GiftLog> getGiftLog(Long playerId, Long userId) {
        Example example = new Example(GiftLog.class);
        example.createCriteria().andEqualTo("playerId",playerId);
        return giftLogMapper.selectByExample(example);
    }

    @Override
    public H5PlayerDetailModel vote(HttpServletRequest request, H5VoteModel h5VoteModel,Integer vote,boolean flag) {
        Long playerId = h5VoteModel.getPlayerId();
//        String name = h5VoteModel.getName();
        String openId = h5VoteModel.getOpenId();

//        if(name == null)
//            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"name must not be null.");
        if (openId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"openId must not be null.");

        if(playerId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"playerId must not be null.");
        Player player = findById(playerId);

        Long activityId = player.getActivityId();
        Activity activity = activityService.findById(activityId);
        ActivitySetting activitySetting = new ActivitySetting();
        activitySetting.setActivityId(activityId);
        activitySetting = activitySettingMapper.selectOne(activitySetting);
        Date applyStartTime = activitySetting.getApplyStartTime();
        Date applyEndTime = activitySetting.getApplyEndTime();
        Date voteStartTime = activitySetting.getVoteStartTime();
        Date voteEndTime = activitySetting.getVoteEndTime();
        if(activity == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"选手对应的活动为空！");

        Integer approvalStatus = activity.getApprovalStatus();
        if(approvalStatus == 1)
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动未通过审核，不允许投票！");
        Integer activityStatus = activity.getActivityStatus();
        if(activityStatus == 0)
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动已关闭，不允许投票！");
//        if(activityStatus == 6 || activity.getActivityStartTime().getTime() > System.currentTimeMillis()) {
//            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动未开始，不允许投票！");
//        }
        if(applyStartTime == null || applyEndTime == null){
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动报名时间异常，不允许投票！");
        }
        if(voteStartTime.getTime() > System.currentTimeMillis()){
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动未到投票时间，不允许投票！");
        }
        if(voteEndTime.getTime() < System.currentTimeMillis()){
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"活动投票时间结束，不允许投票！");
        }
        if(!flag) {
            RuleSetting ruleSetting = new RuleSetting();
            ruleSetting.setActivityId(activityId);
            ruleSetting = ruleSettingMapper.selectOne(ruleSetting);
            if (ruleSetting == null)
                throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR, "活动规则为空.");
            Integer activityPlayers = activity.getActivityPlayers() == null ? 0 : activity.getActivityPlayers();
            Integer minPlayers = ruleSetting.getMinPlayers() != null ? ruleSetting.getMinPlayers() : 0;
            Integer userLimit = ruleSetting.getUserLimit() != null ? ruleSetting.getUserLimit() : 0;
            Integer dayLimit = ruleSetting.getDayLimit() != null ? ruleSetting.getDayLimit() : 0;
            Integer sumLimit = ruleSetting.getSumLimit() != null ? ruleSetting.getSumLimit() : 0;
            Integer voteInterval = ruleSetting.getVoteInterval() != null ? ruleSetting.getVoteInterval() : 0;
            Integer lockStatus = player.getLockStatus();
            Integer lockTime = player.getLockTime();
            Date updateTime = player.getLockDate();
            if (lockStatus == 0 && lockTime != null) {
                long diff = TimeSearchUtil.dateDiff(updateTime, new Date());
                if (diff != 0) {
                    if (diff / 60 / 1000 < lockTime) {
                        log.info("锁定状态:{},锁定时间 :{},锁定条件 :{}",lockStatus,lockTime,lockStatus == 0 && lockTime != null);
                        throw new XzcmBaseRuntimeException(CodeMessage.VOTEERROR, "该选手被锁定了，未过锁定时间不允许投票！");
                    }
                }
            }
            if (activityPlayers < minPlayers)
                throw new XzcmBaseRuntimeException(CodeMessage.VOTEERROR, "活动人数不足，暂时不能投票！");
            Integer playerDayUserVotes = voteLogMapper.getUserLimit(openId, playerId,activityId);
            if (playerDayUserVotes != null && playerDayUserVotes >= userLimit)
                throw new XzcmBaseRuntimeException(CodeMessage.VOTEERROR);
            Integer userDayVotes = voteLogMapper.getDayLimit(openId,activityId);
            if (userDayVotes != null && userDayVotes >= dayLimit && dayLimit != 0)
                throw new XzcmBaseRuntimeException(CodeMessage.VOTEERROR);
            Integer userVoteSum = voteLogMapper.getUserVoteSum(openId,activityId);
            if (userVoteSum != null && userVoteSum >= sumLimit && sumLimit != 0)
                throw new XzcmBaseRuntimeException(CodeMessage.VOTEERROR);
            Integer voteIn = voteLogMapper.voteInternal(openId);
            if (voteIn != null && voteIn < voteInterval && voteInterval != 0)
                throw new XzcmBaseRuntimeException(CodeMessage.VOTEERROR, "投票间隔时间过短，请稍后投票！");
        }

        int activityActVotes = activity.getActVotes() != null ? activity.getActVotes() : 0;
        activity.setActVotes(activityActVotes+vote);
        activity.setUpdateTime(new Date());
        activityService.updateSelective(activity);

        int playerVotes = player.getActualVotes() != null?player.getActualVotes():0;
        player.setActualVotes(playerVotes + vote);
        Integer integer = updateSelective(player);

        VoteLog voteLog = new VoteLog();
        voteLog.setIp(HttpUtil.getClientIP(request)+" "+IpUtil.getCity(HttpUtil.getClientIP(request)));
//        voteLog.setName(name);
        voteLog.setRemark("投了"+vote+"票！");
        voteLog.setOpenId(openId);
        voteLog.setCreateTime(new Date());
        voteLog.setCreator(0L);
        voteLog.setPlayerId(playerId);
        voteLogMapper.insert(voteLog);

        return getH5PlayerDetail(playerId);
    }

    @Override
    public Map<String, Object> gift(HttpServletRequest request, H5GiftModel h5GiftModel) {
        Long playerId = h5GiftModel.getPlayerId();
        String openId = h5GiftModel.getOpenId();
//        Integer status = h5GiftModel.getStatus();
        Long giftId = h5GiftModel.getGiftId();
        BigDecimal giftAmount = h5GiftModel.getGiftAmount();

        if(giftAmount == null && giftId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"礼物金额为空！");

        BigDecimal gifAmount = giftAmount;
        if(giftId != null){
            GiftSetting giftSetting = giftSettingMapper.selectByPrimaryKey(giftId);
            if(giftSetting == null)
                throw  new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，礼物不存在！");
            if(giftSetting.getGiftPrice() == null)
                throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，礼物金额不存在！");
            gifAmount = giftSetting.getGiftPrice();
        }

        Player player = findById(playerId);
        if (player == null){
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，选手不存在！");
        }
        Long activityId = player.getActivityId();
        Activity activity = activityService.findById(activityId);
        if(activity == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，活动不存在！");
        RuleSetting ruleSetting = new RuleSetting();
        ruleSetting.setActivityId(activityId);
        ruleSetting = ruleSettingMapper.selectOne(ruleSetting);
        if(ruleSetting == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"数据异常，活动设置不存在！");
        BigDecimal giftLimit = ruleSetting.getGiftLimit();

        BigDecimal userSumGift = giftLogMapper.getUserSumGift(openId);
        userSumGift =  userSumGift.add(gifAmount);
        if(userSumGift != null && userSumGift.compareTo(giftLimit) > 0 && giftLimit.compareTo(new BigDecimal(0.0000)) != 0)
            throw new XzcmBaseRuntimeException(CodeMessage.GIFTREEOR);

        Integer integer = 0;
//        if(status == 0){
        GiftLog giftLog = new GiftLog();
        giftLog.setGiftAmount(gifAmount);
        BeanUtil.copyPropertiesIgnoreNull(h5GiftModel,giftLog);
        giftLog.setCreateTime(new Date());
        giftLog.setCreator(0L);
        giftLog.setStatus(1);
        giftLogMapper.insert(giftLog);
        GiftPayModel giftPayModel = new GiftPayModel();
        giftPayModel.setGitId(giftLog.getId());
        Map<String, Object> map = weChatPaYService.weixinPayWap(request, giftPayModel);
//        }else {
//            BigDecimal gift = player.getGift();
//            player.setGift(gift != null?gift.add(h5GiftModel.getGiftAmount()):h5GiftModel.getGiftAmount());
//            integer = updateSelective(player);
//
//            BigDecimal activityAmount = activity.getActivityAmount();
//            activity.setActivityAmount(activityAmount != null?activityAmount.add(h5GiftModel.getGiftAmount()):h5GiftModel.getGiftAmount());
//            activity.setUpdateTime(new Date());
//            activityService.updateSelective(activity);
//        }
//        H5VoteModel h5VoteModel = new H5VoteModel();
//        h5VoteModel.setOpenId(h5GiftModel.getOpenId());
//        h5VoteModel.setPlayerId(playerId);
//        vote(request,h5VoteModel,Integer.parseInt(giftAmount.multiply(new BigDecimal(3)).toString()));
        return map;
    }

    @Override
    public Integer status(PlayerStatusModel playerStatusModel, Long userId) {
//        boolean isManage = roleService.checkUserManagerPermission(userId);
//        if(!isManage)
//            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR);

        Integer approvalStatus = playerStatusModel.getApprovalStatus();
        Integer lockStatus = playerStatusModel.getLockStatus();
        Integer voteStatus = playerStatusModel.getVoteStatus();
        Integer lockTime = playerStatusModel.getLockTime();
        Long playerId = playerStatusModel.getPlayerId();

        Player player = findById(playerId);

        if(player == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"选手不存在！");
        if(approvalStatus != null)
            player.setApprovalStatus(approvalStatus);
        if(voteStatus != null)
            player.setVoteStatus(voteStatus);
        if(lockStatus != null && lockStatus != 1 && lockTime != null){
            player.setLockStatus(lockStatus);
            player.setLockTime(lockTime);
            player.setLockDate(new Date());
        }
        if(lockStatus != null && lockStatus == 1)
            player.setLockStatus(lockStatus);

        if(lockStatus != null && lockStatus == 0 && lockTime == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"指定为锁定状态时需要指定锁定的时间！");

        player.setUpdateTime(new Date());

        Integer integer = updateSelective(player);


        return integer;
    }

    @Override
    public Integer shareCallback(HttpServletRequest request, ShareCallbackModel shareCallbackModel) {
        Long activityId = shareCallbackModel.getActivityId();
        Long playerId = shareCallbackModel.getPlayerId();

        if(activityId == null && playerId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR);

        Integer result = 0;
        if(activityId != null){
            Activity activity = activityService.findById(activityId);
            activity.setActivityShares((activity.getActivityShares()==null?0:activity.getActivityShares())+ 1);
            result += activityService.updateSelective(activity);
        }
        if(playerId != null){
            Player player = findById(playerId);
            player.setShares((player.getShares()==null?0:player.getShares())+1);
            result += updateSelective(player);
        }

        return result;
    }

    @Override
    @Async
    public ScheduleJob scheduled(HttpServletRequest request,PlayerScheduledModel playerScheduledModel, Long userId) {
//        boolean isManage = roleService.checkUserManagerPermission(userId);
//        if(!isManage)
//            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR);
//        Long userPermission = roleService.getUserPermission(userId);
//        if (userPermission == 3) {
//            throw new PermissionException();
//        }

        Player player = findById(playerScheduledModel.getPlayerId());
        player.setSchedulStatus(0);
        updateSelective(player);
        executorService.execute(() -> {
            String jobName = "选手编号"+playerScheduledModel.getPlayerId()+"的定时投票任务";
            String groupName = "自动投票任务组";

            ScheduleJob record = scheduleJobService.findByJobNameAndJobGroup(jobName,groupName);
            if (null != record) {
                scheduleJobService.deleteScheduleJob(record.getId());
            }

            //处理时间
            Integer seconds = playerScheduledModel.getSeconds();
            Integer hours = seconds/60;
            Integer day = hours/24;
            String corn = "0 0 0 0/1 *  ?";//默认一天一次
            if(seconds != null){
                if(seconds >= 1 && seconds < 60){
                    corn = "0 */1 * * * ?".replace("/1","/"+ seconds);
                }else if(hours >= 1 && hours < 24){
                    corn = "0 0 */1 * *  ?".replace("/1","/"+ hours);
                }else if(day >= 1){
                    corn = "0 0 0 /1 *  ?".replace("/1","/"+ day);
                }
            }
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setRemoteUrl(request.getParameter("remoteUrl"));
            User user = new User();
            user.setId(userId);
            scheduleJob.setBeanClass("com.xzcmapi.service.impl.PlayerServiceImpl");
            scheduleJob.setCron(corn);
            scheduleJob.setIsAsync(true);
            scheduleJob.setIsLocal(true);
            scheduleJob.setJobName(jobName);
            scheduleJob.setJobGroup(groupName);
            scheduleJob.setStatus(1);
            scheduleJob.setMethodName("autoVotes");
            scheduleJob.setRemarks("设置自动给选手投票");
            ScheduleParam scheduleParam = new ScheduleParam();
            scheduleParam.setPlayerId(playerScheduledModel.getPlayerId());
            scheduleParam.setVotes(playerScheduledModel.getVotes());
            scheduleJob.setParams(playerScheduledModel.getPlayerId()+","+playerScheduledModel.getVotes());
            scheduleJobService.saveScheduleJob(scheduleJob,user);
            player.setSchedulId(scheduleJob.getId());
            updateSelective(player);
        });

        return null;
    }

    /**
     * 自动投票设置
     * @param playerId
     * @param votes
     */
    public void autoVotes(Long playerId,Integer votes){
        PlayerService bean = SpringUtils.getBean(PlayerService.class);
        Player player = bean.findById(playerId);
        if(player != null){
            player.setManualVotes((player.getManualVotes()==null?0:player.getManualVotes()) + votes);
            bean.updateSelective(player);
        }

    }


    @Override
    public Integer batchDelete(List<Long> playerIds, Long userId) {
        Integer result = 0;
        for (Long id : playerIds) {
            result += delete(id, userId);
        }
        return result;
    }

    @Override
    @Async
    public Integer scheduledDown(Long playerId, Long userId) {
        if(playerId ==null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR);
//        Long userPermission = roleService.getUserPermission(userId);
//        if(userPermission == 3)
//            throw new PermissionException();
        Player player = findById(playerId);
        Long schedulId = player.getSchedulId();
        if(schedulId == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"定时任务不存在！");
        Integer schedulStatus = player.getSchedulStatus();
        if(schedulStatus ==null || schedulStatus == 1)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"定时任务已经关闭");
        player.setSchedulStatus(1);
        updateSelective(player);
        executorService.execute(() -> scheduleJobService.deleteScheduleJob(schedulId));
        return 1;
    }

    @Override
    public PlayerModel detail(Long id) {
        Player player = findById(id);
        List<PlayerModel> playerModels = playerMapper.get(null, id, null, player.getActivityId(),null,null);
        Example example = new Example(FileMaterial.class);
        example.createCriteria().andEqualTo("type",FileMaterial.Type.PLAYER.getValue())
                .andEqualTo("relatedId",id);
        List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example);
        List<String> images = new ArrayList<>();
        if(fileMaterials != null && !fileMaterials.isEmpty())
            fileMaterials.forEach(it -> {
                images.add(it.getFileUrl());
            });
        if(!images.isEmpty() && playerModels != null && !playerModels.isEmpty())
            playerModels.get(0).setImages(images);

        if(images.isEmpty() && playerModels != null && !playerModels.isEmpty())
            playerModels.get(0).setImages(playerModels.get(0).getImage() != null ? Collections.singletonList(playerModels.get(0).getImage()) : images);

        return playerModels != null && !playerModels.isEmpty()?playerModels.get(0):new PlayerModel();
    }

    @Override
    public void updateLockStatus() {
        Example example = new Example(Player.class);
        example.createCriteria().andEqualTo("lockStatus",0);
        List<Player> players = playerMapper.selectByExample(example);
//        log.info("需要更新的锁定的选手的数量有: {}",players.size());
        for (Player player : players) {
            if(player.getLockDate() != null && player.getLockTime() != null){
                long diff = TimeSearchUtil.dateDiff(player.getLockDate(), new Date());
//                log.info("锁定的时常为: {},锁定时间的差值为: {}",player.getLockTime(),diff);
                if (diff != 0) {
                    if (diff / 60 / 1000 > player.getLockTime()) {
                        player.setLockStatus(1);
                        updateSelective(player);
                    }
                }
            }
        }
    }
}
