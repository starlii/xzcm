package com.xzcmapi.controller.system;

import com.xiaoleilu.hutool.http.HttpUtil;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.Permission;
import com.xzcmapi.entity.Role;
import com.xzcmapi.entity.User;
import com.xzcmapi.model.UserLoginVo;
import com.xzcmapi.service.PermissionService;
import com.xzcmapi.service.RoleService;
import com.xzcmapi.service.UserService;
import com.xzcmapi.util.PasswordUtil;
import com.xzcmapi.util.auth.TokenManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@Api(description = "登录")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 文件服务地址
    @Value("${file.url}")
    private String fileUrl;

    /**
     * 用户登陆
     * 先根据用户名查询出一条用户记录再对比密码是否正确可以防止sql注入
     *
     * @return
     */
    @PostMapping(value = "/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public ResponseResult login(HttpServletRequest request
            , @RequestBody UserLoginVo userLoginVo) {
        try {
            String code = userLoginVo.getCode();
            if(code == null)
                return new ResponseResult(true,BAD_REQUEST,"请输入验证码");
            String clientIP = HttpUtil.getClientIP(request);
//            String sessionCode = request.getSession().getAttribute("code").toString();
//            if (!"".equals(code) && sessionCode != null && !"".equals(sessionCode)) {
//                if(!code.equalsIgnoreCase(sessionCode))
//                    return new ResponseResult(true,BAD_REQUEST,"验证码输入有误，请重新输入");
//            }else {
//                return new ResponseResult(true,BAD_REQUEST,"验证失败");
//            }

            //通过redis 验证
            String dbCode = stringRedisTemplate.boundValueOps(clientIP).get();
            if (!"".equals(code) && dbCode != null && !"".equals(dbCode)) {
                if(!code.equalsIgnoreCase(dbCode))
                    return new ResponseResult(true,BAD_REQUEST,"验证码输入有误，请重新输入");
            }else {
                return new ResponseResult(true,BAD_REQUEST,"验证码失效，请刷新");
            }

            User user = userService.findByUserName(userLoginVo.getUsername());
            if (Objects.isNull(user)) {
                return new ResponseResult(true, DATA_ERROR,"无此用户");
            }
            List<Role> roleList = new ArrayList<>();

            if (Objects.equals(user.getStatus(), 1)) {
                return new ResponseResult(true,DATA_ERROR, "用户未启用");
            }
            if (!user.getPassword().equals(PasswordUtil.encrypt(userLoginVo.getPassword()))) {
                return new ResponseResult(true, DATA_ERROR,"密码错误");
            }

//            //获取用户的所有角色信息
//            roleList = roleService.findByUserId(user.getId());
//            if (roleList.isEmpty()) {
//                return new ResponseResult(true, DATA_ERROR,"用户权限异常");
//            }
//
//            List<Permission> menuListByUserId = permissionService.findMenuListByUserId(user.getId());

            //update
            user.setLoginTime(new Date());
            user.setIp(HttpUtil.getClientIP(request));
            userService.updateSelective(user);

            //生成一个token，保存用户登录状态
            String valiToken = tokenManager.createToken(user.getId());
            Map<String, Object> loginMap = new HashMap<>();
            loginMap.put("token", valiToken);
//            loginMap.put("user", user);
//            loginMap.put("roleList", roleList);
//            loginMap.put("menuList",menuListByUserId);

            //删除code
            stringRedisTemplate.delete(clientIP);
            return new ResponseResult(false, SUCCESS_LOAD_MESSAGE,SUCCESS_CODE, loginMap);
        } catch (Exception e) {
            log.error(e);
            log.error("对用户进行登录验证失败! username = {} e = {}", userLoginVo.getUsername(), e);
            return new ResponseResult(true, INTERNAL_ERROR,"系统异常");
        }
    }

    @PostMapping(value = "/signOut")
    @Authorization
    @ApiOperation(value = "登出", notes = "登出")
    public ResponseResult signOut(HttpServletRequest request,
                                @RequestHeader("authorization")String authorization) {
        Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
        stringRedisTemplate.delete(String.valueOf(userId));
        return ResponseResult.toResponseResult(null);
    }

    @PostMapping(value = "/switchUser")
    @Authorization
    @ApiOperation(value = "切换用户", notes = "切换用户")
    public ResponseResult switchUser(HttpServletRequest request
                                    ,@RequestBody UserLoginVo userLoginVo,
                                     @RequestHeader("authorization")String authorization) {
        try {

            //登出当前用户
            Long userId = (Long)request.getAttribute(Constant.CURRENT_USER_ID);
            stringRedisTemplate.delete(String.valueOf(userId));

            User user = userService.findByUserName(userLoginVo.getUsername());
            if (Objects.isNull(user)) {
                return new ResponseResult(true, DATA_ERROR,"无此用户");
            }
            List<Role> roleList = new ArrayList<>();

            if (Objects.equals(user.getStatus(), 1)) {
                return new ResponseResult(true,DATA_ERROR, "用户未启用");
            }
            if (!user.getPassword().equals(userLoginVo.getPassword())) {
                return new ResponseResult(true, DATA_ERROR,"密码错误");
            }

            //update
            user.setLoginTime(new Date());
            user.setIp(HttpUtil.getClientIP(request));
            userService.updateSelective(user);

            //生成一个token，保存用户登录状态
            String valiToken = tokenManager.createToken(user.getId());
            Map<String, Object> loginMap = new HashMap<>();
            loginMap.put("token", valiToken);

            return new ResponseResult(false, SUCCESS_LOAD_MESSAGE,SUCCESS_CODE, loginMap);
        } catch (Exception e) {
            log.error(e);
            log.error("对用户进行登录验证失败! username = {} e = {}", userLoginVo.getUsername(), e);
            return new ResponseResult(true, INTERNAL_ERROR,"系统异常");
        }
    }

}
