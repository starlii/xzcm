package com.xzcmapi.controller.activity;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.VoteLog;
import com.xzcmapi.exception.DataException;
import com.xzcmapi.exception.PermissionException;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.*;
import com.xzcmapi.service.ActivityService;
import io.swagger.annotations.*;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/activity")
@Api(description = "活动管理")
public class ActivityController extends BaseController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/list")
    @ApiOperation(value = "活动列表",notes = "活动列表")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult<PageInfo<List<ActivityModel>>> get(HttpServletRequest request, @RequestBody(required = false) SearchModel search){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer page = search.getPage();
            Integer size = search.getSize();
            if(page == null)
                search.setPage(1);
            if(size == null)
                search.setSize(10);
            PageInfo<List<ActivityModel>> list = activityService.list(search,userId,false);
            return new  ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,list);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    , e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE, INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/listOnlySelf")
    @ApiOperation(value = "活动列表",notes = "活动列表")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult<PageInfo<List<ActivityModel>>> listOnlySelf(HttpServletRequest request, @RequestBody(required = false) SearchModel search){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer page = search.getPage();
            Integer size = search.getSize();
            if(page == null)
                search.setPage(1);
            if(size == null)
                search.setSize(10);
            PageInfo<List<ActivityModel>> list = activityService.list(search,userId,true);
            return new  ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,list);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    , e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE, INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加活动",notes = "添加活动")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult add(HttpServletRequest request, @RequestBody @ApiParam(value = "添加活动参数，参数中的activityId字段不需要传") SaveActivityModel activity){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer add = activityService.add(activity, userId);
            if (!add.equals(0))
                return new  ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,add);
            return new  ResponseResult(true,"保存失败但是没有抛出异常！",INTERNAL_ERROR,null);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true
                    ,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新活动",notes = "更新活动")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult update(HttpServletRequest request,
                                 @RequestBody @ApiParam(value = "更新活动参数") SaveActivityModel activity){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer update = activityService.update(activity, userId);
            if(!update.equals(0))
                return new  ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,update);
            return new  ResponseResult(true,"更新失败但是没有抛出异常！",INTERNAL_ERROR,null);
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

    @DeleteMapping("/delete")
    @ApiOperation(value = "批量刪除活动",notes = "批量刪除活动")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult delete(HttpServletRequest request,
                              @RequestParam @ApiParam("活动ids") List<Long> activityIds){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer delete = activityService.delete(activityIds, userId);
            if (delete == 1)
                return new  ResponseResult(false,SUCCESS_DELETE_MESSAGE,SUCCESS_CODE,delete);
            return new  ResponseResult(true,"删除失败但是没有抛出异常！",INTERNAL_ERROR,null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_DELETE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/batchUpdate")
    @ApiOperation(value = "批量更新活动备注",notes = "批量更新活动备注")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult batchUpdate(HttpServletRequest request,
                                 @RequestBody @ApiParam(value = "更新活动参数") BatchUpdateModel batch){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer integer = activityService.batchUpdate(batch, userId);
            return new  ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,integer);
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

    @PutMapping("/updateViews")
    @ApiOperation(value = "手动增加浏览量",notes = "手动增加浏览量")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult updateViews(HttpServletRequest request,
                                 @RequestBody @ApiParam("增量参数") UpdateViewsModel views){
        try{
//            Integer views1 = views.getViews();
//            if(views1 == null || views1 <= 0)
//                return new  ResponseResult(true,"更新失败，浏览量不能为负数或者零！",INTERNAL_ERROR,null);
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer delete = activityService.updateViews(views, userId);
            if (delete == 1)
                return new  ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,delete);
            return new  ResponseResult(true,"删除失败但是没有抛出异常！",INTERNAL_ERROR,null);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PutMapping("/addVote")
    @ApiOperation(value = "加票",notes = "加票")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    })
    public ResponseResult addVote(HttpServletRequest request,
                                      @RequestParam @ApiParam("活动Id") Long activityId){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            Integer delete = activityService.addVotes(activityId, userId);
            if (delete == 1)
                return new  ResponseResult(false,SUCCESS_UPDATE_MESSAGE,SUCCESS_CODE,delete);
            return new  ResponseResult(true,"删除失败但是没有抛出异常！",INTERNAL_ERROR,null);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @GetMapping("/getActivityAccessLink")
    @Authorization
    @ApiOperation(value = "查看活动的访问连接",notes = "查看活动的访问连接")
    public ResponseResult<QrCodeModel> getActivityAccessLink(HttpServletRequest request,
                                                             @RequestParam("activityId")@ApiParam("活动id") Long activityId,
                                                             @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            QrCodeModel activityAccessLink = activityService.getActivityAccessLink(activityId,userId);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,activityAccessLink);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @GetMapping("/getActivityDetail")
    @ApiOperation(value = "获取活动详情页",notes = "获取活动详情页")
    public ResponseResult getActivityDetail(HttpServletRequest request,
                                            @RequestParam("activityId")Long activityId,
                                            @RequestParam(value = "type",required = false)Integer type){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            SaveActivityModel saveActivityModel = activityService.getActivityDetail(activityId,userId,type);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,saveActivityModel);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @GetMapping("/getGiftLog")
    @Authorization
    @ApiOperation(value = "查看活动所有礼物记录",notes = "查看活动所有礼物记录")
    public ResponseResult<GiftLog> getGiftLog(HttpServletRequest request,
                                     @RequestParam(value = "activityId",required = false)Long activityId,
                                     @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            List<GiftLog> giftLog = activityService.getGiftLog(activityId,userId,null,null);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,giftLog);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }

    }

    @GetMapping("/getVoteLog")
    @Authorization
    @ApiOperation(value = "查看活动所有投票记录",notes = "查看活动所有投票记录")
    public ResponseResult<GiftLog> getVoteLog(HttpServletRequest request,
                                              @RequestParam(value = "activityId",required = false)Long activityId,
                                              @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            List<VoteLog> voteLog = activityService.getVoteLog(activityId,userId,null);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,voteLog);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg()
                    ,e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }

    }


}
