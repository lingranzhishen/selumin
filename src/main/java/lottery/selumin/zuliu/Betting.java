package lottery.selumin.zuliu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lottery.selumin.CalculateUtil;

public class Betting {
	private String sequenceNo;
	private int sequenceNoOfToday;
	private String bettingNo;
	private String num;
	private List<Integer>checkBoxValues;
	private List<Integer> missNums;
	
	public Betting(){
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


	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public int getSequenceNoOfToday() {
		if (StringUtils.isNoneEmpty(sequenceNo)) {
			return CalculateUtil.getSequenceIntValue(sequenceNo);
		}
		return sequenceNoOfToday;
	}

	public void setSequenceNoOfToday(int sequenceNoOfToday) {
		this.sequenceNoOfToday = sequenceNoOfToday;
	}

	public String getBettingNo() {
		return bettingNo;
	}

	public void setBettingNo(String bettingNo) {
		this.bettingNo = bettingNo;
	}


	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

}
