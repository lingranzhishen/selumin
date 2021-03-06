package lottery.selumin.yr;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import lottery.selumin.Betting;
import lottery.selumin.CalculateUtil;
import lottery.selumin.Constant;

/**
 * Hello world!
 *
 */
public class Yrapp {

	public static final String lottoryType = "r_cqss";
	private static final int sleepTime = 2000;
	public static final String domain = "https://www.yrcf99.com/?index.php";
	public static final String CQ_URL = "https://www.yrcf99.com/?controller=default&action=lotterybet&nav=ssc";
	// public static final String CQ_URL =
	// "https://www.yiruncaifu.com/?controller=default&action=lotterybet&nav=ssc&curmid=2339";

	public static int count = 10;
	public static File log = new File(Constant.LOG_PATH + LocalDate.now().toString() + "_120");
	static {
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static double[] moneyTimes = { 46.87, 71.57, 108.36, 133.06, 165.31, 190.01, 218.23, 252.00, 276.19, 309.96,
			332.64, 363.38, 388.08, 421.34, 455.62, 481.82, 507.02, 537.77, 568.51, 591.70 };

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", Constant.DERIVER_PATH);
		WebDriver driver = new ChromeDriver();
		// 登录
		while (!login(driver)) {
			;
		}
		refreshWait(driver);
		OneDayBetting120 odb = betting120(driver);
		driver.quit();
	}

	public static OneDayBetting120 betting120(WebDriver driver) {
		driver.navigate().refresh();
		OneDayBetting120 odb = new OneDayBetting120(getCurrentMoney(driver));
		while (!odb.isFinish()) {
			odb.setCurrentMoney(getCurrentMoney(driver));

			betting(driver, odb);
			refreshWait(driver);
		}
		// 关闭浏览器
		return odb;
	}

	private static boolean login(WebDriver driver) {
		driver.get(domain);
		// 通过 id 找到 input 的 DOM
		WebElement userName = driver.findElement(By.id("userName"));
		WebElement password = driver.findElement(By.id("password"));
		WebElement verifyCode = driver.findElement(By.id("code"));
		WebElement submit = driver.findElement(By.id("submit"));

		// 输入关键字
		// userName.sendKeys(readVerifyCode("用户名"));
		// password.sendKeys(readVerifyCode("密码"));
		userName.sendKeys("lingran120");
		password.sendKeys("h523588");
		String code = readVerifyCode("验证码");
		verifyCode.sendKeys(code);
		// 提交 input 所在的 form
		submit.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.get(domain);
		return !doesWebElementExist(driver, By.id("userName"));

	}

	public static boolean doesWebElementExist(WebDriver driver, By selector) {

		try {
			driver.findElement(selector);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private static double getCurrentMoney(WebDriver driver) {
		Double currentMoney = null;
		driver.navigate().to(domain);
		while (currentMoney == null) {
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			WebElement money = driver.findElement(By.id("refff"));
			try {
				currentMoney = Double.parseDouble(money.getText().replaceAll(",", ""));
			} catch (Exception e) {
				currentMoney = null;
			}
		}
		if (OneDayBetting120.initialMoney < 1) {
			OneDayBetting120.initialMoney = currentMoney;
		}
		return currentMoney;
	}

	private static Betting120 betting(WebDriver driver, OneDayBetting120 odb) {
		try {
			driver.navigate().to(CQ_URL);
			Thread.sleep(sleepTime);
			Betting120 betting = new Betting120();
			Thread.sleep(sleepTime);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			driver.switchTo().frame(driver.findElement(By.id("main")));

			WebElement lastBettingSequenceNo = driver.findElement(By.id("nowolddiv"));
			WebElement currentBettingSequenceNo = driver.findElement(By.id("current_issue"));
			// 上次投注期数
			int lastBettingSequenceNoIntValue = CalculateUtil.getSequenceIntValue(lastBettingSequenceNo.getText());
			int currentBettingSequenceNoIntValue = CalculateUtil
					.getSequenceIntValue(currentBettingSequenceNo.getText());// 当前投注期数
			// 判断是否是下一期
			while (currentBettingSequenceNoIntValue != lastBettingSequenceNoIntValue + 1) {
				currentBettingSequenceNoIntValue = CalculateUtil
						.getSequenceIntValue(currentBettingSequenceNo.getText());
				lastBettingSequenceNoIntValue = CalculateUtil.getSequenceIntValue(lastBettingSequenceNo.getText());
				if (lastBettingSequenceNoIntValue == 120) {
					if (currentBettingSequenceNoIntValue == 1) {
						break;
					}
				}
				Thread.sleep(10000);
			}
			betting.setSequenceNo(currentBettingSequenceNo.getText());
			betting.setTimes(odb.getNextBettingCostTimes());
			betting.setCost(betting.getTimes() * odb.getUnitCost());
			if (odb.isCanBet()) {
				WebElement two1 = driver.findElement(By.id("two1"));
				two1.click();
				WebElement smalllabel_1_0 = null;
				List<WebElement> smalllabels = driver.findElement(By.id("lt_samll_label"))
						.findElements(By.tagName("label"));
				for (WebElement e : smalllabels) {
					if (StringUtil.endsWithIgnoreCase("组选120", e.getText())) {
						smalllabel_1_0 = e;
						break;
					}
				}
				smalllabel_1_0.click();
				WebElement rightNums = driver.findElement(By.id("right_05"));
				WebElement allElement = rightNums.findElement(By.name("all"));// 全
				allElement.click();
				Select sel = new Select(driver.findElement(By.name("lt_project_modes")));
				sel.selectByIndex(3);
				WebElement lt_sel_insert = driver.findElement(By.id("lt_sel_insert"));
				WebElement lt_trace_if_button_div = driver.findElement(By.id("lt_trace_if_button_div"));

				lt_sel_insert.click();
				Thread.sleep(2000);
				lt_trace_if_button_div.click();

				Select lt_trace_qissueno = new Select(driver.findElement(By.id("lt_trace_qissueno")));
				WebElement lt_trace_ok = driver.findElement(By.id("lt_trace_ok"));
				WebElement lt_sendok_c2 = driver.findElement(By.id("lt_sendok_c2"));
				WebElement lt_trace_margin = driver.findElement(By.id("lt_trace_margin"));
				WebElement lt_trace_times_margin = driver.findElement(By.id("lt_trace_times_margin"));
				Integer times = caculateTime(odb.getCurrentMoney());
				lt_trace_margin.clear();
				lt_trace_margin.sendKeys("10");
				lt_trace_qissueno.selectByValue("10");
				lt_trace_times_margin.clear();
				lt_trace_times_margin.sendKeys(times.toString());
				lt_trace_ok.click();
				WebElement confirm_yes = driver.findElement(By.id("confirm_yes"));

				confirm_yes.click();
				Thread.sleep(2000);
				lt_sendok_c2.click();
				Thread.sleep(2000);
				confirm_yes = driver.findElement(By.id("confirm_yes"));
				confirm_yes.click();

				WebElement alert_close_button = driver.findElement(By.id("alert_close_button"));
				alert_close_button.click();
				Thread.sleep(2000);
			}
			odb.setSettingCount(1);
			waitingResult(betting, driver);
			odb.addBet(betting);
			try {
				FileUtils.write(log, betting.toString(), true);
			} catch (IOException e) {
			}
			System.out.println(betting);
			return betting;
		} catch (Exception e) {
			Log.info(e.getMessage());
		}
		return null;
	}

	private static Integer caculateTime(double currentMoney) {
		for (int i = moneyTimes.length - 1; i >= 0; i--) {
			if (currentMoney >= moneyTimes[i]) {
				return i + 1;
			}
		}
		return 1;
	}

	public static void waitingResult(Betting120 betting, WebDriver driver) {
		boolean isEnd = false;
		while (!isEnd) {
			try {
				driver.navigate().to(CQ_URL);
				Thread.sleep(1000);
				Thread.sleep(5000);
				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				driver.switchTo().frame(driver.findElement(By.id("main")));

				WebElement lastBettingSequenceNo = driver.findElement(By.id("nowolddiv"));
				WebElement lastNum0 = driver.findElement(By.id("last_code_num0"));
				WebElement lastNum1 = driver.findElement(By.id("last_code_num1"));
				WebElement lastNum2 = driver.findElement(By.id("last_code_num2"));
				WebElement lastNum3 = driver.findElement(By.id("last_code_num3"));
				WebElement lastNum4 = driver.findElement(By.id("last_code_num4"));
				int lastBettingSequenceNoIntValue = CalculateUtil.getSequenceIntValue(lastBettingSequenceNo.getText());
				try {
					FileUtils.write(log, "\nlastBettingSequenceNoIntValue........" + lastBettingSequenceNoIntValue,
							true);
					FileUtils.write(log, "\nwaiting........" + betting.getSequenceNoOfToday(), true);
				} catch (IOException e) {
				}
				System.out.println("lastBettingSequenceNoIntValue........" + lastBettingSequenceNoIntValue);
				System.out.print("waiting........" + betting.getSequenceNoOfToday());
				while (betting.getSequenceNoOfToday() != lastBettingSequenceNoIntValue) {
					if (betting.getSequenceNoOfToday() == 1) {
						try {
							FileUtils.write(log, "\n新的一天开始了", true);
						} catch (IOException e) {
						}
					} else if (betting.getSequenceNoOfToday() + 1 < lastBettingSequenceNoIntValue) {
						try {
							FileUtils.write(log, "\n开奖号码失败", true);
						} catch (IOException e) {
						}
						isEnd = true;
						betting.setNum("12345");
						betting.setResult(1);
						betting.setAward(betting.getCost() * (Constant.CQ_120_WIN_RATIO + 1));
						break;
					}
					System.out.print(".");
					Thread.sleep(sleepTime * 5);
					lastBettingSequenceNoIntValue = CalculateUtil.getSequenceIntValue(lastBettingSequenceNo.getText());
				}
				Thread.sleep(sleepTime * 5);
				System.out.println();
				String lastNumStr = lastNum0.getText() + lastNum1.getText() + lastNum2.getText() + lastNum3.getText()
						+ lastNum4.getText();
				try {
					FileUtils.write(log, "\n开奖号码" + lastNumStr, true);
				} catch (IOException e) {
				}
				betting.setNum(lastNumStr);
				if (isLegalLottery(lastNumStr)) {
					if (is120(lastNumStr)) {
						betting.setResult(1);
						betting.setAward(betting.getCost() * (Constant.CQ_120_WIN_RATIO + 1));
					} else {
						betting.setResult(0);
						betting.setAward(0);
					}
				} else {
					betting.setResult(0);
					betting.setAward(0);
				}
				try {
					FileUtils.write(log, "\n开奖号码" + betting, true);
				} catch (IOException e) {
				}
				isEnd = true;
			} catch (Exception e) {
				isEnd = false;
				Log.info(e.getMessage());
			}
		}
	}

	private static boolean isLegalLottery(String num) {
		if (StringUtils.isEmpty(num)) {
			return false;
		}
		if (num.length() != 5) {
			return false;
		}
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) >= '0' && num.charAt(i) <= '9') {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public static void refreshWait(WebDriver driver) {
		boolean isEnd = false;
		while (!isEnd) {
			try {
				driver.navigate().to(CQ_URL);
				Thread.sleep(sleepTime);
				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				if (LocalDateTime.now().getHour() >= 10 || LocalDateTime.now().getHour() <= 2) {
					isEnd = true;
				}
			} catch (Exception e) {
				isEnd = false;
				Log.info(e.getMessage());
			}
		}
	}

	public static boolean is120(String num) {
		char[] numArray = num.toCharArray();
		for (int i = 0; i < numArray.length; i++) {
			for (int j = i + 1; j < numArray.length; j++) {
				if (numArray[i] == numArray[j])
					return false;
			}
		}
		return true;
	}

	/**
	 * 输入验证码
	 */
	public static String readVerifyCode(String hint) {
		System.out.println("请输入" + hint + ":");
		Scanner s = new Scanner(System.in);
		return s.nextLine();
	}
}
