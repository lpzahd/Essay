package com.lpzahd.aop;

import com.lpzahd.Objects;
import com.lpzahd.aop.api.ThrottleFirst;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
@Aspect
public class TAspect {

    //在带有ThrottleFirst注解的方法
    @Pointcut("execution(@com.lpzahd.test.aoplib.api.ThrottleFirst * *(..))")
    public void throttle() {

    }

    static List<MethodModel> methods;

    static {
        methods = new ArrayList<>();
    }

    public static void clear() {
        methods.clear();
    }

    @Around("throttle()")
    public Object throttleFirst(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();

        String methodName = signature.getName();

        MethodModel methodModel = findMethod(methodName);

        if(methodModel == null) {
            Class<?> cls = signature.getDeclaringType();
            Method method = cls.getDeclaredMethod(methodName);
            Annotation[] annotations = method.getAnnotations();
            long value = 0;
            for(Annotation annotation : annotations) {
                if(annotation instanceof ThrottleFirst) {
                    value = ((ThrottleFirst) annotation).value();
                    break;
                }
            }
            methodModel = new MethodModel();
            methodModel.method = methodName;
            methodModel.throttleTime = value;
            methodModel.preActionTime = 0;
            methods.add(methodModel);
        }


        long currTime = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(currTime - methodModel.preActionTime);

        if(lengthMillis > methodModel.throttleTime) {
            methodModel.preActionTime = currTime;
            return joinPoint.proceed();
        }

        return null;

    }

    private MethodModel findMethod(String methodName) {
        final int size = methods.size();
        for(int i = 0; i<size; i++) {
            MethodModel methodModel = methods.get(i);
            if(Objects.equals(methodName,methodModel.method)) {
                return methodModel;
            }
        }
        return null;
    }

    public static class MethodModel {

        private String method;
        private long throttleTime;
        private long preActionTime;

    }
}
