package org.example.walletservice.service.enums;

/**
 * An enum representing various operations related to player actions.
 */
public enum Operation {
	CREDIT,
	DEBIT,
	REGISTRATION,
	EXIT,
	LOG_IN {
		@Override
		public String toString() {
			return "LOG IN";
		}
	},
	TRANSACTIONAL_HISTORY {
		@Override
		public String toString() {
			return "TRANSACTIONAL HISTORY";
		}
	},
	VIEW_BALANCE {
		@Override
		public String toString() {
			return "VIEW BALANCE";
		}
	},
	SHOW_ALL_LOGS {
		@Override
		public String toString() {
			return "SHOW ALL LOGS";
		}
	},
	SHOW_LOGS_PLAYER {
		@Override
		public String toString() {
			return "SHOW LOGS PLAYER";
		}
	}
}
