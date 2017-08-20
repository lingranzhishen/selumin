package lottery.selumin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Hello world!
 *
 */
public class BackThree {

	public static final String lottoryType = "r_cqss";
	public static final String domain = "https://www.xbtx002.com/";
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
		while (count-- > 0) {
			OneDayBackThreeBetting odb = betting(driver);
			if (odb.isAllFinish()) {
				break;
			}
		}
		driver.quit();
	}

	public static OneDayBackThreeBetting betting(WebDriver driver) {
		driver.navigate().refresh();
		OneDayBackThreeBetting odb = new OneDayBackThreeBetting(getCurrentMoney(driver));
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
		userName.sendKeys("lingran");
		password.sendKeys("h523588");
		String code = readVerifyCode();
		verifyCode.sendKeys(code);
		// 提交 input 所在的 form
		submit.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		driver.navigate().to(domain + "?index.php");
		while (currentMoney == null) {
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			WebElement money = driver.findElement(By.id("refff"));
			try {
				currentMoney = Double.parseDouble(money.getText());
			} catch (Exception e) {
				currentMoney = null;
			}
		}
		if (OneDayBetting.initialMoney < 1) {
			OneDayBetting.initialMoney = currentMoney;
		}
		return currentMoney;
	}

	private static Betting betting(WebDriver driver, OneDayBackThreeBetting odb) {
		try {
			driver.navigate().to(domain + "?index.php");
			Thread.sleep(5000);
			Betting betting = new Betting();
			WebElement r_cqss = driver.findElement(By.id(lottoryType));
			// driver.navigate().to("https://www.xbtx88.com/?nav=ssc");
			r_cqss.click();
			Thread.sleep(5000);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			driver.switchTo().frame(driver.findElement(By.id("main")));

			WebElement lastBettingSequenceNo = driver.findElement(By.id("nowolddiv"));
			WebElement currentBettingSequenceNo = driver.findElement(By.id("current_issue"));
			WebElement ge = driver.findElement(By.id("last_code_num4"));
			WebElement shi = driver.findElement(By.id("last_code_num3"));
			WebElement bai = driver.findElement(By.id("last_code_num2"));
			int geNum = Integer.valueOf(ge.getText());// 个位数
			int shiNum = Integer.valueOf(shi.getText());// 十位数
			int baiNum = Integer.valueOf(bai.getText());// 百位数
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
			geNum = Integer.valueOf(ge.getText());// 个位数
			shiNum = Integer.valueOf(shi.getText());// 十位数
			baiNum = Integer.valueOf(bai.getText());// 百位数
			betting.setSequenceNo(currentBettingSequenceNo.getText());
			if (geNum != shiNum && shiNum != baiNum && geNum != baiNum) {
				betting.setType(3);
			} else {
				betting.setType(4);
			}

			betting.setCost(odb.getNextBettingCost());
			WebElement two6 = driver.findElement(By.id("two6"));
			two6.click();
			WebElement zu6 = null;
			WebElement t = driver.findElement(By.id("lt_samll_label"));
			List<WebElement> playDetailList = t.findElements(By.className("SecondPlayClass_List"));
			for (WebElement we : playDetailList) {
				WebElement playTitle = we.findElement(By.className("methodgroupname"));
				if (playTitle.getText().contains("后三组选")) {
					WebElement zu = we.findElement(By.className("play_detail"));
					List<WebElement> zuArray = zu.findElements(By.cssSelector("label"));
					for (WebElement w : zuArray) {
						if (w.getText().equalsIgnoreCase("组六")) {
							zu6 = w;
						}
					}
				}
			}
			zu6.click();
			WebElement rightNums = driver.findElement(By.id("right_05"));
			WebElement allElement = rightNums.findElement(By.name("all"));// 全
			Select sel = new Select(driver.findElement(By.name("lt_project_modes")));
			sel.selectByIndex(3);
			WebElement bettingRightNow = driver.findElement(By.id("lt_sel_onekeybet"));
			WebElement lt_sel_times = driver.findElement(By.id("lt_sel_times"));
			WebElement plus_sel_times = driver.findElement(By.className("plus"));
			allElement.click();
			int times = odb.getNextBettingTimes();
			// lt_sel_times.sendKeys(String.valueOf(times - 1));
			while (--times > 0) {
				plus_sel_times.click();
				Thread.sleep(500);
			}

			bettingRightNow.click();
			// WebElement JS_blockPage =
			// driver.findElement(By.className("JS_blockPage"));

			WebElement confirm_yes = driver.findElement(By.id("confirm_yes"));
			confirm_yes.click();
			Thread.sleep(5000);
			WebElement alert_close_button = driver.findElement(By.id("alert_close_button"));
			alert_close_button.click();
			Thread.sleep(5000);
			System.out.println("期数:" + betting.getSequenceNo() + "投注:" + betting.getCost());
			waitingResult(betting, driver);
			odb.addBet(betting);
			try {
				FileUtils.write(log, "\n期数:" + betting.getSequenceNo() + "投注:" + betting.getCost(), true);
				FileUtils.write(log, "\n剩余金额:" + odb.getCurrentMoney(), true);
				FileUtils.write(log, "\n目标金额:" + odb.getAimMoney(), true);
			} catch (IOException e) {
			}
			System.out.println(betting);
			return betting;
		} catch (Exception e) {
			Log.info(e.getMessage());
		}
		return null;
	}

	public static void waitingResult(Betting betting, WebDriver driver) {
		boolean isEnd = false;
		while (!isEnd) {
			try {
				driver.navigate().to(domain + "?index.php");
				Thread.sleep(5000);
				WebElement r_cqss = driver.findElement(By.id(lottoryType));
				// driver.navigate().to("https://www.xbtx88.com/?nav=ssc");
				r_cqss.click();
				Thread.sleep(5000);
				driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				driver.switchTo().frame(driver.findElement(By.id("main")));

				WebElement lastBettingSequenceNo = driver.findElement(By.id("nowolddiv"));
				WebElement ge = driver.findElement(By.id("last_code_num4"));
				WebElement shi = driver.findElement(By.id("last_code_num3"));
				WebElement bai = driver.findElement(By.id("last_code_num2"));
				int geNum = Integer.valueOf(ge.getText());// 个位数
				int shiNum = Integer.valueOf(shi.getText());// 十位数
				int baiNum = Integer.valueOf(bai.getText());// 百位数
				int lastBettingSequenceNoIntValue = CalculateUtil.getSequenceIntValue(lastBettingSequenceNo.getText());

				System.out.println("lastBettingSequenceNoIntValue........" + lastBettingSequenceNoIntValue);
				System.out.print("waiting........" + betting.getSequenceNoOfToday());
				while (betting.getSequenceNoOfToday() != lastBettingSequenceNoIntValue) {
					System.out.print(".");
					Thread.sleep(30000);
					lastBettingSequenceNoIntValue = CalculateUtil.getSequenceIntValue(lastBettingSequenceNo.getText());
				}
				System.out.println();
				geNum = Integer.valueOf(ge.getText());// 个位数
				shiNum = Integer.valueOf(shi.getText());// 十位数
				baiNum = Integer.valueOf(bai.getText());// 百位数
				if (geNum != shiNum && shiNum != baiNum && geNum != baiNum) {
					betting.setType(3);
					betting.setResult(1);
					betting.setAward(betting.getCost() * (1 + Constant.BACK_THREE_WIN_RATIO));
				} else {
					betting.setType(4);
					betting.setResult(0);
					betting.setAward(0);
				}
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
				driver.navigate().to(domain + "?index.php");
				Thread.sleep(5000);
				WebElement r_cqss = driver.findElement(By.id(lottoryType));
				// driver.navigate().to("https://www.xbtx88.com/?nav=ssc");
				r_cqss.click();
				Thread.sleep(5000);
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
