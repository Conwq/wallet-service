package org.example.walletservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TimeCountingAspect {

	@Around("execution(* org.example.walletservice.service.PlayerService.logIn(..))")
	public Object logInAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long endTime = System.currentTimeMillis();

		System.out.printf("Execution of method %s finished. Execution time is %s ms\n",
				joinPoint.getSignature(), (endTime - startTime));

		return result;
	}
}
