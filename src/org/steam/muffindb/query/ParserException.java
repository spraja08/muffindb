package org.steam.muffindb.query;
public class ParserException extends RuntimeException {
	private static final long serialVersionUID = -1009747984332258423L;

	private Token token = null;

	public ParserException( String message ){
		super( message );
	}

	public ParserException( String message, Token token ){
		super( message );
		this.token = token;
	}

	public Token getToken() {
		return token;
	}

	public String getMessage() {
		String msg = super.getMessage();
		if( token != null ) {
			msg = msg.replace( "%s", token.token );
		}
		return msg;
	}

}
