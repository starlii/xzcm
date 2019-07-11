package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.Player;
import com.xzcmapi.entity.UploadFile;
import com.xzcmapi.entity.VoteLog;
import com.xzcmapi.model.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface PlayerService extends BaseService<Player> {

    PageInfo<List<PlayerModel>> get(PlayerSearchModel searchModel, Long userId,Boolean isAll);

    Player add(SavePlayerModel playerModel, Long userId);

    Integer update(PlayerModel playerModel,Long userId);

    Integer update(BatchPlayerModel batchPlayerModel,Long userId,HttpServletRequest request);

    Integer delete(Long playerId,Long userId);

    UploadFile getExcelData(ExportDataModel exportDataModel, Long userId);

    Integer batchUpdate(BatchUpdateModel batchUpdateModel,Long userId);

    Integer star(Long playerId,Long userId);

    VotesModel getVotesByActivity(Long activityId);

    Integer oneClickAdded(PlayerSearchModel playerSearchModel,Long userId);

    QrCodeModel getPlayerAccessLink(Long playerId,Long userId);

    H5PlayerDetailModel getH5PlayerDetail(Long playerId);

    List<PlayerRankModel> getPlayerRank(Long activityId);

    List<VoteLog> getVoteLog(Long playerId,Long userId);

    List<GiftLog> getGiftLog(Long playerId,Long userId);

    H5PlayerDetailModel vote(HttpServletRequest request,H5VoteModel h5VoteModel,Integer vote,boolean flag);

    Map<String, Object> gift(HttpServletRequest request, H5GiftModel h5GiftModel);

    Integer status(PlayerStatusModel playerStatusModel,Long userId);

    Integer shareCallback(HttpServletRequest request,ShareCallbackModel shareCallbackModel);

    ScheduleJob scheduled(HttpServletRequest request,PlayerScheduledModel playerScheduledModel,Long userId);

    Integer batchDelete(List<Long> playerIds,Long userId);

    Integer scheduledDown(Long playerId,Long userId);

    PlayerModel detail(Long id);

    void updateLockStatus();
}
