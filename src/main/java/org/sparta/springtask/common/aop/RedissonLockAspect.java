package org.sparta.springtask.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sparta.springtask.common.annotation.RedissonLock;
import org.sparta.springtask.common.code.ResponseCode;
import org.sparta.springtask.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(org.sparta.springtask.common.annotation.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);

        String lockKey = getLockKey(annotation , joinPoint);
        log.debug("redissonLock lockKey:{}", lockKey);

        RLock lock = redissonClient.getFairLock(lockKey);

        boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS);
        if (!lockable) {
            log.debug("Lock 획득 실패={}", lockKey);
            return null;
        }

        Object result = null; // 리턴 값 저장할 변수

        try {
            log.debug("로직 수행");
            result = joinPoint.proceed();
        } catch (InterruptedException e) {
            log.debug("에러 발생");
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) { // 현재 스레드가 락을 보유하고 있는지 확인
                log.debug("락 해제");
                lock.unlock();
            }
        }
        return result;
    }

    private String getLockKey(RedissonLock annotation , ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String lockKey = "";
        char c = annotation.value().charAt(0);

        if (c == '#') {         // 파라미터 기반
            String parameterName = annotation.value().substring(1);
            Object[] args = joinPoint.getArgs();

            String value = null;
            for (int i = 0; i < signature.getParameterNames().length; i++) {
                if (signature.getParameterNames()[i].equals(parameterName)) {
                    value = args[i] == null ? "" : args[i].toString();
                    break;
                }
            }
            if (value == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST , ResponseCode.REDISSON_ANNOTATION_ERROR.getMessage());
            }

            lockKey = annotation.value() + ":" + value;
        } else if (c == '@') {  // 메서드명 기반
            lockKey = annotation.value();
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST , ResponseCode.REDISSON_ANNOTATION_ERROR.getMessage());
        }

        return lockKey;
    }
}