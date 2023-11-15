package ru.patseev.auditspringbootstarter.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
		int playerID = getAuthorizedPlayerIDFromContext();
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.SHOW_ALL_LOGS, Status.SUCCESSFUL, playerID);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.SHOW_ALL_LOGS, Status.FAIL, playerID);
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
		int playerID = getAuthorizedPlayerIDFromContext();
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, Status.SUCCESSFUL, playerID);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, Status.FAIL, playerID);
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
		String username = authentication.getName();
		int playerId = playerRepository.findIdByUsername(username);
		recordActionInLog(Operation.LOG_IN, Status.SUCCESSFUL, playerId);
	}

	/**
	 * Logs a successful player balance retrieval event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public void playerBalanceAspectAfterReturning() {
		System.out.println("Returning");
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.VIEW_BALANCE, Status.SUCCESSFUL, playerID);
	}

	/**
	 * Logs a failed player balance retrieval event.
	 */
	@AfterThrowing("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public void playerBalanceAspectAfterThrowing() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.VIEW_BALANCE, Status.FAIL, playerID);
	}

	/**
	 * Logs a successful credit transaction event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public void creditAspectAspectAfterReturning() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.CREDIT, Status.SUCCESSFUL, playerID);
	}

	/**
	 * Logs a failed credit transaction event.
	 */

	@AfterThrowing("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public void creditAspectAfterThrowing() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.CREDIT, Status.FAIL, playerID);
	}

	/**
	 * Logs a successful debit transaction event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public void debitAspectAspectAfterReturning() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.DEBIT, Status.SUCCESSFUL, playerID);
	}

	/**
	 * Logs a failed debit transaction event.
	 */
	@AfterThrowing("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public void debitAspectAfterThrowing() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.DEBIT, Status.FAIL, playerID);
	}

	/**
	 * Logs a successful player transactional history retrieval event.
	 */
	@AfterReturning("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public void transactionAspectAspectAfterReturning() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.SUCCESSFUL, playerID);
	}

	/**
	 * Logs a failed player transactional history retrieval event.
	 */
	@AfterThrowing("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public void transactionAspectAfterThrowing() {
		int playerID = getAuthorizedPlayerIDFromContext();
		recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.FAIL, playerID);
	}

	/**
	 * Records the performed action in the log.
	 *
	 * @param operation The type of operation being logged.
	 * @param status    The status of the operation (SUCCESSFUL or FAIL).
	 * @param playerID  Player id
	 */
	private void recordActionInLog(Operation operation, Status status, int playerID) {
		String formatLog = String.format(LOG_TEMPLATE, operation.toString(), status.toString());
		Log log = logMapper.toEntity(formatLog);
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