package com.xzcmapi.service;

import com.xzcmapi.entity.SystemParam;
import com.xzcmapi.model.SettingModel;

import java.util.Map;

public interface SystemParamService extends BaseService<SystemParam>{

    Map<String,String> basic();

    Map<String,String> weChat();

    Integer update(SettingModel settingModel);
}
