package ru.patseev.auditspringbootstarter.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import ru.patseev.auditspringbootstarter.logger.mapper.LogMapper;
import ru.patseev.auditspringbootstarter.logger.model.Log;
import ru.patseev.auditspringbootstarter.logger.entities.Operation;
import ru.patseev.auditspringbootstarter.logger.entities.Status;
import ru.patseev.auditspringbootstarter.logger.repository.LoggerRepository;

/**
 * Aspect for logging operations and recording them in the log repository.
 */
@Aspect
public class LoggerAspect {
	private final LoggerRepository loggerRepository;
	private final LogMapper logMapper;
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
	public LoggerAspect(LoggerRepository loggerRepository, LogMapper logMapper) {
		this.loggerRepository = loggerRepository;
		this.logMapper = logMapper;
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
	 * Intercepts and logs the player login operation in PlayerService.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.logIn(..))")
	public Object logInAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.LOG_IN, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.LOG_IN, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Intercepts and logs the player registration operation in PlayerService.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.registrationPlayer(..))")
	public Object registrationPlayerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.REGISTRATION, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.REGISTRATION, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Intercepts and logs the method for retrieving a player's balance.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public Object getPlayerBalanceAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.VIEW_BALANCE, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.VIEW_BALANCE, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Intercepts and logs credit transactions.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public Object creditAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.CREDIT, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.CREDIT, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Intercepts and logs debit transactions.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public Object debitAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.DEBIT, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.DEBIT, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Intercepts and logs the method for retrieving the player's transactional history.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The result of the original method call.
	 * @throws Throwable Any exception thrown by the intercepted method.
	 */
	@Around("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public Object getPlayerTransactionHistoryAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.SUCCESSFUL);
			return result;

		} catch (RuntimeException e) {
			recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.FAIL);
			throw e;
		}
	}

	/**
	 * Records the performed action in the log.
	 *
	 * @param operation The type of operation being logged.
	 * @param status    The status of the operation (SUCCESSFUL or FAIL).
	 */
	private void recordActionInLog(Operation operation, Status status) {
		String formatLog = String.format(LOG_TEMPLATE, operation.toString(), status.toString());
		Log log = logMapper.toEntity(formatLog);
		loggerRepository.recordAction(log);
	}
}