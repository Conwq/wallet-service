package org.example.walletservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAuditAspect {
	/**
	 * Intercepts and audits the getAllLogs operation in LoggerService.
	 *
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.LoggerService.getAllLogs(..))")
	public Object getAllLogsAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] All logs viewed.");

		} catch (PlayerNotLoggedInException e) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			throw e;

		} catch (PlayerDoesNotHaveAccessException e) {
			System.out.println("[FAIL] You do not have access to this resource.");
			throw e;
		}

		return result;
	}

	/**
	 * Intercepts and audits the getLogsByUsername operation in LoggerService.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.LoggerService.getLogsByUsername(..))")
	public Object getLogsByUsername(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		String inputUsernameForSearch = (String) args[1];
		Object result;

		try {
			result = joinPoint.proceed();
			System.out.printf("[SUCCESSFUL] %s player logs viewed.\n", inputUsernameForSearch);

		} catch (PlayerNotFoundException e) {
			System.out.println("[FAIL] Current player not found.");
			throw e;

		} catch (PlayerNotLoggedInException e) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			throw e;

		} catch (PlayerDoesNotHaveAccessException e) {
			System.out.println("[FAIL] You do not have access to this resource.");
			throw e;
		}

		return result;
	}
}
