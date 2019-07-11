package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.Article;
import com.xzcmapi.entity.User;
import com.xzcmapi.model.ArticleModel;
import com.xzcmapi.model.PageParam;

import java.util.List;

public interface ArticleService extends BaseService<Article> {

    PageInfo<List<Article>> get(String title, PageParam pageParam);
    void add(ArticleModel articleModel,User user);
    void update(ArticleModel articleModel,User user);
    void delete(Long id);
}
