package lottery.selumin;

public class Constant {
	public final static String DERIVER_PATH = "D:\\selumin\\aa\\chromedriver.exe";
	public final static int LOTTERY_COUNT = 120;
	public final static double WIN_PERCENT = 0.01;
	public final static int CONTINUE_MAX_SIZE = 3;
	public final static double STOP_LOSE_PERCENT = 0.05;// 止损
	public final static String LOG_PATH = "log";
	public final static double BACK_THREE_MIN_COST=0.24;//后三最小投注
	public final static double BACK_THREE_STOP_LOSE_PERCENT = 0.05;// 止损
	public final static double BACK_THREE_WIN_RATIO=(0.3223-0.24)/0.24;
	public final static double BACK_THREE_WIN_PERCENT = 0.01;
	
	public final static int MAX_120_MISS_COUNT=8;
	public final static double CQ_120_WIN_RATIO=(8.0584-2.5200)/2.5200;
}
