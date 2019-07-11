package com.xzcmapi.controller.reports;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.ChargeModel;
import com.xzcmapi.model.ReportSearchModel;
import com.xzcmapi.model.SettlementModel;
import com.xzcmapi.model.TodayDealsModel;
import com.xzcmapi.service.ReportService;
import com.xzcmapi.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.xzcmapi.common.ResponseResult.toResponseResult;

@RestController
@RequestMapping("/api/reports")
@Api(description = "数据统计")
public class ReportController extends BaseController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/statistics")
    @Authorization
    @ApiOperation(value = "获取统计数据",notes = "获取统计数据")
    public ResponseResult<TodayDealsModel> getTodayDeals(HttpServletRequest request,
                                                         @RequestParam(value = "self",required = false) Integer self,
                                                         @RequestHeader("authorization")String authorization){
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            TodayDealsModel todayDeals = reportService.getTodayDeals(userId,self);
            return toResponseResult(todayDeals);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/settlement")
    @Authorization
    @ApiOperation(value = "结算报表",notes = "结算报表")
    public ResponseResult<SettlementModel> settlement(HttpServletRequest request,
                                                      @RequestBody(required = false) ReportSearchModel reportSearchModel,
                                                      @RequestHeader("authorization")String authorization){
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
//            boolean b = roleService.checkUserManagerPermission(userId);
//            if(b)
//                userId = null;
            if(reportSearchModel == null){
                reportSearchModel = new ReportSearchModel();
                reportSearchModel.setPage(1);
                reportSearchModel.setSize(10);
            }
            PageInfo<List<SettlementModel>> settlement = reportService.settlement(reportSearchModel, userId);
            return toResponseResult(settlement);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PutMapping("/updateStatus")
    @Authorization
    @ApiOperation(value = "更新结算报表状态",notes = "更新结算报表状态")
    public ResponseResult updateStatus(HttpServletRequest request,
                                              @RequestParam("id") Long id,
                                              @RequestHeader("authorization")String authorization){
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
//            boolean b = roleService.checkUserManagerPermission(userId);
            return toResponseResult(reportService.updateStatus(id,userId));
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }

    @PostMapping("/charge")
    @Authorization
    @ApiOperation(value = "充值报表",notes = "充值报表")
    public ResponseResult<ChargeModel> charge(HttpServletRequest request,
                                              @RequestBody(required = false) ReportSearchModel reportSearchModel,
                                              @RequestHeader("authorization")String authorization){
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
//            boolean b = roleService.checkUserManagerPermission(userId);
//            if(b)
//                userId = null;
            if(reportSearchModel == null){
                reportSearchModel = new ReportSearchModel();
                reportSearchModel.setPage(1);
                reportSearchModel.setSize(10);
            }
            PageInfo<List<ChargeModel>> charge = reportService.charge(reportSearchModel, userId);
            return toResponseResult(charge);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,INTERNAL_ERROR,null);
        }
    }
}
