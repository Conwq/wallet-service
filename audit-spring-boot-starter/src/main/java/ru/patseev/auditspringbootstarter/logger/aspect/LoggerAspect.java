package ru.patseev.auditspringbootstarter.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import ru.patseev.auditspringbootstarter.logger.entities.Operation;
import ru.patseev.auditspringbootstarter.logger.entities.Status;
import ru.patseev.auditspringbootstarter.logger.mapper.LogMapper;
import ru.patseev.auditspringbootstarter.logger.model.Log;
import ru.patseev.auditspringbootstarter.logger.repository.LoggerRepository;
import ru.patseev.auditspringbootstarter.logger.repository.impl.PlayRepository;

/**
 * Aspect for logging operations and recording them in the log repository.
 */
@Aspect
public class LoggerAspect {
	private final LoggerRepository loggerRepository;
	private final LogMapper logMapper;
	private final PlayRepository playerRepository;
	private static final String LOG_TEMPLATE =
			"""
					-Operation: %s-
					-Status: %s-
					""";

	/**
	 * Constructor to inject dependencies.
	 *
	 * @param loggerRepository The repository for storing logs.
	 * @param logMapper        The mapper to convert log data.
	 */
	@Autowired
	public LoggerAspect(LoggerRepository loggerRepository, LogMapper logMapper, PlayRepository playerRepository) {
		this.loggerRepository = loggerRepository;
		this.logMapper = logMapper;
		this.playerRepository = playerRepository;
	}

	/**
	 * Intercepts and logs the getAllLogs operation.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.LoggerService.getAllLogs(..))")
	public Object getAllLogsAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.SHOW_ALL_LOGS, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.SHOW_ALL_LOGS, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Intercepts and logs the getLogsByUsername operation.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.LoggerService.getLogsByUsername(..))")
	public Object getLogsByUsername(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Logs a successful login event.
	 *
	 * @param authentication The authentication object containing information about the successful login.
	 */
	@AfterReturning(pointcut = "execution(* org.springframework.security.authentication.AuthenticationProvider.authenticate(..))",
			returning = "authentication")
	public void logSuccessfulLogin(Authentication authentication) {
		recordActionInLog(Operation.LOG_IN, Status.SUCCESSFUL, authentication);
	}

	/**
	 * Logs a successful player balance retrieval event.
	 */
//	@AfterReturning("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
//	public void playerBalanceAspectAfterReturning() {
//		recordActionInLog(Operation.VIEW_BALANCE, Status.SUCCESSFUL);
//	}
//
//	/**
//	 * Logs a failed player balance retrieval event.
//	 */
//	@AfterThrowing("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
//	public void playerBalanceAspectAfterThrowing() {
//		recordActionInLog(Operation.VIEW_BALANCE, Status.FAIL);
//	}

	@Around("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public Object creditAspectAfterReturning(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			recordActionInLog(Operation.CREDIT, Status.SUCCESSFUL);
		} catch (RuntimeException e) {
			recordActionInLog(Operation.CREDIT, Status.FAIL);
			throw e;
		}
		return result;
	}


	/**
	 * Logs a successful credit transaction event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public void creditAspectAfterReturning() {
		recordActionInLog(Operation.CREDIT, Status.SUCCESSFUL);
	}

	/**
	 * Logs a failed credit transaction event.
	 */

	@AfterThrowing("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public void creditAspectAfterThrowing() {
		recordActionInLog(Operation.CREDIT, Status.FAIL);
	}

	/**
	 * Logs a successful debit transaction event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public void debitAspectAspectAfterReturning() {
		recordActionInLog(Operation.DEBIT, Status.SUCCESSFUL);
	}

	/**
	 * Logs a failed debit transaction event.
	 */
	@AfterThrowing("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public void debitAspectAfterThrowing() {
		recordActionInLog(Operation.DEBIT, Status.FAIL);
	}

	/**
	 * Logs a successful player transactional history retrieval event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public void transactionAspectAspectAfterReturning() {
		recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.SUCCESSFUL);
	}

	/**
	 * Logs a failed player transactional history retrieval event.
	 */
	@AfterThrowing("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public void transactionAspectAfterThrowing() {
		recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.FAIL);
	}

	/**
	 * Records the performed action in the log.
	 *
	 * @param operation The type of operation being logged.
	 * @param status    The status of the operation (SUCCESSFUL or FAIL).
	 */
	private void recordActionInLog(Operation operation, Status status) {
		int playerID = getAuthorizedPlayerIDFromContext();
		record(operation, status, playerID);
	}

	private void recordActionInLog(Operation operation, Status status, Authentication authentication) {
		int playerID = playerRepository.findIdByUsername(authentication.getName());
		record(operation, status, playerID);
	}

	private void record(Operation operation, Status status, int playerID) {
		Log log = logMapper.toEntity(operation, status);
		loggerRepository.recordAction(log, playerID);
	}

	/**
	 * Retrieves the ID of the authorized player from the security context.
	 *
	 * @return The ID of the authorized player.
	 */
	private int getAuthorizedPlayerIDFromContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return playerRepository.findIdByUsername(username);
	}
}