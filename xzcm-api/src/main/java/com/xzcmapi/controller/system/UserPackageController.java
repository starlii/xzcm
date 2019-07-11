package com.xzcmapi.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.annotation.CurrentUser;
import com.xzcmapi.annotation.OperationLog;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.User;
import com.xzcmapi.entity.UserPackage;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.model.UserModel;
import com.xzcmapi.service.UserPackageService;
import com.xzcmapi.util.XzcmStringUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/package")
@Api(description = "用户套餐")
public class UserPackageController extends BaseController {


    @Autowired
    private UserPackageService userPackageService;


    @GetMapping(value = "/get")
    @ApiOperation(value = "获取用户套餐", notes = "获取用户套餐")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", defaultValue = "1", dataType = "integer", paramType = "query",
                    value = "页数 (1..N)"),
            @ApiImplicitParam(name = "size", defaultValue = "10", dataType = "integer", paramType = "query",
                    value = "每页大小"),
            @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名1 asc,属性名2 desc. ")
    })
    public ResponseResult<UserPackage> get(HttpServletRequest request,
                                    @ApiIgnore PageParam pageParam,
                                    @RequestParam(value = "id",required = false)Long id,
                                    @RequestHeader("authorization")String authorization) {
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
//            if(id != null){
//                UserPackage byId = userPackageService.findById(id);
//                return new ResponseResult(false, SUCCESS_LOAD_MESSAGE, new PageInfo(Arrays.asList(byId)));
//            }
//            Example example = new Example(UserPackage.class);
//            example.createCriteria().andEqualTo("creator",userId);
            PageHelper.startPage(pageParam.getPage(),pageParam.getSize());
            List<UserPackage> all = userPackageService.get(id,userId);
            return new ResponseResult(false, SUCCESS_LOAD_MESSAGE, new PageInfo<>(all));
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult(true, FAILURE_LOAD_MESSAGE, e.getMessage());
        }
    }

    @GetMapping(value = "/getNoPage")
    @ApiOperation(value = "获取用户套餐", notes = "获取用户套餐")
    @Authorization
    public ResponseResult<UserPackage> get(HttpServletRequest request,
                                           @RequestHeader("authorization")String authorization) {
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
//            Example example = new Example(UserPackage.class);
//            example.createCriteria().andEqualTo("creator",userId);
            List<UserPackage> all = userPackageService.get(null,userId);
            return new ResponseResult(false, SUCCESS_LOAD_MESSAGE, all);
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult(true, FAILURE_LOAD_MESSAGE, e.getMessage());
        }
    }

    @PostMapping(value = "/save")
    @ApiOperation(value = "添加套餐", notes = "添加套餐")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult save(HttpServletRequest request,
                                   @RequestBody UserPackage user) throws Exception {

        try{
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            user.setCreateTime(new Date());
            user.setCreator(userId);
            userPackageService.save(user);
            return new ResponseResult(false, SUCCESS_CODE,"添加套餐成功！");
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE,INTERNAL_ERROR,null);
        }
    }


    @PutMapping(value = "/update")
    @ApiOperation(value = "更新套餐信息", notes = "更新套餐信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult update( @RequestBody @ApiParam(value = "套餐信息") UserPackage userPackage) throws Exception {
        userPackageService.updateSelective(userPackage);
        return new ResponseResult(false,SUCCESS_CODE, "编辑套餐成功!");
    }

    @DeleteMapping(value = "/deleteUser")
    @ApiOperation(value = "删除套餐", notes = "删除套餐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult delete(@RequestParam(value = "packageId") Long packageId) throws Exception {
        try {
            Integer integer = userPackageService.deleteById(packageId);
            return new ResponseResult(false,SUCCESS_CODE, "删除成功");
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult(true, INTERNAL_ERROR,"系统异常");
        }
    }
}
