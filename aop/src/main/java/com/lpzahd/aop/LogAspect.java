package com.lpzahd.aop;

import android.os.Looper;
import android.util.Log;

import com.lpzahd.Config;
import com.lpzahd.Strings;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.TimeUnit;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
@Aspect
public class LogAspect {

    private static volatile boolean enabled = true;

    public static void setEnabled(boolean enabled) {
        LogAspect.enabled = enabled;
    }

    //带有Log注解的所有类
    @Pointcut("within(@com.lpzahd.aop.api.Log *)")
    public void withinAnnotatedClass() {
    }

    //在带有Log注解的所有类，除去synthetic修饰的方法
    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    //在带有Log注解的所有类，除去synthetic修饰的构造方法
    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    }

    //在带有Log注解的方法
    @Pointcut("execution(@com.lpzahd.aop.api.Log * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    //在带有Log注解的构造方法
    @Pointcut("execution(@com.lpzahd.aop.api.Log *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    }

    @Around("method() || constructor()")
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!enabled)
            return joinPoint.proceed();

        enterMethod(joinPoint);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);

        exitMethod(joinPoint, result, lengthMillis);

        return result;
    }

    private static void enterMethod(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Class<?> cls = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        StringBuilder builder = new StringBuilder();
        builder.append("--------enter--------")
                .append("\n")
                .append("类 [ ")
                .append(asTag(cls))
                .append(" ] ")
//                .append("\u21E2 ")
                .append("\t");

        builder.append("方法 ")
                .append(methodName)
                .append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append('=');
            builder.append(Strings.toString(parameterValues[i]));
        }
        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append("\t").append("线程 [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }
        builder.append(']');

        Log.e(Config.LOG, builder.toString());
    }

    private static void exitMethod(JoinPoint joinPoint, Object result, long lengthMillis) {
        Signature signature = joinPoint.getSignature();

        Class<?> cls = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && ((MethodSignature) signature).getReturnType() != void.class;

        StringBuilder builder = new StringBuilder()
                .append("--------exit--------")
                .append("\n")
                .append("类 [ ")
                .append(asTag(cls))
                .append(" ] ")
//                .append("\u21E0 ")
                .append("\t")
                .append("方法 [")
                .append(methodName)
                .append(" [")
                .append(lengthMillis)
                .append("ms]");

        if (hasReturnType) {
            builder.append("\t").append("返回值 = ");
            builder.append(Strings.toString(result));
        }

        Log.e(Config.LOG, builder.toString());
    }

    private static String asTag(Class<?> cls) {
        if (cls.isAnonymousClass()) {
            return asTag(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }

}
