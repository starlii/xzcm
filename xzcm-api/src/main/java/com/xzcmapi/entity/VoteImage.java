package com.xzcmapi.entity;

import io.swagger.annotations.Api;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_vote_image")
public class VoteImage extends BaseEntity{
    @Column(name = "url")
    private String url;
    @Column(name = "vote_id")
    private Long vote_id;
}
