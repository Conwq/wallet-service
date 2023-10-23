package org.example.walletservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;

@Aspect
public class PlayerServiceAuditAspect {
	private final LoggerService loggerService;
	private final PlayerMapper playerMapper;
	private static final String PLAYER_NOT_FOUND = "Current player not found. Please try again.";
	private static final String INCORRECT_PASSWORD = "Incorrect password.";

	public PlayerServiceAuditAspect() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		loggerService = context.getLoggerService();
		this.playerMapper = PlayerMapper.instance;
	}

	@Around("execution(* org.example.walletservice.service.PlayerService.logIn(..))")
	public Object logInAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result;
		try {
			result = joinPoint.proceed();
			if (result instanceof AuthPlayerDto authPlayerDto) {
				Player player = playerMapper.toEntity(authPlayerDto);
				loggerService.recordActionInLog(Operation.LOG_IN, player, Status.SUCCESSFUL);
				System.out.println("[SUCCESSFUL] Sign in was successful.");
			}
		} catch (PlayerNotFoundException e) {
			handleSignInException(e);
			throw e;
		}
		return result;
	}

	private void handleSignInException(PlayerNotFoundException e) {
		if (e.getMessage().equals(PLAYER_NOT_FOUND)) {
			System.out.println("[FAIL] Sign in was unsuccessful - a player with this username not exists.");
		} else if (e.getMessage().equals(INCORRECT_PASSWORD)) {
			System.out.println("[FAIL] Sign in was unsuccessful - invalid password.");
		}
	}

	@Around("execution(* org.example.walletservice.service.PlayerService.registrationPlayer(..))")
	public void registrationPlayerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			joinPoint.proceed();
			System.out.println("[SUCCESSFUL] Registration was successful.");
		} catch (PlayerAlreadyExistException e) {
			System.out.println("[FAIL] Registration was unsuccessful - a player with this username exists.");
			throw e;
		}
	}

	@Around("execution(* org.example.walletservice.service.PlayerService.getPlayerBalance(..))")
	public Object getPlayerBalanceAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		Player player = getInputArgs(joinPoint);
		Object result = joinPoint.proceed();
		loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.SUCCESSFUL);
		System.out.println("[SUCCESSFUL] Receiving the balance was successful.");
		return result;
	}

	private Player getInputArgs(ProceedingJoinPoint joinPoint) {
		Player player = null;
		Object[] methodArgs = joinPoint.getArgs();
		for (Object arg : methodArgs) {
			if (arg instanceof AuthPlayerDto authPlayer) {
				player = playerMapper.toEntity(authPlayer);
			}
		}
		return player;
	}

	@Pointcut("execution(* org.example.walletservice.service.PlayerService.logIn(..)) || " +
			"execution(* org.example.walletservice.service.PlayerService.registrationPlayer(..))")
	public void pointcutForRegistrationAndLogin() {
	}

	@AfterThrowing(pointcut = "pointcutForRegistrationAndLogin()", throwing = "e")
	public void afterInputHandler(InvalidInputDataException e) {
		if (e.getMessage().equals("Username or password can`t be empty.")) {
			System.out.println("[FAIL] Invalid data - username or password can`t be empty.");
		}
		if (e.getMessage().equals("The length of the username or password cannot be less than 1")) {
			System.out.println("[FAIL] Invalid data - the length of the username or password cannot be less than 1.");
		}
	}
}
