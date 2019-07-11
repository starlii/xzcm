package com.xzcmapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.entity.*;
import com.xzcmapi.exception.DataException;
import com.xzcmapi.exception.PermissionException;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.*;
import com.xzcmapi.model.*;
import com.xzcmapi.service.*;
import com.xzcmapi.util.BeanUtil;
import com.xzcmapi.util.QrCodeCreateUtil;
import com.xzcmapi.util.StringRandom;
import com.xzcmapi.util.TimeSearchUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.formula.functions.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static sun.audio.AudioPlayer.player;

@Service
@Transactional
public class ActivityServiceImpl extends BaseServiceImpl<Activity> implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ActivitySettingMapper activitySettingMapper;
    @Autowired
    private RuleSettingMapper ruleSettingMapper;
    @Autowired
    private VoteSettingMapper voteSettingMapper;
    @Autowired
    private InterfaceSettingMapper interfaceSettingMapper;
    @Autowired
    private ApplySettingMapper applySettingMapper;
    @Autowired
    private GiftSettingMapper giftSettingMapper;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private UploadFileCridFsService uploadFileService;
    @Autowired
    private FileMaterialMapper fileMaterialMapper;
    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private GiftLogMapper giftLogMapper;
    @Autowired
    private VoteLogMapper voteLogMapper;
    @Autowired
    private SystemParamService systemParamService;
    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Value("${xzcm.url}")
    private String serverUrl;

    private static final String ACCESSURL = "/activity/getH5HomePage?key=";

    @Override
    public PageInfo<List<ActivityModel>> list(SearchModel searchModel,Long userId,Boolean isSelf) {
        int time = searchModel.getTime();
        String search = searchModel.getSearch();
        Integer status = searchModel.getStatus();
        Date nextDay = null;
        if (time != 0){
             nextDay = TimeSearchUtil.getNextDay(new Date(), time);
        }
        Integer approval = null;
//        if(status != null && status == 0){
//            approval = 0;
//            status = null;
//        }
        Integer todayEnd = null;
        if(status != null && status == 4){
            todayEnd = 4;
        }
        User user = userService.findById(userId);
//        List<Role> byUserId = roleMapper.findByUserId(userId);
//        for (Role role : byUserId) {
//            if (role.getIsManager().equals(Role.Manager.MANAGER.getValue())) {
//                userId = null;
//                break;
//            }
//        }
        PageHelper.startPage(searchModel.getPage(),searchModel.getSize(),searchModel.getSort());
        List<ActivityModel> activityModels = activityMapper.get(search, nextDay,status,userId,user.getRoleValue(),isSelf,todayEnd);
        activityModels.forEach(it -> {
            it.setStatusName(ActivityModel.getStatusName(it.getActivityStatus()));
        });
        return new PageInfo(activityModels);
    }

    @Override
    public Integer add(SaveActivityModel saveActivityModel, Long userId) {
        Integer result = 0;
        String activityName = saveActivityModel.getActivityName();
        if (activityName == null)
            throw new DataException("活动标题缺失！");
        if(userId == null)
            throw new PermissionException();

        //save activity
        Activity activity = new Activity();
        activity.setCreateTime(new Date());
        activity.setCreator(userId);
        activity.setUpdateTime(new Date());
        activity.setApprovalStatus(0);
        activity.setIsDefaultStatus(0);
        activity.setUpdater(userId);
        ActivitySettingModel activitySetting = saveActivityModel.getActivitySetting();
        Date voteStart = null;
        Date voteEnd = null;
        if(activitySetting != null && activitySetting.getApplyStartTime() != null){
            activity.setActivityStartTime(activitySetting.getApplyStartTime());
            voteStart = activitySetting.getApplyStartTime();
        }
        if(activitySetting != null && activitySetting.getVoteEndTime() != null){
            activity.setActivityEndTime(new Date(activitySetting.getVoteEndTime().getTime()+(activitySetting.getEndGiftTime() == null?0:activitySetting.getEndGiftTime())));
            voteEnd = activitySetting.getVoteEndTime();
        }
        if(activity.getActivityEndTime() == null || activity.getActivityStartTime() == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"活动报名和投票时间不能为空！");
        if(activity.getActivityStartTime().getTime() > activity.getActivityEndTime().getTime())
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"活动开始时间大于结束时间！");
        activity.setActivityStatus(getActivityStatus(activity.getActivityStartTime(),activity.getActivityEndTime(),voteStart,voteEnd));
        BeanUtil.copyPropertiesIgnoreNull(saveActivityModel,activity);
        List<String> images = saveActivityModel.getImages();
        activity.setActivityImage(images != null && !images.isEmpty()?images.get(0):null);
        if(activity.getApprovalStatus() == 1){
            activity.setActivityStatus(0);
        }
        save(activity);
        result++;


        //save activity files
        if(images != null && !images.isEmpty()){
            List<FileMaterial> fileMaterials = new ArrayList<>();
            for (String image : images) {
                FileMaterial fileMaterial = new FileMaterial();
                fileMaterial.setType(4);
                fileMaterial.setCreateTime(new Date());
                fileMaterial.setCreator(userId);
                fileMaterial.setFileUrl(image);
                fileMaterial.setRelatedId(activity.getId());
                fileMaterials.add(fileMaterial);
            }
            if(!fileMaterials.isEmpty())
                fileMaterialMapper.insertList(fileMaterials);
        }

        Long activetyId = activity.getId();
        //save activity setting
        if(activitySetting != null){
            ActivitySetting activitySet = new ActivitySetting();
            activitySet.setCreateTime(new Date());
            activitySet.setCreator(userId);
            activitySet.setActivityId(activetyId);
            BeanUtil.copyPropertiesIgnoreNull(activitySetting,activitySet);
            activitySet.setMode1(activitySetting.getMode1() == null?1:activitySetting.getMode1());
            activitySet.setMode2(activitySetting.getMode2() == null?2:activitySetting.getMode2());
            activitySet.setMode2(activitySetting.getMode3() == null?2:activitySetting.getMode3());
            activitySet.setIsShowPage(activitySetting.getIsShowPage() == null?0:activitySetting.getIsShowPage());
            activitySettingMapper.insert(activitySet);
            result++;
        }

        //save rule setting
        RuleSettingModel ruleSettingModel = saveActivityModel.getRuleSetting();
        RuleSetting ruleSetting = new RuleSetting();
        ruleSetting.setCreateTime(new Date());
        ruleSetting.setCreator(userId);
        ruleSetting.setActivityId(activetyId);
        ruleSetting.setMinPlayers(0);//默认
        ruleSetting.setUserLimit(1);//
        ruleSetting.setDayLimit(3);//0为不限制
        ruleSetting.setSumLimit(0);//0为不限制
        ruleSetting.setVoteInterval(0);//0为不限制
        ruleSetting.setGiftLimit(new BigDecimal(0));//0为不限制
        if(ruleSettingModel != null){
            BeanUtil.copyPropertiesIgnoreNull(ruleSettingModel,ruleSetting);
            ruleSetting.setIsFollow(ruleSettingModel.getIsFollow() == null?0:ruleSettingModel.getIsFollow());//默认是
            ruleSetting.setApprovalType(ruleSettingModel.getApprovalType() == null?0:ruleSettingModel.getApprovalType());//默认是
            ruleSetting.setIsRemainder(ruleSettingModel.getIsRemainder() == null?1:ruleSettingModel.getIsRemainder());//默认否
            ruleSetting.setAnonymousGift(ruleSettingModel.getAnonymousGift() == null?1:ruleSettingModel.getAnonymousGift());//默认否
            ruleSetting.setVoteSelf(ruleSettingModel.getVoteSelf() == null?0:ruleSettingModel.getVoteSelf());//默认是
            ruleSetting.setVoteAuth(ruleSettingModel.getVoteAuth() == null?1:ruleSettingModel.getVoteAuth());//默认否
        }
        ruleSettingMapper.insert(ruleSetting);
        result++;


        //save vote setting
        VoteSettingModel voteSettingModel = saveActivityModel.getVoteSetting();

        VoteSetting voteSetting = new VoteSetting();
        voteSetting.setCreateTime(new Date());
        voteSetting.setCreator(userId);
        voteSetting.setActivityId(activetyId);
        if (voteSettingModel != null){
            BeanUtil.copyPropertiesIgnoreNull(voteSettingModel,voteSetting);
            voteSetting.setIsStar(voteSettingModel.getIsStar() == null?1:voteSettingModel.getIsStar());
            voteSetting.setIsGiftFor(voteSettingModel.getIsGiftFor()==null?1:voteSettingModel.getIsGiftFor());
            voteSetting.setIsVoteFor(voteSettingModel.getIsVoteFor() == null?0:voteSettingModel.getIsVoteFor());
        }
        voteSettingMapper.insert(voteSetting);
            result++;

        //save interface setting
        InterfaceSettingModel interfaceSettingModel = saveActivityModel.getInterfaceSetting();

        InterfaceSetting interfaceSetting = new InterfaceSetting();
        interfaceSetting.setCreateTime(new Date());
        interfaceSetting.setCreator(userId);
        interfaceSetting.setActivityId(activetyId);
        if (interfaceSettingModel != null){
            BeanUtil.copyPropertiesIgnoreNull(interfaceSettingModel,interfaceSetting);
            interfaceSetting.setHomeShowOrder(interfaceSettingModel.getHomeShowOrder() == null?4:interfaceSettingModel.getHomeShowOrder());
            interfaceSetting.setShowGift(interfaceSettingModel.getShowGift() == null?1:interfaceSettingModel.getShowGift());
            interfaceSetting.setShowPopularity(interfaceSettingModel.getShowPopularity() == null?1:interfaceSettingModel.getShowPopularity());
            interfaceSetting.setThemeColor(interfaceSettingModel.getThemeColor() == null?"6281FD":interfaceSettingModel.getThemeColor());
            activity.setMualViews(interfaceSetting.getVisualViews() != null?interfaceSetting.getVisualViews():0);
        }
        interfaceSettingMapper.insert(interfaceSetting);
        result++;

        //save apply setting
        ApplySettingModel applySettingModel = saveActivityModel.getApplySetting();

            ApplySetting applySetting = new ApplySetting();
            applySetting.setCreateTime(new Date());
            applySetting.setCreator(userId);
            applySetting.setActivityId(activetyId);
        if (applySettingModel != null){
            BeanUtil.copyPropertiesIgnoreNull(applySettingModel,applySetting);
        }
        applySettingMapper.insert(applySetting);
        result++;

        //save gift setting
        GiftSettingModel giftSettingModel = saveActivityModel.getGiftSetting();

        GiftSetting giftSetting = new GiftSetting();
        giftSetting.setCreateTime(new Date());
        giftSetting.setCreator(userId);
        giftSetting.setActivityId(activetyId);
        giftSetting.setGiftName("钻石");//不可修改
        if (giftSettingModel !=null){
            BeanUtil.copyPropertiesIgnoreNull(giftSettingModel,giftSetting);
            giftSetting.setGiftPage(giftSettingModel.getGiftPage() == null?1:giftSettingModel.getGiftPage());
            giftSetting.setGiftPrice(new BigDecimal(1));//默认值
            giftSetting.setGiftVotes(3);//默认值
        }
        List<BigDecimal> giftModels = giftSettingModel.getGiftPrice();
        List<GiftSetting> giftSettings = new ArrayList<>();
        if(giftModels != null && !giftModels.isEmpty()){
            for (BigDecimal price : giftModels) {
                GiftSetting tempGift = new GiftSetting();
                BeanUtil.copyPropertiesIgnoreNull(giftSetting,tempGift);
                tempGift.setGiftPrice(price);
                tempGift.setGiftVotes(Integer.valueOf(String.valueOf(new BigDecimal(3).multiply(price))));//票数为价格的三倍
                giftSettings.add(tempGift);
            }
            if(!giftSettings.isEmpty())
                giftSettingMapper.insertList(giftSettings);
        }else {
            giftSettingMapper.insert(giftSetting);
        }
        result++;
        updateSelective(activity);
        return result;
    }

    @Override
    public Integer update(SaveActivityModel saveActivityModel, Long userId) {
        Integer result = 0;
        List<String> images = saveActivityModel.getImages();
        if(userId == null)
            throw new PermissionException("权限异常，用户没有登录!");
        Long activityId = saveActivityModel.getActivityId();
        if(activityId == null)
            throw new DataException("activityId is null.");
        Activity activity = findById(activityId);
        if(activity == null)
            throw new DataException("数据异常，活动不存在！");
//        boolean isManager = checkUserManagerPermission(userId);
//        if(!activity.getCreator().equals(userId) && !isManager)
//            throw new PermissionException();

        activity.setUpdater(userId);
        activity.setUpdateTime(new Date());
        ActivitySettingModel activitySetting = saveActivityModel.getActivitySetting();
        Date voteStart = null;
        Date voteEnd = null;
        if(activitySetting != null && activitySetting.getApplyStartTime() != null){
            activity.setActivityStartTime(activitySetting.getApplyStartTime());
            voteStart = activitySetting.getApplyStartTime();
        }
        if(activitySetting != null && activitySetting.getVoteEndTime() != null){
            activity.setActivityEndTime(new Date(activitySetting.getVoteEndTime().getTime()+(activitySetting.getEndGiftTime() != null?activitySetting.getEndGiftTime():0)));
            voteEnd = activitySetting.getVoteEndTime();
        }

        if(images != null && !images.isEmpty()){
            Example example = new Example(FileMaterial.class);
            example.createCriteria().andEqualTo("type",4)
                    .andEqualTo("relatedId",activity.getId()).andIsNull("star");
            fileMaterialMapper.deleteByExample(example);
            List<FileMaterial> fileMaterials = new ArrayList<>();
            for (String image : images) {
                FileMaterial fileMaterial = new FileMaterial();
                fileMaterial.setType(4);
                fileMaterial.setCreateTime(new Date());
                fileMaterial.setCreator(userId);
                fileMaterial.setFileUrl(image);
                fileMaterial.setRelatedId(activity.getId());
                fileMaterials.add(fileMaterial);
            }
            if(!fileMaterials.isEmpty())
                fileMaterialMapper.insertList(fileMaterials);
            activity.setActivityImage(images.get(0));
        }
        activity.setActivityStatus(getActivityStatus(activity.getActivityStartTime(),activity.getActivityEndTime(),voteStart,voteEnd));
        BeanUtil.copyPropertiesIgnoreNull(saveActivityModel,activity);
        if ((saveActivityModel.getActivityStatus() != null && saveActivityModel.getActivityStatus() == 0)
                || (saveActivityModel.getApprovalStatus() != null && saveActivityModel.getApprovalStatus() == 1)) {
            activity.setActivityStatus(0);
            activity.setApprovalStatus(1);
            activity.setIsDefaultStatus(1);
        }else if((saveActivityModel.getActivityStatus() != null && saveActivityModel.getActivityStatus() == 1)
                ||(saveActivityModel.getApprovalStatus() != null && saveActivityModel.getApprovalStatus() == 0)){
            activity.setApprovalStatus(0);
            activity.setActivityStatus(getActivityStatus(activity.getActivityStartTime(),activity.getActivityEndTime(),voteStart,voteEnd));
        }
        updateSelective(activity);
        result++;

        //update activity setting
        ActivitySetting dbActivitySet = new ActivitySetting();
        dbActivitySet.setActivityId(activityId);
        dbActivitySet = activitySettingMapper.selectOne(dbActivitySet);
        if(activitySetting != null && dbActivitySet != null){
            BeanUtil.copyPropertiesIgnoreNull(activitySetting,dbActivitySet);
            activitySettingMapper.updateByPrimaryKeySelective(dbActivitySet);
            result++;
        }

        //update status
        if(activity.getApprovalStatus() == 0){
            if(dbActivitySet != null && dbActivitySet.getApplyStartTime() != null){
                activity.setActivityStartTime(dbActivitySet.getApplyStartTime());
                voteStart = dbActivitySet.getApplyStartTime();
            }
            if(dbActivitySet != null && dbActivitySet.getVoteEndTime() != null){
                activity.setActivityEndTime(new Date(dbActivitySet.getVoteEndTime().getTime()+(dbActivitySet.getEndGiftTime() != null?dbActivitySet.getEndGiftTime():0)));
                voteEnd = dbActivitySet.getVoteEndTime();
            }
            activity.setActivityStatus(getActivityStatus(activity.getActivityStartTime(),activity.getActivityEndTime(),voteStart,voteEnd));
            updateSelective(activity);
        }

        if(activity.getApprovalStatus() == 1){
            //活动关闭 停止自动投票
            Example example = new Example(Player.class);
            example.createCriteria().andEqualTo("activityId",activity.getId());
            List<Player> players = playerMapper.selectByExample(example);
            for (Player player1 : players) {
                scheduleJobService.pauseScheduleJobForAutoVote(player1.getId());
            }
        }

        //update rule setting
        RuleSettingModel ruleSettingModel = saveActivityModel.getRuleSetting();
        RuleSetting dbRuleSetting = new RuleSetting();
        dbRuleSetting.setActivityId(activityId);
        dbRuleSetting = ruleSettingMapper.selectOne(dbRuleSetting);
        if(ruleSettingModel != null && dbRuleSetting != null){
            BeanUtil.copyPropertiesIgnoreNull(ruleSettingModel,dbRuleSetting);
            ruleSettingMapper.updateByPrimaryKeySelective(dbRuleSetting);
            result++;
        }

        //save vote setting
        VoteSettingModel voteSettingModel = saveActivityModel.getVoteSetting();
        VoteSetting dbVoteSetting = new VoteSetting();
        dbVoteSetting.setActivityId(activityId);
        dbVoteSetting = voteSettingMapper.selectOne(dbVoteSetting);
        if (voteSettingModel != null && dbVoteSetting != null){
            BeanUtil.copyPropertiesIgnoreNull(voteSettingModel,dbVoteSetting);
            voteSettingMapper.updateByPrimaryKeySelective(dbVoteSetting);
            result++;
        }

        //save interface setting
        InterfaceSettingModel interfaceSettingModel = saveActivityModel.getInterfaceSetting();
        InterfaceSetting dbInterfaceSetting = new InterfaceSetting();
        dbInterfaceSetting.setActivityId(activityId);
        dbInterfaceSetting = interfaceSettingMapper.selectOne(dbInterfaceSetting);
        if (interfaceSettingModel != null && dbInterfaceSetting != null){
            BeanUtil.copyPropertiesIgnoreNull(interfaceSettingModel,dbInterfaceSetting);
            interfaceSettingMapper.updateByPrimaryKeySelective(dbInterfaceSetting);
            activity.setMualViews(interfaceSettingModel.getVisualViews() != null?interfaceSettingModel.getVisualViews():activity.getMualViews());
            result++;
        }

        //save apply setting
        ApplySettingModel applySettingModel = saveActivityModel.getApplySetting();
        ApplySetting dbApplySetting = new ApplySetting();
        dbApplySetting.setActivityId(activityId);
        dbApplySetting = applySettingMapper.selectOne(dbApplySetting);
        if (applySettingModel != null && dbApplySetting != null){
            BeanUtil.copyPropertiesIgnoreNull(applySettingModel,dbApplySetting);
            applySettingMapper.updateByPrimaryKeySelective(dbApplySetting);
            result++;
        }

        //save gift setting
        GiftSettingModel giftSettingModel = saveActivityModel.getGiftSetting();
        if (giftSettingModel !=null){
            Example example = new Example(GiftSetting.class);
            example.createCriteria().andEqualTo("activityId",activity.getId());
            giftSettingMapper.deleteByExample(example);


            GiftSetting giftSetting = new GiftSetting();
            giftSetting.setCreateTime(new Date());
            giftSetting.setCreator(userId);
            giftSetting.setActivityId(activity.getId());
            BeanUtil.copyPropertiesIgnoreNull(giftSettingModel,giftSetting);
            giftSetting.setGiftPage(giftSettingModel.getGiftPage() == null?1:giftSettingModel.getGiftPage());
            giftSetting.setGiftPrice(new BigDecimal(1));//默认值
            giftSetting.setGiftVotes(3);//默认值
            giftSetting.setGiftName("钻石");//不可修改
            List<BigDecimal> giftModels = giftSettingModel.getGiftPrice();
            List<GiftSetting> giftSettings = new ArrayList<>();
            if(giftModels != null && !giftModels.isEmpty()){
                for (BigDecimal price : giftModels) {
                    GiftSetting tempGift = new GiftSetting();
                    BeanUtil.copyPropertiesIgnoreNull(giftSetting,tempGift);
                    tempGift.setGiftPrice(price);
                    tempGift.setGiftVotes(Integer.valueOf(String.valueOf(new BigDecimal(3).multiply(price))));//票数为价格的三倍
                    giftSettings.add(tempGift);
                }
                if(!giftSettings.isEmpty())
                    giftSettingMapper.insertList(giftSettings);
            }else {
                giftSettingMapper.insert(giftSetting);
            }
            result++;
        }
        updateSelective(activity);
        return result;
    }

    /**
     *  检查用户是否有管理员权限
     * @param userId
     * @return
     */
    private boolean checkUserManagerPermission(Long userId){
//        List<Role> byUserId = roleMapper.findByUserId(userId);
//        if(byUserId == null || byUserId.isEmpty())
//            return false;
//        for (Role role : byUserId) {
//            Integer isManager = role.getIsManager();
//            if(isManager.equals(Role.Manager.MANAGER.getValue()))
//                return true;
//        }
        User user = userService.findById(userId);
        if (user.getRole().equals(User.Role.SYSTEM.getRemark()))
            return true;
        return false;
    }

    @Override
    public Integer delete(List<Long> ids, Long userId) {
        Integer result = 0;
//        boolean isManager = checkUserManagerPermission(userId);
        for (Long id : ids) {
            Activity activity = findById(id);
            if(activity == null)
                throw new DataException("选中的部分活动已被删除！");
//            if(!activity.getCreator().equals(userId) && !isManager)
//                throw new PermissionException();
            deleteImages(id,1);
            result += deleteById(id);

            Example example = new Example(Player.class);
            example.createCriteria().andEqualTo("activityId",id);
            List<Player> players = playerMapper.selectByExample(example);
            //删除选手图片
            players.forEach(it -> deleteImages(it.getId(),2));
            //删除相关的选手的投票定时任务
            for (Player player1 : players) {
                scheduleJobService.deleteScheduleJobForAutoVote(player1.getId());
            }
            //删除选手
            playerMapper.deleteByExample(example);

            //delete activity setting
            ActivitySetting activitySetting = new ActivitySetting();
            activitySetting.setActivityId(id);
            activitySettingMapper.delete(activitySetting);
            RuleSetting ruleSetting = new RuleSetting();
            ruleSetting.setActivityId(id);
            ruleSettingMapper.delete(ruleSetting);
            VoteSetting voteSetting = new VoteSetting();
            voteSetting.setActivityId(id);
            voteSettingMapper.delete(voteSetting);
            InterfaceSetting interfaceSetting = new InterfaceSetting();
            interfaceSetting.setActivityId(id);
            interfaceSettingMapper.delete(interfaceSetting);
            ApplySetting applySetting = new ApplySetting();
            applySetting.setActivityId(id);
            applySettingMapper.delete(applySetting);
            GiftSetting giftSetting = new GiftSetting();
            giftSetting.setActivityId(id);
            giftSettingMapper.delete(giftSetting);
        }

        return result;
    }

    /**
     * 删除服务器中图片
     * @param id
     */
//    @Async
    @Override
    public void deleteImages(Long id,Integer type){
        String reUrl = "";
        if(type == 1){
            Activity activity = findById(id);
            if(activity != null){
                reUrl = activity.getActivityImage();
            }
        }else if(type == 2){
            Player player = playerService.findById(id);
            if(player != null){
                reUrl = player.getImage();
            }
        }
        if(reUrl != null && !reUrl.equals("")){
            if (!reUrl.contains("/image")) {
                String fileId = reUrl.substring(reUrl.lastIndexOf("/")+1,reUrl.length());
                uploadFileService.removeFile(fileId);
            }else{
                String subFileUrl = reUrl.substring(reUrl.indexOf("/image") + 7, reUrl.length());
                fileService.remove(subFileUrl);
            }
        }
        Example example = new Example(FileMaterial.class);
        example.createCriteria().andEqualTo("relatedId",id);
        List<FileMaterial> fileMaterials = fileMaterialMapper.selectByExample(example);
        for (FileMaterial fileMaterial : fileMaterials) {
            String fileUrl = fileMaterial.getFileUrl();
            try {
                if (fileUrl != null && !fileUrl.contains("/image")) {
                    String fileId = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.length());
                    uploadFileService.removeFile(fileId);
                }else if(fileUrl != null){
                    String subFileUrl = fileUrl.substring(fileUrl.indexOf("/image") + 7, fileUrl.length());
                    fileService.remove(subFileUrl);
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public Integer updateViews(UpdateViewsModel viewsModel, Long userId)  {
        if(userId == null)
            throw new PermissionException("权限异常，用户没有登录!");
//        Long userPermission = roleService.getUserPermission(userId);
//        if(userPermission == 3)
//            throw new PermissionException();
        Long activityId = viewsModel.getActivityId();
        if(activityId == null)
            throw new DataException("activityId is null.");
        Activity activity = findById(activityId);
        if(activity == null)
            throw new DataException("数据异常，活动不存在！");
        activity.setMualViews((activity.getMualViews() != null?activity.getMualViews():0) + (viewsModel.getViews() != null?viewsModel.getViews():0));
        Integer integer = updateSelective(activity);
        return integer;
    }

    @Override
    public Integer batchUpdate(BatchUpdateModel batchUpdateModel, Long userId) {
        Integer result = 0;
//        boolean isManager = checkUserManagerPermission(userId);
        List<Long> activityIds = batchUpdateModel.getIds();
        if (activityIds == null || activityIds.isEmpty())
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR);
        for (Long id : activityIds) {
            Activity activity = findById(id);
            if(activity == null)
                throw new DataException("选中的部分活动已被删除，批量更新失败！");
//            if(!activity.getCreator().equals(userId) && !isManager)
//                throw new PermissionException();
            activity.setActivityRemark(batchUpdateModel.getRemark() == null?activity.getActivityRemark():batchUpdateModel.getRemark());
            activity.setActivityRemarkSec(batchUpdateModel.getRemarkSec() == null?activity.getActivityRemarkSec():batchUpdateModel.getRemarkSec());
            result += updateSelective(activity);
        }
        return result;
    }

    @Override
    public Integer addVotes(Long activityId, Long userId) {
        if(activityId == null){
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"activityId is null");
        }
        Long userPermission = roleService.getUserPermission(userId);
        if(userPermission == 3)
            throw new PermissionException();
        Activity activity = findById(activityId);

        Example example = new Example(Player.class);
        example.createCriteria().andEqualTo("activityId",activityId);
        List<Player> players = playerService.selectByExample(example);

        //随机加票 从1到5随机一个数作为票数随机加到小于10名的选手中，要是选手小于10 则都加
        if (!players.isEmpty()){
            Integer mulVotes = 0;
            while (mulVotes == 0){
                mulVotes = (int) (Math.random() * 5);
            }
            Integer sum = 0;
            Integer limit = players.size() <= 10?players.size():10;
            while (sum <= limit) {
                int index = (int)(Math.random() * players.size());
                Player player = players.get(index);
                player.setManualVotes(player.getManualVotes() + mulVotes);
                sum += playerService.update(player);
            }
        }
        VotesModel votesByActivity = playerService.getVotesByActivity(activityId);

        Integer integer = 0;
        if(votesByActivity != null){
            activity.setUpdater(userId);
            activity.setUpdateTime(new Date());
            activity.setMualVotes(votesByActivity.getMualVotes());
            activity.setActVotes(votesByActivity.getActualVotes());
             integer = updateSelective(activity);
        }
        return integer;
    }

    @Override
    public void updateActivityStatus() {
        logger.info("开始更新活动状态");
        int sum = 0;
        List<ActivityDateModel> allActivityDate = activityMapper.getAllActivityDate();
        if(allActivityDate != null && !allActivityDate.isEmpty()){
            for (ActivityDateModel activityDateModel : allActivityDate) {
                Integer activityStatus = getActivityStatus(activityDateModel.getActivetyStartTime(),
                        activityDateModel.getActivityEndTime(),
                        activityDateModel.getVoteStartTime(),
                        activityDateModel.getVoteEndTime());
                if(activityStatus != null){
                    Activity activity = new Activity();
                    activity.setId(activityDateModel.getActivityId());
                    activity.setActivityStatus(activityDateModel.getApprovalStatus() != 1?activityStatus:0);
                    activity.setUpdateTime(new Date());
                    activity.setUpdater(0L);//0为系统更新
                    sum += updateSelective(activity);
                }else {
                    logger.warn("活动id为{}的活动日期数据异常！",activityDateModel.getActivityId());
                }

                //活动结束后 停止自动投票
                if(activityDateModel.getActivityEndTime() != null &&
                        System.currentTimeMillis() > activityDateModel.getActivityEndTime().getTime()){
                    Example example = new Example(Player.class);
                    example.createCriteria().andEqualTo("activityId",activityDateModel.getActivityId());
                    List<Player> players = playerMapper.selectByExample(example);
                    for (Player player1 : players) {
                        scheduleJobService.pauseScheduleJobForAutoVote(player1.getId());
                    }
                }
            }
        }
        logger.info("更新结束，总共更新{}条数据",sum);
    }

    @Override
    public void updateTodayStar() {
        logger.info("10:00 系统开始更新今日之星");
        Integer sum = 0;
        List<ActivityStarModel> activityStar = activityMapper.getActivityStar();
        if(activityStar != null && !activityStar.isEmpty()){
            for (ActivityStarModel activityStarModel : activityStar) {
                if(activityStarModel.getPlayerId() != null
                        && activityStarModel.getActivityId() != null){
                    Activity activity = new Activity();
                    activity.setId(activityStarModel.getActivityId());
                    activity.setUpdateTime(new Date());
                    activity.setStar(activityStarModel.getPlayerId());
                    activity.setUpdater(0L);//0为系统更新
                    sum += updateSelective(activity);
                    playerService.star(activityStarModel.getPlayerId(),0L);
                }
            }
        }
        logger.info("今日选出{}个今日之星，更新结束",sum);
    }

    /**
     * 获取活动的状态
     * @param startTime
     * @param endTime
     * @param voteStart
     * @param voteEnd
     * @return
     */
    private Integer getActivityStatus(Date startTime,Date endTime,Date voteStart,Date voteEnd){
        Date currentDate = new Date();
        if(startTime == null || endTime == null || voteStart == null || voteEnd == null)
            return null;
        int startDiffDays = TimeSearchUtil.getDiffDays(currentDate, startTime);
        long startDateDiff = TimeSearchUtil.dateDiff(currentDate, startTime);
        int endDiffDays = TimeSearchUtil.getDiffDays(currentDate, endTime);
        long endDateDiff = TimeSearchUtil.dateDiff(currentDate, endTime);
//        if(startDiffDays == 0 && startDateDiff > 0){
//            return Activity.Status.STARTTODAY.getValue();
        if(startDateDiff < 0 && endDateDiff > 0){
            return Activity.Status.PROCESSING.getValue();
        }else if(startDateDiff > 0){
            return Activity.Status.NOTSTART.getValue();
//        }else if(endDiffDays == 0 && endDateDiff > 0){
//            return Activity.Status.OVERTODAY.getValue();
        }else if(endDateDiff < 0) {
            return Activity.Status.OVER.getValue();
        }
//        }else if(currentDate.getTime() < voteEnd.getTime()
//                && currentDate.getTime() > voteStart.getTime()){
//            return Activity.Status.REGISTRATING.getValue();
//        }
        return null;
    }

    @Override
    public QrCodeModel getActivityAccessLink(Long activityId,Long userId) {
        String domain = "";
        Map<String, String> basic = systemParamService.basic();
        String platformDomain = basic.get("platformDomain");
        if(platformDomain == null || platformDomain.equals(""))
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"平台域名配置异常！");
        domain = platformDomain.substring(platformDomain.indexOf("."),platformDomain.length())+"#/home";
        Example example = new Example(FileMaterial.class);
        example.createCriteria().andEqualTo("type",2);
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
            String finalDomain ="http://"+randomDomain + (value != null?("." + value):"") + domain + "?activityId=" + activityId;
            qrCodeModel.setAccessUrl(finalDomain);
            File qrFile = File.createTempFile("qrJpg",".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(qrFile);

            QrCodeCreateUtil.createQrCode(fileOutputStream,finalDomain,900,"JPEG");
            UploadFile uploadFile = uploadFileService.uploadFile(qrFile);
            qrCodeModel.setQrCodeUrl(uploadFile.getUrl());

            FileMaterial fileMaterial = new FileMaterial();
            fileMaterial.setFileUrl(uploadFile.getUrl());
            fileMaterial.setType(2);
            fileMaterial.setRelatedId(activityId);
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
    public H5HomePageModel getH5HomePage(Long activityId, HttpServletRequest request) {
        //更新实际浏览量
        Activity activity = findById(activityId);
        if(activity == null)
            throw new XzcmBaseRuntimeException(CodeMessage.DATAERROR,"活动不存在！");
        activity.setActViews((activity.getActViews() == null?0:activity.getActViews()) + 1);
        updateSelective(activity);

        H5HomePageModel h5HomePageModel = new H5HomePageModel();
        h5HomePageModel = activityMapper.getH5HomePage(activityId);
        H5PlayerDetailModel h5PlayerDetailModel = null;
        List<String> images = new ArrayList<>();
        if(h5HomePageModel.getStarPlayer() != null){
            h5PlayerDetailModel = playerService.getH5PlayerDetail(h5HomePageModel.getStarPlayer());
            Player starPlayer = playerService.findById(h5HomePageModel.getStarPlayer());
            if(starPlayer != null && starPlayer.getImage() != null)
                images.add(starPlayer.getImage());
        }
        h5HomePageModel.setTodayPlayer(h5PlayerDetailModel);
        List<String> images1 = fileMaterialMapper.getImages(activityId, 4);
        images.addAll(images1);
        h5HomePageModel.setImages(images);
        List<Player> popularityPlayers = playerMapper.getPopularityPlayers(activityId);
        List<Player> latestPlayers = playerMapper.getLatestPlayers(activityId);
        List<H5PlayerDetailModel> popus = new ArrayList<>();
        if(popularityPlayers != null && !popularityPlayers.isEmpty()){
            popularityPlayers.forEach(it -> {
                H5PlayerDetailModel h5PlayerDetail = playerService.getH5PlayerDetail(it.getId());
                popus.add(h5PlayerDetail);
            });
        }
        List<H5PlayerDetailModel> result = popus.stream().sorted(Comparator.comparing(H5PlayerDetailModel::getRank)).collect(Collectors.toList());
        List<H5PlayerDetailModel> last = new ArrayList<>();
        if(latestPlayers != null && !latestPlayers.isEmpty()){
            latestPlayers.forEach(it -> {
                H5PlayerDetailModel h5PlayerDetail = playerService.getH5PlayerDetail(it.getId());
                last.add(h5PlayerDetail);
            });
        }
        h5HomePageModel.setPopularityPlayers(result);
        h5HomePageModel.setLatestPlayers(last);
        InterfaceSetting interfaceSetting = new InterfaceSetting();
        interfaceSetting.setActivityId(activityId);
        interfaceSetting = interfaceSettingMapper.selectOne(interfaceSetting);
        h5HomePageModel.setThemeColor(interfaceSetting.getThemeColor());
        return h5HomePageModel;
    }

    public List<H5PlayerDetailModel> getPopularityPlayers(PlayerPageModel playerPageModel){
        Integer pageNum = playerPageModel.getPage()==null?1:playerPageModel.getPage();
        Integer pageSize = playerPageModel.getSize()==null?10:playerPageModel.getSize();
        Integer limitStart = pageSize*(pageNum - 1);
        Integer limitEnd = limitStart+pageSize;
        List<Player> popularityPlayers = playerMapper.getPopularityPlayersPage(playerPageModel.getActivityId(),limitStart,limitEnd);
        List<H5PlayerDetailModel> popus = new ArrayList<>();
        if(popularityPlayers != null && !popularityPlayers.isEmpty()){
            popularityPlayers.forEach(it -> {
                H5PlayerDetailModel h5PlayerDetail = playerService.getH5PlayerDetail(it.getId());
                popus.add(h5PlayerDetail);
            });
        }
        List<H5PlayerDetailModel> result = popus.stream().sorted(Comparator.comparing(H5PlayerDetailModel::getRank)).collect(Collectors.toList());
        return result;
    }

    @Override
    public SaveActivityModel getActivityDetail(Long activityId, Long userId,Integer type) {
        SaveActivityModel saveActivityModel = new SaveActivityModel();
        Activity activity = findById(activityId);
        BeanUtil.copyPropertiesIgnoreNull(activity,saveActivityModel);
        saveActivityModel.setActivityId(activityId);
        ActivitySetting activitySetting = new ActivitySetting();
        activitySetting.setActivityId(activityId);
        activitySetting = activitySettingMapper.selectOne(activitySetting);
        ActivitySettingModel activitySettingModel = new ActivitySettingModel();
        BeanUtil.copyPropertiesIgnoreNull(activitySetting,activitySettingModel);
        saveActivityModel.setActivitySetting(activitySettingModel);
        RuleSetting ruleSetting = new RuleSetting();
        ruleSetting.setActivityId(activityId);
        ruleSetting =  ruleSettingMapper.selectOne(ruleSetting);
        RuleSettingModel ruleSettingModel = new RuleSettingModel();
        BeanUtil.copyPropertiesIgnoreNull(ruleSetting,ruleSettingModel);
        saveActivityModel.setRuleSetting(ruleSettingModel);
        VoteSetting voteSetting = new VoteSetting();
        voteSetting.setActivityId(activityId);
        voteSetting = voteSettingMapper.selectOne(voteSetting);
        VoteSettingModel voteSettingModel = new VoteSettingModel();
        BeanUtil.copyPropertiesIgnoreNull(voteSetting,voteSettingModel);
        saveActivityModel.setVoteSetting(voteSettingModel);
        InterfaceSetting interfaceSetting = new InterfaceSetting();
        interfaceSetting.setActivityId(activityId);
        interfaceSetting = interfaceSettingMapper.selectOne(interfaceSetting);
        InterfaceSettingModel interfaceSettingModel = new InterfaceSettingModel();
        BeanUtil.copyPropertiesIgnoreNull(interfaceSetting,interfaceSettingModel);
        saveActivityModel.setInterfaceSetting(interfaceSettingModel);
        ApplySetting applySetting = new ApplySetting();
        applySetting.setActivityId(activityId);
        applySetting = applySettingMapper.selectOne(applySetting);
        ApplySettingModel applySettingModel = new ApplySettingModel();
        BeanUtil.copyPropertiesIgnoreNull(applySetting,applySettingModel);
        saveActivityModel.setApplySetting(applySettingModel);
        GiftSetting giftSetting = new GiftSetting();
        giftSetting.setActivityId(activityId);
        List<GiftSetting> select = giftSettingMapper.select(giftSetting);
        List<BigDecimal> bigDecimals = new ArrayList<>();
        for (GiftSetting setting : select) {
            bigDecimals.add(setting.getGiftPrice());
        }
        GiftSettingModel giftSettingModel = new GiftSettingModel();
        BeanUtil.copyPropertiesIgnoreNull(!select.isEmpty() && select.get(0)!=null?select.get(0):giftSetting,giftSettingModel);
        giftSettingModel.setGiftPrice(bigDecimals);
        saveActivityModel.setGiftSetting(giftSettingModel);
        List<String> images = new ArrayList<>();
        if(type != null){
            fileMaterialMapper.getImagesForAdmin(activityId,4);
        }else {
            images = fileMaterialMapper.getImages(activityId, 4);
        }
        saveActivityModel.setImages(images);

        return saveActivityModel;
    }

    @Override
    public List<GiftLog> getGiftLog(Long activityId,Long userId,Boolean status,Integer self) {
//        boolean isManage = roleService.checkUserManagerPermission(userId);
//        return giftLogMapper.getGiftLog(activityId,isManage?null:userId);
        User user = userService.findById(userId);
        return giftLogMapper.getGiftLog(activityId,userId,user.getRoleValue(),status,self);
    }

    @Override
    public List<VoteLog> getVoteLog(Long activityId,Long userId,Integer self) {
//        boolean isManage = roleService.checkUserManagerPermission(userId);
//        return voteLogMapper.getVoteLog(activityId,isManage?null:userId);
        User user = userService.findById(userId);
        return voteLogMapper.getVoteLog(activityId,userId,user.getRoleValue(),self);
    }

    @Override
    public List<GiftSetting> getGift(Long activityId) {
        Example example = new Example(GiftSetting.class);
        example.createCriteria().andEqualTo("activityId",activityId);
        List<GiftSetting> giftSettings = giftSettingMapper.selectByExample(example);
        return giftSettings;
    }

    @Override
    public Map<String, Object> hasJoin(Long activityId, String openId) {
        Map<String,Object> result = new HashMap<>();
        Long hasJoin = playerMapper.hasJoin(activityId, openId);
        result.put("hasJoin",hasJoin != null ? 0 : 1);
        if(hasJoin != null)
            result.put("playerId",hasJoin);
        return result;
    }

    @Override
    public List<FileMaterial> getDeleteModel(Long userId) {
        List<Long> deleteFileModels = new ArrayList<>();
        User user = userService.findById(userId);
        List<DeleteFileModel> deleteModel = activityMapper.getDeleteModel(userId, user.getRoleValue());
        Map<Long,List<Long>> map = new HashMap<>();
        for (DeleteFileModel deleteFileModel : deleteModel) {
            List<Long> players = new ArrayList<>();
            if (map.containsKey(deleteFileModel.getActivityId())) {
                List<Long> list = map.get(deleteFileModel.getActivityId());
                list.add(deleteFileModel.getPlayerId());
            }else {
                players.add(deleteFileModel.getPlayerId());
                map.put(deleteFileModel.getActivityId(),players);
            }
        }
        for (Long aLong : map.keySet()) {
            deleteFileModels.add(aLong);
            deleteFileModels.addAll(map.get(aLong));
        }
        List<FileMaterial> delete = fileMaterialMapper.getDelete(deleteFileModels);
        for (FileMaterial fileMaterial : delete) {
            fileMaterialMapper.delete(fileMaterial);
        }
        return delete;
    }

    @Override
    public TodayDealsModel getTodayDeals(Long userId,Integer self) {
        User user = userService.findById(userId);
        if(self != null)
            return giftLogMapper.getTodayDealsSelf(userId,user.getRoleValue());
        return giftLogMapper.getTodayDeals(userId,user.getRoleValue());
    }
}
