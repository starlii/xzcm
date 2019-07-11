package com.xzcmapi.controller.system;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.CurrentUser;
import com.xzcmapi.annotation.OperationLog;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.common.SysParam;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.Role;
import com.xzcmapi.entity.User;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.model.ModifyPwdParam;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.model.SingleKeyParam;
import com.xzcmapi.model.UserModel;
import com.xzcmapi.service.LogService;
import com.xzcmapi.service.RoleService;
import com.xzcmapi.service.UserRoleService;
import com.xzcmapi.service.UserService;
import com.xzcmapi.util.PasswordUtil;
import com.xzcmapi.util.XzcmStringUtils;
import io.swagger.annotations.*;
import com.xzcmapi.annotation.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/user")
@Api(description = "用户管理")
public class UserController extends BaseController {

    private static final String BASE_PATH = "system/manager/";

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserRoleService userRoleService;

    @Autowired
    private LogService logService;


    /**
     * 用户管理页面分页查询管理员列表
     *
     * @param username

     * @param pageParam
     * @return
     */
    @GetMapping(value = "/get")
    @ApiOperation(value = "用户管理查询列表", notes = "用户管理查询列表")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", defaultValue = "1", dataType = "integer", paramType = "query",
                    value = "页数 (1..N)"),
            @ApiImplicitParam(name = "size", defaultValue = "10", dataType = "integer", paramType = "query",
                    value = "每页大小"),
            @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名1 asc,属性名2 desc. ")
    })
    public ResponseResult<User> get(HttpServletRequest request,
                              @RequestParam(value = "username", required = false) String username,
                                    @RequestParam(value = "id", required = false) Long id,
                                       @RequestParam(value = "userPackage",required = false) Integer userPackage,
                                       @ApiIgnore PageParam pageParam,
                              @RequestHeader("authorization")String authorization) {
        log.debug("request to getDeptUser by params username:{}", username);
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            PageInfo<List<UserModel>> pageInfo = userService.get(username,pageParam,userId,id,userPackage);
            return new ResponseResult(false, "", pageInfo);
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult(true, FAILURE_LOAD_MESSAGE, e.getMessage());
        }
    }


    /**
     * 查找用户所有的角色
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/getUserRoles")
    @ApiOperation(value = "查找用户所有的角色", notes = "查找用户所有的角色")
    @Authorization
    public ResponseResult getUserRoles(@RequestParam(value = "userId") Long userId,
                                       @RequestHeader("authorization")String authorization) {
        log.debug("request to get user roles by user id {}", userId);
        if (Objects.isNull(userId)) {
            return new ResponseResult(true, DATA_ERROR,"参数错误");
        }
        try {
            List<Role> roleList = roleService.findByUserId(userId);
            return new ResponseResult(false, "",SUCCESS_CODE, roleList);
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult<>(true, FAILURE_LOAD_MESSAGE, INTERNAL_ERROR, e.getMessage());
        }

    }

    /**
     * 添加用户
     *
     * @param user
     * @param creator
     * @return
     * @throws Exception
     */
    @OperationLog(value = "添加用戶")
    @PostMapping(value = "/save")
    @ApiOperation(value = "添加用戶", notes = "添加用戶")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult saveUser(HttpServletRequest request,
                                   @RequestBody @Validated UserModel user) throws Exception {

        try{
            log.debug("添加管理员参数! user = {}", user);
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            if (Objects.isNull(user) || XzcmStringUtils.isEmpty(user.getUsername())) {
                return new ResponseResult(true, DATA_ERROR,"参数不全");
            }
            if (Objects.nonNull(userService.findByUserName(user.getUsername()))) {
                return new ResponseResult(true, DATA_ERROR,"用户名已存在");
            }
            if(!user.getPassword().equals(user.getRepassword()))
                return new ResponseResult(true,DATA_ERROR,"两次输入密码不一致");
            user.setId(null);
            Boolean flag = userService.saveUser(user, userId);
            if (flag) {
                log.info("添加管理员成功! userId = {}", user.getId());
                return new ResponseResult(false, SUCCESS_CODE,"添加管理员成功！");
            }
            log.info("添加管理员失败, 但没有抛出异常! userId = {}", user.getId());
            return new ResponseResult(true, INTERNAL_ERROR,"添加管理员失败，但是没有抛出异常！");
        }catch (XzcmBaseRuntimeException e){
            return new ResponseResult(true,e.getCodeMessage().getMsg(),e.getCodeMessage().getCode(),null);
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE,INTERNAL_ERROR,null);
        }
    }


    /**
     * 更新管理员信息
     *
     * @param userModel
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新管理员信息", notes = "更新管理员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult updateUser(@Validated @RequestBody @ApiParam(value = "用户信息") UserModel userModel, @ApiIgnore @CurrentUser User creator) throws Exception {
        log.debug("编辑用户参数!user = {}", userModel);
        if (Objects.isNull(userModel.getId())) {
            return new ResponseResult(true, DATA_ERROR,"参数不全");
        }
        User u = userService.findById(userModel.getId());
        if (Objects.isNull(u)) {
            log.info("编辑的用户不存在! ");
            return new ResponseResult(true,DATA_ERROR, "编辑的用户不存在！");
        }
        Boolean flag = userService.updateUser(userModel);
        if (flag) {
            log.info("编辑管理员成功!user = {}", userModel);
            return new ResponseResult(false,SUCCESS_CODE, "编辑管理员成功!");
        }
        log.info("编辑管理员失败,但没有抛出异常 !user = {}", userModel);
        return new ResponseResult(true, INTERNAL_ERROR,"编辑管理员失败,但没有抛出异常 !");
    }


    @PutMapping(value = "/modifyPassword")
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult modifyPassword(HttpServletRequest request,@Validated @RequestBody ModifyPwdParam modifyPwdParam, @ApiIgnore @CurrentUser User creator) throws Exception {

        Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
        if (Objects.isNull(modifyPwdParam) ||
                XzcmStringUtils.isEmpty(modifyPwdParam.getNewPwd()) || XzcmStringUtils.isEmpty(modifyPwdParam.getOldPwd())) {
            return new ResponseResult(true,DATA_ERROR, "参数错误");
        }
//        if (!Objects.equals(creator.getId(), modifyPwdParam.getUserId())) {
//            return new ResponseResult(true,DATA_ERROR, "没有权限");
//        }
        User user = userService.findById(userId);
        if (Objects.isNull(user)) {
            return new ResponseResult(true, DATA_ERROR,"用户不存在");
        }
        if (!user.getPassword().equals(PasswordUtil.encrypt(modifyPwdParam.getOldPwd()))) {
            return new ResponseResult(true,DATA_ERROR, "原密码错误");
        }
        if (modifyPwdParam.getUsername() != null && Objects.nonNull(userService.findByUserName(modifyPwdParam.getUsername()))) {
            return new ResponseResult(true, DATA_ERROR,"用户名已存在");
        }
        User update = new User();
        update.setId(user.getId());
//        update.setManager(user.isManager());
        update.setPassword(PasswordUtil.encrypt(modifyPwdParam.getNewPwd()));
        update.setUsername(modifyPwdParam.getUsername());
        userService.updateSelective(update);

        userService.updateSelective(update);
        return new ResponseResult(false,SUCCESS_CODE, "修改成功");
    }

    @PutMapping(value = "/resetPassword")
    @ApiModelProperty(value = "重置密码", notes = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult resetPassword(@RequestBody SingleKeyParam singleKeyParam, @ApiIgnore @CurrentUser User creator) {
        if (Objects.isNull(singleKeyParam) || Objects.isNull(singleKeyParam.getId())) {
            return new ResponseResult(true,DATA_ERROR, "参数不全");
        }
        Long id = singleKeyParam.getId();
        User user = userService.findById(id);
        if (Objects.isNull(user)) {
            return new ResponseResult(true,DATA_ERROR, "用户不存在");
        }
        User update = new User();
        update.setId(user.getId());
        String str = SysParam.DEFAULT_PWD;
        update.setPassword(PasswordUtil.encrypt(str));
        userService.updateSelective(update);
        return new ResponseResult(false, SUCCESS_CODE,"重置成功");
    }



    @DeleteMapping(value = "/deleteUser")
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult deleteUser(@ApiIgnore @CurrentUser User user, @RequestParam(value = "userId") Long userId) throws Exception {
        try {
            User deleteUser = userService.findById(userId);
            if (Objects.isNull(deleteUser)) {
                return new ResponseResult(true,DATA_ERROR, "用户不存在");
            }
            userService.deleteUser(deleteUser);
            return new ResponseResult(false,SUCCESS_CODE, "删除成功");
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult(true, INTERNAL_ERROR,"系统异常");
        }
    }

    @GetMapping(value = "/getCurrentUser")
    @ApiOperation(value = "获取当前用户", notes = "获取当前用户")
    @Authorization
    public ResponseResult getCurrentUser(HttpServletRequest request,
                                       @RequestHeader("authorization")String authorization) {
        try {
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            User user = userService.findById(userId);
            for (User.Role role : User.Role.values()) {
                if(role.getRemark().equals(user.getRole()))
                    user.setRole(role.getRemark());
            }
            return new ResponseResult(false, "",SUCCESS_CODE, user);
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult<>(true, FAILURE_LOAD_MESSAGE, INTERNAL_ERROR, e.getMessage());
        }

    }
}
