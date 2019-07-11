package com.xzcmapi.mapper;

import com.xzcmapi.entity.Player;
import com.xzcmapi.model.*;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PlayerMapper extends MyMapper<Player> {
    List<PlayerModel> get(@Param("name")String name,
                          @Param("id")Long id,
                          @Param("approvalStatus")Integer approvalStatus,
                          @Param("activityId")Long activityId,
                          @Param("num")Long num,
                          @Param("sort")String sort);

    List<PlayerModel> getAll(@Param("name")String name,
                          @Param("id")Long id,
                          @Param("approvalStatus")Integer approvalStatus,
                          @Param("role")String role,
                          @Param("num")Long num);

    List<ExcelModel> getExcel(@Param("activityId")Long activityId,
                              @Param("playerIds")List<Long> playerIds);

    VotesModel getVotesByActivity(@Param("activityId")Long activityId);

    @Select("SELECT * FROM xzcm_activity_player WHERE activity_id = #{activityId} ORDER BY (ifnull(actual_votes,0) + ifnull(manual_votes,0)) DESC LIMIT 0,10")
    List<Player> getPopularityPlayers(@Param("activityId")Long activityId);

    @Select("SELECT * FROM xzcm_activity_player WHERE activity_id = #{activityId} ORDER BY (ifnull(actual_votes,0) + ifnull(manual_votes,0)) DESC LIMIT #{limitStart},#{limitEnd}")
    List<Player> getPopularityPlayersPage(@Param("activityId")Long activityId,@Param("limitStart")Integer limitStart ,@Param("limitEnd")Integer limitEnd);

    @Select("SELECT * FROM xzcm_activity_player WHERE activity_id = #{activityId} ORDER BY  create_time desc LIMIT 0,10")
    List<Player> getLatestPlayers(@Param("activityId")Long activityId);




    H5PlayerDetailModel getH5PlayerDetail(@Param("playerId") Long playerId,
                                          @Param("activityId")Long activityId);

    @Select("SELECT (IFNULL(actual_votes,0)+IFNULL(manual_votes,0))-#{votes} FROM xzcm_activity_player p WHERE (IFNULL(actual_votes,0)+IFNULL(manual_votes,0)) > #{votes} \n" +
            "and activity_id = #{activityId}\n" +
            "ORDER BY (ifnull(actual_votes,0)+ifnull(manual_votes,0)) ASC LIMIT 0,1")
    Integer gapPre(@Param("votes")Integer votes,
                   @Param("activityId")Long activityId);

    @Select("SELECT\n" +
            "p.name ,p.id playerId,p.image ,(ifnull(p.manual_votes,0)+ifnull(p.actual_votes,0)) votes,\n" +
            "\tr.rowNo AS rank\n" +
            "FROM\n" +
            "\txzcm_activity_player p INNER JOIN (SELECT p.id AS id,(@rowNum:=@rowNum+1) AS rowNo FROM xzcm_activity_player p,\n" +
            "\t(SELECT @rowNum := 0) t\n" +
            "WHERE\n" +
            "\tp.activity_id = #{activityId}  ORDER BY ifnull(p.manual_votes,0)+ifnull(p.actual_votes,0) desc) r ON r.id = p.id limit 0,#{num}")
    List<PlayerRankModel> getPlayerRankModel(@Param("activityId")Long activityId,
                                             @Param("num")Integer num);

    @Select("SELECT\n" +
            "p.name ,p.id playerId,p.image ,(ifnull(p.manual_votes,0)+ifnull(p.actual_votes,0)) votes,\n" +
            "\tr.rowNo AS rank\n" +
            "FROM\n" +
            "\txzcm_activity_player p INNER JOIN (SELECT p.id AS id,(@rowNum:=@rowNum+1) AS rowNo FROM xzcm_activity_player p,\n" +
            "\t(SELECT @rowNum := 0) t\n" +
            "WHERE\n" +
            "\tp.activity_id = #{activityId}  ORDER BY ifnull(p.manual_votes,0)+ifnull(p.actual_votes,0) desc) r ON r.id = p.id ")
    List<PlayerRankModel> getPlayerRankModelForAll(@Param("activityId")Long activityId);

    @Select("SELECT MAX(num) num FROM xzcm_activity_player WHERE activity_id = #{activityId} GROUP BY activity_id")
    Long getLastestNum(@Param("activityId")Long activityId);

    @Select("SELECT id FROM xzcm_activity_player WHERE activity_id = #{activityId} and open_id = #{openId}")
    Long hasJoin(@Param("activityId")Long activityId,
                @Param("openId")String openId);
}
