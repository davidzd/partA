package client.exception;

public class ServerBusyException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServerBusyException() {
		super();
	}
	
	@Override
	public String getMessage() {
		return "The server is busy now. Please, try again later";
	}
}
