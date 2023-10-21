package org.example.walletservice.in.command;

import java.util.HashMap;
import java.util.Map;

public class CommandProvider {
	private final Map<String, Command> commands = new HashMap() {{
		put("registration", Command.REGISTRATION);
		put("sign_in", Command.SIGN_IN);
		put("credit", Command.CREDIT);
		put("debit", Command.DEBIT);
		put("show_all_log", Command.SHOW_ALL_LOG);
		put("show_player_log", Command.SHOW_PLAYER_LOG);
	}};

	public Command getCommand(String key) {
		return commands.get(key);
	}
}
