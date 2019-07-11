package com.xzcmapi.controller.h5;


import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.Activity;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.Player;
import com.xzcmapi.entity.ReportForm;
import com.xzcmapi.mapper.GiftLogMapper;
import com.xzcmapi.model.GiftPayModel;
import com.xzcmapi.model.H5VoteModel;
import com.xzcmapi.service.*;
import com.xzcmapi.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/api/wx")
@Api(description = "微信支付管理")
public class WxPaymentController extends BaseController {

//    private static String APPID = "wxd0ee876c47b05860";

//    private static String MERID = "1520920221";

//    private static String SIGNKEY = "NRIiC3M5EOeSit3vX9AA7NUFRavkf7be";

//    private static String appSecret = "4d292ef62440fb42f61bf46fe96057c4";

    private static String CALLBACK = "http://47.107.180.92:8081/api/wx/notify";

    private static String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    private static String getOpenIdurl = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    public final static String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";


    @Autowired
    private GiftLogService giftLogService;
    @Autowired
    private SystemParamService systemParamService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ReportFormService reportFormService;


    @ResponseBody
    @PostMapping(value = "/pay" ,produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "微信支付接口 -- 返回支付prepay_id",notes = "微信支付接口 -- 返回支付prepay_id")
    public ResponseResult weixinPayWap(HttpServletRequest request, @RequestBody GiftPayModel giftPay) {
        Long gifId = giftPay.getGitId();
        GiftLog giftLog = giftLogService.findById(gifId);

        if(gifId == null || giftLog == null)
            return new ResponseResult(true,"此礼物id没有对应的礼物申请记录！",CodeMessage.PARAMSERROR.getCode(),null);
        String openId = giftLog.getOpenId();
        if(openId == null)
            return new ResponseResult(true,"openId为空！",CodeMessage.PARAMSERROR.getCode(),null);


        Map<String, String> stringStringMap = systemParamService.weChat();
        String APPID = stringStringMap.get("appId");
        String MERID = stringStringMap.get("payAccount");
        String SIGNKEY = stringStringMap.get("payRsa");



        JSONObject result = new JSONObject();
        String spbill_create_ip = getIpAddr(request);//生产
        log.info("spbill_create_ip="+spbill_create_ip);
        String scene_info = "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"h5.leesummer.cn\",\"wap_name\": \"信息认证\"}}";
        String tradeType = "JSAPI";//支付标记
        String MD5 = "MD5";//虽然官方文档不是必须参数，但是不送有时候会验签失败
        String subject = request.getParameter("subject");//前端上送的支付主题
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
        params.put("body",subject);
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
                "<body>"+subject+"</body>"+//
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
            String return_code  = (String) map.get("return_code");
            String return_msg = (String) map.get("return_msg");
            if("SUCCESS".equals(return_code) && "OK".equals(return_msg)){
                prepay_id = (String) map.get("prepay_id");//调微信支付接口地址
                log.info("prepay_id="+prepay_id);
            }else{
                log.error("统一支付接口获取预支付订单出错");
                return new ResponseResult(true,"支付错误",INTERNAL_ERROR,null);
            }
        } catch (Exception e) {
            log.error("统一支付接口获取预支付订单出错");
            return new ResponseResult(true,"支付错误",INTERNAL_ERROR,null);
        }
        result.put("prepay_id",prepay_id);

        //添加微信支付记录日志等操作,更新唯一Id
        giftLog.setOrderId(out_trade_no);
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



        return ResponseResult.toResponseResult(payParams);
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



    @PostMapping(value = "/notify")
    @ApiOperation(value = "回调地址",notes = "回调地址")
    public void weixinPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader reader = request.getReader();
        String line = "";
        Map map = new HashMap();
        String xml = "<xml><return_code><![CDATA[FAIL]]></xml>";;
        JSONObject dataInfo = new JSONObject();
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        request.getReader().close();
        log.info("----接收到的报文---"+inputString.toString());
        if(inputString.toString().length()>0){
            map = XMLUtils.parseXmlToList(inputString.toString());
        }else{
            log.info("接受微信报文为空");
        }
        log.info("map="+map);
        if(map!=null && "SUCCESS".equals(map.get("result_code"))){
            xml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            String out_trade_no = (String)map.get("out_trade_no");
            if(out_trade_no == null)
                log.error("回调的支付orderId为空");
            GiftLog giftLog = new GiftLog();
            giftLog.setOrderId(out_trade_no);
            giftLog = giftLogService.findOne(giftLog);


            Player player = playerService.findById(giftLog.getPlayerId());

            Long activityId = player.getActivityId();
            Activity activity = activityService.findById(activityId);

            String transaction_id = (String)map.get("transaction_id");
            giftLog.setBusinessId(transaction_id);
            giftLog.setStatus(0);
            giftLog.setActivityId(activityId);
            giftLog.setCreator(activity.getCreator());
            BigDecimal gift = player.getGift();
            player.setGift(gift != null?gift.add(giftLog.getGiftAmount()):giftLog.getGiftAmount());
            giftLogService.updateSelective(giftLog);
            playerService.updateSelective(player);

            BigDecimal activityAmount = activity.getActivityAmount();
            activity.setActivityAmount(activityAmount != null?activityAmount.add(giftLog.getGiftAmount()):giftLog.getGiftAmount());
            activity.setUpdateTime(new Date());
            activityService.updateSelective(activity);
            H5VoteModel h5VoteModel = new H5VoteModel();
            h5VoteModel.setOpenId(giftLog.getOpenId());
            h5VoteModel.setPlayerId(giftLog.getPlayerId());
            playerService.vote(request,h5VoteModel,giftLog.getGiftAmount().multiply(new BigDecimal(3)).intValue(),true);

            //更新结算报表和充值报表
            ReportForm reportForm = new ReportForm();
            reportForm.setCreateTime(new Date());
            reportForm.setCreator(0L);
            //查询今天的数据
            Long creator = activity.getCreator();
            ReportForm day = reportFormService.getDaySett(creator);
            //查询今天充值的活动数
            Integer todayActivity = giftLogService.getTodayActivity(creator);
            if(day == null){
                reportForm.setAmount(giftLog.getGiftAmount());
                reportForm.setStatus(1);//默认未结算
                reportForm.setUserId(creator);
                reportForm.setType(0);//结算
                reportForm.setActivitys(todayActivity == null?1:todayActivity);
                reportFormService.save(reportForm);
            }else {
                day.setActivitys(todayActivity);
                day.setAmount((day.getAmount()==null?new BigDecimal(0):day.getAmount()).add(giftLog.getGiftAmount()));
                reportFormService.updateSelective(day);
            }
            ReportForm dayCharge = reportFormService.getDayCharge(creator);
            ReportForm charge = new ReportForm();
            charge.setCreateTime(new Date());
            charge.setCreator(0L);
            if(dayCharge == null){
                charge.setAmount(giftLog.getGiftAmount());
                charge.setType(1);//充值
                charge.setTransactions(1);
                charge.setUserId(creator);
                reportFormService.save(charge);
            }else{
                dayCharge.setTransactions(dayCharge.getTransactions() == null?1:(dayCharge.getTransactions()+1));
                dayCharge.setAmount((dayCharge.getAmount()==null?new BigDecimal(0):dayCharge.getAmount()).add(giftLog.getGiftAmount()));
                reportFormService.updateSelective(dayCharge);
            }
        }else{
            //失败的业务。。。
        }
        //告诉微信端已经确认支付成功
        response.getWriter().write(xml);
    }

    @ResponseBody
    @PostMapping(value = "/getOpenId" ,produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "微信用户授权",notes = "微信用户授权")
    public ResponseResult getOpenId(@RequestParam String code){
        Map<String, String> stringStringMap = systemParamService.weChat();
        String APPID = stringStringMap.get("appId");
        String appSecret = stringStringMap.get("appSecret");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getOpenIdurl);
        stringBuffer.append("appid=").append(APPID).append("&secret=").append(appSecret)
                .append("&code=").append(code).append("&grant_type=authorization_code");
        String map = WebUtils.get(stringBuffer.toString());
        return ResponseResult.toResponseResult(map);
    }

    @ResponseBody
    @PostMapping(value = "/sign" ,produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "分享参数签名",notes = "分享参数签名")
    public ResponseResult sign(@RequestParam(required = false) String url){
        if(url == null)
            url = "h5.qianchengchuanmei.com/";
        Map<String, String> stringStringMap = systemParamService.weChat();
        String APPID = stringStringMap.get("appId");
        String appSecret = stringStringMap.get("appSecret");
        Map<String, String> accessToken = getAccessToken(APPID, appSecret);
        if(accessToken == null)
            return new ResponseResult(true,"获取accessToken失败！",INTERNAL_ERROR);
        String accessToken1 = accessToken.get("accessToken");
        Map<String, String> stringStringMap1 = JsapiTicket(accessToken1);
        if(stringStringMap1 == null)
            return new ResponseResult(true,"获取ticket失败！",INTERNAL_ERROR);
        String ticket = stringStringMap1.get("ticket");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String messageDigest = MD5Utils.getMessageDigest(String.valueOf(new Random().nextInt(10000)).getBytes());
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("jsapi_ticket",ticket);
        payParams.put("noncestr",messageDigest);
        payParams.put("timestamp",timestamp.substring(0,timestamp.length() - 3));
        payParams.put("url",url);
//        String str = "jsapi_ticket="+ticket+"&noncestr="+messageDigest+"&timestamp="+timestamp+"&url="+url;
//        String sign = DigestUtils.sha1Hex(str);
        String sign = null;
        try {
            sign = SHA1(payParams);
        } catch (DigestException e) {
            e.printStackTrace();
        }
        payParams.put("signature",sign);
        payParams.put("appId",APPID);
        return ResponseResult.toResponseResult(payParams);
    }

    /**
     * 微信小程序获取accessToken
     */
        // 网页授权接口
        public Map<String, String> getAccessToken(String appId,String appSecret) {
            String requestUrl = GetPageAccessTokenUrl.replace("APPID", appId).replace("SECRET", appSecret);
            CloseableHttpClient client = null;
            Map<String, String> result = new HashMap<String, String>();
            String accessToken = null;
            try {
                client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(requestUrl);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = client.execute(httpget, responseHandler);
                JSONObject OpenidJSONO = JSONObject.fromObject(response);
                log.info(OpenidJSONO.toString());
                accessToken = String.valueOf(OpenidJSONO.get("access_token"));
                result.put("accessToken", accessToken);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
            return result;
        }

    /**
     * @description 获取ticket
     */
        // 网页授权接口
        public static Map<String, String> JsapiTicket(String accessToken) {
            String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
            String requestUrl = GetPageAccessTokenUrl.replace("ACCESS_TOKEN", accessToken);
            HttpClient client = null;
            Map<String, String> result = new HashMap<String, String>();
            try {
                client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(requestUrl);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = client.execute(httpget, responseHandler);
                JSONObject OpenidJSONO = JSONObject.fromObject(response);
                String errcode = String.valueOf(OpenidJSONO.get("errcode"));
                String errmsg = String.valueOf(OpenidJSONO.get("errmsg"));
                String ticket = String.valueOf(OpenidJSONO.get("ticket"));
                String expires_in = String.valueOf(OpenidJSONO.get("expires_in"));
                result.put("errcode", errcode);
                result.put("errmsg", errmsg);
                result.put("ticket", ticket);
                result.put("expires_in", expires_in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
            return result;
        }
    /** 
          * SHA1 安全加密算法 
          * @param maps 参数key-value map集合 
          * @return 
          * @throws DigestException  
          */
    public String SHA1(Map<String,Object> maps) throws DigestException {
        //获取信息摘要 - 参数字典排序后字符串  
        String decrypt = getOrderByLexicographic(maps);
        try {
            //指定sha1算法  
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decrypt.getBytes());
            //获取字节数组
            byte messageDigest[] = digest.digest();
            // Create Hex String  
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DigestException("签名错误！");
        }
    }
    /** 
          * 获取参数的字典排序 
          * @param maps 参数key-value map集合 
          * @return String 排序后的字符串 
          */
    private String getOrderByLexicographic(Map<String,Object> maps){
        System.out.println(splitParams(lexicographicOrder(getParamsName(maps)),maps));
        return splitParams(lexicographicOrder(getParamsName(maps)),maps);
    }
    /** 
          * 获取参数名称 key 
          * @param maps 参数key-value map集合 
          * @return 
          */
    private List<String> getParamsName(Map<String,Object> maps){
        List<String> paramNames = new ArrayList<String>();
        for(Map.Entry<String,Object> entry : maps.entrySet()){
            paramNames.add(entry.getKey());
        }
        return paramNames;
    }
    /** 
          * 参名称按字典排序 
          * @param paramNames 参数名称List集合 
          * @return 排序后的参数名称List集合 
          */
    private  List<String> lexicographicOrder(List<String> paramNames){
        Collections.sort(paramNames);
        return paramNames;
    }
    /** 
          * 拼接排序好的参数名称和参数值 
          * @param paramNames 排序后的参数名称集合 
          * @param maps 参数key-value map集合 
          * @return String 拼接后的字符串 
          */
    private  String splitParams(List<String> paramNames,Map<String,Object> maps){
        StringBuilder paramStr = new StringBuilder();
        for(String paramName : paramNames){
            paramStr.append(paramName);
            for(Map.Entry<String,Object> entry : maps.entrySet()){
                if(paramName.equals(entry.getKey())){
                    paramStr.append("="+String.valueOf(entry.getValue())+"&");
                }
            }
        }
        paramStr.deleteCharAt(paramStr.length()-1);
        return paramStr.toString();
    }
}
