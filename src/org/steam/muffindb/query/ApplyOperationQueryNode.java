/**
* Applies the edge to all the connected nodes on the RHS and gets the results
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

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class ApplyOperationQueryNode implements GraphQueryNode {
    private String edge;
    private GraphQueryNode graphQuery;
	
    public ApplyOperationQueryNode( String edge, GraphQueryNode graphQuery ) {
    	this.edge = edge;
    	this.graphQuery = graphQuery;
    }
    
	public Set< String > getValue() {
		Set< String > finalResult = new HashSet< String > ();
		Set< String > result = graphQuery.getValue();
		if( result == null )
			return null;
		for( String obj : result ) { //TODO:Must have a breaking condition to limit the results
			EdgeQueryNode equery = new EdgeQueryNode( edge + obj );
			finalResult = Sets.union( finalResult, equery.getValue() );
		}
		return finalResult;
	}
	
	public String toString() {
		String result = "ApplyOperationQuery Node - " + edge + " - " +  graphQuery.toString();
		return result;
	}

	@Override
	public ArrayList< Set< String > > getAllBerths() {
		ArrayList< Set< String > > finalResult = new ArrayList< Set< String > > ();
		for( int i=0; i< LinkedIndex.berthsPerKey; i++ )
			finalResult.add( new HashSet< String >() );

		ArrayList< Set< String > > result = graphQuery.getAllBerths();
		if( result == null )
			return null;
		for( int j=0; j<LinkedIndex.berthsPerKey; j++ ) {
			Set< String > thisBerth = result.get( j );
	    		for( String obj : thisBerth ) { //TODO:Must have a breaking condition to limit the results
        			EdgeQueryNode equery = new EdgeQueryNode( edge + obj );
        			SetView< String > thisUnion = Sets.union( finalResult.get( j ), equery.getValue() );
        			HashSet< String > thisBerhtResult = new HashSet< String >();
				thisBerhtResult.addAll( thisUnion );
				finalResult.set( j, thisBerhtResult );
        		}
		}
		return finalResult;
	}

	@Override
	public List< Set< String > > getAllBerthsAsync() {
		List< Set< String > > finalResult = new ArrayList< Set< String > > ();
		for( int i=0; i< LinkedIndex.berthsPerKey; i++ )
			finalResult.add( new HashSet< String >() );
		List< Set< String > > result = graphQuery.getAllBerthsAsync();
		if( result == null )
			return null;
		for( int j=0; j<LinkedIndex.berthsPerKey; j++ ) {
			Set< String > thisBerth = result.get( j );
	    		for( String obj : thisBerth ) { //TODO:Must have a breaking condition to limit the results
        			EdgeQueryNode equery = new EdgeQueryNode( edge + obj );
        			List< Set< String > > appliedResult = equery.getAllBerthsAsync();
        			java.util.List< CompletableFuture< Set< String > > > lFutures = new java.util.ArrayList< CompletableFuture< Set< String > > >();
        			for( int k=0; k<appliedResult.size(); k++ ) {
        				Set< String > appledResultBerth = appliedResult.get( k );
        				if( appledResultBerth == null )
        					appledResultBerth = new HashSet< String >();
        				lFutures.add( performUnionOperation( finalResult.get( k ), appledResultBerth ) );
        			}
    				CompletableFuture< Void > allFutures = CompletableFuture
    						.allOf( lFutures.toArray( new CompletableFuture[ lFutures.size() ] ) );
    				CompletableFuture< java.util.List< Set< String > > > allCompletedFuture = allFutures
    						.thenApply( v -> {
    							return lFutures.stream().map( aFuture -> aFuture.join() ).collect( Collectors.toList() );
    						} );
    				finalResult = allCompletedFuture.join();
	    		}
		}	
		return finalResult;
	}
	
	private static CompletableFuture< Set< String > > performUnionOperation( Set< String > a, Set< String > b ) {
		return ( CompletableFuture< Set< String > > ) CompletableFuture.supplyAsync( () -> {
			Set< String > tSetResult = new HashSet< String >();
			tSetResult.addAll( Sets.union( a, b ) );
			return tSetResult;
		} );
	}
	
	public Set< String > getBirthById( int birthId ) {
		Set< String > finalResult = new HashSet< String > ();
		Set< String > result = graphQuery.getBirthById( birthId );
		if( result == null )
			return null;
		for( String obj : result ) { //TODO:Must have a breaking condition to limit the results
			EdgeQueryNode equery = new EdgeQueryNode( edge + obj );
			finalResult = Sets.union( finalResult, equery.getBirthById( birthId ) );
		}
		return finalResult;
	}
}