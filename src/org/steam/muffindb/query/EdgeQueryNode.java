/**
* Async get of all the sharded components
*
* @author  Raja SP
* @version 1.0
*/
package org.steam.muffindb.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.steam.muffindb.LinkedIndex;

public class EdgeQueryNode implements GraphQueryNode {
	public String edgeQuery;

	public EdgeQueryNode( String edgeQuery ) {
		this.edgeQuery = edgeQuery;
	}
	
	@Override
	public ArrayList< Set< String > > getAllBerths() {
		ArrayList< Set< String > > result = get( this.edgeQuery );
		if( result == null ) {
			ArrayList< Set< String > > arrayPostings = new ArrayList< Set< String > >();
			for( int i=0; i< LinkedIndex.berthsPerKey; i++ )
				arrayPostings.add( new HashSet< String >() );
			return arrayPostings;
		}	
		return result;
	}
	
	@Override
	public Set< String > getValue() {
		ArrayList< Set< String > > resultArray = get( this.edgeQuery );
		if( resultArray == null ) 
			return new HashSet< String >();
		HashSet< String > result = new HashSet< String >();
		for( int i=0; i<resultArray.size(); i++ )
			result.addAll( resultArray.get( i ) );
		return result;
	}

	public String toString() {
		return "Edge Query Node : " + edgeQuery;
	}
	
	@Override
	public List< Set< String > > getAllBerthsAsync() {
		return getAllBerths();
	}
	
	public ArrayList< Set< String > >get( String key ) {
		List< Set< String > > result = new ArrayList< Set< String > >();
		
		List< CompletableFuture < Set< String > > > lFutures = new java.util.ArrayList< CompletableFuture< Set< String > > >();
		for( int i=0; i< LinkedIndex.berthsPerKey; i++ )
			lFutures.add( performGet( key + "_" + i ) );
		
		CompletableFuture< Void > allFutures = CompletableFuture
				.allOf( lFutures.toArray( new CompletableFuture[ lFutures.size() ] ) );
		
		CompletableFuture< java.util.List< Set< String > > > allCompletedFuture = allFutures
				.thenApply( v -> {
					return lFutures.stream().map( aFuture -> aFuture.join() ).collect( Collectors.toList() );
				} );
		
		result = allCompletedFuture.join();		
//		for( int i=0; i<berthsPerKey; i++ )
//		    result.add( client.smembers( key + "_" + i ) );
		return ( ArrayList< Set< String > > ) result;
	}
	
	CompletableFuture < Set< String > > performGet( String key ) {
		return ( CompletableFuture< Set< String > > ) CompletableFuture.supplyAsync( () -> LinkedIndex.getInstance().getIndex().get( key ) );
	}
	
	public Set< String > getBirthById( int birthId ) {
		return LinkedIndex.getInstance().getIndex().get( this.edgeQuery + "_" + birthId );
	}
}
