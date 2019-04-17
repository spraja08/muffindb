/**
* Interface
*
* @author  Raja SP
* @version 1.0
*/

package org.steam.muffindb.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface GraphQueryNode {
	public Set< String > getValue();
	public String toString();
	public ArrayList< Set< String > > getAllBerths();
	List< Set< String > > getAllBerthsAsync();
	public Set< String > getBirthById( int birthId );
}
