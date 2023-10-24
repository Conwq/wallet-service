package org.example.walletservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;

/**
 * Aspect for auditing transactions in the wallet service.
 * It intercepts methods in the TransactionService to log relevant information
 * and handle exceptions in a consistent manner.
 */
@Aspect
public class TransactionAuditAspect {
	private final LoggerService loggerService;
	private final PlayerMapper playerMapper;

	public TransactionAuditAspect() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		loggerService = context.getLoggerService();
		this.playerMapper = context.getPlayerMapper();
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
		Player player = getPlayer(joinPoint);
		Object result;
		try {
			result = joinPoint.proceed();

			loggerService.recordActionInLog(Operation.CREDIT, player, Status.SUCCESSFUL);
			System.out.println("[SUCCESSFUL] Credit successful.");

		} catch (InvalidInputDataException e) {
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
			System.out.println("[FAIL] The amount to be entered cannot be less than 0.");
			throw e;

		} catch (TransactionNumberAlreadyExist e) {
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
			System.out.println("[FAIL] A transaction with this number already exists.");
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
		Player player = getPlayer(joinPoint);
		Object result;
		try {
			result = joinPoint.proceed();

			loggerService.recordActionInLog(Operation.DEBIT, player, Status.SUCCESSFUL);
			System.out.println("[SUCCESSFUL] Debit successful.");
		} catch (InvalidInputDataException e) {
			if ("The number of funds to be withdrawn exceeds the number of funds on the account.".equals(e.getMessage())) {
				System.out.println("[FAIL] Withdrawal error - insufficient funds in the account.");
				loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
			}
			if ("The amount to be entered cannot be less than 0.".equals(e.getMessage())) {
				loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
				System.out.println("[FAIL] The amount to be entered cannot be less than 0.");
			}
			throw e;
		} catch (TransactionNumberAlreadyExist e) {
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
			System.out.println("[FAIL] A transaction with this number already exists.");
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
		Player player = getPlayer(joinPoint);

		Object result = joinPoint.proceed();

		loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
		System.out.println("[SUCCESSFUL] Transaction history has been viewed");
		return result;
	}

	/**
	 * Retrieves the player from the method arguments.
	 *
	 * @param joinPoint The join point for the intercepted method.
	 * @return The player entity.
	 */
	private Player getPlayer(ProceedingJoinPoint joinPoint) {
		Player player = null;
		Object[] methodArgs = joinPoint.getArgs();
		for (Object arg : methodArgs) {
			if (arg instanceof AuthPlayerDto authPlayer) {
				player = playerMapper.toEntity(authPlayer);
			}
		}
		return player;
	}
}