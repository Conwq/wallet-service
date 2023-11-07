package ru.patseev.auditspringbootstarter.audit.bean;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import ru.patseev.auditspringbootstarter.audit.exception.*;

@Aspect
public class AuditAspect {

	/**
	 * Intercepts and audits the getAllLogs operation in LoggerAspect.
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
	 * Intercepts and audits the getLogsByUsername operation in LoggerAspect.
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
		if (e.getMessage().equals("Current player not found. Please try again.")) {
			System.out.println("[FAIL] Sign in was unsuccessful - a player with this username not exists.");

		} else if (e.getMessage().equals("Incorrect password.")) {
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
	public void exclusionAfterValidationOfData(InvalidInputDataException e) {
		if (e.getMessage().equals("Username or password can`t be empty.")) {
			System.out.println("[FAIL] Invalid data - username or password can`t be empty.");
		}

		if (e.getMessage().equals("The length of the username or password cannot be less than 1")) {
			System.out.println("[FAIL] Invalid data - the length of the username or password cannot be less than 1.");
		}
	}

	/**
	 * Intercepts and audits credit transactions.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the intercepted method.
	 * @throws Throwable If an exception occurs during method execution.
	 */
	@Around("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public Object creditAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Credit successful.");

		} catch (InvalidInputDataException e) {
			System.out.println("[FAIL] The amount to be entered cannot be less than 0.");
			throw e;

		} catch (TransactionNumberAlreadyExist e) {
			System.out.println("[FAIL] A transaction with this number already exists.");
			throw e;

		} catch (PlayerDoesNotHaveAccessException e) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			throw e;
		}

		return result;
	}

	/**
	 * Intercepts and audits debit transactions.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the intercepted method.
	 * @throws Throwable If an exception occurs during method execution.
	 */
	@Around("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public Object debitAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Debit successful.");

		} catch (InvalidInputDataException e) {
			if ("The number of funds to be withdrawn exceeds the number of funds on the account.".equals(e.getMessage())) {
				System.out.println("[FAIL] Withdrawal error - insufficient funds in the account.");
			}

			if ("The amount to be entered cannot be less than 0.".equals(e.getMessage())) {
				System.out.println("[FAIL] The amount to be entered cannot be less than 0.");
			}
			throw e;

		} catch (TransactionNumberAlreadyExist e) {
			System.out.println("[FAIL] A transaction with this number already exists.");
			throw e;

		} catch (PlayerDoesNotHaveAccessException e) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			throw e;
		}
		return result;
	}

	/**
	 * Intercepts and audits the method for retrieving player transaction history.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the intercepted method.
	 * @throws Throwable If an exception occurs during method execution.
	 */
	@Around("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public Object getPlayerTransactionHistoryAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Transaction history has been viewed");

		} catch (PlayerDoesNotHaveAccessException e) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			throw e;
		}

		return result;
	}
}
