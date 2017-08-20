package lottery.selumin.yr;

import java.time.LocalDate;
import java.util.Stack;

import lottery.selumin.Constant;

public class OneDayBetting120 {
	public static double initialMoney = 0.0;
	public static double winningAim = 56.5;
	public static int MAX_MISS_COUNT=17;
	private Stack<Betting120> bettingList;
	private double oldMoney;
	private double aimMoney;
	private double currentMoney;
	private double unitCost;
	private int continueCount=0;// 连续次数
	private int continueMissCount=0;// 连续遗漏次数
	
	public boolean isCanBet(){
		return continueMissCount>=4;
	}
	
	public boolean isSecondMiss(){
		if(bettingList.isEmpty()){
			return false;
		}
		if(bettingList.peek().isLose()){
			return false;
		}
		int lastMissCount=0;
		for (int i = bettingList.size()-1; i > 0; i--) {
			Betting120 betting = bettingList.get(i - 1);
			if (betting.isLose()) {
				lastMissCount++;
			} else {
				break;
			}
		}
		return lastMissCount>=5;
	}

	public int getContinueMissCount() {
		return continueMissCount;
	}

	public void setContinueMissCount(int continueMissCount) {
		this.continueMissCount = continueMissCount;
	}

	public int getContinueCount() {
		return continueCount;
	}

	public void setContinueCount(int continueCount) {
		this.continueCount = continueCount;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	private LocalDate day;

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public OneDayBetting120() {
		bettingList = new Stack<>();
		day = LocalDate.now();
		continueCount = 0;
	}

	public OneDayBetting120(double currentMoney) {
		this();
		oldMoney = currentMoney;
		this.currentMoney = currentMoney;
		aimMoney = currentMoney + winningAim;
		this.unitCost=0.504;
	}

	public Stack<Betting120> getBettingList() {
		return bettingList;
	}

	public void setBettingList(Stack<Betting120> bettingList) {
		this.bettingList = bettingList;
	}

	public double getOldMoney() {
		return oldMoney;
	}

	public void setOldMoney(double oldMoney) {
		this.oldMoney = oldMoney;
	}

	public double getAimMoney() {
		return aimMoney;
	}

	public void setAimMoney(double aimMoney) {
		this.aimMoney = aimMoney;
	}

	public double getCurrentMoney() {
		return currentMoney;
	}

	public void setCurrentMoney(double currentMoney) {
		this.currentMoney = currentMoney;
	}

	public void addBet(Betting120 betting) {
		bettingList.add(betting);
		if (betting.isWin()) {
			this.continueCount++;
			this.continueMissCount = 0;
		} else {
			this.continueCount = 0;
			this.continueMissCount++;
		}
		setCurrentMoney(currentMoney - betting.getCost() + betting.getAward());
	}

	public double getNextBettingCost() {
		if (bettingList.isEmpty()) {
			return getUnitCost();
		}
		if (continueCount > 0) {
			return getUnitCost();
		}
		if (continueMissCount > 0) {
			double cost = 0.0;
			for (int i = bettingList.size(); i > 0; i--) {
				Betting120 betting = bettingList.get(i - 1);
				if (betting.isLose()) {
					cost += betting.getCost();
				} else {
					break;
				}
			}
			double nextCost = 0.0;
			for (int i = 1; i < 10000; i++) {
				nextCost = getUnitCost() * i;
				if ((nextCost * Constant.CQ_120_WIN_RATIO - cost) / (nextCost + cost) > 0.5) {
					return nextCost;
				}
			}
		}
		return getUnitCost();
	}
	
	public int getNextBettingCostTimes() {
		if (bettingList.isEmpty()) {
			return 1;
		}
		if (continueCount > 0) {
			return 1;
		}
		if (continueMissCount > 0) {
			double cost = 0.0;
			for (int i = bettingList.size(); i > 0; i--) {
				Betting120 betting = bettingList.get(i - 1);
				if (betting.isLose()) {
					cost += betting.getCost();
				} else {
					break;
				}
			}
			double nextCost = 0.0;
			for (int i = 1; i < 10000; i++) {
				nextCost = getUnitCost() * i;
				if ((nextCost * Constant.CQ_120_WIN_RATIO - cost) / (nextCost + cost) > 0.5) {
					return i;
				}
			}
		}
		return 1;
	}

	public int getLastBettingSequenceNo() {
		if (bettingList.isEmpty()) {
			return 0;
		}
		Betting120 lastBetting = bettingList.peek();
		if (lastBetting != null) {
			return lastBetting.getSequenceNoOfToday();
		}
		return 0;
	}

	public boolean isFinish() {
		return continueMissCount>=MAX_MISS_COUNT;
	}

	public void resetAimMoney(double currentMoney) {
		bettingList.clear();
		this.currentMoney = currentMoney;
	}

	public boolean isAllFinish() {
		return continueMissCount>=MAX_MISS_COUNT;
	}
}
