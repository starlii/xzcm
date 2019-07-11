package com.xzcmapi.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.model.GiftPayModel;
import com.xzcmapi.service.GiftLogService;
import com.xzcmapi.service.SystemParamService;
import com.xzcmapi.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class WeChatPaYService {

    private static final Logger log = LoggerFactory.getLogger(WeChatPaYService.class);

//    private static String APPID = "wxd0ee876c47b05860";

//    private static String MERID = "1520920221";

//    private static String SIGNKEY = "NRIiC3M5EOeSit3vX9AA7NUFRavkf7be";

//    private static String appSecret = "4d292ef62440fb42f61bf46fe96057c4";

//    private static String CALLBACK = "http://47.107.180.92:8081/api/wx/notify";
    private static String CALLBACK = "http://39.108.248.112:8081/api/wx/notify";

    private static String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

//    private static String getOpenIdurl = "https://api.weixin.qq.com/sns/oauth2/access_token?";

    @Autowired
    private GiftLogService giftLogService;
    @Autowired
    private SystemParamService systemParamService;


    public Map<String,Object> weixinPayWap(HttpServletRequest request,GiftPayModel giftPay) {

        Map<String, String> map1 = systemParamService.weChat();

        String APPID = map1.get("appId");
        String MERID = map1.get("payAccount");
        String SIGNKEY = map1.get("payRsa");
        String officialAccount = map1.get("officialAccount")+"钻石";

        Long gifId = giftPay.getGitId();
        GiftLog giftLog = giftLogService.findById(gifId);
        String openId = giftLog.getOpenId();
        JSONObject result = new JSONObject();
        String spbill_create_ip = getIpAddr(request);//生产
        log.info("spbill_create_ip="+spbill_create_ip);
        String scene_info = "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"h5.leesummer.cn\",\"wap_name\": \"信息认证\"}}";
        String tradeType = "JSAPI";//支付标记
        String MD5 = "MD5";//虽然官方文档不是必须参数，但是不送有时候会验签失败
//        String subject = request.getParameter("subject");//前端上送的支付主题
        String total_amount = giftLog.getGiftAmount().toString();//前端上送的支付金额
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        //金额转化为分为单位 微信支付以分为单位
        String finalmoney = StringUtils.getMoney(total_amount);
        int randomNum  = (int) (Math.random() * 1999+5000);
        String out_trade_no = TimeSearchUtil.getSysTime("yyyyMMddHHmmss") + randomNum;
        //随机数
        String nonce_str= MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        //签名数据
        Map<String,Object> params = new HashMap<>();
        params.put("appid",APPID);
        params.put("body",officialAccount);
        params.put("mch_id",MERID);
        params.put("nonce_str",nonce_str);
        params.put("notify_url",CALLBACK);
        params.put("out_trade_no",out_trade_no);
        params.put("scene_info",scene_info);
        params.put("sign_type",MD5);
        params.put("spbill_create_ip",spbill_create_ip);
        params.put("total_fee",finalmoney);
        params.put("trade_type",tradeType);
        params.put("openid",openId);
        //签名MD5加密
        String sign = getSign(params,SIGNKEY);//com.xzcmapi.util.MD5.MD5Encode(sb.toString()).toUpperCase();
        log.info("签名数据:"+sign);
        //封装xml报文
        String xml="<xml>"+
                "<appid>"+ APPID+"</appid>"+
                "<mch_id>"+ MERID+"</mch_id>"+
                "<nonce_str>"+nonce_str+"</nonce_str>"+
                "<sign>"+sign+"</sign>"+
                "<body>"+officialAccount+"</body>"+//
                "<out_trade_no>"+out_trade_no+"</out_trade_no>"+
                "<total_fee>"+finalmoney+"</total_fee>"+//
                "<trade_type>"+tradeType+"</trade_type>"+
                "<openid>"+openId+"</openid>"+
                "<notify_url>"+CALLBACK+"</notify_url>"+
                "<sign_type>"+MD5+"</sign_type>"+
                "<scene_info>"+scene_info+"</scene_info>"+
                "<spbill_create_ip>"+spbill_create_ip+"</spbill_create_ip>"+
                "</xml>";

        String prepay_id = "";
        Map map = new HashMap();
        try {
            //预下单 获取接口地址
            map = WebUtils.getMwebUrl(createOrderURL, xml);
            log.info(map.toString());
            String return_code  = (String) map.get("return_code");
            String return_msg = (String) map.get("return_msg");
            if("SUCCESS".equals(return_code) && "OK".equals(return_msg)){
                prepay_id = (String) map.get("prepay_id");//调微信支付接口地址
                log.info("prepay_id="+prepay_id);
            }else{
                log.error("统一支付接口获取预支付订单出错");
            }
        } catch (Exception e) {
            log.error("统一支付接口获取预支付订单出错");
        }
        result.put("prepay_id",prepay_id);

        //添加微信支付记录日志等操作,更新唯一Id
        giftLog.setOrderId(out_trade_no);
        giftLog.setIp(spbill_create_ip + " "+ IpUtil.getCity(spbill_create_ip));
        giftLogService.updateSelective(giftLog);

        //支付参数
        String messageDigest = MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("appId",APPID);
        payParams.put("timeStamp",String.valueOf(System.currentTimeMillis()));
        payParams.put("nonceStr",messageDigest);
        payParams.put("package","prepay_id="+prepay_id);
        payParams.put("signType",MD5);
        String sign1 = getSign(payParams,SIGNKEY);
        payParams.put("paySign",sign1);

        return payParams;
    }

    /**
     * 获取用户实际ip
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static String getSign(Map<String,Object> map,String SIGNKEY){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + SIGNKEY;
        //Util.log("Sign Before MD5:" + result);
        result = MD5.MD5Encode(result).toUpperCase();
        //Util.log("Sign Result:" + result);
        return result;
    }
}
