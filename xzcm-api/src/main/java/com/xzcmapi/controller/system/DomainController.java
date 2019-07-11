package com.xzcmapi.controller.system;

import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.SystemParam;
import com.xzcmapi.model.DomainModel;
import com.xzcmapi.service.SystemParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/domain")
@Api(description = "域名管理")
public class DomainController extends BaseController {

    @Autowired
    private SystemParamService systemParamService;


    @PostMapping("/set")
    @Authorization
    @ApiOperation(value = "设置泛域名",notes = "设置泛域名")
    public ResponseResult setPanDomain(HttpServletRequest request,
                                       @RequestBody DomainModel domainModel,
                                       @RequestHeader("authorization")String authorization){
        Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
        String domain = domainModel.getDomains();
        Long activityId = domainModel.getActivityId();
        SystemParam systemParam = new SystemParam();
        systemParam.setKey("domain_"+activityId);
        systemParam = systemParamService.findOne(systemParam);
        if(systemParam != null){
            systemParam.setValue(domain);
            systemParamService.updateSelective(systemParam);
        }else {
            SystemParam db = new SystemParam();
            db.setKey("domain_"+activityId);
            db.setValue(domain);
            db.setStatus(0);
            db.setType(0);
            db.setCreateTime(new Date());
            db.setCreator(userId);
            systemParamService.save(db);
        }
        return ResponseResult.toResponseResult(null);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取域名设置",notes = "获取域名设置")
    public ResponseResult get(@RequestParam("activityId")Long activityId){
        SystemParam systemParam = new SystemParam();
        systemParam.setKey("domain_"+activityId);
        SystemParam param = systemParamService.findOne(systemParam);
        Map<String,Object> result = new HashMap<>();
        result.put("activityId",activityId);
        result.put("domain",param.getValue());
        return ResponseResult.toResponseResult(result);
    }
}
