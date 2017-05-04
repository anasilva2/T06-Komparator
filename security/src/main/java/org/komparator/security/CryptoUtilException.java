package org.komparator.security;

public class CryptoUtilException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public CryptoUtilException(){}
	
	public CryptoUtilException(String message) {
        super(message);
    }

    public CryptoUtilException(Throwable cause) {
        super(cause);
    }

    public CryptoUtilException(String message, Throwable cause) {
        super(message, cause);
    }
	

}
