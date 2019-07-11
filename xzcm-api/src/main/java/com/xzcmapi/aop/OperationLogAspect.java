package com.xzcmapi.aop;


import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.json.JSONUtil;
import com.xzcmapi.common.Constant;
import com.xzcmapi.entity.Log;
import com.xzcmapi.entity.User;
import com.xzcmapi.mapper.UserMapper;
import com.xzcmapi.service.LogService;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
public class OperationLogAspect {

    //本地异常日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);
    private ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    private ThreadLocal<Log> localLog = new ThreadLocal<Log>();

    /**
     * 注入soaClient用于把日志保存数据库
     */
    @Resource
    private LogService logService;
    @Resource
    private HttpServletRequest request; //这里可以获取到request
    @Autowired
    private UserMapper userMapper;


    /**
     * 定义日志切入点
     */
    @Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
    public void logPointCut() {
    }

    /**
     * 后置通知 用于拦截service层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            //记录请求开始时间
            startTime.set(System.currentTimeMillis());
            //请求的参数
            Object[] args = joinPoint.getArgs();
            String username = "";
            if (request.getAttribute(Constant.CURRENT_USER_ID) != null) {
                Long userId = (Long) request.getAttribute(Constant.CURRENT_USER_ID);
                User user = userMapper.selectByPrimaryKey(userId);
                if (user != null) {
                    username = user.getUsername();
                }
            }
            //*========数据库日志=========*//
            Log log = new Log();
            log.setAppName("");
            log.setUsername(username);
            log.setLogType(0);
            log.setMethodName(getFullMethodName(joinPoint));
            log.setRequestMethod(request.getMethod());
            log.setParams(request.getParameterMap().toString());
//            log.setContent(this.getRemark(request.getParameterMap()));
            log.setOperation(getMethodDescription(joinPoint));
            log.setRequestIp(HttpUtil.getClientIP(request));
            log.setRequestUri(request.getRequestURI());
            log.setUserAgent(request.getHeader("User-Agent"));

//            log.setExceptionCode(null);
//            log.setExceptionDetail(null);

            localLog.set(log);
        } catch (Exception e) {
            //记录本地异常日志
            logger.error("==后置通知异常==");
            logger.error("异常信息:{}", e.getMessage());
        }
    }

    /**
     * 切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
     *
     * @param ret
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "logPointCut()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容

        Log log = localLog.get();
//        log.setTimeConsuming(System.currentTimeMillis() - startTime.get());

        // 保存数据库
        logService.save(log);
    }

    /**
     * 异常通知 用于拦截service层记录异常日志
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Map<String, String[]> requestParams = new HashMap<>();
        //请求的参数
        Object[] args = joinPoint.getArgs();

        try {
            /*==========数据库日志=========*/
            Log log = localLog.get();

            log.setTimeConsuming(System.currentTimeMillis() - startTime.get());
            log.setLogType(1);

            log.setExceptionCode(e.getClass().getName());
            log.setExceptionDetail(e.getMessage());

            //保存数据库
            logService.save(log);
        } catch (Exception ex) {
            //记录本地异常日志
            ex.printStackTrace();
            logger.error("异常方法全路径:{},异常信息:{},请求参数:{}", getFullMethodName(joinPoint), e.getMessage(), JSONUtil.toJsonStr(args));
        }
    }

    /**
     * 获取注解中对方法的描述信息
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    private static String getMethodDescription(JoinPoint joinPoint)
            throws Exception {
        String description = "";
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        ApiOperation operationLog = method.getAnnotation(ApiOperation.class);
        if (null != operationLog) {
            description = operationLog.value();
        }
        return description;
    }

    /**
     * 获取请求的方法名全路径
     *
     * @param joinPoint
     * @return
     */
    private static String getFullMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //请求的方法名全路径
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        return className + "." + methodName + "()";
    }
}