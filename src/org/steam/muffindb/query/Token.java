package org.steam.muffindb.query;

public class Token {
	public static final String EPSILON = "EPSILON";
	public static final String SET_OPERATION = "SET_OPERATION";
	public static final String APPLY_OPERATION = "APPLY_OPERATION";
	public static final String FILTER_OPERATION = "FILTER_OPERATION";
	public static final String OPEN_BRACKET = "OPEN_BRACKET";
	public static final String CLOSE_BRACKET = "CLOSE_BRACKET";
	public static final String SCHEMA_QUERY = "SCHEMA_QUERY";
	public static final String EDGE_QUERY = "EDGE_QUERY";
	public static final String EDGE = "EDGE";
	public static final String PATTERN = "PATTERN";
	public static final String GEORADIUS_OPERATION = "GEOMATCH";
	public static final String INDEX = "INDEX";
	public static final String LATLONG = "LATLONG";
	
	public final String tokenType;
	public final String token;
	public final int pos;

	public Token( String token, String sequence, int pos ){
		super();
		this.tokenType = token;
		this.token = sequence;
		this.pos = pos;
	}
	
	public String toString() {
		return "Token : " + tokenType + " - " + token + " - " + pos;
	}

}
