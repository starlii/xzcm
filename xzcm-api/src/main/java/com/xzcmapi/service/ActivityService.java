package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.*;
import com.xzcmapi.model.*;
import io.swagger.models.auth.In;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ActivityService extends BaseService<Activity>{

    /**
     * 多条件查询活动页面
     * @return
     */
    PageInfo<List<ActivityModel>> list(SearchModel searchModel,Long userId,Boolean isSelf);

    Integer add(SaveActivityModel saveActivityModel,Long userId);

    Integer update(SaveActivityModel saveActivityModel, Long userId);

    Integer delete(List<Long> ids,Long userId);

    Integer updateViews(UpdateViewsModel viewsModel,Long userId);

    Integer batchUpdate(BatchUpdateModel batchUpdateModel,Long userId);

    Integer addVotes(Long activityId,Long userId);

    void updateActivityStatus();

    void updateTodayStar();

    QrCodeModel getActivityAccessLink(Long activityId,Long userId);

    H5HomePageModel getH5HomePage(Long activityId, HttpServletRequest request);

    List<H5PlayerDetailModel> getPopularityPlayers(PlayerPageModel playerPageModel);

    SaveActivityModel getActivityDetail(Long activityId,Long userId,Integer type);

    List<GiftLog> getGiftLog(Long activityId,Long userId,Boolean status,Integer self);

    List<VoteLog> getVoteLog(Long activityId,Long userId,Integer self);

    List<GiftSetting> getGift(Long activityId);

    Map<String,Object> hasJoin(Long activityId, String openId);

    List<FileMaterial>  getDeleteModel(Long userId);

    TodayDealsModel getTodayDeals(Long userId,Integer self);

    void deleteImages(Long id,Integer type);
}
