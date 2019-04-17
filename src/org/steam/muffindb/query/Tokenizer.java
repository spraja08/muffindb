/**
* A very Basic Token identifier
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb.query;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
	private class TokenInfo {
		public final Pattern regex;
		public final String token;

		public TokenInfo( Pattern regex, String token ){
			super();
			this.regex = regex;
			this.token = token;
		}
		
		public String toString() {
			return this.token + " - " + this.regex.toString();
		}
	}

	private LinkedList< TokenInfo > tokenInfos;

	private LinkedList< Token > tokens;

	private static Tokenizer expressionTokenizer = null;

	public Tokenizer(){
		super();
		tokenInfos = new LinkedList< TokenInfo >();
		tokens = new LinkedList< Token >();
	}

	public static Tokenizer getExpressionTokenizer() {
		if( expressionTokenizer == null )
			expressionTokenizer = createExpressionTokenizer();
		return expressionTokenizer;
	}

	private static Tokenizer createExpressionTokenizer() {
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.add( "^AND|^OR|^DIFFERENCE", Token.SET_OPERATION );
		tokenizer.add( "^APPLY", Token.APPLY_OPERATION );
		tokenizer.add( "^FILTER", Token.FILTER_OPERATION );
		tokenizer.add( "^\\(", Token.OPEN_BRACKET );
		tokenizer.add( "^\\)", Token.CLOSE_BRACKET );
		tokenizer.add( "(^\\?:\\w+)|(^\\w*:\\?)", Token.SCHEMA_QUERY );
		tokenizer.add( "^\\w+:\\w+", Token.EDGE_QUERY );
		tokenizer.add( "^\\w+:", Token.EDGE );
		tokenizer.add( "^\\w+=\\w+", Token.PATTERN );
		tokenizer.add( "^GEOMATCH", Token.GEORADIUS_OPERATION );
		tokenizer.add( "[-{0,1}0-9]+(\\.[0-9]+)?,[-{0,1}0-9]+(\\.[0-9]+)?", Token.LATLONG );

		return tokenizer;
	}

	public void add( String regex, String token ) {
		tokenInfos.add( new TokenInfo( Pattern.compile( regex ), token ) );
	}

	public void tokenize( String str ) {
		String s = str.trim();
		int totalLength = s.length();
		tokens.clear();
		while( !s.equals( "" ) ) {
			int remaining = s.length();
			boolean match = false;
			for( TokenInfo info : tokenInfos ) {
				Matcher m = info.regex.matcher( s );
				if( m.find() ) {
					match = true;
					String tok = m.group().trim();
					s = m.replaceFirst( "" ).trim();
					tokens.add( new Token( info.token, tok, totalLength - remaining ) );
					break;
				}
			}
			if( !match )
				throw new ParserException( "Unexpected character in input: " + s );
		}
	}

	public LinkedList< Token > getTokens() {
		return tokens;
	}

}
