/*
 * EvalComponentInQuestion - class to hold one test relative to a assignment question
 * 
 * Created - Paul J. Wagner, 11-Sep-2018
 */
package sqlfe.general;

public class EvalComponentInQuestion {
	// data
	String evalComponentName;		// the name of an evaluation component (e.g. test, condition)
	int percent;					// the percentage weight for this test / question pair
	String condition;				// if a condition, the condition comparison text

	// methods
	// constructors
	// all-arg constructor
	public EvalComponentInQuestion(String evalComponentName, int percent, String condition) {
		super();
		this.evalComponentName = evalComponentName;
		this.percent = percent;
		this.condition = condition;
	}
	
	// default constructor
	public EvalComponentInQuestion() {
		this(null, 0, null);
	}

	// other methods
	// getters and setters
	public String getEvalComponentName() {
		return evalComponentName;
	}

	public void setEvalComponentName(String evalComponentName) {
		this.evalComponentName = evalComponentName;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}	

	// hashCode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((evalComponentName == null) ? 0 : evalComponentName.hashCode());
		result = prime * result + percent;
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
		EvalComponentInQuestion other = (EvalComponentInQuestion) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (evalComponentName == null) {
			if (other.evalComponentName != null)
				return false;
		} else if (!evalComponentName.equals(other.evalComponentName))
			return false;
		if (percent != other.percent)
			return false;
		return true;
	}	// end - method equals

	// toString
	@Override
	public String toString() {
		return "EvalComponentInQuestion [evalComponentName=" + evalComponentName + ", percent=" + percent
				+ ", condition=" + condition + "]";
	}
		
}	// end - class EvalComponentInQuestion
