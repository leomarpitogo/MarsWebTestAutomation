package MarsWTA;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.server.handler.FindElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.sridharbandi.Accessibility;
import io.github.sridharbandi.AccessibilityRunner;
import io.github.sridharbandi.util.Standard;

public class ObjectFactory {
	
	WebDriver driver;
	public String GetWebField;
	public String GetWebFieldVal;
	public String GetCommand;
	public String GetFindBy;
	public boolean autoFocus;
	public boolean EleSelect;
	public boolean EleAvail;
	public boolean EleEnable;
	public boolean EleVisible;
	public String EleText;
	public String[] EleTextArray;
	public boolean CommandStat;
	public String ActualResult;
	public static String StoredWebField;
	public String waitTime;
	public ArrayList<String> AlternativeGetWebField;
	public ArrayList<String> WebTableSortedDataList;
	public ArrayList<String> TestAutoSortedDataList;
	MainExtension MainExt = new MainExtension();
	
	public ObjectFactory(WebDriver ldriver)
	{
		this.driver = ldriver;
	}	
	
	public void SetVal(String lCommand,String lWebFieldVal, String lFindBy, String lWebField)
	{
				this.GetCommand = lCommand;	
				this.GetWebFieldVal = lWebFieldVal;
				this.GetFindBy = lFindBy;
				this.GetWebField = lWebField;
				this.CommandStat = false;
				this.ActualResult = "N/A";
				this.autoFocus = false;
				this.waitTime = "60";
				checkPageIsReady();
				GetStoredWebField();
				ExtractAlternativeGetWebField();

				/*System.out.println("Command: "+ this.GetCommand);
				System.out.println("Text: "+ this.GetWebFieldVal);
				System.out.println("FindBy: "+ this.GetFindBy);
				System.out.println("Field: "+ this.GetWebField);*/
				
	}
	
	public void GetStoredWebField() {
		if(GetWebField.equalsIgnoreCase("StoredWebField()")) {
			this.GetWebField = StoredWebField;
		}
	}
	
	public void ExtractAlternativeGetWebField() {
		
		if(AlternativeGetWebField!=null) {
			AlternativeGetWebField.clear();
		}
		
		
		if(GetWebField.contains("`"))
		{		
			String[] SplitGetWebField = GetWebField.split("\\`");
			String[] SplitLocator = null;
			String GetLocator = "";
			AlternativeGetWebField = new ArrayList<String>();
			for(String Locator: SplitGetWebField) {
				
				 GetLocator = Locator;
				if(Locator.contains("|")) { 				// if has Field|Attribute
					SplitLocator = Locator.split("\\|");
					GetLocator =  SplitLocator[0]; 			// Getting Field
				}

				AlternativeGetWebField.add(GetLocator);
			}
			
			GetWebField = SplitGetWebField[0];	
		}
		
	}
	
	public WebDriver CheckWebAccessibility()
	{
		try {
			//https://github.com/sridharbandi/Java-a11y
			AccessibilityRunner accessibilityRunner = new AccessibilityRunner(driver);
			Accessibility.REPORT_PATH =  MainExt.GetFParentName().replace("\\", "/") + "/AutomationLOG";
	        accessibilityRunner.setStandard(Standard.WCAG2AA);
	        accessibilityRunner.execute();
	        this.ActualResult = "Refer result at " + MainExt.GetFParentName().replace("\\", "/") + "/AutomationLOG/report/json";
	
		    if(accessibilityRunner.errorCount()==0) {
		       CommandStat = true;	
		    }
				
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver SetAttribute()
	{
		CommandStat = false;
		try
		{
			JavascriptExecutor js = (JavascriptExecutor) driver;	
			WebElement Ele = null;
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			Ele = GetElement(SplitGetWebField[0]);
			if(GetWebFieldVal.equalsIgnoreCase("Remove()"))
			{
				js.executeScript("arguments[0].removeAttribute('" + SplitGetWebField[1] +"')", Ele);
			}else{	
				js.executeScript("arguments[0].setAttribute('"+ SplitGetWebField[1] +"', '"+ GetWebFieldVal +"')", Ele);
			}
			
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}			
		
		return this.driver;
	}

	public WebDriver Freeze()
	{	
		CommandStat = false;
		try {
			TimeUnit.SECONDS.sleep(Integer.parseInt(GetWebFieldVal));
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	
	
	public WebDriver CheckFile() {
		
		try{
			if(GetWebFieldVal.toLowerCase().contains("exist(")) {
				
				boolean existFlag = Boolean.parseBoolean(MainExt.RegExGetVal("[a-z|A-Z]+\\(((true|false))\\)", GetWebFieldVal.toLowerCase().trim()));
				String filePath = GetFilePath();
				
				if(!filePath.isEmpty()) {
					
					File file = new File(filePath);
					for(int i=0;i<30;i++) {
						if(file.exists()==existFlag) {
							CommandStat = true;
							break;
						}
						TimeUnit.SECONDS.sleep(1);
					}

				}
				
			}else if(GetWebFieldVal.toLowerCase().contains("size(")) {
				
				
		    	/* For further analysis
				    String path = "C:/Users/leomar.a.pitogo/Downloads/[Environment] - [Sprint No]- [Task Title 1] - Chrome - [AssignedTo].xlsx";
				
				    Path file = Paths.get(path);
				    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
				    System.out.println("creationTime     = " + attr.creationTime());
				    System.out.println("lastAccessTime   = " + attr.lastAccessTime());
				    System.out.println("lastModifiedTime = " + attr.lastModifiedTime());
				    System.out.println("lastModifiedTime = " + attr.);
				
				    System.out.println("isDirectory      = " + attr.isDirectory());
				    System.out.println("isOther          = " + attr.isOther());
				    System.out.println("isRegularFile    = " + attr.isRegularFile());
				    System.out.println("isSymbolicLink   = " + attr.isSymbolicLink());
				    System.out.println("size             = " + attr.size());

				*/
				
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	
	public WebDriver SetText()
	{
		CommandStat = false;
		try
		{
			
			if(this.GetWebFieldVal.equalsIgnoreCase("Clear()")) {
				
				GetElement("").clear();
				CommandStat = true;
				
			}else if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				
				GetElement("").clear();
				GetElement("").sendKeys(MainExt.ExtractGetRowColVal(GetWebFieldVal));
				CommandStat = true;
				
			}else if(this.GetWebFieldVal.toLowerCase().contains("openfiledialog(")){
				GetElement("").click();
				
				if(OpenFileDialog()) {
					CommandStat = true;
				}
			}else if(GetWebFieldVal.toLowerCase().contains(("getjsonval("))){
				
				ServiceFactory NewServiceFactory = new ServiceFactory();
				GetElement("").clear();
				GetElement("").sendKeys(NewServiceFactory.PullValJSON(MainExt.RegExGetVal("^[^\\(]+\\(([^\\)]+)\\)",GetWebFieldVal.trim())));			
				CommandStat = true;
				
			}else if(this.GetWebFieldVal.toLowerCase().contains("dragdropfile(")) {
				
				if(DragDropFile(GetElement(""), 0, 0)) {
					CommandStat = true;
				}
				
			}else{
				GetElement("").clear();
				GetElement("").sendKeys(GetWebFieldVal);
				CommandStat = true;
			}

			checkPageIsReady();
		}catch(Exception e)
		{
			e.printStackTrace();
		}			
		
		return this.driver;
	}
	
	public WebDriver PageContains()
	{
		CommandStat = false;
		try {
			EleText = driver.getPageSource();
			if (GetWebFieldVal.toLowerCase().contains("regex("))
			{
				String[] SplitGetWebFieldVal = GetWebFieldVal.trim().split("\\(");
				if(Pattern.matches(SplitGetWebFieldVal[1].replace(")", ""), EleText))
				{
					CommandStat = true;
				}
			}else
			{
				if(EleText.contains(GetWebFieldVal.trim()))
				{
					CommandStat =true;
				}
			}
			//this.ActualResult = EleText;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver DPSelectDay()
	{
		CommandStat = false;
		try {
			List<WebElement> columns=GetElement("").findElements(By.tagName("td"));

			for (WebElement cell: columns){
			   if (cell.getText().equals(GetWebFieldVal)){
			      cell.findElement(By.linkText(GetWebFieldVal)).click();
			      break;
			   }
			}
			CommandStat = true; 
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		   
	return this.driver;	   
	}
	
	public WebDriver CheckNumberOfElements()
	{
		CommandStat = false;
		try {
			if(GetElementNumbers("").equalsIgnoreCase(GetWebFieldVal))
			{
				CommandStat = true;
			}
		this.ActualResult = GetElementNumbers("");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}

    public WebDriver SetKey()
    {
        CommandStat = false;
        String[] splitGetWebFieldVal = null;   
        String[] multikey = null;
        boolean isMultikey = false;
           
        if(GetWebFieldVal.toLowerCase().contains("pasteviaclipboard")) {
        	splitGetWebFieldVal = GetWebFieldVal.split("\\[");
        	EleText = splitGetWebFieldVal[1].replace("]", "");
        	
        }else {
        	
        	
        	if(GetWebFieldVal.contains(",")) { // for multiple set key in a 1 step
              	
        		multikey = GetWebFieldVal.split("\\,");
        		isMultikey = true;
            	
        	}else {
        		
            	splitGetWebFieldVal = GetWebFieldVal.split("\\(");	
            	EleText = splitGetWebFieldVal[1].replace(")", "");
        	
        	}

        }
        
        try {

              //ExecuteKey(GetElement(""),splitGetWebFieldVal[0], splitGetWebFieldVal[1].replace(")", ""));
    		
    		if(isMultikey) { // for multiple set key in a 1 step
    			
        		for(String mk:multikey) {
        			
            		splitGetWebFieldVal = mk.split("\\(");	
                	EleText = splitGetWebFieldVal[1].replace(")", "");
                	ExecuteKey(GetElement(""),splitGetWebFieldVal[0], EleText);
                	TimeUnit.SECONDS.sleep(1);
                	
        		}
        		
    		}else {
    			ExecuteKey(GetElement(""),splitGetWebFieldVal[0], EleText);
    		}

            CommandStat = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return this.driver;
    }
	
    
	public void ExecuteKey(WebElement Ele, String Key, String val)
	{
		
		if(Key.equalsIgnoreCase("Control")) {
			
			GetElement("").sendKeys(Keys.chord(Keys.CONTROL,val.toLowerCase()));
			
		}else if(Key.equalsIgnoreCase("PasteViaClipboard")) {
			
			CopyClipboard(val);
			GetElement("").sendKeys(Keys.chord(Keys.CONTROL,"v"));
			
		}else if(Key.equalsIgnoreCase("Robot")) {
			try {
				TimeUnit.SECONDS.sleep(5);
				Robot robot = new Robot();
				driver.switchTo().activeElement();
				if(val.equalsIgnoreCase("Escape")) {
					robot.keyPress(KeyEvent.VK_ESCAPE);
					robot.keyRelease(KeyEvent.VK_ESCAPE);
				}else if(val.equalsIgnoreCase("Cancel")) {
					robot.keyPress(KeyEvent.VK_CANCEL);
					robot.keyRelease(KeyEvent.VK_CANCEL);
				}else if(val.equalsIgnoreCase("ArrowUp")) {
					robot.keyPress(KeyEvent.VK_KP_UP);
					robot.keyRelease(KeyEvent.VK_KP_UP);
				}else if(val.equalsIgnoreCase("ArrowDown")) {
					robot.keyPress(KeyEvent.VK_KP_DOWN);
					robot.keyRelease(KeyEvent.VK_KP_DOWN);
				}else if(val.equalsIgnoreCase("ArrowLeft")) {
					robot.keyPress(KeyEvent.VK_KP_LEFT);
					robot.keyRelease(KeyEvent.VK_KP_LEFT);
				}else if(val.equalsIgnoreCase("ArrowRight")) {
					robot.keyPress(KeyEvent.VK_KP_RIGHT);
					robot.keyRelease(KeyEvent.VK_KP_RIGHT);
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else {
			for(int i=1; i<=Integer.parseInt(val);i++)
			{
				if(Key.equalsIgnoreCase("Tab"))
				{
					Ele.sendKeys(Keys.TAB);
				}else if(Key.equalsIgnoreCase("ArrowDown"))
				{
					Ele.sendKeys(Keys.ARROW_DOWN);
				}else if(Key.equalsIgnoreCase("ArrowUp"))
				{
					Ele.sendKeys(Keys.ARROW_UP);
				}else if(Key.equalsIgnoreCase("ArrowRight"))
				{
					Ele.sendKeys(Keys.ARROW_RIGHT);
				}else if(Key.equalsIgnoreCase("ArrowLeft"))
				{
					Ele.sendKeys(Keys.ARROW_LEFT);
				}else if(Key.equalsIgnoreCase("Enter"))
				{
					Ele.sendKeys(Keys.ENTER);
				}else if(Key.equalsIgnoreCase("Escape"))
				{
					Ele.sendKeys(Keys.ESCAPE);
				}else if(Key.equalsIgnoreCase("BackSpace"))
				{
					Ele.sendKeys(Keys.BACK_SPACE);
				}else if(Key.equalsIgnoreCase("Delete"))
				{
					Ele.sendKeys(Keys.DELETE);
				}else if(Key.equalsIgnoreCase("Space"))
				{
					Ele.sendKeys(Keys.SPACE);
				}
			}
		}
		
	}
	
	public WebDriver RichSetText()
	{
		CommandStat = false;
		try {
			JavascriptExecutor js = (JavascriptExecutor) this.driver; 
			WebElement ele = driver.findElement(By.xpath(GetWebField));
			js.executeScript("arguments[0].setAttribute('style', 'display: block;'", ele);
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CheckDropdownVal()
	{
		this.ActualResult = "";
		CommandStat = false;
		try {
			Select categories = new Select(GetElement(""));
            List<WebElement> optionsList = categories.getOptions();
            String[] SplitGetWebFieldVal = GetWebFieldVal.split(",");
            int CounterFound =0;
        
	     	for(String str: SplitGetWebFieldVal){
	            for (WebElement option : optionsList) {
	                  if(str.trim().equals(option.getText().trim())){
	                	  CounterFound++;
	                  }
	                  this.ActualResult+= option.getText() + ",";
	            }
    		}

	         if(CounterFound==SplitGetWebFieldVal.length){
            	CommandStat = true;	
	         }

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	public WebDriver CSSValEqual()
	{
		CommandStat = false;
		EleText = "";
		try
		{
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			String StaticAttri = " XCoordinates, YCoordinates, ";
			
			if(StaticAttri.toLowerCase().contains(" " + SplitGetWebField[1].toLowerCase().trim() + ", "))
			{
				EleText = GetElementVal(GetElement(SplitGetWebField[0]), SplitGetWebField[1].toString());	
			}else{
				
				EleText = GetElement(SplitGetWebField[0]).getCssValue(SplitGetWebField[1].toString());
			}
		
			this.ActualResult = EleText;
			if (EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CSSValNotEqual()
	{
		CommandStat = false;
		EleText = "";
		try
		{
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			String StaticAttri = " XCoordinates, YCoordinates, ";
			
			if(StaticAttri.toLowerCase().contains(" " + SplitGetWebField[1].toLowerCase().trim() + ", "))
			{
				EleText = GetElementVal(GetElement(SplitGetWebField[0]), SplitGetWebField[1].toString());	
			}else{
				
				EleText = GetElement(SplitGetWebField[0]).getCssValue(SplitGetWebField[1].toString());
			}
		
			this.ActualResult = EleText;
			if (!EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public String GetElementVal(WebElement ele, String CSS)
	{
		String CSSVal = "";
		
		if(CSS.equalsIgnoreCase("XCoordinates"))
		{
			CSSVal = String.valueOf(ele.getLocation().getX());
		}else if(CSS.equalsIgnoreCase("YCoordinates"))
		{
			CSSVal = String.valueOf(ele.getLocation().getY());
		}else if(CSS.equalsIgnoreCase("Height"))
		{
			CSSVal = String.valueOf(ele.getSize().getHeight());
		}else if(CSS.equalsIgnoreCase("Width"))
		{
			CSSVal = String.valueOf(ele.getSize().getWidth());
		}
		
		return CSSVal;
	}
	
	public WebDriver CSSValContains()
	{
		CommandStat = false;
		EleText = "";
		try
		{
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0]).getCssValue(SplitGetWebField[1].toString());
			this.ActualResult = EleText;
			if (EleText.contains(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver SetTextEnter()
	{

		try
		{
			GetElement("").clear();
			if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				
				GetElement("").clear();
				GetElement("").sendKeys(MainExt.ExtractGetRowColVal(GetWebFieldVal));
				
			}else if(GetWebFieldVal.toLowerCase().contains(("getjsonval("))){
					
					ServiceFactory NewServiceFactory = new ServiceFactory();
					GetElement("").clear();
					GetElement("").sendKeys(NewServiceFactory.PullValJSON(MainExt.RegExGetVal("^[^\\(]+\\(([^\\)]+)\\)",GetWebFieldVal.trim())));			

			}else{ 
				GetElement("").sendKeys(GetWebFieldVal);			
			}	
			
			GetElement("").sendKeys(Keys.ENTER);
			//System.out.println(String.valueOf(b));
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}			
		
		return this.driver;
	}

	public WebDriver NavigatePage()
	{
		CommandStat = false;
		try
		{
			String[] SplitGetWebFieldVal = GetWebFieldVal.trim().split("\\(");
			NavigatePageTimes(SplitGetWebFieldVal[0].trim(), Integer.parseInt(SplitGetWebFieldVal[1].trim().replace(")", "")));
			CommandStat = true;
			checkPageIsReady();
		}catch(Exception e)
		{
			e.printStackTrace();
		}			
		
		return this.driver;
	}
	
	public void NavigatePageTimes(String Nav, int times)
	{
		for(int i=1; i<=times; i++)
		{
			if (Nav.equalsIgnoreCase("Refresh"))
			{
				driver.navigate().refresh();
			}else if(Nav.equalsIgnoreCase("Forward"))
			{
				driver.navigate().forward();
			}else if(Nav.equalsIgnoreCase("Back"))
			{
				driver.navigate().back();
			}
		}
	}
	
	public WebDriver ClickButton()
	{
		CommandStat = false;
		try
		{
			freeze();
			new WebDriverWait(driver,60).until(ExpectedConditions.elementToBeClickable(GetElement("")));
			//GetElement("").click();
			((JavascriptExecutor)driver).executeScript("arguments[0].click();", GetElement(""));
			CommandStat = true;
			freeze();
			//checkPageIsReady();
		}catch(Exception e)
		{
			e.printStackTrace();
			/*try {
				((JavascriptExecutor)driver).executeScript("arguments[0].click();", GetElement(""));
				CommandStat = true;
			}catch(Exception x) {
				x.printStackTrace();
			}*/
		}			
			
		return this.driver;
	}
	
	
	public String[] WaitUntilTrue(String condition, String text) {
		String[] webresult = {"false","",""};
		Boolean isVisible = false;
		try {
			
			if(new WebDriverWait(driver,60).until(ExpectedConditions.visibilityOf(GetElement("")))!=null) {
				isVisible = true;
			}
			
			
			if(condition.equalsIgnoreCase("visibility")) {
				if(new WebDriverWait(driver,60).until(ExpectedConditions.visibilityOf(GetElement("")))!=null) {
					webresult[0] = "true";
					webresult[1] = "n/a";
				}

			}

			if(condition.equalsIgnoreCase("label") && isVisible) {
				if(new WebDriverWait(driver,60).until(ExpectedConditions.textToBePresentInElement(GetElement(""), text))) {
					webresult[0] = "true";
					webresult[1] = GetElement("").getText();
				}	

			}
		
			if(condition.equalsIgnoreCase("text") && isVisible) {
				if(new WebDriverWait(driver,60).until(ExpectedConditions.textToBePresentInElementValue(GetElement(""), text))) {
					webresult[0] = "true";
					webresult[1] = GetElement("").getAttribute("value");
				}

			}

			webresult[2] = text; 
		}catch(Exception e) {
			e.printStackTrace();
		}

		
		//System.out.println("flag: " + webresult[0] + " \ntext: " + webresult[1]);
		
		return webresult;
	}
	
	public WebDriver DoubleClick()
	{
		CommandStat = false;
		try
		{
			freeze();
			Actions actions = new Actions(driver);
			actions.doubleClick(GetElement("")).perform();
			CommandStat = true;
			freeze();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}			
		
		return this.driver;
	}
	
	public WebDriver RightClick()
	{
		CommandStat = false;
		try
		{
			Actions actions = new Actions(driver);
			actions.contextClick(GetElement("")).perform();
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}			
		
		return this.driver;
	}
	
	public WebDriver CheckAvail()
	{
		CommandStat = false;
		EleAvail = false;
		try
		{
			
			if(GetWebFieldVal.toLowerCase().contains("false(")) {
				String waitingTime = MainExt.RegExGetVal("false\\((([0-9]+))\\)", GetWebFieldVal.toLowerCase()); // getting waiting time
				this.waitTime = waitingTime;
				GetWebFieldVal = "False";
			}
			
			EleAvail = GetElement("").isDisplayed();
		
			this.ActualResult = String.valueOf(EleAvail);
			
			if (EleAvail == Boolean.parseBoolean(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			if(Boolean.parseBoolean(GetWebFieldVal)==false)
			{
				CommandStat = true;
			}
				//e.printStackTrace();
		}
		
		return this.driver;
	}

	public WebDriver CompareTextNotContains()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{
			EleAvail = GetElement("").isDisplayed();
			if (EleAvail)
			{
				EleText = GetElement("").getAttribute("value");
			}
			this.ActualResult = EleText;			
			if(EleAvail && !EleText.contains(GetWebFieldVal))
			{
				CommandStat = true;
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public void PrintSysMode(String print) {
		if(MainExtension.getUIMode()) {
			MainExtension.SetErrorMessage("\n" + print);
		}else {
			System.out.println(print);
		}
	}
	
	public WebDriver CompareTextContains()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{
			
			if(GetWebFieldVal.toLowerCase().contains("waituntiltrue(")){
				
				EleTextArray = null;
				EleTextArray = WaitUntilTrue("text",MainExt.RegExGetVal("[a-z|A-Z]+[^(]+\\(([^)]+)\\)",GetWebFieldVal.trim()));
				EleAvail = Boolean.parseBoolean(EleTextArray[0]);
				EleText = EleTextArray[1];
				//this.ActualResult = EleText;
				
			}else {
				
				EleAvail = GetElement("").isDisplayed();
				if (EleAvail)
				{
					EleText = GetElement("").getAttribute("value");
				}
			}

			
			// Extract Database value if GetRowColVal( is observed.
			if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				GetWebFieldVal = MainExt.ExtractGetRowColVal(GetWebFieldVal);
				PrintSysMode("Expected Result: " + GetWebFieldVal);
			}
			
			this.ActualResult = EleText;
			
			if(GetWebFieldVal.contains("~")) {
				if(EleAvail && MatchItems(GetWebFieldVal,EleText)) {
					CommandStat = true;
				}
			}else if(GetWebFieldVal.equalsIgnoreCase("Empty()"))
			{
				if(EleAvail && (EleText.equalsIgnoreCase("") || EleText.isEmpty() || EleText.equalsIgnoreCase(null)))
				{
					CommandStat = true;
				}
			}else if(GetWebFieldVal.toLowerCase().contains("waituntiltrue("))
			{
				if(EleAvail && EleTextArray[1].contains(EleTextArray[2])) {
					CommandStat = true;
				}
				
			}else {
				
				if(EleAvail && EleText.contains(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareLabelNotContains()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{
			EleAvail = GetElement("").isDisplayed();
			if (EleAvail)
			{
				EleText = GetElement("").getText();
			}
			this.ActualResult = EleText;
			
			if(GetWebFieldVal.contains("~")) {
				if(EleAvail && MatchItems(GetWebFieldVal,EleText)) {
					CommandStat = true;
				}
			}else {
				if(EleAvail && !EleText.contains(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareLabelContains()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{
			List<WebElement> elementList = null;
			if(GetWebFieldVal.toLowerCase().contains("values(")) 
			{
				EleAvail = Integer.parseInt(GetElementNumbers("")) > 0;
				
				if(EleAvail)
				{
					List<String> webTableDataList = CheckSorting(GetWebFieldVal,this.GetWebField).get(0);
					//List<String> testAutoDataList = CheckSorting(GetWebFieldVal,this.GetWebField).get(1);
					
					EleText = webTableDataList.toString();
					GetWebFieldVal = GetWebFieldVal.replaceAll("^[a-z|A-Z]+[^\\(]+\\(", "[").replace(")", "]");			
				}
				
			}else if(GetWebFieldVal.toLowerCase().contains("storexpathifpass(")){
				
				elementList = driver.findElements(By.xpath(GetWebField));			
			
				if(elementList.size()!=0) {
					
					EleAvail = true;
					StoredWebField = "";
					int i = 1;
					String TempWebFieldVal = GetWebFieldVal.replaceAll("^[^\\(]+\\(", "").replace(")", "");
					List<String> webTableDataList = CheckSorting(GetWebFieldVal,GetWebField).get(0);
					EleText = webTableDataList.toString();					
					
					for (WebElement we : elementList) {
						if(we.getText().contains(TempWebFieldVal)) {
				
							Matcher m = Pattern.compile("(/[a-zA-Z]+)/").matcher(RefineXpath(we.toString()));
							if(m.find()) {
								StoredWebField = RefineXpath(we.toString()).replace(m.group(1) + "/", m.group(1) + "[" + String.valueOf(i) + "]/");
								//System.out.println("StoredWebField: " + StoredWebField);
								PrintSysMode("StoredWebField: " + StoredWebField);
							}
							break;							
						}
						
						i++;
					}
					
				}	
			
			}else if(GetWebFieldVal.toLowerCase().contains("waituntiltrue(")){
				EleTextArray = null;
				EleTextArray = WaitUntilTrue("label",MainExt.RegExGetVal("[a-z|A-Z]+[^(]+\\(([^)]+)\\)",GetWebFieldVal.trim()));
				EleAvail = Boolean.parseBoolean(EleTextArray[0]);
				EleText = EleTextArray[1];
				this.ActualResult = EleText;
				
			}else {
				
				EleAvail = GetElement("").isDisplayed();
				if (EleAvail)
				{
					EleText = GetElement("").getText();
				}
				this.ActualResult = EleText;
								
			}

			// Extract Database value if GetRowColVal( is observed.
			if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				GetWebFieldVal = MainExt.ExtractGetRowColVal(GetWebFieldVal).trim();
				//System.out.println("Expected Result: " + GetWebFieldVal);
				PrintSysMode("Expected Result: " + GetWebFieldVal);
			}
			
			if(GetWebFieldVal.contains("~")) {
				if(EleAvail && MatchItems(GetWebFieldVal,EleText)) {
					CommandStat = true;
				}
			}else if(GetWebFieldVal.equalsIgnoreCase("Empty()"))
			{
				if(EleAvail && (EleText.equalsIgnoreCase("") || EleText.isEmpty() || EleText.equalsIgnoreCase(null)))
				{
					CommandStat = true;
				}
			}else if(GetWebFieldVal.toLowerCase().contains("storexpathifpass")){
				if(EleAvail && !StoredWebField.isEmpty()) {
					CommandStat = true;
				}
			}else if(GetWebFieldVal.toLowerCase().contains("waituntiltrue("))
			{
				if(EleAvail && EleTextArray[1].contains(EleTextArray[2])) {
					CommandStat = true;
				}	
			}else {
				
				if(EleAvail && EleText.contains(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public String RefineXpath(String Xpath) {
		String RefinedXpath = "";
		Matcher p = Pattern.compile("xpath: ([^\\$]+)").matcher(Xpath);
		if(p.find()) {
			//System.out.println(p.group(1));
			RefinedXpath = p.group(1).toString().replace("]]", "]");
		}
		return RefinedXpath;
	}
	
	public WebDriver CompareLabelEqual()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{	

			List<WebElement> elementList = null;
			if(GetWebFieldVal.equalsIgnoreCase("Ascending()") || GetWebFieldVal.equalsIgnoreCase("Descending()") || GetWebFieldVal.equalsIgnoreCase("Ascending(Date & Time)") || GetWebFieldVal.equalsIgnoreCase("Descending(Date & Time)") || GetWebFieldVal.toLowerCase().contains("values(")
						||GetWebFieldVal.equalsIgnoreCase("Ascending(with decimal)") || GetWebFieldVal.equalsIgnoreCase("Descending(with decimal)")) 
			{
				EleAvail = Integer.parseInt(GetElementNumbers("")) > 0;
				
				if(EleAvail)
				{
					List<String> webTableDataList = CheckSorting(GetWebFieldVal,this.GetWebField).get(0);
					List<String> testAutoDataList = CheckSorting(GetWebFieldVal,this.GetWebField).get(1);
					
					EleText = webTableDataList.toString();
								
					if(GetWebFieldVal.toLowerCase().contains("values(")) {
						GetWebFieldVal = GetWebFieldVal.replaceAll("^[a-z|A-Z]+[^\\(]+\\(", "[").replace(")", "]");
					}else {
						GetWebFieldVal = testAutoDataList.toString();
					}
					//System.out.println("Expected Result: " + testAutoDataList.toString());
					PrintSysMode("Expected Result: " + testAutoDataList.toString());
				}
			}else if(GetWebFieldVal.toLowerCase().contains("storexpathifpass(")){
				
				elementList = driver.findElements(By.xpath(GetWebField));			
			
				if(elementList.size()!=0) {
					
					EleAvail = true;
					StoredWebField = "";
					int i = 1;
					String TempWebFieldVal = GetWebFieldVal.replaceAll("^[^\\(]+\\(", "").replace(")", "");
					List<String> webTableDataList = CheckSorting(GetWebFieldVal,GetWebField).get(0);
					EleText = webTableDataList.toString();					
					
					for (WebElement we : elementList) {
						if(we.getText().equals(TempWebFieldVal)) {
				
							Matcher m = Pattern.compile("(/[a-zA-Z]+)/").matcher(RefineXpath(we.toString()));				
							if(m.find()) {
								StoredWebField = RefineXpath(we.toString()).replace(m.group(1) + "/", m.group(1) + "[" + String.valueOf(i) + "]/");
								//System.out.println("StoredWebField: " + StoredWebField);
								PrintSysMode("StoredWebField: " + StoredWebField);
							}
							break;							
						}
						
						i++;
					}
					
				}	
			}else if(GetWebFieldVal.toLowerCase().contains("waituntiltrue(")){
				EleTextArray = null;
				EleTextArray = WaitUntilTrue("label",MainExt.RegExGetVal("[a-z|A-Z]+[^(]+\\(([^)]+)\\)",GetWebFieldVal.trim()));
				EleAvail = Boolean.parseBoolean(EleTextArray[0]);
				EleText = EleTextArray[1];
				this.ActualResult = EleText;	
			}else {
				EleAvail = GetElement("").isDisplayed();
				if (EleAvail)
				{
					EleText = GetElement("").getText();
				}
			}

			// Extract Database value if GetRowColVal( is observed.
			if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				GetWebFieldVal = MainExt.ExtractGetRowColVal(GetWebFieldVal).trim();
				//System.out.println("Expected Result: " + GetWebFieldVal);
				PrintSysMode("Expected Result: " + GetWebFieldVal);
			}
			
			this.ActualResult = EleText;
			if(GetWebFieldVal.equalsIgnoreCase("Empty()"))
			{
				if(EleAvail && (EleText.equalsIgnoreCase("") || EleText.isEmpty() || EleText.equalsIgnoreCase(null)))
				{
					CommandStat = true;
				}
			}else if(GetWebFieldVal.toLowerCase().contains("storexpathifpass(")){
				
				if(EleAvail && !StoredWebField.isEmpty()) {
					CommandStat = true;
				}
			}else if(GetWebFieldVal.toLowerCase().contains("waituntiltrue("))
			{
				if(EleAvail && EleTextArray[1].equals(EleTextArray[2])) {
					CommandStat = true;
				}		
			}else
			{
				if(EleAvail && EleText.equals(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareLabelNotEqual()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{	

			EleAvail = GetElement("").isDisplayed();
			if (EleAvail) 
			{
					EleText = GetElement("").getText();	
			}

			this.ActualResult = EleText;
			if(GetWebFieldVal.equalsIgnoreCase("Empty()"))
			{
				if(EleAvail && !EleText.isEmpty())
				{
					CommandStat = true;
				}
			}else
			{
				if(EleAvail && !EleText.equals(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	

	public List<List<String>> CheckSorting(String sortingType, String elementLocator) 
	{
		List<WebElement> elementList = driver.findElements(By.xpath(elementLocator));
		WebTableSortedDataList = new ArrayList<>();
		TestAutoSortedDataList = new ArrayList<>();
		
		for (WebElement we : elementList) {
			WebTableSortedDataList.add(we.getText());
			TestAutoSortedDataList.add(we.getText());
		}
		
		if (sortingType.equalsIgnoreCase("Ascending()")) 
		{
			Collections.reverse(TestAutoSortedDataList);
			Collections.sort(TestAutoSortedDataList, String.CASE_INSENSITIVE_ORDER);
		} else if (sortingType.equalsIgnoreCase("Descending()")) 
		{
			Collections.sort(TestAutoSortedDataList, String.CASE_INSENSITIVE_ORDER);
			Collections.reverse(TestAutoSortedDataList);
		} else if (sortingType.equalsIgnoreCase("Ascending(Date & Time)")) 
		{
			Collections.reverse(TestAutoSortedDataList);
			sortDateAscending(TestAutoSortedDataList);
		} else if (sortingType.equalsIgnoreCase("Descending(Date & Time)")) 
		{
			sortDateAscending(TestAutoSortedDataList);
			Collections.reverse(TestAutoSortedDataList);
		} else if (sortingType.equalsIgnoreCase("Ascending(with decimal)")) 
		{
			Collections.reverse(TestAutoSortedDataList);
		}
		else if (sortingType.equalsIgnoreCase("Descending(with decimal)")) 
		{
			Collections.reverse(TestAutoSortedDataList);
			TestAutoSortedDataList = DescendingDecimal(TestAutoSortedDataList);
		}


		List<List<String>> columnDataList = new ArrayList<List<String>>();
		columnDataList.add(WebTableSortedDataList);
		columnDataList.add(TestAutoSortedDataList);

		return columnDataList;
	}

	public ArrayList<String> DescendingDecimal(ArrayList<String> arrdec){
		ArrayList<String> dec = new ArrayList<String>();
		
		
		for(int i=arrdec.size()-1;i>=0;i--) {
			dec.add(arrdec.get(i));
		}
		
		TestAutoSortedDataList.clear();
		
		return dec;
	}
	
	// Special method for checking the sorting functionality of Date & Time column
	public static void sortDateAscending(List<String> dateList) 
	{

		Collections.sort(dateList, new Comparator<String>() {
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy',' hh:mm:ss");

			@Override
			public int compare(String DATE_1, String DATE_2) {
				try {
					return dateFormat.parse(DATE_1).compareTo(dateFormat.parse(DATE_2));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
	}
	
	public WebDriver SetLabel()
	{
		CommandStat = false;
		EleText = "";
		try {
			
			if(this.GetWebFieldVal.equalsIgnoreCase("Clear()")) {
				((JavascriptExecutor)driver).executeScript("arguments[0].innerText=''", GetElement(""));
				CommandStat = true;
			}else if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				DatabaseFactory DatabaseFactoryNew = new DatabaseFactory();
				DatabaseFactoryNew.SetVal("","","",GetWebFieldVal);
				DatabaseFactoryNew.ResultValEqual();
				((JavascriptExecutor)driver).executeScript("arguments[0].innerText='" + DatabaseFactoryNew.GetActualResult() +"'", GetElement(""));
				CommandStat = true;
			}else
			{
				((JavascriptExecutor)driver).executeScript("arguments[0].innerText='" + GetWebFieldVal +"'", GetElement(""));
				CommandStat = true;	
			}
					
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
			
		return this.driver;
	}
	
	public WebDriver CompareTextEqual()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{

			EleAvail = GetElement("").isDisplayed();
			if (EleAvail)
			{
				EleText = GetElement("").getAttribute("value");
			}
			
			
			// Extract Database value if GetRowColVal( is observed.
			if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				GetWebFieldVal = MainExt.ExtractGetRowColVal(GetWebFieldVal);
				//System.out.println("Expected Result: " + GetWebFieldVal);
				PrintSysMode("Expected Result: " + GetWebFieldVal);
			}
			
			this.ActualResult = EleText;
			
				if(GetWebFieldVal.equalsIgnoreCase("Empty()"))
				{
					
					if(EleAvail && (EleText.equalsIgnoreCase("") || EleText.isEmpty() || EleText.equalsIgnoreCase(null)))
					{
						CommandStat = true;
					}
				}else
				{
					if(EleAvail && EleText.equals(GetWebFieldVal))
					{
						CommandStat = true;
					}
				}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareTextNotEqual()
	{
		CommandStat = false;
		EleAvail = false;
		EleText = "";
		try
		{
			EleAvail = GetElement("").isDisplayed();
			if (EleAvail)
			{
				EleText = GetElement("").getAttribute("value");
			}
			
			
			this.ActualResult = EleText;
			
			if(GetWebFieldVal.equalsIgnoreCase("Empty()")) {
				if(EleAvail && !EleText.isEmpty()) {
					CommandStat = true;
				}
			}else {
				if(EleAvail && !EleText.equals(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}
			

			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver SelectByIndex()
	{
		CommandStat = false;
		try {
			Select select = new Select(GetElement(""));
			select.selectByIndex(Integer.parseInt(GetWebFieldVal));
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();			
		}

		return this.driver;
	}
	
	public WebDriver CheckSelect()
	{
		CommandStat = false;
		EleSelect = false;
		try {
			EleSelect = GetElement("").isSelected();
			if(EleSelect == Boolean.parseBoolean(GetWebFieldVal))
			{
				CommandStat = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CheckEnable()
	{
		CommandStat = false;
		EleEnable = false;
		try {
			EleEnable = GetElement("").isEnabled();
			if(EleEnable == Boolean.parseBoolean(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
		
		return this.driver;
	}
	
	public WebDriver AlertTextEqual()
	{
		//CommandStat = false;
		try {
			Alert alert = driver.switchTo().alert();
			EleText = alert.getText();
			TimeUnit.SECONDS.sleep(5);
			alert.accept();
			this.ActualResult = EleText;
			if(EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	
	public WebDriver TitleEqual()
	{
		CommandStat = false;
		try {
			EleText = driver.getTitle();
			this.ActualResult = EleText;
			if(EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return this.driver;
	}
	
	public WebDriver CheckAllImagesAvailability() {
		CommandStat = false;
		try {
			
			int invalidImageCount = 0;
			List<WebElement> imagesList = driver.findElements(By.tagName(GetWebField));
			//System.out.println("Total no. of images are " + imagesList.size());
			
			for (WebElement imgElement : imagesList) {
				if (imgElement != null) {
					HttpClient client = HttpClientBuilder.create().build();
					HttpGet request = new HttpGet(imgElement.getAttribute("src"));
					HttpResponse response = client.execute(request);
					if (response.getStatusLine().getStatusCode() != 200) {
						invalidImageCount++;
						this.ActualResult = this.ActualResult + imgElement.getAttribute("src").toString() + ": Fail\n";
					}else {
						this.ActualResult = this.ActualResult + imgElement.getAttribute("src").toString() + ": Pass\n";
					}
					//System.out.println("Total no. of invalid images are "	+ invalidImageCount);
				}
			}
			TimeUnit.SECONDS.sleep(1);
			
			if(Boolean.parseBoolean(GetWebFieldVal)==true)
			{
				if (invalidImageCount == 0)
				{
					CommandStat = true;
				}
			}else if(Boolean.parseBoolean(GetWebFieldVal)==false)
			{
				if (invalidImageCount == imagesList.size())
				{
					CommandStat = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	
	public WebDriver CheckEnableDropDownItem()
	{
		//ObjectFactorCommandStat = false;
		try {
			List<WebElement> options=GetElement("").findElements(By.tagName("option"));
			EleEnable = true;

			String SGetWebFieldVal[] = GetWebFieldVal.split("\\(");
			int idx = Integer.parseInt(SGetWebFieldVal[1].trim().replace(")", ""));
			if(options.get(idx).getAttribute("disabled")!=null)
			{	
				if (options.get(idx).getAttribute("disabled").equalsIgnoreCase("true"))
				{
					EleEnable = false;
				}
				
			}	
			
			if(EleEnable==Boolean.parseBoolean(SGetWebFieldVal[0].trim()))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
		
		return this.driver;
	}
	
	public String GetCommandStat()
	{
		String r = "Fail";
		if(this.CommandStat)
		{
			r= "Pass";
		}
		return r;
	}
	
	public WebDriver SetVerSecurityCode()
	{
		//CommandStat = false;	
		try {
			if(driver.getTitle().equals("ESO Symantec VIP"))
			{
				new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By.id("vipOoblink"))).click();
				new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By.id("vipSend"))).click();
					
					//Get(input by user)
				String code = GetEnteredSecurityCode();
					
					//set security code txt(id=otpInput)
				if(!code.trim().equalsIgnoreCase("")) {
					driver.findElement(By.id("otpInput")).sendKeys(code);
					
					// remember
					driver.findElement(By.xpath("//*[@id=\"vipRememberDeviceDiv\"]/label")).click();
					
					//click continue
					driver.findElement(By.id("vipSubmitOTP")).click();
					
					// skip for now
					driver.findElement(By.id("vipSkipbtn")).click();
					CommandStat = true;
				}

			}
			else {
				CommandStat = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver Hover()
	{
		try {
			
			Actions action = new Actions(this.driver);
			
			// Extract Database value if GetRowColVal( is observed.
			if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
				GetWebFieldVal = MainExt.ExtractGetRowColVal(GetWebFieldVal).trim();
				//System.out.println("Expected Result: " + GetWebFieldVal);
				PrintSysMode("Expected Result: " + GetWebFieldVal);
			}
			
			if(!GetWebFieldVal.isEmpty()) {
				
				EleTextArray = new String[] {"",""};
				String[] SplitGetWebField = GetWebField.trim().split("\\|");
				action.moveToElement(GetElement(SplitGetWebField[0].toString().trim())).perform();
				
				EleTextArray[0] = GetWebFieldVal;
				GetWebFieldVal = MainExt.RegExGetVal("[a-z|A-Z]+[^(]+\\(([^)]*)\\)",GetWebFieldVal);
			   
				if(EleTextArray[0].toLowerCase().contains("textequal(")) {
					EleTextArray[1] = GetElement(SplitGetWebField[1].toString().trim()).getAttribute("value");
				}	

				if(EleTextArray[0].toLowerCase().contains("labelequal(")) {
					EleTextArray[1] = GetElement(SplitGetWebField[1]).getText();
				}	
				
				if(EleTextArray[0].toLowerCase().contains("textcontains(")) {
					EleTextArray[1] = GetElement(SplitGetWebField[1].toString().trim()).getAttribute("value");
				}		

				if(EleTextArray[0].toLowerCase().contains("labelcontains(")) {
					EleTextArray[1] = GetElement(SplitGetWebField[1]).getText();
				}
				
				if(EleTextArray[0].toLowerCase().contains("click()")) {
					TimeUnit.SECONDS.sleep(3);
					//GetElement(SplitGetWebField[1]).click();
					((JavascriptExecutor)driver).executeScript("arguments[0].click();", GetElement(SplitGetWebField[1]));
					CommandStat = true;
				}
				
				if(EleTextArray[0].toLowerCase().contains("cssvalequal(")) {
					String[] SplitGetWebFieldByPar = SplitGetWebField[1].split("(");
					EleTextArray[1]  = GetElement(SplitGetWebFieldByPar[0]).getCssValue(SplitGetWebFieldByPar[1].toString().replace(")", ""));
				}
				
				if(EleTextArray[0].toLowerCase().contains("cssvalcontains(")) {
					String[] SplitGetWebFieldByPar = SplitGetWebField[1].split("(");
					EleTextArray[1]  = GetElement(SplitGetWebFieldByPar[0]).getCssValue(SplitGetWebFieldByPar[1].toString().replace(")", ""));
				}
				
				if(EleTextArray[1]!=null) {
					
					this.ActualResult = EleTextArray[1];
					
					if(EleTextArray[0].toLowerCase().contains("equal(")) {
						if(EleTextArray[1].equals(GetWebFieldVal))
						{
							CommandStat = true; // true for both hover and handling
						}
					}
					
					if(EleTextArray[0].toLowerCase().contains("contains(")) {
						if(EleTextArray[1].contains(GetWebFieldVal))
						{
							CommandStat = true; // true for both hover and handling
						}
					}
					

				}else {
					this.ActualResult = "";
				}
					
			}else {
				action.moveToElement(GetElement("")).perform();
				CommandStat = true; // true for hover command only
			}

					
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
			
		return this.driver;
	}
	
	public WebDriver HoverClick()
	{
		CommandStat = false;
		try {
			Actions action = new Actions(this.driver);
			action.moveToElement(GetElement("")).click(GetElement("")).build().perform();
			CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
			
		return this.driver;
	}
	
	public String GetEnteredSecurityCode()
	{	

		String Code = "";
		boolean fLag =true;

		try {
			final JTextField jtf = new JTextField();
			JOptionPane jop = new JOptionPane(jtf, JOptionPane.QUESTION_MESSAGE,
			        JOptionPane.OK_CANCEL_OPTION);
			JDialog dialog = jop.createDialog("Enter Security Code:");
						
			dialog.setVisible(true);
			int result = (Integer) jop.getValue();
			dialog.dispose();
			
			if (result == JOptionPane.OK_OPTION) {
				
			    	Code = jtf.getText();
			    	if(Code.equalsIgnoreCase(""))
			    	{
			    		fLag = false;
			    	}
			}else
			{
				fLag = false;
			}
	
			/*if(fLag==false)
			{
				//System.out.println("Security Code is empty, application will exit.");
				PrintSysMode("Security Code is empty, application will exit.");
				System.exit(0);
			}*/
		}catch(Exception e)
		{
			//e.printStackTrace();
			//System.out.println("Something wrong in the Security Code, application will exit.");
			/*PrintSysMode("Something wrong in the Security Code, application will exit.");
			System.exit(0);*/
		}

		
		return new String(Code);
	}
	
	public WebDriver NewTab()
	{
		CommandStat = false;
		try {
			driver.manage().deleteAllCookies();
			((JavascriptExecutor)this.driver).executeScript("window.");
		  	ArrayList<String> tabs = new ArrayList<String> (this.driver.getWindowHandles());
		    driver.switchTo().window(tabs.get(Integer.parseInt(GetWebFieldVal))); //switches to new tab
		    driver.get(GetWebField);
		    CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}	    
		return this.driver;
	}
	//NewCode
	public WebDriver SwitchTab()
	{
		CommandStat = false;
		try {
		  	ArrayList<String> tabs = new ArrayList<String> (this.driver.getWindowHandles());
		    driver.switchTo().window(tabs.get(Integer.parseInt(GetWebFieldVal)));
		    CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}	    
		return this.driver;
	}
	
	public WebDriver SwitchFrame()
	{
		//CommandStat = false;
		try 
		{
			if(GetWebFieldVal.equalsIgnoreCase("DefaultContent()")){
				driver.switchTo().defaultContent();
				CommandStat = true;
			}else {	
				if(!GetWebField.isEmpty()) {
					driver.switchTo().frame(GetElement(""));	
					CommandStat = true;
				}
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}	    
		return this.driver;
	}
	
	public WebDriver ScrollHorizontal()
	{
		CommandStat = false;
		try {
			    JavascriptExecutor js = (JavascriptExecutor) this.driver;
			  if(GetWebFieldVal.equalsIgnoreCase("Right"))
			  {
				  js.executeScript("window.scrollBy(2000,0)");
			  }else if(GetWebFieldVal.equalsIgnoreCase("Left"))
			  {
				  js.executeScript("window.scrollBy(-2000,0)");
			  }
		        
		        CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	

	public WebDriver ScrollVertical()
	{
		CommandStat = false;
		try {
		    JavascriptExecutor js = (JavascriptExecutor) this.driver;
			  if(GetWebFieldVal.equalsIgnoreCase("Top"))
			  {
				  js.executeScript("window.scrollBy(0,0)");
			  }else if(GetWebFieldVal.equalsIgnoreCase("Bottom"))
			  {
				  js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			  }
	        CommandStat = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	
	public WebDriver CompareAttributeValEqual()
	{
		CommandStat = false;
		EleText = "";
		try {
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0].toString().trim()).getAttribute(SplitGetWebField[1].toString().trim());
			this.ActualResult = EleText;	
			
			if(GetWebFieldVal.equalsIgnoreCase("Empty()"))
			{
				if(EleText.equals("") || EleText.isEmpty() || EleText.equalsIgnoreCase(null))
				{
					CommandStat = true;
				}
			}else
			{
				// Extract Database value if GetRowColVal( is observed.
				if(GetWebFieldVal.toLowerCase().contains(("getrowcolval("))){
					GetWebFieldVal = MainExt.ExtractGetRowColVal(GetWebFieldVal);
				}
				
				if(EleText.equals(GetWebFieldVal))
				{
					CommandStat = true;
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareAttributeValNotEqual()
	{
		CommandStat = false;
		EleText = "";
		try {
			
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0].toString().trim()).getAttribute(SplitGetWebField[1].toString().trim());
			this.ActualResult = EleText;
			if(!EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareAttributeValContains()
	{
		CommandStat = false;
		EleText = "";
		try {
			
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0].toString().trim()).getAttribute(SplitGetWebField[1].toString().trim());			
			this.ActualResult = EleText;
			if(EleText.contains(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver CompareAttributeValNotContains()
	{
		CommandStat = false;
		EleText = "";
		try {
			
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0].toString().trim()).getAttribute(SplitGetWebField[1].toString().trim());
			this.ActualResult = EleText;
			if(!EleText.contains(GetWebFieldVal))
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return this.driver;
	}
	
	public WebDriver LoginACNFederation()
	{
		CommandStat = false;
		try {
			if(driver.getTitle().equals("Sign In"))
			{
				String[] SplitGetWebFieldVal = GetWebFieldVal.trim().split("\\|");
				
				this.driver.findElement(By.id("userNameInput")).clear();
				this.driver.findElement(By.id("passwordInput")).clear();
				
				this.driver.findElement(By.id("userNameInput")).sendKeys(SplitGetWebFieldVal[0]);
				this.driver.findElement(By.id("passwordInput")).sendKeys(SplitGetWebFieldVal[1]);
				this.driver.findElement(By.id("submitButton")).click();
				CommandStat = true;
			}else
			{
				CommandStat = true;
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	
	public WebDriver TooltipTextEqual()
	{
		CommandStat = false;
		EleText = "";
		try {			
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0].toString().trim()).getAttribute(SplitGetWebField[1].toString().trim());
			this.ActualResult = EleText;
			if(EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
				
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
			
		return this.driver;
	}
	
	public WebDriver TooltipTextNotEqual()
	{
		CommandStat = false;
		EleText = "";
		try {
			String[] SplitGetWebField = GetWebField.trim().split("\\|");
			EleText = GetElement(SplitGetWebField[0]).getAttribute(SplitGetWebField[1]);
			this.ActualResult = EleText;
			if(!EleText.equals(GetWebFieldVal))
			{
				CommandStat = true;
			}
				
		}catch(Exception e)
		{
			e.printStackTrace();		
		}	
		return this.driver;
	}
	
	
	public WebDriver WaitUntilVisible()
	{

		EleVisible = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(GetWebFieldVal));
			if(wait.until(ExpectedConditions.visibilityOf(GetElement("")))!=null) {
				EleVisible = true;
			}

			if(EleVisible)
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	
	public WebDriver WaitUntilInVisible()
	{

		EleVisible = false;
		try {
			if(invisible(Integer.parseInt(GetWebFieldVal))) {
				EleVisible = true;
			}

			if(EleVisible)
			{
				CommandStat = true;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return this.driver;
	}
	
	public void checkPageIsReady() {
		try {
			if(driver!=null) {
				  
				  if (((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete")){ 
				   //System.out.println("Page Is loaded.");
				   return; 
				  } 

				  for (int i=0; i<30; i++){ 
					   
					  try {
					    Thread.sleep(1000);
					    }catch (InterruptedException e) {} 
					   
					   //To check page ready state.
					   if (((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete")){ 
					    break; 
					   }   
				  }
			}

		}catch(Exception e) {
			//e.printStackTrace();
		}
		
	}

	public void checkPageIsReady(WebDriver driver) {
		try {
			if(driver!=null) {
				  
				  if (((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete")){ 
				   //System.out.println("Page Is loaded.");
				   return; 
				  } 

				  for (int i=0; i<30; i++){ 
					   
					  try {
					    Thread.sleep(1000);
					    }catch (InterruptedException e) {} 
					   
					   //To check page ready state.
					   if (((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete")){ 
					    break; 
					   }   
				  }
			}

		}catch(Exception e) {
			//e.printStackTrace();
		}
		
	}


	public void JSONEqual()
	{
		//CommandStat = false;
		String Str = "";
		try {
			
			Str = PullValJSON();
			this.ActualResult = Str;
			if(!Str.equals("") || !Str.equals(null) )
			{
				if(Str.trim().equals(GetWebFieldVal))
				{
					CommandStat = true;
				}
					
			}
			
			//TimeUnit.SECONDS.sleep(2);
		}catch(Exception e)
		{
			//e.printStackTrace();			
		}
			
	}
	
	public void JSONContains()
	{
		//CommandStat = false;
		String Str = "";
		try {
			
			Str = PullValJSON();
			this.ActualResult = Str;
			if(!Str.equals("") || !Str.equals(null))
			{
				if(Str.trim().contains(GetWebFieldVal))
				{
					CommandStat = true;
				}
					
			}
			
			//TimeUnit.SECONDS.sleep(2);
		}catch(Exception e)
		{
			//e.printStackTrace();			
		}
			
	}
	
	public WebDriver JSONNotEqual()
	{
		CommandStat = false;
		String Str = "";
		try {
			Str = PullValJSON();
			this.ActualResult = Str;
			if(!Str.equals("") || !Str.equals(null))
			{

				if(!Str.trim().equalsIgnoreCase(GetWebFieldVal))
				{
					CommandStat = true;
				}
					
			}
			TimeUnit.SECONDS.sleep(2);
		}catch(Exception e)
		{
			//e.printStackTrace();			
		}
			
		return this.driver;
	}
	
	
	public void StoreXpathLocator()
	{
		EleAvail = false;
//		EleText = "";
		try
		{	

			List<WebElement> elementList = driver.findElements(By.xpath(GetWebField));			
			if(elementList.size()!=0) {
				int i = 1;
				for (WebElement we : elementList) {
					
					// LabelEqual
					if(GetWebFieldVal.toLowerCase().contains(""))
					
					// LabelContains
					
					
					
					i++;
				}
				CommandStat = true;
			}
			

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//return this.driver;
	}

	
	public String GetActualResult()
	{
		return this.ActualResult;
	}
		
	public String PullValJSON()
	{
		ServiceFactory NewServiceFactory = new ServiceFactory();
		if(GetWebField.equalsIgnoreCase("ResponseCode()")) {
			return NewServiceFactory.PullResponseCode();	
		}else {
			return NewServiceFactory.PullValJSON(this.GetWebField.trim());			
		}

	}
	
	/*
	public String GetEleText()
	{
		return this.EleText;
	}
	*/
	
	/*
	public String ExtractGetRowColVal(String RowColVal) {
		DatabaseFactory DatabaseFactoryNew = new DatabaseFactory();
		DatabaseFactoryNew.SetVal("","","",RowColVal);
		DatabaseFactoryNew.ResultValEqual();			
		return DatabaseFactoryNew.GetActualResult();
	}
	*/
	
	public WebDriver GetLatestDriverActivity()
	{
		return this.driver;
	}

	public boolean MatchItems(String Text, String EleText)
	{
		Boolean FlagContainer = true;
		Boolean FlagRequired = true;
		String[] items = Text.trim().split("\\~");
		
		if(!Text.contains("*")) {
			FlagRequired = false; // not required
		}
		
		for(String s : items) {
			if(FlagRequired) {
				// becomes true once all are found
				if(!EleText.contains(s)) {
					FlagContainer &= true;
				}else {
					FlagContainer &= false;
				}
			}else {
				// becomes true at least 1 is found
				FlagContainer = false;
				if(!EleText.contains(s)) { // 
					FlagContainer = true;
					break;
				}
			}

		}
		return FlagContainer;
	}

	public WebDriver SwitchBrowser(WebDriver SaveWD) {
		WebDriver SwitchDriver = null;
		
		try {
			
			if(GetWebFieldVal.toLowerCase().contains("chrome1")) {
				SwitchDriver = BrowserFactory.ChromeDriver1;
				CommandStat = true;
			}else if(GetWebFieldVal.toLowerCase().contains("firefox1")) {
				SwitchDriver = BrowserFactory.FireFoxDriver1;
				CommandStat = true;		
			}else if(GetWebFieldVal.toLowerCase().contains("ie1")) {
				SwitchDriver =BrowserFactory.IEDriver1;
				CommandStat = true;
			}else if(GetWebFieldVal.toLowerCase().contains("chrome")) {
				SwitchDriver = BrowserFactory.ChromeDriver;
				CommandStat = true;
			}else if(GetWebFieldVal.toLowerCase().contains("firefox")) {
				SwitchDriver = BrowserFactory.FireFoxDriver;
				CommandStat = true;
			}else if(GetWebFieldVal.toLowerCase().contains("ie")) {
				SwitchDriver =BrowserFactory.IEDriver;
				CommandStat = true;
			}		
			
			BrowserFactory.UpdatePrevBrowser(SaveWD, GetWebFieldVal.toLowerCase()); // updating the browser factory driver
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return SwitchDriver;		
	}
	
	public WebDriver ChangeURL() {
		try {
			
			if(GetWebField.contains("+")) {
				GetWebField = MainExt.Concatenation(GetWebField);
			}
			this.driver.navigate().to(GetWebField);
			CommandStat = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
	return driver;	
	}
	
	public void autosFocusElement(WebElement WebEle)
	{
		try {
			
			//if(!GetWebField.isEmpty()) {
				
				/*String ripeField = "";
				
				if(GetWebField.contains("|")) { // w/ pipe
					String[] SplitField = GetWebField.trim().split("\\|");
					ripeField = SplitField[0];
				}else {						// w/o pipe
					ripeField = GetWebField;
				}*/
				
				//((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", GetElement(ripeField));
				((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", WebEle);
				((JavascriptExecutor)driver).executeScript("window.scrollBy(0,-130)");
				//TimeUnit.MILLISECONDS.sleep(500);
			//}
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void highlightFailedElement(String Status)
	{
		try {
			if(Status.equalsIgnoreCase("fail"))
			{
				String ripeField = "";
				
				if(this.GetWebField.contains("|")) { // w/ pipe
					String[] SplitField = this.GetWebField.trim().split("\\|");
					ripeField = SplitField[0];
				}else {						// w/o pipe
					ripeField = this.GetWebField;
				}
				 ((JavascriptExecutor)driver).executeScript("arguments[0].style.border='3px solid red'", GetElement(ripeField));
			}
			
		}catch(Exception e) {
			//e.printStackTrace();
		}

	}

	public WebElement GetElement(String CustomizedWebField)
	{
		WebElement wEle = null;
		String LocalWebField = this.GetWebField;
		int LwaitTime = Integer.parseInt(waitTime); 
		
		if(!CustomizedWebField.isEmpty()) {		
			LocalWebField = CustomizedWebField;
		}
		
		
		if(AlternativeGetWebField!=null) { // check if customizedWebField is override by alternative locators defined in the field column
			LocalWebField = AlternativeGetWebField.get(0);
		}
			
		
		try {
			
			
			if (GetFindBy.equalsIgnoreCase("id"))
			{
				new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.id(LocalWebField)));
				wEle = driver.findElement(By.id(LocalWebField));
			}else if(GetFindBy.equalsIgnoreCase("xpath"))
			{	
				new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.xpath(LocalWebField)));
				wEle = driver.findElement(By.xpath(LocalWebField));
			}else if(GetFindBy.equalsIgnoreCase("partialLinkText"))
			{	
				new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(LocalWebField)));
				wEle = driver.findElement(By.partialLinkText(LocalWebField));
			}else if(GetFindBy.equalsIgnoreCase("name"))
			{	
				new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.name(LocalWebField)));
				wEle = driver.findElement(By.name(LocalWebField));
			}else if(GetFindBy.equalsIgnoreCase("classname"))
			{	
				new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.className(LocalWebField)));
				wEle = driver.findElement(By.className(LocalWebField));
			}	
			

			if(!autoFocus) {
				autosFocusElement(wEle); 
				autoFocus = true;
			}
			
			//new WebDriverWait(driver,20).until(ExpectedConditions.visibilityOf(wEle));
			//WebElement w = new WebDriverWait(driver,20).until(ExpectedConditions.presenceOfElementLocated(By.id(LocalWebField)));
			
			
		}catch(Exception e) {
			//System.out.println(e.getMessage());
				
				// if element is not visible until 20secs, check if there'a alternative otherwise do nothing
							
				if(AlternativeGetWebField!=null) {
					
					for(int a=1; a<AlternativeGetWebField.size(); a++) {
						
						LocalWebField = AlternativeGetWebField.get(a);
						if(new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.visibilityOf(GetElementForAlternative(LocalWebField)))!=null) {
							wEle = GetElementForAlternative(LocalWebField);
							// perform auto focus
							autosFocusElement(wEle);
							//reOrderFieldHashMapVal();
							break;
						}
						
					}
					
				}
			
		}

		return wEle;
	}
	
	public WebElement GetElementForAlternative(String CustomizedWebField)
	{
		WebElement wEle = null;
		String LocalWebField = this.GetWebField;
		int LwaitTime = Integer.parseInt(waitTime); 
		
		if(!CustomizedWebField.isEmpty()) {
			LocalWebField = CustomizedWebField;
		}
		
		if (GetFindBy.equalsIgnoreCase("id"))
		{
			new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.id(LocalWebField)));
			wEle = driver.findElement(By.id(LocalWebField));
		}else if(GetFindBy.equalsIgnoreCase("xpath"))
		{	
			new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.xpath(LocalWebField)));
			wEle = driver.findElement(By.xpath(LocalWebField));
		}else if(GetFindBy.equalsIgnoreCase("partialLinkText"))
		{	
			new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(LocalWebField)));
			wEle = driver.findElement(By.partialLinkText(LocalWebField));
		}else if(GetFindBy.equalsIgnoreCase("name"))
		{	
			new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.name(LocalWebField)));
			wEle = driver.findElement(By.name(LocalWebField));
		}else if(GetFindBy.equalsIgnoreCase("classname"))
		{	
			new WebDriverWait(driver,LwaitTime).until(ExpectedConditions.presenceOfElementLocated(By.className(LocalWebField)));
			wEle = driver.findElement(By.className(LocalWebField));
		}	

		return wEle;
	}
	
	
	public boolean invisible(int wait)
	{

		boolean flag = false;
		try {
			if (GetFindBy.equalsIgnoreCase("id"))
			{
				new WebDriverWait(driver,wait).until(ExpectedConditions.invisibilityOfElementLocated(By.id(this.GetWebField)));
			}else if(GetFindBy.equalsIgnoreCase("xpath"))
			{	
				new WebDriverWait(driver,wait).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(this.GetWebField)));
			}else if(GetFindBy.equalsIgnoreCase("partialLinkText"))
			{	
				new WebDriverWait(driver,wait).until(ExpectedConditions.invisibilityOfElementLocated(By.partialLinkText(this.GetWebField)));
			}else if(GetFindBy.equalsIgnoreCase("name"))
			{	
				new WebDriverWait(driver,wait).until(ExpectedConditions.invisibilityOfElementLocated(By.name(this.GetWebField)));
			}else if(GetFindBy.equalsIgnoreCase("classname"))
			{	
				new WebDriverWait(driver,wait).until(ExpectedConditions.invisibilityOfElementLocated(By.className(this.GetWebField)));
			}	
			flag = true;
		}catch(Exception e) {
			e.printStackTrace();
		}


		return flag;
	}
	
	/*
	public boolean notDisplay() {
		boolean flag = false;
		try {
			if(GetElement("")!=null) {
				for(int i=1; i<=30; i++) {

					if(!GetElement("").isDisplayed()) {
						flag = true;
						break;
					}
				TimeUnit.MILLISECONDS.sleep(1000);
				}
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return flag;
	}*/
	
	
	
	public String GetElementNumbers(String CustomizedWebField)
	{
		String EleText = "";
		String LocalWebField = this.GetWebField;
		if(!CustomizedWebField.isEmpty()) {
			LocalWebField = CustomizedWebField;
		}
		
		if (GetFindBy.equalsIgnoreCase("id"))
		{
			EleText = String.valueOf(driver.findElements(By.id(LocalWebField)).size());
			
		}else if(GetFindBy.equalsIgnoreCase("xpath"))
		{	
			EleText = String.valueOf(driver.findElements(By.xpath(LocalWebField)).size());
		}else if(GetFindBy.equalsIgnoreCase("partialLinkText"))
		{	
			EleText = String.valueOf(driver.findElements(By.partialLinkText(LocalWebField)).size());
		}else if(GetFindBy.equalsIgnoreCase("name"))
		{	
			EleText = String.valueOf(driver.findElements(By.name(LocalWebField)).size());
		}
		
		return EleText;
	}

	public void UpdateGetWebField(String NewWorkingLocator){
		
		String[] SplitGetWebField = null;
		this.GetWebField = NewWorkingLocator;
		if(GetWebField.contains("|")) { 				// if has Field|Attribute
			SplitGetWebField = GetWebField.split("\\|");
			this.GetWebField =  NewWorkingLocator + "|" + SplitGetWebField[1];
		}
		
	}
	
	public String RetryMechanismExtension(int a, String NewripeField) {
			try {
				NewripeField= AlternativeGetWebField.get(a); // replace ripeField with alternative get web field variable
				UpdateGetWebField(NewripeField);					// Update GetWebField
			}catch(Exception e) {
				//e.printStackTrace();
				NewripeField = AlternativeGetWebField.get(0);
				UpdateGetWebField(NewripeField);
			}
			//System.out.println("##########NewripeField: " + NewripeField);
		return NewripeField;
	}
	
	public void RetryMechanism()
	{
		try {
			
			if(!GetCommand.equalsIgnoreCase("CheckAvail") || !GetWebFieldVal.equalsIgnoreCase("false")) {
				String ripeField = "";
				if(!this.GetWebField.isEmpty()) {
					if(this.GetWebField.contains("|")) { // w/ pipe
						String[] SplitField = this.GetWebField.trim().split("\\|");
						ripeField = SplitField[0];
					}else {						// w/o pipe
						ripeField = this.GetWebField;
					}
					
					int a = 1;
					for(int i=0; i<12;i++) {
						//System.out.println("New ripeField: " + ripeField);
						try {
							if(GetElement(ripeField).isDisplayed()) {
								
									// if a > 1, re-order Field hash map 
									if(i>=2) {
										reOrderFieldHashMapVal();
									}
									
								break;
							}else {
								TimeUnit.SECONDS.sleep(5);
								if(i>1 && AlternativeGetWebField.size() !=0) 
								{
									ripeField = RetryMechanismExtension(a,ripeField);
									a++;
								}
							}
						}catch(Exception e) {
							TimeUnit.SECONDS.sleep(5);
							if(i>1 && AlternativeGetWebField.size() !=0) 
							{
								ripeField = RetryMechanismExtension(a,ripeField);
								a++;
							}
						}
					}
					
					if (AlternativeGetWebField.size() !=0) {
						AlternativeGetWebField.clear();
					}
					
				}
			}
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
	}

	public void freeze() throws NumberFormatException, InterruptedException {
		Matcher m = Pattern.compile("(pre|post)freeze\\(([0-9]+)\\)").matcher(GetWebFieldVal.toLowerCase());			
		if(m.find()) {
			TimeUnit.SECONDS.sleep(Integer.parseInt(m.group(2)));
		}
	}
	
	public void reOrderFieldHashMapVal() {
		
		
		try {
			String Key = MainExt.GetCurrentHashMapKey();
			String FieldVal = MainExt.ExtractKeyVal(MainExtension.Field.get(Key));
		
			if(FieldVal.contains("`")) {
				//System.out.println("MainExtension.Field.get(Key); " + FieldVal);
				String[] SplitFieldVal = FieldVal.split("\\`");
				String newFieldVal = "";
				for(int i = (SplitFieldVal.length-1); i>=0; i--) {
					
					if(i!=0) {
						newFieldVal += SplitFieldVal[i] + "`";
					}else {
						newFieldVal += SplitFieldVal[i];
					}	
				}
				
				String[] SplitKey = MainExtension.Field.get(Key).toLowerCase().split("\\(");
				MainExtension.DataRepository.put(SplitKey[1].replace(")", "").toLowerCase(),newFieldVal);
				//System.out.println("New Data repo value: " + MainExtension.DataRepository.get(SplitKey[1].replace(")", "").toLowerCase()));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
				
	}
	
	
	public boolean DragDropFile(WebElement target, int offsetX, int offsetY) {
		
		boolean flag = false;
		try {
			
			String ripePath = "";
			if(GetWebFieldVal.toLowerCase().contains("myuserdir()") && GetWebFieldVal.contains("+")) {
				ripePath = MainExt.RegExGetVal("(^[^\\\\]+\\\\[^\\\\]+\\\\[^\\\\]+)",System.getProperty("user.dir")) + MainExt.RegExGetVal("^[^\\(]+\\(myuserdir\\(\\)\\s*\\+([^\\)]+)\\)", GetWebFieldVal.toLowerCase());
			}else if(GetWebFieldVal.toLowerCase().contains("mytsdir()") && GetWebFieldVal.contains("+")) {
				
				ripePath = MainExt.GetFParentName() + "\\" + MainExt.RegExGetVal("^[^\\(]+\\(mytsdir\\(\\)\\s*\\+([^\\)]+)\\)", GetWebFieldVal.toLowerCase());
			}else {
				ripePath = GetWebFieldVal.replaceAll("^[a-z|A-Z]+\\(", "").replace(")", "");
			}
			
			File filePath = new File(ripePath);
			
		    if(filePath.exists()) {
			    WebDriver driver = ((RemoteWebElement)target).getWrappedDriver();
			    JavascriptExecutor jse = (JavascriptExecutor)driver;
			    WebDriverWait wait = new WebDriverWait(driver, 30);

			    String JS_DROP_FILE =
			        "var target = arguments[0]," +
			        "    offsetX = arguments[1]," +
			        "    offsetY = arguments[2]," +
			        "    document = target.ownerDocument || document," +
			        "    window = document.defaultView || window;" +
			        "" +
			        "var input = document.createElement('INPUT');" +
			        "input.type = 'file';" +
			        "input.style.display = 'none';" +
			        "input.onchange = function () {" +
			        "  var rect = target.getBoundingClientRect()," +
			        "      x = rect.left + (offsetX || (rect.width >> 1))," +
			        "      y = rect.top + (offsetY || (rect.height >> 1))," +
			        "      dataTransfer = { files: this.files };" +
			        "" +
			        "  ['dragenter', 'dragover', 'drop'].forEach(function (name) {" +
			        "    var evt = document.createEvent('MouseEvent');" +
			        "    evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);" +
			        "    evt.dataTransfer = dataTransfer;" +
			        "    target.dispatchEvent(evt);" +
			        "  });" +
			        "" +
			        "  setTimeout(function () { document.body.removeChild(input); }, 25);" +
			        "};" +
			        "document.body.appendChild(input);" +
			        "return input;";

			    WebElement input =  (WebElement)jse.executeScript(JS_DROP_FILE, target, offsetX, offsetY);
			    input.sendKeys(filePath.getAbsoluteFile().toString());
			    wait.until(ExpectedConditions.stalenessOf(input));
			    flag = true;
		    }
		}catch(Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	public String GetFilePath() {
		String filePath = "";
		String rawPath = "";
		
		if(!GetWebField.isEmpty()) {
			
			
			if(GetWebField.contains("+")) {
				// logical path
				
				String[] splitGetWebField = GetWebField.split("\\+");
				
				//MyUserDir()+\downloads\myfile.xlsx
				//MyUserDir()+\downloads\+(/html/body/ngb-modal-window/div/div/div[3]/input[2])
				
				for(String s:splitGetWebField) {
					
					if(s.toLowerCase().trim().equalsIgnoreCase("myuserdir()")) {
						rawPath+= MainExt.RegExGetVal("(^[^\\\\]+\\\\[^\\\\]+\\\\[^\\\\]+)",System.getProperty("user.dir"));
					}else if(s.toLowerCase().trim().equalsIgnoreCase("mytsdir()")) {
						rawPath+= MainExt.GetFParentName() + "\\";
					}else if(!MainExt.RegExGetVal("^\\(([^\r\n]+)\\)", s.trim()).isEmpty()) { // 
						rawPath+= GetElement(MainExt.RegExGetVal("^\\(([^\r\n]+)\\)", s.trim())).getText(); // getting element text value	
					}else{
						rawPath+=s.trim();
					}
						
				}
					
				filePath = rawPath;
			}else {
				// absolute path
				filePath = GetWebField.trim();
			}
			
		}
		
		return filePath;
	}
	
    public void CopyClipboard(String str) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(str);
		clipboard.setContents(strSel, null);
    }
	
	public boolean OpenFileDialog() {
		boolean flag = false;
		try {
			Robot robot = new Robot();
			robot.setAutoDelay(3000);
	        
			String ripePath = "";
			
			if(GetWebFieldVal.toLowerCase().contains("myuserdir()") && GetWebFieldVal.contains("+")) {
				
				ripePath = MainExt.RegExGetVal("(^[^\\\\]+\\\\[^\\\\]+\\\\[^\\\\]+)",System.getProperty("user.dir")) + MainExt.RegExGetVal("^[^\\(]+\\(myuserdir\\(\\)\\s*\\+([^\\)]+)\\)", GetWebFieldVal.toLowerCase());
			}else if(GetWebFieldVal.toLowerCase().contains("mytsdir()") && GetWebFieldVal.contains("+")) {
					
				ripePath = MainExt.GetFParentName() + "\\" + MainExt.RegExGetVal("^[^\\(]+\\(mytsdir\\(\\)\\s*\\+([^\\)]+)\\)", GetWebFieldVal.toLowerCase());
			}else {
				ripePath = GetWebFieldVal.replaceAll("^[a-z|A-Z]+\\(", "").replace(")", "");
			}
			
			File filePath = new File(ripePath);
			
			if(filePath.exists()) {
				
		        StringSelection URI = new StringSelection(ripePath);
		        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(URI, null);
		        
		        robot.setAutoDelay(3000);
		        
		        robot.keyPress(KeyEvent.VK_CONTROL);
		        robot.keyPress(KeyEvent.VK_V);
		        robot.keyRelease(KeyEvent.VK_CONTROL);
		        robot.keyRelease(KeyEvent.VK_V);    
		        
		        robot.setAutoDelay(3000);
		        
		        robot.keyPress(KeyEvent.VK_ALT);
		        robot.keyPress(KeyEvent.VK_O);
		        robot.keyRelease(KeyEvent.VK_ALT);
		        robot.keyRelease(KeyEvent.VK_O);
		        flag = true;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
    public void keyPress(Robot robot, String key, int iteratePress) throws AWTException {
        for(int i=0;i<iteratePress;i++){

            switch(key.toLowerCase()){

                //region KEYS
                case "enter":
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                    break;
                case "alt":
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case "ctrl":
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                    break;
                case "shift":
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case "home":
                    robot.keyPress(KeyEvent.VK_HOME);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_HOME);
                    break;
                case "end":
                    robot.keyPress(KeyEvent.VK_END);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_END);
                    break;
                case "page_up":
                    robot.keyPress(KeyEvent.VK_PAGE_UP);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_PAGE_UP);
                    break;
                case "page_down":
                    robot.keyPress(KeyEvent.VK_PAGE_DOWN);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
                    break;
                case "arrow_up":
                    robot.keyPress(KeyEvent.VK_UP);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_UP);
                    break;
                case "arrow_down":
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                    break;
                case "arrow_right":/*
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_RIGHT);*/
                	
                    break;
                case "escape":
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_ESCAPE);
                    break;
                case "f1":
                    robot.keyPress(KeyEvent.VK_F1);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_F1);
                    break;
                case "f2":
                    robot.keyPress(KeyEvent.VK_F2);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_F2);
                    break;
                case "f3":
                    robot.keyPress(KeyEvent.VK_F3);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_F3);
                    break;
                case "f4":
                    robot.keyPress(KeyEvent.VK_F4);
                    robot.setAutoDelay(1000);
                    robot.keyRelease(KeyEvent.VK_F4);
                    break;
                //endregion
            }
        }
    }
}