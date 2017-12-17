package lottery.selumin.zuliu;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.log.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.google.gson.JsonObject;

import lottery.selumin.CalculateUtil;
import lottery.selumin.Constant;
import lottery.selumin.HttpUtil;

/**
 * Hello world!
 *
 */
public class BackThree {

	public static final String lottoryType = "r_cqss";
	private static final int sleepTime = 2000;
	public static final String domain = "https://www.yrcf666.com/?index.php";
	public static final String CQ_URL = "https://www.yrcf666.com/?controller=default&action=lotterybet&nav=ssc";
	private static final String TRACE_COUNT = "10";//追号次数

	public static int count = 10;
	public static File log = new File(Constant.LOG_PATH + LocalDate.now().toString() + "组三");
	static {
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", Constant.DERIVER_PATH);
		WebDriver driver = new ChromeDriver();
		// 登录
		while (!login(driver)) {
			;
		}
		refreshWait(driver);
		OneDayBackThreeBetting odb = betting(driver);
		driver.quit();
	}

	public static void logger(String msg) {
		try {
			FileUtils.write(log, msg+"\n", true);
		} catch (IOException e) {
		}
	}

	public static OneDayBackThreeBetting betting(WebDriver driver) {
		driver.navigate().refresh();
		OneDayBackThreeBetting odb = new OneDayBackThreeBetting(getCurrentMoney(driver));
		odb.getBettingList().addAll(HttpUtil.getBettingFromRemote());
		while (!odb.isFinish()) {
			try {
				FileUtils.writeStringToFile(log, "\n剩余金额:" + odb.getCurrentMoney(), true);
				FileUtils.write(log, "\n目标金额:" + odb.getAimMoney(), true);
			} catch (IOException e) {
			}
			System.out.println("剩余金额:" + odb.getCurrentMoney());
			System.out.println("目标金额:" + odb.getAimMoney());
			betting(driver, odb);
		}
		try {
			FileUtils.writeStringToFile(log, "\n剩余金额:" + odb.getCurrentMoney(), true);
			FileUtils.write(log, "\n目标金额:" + odb.getAimMoney(), true);
			FileUtils.write(log, "\n输赢:" + (odb.getCurrentMoney() > odb.getOldMoney() ? "赢" : "输"), true);
		} catch (IOException e) {
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

	/**
	 * 输入验证码
	 */
	public static String readVerifyCode(String hint) {
		System.out.println("请输入" + hint + ":");
		Scanner s = new Scanner(System.in);
		return s.nextLine();
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
		driver.navigate().to(CQ_URL);
		while (currentMoney == null) {
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			WebElement money = driver.findElement(By.id("refff"));
			try {
				currentMoney = Double.parseDouble(money.getText());
			} catch (Exception e) {
				currentMoney = null;
			}
		}
		return currentMoney;
	}

	private static Betting betting(WebDriver driver, OneDayBackThreeBetting odb) {
		try {
			driver.navigate().to(CQ_URL);
			Thread.sleep(sleepTime);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			driver.switchTo().frame(driver.findElement(By.id("main")));
			List<BettingRule> bettingRules = odb.getNextBetting();
			Betting betting = new Betting();

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
				Thread.sleep(10000);
			}
			betting.setSequenceNo(currentBettingSequenceNo.getText());
			if (bettingRules != null && bettingRules.size() > 0) {
				try {
					bettingSix(driver, bettingRules);
				} catch (Exception e) {

				}
			}
			Thread.sleep(sleepTime);
			waitingResult(betting, driver);
			odb.addBet(betting);
			try {
				FileUtils.write(log, "\n剩余金额:" + getCurrentMoney(driver), true);
				FileUtils.write(log, "\n目标金额:" + odb.getAimMoney(), true);
			} catch (IOException e) {
			}
			System.out.println(betting.getNum());
			return betting;
		} catch (Exception e) {
			try {
				FileUtils.write(log, e.getMessage(), true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		return null;
	}

	private static void bettingSix(WebDriver driver, List<BettingRule> bettingRules) {
		for (int i = 0; i < bettingRules.size(); i++) {
			try {
				logger(JSON.toString(bettingRules.get(i)));
				bettingSix(driver, bettingRules.get(i));
			} catch (Exception e) {

			}
		}
	}

	private static void bettingSix(WebDriver driver, BettingRule bettingRules) throws InterruptedException {

		WebElement ChooseFun = driver.findElement(By.className("ChooseFun"));
		ChooseFun.click();
		WebElement zuliu = driver.findElement(By.xpath("//*[@id=\"lt_samll_label\"]/div/div[2]/div[2]/label[2]"));
		zuliu.click();
		WebElement poschoose = driver.findElement(By.id("poschoose"));
		List<WebElement> checks = poschoose.findElements(By.className("posChoose"));
		for (WebElement element : checks) {
			if (bettingRules.getCheckBoxValues().contains(Integer.valueOf(element.getAttribute("value")))) {
				if (!element.isSelected()) {
					element.click();
				}
			} else {
				if (element.isSelected()) {
					element.click();
				}
			}
		}

		WebElement numContainer = driver.findElement(By.xpath("//*[@id=\"right_05\"]/div/ul[1]"));
		List<WebElement> nums = numContainer.findElements(By.name("lt_place_0"));
		for (WebElement num : nums) {
			if (!bettingRules.getMissNums().contains(Integer.valueOf(num.getText()))) {
				num.click();
			}
		}

		Select sel = new Select(driver.findElement(By.name("lt_project_modes")));
		sel.selectByIndex(3);
		WebElement lt_sel_insert = driver.findElement(By.id("lt_sel_insert"));
		WebElement lt_trace_if_button_div = driver.findElement(By.id("lt_trace_if_button_div"));

		lt_sel_insert.click();
		Thread.sleep(sleepTime);
		lt_trace_if_button_div.click();
		WebElement button13 = driver.findElement(By.id("button13"));
		button13.click();
		Thread.sleep(sleepTime);
		Select lt_trace_qissueno = new Select(driver.findElement(By.id("lt_trace_qissueno")));
		WebElement lt_trace_ok = driver.findElement(By.id("lt_trace_ok"));
		WebElement lt_sendok_c2 = driver.findElement(By.id("lt_sendok_c2"));
		//追号次数
		lt_trace_qissueno.selectByValue(TRACE_COUNT);
		lt_trace_ok.click();
		WebElement confirm_yes = driver.findElement(By.id("confirm_yes"));

		confirm_yes.click();
		Thread.sleep(sleepTime);
		lt_sendok_c2.click();
		Thread.sleep(sleepTime);
		confirm_yes = driver.findElement(By.id("confirm_yes"));
		confirm_yes.click();

		WebElement alert_close_button = driver.findElement(By.id("alert_close_button"));
		alert_close_button.click();
		Thread.sleep(sleepTime);

	}

	public static void waitingResult(Betting betting, WebDriver driver) {
		boolean isEnd = false;
		while (!isEnd) {
			try {
				driver.navigate().to(CQ_URL);
				Thread.sleep(sleepTime);
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
				
				isEnd = true;
			} catch (Exception e) {
				isEnd = false;
				Log.info(e.getMessage());
			}
		}
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

	/**
	 * 输入验证码
	 */
	public static String readVerifyCode() {
		System.out.println("请输入验证码:");
		Scanner s = new Scanner(System.in);
		return s.nextLine();
	}
}
