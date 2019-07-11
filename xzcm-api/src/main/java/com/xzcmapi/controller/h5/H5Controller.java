package com.xzcmapi.controller.h5;

import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.*;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.FileMaterialMapper;
import com.xzcmapi.mapper.InterfaceSettingMapper;
import com.xzcmapi.model.*;
import com.xzcmapi.service.ActivityService;
import com.xzcmapi.service.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequestMapping("/api/h5")
@RestController
@Api(description = "h5页面相关接口")
public class H5Controller extends BaseController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private FileMaterialMapper fileMaterialMapper;
    @Autowired
    private InterfaceSettingMapper interfaceSettingMapper;

    private static Lock lock = new ReentrantLock();

    @GetMapping("/getH5HomePage")
    @ApiOperation(value = "h5活动首页",notes = "h5活动首页")
    public ResponseResult<H5HomePageModel> getH5HomePage(HttpServletRequest request,
                                                         @RequestParam("activityId")Long activityId){
        try{
            H5HomePageModel h5HomePage = activityService.getH5HomePage(activityId,request);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,h5HomePage);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/getPopularityPlayers")
    @ApiOperation(value = "人气选手",notes = "人气选手")
    public ResponseResult<H5HomePageModel> getPopularityPlayers(HttpServletRequest request,
                                                         @RequestBody PlayerPageModel playerPageModel){
        try{
            List<H5PlayerDetailModel> popularityPlayers = activityService.getPopularityPlayers(playerPageModel);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,popularityPlayers);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @GetMapping("/getH5PlayerDetail")
    @ApiOperation(value = "查看h5选手详情",notes = "查看h5选手详情")
    public ResponseResult getH5PlayerDetail(javax.servlet.http.HttpServletRequest request, @RequestParam("playerId")Long playerId ){
        try{
            H5PlayerDetailModel h5PlayerDetail = playerService.getH5PlayerDetail(playerId);
            return ResponseResult.toResponseResult(h5PlayerDetail);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @GetMapping("/getPlayerRank")
    @ApiOperation(value = "h5--获取选手排名",notes = "h5--获取选手排名")
    public ResponseResult getPlayerRank(javax.servlet.http.HttpServletRequest request,
                                        @RequestParam("activityId")Long activityId){
        try{
            List<PlayerRankModel> playerRank = playerService.getPlayerRank(activityId);
            return ResponseResult.toResponseResult(playerRank);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PutMapping("/vote")
    @ApiOperation(value = "h5--为选手投票",notes = "h5--为选手投票")
    public ResponseResult vote(javax.servlet.http.HttpServletRequest request,
                               @RequestBody H5VoteModel h5VoteModel){
        try{
            lock.lockInterruptibly();
            H5PlayerDetailModel vote = playerService.vote(request, h5VoteModel,1,false);
            return ResponseResult.toResponseResult(vote);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }finally {
            lock.unlock();
        }
    }

    @PutMapping("/gift")
    @ApiOperation(value = "h5--为选手送礼物",notes = "h5--为选手送礼物")
    public ResponseResult gift(javax.servlet.http.HttpServletRequest request,
                               @RequestBody H5GiftModel h5GiftModel){
        try{
            Map<String, Object> gift = playerService.gift(request, h5GiftModel);
            return ResponseResult.toResponseResult(gift);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PutMapping("/shareCallback")
    @ApiOperation(value = "h5--分享后回调统计",notes = "h5--分享后回调统计")
    public ResponseResult shareCallback(javax.servlet.http.HttpServletRequest request,
                               @RequestBody ShareCallbackModel shareCallbackModel){
        try{
            Integer callback = playerService.shareCallback(request, shareCallbackModel);
            return ResponseResult.toResponseResult(callback);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PutMapping("/ipRecord")
    @ApiOperation(value = "h5--ip量统计",notes = "h5--ip量统计")
    public ResponseResult ipRecord(javax.servlet.http.HttpServletRequest request,
                                        @RequestBody ShareCallbackModel shareCallbackModel){
        try{
            Long playerId = shareCallbackModel.getPlayerId();
            if(playerId == null)
                return new ResponseResult(true,CodeMessage.PARAMSERROR.getMsg(),CodeMessage.PARAMSERROR.getCode());
            Player byId = playerService.findById(playerId);
            if(byId == null)
                return new ResponseResult(true,CodeMessage.DATAERROR.getMsg(),CodeMessage.DATAERROR.getCode());
            byId.setIpAmount(byId.getIpAmount() == null?1:byId.getIpAmount()+1);
            Integer integer = playerService.updateSelective(byId);
            return ResponseResult.toResponseResult(integer);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @GetMapping("getHomeMessage")
    @ApiOperation(value = "获取首页轮播信息",notes = "获取首页轮播信息")
    public ResponseResult getHomeMessage(@RequestParam("activityId")Long activityId){
        Activity activity = activityService.findById(activityId);
        if(activity == null)
            return new ResponseResult(true,"活动不存在！",500);
        boolean isOver = true;
        if(activity != null && activity.getActivityStatus() != null && !activity.getActivityStatus().equals(Activity.Status.OVER.getValue()))
            isOver = false;
        Integer isDefault = activity.getIsDefaultStatus();
        Integer approvalStatus = activity.getApprovalStatus();
        InterfaceSetting interfaceSetting = new InterfaceSetting();
        interfaceSetting.setActivityId(activityId);
        interfaceSetting = interfaceSettingMapper.selectOne(interfaceSetting);
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activity.getActivityName());
        map.put("themeColor",interfaceSetting.getThemeColor());
        map.put("homeMessage",interfaceSetting.getAnnouncement());
        map.put("overStatus",isOver);
        map.put("defaultStatus",(isDefault == 0 && approvalStatus == 1)?true:false);
        map.put("mualStatus",(isDefault == 1 && approvalStatus == 1)?true:false);
        map.put("image",activity.getActivityImage());
        return ResponseResult.toResponseResult(map);
    }

    @GetMapping("getImages")
    @ApiOperation(value = "获取轮播图片",notes = "获取轮播图片")
    public ResponseResult getImages(@RequestParam("activityId")Long activityId){
        List<String> images = fileMaterialMapper.getImages(activityId, 4);
        Integer hasStar = fileMaterialMapper.getHasStar(activityId, 4);
        Map<String,Object> map = new HashMap<>();
        map.put("images", images);
        map.put("hasStar", hasStar != null && hasStar > 0 );
        return ResponseResult.toResponseResult(map);
    }

    @GetMapping("/getGift")
    @ApiOperation(value = "获取活动礼物",notes = "获取活动礼物")
    public ResponseResult<GiftSetting> getGift(HttpServletRequest request,
                                           @RequestParam(value = "activityId",required = false)Long activityId){
        try{
            List<GiftSetting> gift = activityService.getGift(activityId);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,gift);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }

    }

    @GetMapping("/hasJoin")
    @ApiOperation(value = "是否已经报名",notes = "是否已经报名")
    public ResponseResult<GiftSetting> hasJoin(HttpServletRequest request,
                                               @RequestParam(value = "activityId",required = false)Long activityId,
                                               @RequestParam(value = "openId",required = false)String openId){
        try{
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,activityService.hasJoin(activityId,openId));
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }

    }
}
