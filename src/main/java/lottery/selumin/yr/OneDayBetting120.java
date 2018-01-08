package lottery.selumin.yr;

import java.time.LocalDate;
import java.util.Stack;

import org.apache.commons.io.FileUtils;

import lottery.selumin.Constant;

public class OneDayBetting120 {
    public static double initialMoney = 0.0;
    public static double winningAim = 56.5;
    public static int MAX_MISS_COUNT = 10;
    public static int MIN_MISS_COUNT = 5;
    public static int TIMES_COUNT = 1;
    private Stack<Betting120> bettingList;
    private double oldMoney;
    private double aimMoney;
    private double currentMoney;
    private double unitCost;
    private int continueCount = 0;// 连续次数
    private int continueMissCount = 0;// 连续遗漏次数
    private int bettingCount;

    public void setSettingCount(int c) {
        bettingCount = c;
    }

    public boolean isCanBet() {
        if (bettingList.isEmpty()) {
            return true;
        }
        if (bettingCount > 0) {
            return false;
        }

        return isSecondMiss();
    }

    public void reset() {
        if (bettingList != null) {
            bettingList.clear();
        }
        continueCount = 0;
        continueMissCount = 0;
        bettingCount = 0;
    }

    public boolean isSecondMiss() {
        if (bettingList.isEmpty()) {
            return false;
        }
        if (bettingList.peek().isWin()) {
            return true;
        }
        if (bettingList.peek().isLose()) {
            return false;
        }
        int lastMissCount = 0;
        for (int i = bettingList.size() - 1; i > 0; i--) {
            Betting120 betting = bettingList.get(i - 1);
            if (betting.isLose()) {
                lastMissCount++;
            } else {
                break;
            }
        }
        return lastMissCount >= MIN_MISS_COUNT;
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
        continueMissCount = 0;
    }

    public OneDayBetting120(double currentMoney) {
        this();
        oldMoney = currentMoney;
        this.currentMoney = currentMoney;
        aimMoney = currentMoney + winningAim;
        this.unitCost = 0.504;
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
        int lastSeq = 0;
        if (bettingList.isEmpty()) {
            lastSeq = betting.getSequenceNoOfToday() - 1;
        } else {
            lastSeq = bettingList.peek().getSequenceNoOfToday();
        }
        int seq = betting.getSequenceNoOfToday();
        bettingList.add(betting);
        if (betting.isWin()) {
            this.continueCount++;
            this.continueMissCount = 0;
            this.bettingCount = 0;
        } else {
            this.continueCount = 0;
            this.continueMissCount++;
        }

        if (seq != lastSeq + 1 && (lastSeq != 120)) {

            try {
                FileUtils.write(Yrapp.log, "下注不连续_" + seq + "_" + lastSeq, true);
            } catch (Exception e) {

            }
            if (this.continueMissCount > 0) {
                this.continueCount = 0;
                this.continueMissCount = 1;
            }
        }
        setCurrentMoney(currentMoney - betting.getCost() + betting.getAward());
    }

    public double getNextBettingCost() {
        return getUnitCost() * getNextBettingCostTimes();
    }

    public int getNextBettingCostTimes() {
        if (bettingList.isEmpty()) {
            return TIMES_COUNT;
        }
        if (continueCount > 0) {
            return TIMES_COUNT;
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
        return TIMES_COUNT;
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
        return oldMoney * 1.1 < currentMoney;
    }

    public void resetAimMoney(double currentMoney) {
        bettingList.clear();
        this.currentMoney = currentMoney;
    }

    public boolean isAllFinish() {
        return continueMissCount >= MAX_MISS_COUNT;
    }
}
