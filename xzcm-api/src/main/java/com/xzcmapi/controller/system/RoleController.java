package com.xzcmapi.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.OperationLog;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.*;
import com.xzcmapi.model.DistPermission;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.service.*;
import com.xzcmapi.util.XzcmStringUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("/api/role")
@Api(description = "角色管理")
public class RoleController extends BaseController {

    private static final String BASE_PATH = "system/";
    private static final Integer BASE_VALUE_ID = 16;
    private static final Integer BASH_VALUE_SORT = 10000;
    private static final Integer BASE_VALUE_PID = 0;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private UserRoleService userRoleService;

    /**
     * 分页查询角色列表
     *
     * @param pageParam 当前页码
     * @return
     */
    @GetMapping("list")
    @OperationLog(value = "查询角色")
    @ApiOperation(value = "带条件的分页查询", notes = "带条件的分页查询")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", defaultValue = "1", dataType = "integer", paramType = "query",
                    value = "页数 (1..N)"),
            @ApiImplicitParam(name = "size", defaultValue = "10", dataType = "integer", paramType = "query",
                    value = "每页大小"),
            @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名1 asc,属性名2 desc. ")
    })
    public ResponseResult list(@RequestParam(value = "name",required = false) @ApiParam(value = "角色名称") String name, @RequestParam(value = "status",required = false) @ApiParam(value = "角色状态") Integer status, @ApiIgnore PageParam pageParam){
        tk.mybatis.mapper.entity.Example example = new tk.mybatis.mapper.entity.Example(Role.class);
        tk.mybatis.mapper.entity.Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        if(StringUtils.isNotEmpty(status+"")){
            criteria.andEqualTo("status",status);
        }
        PageHelper.startPage(pageParam.getPage(),pageParam.getSize(),true);
        pageParam.setOrderBy(example);
        List<Role> roleList = roleService.selectByExample(example);
        log.debug("分页查询角色列表参数! pageNum = {}", pageParam.getPage());
        PageInfo<Role> pageInfo = new PageInfo<>(roleList);
        log.info("分页查询角色列表结果！ pageInfo = {}", pageInfo);
        return new ResponseResult(false, "",SUCCESS_CODE, pageInfo);
    }

    @GetMapping(value = "/getAllUserByRoleId")
    @ApiOperation(value = "通过角色ID分页获取所有的用户",notes = "通过角色ID分页获取所有的用户")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", defaultValue = "1",dataType = "integer", paramType = "query",
                    value = "页数 (1..N)"),
            @ApiImplicitParam(name = "size",defaultValue = "10", dataType = "integer", paramType = "query",
                    value = "每页大小"),
            @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名1 asc,属性名2 desc. ")
    })
    public ResponseResult getAllUserByRoleId(@RequestParam(value = "id") Long id, @RequestParam(value = "name",required = false) @ApiParam(value = "用户名") String name, PageParam pageParam){
        PageHelper.startPage(pageParam.getPage(),pageParam.getSize(),true);
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("name",name);
        List<Map<String,Object>> mapList = roleService.getAllUserByRoleId(map);
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(mapList);
        return new ResponseResult(false,"",SUCCESS_CODE,pageInfo);
    }

    @GetMapping("/getAllRes")
    @ApiOperation(value = "获取所有的资源",notes = "获取所有的资源")
    @Authorization
    public ResponseResult getAllRes(){
        log.debug("获取所有资源！");
        Example example = new Example(Permission.class);
        example.orderBy("sort").asc();
        List<Permission> permissionList = permissionService.selectByExample(example);
        return new ResponseResult(false,"",SUCCESS_CODE,permissionList);
    }

    @GetMapping("/getRoleRes")
    @ApiOperation(value = "角色查找资源",notes = "角色查找资源")
    @Authorization
    public ResponseResult getRoleRes(@RequestParam(value = "roleId",required = true) Long roleId){
        Role role = roleService.findById(roleId);
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(role.getId());
        List<RolePermission> rolePermissionList = rolePermissionService.findListByWhere(rolePermission);
        List<Permission> list = new ArrayList<>();
        Permission permission;
        for(RolePermission res:rolePermissionList){
            permission = permissionService.findById(res.getPermissionId());
            list.add(permission);
        }
        return new ResponseResult(false,"",SUCCESS_CODE,list);
    }

    @ApiOperation(value = "通过角色ID分配权限",notes = "通过角色ID分配权限")
    @PostMapping("/distributePermission")
    @Authorization
    public ResponseResult distributePermission(@RequestBody DistPermission distPermission){
        log.debug("分配权限！");
        roleService.distributePermissionByRoleId(distPermission.getRoleId(),distPermission.getPermissionIds());
        return new ResponseResult(false,SUCCESS_CODE,SUCCESS_SAVE_MESSAGE);
    }

    /**
     * 根据主键ID删除角色
     *
     * @param id
     * @return
     */
    @OperationLog(value = "删除角色")
    @DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value = "根据主键ID删除角色", notes = "根据主键ID删除角色")
    @Authorization
    public ResponseResult delete(@PathVariable("id") Long id) {

        log.debug("删除管理员! id = {}", id);

        if (null == id) {
            log.info("删除角色不存在! id = {}", id);
            return new ResponseResult(true, DATA_ERROR,"删除角色不存在!");
        }

        //判断该角色下是否绑定了用户 若有，则不允许删除
        UserRole userRole = new UserRole();
        userRole.setRoleId(id);
        List<UserRole> userRoles = userRoleService.findListByWhere(userRole);
        if(!userRoles.isEmpty()){
            log.info("删除角色失败，该角色下有用户存在，不允许删除! id = {}", id);
            return new ResponseResult(true, DATA_ERROR,"删除角色失败，该角色下有用户存在，不允许删除! ");
        }
        Boolean flag = roleService.deleteRoleAndRolePermissionByRoleId(id);
        if (flag) {
            return new ResponseResult(false, SUCCESS_CODE,"删除角色成功!");
        }
        log.info("删除角色失败，但没有抛出异常! id = {}", id);
        return new ResponseResult(true, INTERNAL_ERROR,"删除角色失败，但没有抛出异常!");
    }


    /**
     * 添加角色并分配权限
     *
     * @param role
     * @return
     */
    @OperationLog(value = "添加角色")
    @ApiOperation(value = "添加角色并分配权限", notes = "添加角色并分配权限")
    @PostMapping("/saveRole")
    @Authorization
    public ResponseResult saveRole(@Validated @ApiParam(value = "添加角色") @RequestBody Role role, @RequestParam(required = false) @ApiParam(value = "权限ID") Long[] permissionIds) {
        log.debug("添加角色并分配权限参数! role = {}, permissionIds = {}", role, permissionIds);
        Boolean flag = roleService.saveRoleAndRolePermission(role, permissionIds);
        if (flag) {
            log.info("添加角色并分配权限成功! roleId = {}", role.getId());
            return new ResponseResult(false, SUCCESS_CODE,"添加角色并分配权限成功!");
        }
        log.info("添加角色并分配权限失败，但没有抛出异常! roleId = {}", role.getId());
        return new ResponseResult(true, INTERNAL_ERROR,"添加角色并分配权限失败，但没有抛出异常!");
    }


    @PutMapping("/update")
    @ApiOperation(value = "角色更新")
    public ResponseResult update(@RequestBody Role role){
        if(role == null ){
            return new ResponseResult(true,DATA_ERROR,"角色不存在！");
        }
        if(Objects.isNull(role.getId()) || XzcmStringUtils.isEmpty(role.getName()) || Objects.isNull(role.getStatus())){
            return new ResponseResult(true,DATA_ERROR,"角色参数错误");
        }
        Role record = roleService.findById(role.getId());
        if(Objects.isNull(record)){
            return new ResponseResult(true,DATA_ERROR,"角色不存在");
        }

        record.setName(role.getName());
        record.setStatus(role.getStatus());
        record.setRemark(role.getRemark());
        return new ResponseResult(false,"更新成功！",SUCCESS_CODE,roleService.update(record));
    }

}
