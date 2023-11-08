package ru.patseev.auditspringbootstarter.audit.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect for auditing various operations in the application.
 */
@Aspect
public class AuditAspect {
	private static final String FAIL = "[FAIL] ";
	private static final String SUCCESSFUL = "[SUCCESSFUL] ";

	/**
	 * Audits the successful completion of the getAllLogs operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.LoggerService.getAllLogs(..))")
	public void getAllLogsAspect() {
		System.out.println(SUCCESSFUL + "All logs viewed.");
	}

	/**
	 * Audits the successful completion of the getLogsByUsername operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.LoggerService.getLogsByUsername(..))")
	public void getLogsByUsername() {
		System.out.println(SUCCESSFUL + "Player logs viewed.");
	}

	/**
	 * Audits the successful completion of the logIn operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.logIn(..))")
	public void logInAspect() {
		System.out.println(SUCCESSFUL + "Sign in was successful.");
	}

	/**
	 * Audits the successful completion of the registrationPlayer operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.registrationPlayer(..))")
	public void registrationPlayerAspect() {
		System.out.println(SUCCESSFUL + "Registration was successful.");
	}

	/**
	 * Audits the successful completion of the getPlayerBalance operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public void getPlayerBalanceAspect() {
		System.out.println(SUCCESSFUL + "Receiving the balance was successful.");
	}

	/**
	 * Audits the successful completion of the credit operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public void creditAspect() {
		System.out.println(SUCCESSFUL + "Credit successful.");
	}

	/**
	 * Audits the successful completion of the debit operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public void debitAspect() {
		System.out.println(SUCCESSFUL + "Debit successful.");
	}

	/**
	 * Audits the successful completion of the getPlayerTransactionalHistory operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public void getPlayerTransactionHistoryAspect() {
		System.out.println(SUCCESSFUL + "Transaction history has been viewed");
	}

	/**
	 * Handles exceptions thrown by methods in the service package.
	 *
	 * @param e The RuntimeException thrown by the intercepted methods.
	 */
	@AfterThrowing(value = "execution(* org.example.walletservice.service..*(..))", throwing = "e")
	public void afterThrow(RuntimeException e) {
		System.out.println(FAIL + e.getMessage());
		throw e;
	}
}
