/*
 * QuestionAnswer - class to hold the answer information for one SQL question
 * 
 * Created - Paul J. Wagner, 12-Sep-2018
 */
package sqlfe.general;

public class QuestionAnswer {
	// data
	private String qNumStr;			// question number; e.g. 1., 1a. or 1.d)
	private Query actualQuery;		// query answer that was submitted
	private double marks;			// marks awarded for this answer

	
	// methods
	// constructors
	// all-args constructor
	public QuestionAnswer(String qNumStr, Query actualQuery, double marks) {
		super();
		this.qNumStr = qNumStr;
		this.actualQuery = actualQuery;
		this.marks = marks;
	}
	
	// default constructor
	public QuestionAnswer() {
		this(null, null, 0.0);
	}

	// getters and setters
	public String getQNumStr() {
		return qNumStr;
	}

	public void setQNumStr(String qNumStr) {
		this.qNumStr = qNumStr;
	}

	public Query getActualQuery() {
		return actualQuery;
	}

	public void setActualQuery(Query actualQuery) {
		this.actualQuery = actualQuery;
	}

	public double getMarks() {
		return marks;
	}

	public void setMarks(double marks) {
		this.marks = marks;
	}

	// hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actualQuery == null) ? 0 : actualQuery.hashCode());
		long temp;
		temp = Double.doubleToLongBits(marks);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((qNumStr == null) ? 0 : qNumStr.hashCode());
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
		QuestionAnswer other = (QuestionAnswer) obj;
		if (actualQuery == null) {
			if (other.actualQuery != null)
				return false;
		} else if (!actualQuery.equals(other.actualQuery))
			return false;
		if (Double.doubleToLongBits(marks) != Double.doubleToLongBits(other.marks))
			return false;
		if (qNumStr == null) {
			if (other.qNumStr != null)
				return false;
		} else if (!qNumStr.equals(other.qNumStr))
			return false;
		return true;
	}	// end - method equals

	// toString
	@Override
	public String toString() {
		return "QuestionAnswer [qNumStr=" + qNumStr + ", actualQuery=" + actualQuery + ", marks=" + marks + "]";
	}
		
}	// end - class QuestionAnswer
