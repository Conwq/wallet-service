package org.example.walletservice.in.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Provider class for mapping command strings to Command enum values.
 */
public class CommandProvider {
	private final Map<String, Command> commands = new HashMap<>() {{
		put("registration", Command.REGISTRATION);
		put("sign_in", Command.SIGN_IN);
		put("credit", Command.CREDIT);
		put("debit", Command.DEBIT);
		put("show_all_log", Command.SHOW_ALL_LOG);
		put("show_player_log", Command.SHOW_PLAYER_LOG);
		put("no_command", Command.NO_COMMAND);
	}};

	/**
	 * Gets the Command enum value for a given command string.
	 *
	 * @param key The command string.
	 * @return The corresponding Command enum value.
	 */
	public Command getCommand(String key) {
		try {
			return commands.get(key);
		} catch (NullPointerException | ClassCastException e) {
			return commands.get("no_command");
		}
	}
}
