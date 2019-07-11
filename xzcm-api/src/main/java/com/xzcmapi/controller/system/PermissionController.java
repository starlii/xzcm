package com.xzcmapi.controller.system;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.OperationLog;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.Permission;
import com.xzcmapi.service.PermissionService;
import com.xzcmapi.util.xss.XssHttpServletRequestWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
@Api(description = "权限管理")
public class PermissionController extends BaseController {

    private static final String BASE_PATH = "system/";

    @Resource
    private PermissionService permissionService;

    /**
     * 分页查询权限列表
     * @param pageNum 当前页码
     * @return
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "分页查询权限列表",notes = "分页查询权限列表")
    public ResponseResult list(@RequestParam(value = "pageNum", defaultValue = "1") @ApiParam(value = "当前页码") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") @ApiParam(value = "每页数量") Integer pageSize,
                               @RequestBody(required = false) Permission model){
        String name = null;
        if(model != null)
            name = model.getName();
        log.debug("分页查询权限列表参数! pageNum = {},pageSize = {}, name = {}", pageNum,pageSize,name);
        PageInfo<Permission> pageInfo = permissionService.findPage(pageNum, PAGESIZE, name);
        log.info("分页查询权限列表结果！ pageInfo = {}", pageInfo);
        return new ResponseResult(false,"",SUCCESS_CODE,pageInfo);
    }

    /**
     * 根据主键ID删除权限
     * @param id
     * @return
     */
    @OperationLog(value = "删除权限")
    @CacheEvict(value = "menuListCache", allEntries = true)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "根据主键ID删除权限",notes = "根据主键ID删除权限")
    public ResponseResult delete(@PathVariable("id") Long id) {
        log.debug("删除权限! id = {}", id);
        if (null == id) {
            log.info("删除权限不存在! id = {}", id);
            return new ResponseResult(true,DATA_ERROR,"删除权限不存在!");
        }

        Boolean flag = permissionService.deletePermissionAndRolePermissionByPermissionId(id);
        if(flag){
            log.info("删除权限成功! id = {}", id);
            return new ResponseResult(false,SUCCESS_CODE,"删除成功！");
        }

        log.info("删除权限失败，但没有抛出异常! id = {}", id);
        return new ResponseResult(true, INTERNAL_ERROR,"删除权限失败，但没有抛出异常!");
    }

    /**
     * 添加权限
     * @param permission
     * @return
     */
    @OperationLog(value = "添加权限")
    @CacheEvict(value = "menuListCache", allEntries = true)
    @PostMapping("/save")
    @ApiOperation(value = "添加权限",notes = "添加权限")
    public ResponseResult savePermission(HttpServletRequest request, @RequestBody Permission permission){
        log.debug("添加权限参数! permission = {}", permission);

        //获取不进行xss过滤的request对象
        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);

        permission.setIcon(orgRequest.getParameter("icon"));
        permission.setParentId(permission.getParentId() == null ? 0 : permission.getParentId());
        permissionService.save(permission);
        log.info("添加权限成功! permissionId = {}", permission.getId());
        return new ResponseResult(false,SUCCESS_CODE,"添加成功！");
    }

    /**
     * 根据资源类型获取权限列表
     * @param type
     * @return
     */
    @ResponseBody
    @GetMapping("/getPermissionList")
    @ApiOperation(value = "根据资源类型获取权限列表",notes = "根据资源类型获取权限列表")
    public ResponseResult getPermissionList(Integer type){
        log.debug("根据资源类型获取权限列表，类型type = {}", type);
        List<Permission> permissionList = permissionService.findListByType(type);
        log.info("根据资源类型获取权限成功! permissionList = {}", permissionList);

        return new ResponseResult(false,"获取成功！",SUCCESS_CODE,permissionList);
    }

    /**

    /**
     * 更新权限信息
     * @param permission
     * @return
     */
    @OperationLog(value = "编辑权限")
    @CacheEvict(value = "menuListCache", allEntries = true)
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新权限信息",notes = "更新权限信息")
    public ResponseResult updatePermission(HttpServletRequest request,@RequestBody Permission permission){
        log.debug("编辑权限参数! id= {}, permission = {}",permission.getId(), permission);

        if(null == permission.getId()){
            return new ResponseResult(true,DATA_ERROR,"ID不能为空！");
        }

        //获取不进行xss过滤的request对象
        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);

        permission.setIcon(orgRequest.getParameter("icon"));

        permissionService.updateSelective(permission);
        log.info("编辑权限成功! id= {}, permission = {}", permission.getId(), permission);
        return new ResponseResult(false,SUCCESS_CODE,"编辑成功！");
    }

}
