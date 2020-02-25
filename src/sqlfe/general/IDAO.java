package sqlfe.general;

import java.sql.Connection;
import java.sql.ResultSet;

public interface IDAO {
	public Connection connect();
	public ResultSet executeSQLQuery (String sqlQuery);
	public ResultSet executeSQLQueryPrepared (String sqlQuery);	
	public int executeSQLNonQuery (String sqlCommand);
	public ResultSetMetaDataSummary processResultSet (ResultSet resSet);
	public Connection disconnect ();
}
