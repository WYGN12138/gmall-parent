package com.atguigu.gmall.item.cache.aspect;

import ch.qos.logback.core.boolex.EvaluationException;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.item.cache.annotation.GmallCache;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.apache.ibatis.ognl.Evaluation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Aspect //声明这是一个切面
@Component
public class CacheAspect {
    //    @Before("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
//    public void  haha(){
//        System.out.println("前置通知......");
//    }
//    @Around("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
//    public Object around(ProceedingJoinPoint joinPoint){
//        //1.获取签名，将要执行的目标方法的签名
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        //2.获取当时调用者调用的目标方法传递的所有参数
//        Object[] args = joinPoint.getArgs();
//        System.out.println(joinPoint.getTarget());//target真正目标对像
//        System.out.println(joinPoint.getThis());//this式当前代理对象（给目标对象逃了一层）
//        //3.放行目标方法
//        Method method = signature.getMethod();
//        //(Object obj,Object args)
//        //前置通知
//        Object result=null;
//        try {
//            //目标方法执行 并返回返回值 修改参数
//             result = method.invoke(joinPoint.getTarget(), args);
//             //返回通知
//        } catch (Exception e) {
////            e.printStackTrace();
//            throw new RuntimeException(e);
//            //异常通知
//        }finally {
//            //后置通知
//        }
//        return result;
//    }
    @Autowired
    CacheOpsService cacheOpsService;
    ExpressionParser parser = new SpelExpressionParser();
    ParserContext context = new TemplateParserContext();

    @Around("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object arg = joinPoint.getArgs()[0];
        Object result = null;
        //key不同方法可能不一样
        String cacheKey = determinCacheKey(joinPoint);
        //1.先查缓存

        Type   returnType=  getMethodGenericReturnType(joinPoint);

//        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
        Object cacheData = cacheOpsService.getCacheData(cacheKey,returnType);
        //2.缓存
        if (cacheData == null) {
            //3.准备回源
            //4.先问布隆
            //TODO 布隆不通用
            boolean contains = cacheOpsService.bloomContains(arg);
            if (!contains) {
                return null;
            }
            //5。布隆说有准备回源，加锁
            boolean lock = false;
            try {
                lock = cacheOpsService.tryLock((Long) arg);
                if (lock) {
                    //6.获取到锁，开始回源
                    Object proceed = joinPoint.proceed(joinPoint.getArgs());
                    //7.调用成功，重新保存缓存
                    cacheOpsService.saveData(cacheKey, arg);
                    return proceed;

                } else {
                    Thread.sleep(1000L);
                    return cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
                }
            } finally {
                if (lock) cacheOpsService.unlock((Long) arg);

            }
        }
        // 缓存中有直接返回
        return cacheData;
    }

    /**
     * 获取目标的紧缺返回值类型
     * @param joinPoint
     * @return
     */
    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Type type = method.getGenericReturnType();
        return type;
    }

    /**
     * 根据当前整个连接点执行的信息确定缓存用什么key
     *
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2.拿到目标方法
        Method method = signature.getMethod();
        //3.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        String expression = cacheAnnotation.cacheKey();

        //计算表达式
        String cacheKey = evaluationException(expression, joinPoint, String.class);
        return cacheKey;
    }

    private <T> T evaluationException(String expression,
                                      ProceedingJoinPoint joinPoint,
                                      Class<T> clz) {
        //1.创建表达式解析器
        Expression exp = parser.parseExpression(expression, context);
        //2.sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        //3.取出所有参数 绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params", args);
        //上下文中的变量
        T expValue = exp.getValue(evaluationContext, clz);

        return expValue;
    }
}
