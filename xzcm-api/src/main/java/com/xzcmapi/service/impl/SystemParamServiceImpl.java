package com.xzcmapi.service.impl;

import com.xzcmapi.entity.SystemParam;
import com.xzcmapi.model.BasicSettingModel;
import com.xzcmapi.model.SettingModel;
import com.xzcmapi.model.WechatSettingModel;
import com.xzcmapi.service.SystemParamService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SystemParamServiceImpl extends BaseServiceImpl<SystemParam> implements SystemParamService {

    @Override
    public Map<String, String> basic() {
        Map<String,String> result = new HashMap<>();
        Example example = new Example(SystemParam.class);
        example.createCriteria().orEqualTo("key","platformName")
                .orEqualTo("key","platformDomain")
                .orEqualTo("key","newUserPackage");
        List<SystemParam> systemParams = selectByExample(example);
        for (SystemParam systemParam : systemParams) {
            result.put(systemParam.getKey(),systemParam.getValue());
        }
        return result;
    }

    @Override
    public Map<String, String> weChat() {
        Map<String,String> result = new HashMap<>();
        Example example = new Example(SystemParam.class);
        example.createCriteria().andEqualTo("type",1);
        List<SystemParam> systemParams = selectByExample(example);
        for (SystemParam systemParam : systemParams) {
            result.put(systemParam.getKey(),systemParam.getValue());
        }
        return result;
    }

    @Override
    public Integer update(SettingModel settingModel) {
        BasicSettingModel basicSetting = settingModel.getBasicSetting();
        WechatSettingModel wechatSetting = settingModel.getWechatSetting();
        if(basicSetting != null){
            Example example = new Example(SystemParam.class);
            example.createCriteria().orEqualTo("key","platformName")
                    .orEqualTo("key","platformDomain")
                    .orEqualTo("key","newUserPackage");
            List<SystemParam> systemParams = selectByExample(example);
            for (SystemParam systemParam : systemParams) {
                if(systemParam.getKey().equals("platformName")){
                    systemParam.setValue(basicSetting.getPlatformName());
                }else if (systemParam.getKey().equals("platformDomain")){
                    systemParam.setValue(basicSetting.getPlatformDomain());
                }else if(systemParam.getKey().equals("newUserPackage")){
                    systemParam.setValue(basicSetting.getNewUserPackage());
                }
                updateSelective(systemParam);
            }
        }
        if(wechatSetting != null){
            Example example = new Example(SystemParam.class);
            example.createCriteria().andEqualTo("type",1);
            List<SystemParam> systemParams = selectByExample(example);
            for (SystemParam systemParam : systemParams) {
                if(systemParam.getKey().equals("officialAccount") && wechatSetting.getOfficialAccount() != null){
                    systemParam.setValue(wechatSetting.getOfficialAccount());
                }else if(systemParam.getKey().equals("ccountImage") && wechatSetting.getCcountImage() != null){
                    systemParam.setValue(wechatSetting.getCcountImage());
                }else if(systemParam.getKey().equals("appId") && wechatSetting.getAppId() != null){
                    systemParam.setValue(wechatSetting.getAppId());
                }else if(systemParam.getKey().equals("payAccount") && wechatSetting.getPayAccount() != null){
                    systemParam.setValue(wechatSetting.getPayAccount());
                }else if(systemParam.getKey().equals("payRsa") && wechatSetting.getPayRsa() != null){
                    systemParam.setValue(wechatSetting.getPayRsa());
                }else if(systemParam.getKey().equals("apiclientCert") && wechatSetting.getApiclientCert() != null){
                    systemParam.setValue(wechatSetting.getApiclientCert());
                }else if(systemParam.getKey().equals("apiclientKey") && wechatSetting.getApiclientKey() != null){
                    systemParam.setValue(wechatSetting.getApiclientKey());
                }else if(systemParam.getKey().equals("rootca") && wechatSetting.getRootca() != null){
                    systemParam.setValue(wechatSetting.getRootca());
                }else if(systemParam.getKey().equals("follow") && wechatSetting.getFollow() != null){
                    systemParam.setValue(wechatSetting.getFollow());
                }else if(systemParam.getKey().equals("orderMessage") && wechatSetting.getOrderMessage() != null){
                    systemParam.setValue(wechatSetting.getOrderMessage());
                }else if(systemParam.getKey().equals("appSecret") && wechatSetting.getAppSecret() != null){
                    systemParam.setValue(wechatSetting.getAppSecret());
                }
                updateSelective(systemParam);
            }
        }
        return null;
    }
}
