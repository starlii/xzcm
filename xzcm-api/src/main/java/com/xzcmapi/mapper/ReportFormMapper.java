package com.xzcmapi.mapper;

import com.xzcmapi.entity.ReportForm;
import com.xzcmapi.model.ChargeModel;
import com.xzcmapi.model.ReportSearchModel;
import com.xzcmapi.model.SettlementModel;
import com.xzcmapi.util.MyMapper;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;

public interface ReportFormMapper extends MyMapper<ReportForm> {

    List<SettlementModel> settlement( @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime,
                                      @Param("username") String username,
                                      @Param("userId") Long userId,
                                      @Param("role")String role);

    List<ChargeModel> charge(@Param("startTime") Date startTime,
                             @Param("endTime") Date endTime,
                             @Param("username") String username,
                             @Param("userId") Long userId,
                             @Param("role")String role);

    @Select("SELECT id,activitys,amount FROM xzcm_report_form WHERE TO_DAYS(create_time) = TO_DAYS(NOW()) AND type = 0 AND user_id = #{userId}")
    ReportForm getDaySett(@Param("userId")Long userId);

    @Select("SELECT id,amount,transactions FROM xzcm_report_form WHERE TO_DAYS(create_time) = TO_DAYS(NOW()) AND type = 1 AND user_id = #{userId}")
    ReportForm getDayCharge(@Param("userId")Long userId);

}
