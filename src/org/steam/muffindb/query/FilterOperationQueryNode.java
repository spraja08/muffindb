/**
* Filters the results based on the RHS value
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb.query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.steam.muffindb.LinkedIndex;

import com.google.common.collect.Sets;

public class FilterOperationQueryNode implements GraphQueryNode {
    private String edge;
    private String filterPattern;
    private GraphQueryNode graphQuery;
	
    public FilterOperationQueryNode( String edge, GraphQueryNode graphQuery ) {
        	String[] parts = edge.split( "=" );
        	this.edge = parts[ 0 ];
        this.filterPattern = parts[ 1 ];
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
        			EdgeQueryNode equery = new EdgeQueryNode( edge + ":" + obj );
        			Set< String > appliedResult = equery.getValue();
        			if( appliedResult.contains( filterPattern ) ) {
        				finalResult.get( j ).add( obj );
        			}
        		}
		}
		return finalResult;
	}

	@Override
	public List< Set< String > > getAllBerthsAsync() {
	    return getAllBerths();
	}

	@Override
	public Set< String > getBirthById( int birthId ) {
		Set< String > finalResult = new HashSet< String > ();
		Set< String > result = graphQuery.getBirthById( birthId );
		if( result == null )
			return null;
		for( String obj : result ) { //TODO:Must have a breaking condition to limit the results
			EdgeQueryNode equery = new EdgeQueryNode( edge + ":" + obj );
			Set< String > appliedResult = equery.getBirthById( birthId );
			if( appliedResult.contains( filterPattern ) ) 
				finalResult.add( obj );
		}
		return finalResult;
	}
}