package org.example.walletservice.repository.manager;

public enum DBParameter {
	DRIVER {
		@Override
		public String toString() {
			return "db.driver";
		}
	},

	URL {
		@Override
		public String toString() {
			return "db.url";
		}
	},

	USER {
		@Override
		public String toString() {
			return "db.user";
		}
	},

	PASSWORD {
		@Override
		public String toString() {
			return "db.password";
		}
	}
}
