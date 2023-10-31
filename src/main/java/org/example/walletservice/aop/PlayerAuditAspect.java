package org.example.walletservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.walletservice.service.exception.*;
import org.springframework.stereotype.Component;

/**
 * Aspect for auditing player-related operations in the wallet service.
 * It intercepts methods in the PlayerService and LoggerService to log relevant information
 * and handle exceptions in a consistent manner.
 */
@Aspect
@Component
public class PlayerAuditAspect {
	private static final String PLAYER_NOT_FOUND = "Current player not found. Please try again.";
	private static final String INCORRECT_PASSWORD = "Incorrect password.";

	/**
	 * Intercepts and audits the logIn operation in PlayerService.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.logIn(..))")
	public Object logInAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Sign in was successful.");

		} catch (PlayerNotFoundException e) {
			displaysErrorMessageInCaseOfInvalidData(e);
			throw e;
		}

		return result;
	}

	/**
	 * Handles exceptions related to sign-in operation.
	 *
	 * @param e The PlayerNotFoundException thrown by the intercepted method.
	 */
	private void displaysErrorMessageInCaseOfInvalidData(PlayerNotFoundException e) {
		if (e.getMessage().equals(PLAYER_NOT_FOUND)) {
			System.out.println("[FAIL] Sign in was unsuccessful - a player with this username not exists.");

		} else if (e.getMessage().equals(INCORRECT_PASSWORD)) {
			System.out.println("[FAIL] Sign in was unsuccessful - invalid password.");
		}
	}

	/**
	 * Intercepts and audits the registrationPlayer operation in PlayerService.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.registrationPlayer(..))")
	public Object registrationPlayerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Registration was successful.");

		} catch (PlayerAlreadyExistException e) {
			System.out.println("[FAIL] Registration was unsuccessful - a player with this username exists.");
			throw e;
		}

		return result;
	}

	/**
	 * Intercepts and audits the getPlayerBalance operation in PlayerService.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public Object getPlayerBalanceAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Receiving the balance was successful.");

		} catch (PlayerNotLoggedInException e) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			throw e;
		}

		return result;
	}

	/**
	 * Defines a pointcut for registration and login operations in the PlayerService.
	 */
	@Pointcut("execution(* org.example.walletservice.service.PlayerService.logIn(..)) || " +
			"execution(* org.example.walletservice.service.PlayerService.registrationPlayer(..))"
	)
	public void pointcutForRegistrationAndLogin() {
	}

	/**
	 * Handles exceptions related to invalid input data after registration and login operations.
	 *
	 * @param e The InvalidInputDataException thrown by the intercepted methods.
	 */
	@AfterThrowing(pointcut = "pointcutForRegistrationAndLogin()", throwing = "e")
	public void afterInputHandler(InvalidInputDataException e) {
		if (e.getMessage().equals("Username or password can`t be empty.")) {
			System.out.println("[FAIL] Invalid data - username or password can`t be empty.");
		}

		if (e.getMessage().equals("The length of the username or password cannot be less than 1")) {
			System.out.println("[FAIL] Invalid data - the length of the username or password cannot be less than 1.");
		}
	}

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