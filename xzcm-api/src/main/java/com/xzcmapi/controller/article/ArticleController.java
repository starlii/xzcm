package com.xzcmapi.controller.article;


import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.Authorization;
import com.xzcmapi.annotation.CurrentUser;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.Article;
import com.xzcmapi.entity.User;
import com.xzcmapi.model.ArticleModel;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/api/article")
@Api(description = "文章管理")
public class ArticleController extends BaseController {


    @Autowired
    private ArticleService articleService;

    @GetMapping("/list")
    @ApiOperation(value = "查找文章--分页",notes = "查找文章--分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", defaultValue = "1", dataType = "integer", paramType = "query",
                    value = "页数 (1..N)"),
            @ApiImplicitParam(name = "size", defaultValue = "10", dataType = "integer", paramType = "query",
                    value = "每页大小"),
            @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query",
                    value = "依据什么排序: 属性名1 asc,属性名2 desc. ")
    })
    public ResponseResult get(@RequestParam(value = "title", required = false) String title,
                              @ApiIgnore PageParam pageParam){
        log.debug("request to get Article by params title:{}", title);
        try {
            PageInfo<List<Article>> pageInfo = articleService.get(title,pageParam);
            return new ResponseResult<>(false, "", pageInfo);
        } catch (Exception e) {
            log.error(e);
            return new ResponseResult<>(true, FAILURE_LOAD_MESSAGE, e.getMessage());
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加文章",notes = "添加文章")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult add(@RequestBody ArticleModel articleModel, @CurrentUser User user){
        try{
            if (articleModel.getTitle() == null)
                return new ResponseResult(true,"文章标题为空！",null);
            articleService.add(articleModel,user);
            return new ResponseResult<>(false, SUCCESS_SAVE_MESSAGE, "");
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_SAVE_MESSAGE,e.getMessage());
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新文章",notes = "更新文章")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult update(@RequestBody ArticleModel articleModel, @CurrentUser User user){
        try{
            if(articleModel.getId() == null)
                return new ResponseResult(true,"文章id为空","");
            articleService.update(articleModel,user);
            return new ResponseResult<>(false, SUCCESS_UPDATE_MESSAGE, "");
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_UPDATE_MESSAGE,e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除文章",notes = "删除文章")
    @Authorization
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult delete(@PathVariable("id") Long id){
        try {
            if (id == null)
                return new ResponseResult(true,"id 为空","");
            articleService.delete(id);
            return new ResponseResult<>(false, SUCCESS_DELETE_MESSAGE, "");
        }catch (Exception e){
            return new ResponseResult(true,FAILURE_DELETE_MESSAGE,e.getMessage());
        }
    }

}
