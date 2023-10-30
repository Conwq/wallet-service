package org.example.walletservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
import org.springframework.stereotype.Component;

/**
 * Aspect for auditing transactions in the wallet service.
 * It intercepts methods in the TransactionService to log relevant information
 * and handle exceptions in a consistent manner.
 */
@Aspect
@Component
public class TransactionAuditAspect {

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