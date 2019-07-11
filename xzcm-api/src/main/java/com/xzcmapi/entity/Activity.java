package com.xzcmapi.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "xzcm_activity")
public class Activity extends BaseEntity{

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "activity_start_time")
    private Date activityStartTime;

    @Column(name = "activity_end_time")
    private Date activityEndTime;

    @Column(name = "activity_players")
    private Integer activityPlayers;

    @Column(name = "activity_mual_votes")
    private Integer mualVotes;

    @Column(name = "activity_amount")
    private BigDecimal activityAmount;

    @Column(name = "activity_shares")
    private Integer activityShares;

    @Column(name = "activity_url")
    private String activityUrl;

    @Column(name = "activity_status")
    private Integer activityStatus;

    @Column(name = "activity_remark")
    private String activityRemark;

    @Column(name = "activity_image")
    private String activityImage;

    @Column(name = "activity_remarksec")
    private String activityRemarkSec;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "updater")
    private Long updater;

    @Column(name = "star")
    private Long star;

    @Column(name = "activity_act_votes")
    private Integer actVotes;

    @Column(name = "act_views")
    private Integer actViews;

    @Column(name = "mual_views")
    private Integer mualViews;

    @Column(name = "is_default_status")
    private Integer isDefaultStatus;


    @Column(name = "approval_status")
    private Integer approvalStatus;


    public enum Status{
        UNAPPROVAL(0,"待审核"),
        REGISTRATING(1,"报名中"),
        PROCESSING(2,"进行中"),
        OVER(3,"已结束"),
        STARTTODAY(4,"今日开始"),
        OVERTODAY(5,"今日结束"),
        NOTSTART(6,"未开始");
        private Integer value;
        private String remark;

        Status(Integer value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

}
