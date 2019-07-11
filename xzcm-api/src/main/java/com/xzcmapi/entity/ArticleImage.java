package com.xzcmapi.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_article_image")
public class ArticleImage extends BaseEntity{

    @Column(name = "article_id")
    @ApiModelProperty(notes = "文章id")
    private Long articleId;

    @ApiModelProperty(notes = "url")
    @Column(name = "url")
    private String url;

    @ApiModelProperty(notes = "是否删除")
    @Column(name = "is_del")
    private Integer isDel;

}
