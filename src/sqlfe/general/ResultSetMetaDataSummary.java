/*
 * ResultSetMetaDataSummary - class to hold result set meta data stats (num rows, num columns)
 *
 * Created - Paul J. Wagner, 18-Oct-2017
 */

package sqlfe.general;

public class ResultSetMetaDataSummary {
	// data
	int numRows;			// number of rows in result set
	int numCols;			// number of columns in result set
	String columnSet;		// set of column names as a string
	String resultString;	// result set as a string

	// operations
	// -- constructors
	// ---- all-arg constructor
	public ResultSetMetaDataSummary(int numRows, int numCols, String columnSet, String resultString) {
		super();
		this.numRows = numRows;
		this.numCols = numCols;
		this.columnSet = columnSet;
		this.resultString = resultString;
	}	
	
	// ---- default constructor
	public ResultSetMetaDataSummary() {
		this(-1,-1, null, null);
	}

	// -- other methods
	// -- getters and setters
	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}
	
	public String getColumnSet() {
		return columnSet;
	}

	public void setColumnSet(String columnSet) {
		this.columnSet = columnSet;
	}
	
	public String getResultString() {
		return resultString;
	}
	
	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	
	@Override
	public String toString() {
		return "ResultSetMetaDataSummary [numRows=" + numRows + ", numCols=" + numCols + ", columnSet=" + columnSet
				+ ", resultString=" + resultString + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnSet == null) ? 0 : columnSet.hashCode());
		result = prime * result + numCols;
		result = prime * result + numRows;
		result = prime * result + ((resultString == null) ? 0 : resultString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultSetMetaDataSummary other = (ResultSetMetaDataSummary) obj;
		if (columnSet == null) {
			if (other.columnSet != null)
				return false;
		} else if (!columnSet.equals(other.columnSet))
			return false;
		if (numCols != other.numCols)
			return false;
		if (numRows != other.numRows)
			return false;
		if (resultString == null) {
			if (other.resultString != null)
				return false;
		} else if (!resultString.equals(other.resultString))
			return false;
		return true;
	}

}	// end - class ResultSetMetaDataSummary
