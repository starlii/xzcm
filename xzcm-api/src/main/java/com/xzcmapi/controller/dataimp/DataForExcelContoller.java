package com.xzcmapi.controller.dataimp;

import com.xiaoleilu.hutool.json.JSONObject;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.controller.file.FileController;
import com.xzcmapi.entity.UploadFile;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.DataImportRecord;
import com.xzcmapi.model.ExcelModel;
import com.xzcmapi.model.ExportDataModel;
import com.xzcmapi.service.PlayerService;
import com.xzcmapi.service.RoleService;
import com.xzcmapi.service.UploadFileCridFsService;
import com.xzcmapi.service.impl.DataInfoExcelService;
import com.xzcmapi.service.impl.RoleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/api/import")
@RestController
@Api(description = "导入/导出管理")
public class DataForExcelContoller extends BaseController {

    @Autowired
    private DataInfoExcelService dataInfoExcelService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private RoleService roleService;


    @PostMapping("/importExcelData")
    @ApiOperation(value = "批量导入选手", notes = "批量导入选手")
    @Authorization
    public ResponseResult importExcelData(HttpServletRequest request,
                                          @RequestBody DataImportRecord dataImportRecord,
                                          @RequestHeader(value = "authorization") @ApiParam("authorization") String authorization) {
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
//            boolean isManager = roleService.checkUserManagerPermission(userId);
//            if (!isManager) {
//                return new ResponseResult(true,CodeMessage.PEMISSIONERROR.getMsg(),
//                        CodeMessage.PEMISSIONERROR.getCode());
//            }
            JSONObject jsonObject = dataInfoExcelService.importExcelData(dataImportRecord, userId);
            if(jsonObject != null && jsonObject.get("forceErrorList") != null)
                return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,jsonObject);
            return new ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,jsonObject);
        } catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }

    @PostMapping("/exportExcelData")
    @ApiOperation(value = "导出活动选手", notes = "导出活动选手")
    @Authorization
    public ResponseResult exportExcelData(HttpServletRequest request,
                                          @RequestBody ExportDataModel exportDataModel,
                                          @RequestHeader(value = "authorization") @ApiParam("authorization") String authorization) {
        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            UploadFile excelData = playerService.getExcelData(exportDataModel, userId);
            return new ResponseResult(false,SUCCESS_SAVE_MESSAGE,SUCCESS_CODE,excelData);
        } catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),
                    e.getCodeMessage().getCode(),e.getMessage());
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE, INTERNAL_ERROR,e.getMessage());
        }
    }
}
