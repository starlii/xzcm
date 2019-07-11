package com.xzcmapi.controller.activity;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.Log;
import com.xzcmapi.entity.Player;
import com.xzcmapi.entity.VoteLog;
import com.xzcmapi.exception.DataException;
import com.xzcmapi.exception.PermissionException;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.*;
import com.xzcmapi.service.PlayerService;
import com.xzcmapi.util.xss.XssHttpServletRequestWrapper;
import io.swagger.annotations.*;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/player")
@RestController
@Api(description = "选手管理")
public class PlayerController extends BaseController {

    @Autowired
    private PlayerService playerService;

    @PostMapping("/add")
    @ApiOperation(value = "新增选手",notes = "新增选手")
    public ResponseResult add(HttpServletRequest request
            , @RequestBody @ApiParam("新增选手实体")SavePlayerModel player){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Player add = playerService.add(player, userId);
            return new ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,add);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }


    @DeleteMapping("/delete/{playerId}")
    @Authorization
    @ApiOperation(value = "删除选手",notes = "删除选手")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public ResponseResult delete(HttpServletRequest request
            ,@PathVariable("playerId")Long playerId){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer delete = playerService.delete(playerId, userId);
            return new ResponseResult(false,SUCCESS_DELETE_MESSAGE,SUCCESS_CODE,delete);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),e.getMessage());
        } catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/batchDelete")
    @Authorization
    @ApiOperation(value = "批量删除选手",notes = "批量删除选手")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public ResponseResult batchDelete(HttpServletRequest request
            ,@RequestBody BatchUpdateModel  playerIds){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer delete = playerService.batchDelete(playerIds.getIds(), userId);
            return new ResponseResult(false,SUCCESS_DELETE_MESSAGE,SUCCESS_CODE,delete);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),e.getMessage());
        } catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/update")
    @Authorization
    @ApiOperation(value = "更新选手",notes = "更新选手")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public ResponseResult update(HttpServletRequest request
            ,@RequestBody @ApiParam("更新选手实体") PlayerModel player){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer update = playerService.update(player, userId);
            return new ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,update);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true
                    ,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }


    @PostMapping("/get")
    @ApiOperation(value = "获取选手列表",notes = "获取选手列表")
    public ResponseResult<PageInfo<List<PlayerModel>>> get(HttpServletRequest request,
                              @RequestBody @ApiParam("选手搜索参数") PlayerSearchModel search){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            PageInfo<List<PlayerModel>> listPageInfo = playerService.get(search, userId,false);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,listPageInfo);
        }catch(XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PostMapping("/approvaling")
    @Authorization
    @ApiOperation(value = "待审核选手列表",notes = "待审核选手列表")
    public ResponseResult<PageInfo<List<PlayerModel>>> approvaling(HttpServletRequest request,
                                                           @RequestBody @ApiParam("选手搜索参数") PlayerSearchModel search,
                                                                   @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            PageInfo<List<PlayerModel>> listPageInfo = playerService.get(search, userId,true);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,listPageInfo);
        }catch(XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/muaUpdate")
    @Authorization
    @ApiOperation(value = "手动更新票数,礼物，浏览量",notes = "手动更新票数,礼物，浏览量")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public ResponseResult batchUpdate(HttpServletRequest request
            ,@RequestBody @ApiParam("更新选手") BatchPlayerModel player){
        try{
            Integer views = player.getViews();
            BigDecimal gift = player.getGift();
            Integer votes = player.getVotes();
//            if((views != null && views <=0) ||
//                    (gift != null && gift.compareTo(new BigDecimal(0)) <= 0)
//                    ||(votes != null && votes <= 0))
//                return new ResponseResult(true,"更新失败，更新的数值不能小于或者等于零", INTERNAL_ERROR);
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer update = playerService.update(player, userId,request);
            return new ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,update);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true
                    ,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }


    @PutMapping("/batchUpdate")
    @ApiOperation(value = "批量更新选手备注",notes = "批量更新选手备注")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult batchUpdate(org.apache.catalina.servlet4preview.http.HttpServletRequest request,
                                      @RequestBody @ApiParam(value = "更新活动参数") BatchUpdateModel batch){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer integer = playerService.batchUpdate(batch, userId);
            return ResponseResult.toResponseResult(integer);
        }catch (PermissionException e){
            return new ResponseResult(true
                    ,FAILURE_UPDATE_MESSAGE
                    ,e.getCodeMessage().getCode()
                    ,e.getMessage());
        }catch (DataException e){
            return new ResponseResult(true
                    ,FAILURE_UPDATE_MESSAGE
                    ,e.getCodeMessage().getCode()
                    ,e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true
                    ,FAILURE_UPDATE_MESSAGE
                    ,INTERNAL_ERROR
                    ,e.getMessage());
        }
    }

    @PutMapping("/star")
    @Authorization
    @ApiOperation(value = "设置今日之星",notes = "设置今日之星")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public ResponseResult star(HttpServletRequest request
            ,@RequestParam("playerId") @ApiParam("更新选手")Long playerId){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer update = playerService.star(playerId, userId);
            return new ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,update);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true
                    ,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/oneClickAdded")
    @Authorization
    @ApiOperation(value = "一键当页随机加票",notes = "一键当页随机加票")
    //针对当前页面随机加票，
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public ResponseResult oneClickAdded(HttpServletRequest request,@RequestBody @ApiParam("一键加票参数，activityId必传、页数必传") PlayerSearchModel search){
            try{
                Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
                Integer integer = playerService.oneClickAdded(search, userId);
                return ResponseResult.toResponseResult(integer);
            }catch (XzcmBaseRuntimeException e){
                return new ResponseResult(true
                        ,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
            }catch (Exception e){
                return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
            }
    }

    @GetMapping("/getPlayerAccessLink")
    @ApiOperation(value = "查看选手的访问连接",notes = "查看选手的访问连接")
    public ResponseResult getPlayerAccessLink(org.apache.catalina.servlet4preview.http.HttpServletRequest request,
                                              @RequestParam("playerId")@ApiParam("选手id") Long playerId){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            QrCodeModel activityAccessLink = playerService.getPlayerAccessLink(playerId,userId);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,activityAccessLink);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }



    @GetMapping("/getVoteLog")
    @Authorization
    @ApiOperation(value = "数据记录--投票记录",notes = "数据记录--投票记录")
    public ResponseResult getVoteLog(HttpServletRequest request,
                                        @RequestParam("playerId")Long playerId,
                                     @RequestParam(value = "pageNum",required = false)Integer pageNum,
                                     @RequestParam(value = "pageSize",required = false)Integer pageSize,
                                     @RequestParam(value = "sort",required = false)String sort,
                                     @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
            PageHelper.startPage(pageNum == null?1:pageNum,pageSize == null?30:pageSize,sort);
            List<VoteLog> voteLog = playerService.getVoteLog(playerId, userId);
            return ResponseResult.toResponseResult(new PageInfo<>(voteLog));
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @GetMapping("/getGiftLog")
    @Authorization
    @ApiOperation(value = "数据记录--礼物记录",notes = "数据记录--礼物记录")
    public ResponseResult<GiftLog> getGiftLog(HttpServletRequest request,
                                     @RequestParam("playerId")Long playerId,
                                              @RequestParam(value = "pageNum",required = false)Integer pageNum,
                                              @RequestParam(value = "pageSize",required = false)Integer pageSize,
                                              @RequestParam(value = "sort",required = false)String sort,
                                     @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
            PageHelper.startPage(pageNum == null?1:pageNum,pageSize == null?30:pageSize, sort);
            List<GiftLog> giftLog = playerService.getGiftLog(playerId, userId);
            return ResponseResult.toResponseResult(new PageInfo<>(giftLog));
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }


    @PutMapping("/status")
    @Authorization
    @ApiOperation(value = "更新选手的相关状态",notes = "更新选手的相关状态")
    public ResponseResult status(HttpServletRequest request,
                                 @RequestBody PlayerStatusModel playerStatusModel,
                                 @RequestHeader("authorization") String authorization){

        Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
        try{
            Integer status = playerService.status(playerStatusModel, userId);
            return ResponseResult.toResponseResult(status);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PutMapping("/scheduled")
    @Authorization
    @ApiOperation(value = "给选手设置多少秒后定时投票",notes = "给选手设置多少秒后定时投票")
    public ResponseResult<ScheduleJob> scheduled(HttpServletRequest request,
                                 @RequestBody PlayerScheduledModel playerScheduledModel,
                                 @RequestHeader("authorization") String authorization){

        Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);
        try{
            ScheduleJob status = playerService.scheduled(orgRequest,playerScheduledModel, userId);
            return ResponseResult.toResponseResult(status);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PutMapping("/scheduledDown")
    @Authorization
    @ApiOperation(value = "关闭投票定时任务",notes = "关闭投票定时任务")
    public ResponseResult<ScheduleJob> scheduledDown(HttpServletRequest request,
                                                 @RequestParam Long playerId,
                                                 @RequestHeader("authorization") String authorization){

        Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);
        try{
            Integer integer = playerService.scheduledDown(playerId, userId);
            return ResponseResult.toResponseResult(integer);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }


    @GetMapping("/detail")
    @ApiOperation(value = "选手详情",notes = "选手详情")
    public ResponseResult<Player> detail(HttpServletRequest request,
                                         @RequestParam("playerId")Long playerId){
        try{
            return ResponseResult.toResponseResult(playerService.detail(playerId));
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }


}
