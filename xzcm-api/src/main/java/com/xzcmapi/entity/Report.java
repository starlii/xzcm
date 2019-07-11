package com.xzcmapi.entity;

import com.xzcmapi.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

@Data
@Table(name = "xzcm_report_record")
public class Report extends BaseEntity {

    @Column(name = "ip")
    private String ip;

    @Column(name = "report_page")
    private String reportPage;

    @Column(name = "report_times")
    private Integer reportTimes;

    @Column(name = "report_content")
    private String reportContent;

    @Column(name = "report_type")
    @ApiModelProperty(notes = "举报类型")
    private Integer reportType;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "player_id")
    private Long playerId;

    @Transient
    @ApiModelProperty(notes = "活动名称")
    private String activityName;

    @Transient
    @ApiModelProperty(notes = "选手姓名")
    private String playerName;

    public static String getReportContent(Integer type){
        Map<Integer,String> map = new HashMap<>();
        map.put(1,"网页包含欺诈信息（如：假红包）");
        map.put(2,"网页包含色情信息");
        map.put(3,"网页包含暴力恐怖信息");
        map.put(4,"网页包含政治敏感信息");
        map.put(5,"网页在收集个人隐私信息（如：钓鱼链接）");
        map.put(6,"网页包含诱导分享／关注性质的內容");
        map.put(7,"网页可能包含谣言信息");
        map.put(8,"网页包含赌博信息");
        return map.get(type);
    }
}
