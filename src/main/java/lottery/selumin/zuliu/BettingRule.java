package lottery.selumin.zuliu;

import java.util.ArrayList;
import java.util.List;

public class BettingRule {
	private List<Integer>checkBoxValues;
	private List<Integer> missNums;
	
	public BettingRule(){
		checkBoxValues=new ArrayList<Integer>();
		missNums=new ArrayList<Integer>();
	}
	public List<Integer> getMissNums() {
		return missNums;
	}

	public void setMissNums(List<Integer> missNums) {
		this.missNums = missNums;
	}

	public List<Integer> getCheckBoxValues() {
		return checkBoxValues;
	}

	public void setCheckBoxValues(List<Integer> checkBoxValues) {
		this.checkBoxValues = checkBoxValues;
	}
}
