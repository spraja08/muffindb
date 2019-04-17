/**
* Async AND OR DIFF set operations
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.steam.muffindb.LinkedIndex;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class SetOperationQueryNode implements GraphQueryNode {
	public String operation;
	public ArrayList< GraphQueryNode > setOperationQueries;

	public SetOperationQueryNode( String operation, ArrayList< GraphQueryNode > setOperationQueries ){
		this.operation = operation;
		this.setOperationQueries = setOperationQueries;
	}
	
	public Set< String > getValue() {
		Set<String> result = null;
		for( int i = 0; i < setOperationQueries.size(); i++ ) {
			GraphQueryNode aGraphQuery = setOperationQueries.get( i );
			Set< String > thisResult = aGraphQuery.getValue();
			if( i == 0 ) {
				result = thisResult;
				continue;
			}
			if( operation.equalsIgnoreCase( "AND" ) ) {
				result = Sets.intersection( result, thisResult );
			}else if( operation.equalsIgnoreCase( "OR" ) ) {
				result = Sets.union( result, thisResult );
			}else if( operation.equalsIgnoreCase( "DIFFERENCE" ) ) {
				result = Sets.difference( result, thisResult );
			}else {
				throw new EvaluationException( "Unknown Operation : " + this.operation );
			}
		}
		return result;
	}

	public String toString() {
		String result = "SetOperationQuery Node - " + operation;
		for( GraphQueryNode qnode : setOperationQueries ) {
			result += " - " + qnode.toString() + " , ";
		}
		return result;
	}

	@Override
	public ArrayList< Set< String > > getAllBerths() {
		ArrayList< Set< String > > result = null;
		for( int i = 0; i < setOperationQueries.size(); i++ ) {
			GraphQueryNode aGraphQuery = setOperationQueries.get( i );
			ArrayList< Set< String > > thisResult = aGraphQuery.getAllBerths();
			if( i == 0 ) {
				result = thisResult;
				continue;
			}
			SetView< String > setView = null;
			for( int j = 0; j < LinkedIndex.berthsPerKey; j++ ) {
				if( operation.equalsIgnoreCase( "AND" ) )
					setView = Sets.intersection( result.get( j ), thisResult.get( j ) );
				else if( operation.equalsIgnoreCase( "OR" ) )
					setView = Sets.union( result.get( j ), thisResult.get( j ) );
				else if( operation.equalsIgnoreCase( "DIFFERENCE" ) )
					setView = Sets.difference( result.get( j ), thisResult.get( j ) );
				Set< String > thisBerhtResult = new HashSet< String >();
				thisBerhtResult.addAll( setView );
				result.set( j, thisBerhtResult );
			}
		}
		return result;
	}

	@Override
	public List< Set< String > > getAllBerthsAsync() {
		List< Set< String > > result = null;
		for( int i = 0; i < setOperationQueries.size(); i++ ) {
			GraphQueryNode aGraphQuery = setOperationQueries.get( i );
			List< Set< String > > thisResult = aGraphQuery.getAllBerthsAsync();
			if( i == 0 ) {
				result = thisResult;
				continue;
			}

			java.util.List< CompletableFuture< Set< String > > > lFutures = new java.util.ArrayList< CompletableFuture< Set< String > > >();
			for( int j = 0; j < LinkedIndex.berthsPerKey; j++ )
				lFutures.add( performSetOperation( result.get( j ), thisResult.get( j ), operation ) );

			CompletableFuture< Void > allFutures = CompletableFuture
					.allOf( lFutures.toArray( new CompletableFuture[ lFutures.size() ] ) );

			CompletableFuture< java.util.List< Set< String > > > allCompletedFuture = allFutures
					.thenApply( v -> {
						return lFutures.stream().map( aFuture -> aFuture.join() ).collect( Collectors.toList() );
					} );
			
			result = allCompletedFuture.join();
		}
		return result;
	}

	private static CompletableFuture< Set< String > > performSetOperation( Set< String > a, Set< String > b,
			String setOperation ) {
		return ( CompletableFuture< Set< String > > ) CompletableFuture.supplyAsync( () -> {
			Set< String > tSetResult = new HashSet< String >();
			if( setOperation.equalsIgnoreCase( "AND" ) )
				tSetResult.addAll( Sets.intersection( a, b ) );
			else if( setOperation.equalsIgnoreCase( "OR" ) )
				tSetResult.addAll( Sets.union( a, b ) );
			else if( setOperation.equalsIgnoreCase( "DIFFERENCE" ) )
				tSetResult.addAll( Sets.difference( a, b ) );
			return tSetResult;
		} );
	}
	
	public Set< String > getBirthById( int birthId ) {
		Set< String > result = null;
		for( int i = 0; i < setOperationQueries.size(); i++ ) {
			GraphQueryNode aGraphQuery = setOperationQueries.get( i );
			Set< String > thisResult = aGraphQuery.getBirthById( birthId );
			if( i == 0 ) {
				result = thisResult;
				continue;
			}
			if( operation.equalsIgnoreCase( "AND" ) ) {
				result = Sets.intersection( result, thisResult );
			}else if( operation.equalsIgnoreCase( "OR" ) ) {
				result = Sets.union( result, thisResult );
			}else if( operation.equalsIgnoreCase( "DIFFERENCE" ) ) {
				result = Sets.difference( result, thisResult );
			}else {
				throw new EvaluationException( "Unknown Operation : " + this.operation );
			}
		}
		return result;
	}
}