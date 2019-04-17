/**
* A simple Prefixed Expression Parser. Mostly works.
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb.query;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.logging.log4j.Level;
import org.steam.muffindb.LinkedIndex;

//GraphQuery -> SetOperationQuery
//GraphQuery -> ApplyOperationQuery
//GraphQuery -> FilterOperationQuery
//GraphQuery -> GeoRadiusQuery
//GraphQuery -> SchemaQuery
//GraphQuery -> EdgeQuery

//SetOperationQuery -> ( SET_OPERATION GraphQuery GraphQuery )
//ApplyOperationQuery -> ( APPLY_OPERATION EDGE GraphQuery )
//FilterOperationQuery -> ( FILTER_OPERATION pattern GraphQuery )
//GeoRadiusQuery -> ( GEORADIUS_OPERATION Index Lat,Long )                //GeoRadius
//SchemaQuery -> \w+?\w+
//EdgeQuery -> \w+:\w+

//EDGE		 -> \w+:
//SET_OPERATION -> AND|OR|DIFFERENCE
//APPLY_OPERATION -> APPLY
//FILTER_OPERATION -> FILTER
//pattern -> \w+=\w+

//GEORADIUS_OPERATION -> GEOMATCH                                        //GeoRadius
//Index -> \w+:\w+                                                       //GeoRadius
//Lat,Long -> [-{0,1}0-9]+(\.[0-9]+)?,[-{0,1}0-9]+(\.[0-9]+)?            //GeoRadius


public class Parser {
	LinkedList< Token > tokens;
	Token lookahead;

	public GraphQueryNode parse( String expression ) throws ParserException {
		Tokenizer tokenizer = Tokenizer.getExpressionTokenizer();
		tokenizer.tokenize( expression );
		LinkedList< Token > tokens = tokenizer.getTokens();
		for( int i = 0; i < tokens.size(); i++ ) {
			LinkedIndex.logger.log(  Level.INFO, tokens.get( i ).toString() );
		}
		return this.parse( tokens );
	}

	public GraphQueryNode parse( LinkedList< Token > tokens ) throws ParserException {
		// implementing a recursive descent parser
		this.tokens = ( LinkedList< Token > ) tokens.clone();
		lookahead = this.tokens.getFirst();

		// top level non-terminal is GraphQuery
		GraphQueryNode gQueryNode = null;
		if( lookahead.tokenType.equals( Token.OPEN_BRACKET ) ) {
			nextToken();
			gQueryNode = setOperationQuery();
			if( gQueryNode == null )
				gQueryNode = applyOperationQuery();
			if( gQueryNode == null )
				gQueryNode = filterOperationQuery();
			if( gQueryNode == null )
				throw new ParserException( "Error in query. Unable to parse" );
		}else {
			gQueryNode = edgeQuery();
			if( gQueryNode == null )
				gQueryNode = schemaQuery();
			if( gQueryNode == null )
				throw new ParserException( "Error in query. Unable to parse" );
		}
		return gQueryNode;
	}

	private GraphQueryNode setOperationQuery() {
		// SetOperationQuery -> ( SET_OPERATION GraphQuery GraphQuery )
		if( lookahead.tokenType.equals( Token.SET_OPERATION ) ) {
			String operation = lookahead.token;
			nextToken();
			GraphQueryNode lhs = parse( this.tokens );
			GraphQueryNode rhs = parse( this.tokens );
			if( lookahead.tokenType.equals( Token.CLOSE_BRACKET ) ) {
				nextToken();
				ArrayList< GraphQueryNode > gqs = new ArrayList< GraphQueryNode >();
				gqs.add( lhs );
				gqs.add( rhs );
				return new SetOperationQueryNode( operation, gqs );
			}
		}
		return null;
	}

	private GraphQueryNode applyOperationQuery() {
		// ApplyOperationQuery -> ( APPLY_OPERATION EDGE GraphQuery )
		if( lookahead.tokenType.equals( Token.APPLY_OPERATION ) ) {
			nextToken();
			if( lookahead.tokenType.equals( Token.EDGE ) ) {
				String edge = lookahead.token;
				nextToken();
				GraphQueryNode rhs = parse( this.tokens );
				if( lookahead.tokenType.equals( Token.CLOSE_BRACKET ) ) {
					nextToken();
					return new ApplyOperationQueryNode( edge, rhs );
				}
			}
		}
		return null;
	}

	private GraphQueryNode filterOperationQuery() {
		// ApplyOperationQuery -> ( APPLY_OPERATION EDGE GraphQuery )
		if( lookahead.tokenType.equals( Token.FILTER_OPERATION ) ) {
			nextToken();
			if( lookahead.tokenType.equals( Token.PATTERN ) ) {
				String pattern = lookahead.token;
				nextToken();
				GraphQueryNode rhs = parse( this.tokens );
				if( lookahead.tokenType.equals( Token.CLOSE_BRACKET ) ) {
					nextToken();
					return new FilterOperationQueryNode( pattern, rhs );
				}
			}
		}
		return null;
	}

	
	private GraphQueryNode edgeQuery() {
		// GraphQuery -> \w+:[0-9]+
		GraphQueryNode node = null;
		if( lookahead.tokenType.equals( Token.EDGE_QUERY ) ) {
			node = new EdgeQueryNode( lookahead.token );
			nextToken();
		}
		return node;
	}
	
	private GraphQueryNode schemaQuery() {
		// GraphQuery -> \w+:[0-9]+
		GraphQueryNode node = null;
		if( lookahead.tokenType.equals( Token.SCHEMA_QUERY ) ) {
			node = new SchemaQueryNode( lookahead.token );
			nextToken();
		}
		return node;
	}


	private void nextToken() {
		tokens.pop();
		// at the end of input we return an epsilon token
		if( tokens.isEmpty() )
			lookahead = new Token( Token.EPSILON, "", -1 );
		else
			lookahead = tokens.getFirst();
	}
}
