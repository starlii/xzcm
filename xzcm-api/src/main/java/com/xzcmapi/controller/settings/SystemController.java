package com.xzcmapi.controller.settings;

import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.SystemParam;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.SettingModel;
import com.xzcmapi.service.SystemParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/setting")
@Api(description = "平台设置")
public class SystemController extends BaseController {

    @Autowired
    private SystemParamService systemParamService;


//    @GetMapping("/list")
//    @ApiOperation(value = "获取平台设置",notes = "获取平台设置")
//    public ResponseResult get(){
//        try{
//            Map<String, String> basic = systemParamService.basic();
//            return new  ResponseResult(false,SUCCESS_LOAD_MESSAGE,basic);
//        }catch (Exception e){
//            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,e.getMessage());
//        }
//    }

    @GetMapping("/basic")
    @ApiOperation(value = "获取平台基础信息设置",notes = "获取平台基础信息设置")
    public ResponseResult basic(){
        try{
            Map<String, String> basic = systemParamService.basic();
            return new  ResponseResult(false,SUCCESS_LOAD_MESSAGE,basic);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,e.getMessage());
        }
    }

    @GetMapping("/weChat")
    @ApiOperation(value = "获取微信设置",notes = "获取微信设置")
    public ResponseResult weChat(){
        try{
            Map<String, String> basic = systemParamService.weChat();
            return new  ResponseResult(false,SUCCESS_LOAD_MESSAGE,basic);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_LOAD_MESSAGE,e.getMessage());
        }
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新平台设置",notes = "更新平台设置")
    public ResponseResult update(HttpServletRequest request,
                                 @RequestBody SettingModel setting){
        try {
            Integer update = systemParamService.update(setting);
            return ResponseResult.toResponseResult(update);
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,INTERNAL_ERROR,null);
        }
    }
}
