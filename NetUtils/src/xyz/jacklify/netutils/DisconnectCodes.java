package xyz.jacklify.netutils;

public enum DisconnectCodes {

	PROTOCOL_VERSION_MISMATCH (0, "The networking library versions are out-of-date. Please contact the developer."),
	
	IMPLEMENTATION_VERSION_MISMATCH (1, "The game version is out-of-date. Please update your client."),
	
	TIME_OUT (2, "Connection timed out."),
	
	INVALID_CREDENTIALS (3, "Your login credentials are incorrect."),
	
	UNAUTHORIZED_ACTION (4, "Your client has attempted to preform an action that it was not authorized for."),
	
	ADMINISTRATIVE_KICK (5, "Your client has been disconnected by an administrator."),
	
	CONSOLE_KICK (6, "Your client has been disconnected by the server."),
	
	BAN (7, "You are not authorized to access this server."),
	
	INTERNAL_ERROR (8, "You have been disconnected by an internal server error. Please contact the developer"),
	
	SERVER_SHUTDOWN (9, "The server has shut down"),
	
	MAINTENANCE (10, "The server is shutting down for maintenance."),
	
	SERVER_FULL (11, "The server is full."),
	
	PROTOCOL_VIOLATION (12, "Your client attempted to violate the rules of the protocol.");
	
	private int id = -1;
	private String defaultMsg = "Disconnected.";
	
	DisconnectCodes(int id, String defaultMsg) {
		this.id = id;
		this.defaultMsg = defaultMsg;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getMessage() {
		return this.defaultMsg;
	}
}
