package com.xzcmapi.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.Player;
import com.xzcmapi.entity.Report;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.PlayerModel;
import com.xzcmapi.model.PlayerSearchModel;
import com.xzcmapi.model.SavePlayerModel;
import com.xzcmapi.service.ReportProService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RequestMapping("/api/reportPro")
@RestController
@Api(description = "投诉管理")
public class ReportProController extends BaseController {

    @Autowired
    private ReportProService reportProService;

    @PostMapping("/add")
    @ApiOperation(value = "新增举报",notes = "新增举报")
    public ResponseResult add(HttpServletRequest request
            , @RequestBody @ApiParam("新增举报实体") Report report){
        try{
            Report add = reportProService.add(request,report);
            return new ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,add);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取举报列表",notes = "获取举报列表")
    @Authorization
    public ResponseResult<PageInfo<List<Report>>> get(HttpServletRequest request,
                                                           @RequestParam(value = "activityId",required = false) Long activityId,
                                                            @RequestParam(value = "pageNum" ,defaultValue = "1")Integer pageNum,
                                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                      @RequestHeader("authorization")String authorization){
        try{
            Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
            PageHelper.startPage(pageNum,pageSize);
            List<Report> listPageInfo = reportProService.get(activityId,userId);
            return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,SUCCESS_CODE,new PageInfo<>(listPageInfo));
        }catch(XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(), e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }
}
