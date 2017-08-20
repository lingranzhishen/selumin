package lottery.selumin;

import java.time.LocalDate;
import java.util.Stack;

public class OneDayBackThreeBetting {
	public static double initialMoney = 0.0;
	private Stack<Betting> bettingList;
	private double oldMoney;
	private double aimMoney;
	private double currentMoney;
	private double unitCost;
	private double firstNotLosingBettingMoney;// 第一次没有输钱的成本
	private int continueCount;// 连续命中次数

	public int getContinueCount() {
		return continueCount;
	}

	public void setContinueCount(int continueCount) {
		this.continueCount = continueCount;
	}

	public double getUnitCost() {
		if (unitCost < Constant.BACK_THREE_MIN_COST) {
			return Constant.BACK_THREE_MIN_COST;
		}
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

	public OneDayBackThreeBetting() {
		bettingList = new Stack<>();
		day = LocalDate.now();
		continueCount = 0;
	}

	public OneDayBackThreeBetting(double currentMoney) {
		this();
		oldMoney = currentMoney;
		this.currentMoney = currentMoney;
		aimMoney = currentMoney * (1 + Constant.WIN_PERCENT);
		unitCost = 0.24;
		firstNotLosingBettingMoney = currentMoney;
	}

	public Stack<Betting> getBettingList() {
		return bettingList;
	}

	public void setBettingList(Stack<Betting> bettingList) {
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

	public void addBet(Betting betting) {
		bettingList.add(betting);
		if (betting.isWin()) {
			this.continueCount++;
		} else {
			this.continueCount = 0;
		}
		setCurrentMoney(currentMoney - betting.getCost() + betting.getAward());
		if (betting.isLose()) {
			firstNotLosingBettingMoney = currentMoney;
		}
	}

	public double getNextBettingCost() {
		return getUnitCost() * getNextBettingTimes();
	}

	public int getNextBettingTimes() {
		if (bettingList.isEmpty()) {
			return 1;
		}
		Betting lastBetting = bettingList.peek();
		if (lastBetting == null || lastBetting.isLose()) {
			this.continueCount = 0;
			return 1;
		}
		if (currentMoney + getUnitCost() * Constant.BACK_THREE_WIN_RATIO > aimMoney) {
			return 1;
		}
		if (currentMoney - firstNotLosingBettingMoney > getUnitCost()) {
			return (int) Math.ceil((currentMoney - firstNotLosingBettingMoney) / unitCost);
		}
		return 1;
	}

	public int getLastBettingSequenceNo() {
		if (bettingList.isEmpty()) {
			return 0;
		}
		Betting lastBetting = bettingList.peek();
		if (lastBetting != null) {
			return lastBetting.getSequenceNoOfToday();
		}
		return 0;
	}

	public boolean isFinish() {
		return currentMoney >= aimMoney || currentMoney <= oldMoney * (1.0 - Constant.BACK_THREE_STOP_LOSE_PERCENT);
	}

	public boolean isAllFinish() {
		return currentMoney >= initialMoney * (1.0 + Constant.BACK_THREE_WIN_PERCENT * 3.0);
	}
}
