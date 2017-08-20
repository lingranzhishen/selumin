package lottery.selumin.yr;

import org.apache.commons.lang3.StringUtils;

import lottery.selumin.CalculateUtil;

public class Betting120 {
	private String sequenceNo;
	private int sequenceNoOfToday;
	private String bettingNo;
	private String num;
	private int type;// 1 120 0其他
	private int result;// 1中
	private double award;// 奖金
	private double cost;// 成本
	private int times;

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
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

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public double getAward() {
		return award;
	}

	public void setAward(double award) {
		this.award = award;
	}

	public boolean isLose() {
		return result != 1;
	}

	public boolean isWin() {
		return result == 1;
	}
	
	public String toString(){
		return "期数"+getSequenceNo()
				+"-成本:"+cost
				+getResultType()
				+"-奖金:"+award
				+"-中奖:"+(getResult()==1?"中":"不中");
	}
	
	public String getResultType(){
		if(getType()>2){
			if(getType()==3)
			return "-后三:"+"组六";
			else
				return "-后三:"+"组三";
		}
		return "-奇偶:"+(getType()==1?"奇数":"偶数");
	}
}
