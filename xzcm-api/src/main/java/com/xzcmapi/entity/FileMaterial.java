package com.xzcmapi.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_file_material")
public class FileMaterial extends BaseEntity{

    @Column(name = "file_url")
    private String fileUrl;
    @Column(name = "type")
    private Integer type;
    @Column(name = "related_id")
    private Long relatedId;
    @Column(name = "star")
    private Integer star;
    @Column(name = "batch_num")
    private String batchNum;
    @Column(name = "mongo_file_Id")
    private String mongoFileId;

    public enum Type{
        OTHER(0,"其他"),PLAYER(1,"选手素材"),ACTIVITYQR(2,"活动二维码"),PLAYERQA(3,"选手二维码"),ACTIVITY(4,"活动图片");

        Type(int value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        private int value;
        private String remark;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
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
