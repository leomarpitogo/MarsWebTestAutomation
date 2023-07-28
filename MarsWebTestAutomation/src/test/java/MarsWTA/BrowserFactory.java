package MarsWTA;


import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;	
	
public class BrowserFactory {
	public static WebDriver BsDriver, MobileChromeDriver, ChromeDriver, ChromeDriver1, FireFoxDriver, FireFoxDriver1, IEDriver, IEDriver1;
	public static WebDriver wd = null;
	public static String PrevBrowser = "";
	static String browser;
	static int WaitSec;
	static String exeDir;
	public static HashMap<String,String> BrowserDrivers = new HashMap<String,String>();
	public static MainExtension MainExt = new MainExtension();
	public static WebDriver StartBrowser(String WebFieldVal, String url, WebDriver FDriver){
			
		
			//exeDir = System.getProperty("user.dir") + "\\BrowserFactory\\";
			exeDir = System.getProperty("java.library.path") + "\\";
			browser = WebFieldVal;
			WaitSec = 60;
			
			
			/*
			if(!PrevBrowser.isEmpty()) {
				if(browser.toLowerCase().contains(PrevBrowser)) {
					

					//if(FDriver!=null) {
					//	FDriver.quit();
					//}
					
					if(MainDriver!= null)
				    {
				    	MainDriver.quit();
				    }
					
					
					
				}
			} */
			
			KillMyWebDriver(WebFieldVal);
			SetPreviousBrowser(WebFieldVal);
			
			

			if(WebFieldVal.contains("(") && WebFieldVal.contains(")")) { // splitting for waiting seconds
				String[] splitWebFieldVal = WebFieldVal.trim().split("\\(");
				browser = splitWebFieldVal[0];
				WaitSec = Integer.parseInt(splitWebFieldVal[1].replace(")", ""));
			}
						
			if(browser.toLowerCase().contains("firefox"))
			{
				initializeFIREFOX(WebFieldVal);

			}else if(browser.toLowerCase().contains("webmobile-chrome"))
			{
				wd = new ChromeDriver(ChromeOptionsEmulator());
				SetMyWebDriver(WebFieldVal);

			}else if(browser.toLowerCase().contains("chrome"))
			{
				initializeCHROME(WebFieldVal);

			}else if(browser.equalsIgnoreCase("ie"))
			{
				initializeIE(WebFieldVal);

			}else if(browser.toLowerCase().contains("browserstack"))
			{
				//BrowserStack[browserName=Andriod,device=Samsung Galaxy S9 Plus,realMobile=true,os_version=9.0]
				wd = initializeBROWSERSTACK();

			}else{

			    System.out.println("Test Automation is Stopped due to the unknown OPEN commmand TEXT value");
			    System.exit(0);
			}
			
			url = MainExt.Concatenation(url);
			
			//SetMyWebDriver(WebFieldVal);
			if(browser.toLowerCase().contains("browserstack")) {
				System.out.println("Setting up BrowserStack");
				wd.get(url);
			}
			
			if(!browser.toLowerCase().contains("browserstack") && !browser.toLowerCase().contains("webmobile-chrome")) {
				System.out.println("Setting up local browser");
				MyWebDriver(WebFieldVal).get(url);
				MyWebDriver(WebFieldVal).manage().window().maximize();
				SetMyWebDriver(WebFieldVal);
				MyWebDriver(WebFieldVal).manage().timeouts().implicitlyWait(WaitSec, TimeUnit.SECONDS);
			}
			
			
			//SetMyWebDriver(WebFieldVal);
			
			/*

			*/
			
		//return MainDriver;
		return wd;
	}
	
	public static WebDriver MyWebDriver(String Browser) {
		
		if(Browser.toLowerCase().contains("chrome1")) {
			wd = ChromeDriver1;
		}else if(Browser.toLowerCase().contains("firefox1")) {
			wd = FireFoxDriver1;		
		}else if(Browser.toLowerCase().contains("ie1")) {
			wd = IEDriver1;
		}else if(Browser.toLowerCase().contains("webmobile-chrome")) {
				wd = MobileChromeDriver;
		}else if(Browser.toLowerCase().contains("chrome")) {
			wd = ChromeDriver;
		}else if(Browser.toLowerCase().contains("firefox")) {
			wd = FireFoxDriver;
		}else if(Browser.toLowerCase().contains("ie")) {
			wd = IEDriver;
		}else if(Browser.toLowerCase().contains("browserstack")) {
			wd = BsDriver;
		}
		

		return wd;
	}
	
	public static void SetMyWebDriver(String Browser) {
		
		if(Browser.toLowerCase().contains("chrome1")) {
			ChromeDriver1 = wd;
		}else if(Browser.toLowerCase().contains("firefox1")) {
			FireFoxDriver1 = wd;		
		}else if(Browser.toLowerCase().contains("ie1")) {
			IEDriver1 = wd;
		}else if(Browser.toLowerCase().contains("webmobile-chrome")) {
			MobileChromeDriver = wd;
		}else if(Browser.toLowerCase().contains("chrome")) {
			ChromeDriver = wd;
		}else if(Browser.toLowerCase().contains("firefox")) {
			FireFoxDriver = wd;
		}else if(Browser.toLowerCase().contains("ie")) {
			IEDriver = wd;
		}else if(Browser.toLowerCase().contains("browserstack")) {
			BsDriver = wd;
		}
		
	}
	
	public static void KillMyWebDriver(String Browser) {
		
		if(!PrevBrowser.isEmpty()){
			if(PrevBrowser.contains(Browser.trim().toLowerCase())){
				
				if(Browser.toLowerCase().contains("chrome1")) {
					ChromeDriver1.quit();
				}else if(Browser.toLowerCase().contains("firefox1")) {
					FireFoxDriver1.quit();		
				}else if(Browser.toLowerCase().contains("ie1")) {
					IEDriver1.quit();		
				}else if(Browser.toLowerCase().contains("chrome")) {
					ChromeDriver.quit();
				}else if(Browser.toLowerCase().contains("firefox")) {
					FireFoxDriver.quit();
				}else if(Browser.toLowerCase().contains("ie")) {
					IEDriver.quit();
				}
			}
		}

	}
	
	
	public static void SetPreviousBrowser(String Browser) {
		wd = null;
		if(Browser.toLowerCase().contains("chrome1")) {
			PrevBrowser = "chrome1";
		}else if(Browser.toLowerCase().contains("firefox1")) {
			PrevBrowser = "firefox1";				
		}else if(Browser.toLowerCase().contains("ie1")) {
			PrevBrowser = "ie1";				
		}else if(Browser.toLowerCase().contains("chrome")) {
			PrevBrowser = "chrome";
		}else if(Browser.toLowerCase().contains("firefox")) {
			PrevBrowser = "firefox";
		}else if(Browser.toLowerCase().contains("ie")) {
			PrevBrowser = "ie";
		}
	}

	
	public static void UpdatePrevBrowser(WebDriver wd, String newBrowser) {
		
		// Update local WebDriver w/ the latest activity - for next switching
		if(PrevBrowser.contains("chrome1")) {
			ChromeDriver1 = wd;
		}else if(PrevBrowser.contains("firefox1")) {
			FireFoxDriver1 = wd;			
		}else if(PrevBrowser.contains("ie1")) {
			IEDriver1 = wd;				
		}else if(PrevBrowser.contains("chrome")) {
			ChromeDriver = wd;
		}else if(PrevBrowser.contains("firefox")) {
			FireFoxDriver = wd;
		}else if(PrevBrowser.contains("ie")) {
			IEDriver = wd;
		}
		
		// update new PrevBrowser
		if(newBrowser.contains("chrome1")) {
			PrevBrowser = "chrome1";
		}else if(newBrowser.contains("firefox1")) {
			PrevBrowser = "firefox1";		
		}else if(newBrowser.contains("ie1")) {
			PrevBrowser = "ie1";	
		}else if(newBrowser.contains("chrome")) {
			PrevBrowser = "chrome";
		}else if(newBrowser.contains("firefox")) {
			PrevBrowser = "firefox";
		}else if(newBrowser.contains("ie")) {
			PrevBrowser = "ie";
		}
		
	}
	
	public static void initializeCHROME(String WFV) {
		System.setProperty("webdriver.chrome.driver", exeDir + "chromedriver.exe"); 
		//SetChromeDriver(WFV);
		ChromeOptions options = new ChromeOptions();
		if(WFV.toLowerCase().contains("-incognito")) {
			options.addArguments("--incognito");
		}else {
			if(!WFV.contains("1")) {
				options.addArguments("user-data-dir=/path/to/your/custom/profile");	
			}
		}
		
		//Map<String, Object> prefs = new HashMap<String, Object>();
		//prefs.put("profile.default_content_setting_values.notifications", 2);
		//options.setExperimentalOption("prefs", prefs);
		
		//options.addArguments("--disable-notifications");
		
		//Map prefs=new HashMap();
		//prefs.put("profile.default_content_setting_values.notifications",1);
		//options.setExperimentalOption("prefs",prefs);
		//MainDriver = new ChromeDriver(options);
		wd = new ChromeDriver(options);
		SetMyWebDriver(WFV);
	}
	
	public static void initializeFIREFOX(String WFV) {
		System.setProperty("webdriver.gecko.driver", exeDir + "geckodriver.exe"); 
		//SetChromeDriver(WFV);
		FirefoxOptions options = new FirefoxOptions();
		if(WFV.toLowerCase().contains("-private")) {
			options.addArguments("-private");
		}else {
	        ProfilesIni profileIni = new ProfilesIni();
	        FirefoxProfile profile = profileIni.getProfile("default");
	        options.setProfile(profile);
		}
		
		//MainDriver = new FirefoxDriver(options);
		wd = new FirefoxDriver(options);
		SetMyWebDriver(WFV);
	}
	
	public static void initializeIE(String WFV) {
		System.setProperty("webdriver.ie.driver", exeDir + "IEDriverServer.exe"); 
		//SetChromeDriver(WFV);
		//MainDriver = new InternetExplorerDriver();
		wd = new InternetExplorerDriver();
		SetMyWebDriver(WFV);
	}
	
	public static WebDriver initializeBROWSERSTACK() {
		try {
			//BrowserStack[Andriod,Samsung Galaxy S9 Plus,true,9.0]
			
			  String AUTOMATE_USERNAME = "joselledelacruz1";
			  String AUTOMATE_ACCESS_KEY = "xYGzyLRbf1M3C9T7isRd";
			  
			  String URL = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";
			  DesiredCapabilities caps = BSDesireCaps(browser.toLowerCase().trim().replace("browserstack[", "").replace("]", "").split(","));
			  wd = new RemoteWebDriver(new URL(URL), caps);
			  
			/*
			 
			 markTestStatus("failed","Naay title did not match!",driver);
			 
				  // This method accepts the status, reason and WebDriver instance and marks the test on BrowserStack
				  public static void markTestStatus(String status, String reason, WebDriver driver) {
					JavascriptExecutor jse = (JavascriptExecutor)driver;
					jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \""+status+"\", \"reason\": \""+reason+"\"}}");
				  }
			 */
			  System.out.println("BrowserStack Connection is Successful");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//note: https://chromedriver.chromium.org/mobile-emulation
		
		return wd;
	}
	
	static DesiredCapabilities BSDesireCaps(String[] cap) {
		//https://www.browserstack.com/docs/automate/selenium/getting-started/java
		DesiredCapabilities caps = new DesiredCapabilities();
	    caps.setCapability("browserName", cap[0]);
	    caps.setCapability("device", cap[1]);
	    caps.setCapability("realMobile", cap[2]);
	    caps.setCapability("os_version", cap[3]);
	    caps.setCapability("name", cap[1]); // test name
	    caps.setCapability("build", cap[1]); // CI/CD job or build name
	    caps.setCapability("browserstack.local", "false");
	    //caps.setCapability("browserstack.video", "false");
		/*DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("os_version", "10.0");
		caps.setCapability("device", "Samsung Galaxy S20");
		caps.setCapability("real_mobile", "true");
		caps.setCapability("browserstack.local", "false");*/

		return caps;
	}
	
	public static ChromeOptions ChromeOptionsEmulator() { //https://chromedriver.chromium.org/mobile-emulation
		System.setProperty("webdriver.chrome.driver", exeDir + "chromedriver.exe");
		/*
		Map<String, Object> deviceMetrics = new HashMap<>();
		deviceMetrics.put("width", 414);
		deviceMetrics.put("height", 736);
		deviceMetrics.put("pixelRatio", 3.0);


		Map<String, Object> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceMetrics", deviceMetrics);
		//mobileEmulation.put("userAgent", "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
		*/

		//webmobile-chrome[iPhone 6]

		Map<String, String> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceName", browser.replaceAll("^[^\\[]+\\[","").replace("]",""));
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

		return chromeOptions;
	}

//https://stackoverflow.com/questions/27630190/python-selenium-incognito-private-mode
//https://chercher.tech/java/incognito-browser-selenium
//https://stackoverflow.com/questions/17055273/selenium-webdriver-size-of-the-text-field

}
