package ru.patseev.auditspringbootstarter.logger.bean;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import ru.patseev.auditspringbootstarter.audit.exception.*;
import ru.patseev.auditspringbootstarter.logger.mapper.LogMapper;
import ru.patseev.auditspringbootstarter.logger.model.Log;
import ru.patseev.auditspringbootstarter.logger.model.Operation;
import ru.patseev.auditspringbootstarter.logger.model.Status;

@Aspect
public class LoggerAspect {
	private final LoggerRepository loggerRepository;
	private final LogMapper logMapper;
	private static final String LOG_TEMPLATE =
			"""
					-Operation: %s-
					-Status: %s-
					""";

	@Autowired
	public LoggerAspect(LoggerRepository loggerRepository, LogMapper logMapper) {
		this.loggerRepository = loggerRepository;
		this.logMapper = logMapper;
	}

	@Around("execution(* org.example.walletservice.service.LoggerService.getAllLogs(..))")
	public Object getAllLogsAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.SHOW_ALL_LOGS, Status.SUCCESSFUL);
			return result;

		} catch (PlayerNotLoggedInException | PlayerDoesNotHaveAccessException e) {
			recordActionInLog(Operation.SHOW_ALL_LOGS, Status.FAIL);
			throw e;
		}
	}

	@Around("execution(* org.example.walletservice.service.LoggerService.getLogsByUsername(..))")
	public Object getLogsByUsername(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, Status.SUCCESSFUL);
			return result;

		} catch (PlayerNotFoundException | PlayerNotLoggedInException | PlayerDoesNotHaveAccessException e) {
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, Status.FAIL);
			throw e;

		}
	}

	@Around("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.logIn(..))")
	public Object logInAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.LOG_IN, Status.SUCCESSFUL);
			return result;

		} catch (PlayerNotFoundException e) {
			recordActionInLog(Operation.LOG_IN, Status.FAIL);
			throw e;
		}
	}

	@Around("execution(* org.example.walletservice.service.impl.PlayerServiceImpl.registrationPlayer(..))")
	public Object registrationPlayerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.REGISTRATION, Status.SUCCESSFUL);
			return result;

		} catch (PlayerAlreadyExistException e) {
			recordActionInLog(Operation.REGISTRATION, Status.FAIL);
			throw e;
		}
	}

	@Around("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public Object getPlayerBalanceAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.VIEW_BALANCE, Status.SUCCESSFUL);
			return result;

		} catch (PlayerNotLoggedInException e) {
			recordActionInLog(Operation.VIEW_BALANCE, Status.FAIL);
			throw e;
		}
	}

	@AfterThrowing(value = "execution(* org.example.walletservice.service.PlayerService.logIn(..))", throwing = "e")
	public void exclusionAfterValidationOfDataLogIn(InvalidInputDataException e) {
		recordActionInLog(Operation.LOG_IN, Status.FAIL);
	}

	@AfterThrowing(value = "execution(* org.example.walletservice.service.PlayerService.registrationPlayer(..))", throwing = "e")
	public void exclusionAfterValidationOfDataRegistration(InvalidInputDataException e) {
		recordActionInLog(Operation.REGISTRATION, Status.FAIL);
	}

	@Around("execution(* org.example.walletservice.service.TransactionService.credit(..))")
	public Object creditAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.CREDIT, Status.SUCCESSFUL);
			return result;

		} catch (InvalidInputDataException | TransactionNumberAlreadyExist | PlayerDoesNotHaveAccessException e) {
			recordActionInLog(Operation.CREDIT, Status.FAIL);
			throw e;
		}
	}

	@Around("execution(* org.example.walletservice.service.TransactionService.debit(..))")
	public Object debitAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.DEBIT, Status.SUCCESSFUL);
			return result;

		} catch (InvalidInputDataException | TransactionNumberAlreadyExist | PlayerDoesNotHaveAccessException e) {
			recordActionInLog(Operation.DEBIT, Status.FAIL);
			throw e;
		}
	}

	@Around("execution(* org.example.walletservice.service.TransactionService.getPlayerTransactionalHistory(..))")
	public Object getPlayerTransactionHistoryAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.SUCCESSFUL);
			return result;

		} catch (PlayerDoesNotHaveAccessException e) {
			recordActionInLog(Operation.TRANSACTIONAL_HISTORY, Status.FAIL);
			throw e;
		}
	}

	private void recordActionInLog(Operation operation, Status status) {
		String formatLog = String.format(LOG_TEMPLATE, operation.toString(), status.toString());
		Log log = logMapper.toEntity(formatLog);
		loggerRepository.recordAction(log);
	}
}