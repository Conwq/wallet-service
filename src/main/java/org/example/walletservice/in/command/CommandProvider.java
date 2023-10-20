package org.example.walletservice.in.command;

import org.example.walletservice.in.command.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandProvider {
	private final Map<String, Command> commands = new HashMap(){{
		put("registration", Command.REGISTRATION);
		put("sign_in", Command.SIGN_IN);
		put("credit", Command.CREDIT);
		put("debit", Command.DEBIT);
	}};

	public Command getCommand (String key){
		return commands.get(key);
	}
}
