package com.xzcmapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xzcmapi.common.Constant;
import com.xzcmapi.entity.Log;
import com.xzcmapi.entity.User;
import com.xzcmapi.mapper.UserMapper;
import com.xzcmapi.service.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Transactional
@Service
public class LogServiceImpl extends BaseServiceImpl<Log> implements LogService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public PageInfo<Log> findPage(Integer pageNum, Integer pageSize, String username, String startTime, String endTime) {
        Example example = new Example(Log.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(username)){
            criteria.andLike("logRealname", "%"+username+"%");
        }if(startTime != null && endTime != null){
            criteria.andBetween("createTime", DateUtil.beginOfDay(DateUtil.parse(startTime)), DateUtil.endOfDay(DateUtil.parse(endTime)));
        }
        //倒序
        example.orderBy("createTime").desc();

        //分页
        PageHelper.startPage(pageNum,pageSize);
        List<Log> logList = this.selectByExample(example);

        return new PageInfo<Log>(logList);
    }

    @Override
    public void saveLog(Log log) {
        if (request.getAttribute(Constant.CURRENT_USER_ID) != null) {
            Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
            User user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                log.setUsername(user.getUsername());
                log.setCreator(userId);
            }
        }
        log.setRequestIp(HttpUtil.getClientIP(request));
        log.setRequestUri(request.getRequestURI());
        log.setUserAgent(request.getHeader("User-Agent"));
        this.save(log);
    }
}
