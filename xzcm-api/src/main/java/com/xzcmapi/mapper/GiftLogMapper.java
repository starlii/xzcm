package com.xzcmapi.mapper;

import com.sun.javafx.iio.gif.GIFImageLoader2;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.model.TodayDealsModel;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

public interface GiftLogMapper extends MyMapper<GiftLog> {

    @Select("SELECT IFNULL(SUM(gift_amount),0) FROM xzcm_gift_log WHERE open_id = #{openId}")
    BigDecimal getUserSumGift(@Param("openId")String openId);


    List<GiftLog> getGiftLog(@Param("activityId")Long activityId,
                             @Param("userId")Long userId,
                             @Param("role")String role,
                             @Param("status")Boolean status,
                             @Param("self")Integer self);

    @Select("SELECT COUNT(*) FROM xzcm_gift_log WHERE TO_DAYS(create_time) = TO_DAYS(NOW()) AND creator = #{userId}")
    Integer getTodayActivity(@Param("userId")Long userId);

    TodayDealsModel getTodayDeals(@Param("userId") Long userId,
                                  @Param("role")String role);

    TodayDealsModel getTodayDealsSelf(@Param("userId") Long userId,
                                  @Param("role")String role);
}
