/**
* Gets the list of values for a given edge
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.steam.muffindb.LinkedIndex;

public class SchemaQueryNode implements GraphQueryNode {
	public String schemaQuery;

	public SchemaQueryNode( String edgeQuery ) {
		this.schemaQuery = edgeQuery;
	}
	
	
	@Override
	public ArrayList< Set< String > > getAllBerths() {
		ArrayList< Set< String > > arrayPostings = new ArrayList< Set< String > >();
		for( int i = 0; i < LinkedIndex.berthsPerKey; i++ ) {
			if( i == 0 )
				arrayPostings.add( this.getValue() );
			else
				arrayPostings.add( new HashSet< String >() );
		}
		return arrayPostings;
	}
	
	
	@Override
	public HashSet< String > getValue() {
		Set< String > keys = org.steam.muffindb.LinkedIndex.getInstance().keys( );
		if( keys == null ) 
			return new HashSet< String >();
		HashSet< String > result = new HashSet<>();
		Iterator< String > itr = keys.iterator();
		while( itr.hasNext() ) {
			result.add( itr.next().replaceAll( "_\\d+$", "" ) );
		}
		return result;
	}

	public String toString() {
		return "Schema Query Node : " + schemaQuery;
	}
	
	@Override
	public List< Set< String > > getAllBerthsAsync() {
		return getAllBerths();
	}


	@Override
	public Set< String > getBirthById( int birthId ) {
		return getValue();
	}
}
