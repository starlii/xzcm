package com.xzcmapi.mapper;

import com.xzcmapi.entity.FileMaterial;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.hibernate.validator.constraints.SafeHtml;

import java.util.List;

public interface FileMaterialMapper extends MyMapper<FileMaterial> {

    @Select("select file_url  from\txzcm_file_material WHERE related_id = #{activityId} AND type = #{type} order by create_time desc")
    List<String> getImages(@Param("activityId")Long activityId,
                           @Param("type")Integer type);
    @Select("select file_url  from\txzcm_file_material WHERE related_id = #{activityId} AND type = #{type} and star is null order by create_time desc")
    List<String> getImagesForAdmin(@Param("activityId")Long activityId,
                           @Param("type")Integer type);
    @Select("select count(*) from\txzcm_file_material WHERE related_id = #{activityId} AND type = #{type} and star=0 order by create_time desc")
    Integer getHasStar(@Param("activityId")Long activityId,
                       @Param("type")Integer type);

    List<FileMaterial> getDelete(@Param("ids") List<Long> ids);
}
