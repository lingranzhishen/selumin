package lottery.selumin;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class OneDayBetting {
	public static double initialMoney = 0.0;
	private Stack<Betting> bettingList;
	private double oldMoney;
	private double aimMoney;
	private double currentMoney;
	private double unitCost;
	private int continueCount;// 连续命中次数

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

	public OneDayBetting() {
		bettingList = new Stack<>();
		day = LocalDate.now();
		continueCount = 0;
	}

	public OneDayBetting(double currentMoney) {
		this();
		oldMoney = currentMoney;
		this.currentMoney = currentMoney;
		aimMoney = currentMoney * (1 + Constant.WIN_PERCENT);
		unitCost = currentMoney / Constant.LOTTERY_COUNT;
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
	}

	public double getNextBettingCost() {
		if (bettingList.isEmpty()) {
			return getUnitCost();
		}
		Betting lastBetting = bettingList.peek();
		if (lastBetting == null || lastBetting.isLose() || (currentMoney + unitCost * 0.93) > aimMoney) {
			// this.continueCount = 0;
			return getUnitCost();
		}
		return lastBetting.getAward();
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
		return currentMoney >= aimMoney || currentMoney <= oldMoney * (1.0 - Constant.STOP_LOSE_PERCENT);
	}

	public void resetAimMoney(double currentMoney) {
		bettingList.clear();
		this.currentMoney = currentMoney;
		oldMoney = currentMoney;
		aimMoney = currentMoney * (1 + Constant.WIN_PERCENT);
		continueCount = 0;
		unitCost = currentMoney / Constant.LOTTERY_COUNT;
	}

	 public boolean isAllFinish() {
	 return currentMoney >= initialMoney * (1.0 + Constant.WIN_PERCENT * 2.0)||currentMoney <= initialMoney * (1.0 - Constant.WIN_PERCENT * 5.0);
	 }
}
