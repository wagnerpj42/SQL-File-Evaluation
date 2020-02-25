/*
 * Query - class to hold an SQL query
 * 
 * Created - Paul J. Wagner, 18-Oct-2017
 */

package sqlfe.general;

public class Query {
	// data
	String queryString;			// the query, as a string
	
	// methods
	// constructors
	public Query (String aQueryString) {
		this.queryString = aQueryString;
	}
	
	// other methods
	// hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((queryString == null) ? 0 : queryString.hashCode());
		return result;
	}

	// equals
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (queryString == null) {
			if (other.queryString != null)
				return false;
		} else if (!queryString.equals(other.queryString))
			return false;
		return true;
	}	// end - method equals

	// -- toString
	public String toString() {
		return queryString;
	}

}	// end - class Query
