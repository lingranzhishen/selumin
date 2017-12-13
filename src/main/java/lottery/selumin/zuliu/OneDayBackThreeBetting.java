package lottery.selumin.zuliu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class OneDayBackThreeBetting {
	public static double initialMoney = 0.0;
	private List<Betting> bettingList;
	private double oldMoney;
	private double aimMoney;
	private double currentMoney;

	private LocalDate day;

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public OneDayBackThreeBetting() {
		bettingList = new ArrayList<>();
		day = LocalDate.now();
	}

	public OneDayBackThreeBetting(double currentMoney) {
		this();
		oldMoney = currentMoney;
		this.currentMoney = currentMoney;
		aimMoney = currentMoney * (1 + Constant.WIN_PERCENT);
	}

	public List<Betting> getBettingList() {
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
	}

	public int getLastBettingSequenceNo() {
		if (bettingList.isEmpty()) {
			return 0;
		}
		Betting lastBetting = bettingList.get(bettingList.size() - 1);
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

	public List<BettingRule> getNextBetting() {
		return getMatchResult(bettingList);
	}

	private List<BettingRule> getMatchResult(List<Betting> lotteryRecords) {
		List<BettingRule> bettings = new ArrayList<BettingRule>();
		for (int f = 0; f < 5; f++)
			for (int s = f + 1; s < 5; s++)
				for (int t = s + 1; t < 5; t++) {
					for (int a = 0; a < 10; a++) {
						for (int b = a; b < 10; b++) {
							if (a == b)
								continue;
							int missNum = isMiss(lotteryRecords, a, b, f, s, t);
							if (missNum == 20) {
								BettingRule betting = new BettingRule();
								betting.getCheckBoxValues().add(f + 1);
								betting.getCheckBoxValues().add(s + 1);
								betting.getCheckBoxValues().add(t + 1);
								betting.getMissNums().add(a);
								betting.getMissNums().add(b);
								bettings.add(betting);
							}
						}
					}
				}
		return bettings;
	}

	private int isMiss(List<Betting> lotteryRecords, int a, int b, int f, int s, int t) {
		int missNum = 0;
		for (int i = lotteryRecords.size() - 1; i >= 0; i--) {
			if (isMiss(lotteryRecords.get(i), a, b, f, s, t)) {
				missNum++;
			} else {
				return missNum;
			}
		}
		return missNum;
	}

	private boolean isMiss(Betting lotteryRecord, int a, int b, int f, int s, int t) {
		String threeNum = "" + lotteryRecord.getNum().charAt(f) + lotteryRecord.getNum().charAt(s)
				+ lotteryRecord.getNum().charAt(t);
		if (threeNum.contains(String.valueOf(a)) || threeNum.contains(String.valueOf(b))) {
			return true;
		}
		char first = lotteryRecord.getNum().charAt(f);
		char second = lotteryRecord.getNum().charAt(s);
		char third = lotteryRecord.getNum().charAt(t);
		if (first == second || second == third || first == third) {
			return true;
		}
		return false;
	}
}
