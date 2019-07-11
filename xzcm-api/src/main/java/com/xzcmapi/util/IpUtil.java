package com.xzcmapi.util;

import lombok.extern.slf4j.Slf4j;
import net.ipip.ipdb.City;
import net.ipip.ipdb.CityInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Arrays;

@Slf4j
public class IpUtil {

//    @Value("classpath:static/data/ipipfree.ipdb")
//    private static String dbPath;

    public static String getCity(String ip){
        StringBuilder city = new StringBuilder();
        try {
//            File file = ResourceUtils.getFile("classpath:static/data/ipipfree.ipdb");
            // City类可用于IPDB格式的IPv4免费库，IPv4与IPv6的每周高级版、每日标准版、每日高级版、每日专业版、每日旗舰版
            City db = new City("/usr/local/xzcm/ipipfree.ipdb");
            // db.find(address, language) 返回索引数组
            log.info(Arrays.toString(db.find("1.1.1.1", "CN")));
            // db.findInfo(address, language) 返回 CityInfo 对象
            CityInfo info = db.findInfo(ip, "CN");
//            log.info("解析出来的地址:{}",info);
            city.append(info.getCountryName()).append(" ").append(info.getRegionName()).append(" ")
                    .append(info.getCityName());
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return city.toString();
    }

    public static void main(String[] args) {
        try {
            // City类可用于IPDB格式的IPv4免费库，IPv4与IPv6的每周高级版、每日标准版、每日高级版、每日专业版、每日旗舰版
            City db = new City("D:\\17monipdb\\ipipfree.ipdb");
            // db.find(address, language) 返回索引数组
            System.out.println(Arrays.toString(db.find("1.1.1.1", "CN")));
            // db.findInfo(address, language) 返回 CityInfo 对象
            CityInfo info = db.findInfo("118.28.1.1", "CN");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
