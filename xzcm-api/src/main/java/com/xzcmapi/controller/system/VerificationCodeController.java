package com.xzcmapi.controller.system;

import com.xiaoleilu.hutool.http.HttpUtil;
import com.xzcmapi.common.Constant;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.util.CodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/code")
@Api(description = "验证码")
public class VerificationCodeController extends BaseController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/generate")
    @ApiOperation(value = "随机生成验证码",notes = "随机生成验证码")
    public ResponseResult generate(HttpServletRequest request,
                                           HttpServletResponse response){
        try {
            Map<String, Object> map = CodeUtil.generateCodeAndPic();

            String clientIP = HttpUtil.getClientIP(request);

            //通过redis验证 保存验证码
            stringRedisTemplate.boundValueOps(clientIP).set(map.get("code").toString(), Constant.VER_CODE_TIME, TimeUnit.MINUTES);

            //写入四位数的code,放在session中
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("code",map.get("code").toString());

            //禁止图像缓存
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", -1);
            //设置类型
            response.setContentType("image/png");
            OutputStream out = response.getOutputStream();
            ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", out);
            out.flush();
            out.close();
            return ResponseResult.toResponseResult(null);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseResult(true,"验证码生成失败！",INTERNAL_ERROR);
        }

    }
}
