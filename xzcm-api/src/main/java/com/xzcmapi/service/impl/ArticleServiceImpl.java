package com.xzcmapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.Article;
import com.xzcmapi.entity.ArticleImage;
import com.xzcmapi.entity.User;
import com.xzcmapi.mapper.ArticleImagesMapper;
import com.xzcmapi.model.ArticleModel;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.service.ArticleService;
import com.xzcmapi.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ArticleServiceImpl extends BaseServiceImpl<Article> implements ArticleService {


    @Autowired
    private ArticleImagesMapper articleImagesMapper;

    @Override
    public PageInfo<List<Article>> get(String title, PageParam pageParam) {
        Example example = new Example(Article.class);
        example.orderBy("updateTime").desc();
        if(title != null)
            example.createCriteria().andLike("title","%"+title+"%");
        PageHelper.startPage(pageParam.getPage(),pageParam.getSize());
        return new PageInfo(selectByExample(example));
    }

    @Override
    public void add(ArticleModel articleModel, User user) {
        //save
        Article article = new Article();
        BeanUtil.copyPropertiesIgnoreNull(articleModel,article);
        article.setCreateTime(new Date());
        article.setCreator(user.getId());
        article.setUpdateId(user.getId());
        article.setUpdateId(user.getId());
        save(article);

        //save picture
        if(articleModel.getUrls() != null && !articleModel.getUrls().isEmpty()){
            List<ArticleImage> images = new ArrayList<>();
            for (String s : articleModel.getUrls()) {
                ArticleImage articleImage = new ArticleImage();
                articleImage.setArticleId(article.getId());
                articleImage.setIsDel(0);
                articleImage.setUrl(s);
                articleImage.setCreateTime(new Date());
                articleImage.setCreator(user.getId());
                images.add(articleImage);
            }
            articleImagesMapper.insertList(images);
        }
    }

    @Override
    public void update(ArticleModel articleModel, User user) {
        Article article = new Article();
        BeanUtil.copyPropertiesIgnoreNull(articleModel,article);
        article.setUpdateId(user.getId());
        article.setUpdateTime(new Date());
        updateSelective(article);
    }

    @Override
    public void delete(Long id) {
        Example example = new Example(ArticleImage.class);
        example.createCriteria().andEqualTo("articleId",id);
        articleImagesMapper.deleteByExample(example);
        deleteById(id);
    }
}
