package org.steam.muffindb.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.steam.muffindb.LinkedIndex;
import org.steam.muffindb.query.GraphQueryNode;
import org.steam.muffindb.query.Parser;

class muffindbTest {

	@BeforeEach
	void setUp() throws Exception {
		LinkedIndex.getInstance().performIndexing( "triples.csv" );
		LinkedIndex.getInstance().keys();
		LinkedIndex.getInstance().dumpIndex();
	}

	@Test
	void testApply() {
		Parser parser = new Parser();
		GraphQueryNode gquery = parser.parse( "( APPLY friend: ( AND livesIn:MarineParade likes:Football ) )" );
		List< Set< String > > result = gquery.getAllBerthsAsync();
		Set< String > allResults = new HashSet< String >();
		for( int i = 0; i < result.size(); i++ )
			allResults.addAll( result.get( i ) );
		assertEquals( "[Raja]", allResults.toString() );
	}

	@Test
	void testEdge() {
		Parser parser = new Parser();
		GraphQueryNode gquery = parser.parse( "likes:Football" );
		List< Set< String > > result = gquery.getAllBerthsAsync();
		Set< String > allResults = new HashSet< String >();
		for( int i = 0; i < result.size(); i++ )
			allResults.addAll( result.get( i ) );
		assertEquals( "[Babu, Biju, Ajay]", allResults.toString() );
	}
	
	@Test
	void testSetOperation() {
		Parser parser = new Parser();
		GraphQueryNode gquery = parser.parse( "( AND livesIn:MarineParade likes:Football )" );
		List< Set< String > > result = gquery.getAllBerthsAsync();
		Set< String > allResults = new HashSet< String >();
		for( int i = 0; i < result.size(); i++ )
			allResults.addAll( result.get( i ) );
		assertEquals( "[Babu, Ajay]", allResults.toString() );
	}
}
