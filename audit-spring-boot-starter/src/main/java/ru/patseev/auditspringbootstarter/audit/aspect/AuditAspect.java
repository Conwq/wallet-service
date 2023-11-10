package ru.patseev.auditspringbootstarter.audit.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for auditing various operations in the application.
 */
@Aspect
public class AuditAspect {
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditAspect.class);
	private static final String FAIL = "[FAIL] {}";
	private static final String SUCCESSFUL = "[SUCCESSFUL] {}";

	/**
	 * Audits the successful completion of the getAllLogs operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.LoggerService.getAllLogs(..))")
	public void getAllLogsAspect() {
		LOGGER.info(SUCCESSFUL, "All logs viewed.");
	}

	/**
	 * Audits the successful completion of the getLogsByUsername operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.LoggerService.getLogsByUsername(..))")
	public void getLogsByUsername() {
		LOGGER.info(SUCCESSFUL, "Player logs viewed.");
	}

	/**
	 * Audits the successful completion of the logIn operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.logIn(..))")
	public void logInAspect() {
		LOGGER.info(SUCCESSFUL, "Sign in was successful.");
	}

	/**
	 * Audits the successful completion of the registrationPlayer operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.registrationPlayer(..))")
	public void registrationPlayerAspect() {
		LOGGER.info(SUCCESSFUL, "Registration was successful.");
	}

	/**
	 * Audits the successful completion of the getPlayerBalance operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public void getPlayerBalanceAspect() {
		LOGGER.info(SUCCESSFUL, "Receiving the balance was successful.");
	}

	/**
	 * Audits the successful completion of the credit operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public void creditAspect() {
		LOGGER.info(SUCCESSFUL, "Credit successful.");
	}

	/**
	 * Audits the successful completion of the debit operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public void debitAspect() {
		LOGGER.info(SUCCESSFUL, "Debit successful.");
	}

	/**
	 * Audits the successful completion of the getPlayerTransactionalHistory operation.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public void getPlayerTransactionHistoryAspect() {
		LOGGER.info(SUCCESSFUL, "Transaction history has been viewed");
	}

	/**
	 * Handles exceptions thrown by methods in the service package.
	 *
	 * @param e The RuntimeException thrown by the intercepted methods.
	 */
	@AfterThrowing(value = "execution(* org.example.walletservice.service..*(..))", throwing = "e")
	public void afterThrow(RuntimeException e) {
		LOGGER.error(FAIL, e.getMessage());
		throw e;
	}
}
