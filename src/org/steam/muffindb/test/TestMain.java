package org.steam.muffindb.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.steam.muffindb.LinkedIndex;
import org.steam.muffindb.query.GraphQueryNode;
import org.steam.muffindb.query.Parser;

public class TestMain {

	public static void main( String[ ] args ) throws IOException {
		LinkedIndex.getInstance().performIndexing( "triples.csv" );
		LinkedIndex.getInstance().keys();
		LinkedIndex.getInstance().dumpIndex();	
		
		Parser parser = new Parser();
//		GraphQueryNode gquery = parser.parse( "( APPLY friend: ( AND livesIn:MarineParade likes:Football ) )" );
		GraphQueryNode gquery = parser.parse( "( AND livesIn:MarineParade likes:Football )" );
		List< Set< String > > result = gquery.getAllBerthsAsync();
		Set< String > allResults = new HashSet< String >();
		for( int i = 0; i < result.size(); i++ )
			allResults.addAll( result.get( i ) );
		System.out.println(  allResults.toString() );
	}
}
