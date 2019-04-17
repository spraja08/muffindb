/**
* The sharded redis client for storing adjacency records. 
* Any graph can be stored disected into the set adjacency records
* The format is edge:startNode -> [ endNode1, endNode2..]   
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;

import au.com.bytecode.opencsv.CSVReader;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class LinkedIndex {

	public static int berthsPerKey = 2;
	
	private static LinkedIndex instance = null;
	private HashMap< String, Set< String > >index;
	
	public static final Logger logger = LogManager.getLogger( LinkedIndex.class);

	private LinkedIndex( ) {
		index = new HashMap< String, Set< String > >();
	}

	public static LinkedIndex getInstance() {
		if( instance == null )
			instance = new LinkedIndex();
		return instance;
	}
	
	public HashMap< String, Set< String > > getIndex() {
		return index;
	}
	
	private void addIndex( String key, String value ) {
		Set< String > postings = null;
		if( index.get( key ) == null )  
			postings = new HashSet< String >();
		else 
			postings = index.get( key );
		postings.add( value );
		index.put( key, postings );
	}
	
	private void removeIndex( String key, String value ) {
		if( index.get( key ) != null )  
			index.get( key ).remove( value );
	}
	
	public void performIndexing( String subject, String pred, String object ) {
		int berthHash = object.hashCode();
		int berthIndex = ( berthHash < 0 ? ( berthHash * -1 ) : berthHash ) % berthsPerKey;
		String key = pred + ":" + subject + "_" + berthIndex; 
		addIndex( key, object );
		//logger.log( Level.INFO, "Indexed - " + key  + " : " + client.smembers( key ).toString() );
		logger.log( Level.INFO, "Indexed - " + key  );
	}

	public void removeIndex( String subject, String pred, String object ) {
		int berthHash = object.hashCode();
		int berthIndex = ( berthHash < 0 ? ( berthHash * -1 ) : berthHash ) % berthsPerKey;
		String key = pred + ":" + subject + "_" + berthIndex; 
		removeIndex( key, object );
		logger.log( Level.INFO, "Index Removed - " + key  + " : " + index.get( key ).toString() );
	}

	public void performIndexing( String fileName ) throws IOException {	
		System.out.println(  "Started indexing file : " + fileName );
		CSVReader reader = new CSVReader( new FileReader( fileName ) );
		String[] line = null;
		int count = 0;
		while( ( line = reader.readNext() ) != null ) {
			if( line[0].trim().length() == 0 )
				continue;
			count++;
			String key = line[1] + ":" + line[0];
			performIndexing( line[ 0 ], line[ 1 ], line[ 2 ] );
		}
		logger.log( Level.INFO, "Finished indexing. Triples count : " + count );
	}
		
	public Set< String > keys( ) {
		logger.log( Level.INFO, "Start getting keys..." );
		Set< String > keys = index.keySet();
		logger.log( Level.INFO, "Keys gotten : " + keys.toString() );
		return keys;
	}


	public void dumpIndex( ) {
		logger.log( Level.INFO,  "---------------------Dumping all Entries -------------------" );
		Set< String > allKeys = keys();
		Iterator< String > itr = allKeys.iterator();
		while( itr.hasNext() ) {
			String key = itr.next();
			Set< String > value = index.get( key );
			logger.log( Level.INFO, key + " - " + value.toString() );
		}
	}
}
