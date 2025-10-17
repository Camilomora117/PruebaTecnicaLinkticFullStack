package com.example.productservice.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String BASE = "com.example.productservice";
    private static final int MAX_ARG_LEN = 400;

    /** Capas a incluir **/
    @Pointcut("within(" + BASE + ".infrastructure.web.controller..*)")
    public void controllerLayer() {}

    @Pointcut("within(" + BASE + ".application.service..*)")
    public void applicationLayer() {}

    @Pointcut("within(" + BASE + ".infrastructure.persistence.adapter..*)")
    public void adapterLayer() {}

    @Pointcut("within(" + BASE + ".infrastructure.persistence.repository..*)")
    public void repositoryLayer() {}

    /** Capas a excluir **/
    @Pointcut("within(" + BASE + ".domain..*)")
    public void domainLayer() {}

    @Pointcut("within(" + BASE + ".infrastructure..mapper..*)")
    public void mapperLayer() {}

    /** 🎯 Punto de corte global — “todo el flujo” **/
    @Pointcut("(controllerLayer() || applicationLayer() || adapterLayer() || repositoryLayer()) && !mapperLayer() && !domainLayer()")
    public void appFlow() {}

    /** 🔁 Un solo Around para todo **/
    @Around("appFlow()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String args = safeArgs(pjp.getArgs());
        String signature = className + "." + methodName + "()";

        log.info("▶️ Iniciando: {} con args={}", signature, args);
        long start = System.nanoTime();

        try {
            Object result = pjp.proceed();
            long tookMs = (System.nanoTime() - start) / 1_000_000;

            log.info("✅ Finalizó: {} ({} ms) → Retorno={}", signature, tookMs, shorten(result));
            return result;
        } catch (Throwable ex) {
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            log.error("❌ Error en: {} ({} ms) → {}", signature, tookMs, ex.getMessage(), ex);
            throw ex;
        }
    }

    /** Helpers para limitar **/
    private String safeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        return Arrays.stream(args)
                .map(this::shorten)
                .toList()
                .toString();
    }

    private String shorten(Object o) {
        String s = String.valueOf(o);
        return s.length() > MAX_ARG_LEN ? s.substring(0, MAX_ARG_LEN) + "...(truncated)" : s;
    }
}

