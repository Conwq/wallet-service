package ru.patseev.auditspringbootstarter.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect for counting the execution time of the logIn method in PlayerService.
 */
@Aspect
public class TimeCountingAspect {

	/**
	 * Around advice for the logIn method in PlayerService.
	 *
	 * @param joinPoint The proceeding join point.
	 * @return The result of the method execution.
	 * @throws Throwable If an error occurs during the method execution.
	 */
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
