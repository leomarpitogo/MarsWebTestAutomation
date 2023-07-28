package MarsWTA;

import static org.junit.Assert.*;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class MainExtension { 
	public static Boolean isUIMode;
	static ExtentTest test;
	static ExtentReports report;
	static WebDriver MainDriver;
	static String FName;
	static String FParentName;
	static String FileName;
	static String Stat;
	static Boolean SummaryStat;
	static String SDate;
	static String EDate;
	static Boolean SKIPNextFlag = false;
	static Boolean StatusIsSkip = false;
	static String ErrorMsg = "";
	static String ErrorMsgPerRow = "";
	static Boolean ErrFree = true;
	static Boolean EnableRepeatStepsEx = false;
	static String CurrentHashMapKey;
	static String ScreenShotCurrentDir;
	static String ExtentReportPath;
	String ActualResult = "";
	String ExternalFile = "";
	static HashMap<String, String> Description = new HashMap<String, String>();	
	static HashMap<String, String> TestId = new HashMap<String, String>();
	static HashMap<String, String> Command = new HashMap<String, String>();
	static HashMap<String, String> Text = new HashMap<String, String>();
	static HashMap<String, String> FindBy = new HashMap<String, String>();
	static HashMap<String, String> Field = new HashMap<String, String>();
	static HashMap<String, String> Skip = new HashMap<String, String>();
	static HashMap<String, String> SkipNextIfPass = new HashMap<String, String>();
	static HashMap<String, String> StopAllIfFail = new HashMap<String, String>();
	static HashMap<String, String> DescriptionEx = new HashMap<String, String>();	
	static HashMap<String, String> TestIdEx = new HashMap<String, String>();
	static HashMap<String, String> CommandEx = new HashMap<String, String>();
	static HashMap<String, String> TextEx = new HashMap<String, String>();
	static HashMap<String, String> FindByEx = new HashMap<String, String>();
	static HashMap<String, String> FieldEx = new HashMap<String, String>();
	static HashMap<String, String> SkipEx = new HashMap<String, String>();
	static HashMap<String, String> SkipNextIfPassEx = new HashMap<String, String>();
	static HashMap<String, String> StopAllIfFailEx = new HashMap<String, String>();
	static HashMap<String, String> RegStatus = new HashMap<String, String>();
	static HashMap<String, String> SumStatus = new HashMap<String, String>();
	static HashMap<String, String> SumTestId = new HashMap<String, String>();
	static HashMap<String, String> TestConditions = new HashMap<String, String>();
	static HashMap<String, String> Start = new HashMap<String, String>();
	static HashMap<String, String> End = new HashMap<String, String>();
	static HashMap<String, String> Duration = new HashMap<String, String>();
	static HashMap<String, String> DataRepository = new HashMap<String, String>();
	static HashMap<String, String> DataRepositoryEx = new HashMap<String, String>();
	static HashMap<String, String> TempText = new HashMap<String, String>();
	static HashMap<String, String> TempField = new HashMap<String, String>();
    //Main MainNew = new Main();
   // ThreadFactory threadfactory = new ThreadFactory();
    
	public void RunValidation() throws IOException, InterruptedException
	{

		String LFName = GetFName();
		SetErrorMessage("Empty()");
		SetErrorFreeFlag(true);
		ErrFree = true;
		
		System.out.println("LFName: " + LFName);
		if(CheckExcelAvail(LFName))
		{
			if(!CheckFileIsOpen(LFName))
			{
				CreateLogDirectory();
				CheckRequiredColumns(LFName);
				CheckValidValuePerCommand(LFName);
				if(GetErrorFreeFlag())
				{
					ClearStatusDateTimeRegression();
					ClearStatusDateTimeTestConditions();
					CreateScreenshortDir();
					ImportRepository(LFName, false, "Data Source");
					ImportExcelData(LFName, false, "Regression");
					
					if(getUIMode()) {
						SetErrorMessage("# Starting... \n");
					}else {
						System.out.println("# Starting... \n");
					}
					ReportINI();
					
				}else
				{
					if(!getUIMode()) {
						System.out.println(GetErrorMessage());
						WriteLog(GetErrorMessage());
						OpenExcel();
						//System.exit(0);	
					}
					
				}	
				
			}else
			{
				if(!getUIMode()) {
					System.out.println("# Input Excel is currently opened, please close the file and execute again. \n");
					//System.exit(0);					
				}else {
					SetErrorMessage("# Input Excel is currently opened, please close the file and execute again. \n");
				}
		
			}	

			
		}else
		{
			if(!getUIMode()) {
				System.out.println("# Application cannot find " + LFName);
				//System.exit(0);
			}else {
				SetErrorMessage("# Application cannot find " + LFName);
			}

		}
	}
	
	public void SetFName(String NameF, String ParentNameF, String FileNameF)
	{
		FName = NameF;
		FParentName = ParentNameF;
		FileName = FileNameF;
	}
	
	public String GetFName()
	{
		return FName;
	}
	
	public String GetFParentName()
	{
		return FParentName;
	}
	
	public static void SetUIMode(boolean Flag) {
		isUIMode = Flag;
	}
	
	public static boolean getUIMode() {
		return isUIMode;
	}
	
	public void SetCurrentHashMapKey(String key) {
		CurrentHashMapKey = key;
	}
	
	public String GetCurrentHashMapKey() {
		return CurrentHashMapKey;
	}
		
	public String RepeatExternalSteps(String WebFieldVal, String RepeatExternalStepsRowNumber) {
		
		Boolean RStatus = true;
		//String InternalKey = String.valueOf(Integer.parseInt(RepeatExternalStepsRowNumber) - 1);
		String InternalKey = RepeatExternalStepsRowNumber;
		try {
			
			String ExternalFileName = "";
			if(Field.get(InternalKey).contains("<")) {
				ExternalFileName = Field.get(InternalKey).replace("<", "").replace(">", "");
			}else {
				ExternalFileName =FParentName + "\\" + Field.get(InternalKey);
			}
			
			
			if(ExternalFile.isEmpty()) {
				ImportExcelData(ExternalFileName, true, "Regression"); // New: importing external data	
				ImportRepository(ExternalFileName, true, "Data Source");
			}else {
				if(!ExternalFile.equalsIgnoreCase(Field.get(InternalKey))) { // Renew: importing external data	
					DescriptionEx.clear();  
					TestIdEx.clear();
					CommandEx.clear();							
					TextEx.clear();
					FieldEx.clear();
					FindByEx.clear();
					SkipEx.clear();
					SkipNextIfPassEx.clear();
					StopAllIfFailEx.clear();
					DataRepositoryEx.clear();
					ImportExcelData(ExternalFileName, true, "Regression"); 
					ImportRepository(ExternalFileName, true, "Data Source");
				}
			}
		}catch(Exception e) {
			RStatus = false;
			e.printStackTrace();
		}

			if(RStatus) { // if above importing is successfully, otherwise fail
				String[] Steps = null;
				String StepList = "";
				
				if(WebFieldVal.contains("-")) { // range
					String FromRange[] = WebFieldVal.trim().split("-"); 
					for(int i=Integer.parseInt(FromRange[0]); i<=Integer.parseInt(FromRange[1]); i++)
						{
							StepList += String.valueOf(i) + ",";
						}
				}
				
				if(StepList.isEmpty()) {
					Steps = WebFieldVal.trim().split(","); // single convert to series for looping
				}else {
					Steps = StepList.trim().split(","); // range convert to series for looping
				}
				
				String key = "", FieldKey = "", TextKey = "", SkipKey = "N";

				Print("StartRepeatExternal","","","","","","","","",""); // start of repeat steps
				EnableRepeatStepsEx = true;	// to enable RepeatStepsEx method
				for(String s : Steps)
				{

					try {
					 	key = String.valueOf(Integer.parseInt(s) - 1); // convert row number to hash map key
					 	SetCurrentHashMapKey(key);
					 	
				        TextKey = ExtractKeyValEx(TextEx.get(key));
				        FieldKey = ExtractKeyValEx(FieldEx.get(key));
				      
				        Print("Details",s,DescriptionEx.get(key),CommandEx.get(key),TextKey,FindByEx.get(key),FieldKey,SkipKey,SkipNextIfPassEx.get(key),StopAllIfFailEx.get(key));
				        ExecuteCommand(CommandEx.get(key),TextKey,FindByEx.get(key),FieldKey,SkipKey, SkipNextIfPassEx.get(key),key);
				        Print("Status","","","","","","","","","");
				        RStatus &= ConvStatToBool(Stat);
				       
				        //TimeUnit.SECONDS.sleep(1);
				        TakeScreenShot(ScreenShotCurrentDir + "\\" + RepeatExternalStepsRowNumber + "_RepeatExternal_" + s + "_" + TestIdEx.get(key) + "_" +  Stat + ".jpg");
				        //TimeUnit.SECONDS.sleep(1);

					}catch(Exception e) {
						//e.printStackTrace();
					}

				}
				EnableRepeatStepsEx = false;
				Print("EndRepeatExternal","","","","","","","","","");
				
			}

		
		if(RStatus)
		{
			return "Pass";
		}else {
			return "Fail";
		}
	}
	
	
	public String RepeatInternalSteps(String WebFieldVal, String RepeatExternalStepsRowNumber) {
		
		Boolean RStatus = true;
		//String InternalKey = String.valueOf(Integer.parseInt(RepeatExternalStepsRowNumber) - 1);
		String InternalKey = RepeatExternalStepsRowNumber;
		try {
			
			// Sheets(regression1, Data Source1)
			String regSheetName, dataSourceSheetName;
			
			String[] splitSheet = Field.get(InternalKey).toLowerCase().replace("sheets(", "").replace(")", "").split(",");
			regSheetName = splitSheet[0].trim();
			dataSourceSheetName = splitSheet[1].trim();
			
			
			if(ExternalFile.isEmpty()) {
				ImportExcelData(GetFName(), true, regSheetName); // New: importing external data	
				ImportRepository(GetFName(), true, dataSourceSheetName);
			}else {
				if(!ExternalFile.equalsIgnoreCase(Field.get(InternalKey))) { // Renew: importing external data
					DescriptionEx.clear();  
					TestIdEx.clear();
					CommandEx.clear();							
					TextEx.clear();
					FieldEx.clear();
					FindByEx.clear();
					SkipEx.clear();
					SkipNextIfPassEx.clear();
					StopAllIfFailEx.clear();
					DataRepositoryEx.clear();
										
					ImportExcelData(GetFName(), true, regSheetName); // New: importing external data	
					ImportRepository(GetFName(), true, dataSourceSheetName);
				}
			}
			
			
		}catch(Exception e) {
			RStatus = false;
			e.printStackTrace();
		}

			if(RStatus) { // if above importing is successfully, otherwise fail
				String[] Steps = null;
				String StepList = "";
				
				if(WebFieldVal.contains("-")) { // range
					String FromRange[] = WebFieldVal.trim().split("-"); 
					for(int i=Integer.parseInt(FromRange[0]); i<=Integer.parseInt(FromRange[1]); i++)
						{
							StepList += String.valueOf(i) + ",";
						}
				}
				
				if(StepList.isEmpty()) {
					Steps = WebFieldVal.trim().split(","); // single convert to series for looping
				}else {
					Steps = StepList.trim().split(","); // range convert to series for looping
				}
				
				String key = "", FieldKey = "", TextKey = "", SkipKey = "N";

		        
				Print("StartRepeatExternal","","","","","","","","",""); // start of repeat steps
				EnableRepeatStepsEx = true;	// to enable RepeatStepsEx method
				for(String s : Steps)
				{

					try {
					 	key = String.valueOf(Integer.parseInt(s) - 1); // convert row number to hash map key
					 	SetCurrentHashMapKey(key);
					 	
				        TextKey = ExtractKeyValEx(TextEx.get(key));
				        FieldKey = ExtractKeyValEx(FieldEx.get(key));
				      
				        Print("Details",s,DescriptionEx.get(key),CommandEx.get(key),TextKey,FindByEx.get(key),FieldKey,SkipKey,SkipNextIfPassEx.get(key),StopAllIfFailEx.get(key));
				        ExecuteCommand(CommandEx.get(key),TextKey,FindByEx.get(key),FieldKey,SkipKey, SkipNextIfPassEx.get(key),key);
				        Print("Status","","","","","","","","","");
				        RStatus &= ConvStatToBool(Stat);
				       
				        //TimeUnit.SECONDS.sleep(1);
				        TakeScreenShot(ScreenShotCurrentDir + "\\" + RepeatExternalStepsRowNumber + "_RepeatExternal_" + s + "_" + TestIdEx.get(key) + "_" +  Stat + ".jpg");
				        //TimeUnit.SECONDS.sleep(1);

					}catch(Exception e) {
						//e.printStackTrace();
					}

				}
				Print("EndRepeatExternal","","","","","","","","","");
				EnableRepeatStepsEx = false;
			}

		
		if(RStatus)
		{
			return "Pass";
		}else {
			return "Fail";
		}
	}

	
	public void ExecuteCommand(String Command, String WebFieldVal,String WebFindBy, String WebField, String Skip, String SkipNextIfPass, String Key)
	{
			Stat = "Fail";
			ObjectFactory ObjectFactoryNew = PageFactory.initElements(MainDriver,ObjectFactory.class);
			ServiceFactory ServiceFactoryNew = new ServiceFactory(); 
			DatabaseFactory DatabaseFactoryNew = new DatabaseFactory();
			
			this.ActualResult = "N/A";
			
			if(Command.equalsIgnoreCase("RepeatSteps"))
			{
				
				if(Skip.equalsIgnoreCase("N")){
					
					String RSkipNextIfPass = SkipNextIfPass;
					if(EnableRepeatStepsEx) {
						Stat = RepeatStepsEx(WebFieldVal,Key);
					}else {
						Stat = RepeatSteps(WebFieldVal,Key);
					}
						
					//this.ActualResult = "N/A";
					
					if(RSkipNextIfPass.equalsIgnoreCase("Y") && ConvStatToBool(Stat)==true)
					{
						SetSKIPNextFlag(true); // trigger next step to SKIP
					}
					
				}else if(Skip.equalsIgnoreCase("Y")){	
					Stat = "N/A";
				}else{
					Stat = "Unknown";
				}
				
				
			}else if(Command.equalsIgnoreCase("RepeatExternalSteps")){
				
				if(Skip.equalsIgnoreCase("N")){
					
					String RSkipNextIfPass = SkipNextIfPass;
					//Stat = RepeatExternalSteps(WebFieldVal,String.valueOf(Integer.parseInt(Key)+1));
					Stat = RepeatExternalSteps(WebFieldVal,Key);
					//this.ActualResult = "N/A";
					
					if(RSkipNextIfPass.equalsIgnoreCase("Y") && ConvStatToBool(Stat)==true)
					{
						SetSKIPNextFlag(true); // trigger next step to SKIP
					}
					
				}else if(Skip.equalsIgnoreCase("Y")){	
					Stat = "N/A";
				}else{
					Stat = "Unknown";
				}
				
			}else if(Command.equalsIgnoreCase("RepeatInternalSteps")){
				
				if(Skip.equalsIgnoreCase("N")){
					
					String RSkipNextIfPass = SkipNextIfPass;
					//Stat = RepeatExternalSteps(WebFieldVal,String.valueOf(Integer.parseInt(Key)+1));
					Stat = RepeatInternalSteps(WebFieldVal,Key);
					//this.ActualResult = "N/A";
					
					if(RSkipNextIfPass.equalsIgnoreCase("Y") && ConvStatToBool(Stat)==true)
					{
						SetSKIPNextFlag(true); // trigger next step to SKIP
					}
					
				}else if(Skip.equalsIgnoreCase("Y")){	
					Stat = "N/A";
				}else{
					Stat = "Unknown";
				}
				
			}else { // None RepeatSteps
				
				if(Skip.equalsIgnoreCase("N")){
					ObjectFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
					if (Command.equalsIgnoreCase("SetText"))
					{

						MainDriver = ObjectFactoryNew.SetText();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("CheckWebAccessibility")) 
					{
						
						MainDriver = ObjectFactoryNew.CheckWebAccessibility();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("SetTextEnter"))
					{
						
						MainDriver = ObjectFactoryNew.SetTextEnter();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("Click"))
					{

						MainDriver = ObjectFactoryNew.ClickButton();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("DoubleClick"))
					{

						MainDriver = ObjectFactoryNew.DoubleClick();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("RightClick"))
					{

						MainDriver = ObjectFactoryNew.RightClick();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("CheckAvail"))
					{

						MainDriver = ObjectFactoryNew.CheckAvail();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("TextEqual"))
					{

						MainDriver = ObjectFactoryNew.CompareTextEqual();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("TextNotEqual"))
					{

						MainDriver = ObjectFactoryNew.CompareTextNotEqual();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("TextContains"))
					{

						MainDriver = ObjectFactoryNew.CompareTextContains();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("TextNotContains"))
					{

						MainDriver = ObjectFactoryNew.CompareTextNotContains();
						Stat = ObjectFactoryNew.GetCommandStat();								
					}else if(Command.equalsIgnoreCase("Select"))
					{

						MainDriver = ObjectFactoryNew.SelectByIndex();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("CheckSelect"))
					{

						MainDriver = ObjectFactoryNew.CheckSelect();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("CheckEnable"))
					{

						MainDriver = ObjectFactoryNew.CheckEnable();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("CheckEnableDropDownItem"))
					{

						MainDriver = ObjectFactoryNew.CheckEnableDropDownItem();
						Stat = ObjectFactoryNew.GetCommandStat();					
					}else if(Command.equalsIgnoreCase("AlertTextEqual"))
					{

						MainDriver = ObjectFactoryNew.AlertTextEqual();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("WaitUntilVisible"))
					{

						MainDriver = ObjectFactoryNew.WaitUntilVisible();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("WaitUntilInVisible"))
					{

						MainDriver = ObjectFactoryNew.WaitUntilInVisible();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("TitleEqual"))
					{

						MainDriver = ObjectFactoryNew.TitleEqual();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("SetVerSecurityCode"))
					{

						MainDriver = ObjectFactoryNew.SetVerSecurityCode();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("CheckAllImagesAvail"))
					{

						MainDriver = ObjectFactoryNew.CheckAllImagesAvailability();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("Open"))
					{
						if(WebFieldVal.toLowerCase().contains("switchbrowser(")) {
	
							MainDriver = ObjectFactoryNew.SwitchBrowser(MainDriver);
							Stat = ObjectFactoryNew.GetCommandStat();
						}else if(WebFieldVal.equalsIgnoreCase("changeurl()")){
	
							MainDriver = ObjectFactoryNew.ChangeURL();
							Stat = ObjectFactoryNew.GetCommandStat();							
						}else {
							WebDriver driver = BrowserFactory.StartBrowser(WebFieldVal,WebField,MainDriver);
							MainDriver = driver;
							Stat = "Pass";
							ObjectFactoryNew.SetVal("", "", "", "");	
						}
						
					}else if(Command.equalsIgnoreCase("NewTab"))
					{		

						MainDriver = ObjectFactoryNew.NewTab();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("Hover"))
					{

						MainDriver = ObjectFactoryNew.Hover();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("HoverClick"))
					{

						MainDriver = ObjectFactoryNew.HoverClick();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("TooltipTextNotEqual"))
					{

						MainDriver = ObjectFactoryNew.TooltipTextNotEqual();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("TooltipTextEqual"))
					{

						MainDriver = ObjectFactoryNew.TooltipTextEqual();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("StoreValToken(Service)"))
					{
						ServiceFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
						ServiceFactoryNew.StoreValTokenService();
						Stat = ServiceFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("StoreValJSON(Service)"))
					{
						ServiceFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
						ServiceFactoryNew.StoreValJSONService();
						Stat = ServiceFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("JSONEqual"))
					{

						ObjectFactoryNew.JSONEqual();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("JSONNotEqual"))
					{

						MainDriver = ObjectFactoryNew.JSONNotEqual();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("JSONContains"))
					{

						ObjectFactoryNew.JSONContains();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("SwitchTab"))
					{

						MainDriver = ObjectFactoryNew.SwitchTab();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("SwitchFrame"))
					{

						MainDriver =  ObjectFactoryNew.SwitchFrame();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("LoginACNFederation"))
					{

						MainDriver = ObjectFactoryNew.LoginACNFederation();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("ScrollVertical"))
					{

						MainDriver = ObjectFactoryNew.ScrollVertical();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("ScrollHorizontal"))
					{

						MainDriver = ObjectFactoryNew.ScrollHorizontal();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("AttributeValEqual"))
					{

						MainDriver = ObjectFactoryNew.CompareAttributeValEqual();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("AttributeValNotEqual"))
					{

						MainDriver = ObjectFactoryNew.CompareAttributeValNotEqual();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("RichSetText")) // for further analysis
					{

						MainDriver = ObjectFactoryNew.RichSetText();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("CSSValEqual"))
					{

						MainDriver = ObjectFactoryNew.CSSValEqual();
						Stat = ObjectFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("CSSValNotEqual"))
					{

						MainDriver = ObjectFactoryNew.CSSValNotEqual();
						Stat = ObjectFactoryNew.GetCommandStat();			
					}else if(Command.equalsIgnoreCase("CSSValContains"))
					{

						MainDriver = ObjectFactoryNew.CSSValContains();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("NavigatePage"))
					{

						MainDriver = ObjectFactoryNew.NavigatePage();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("LabelEqual"))
					{

						MainDriver = ObjectFactoryNew.CompareLabelEqual();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("LabelNotEqual"))
					{

						MainDriver = ObjectFactoryNew.CompareLabelNotEqual();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("LabelContains"))
					{

						MainDriver = ObjectFactoryNew.CompareLabelContains();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("LabelNotContains"))
					{

						MainDriver = ObjectFactoryNew.CompareLabelNotContains();
						Stat = ObjectFactoryNew.GetCommandStat();		
					}else if(Command.equalsIgnoreCase("CheckDropdownVal"))
					{

						MainDriver = ObjectFactoryNew.CheckDropdownVal();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("SetKey"))
					{

						MainDriver = ObjectFactoryNew.SetKey();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else if(Command.equalsIgnoreCase("DPSelectDay"))
					{

						MainDriver = ObjectFactoryNew.DPSelectDay();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("PageContains"))
					{

						MainDriver = ObjectFactoryNew.PageContains();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("SetAttribute"))
					{

						MainDriver = ObjectFactoryNew.SetAttribute();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("Freeze"))
					{

						MainDriver = ObjectFactoryNew.Freeze();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("CheckNumberOfElements"))
					{

						MainDriver = ObjectFactoryNew.CheckNumberOfElements();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("SetLabel"))
					{

						MainDriver = ObjectFactoryNew.SetLabel();
						Stat = ObjectFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("CheckDuration"))
					{
						Stat = CheckDuration(Command,WebFieldVal,WebField,Key);				
					}else if(Command.equalsIgnoreCase("ConnectSQLServer(Database)"))
					{
						DatabaseFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
						DatabaseFactoryNew.ConnectSQLServer();
						Stat = DatabaseFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("StoreValResult(Database)"))
					{
						DatabaseFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
						DatabaseFactoryNew.StoreValResult();
						Stat = DatabaseFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("ResultValEqual"))
					{
						DatabaseFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
						DatabaseFactoryNew.ResultValEqual();
						Stat = DatabaseFactoryNew.GetCommandStat();				
					}else if(Command.equalsIgnoreCase("ResultValContains"))
					{
						DatabaseFactoryNew.SetVal(Command,WebFieldVal,WebFindBy,WebField);
						DatabaseFactoryNew.ResultValContains();
						Stat = DatabaseFactoryNew.GetCommandStat();	
					}else if(Command.equalsIgnoreCase("CheckFile"))
					{

						MainDriver = ObjectFactoryNew.CheckFile();
						Stat = ObjectFactoryNew.GetCommandStat();
					}else {
						Stat = "Unknown";
					}
			
					if(Pattern.matches("(storevaltoken\\(service\\)|storevaljson\\(service\\))", Command.toLowerCase())) {
						this.ActualResult = ServiceFactoryNew.GetActualResult();
					}else if(Pattern.matches("(connectsqlserver\\(database\\)|storevalresult\\(database\\)|resultvalequal|resultvalcontains)", Command.toLowerCase())){
						if(DatabaseFactoryNew.GetActualResult() != null) {
							this.ActualResult = DatabaseFactoryNew.GetActualResult();	
						}
					}else {
						if(ObjectFactoryNew.GetActualResult() != null) {
							this.ActualResult = ObjectFactoryNew.GetActualResult();	
						}
					}
					
					if(SkipNextIfPass.equalsIgnoreCase("Y") && ConvStatToBool(Stat)==true)
					{
						SetSKIPNextFlag(true); // trigger next step to SKIP
					}

					
					if(UIMode.isHighlightElement) {
						ObjectFactoryNew.highlightFailedElement(Stat);
					}
					
					//waiting until page is ready
					ObjectFactory OF = new ObjectFactory(null);
					OF.checkPageIsReady(MainDriver);
					
				}else if(Skip.equalsIgnoreCase("Y")){	
					Stat = "SKIP";
				}else{
					Stat = "Unknown";
				}	
					
			}
			
	}
	
	public String CheckDuration(String Command, String WebFieldVal, String WebField, String Key) {
		String LStat = "Pass";
		try {
			String[] SplitWebField = null;
			Double dueResult = 00.000;
			if (WebField.contains("-")) { // range type
				
				SplitWebField = WebField.split("\\-");	
				for(int i = Integer.parseInt(SplitWebField[0])-1; i<= Integer.parseInt(SplitWebField[1])-1; i++){
					dueResult+= Double.parseDouble(Duration.get(String.valueOf(i)));
				}
				
			}else if(WebField.contains(",")){ // series type
				
				SplitWebField = WebField.split("\\,");	
				for(String getKey : SplitWebField){
					dueResult+= Double.parseDouble(Duration.get(String.valueOf(Integer.parseInt(getKey)-1)));
				}
				
			}else { // single type
				dueResult = Double.parseDouble(Duration.get(String.valueOf(Integer.parseInt(WebField)-1)));
			}
			
			// if command is click & text column is not empty & browser factory is enabled & click status is pass then perform assertion.
			this.ActualResult = String.valueOf(dueResult);
			if(dueResult > Double.parseDouble(WebFieldVal)) {
				LStat = "Fail";
			}
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
		return LStat;
	}
	
	public void SetDuration(String Key) {	
		
		try {
			// put duration
			String[] timeStart = Start.get(Key).split(" ");
			String[] timeEnd = End.get(Key).split(" ");
			SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss");
			Date dtStart = SDF.parse(timeStart[1]);
			Date dtEnd  = SDF.parse(timeEnd[1]);
			String[] getTimeStartMS = timeStart[1].split(":");
			String[] getTimeEndMS = timeEnd[1].split(":");
			Long diffMS = Long.parseLong(getTimeEndMS[3])  - Long.parseLong(getTimeStartMS[3]); // get MS difference
			Long diff = (dtEnd.getTime() - dtStart.getTime())/1000; // get Seconds difference
			Double diffresult = (double)(diffMS + (diff * 1000))/1000; // sum up MS difference + converted Seconds to MS and convert back to Seconds
			Duration.put(Key, String.valueOf(diffresult));
				
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	public String ExtractGetRowColVal(String RowColVal) {
		DatabaseFactory DatabaseFactoryNew = new DatabaseFactory();
		DatabaseFactoryNew.SetVal("","","",RowColVal);
		DatabaseFactoryNew.ResultValEqual();			
		return DatabaseFactoryNew.GetActualResult();
	}
	
	public String RegExGetVal(String find, String text) {
		String refineVal = "";
		Matcher p = Pattern.compile(find).matcher(text);
		
		if(p.find()) {
			refineVal = p.group(1).toString();
		}
		
		return refineVal;
	}
	
	public String Concatenation(String raw) { // not being use. for further analysis
			String ripe = "";
			
			if(raw.contains("+")) { // with + sign
				String[] splitRaw = raw.split("\\+");
				
				for(String s:splitRaw) {	
					
					
					if(s.toLowerCase().trim().contains("getrowcolval(")) {
						
						ripe+= ExtractGetRowColVal(s.trim());
						
					}else if(s.toLowerCase().trim().contains("myuserdir()")) {
						
						ripe+=RegExGetVal("(^[^\\\\]+\\\\[^\\\\]+\\\\[^\\\\]+)",System.getProperty("user.dir"));
	
					}else if(s.toLowerCase().trim().contains("mycurrentexcel()")) {
						
						ripe+=GetFName();
						
					}else { 
						// no special method, purely concatenate
						ripe+=s.trim();
					}
				}
				
				
			}else {// no + sign
				
				if(raw.toLowerCase().trim().contains("getrowcolval(")) {
					
					ripe+= ExtractGetRowColVal(raw.trim());
					
				}else if(raw.toLowerCase().trim().contains("myuserdir()")) {
					
					ripe+=RegExGetVal("(^[^\\\\]+\\\\[^\\\\]+\\\\[^\\\\]+)",System.getProperty("user.dir"));
					
				}else if(raw.toLowerCase().trim().contains("mycurrentexcel()")) {
					
					ripe+=GetFName();
						
				}else { 
					// no special method, purely concatenate
					ripe+=raw.trim();
				}
				
			}

			
		return ripe;
	}
	
	public String ExtractKeyVal(String RawValue)
	{		
		String ExtractRawVal = RawValue;
		String ExtractedVal0 = "";
		String ExtractedVal1 = "";
		String ExtractedValNoPipe = "";
		
		try {
			
			//concatenation
			
			if(RawValue.toLowerCase().contains("key("))
			{
				
				if(RawValue.contains("|") || RawValue.contains(" + ")) {
					
					// perform split
					String SplitRawValue[] = null;
					if(RawValue.contains("|")) {
						SplitRawValue = RawValue.split("\\|");	
					}else {
						SplitRawValue = RawValue.split(" \\+ ");
					}
					
					
					// check if there's a key( for index = 0
					if(SplitRawValue[0].toLowerCase().contains("key("))
					{
						// split index if there's a key(
						String[] SplitKey = SplitRawValue[0].toLowerCase().trim().split("\\(");
						ExtractedVal0 = DataRepository.get(SplitKey[1].trim().replace(")", ""));
					}else {
						// get the raw value(0) if NO key( found
						ExtractedVal0 = SplitRawValue[0];
					}
					//System.out.println("ExtractedVal0" + ExtractedVal0);
					
					// check if there's a key( for index = 1
					if(SplitRawValue[1].toLowerCase().contains("key("))
					{
						// split index if there's a key(
						String[] SplitKey = SplitRawValue[1].toLowerCase().trim().split("\\(");
						ExtractedVal1 = DataRepository.get(SplitKey[1].trim().replace(")", ""));
					}else {
						// get the raw value(1) if NO key( found
						ExtractedVal1 = SplitRawValue[1];
					}
					
					//System.out.println("ExtractedVal1" + ExtractedVal1);
				}else {
					// with key( w/o pipe or w/o plus
					String[] SplitKey = RawValue.toLowerCase().split("\\(");
					ExtractedValNoPipe = DataRepository.get(SplitKey[1].replace(")", "").toLowerCase());
				}	
				
				// consolidate extracted key values
				if(ExtractedValNoPipe.isEmpty()) {
					if(RawValue.contains("|")) {
						ExtractRawVal = ExtractedVal0 + "|" + ExtractedVal1;
					}else {
						ExtractRawVal = ExtractedVal0 + ExtractedVal1;
					}
						
				}else {
					ExtractRawVal = ExtractedValNoPipe;
				}
				
			}
		}catch(Exception e)
		{
			//e.printStackTrace();	
			return ExtractRawVal;
		}
		
		return ExtractRawVal;
	}
	
	public String ExtractKeyValEx(String RawValue)
	{
		String ExtractRawVal = RawValue;
		String ExtractedVal0 = "";
		String ExtractedVal1 = "";
		String ExtractedValNoPipe = "";
		
		try {
			
			//concatenation
			
			if(RawValue.toLowerCase().contains("key("))
			{
				
				if(RawValue.contains("|") || RawValue.contains(" + ")) {
					
					// perform split
					String SplitRawValue[] = null;
					if(RawValue.contains("|")) {
						SplitRawValue = RawValue.split("\\|");	
					}else {
						SplitRawValue = RawValue.split(" \\+ ");
					}
					
					
					// check if there's a key( for index = 0
					if(SplitRawValue[0].toLowerCase().contains("key("))
					{
						// split index if there's a key(
						String[] SplitKey = SplitRawValue[0].toLowerCase().trim().split("\\(");
						ExtractedVal0 = DataRepositoryEx.get(SplitKey[1].trim().replace(")", ""));
					}else {
						// get the raw value(0) if NO key( found
						ExtractedVal0 = SplitRawValue[0];
					}
					//System.out.println("ExtractedVal0" + ExtractedVal0);
					
					// check if there's a key( for index = 1
					if(SplitRawValue[1].toLowerCase().contains("key("))
					{
						// split index if there's a key(
						String[] SplitKey = SplitRawValue[1].toLowerCase().trim().split("\\(");
						ExtractedVal1 = DataRepositoryEx.get(SplitKey[1].trim().replace(")", ""));
					}else {
						// get the raw value(1) if NO key( found
						ExtractedVal1 = SplitRawValue[1];
					}
					
					//System.out.println("ExtractedVal1" + ExtractedVal1);
				}else {
					// with key( w/o pipe or w/o plus
					String[] SplitKey = RawValue.toLowerCase().split("\\(");
					ExtractedValNoPipe = DataRepositoryEx.get(SplitKey[1].replace(")", "").toLowerCase());
				}	
				
				// consolidate extracted key values
				if(ExtractedValNoPipe.isEmpty()) {
					if(RawValue.contains("|")) {
						ExtractRawVal = ExtractedVal0 + "|" + ExtractedVal1;
					}else {
						ExtractRawVal = ExtractedVal0 + ExtractedVal1;
					}
						
				}else {
					ExtractRawVal = ExtractedValNoPipe;
				}
				
			}
		}catch(Exception e)
		{
			//e.printStackTrace();	
			return ExtractRawVal;
		}
		
		return ExtractRawVal;
	}
	
	
	public void AutomationEngine(String InputFile) throws IOException, InterruptedException
	{
		    SummaryStat = true;
		    String NextTestID;
		    String FieldKey = "";
		    String TextKey = "";

		    for (int i = 1; i <= TestId.size(); i++) {
		        String Key = String.valueOf(i);
		        SetCurrentHashMapKey(Key);
		        TextKey = ExtractKeyVal(Text.get(Key));
		        FieldKey = ExtractKeyVal(Field.get(Key));
		        		        
		        try {
		        	Print("Details", String.valueOf(i+1), Description.get(Key),Command.get(Key),TextKey,FindBy.get(Key),FieldKey,Skip.get(Key),SkipNextIfPass.get(Key),StopAllIfFail.get(Key));
					
					if(i==TestId.size())//Last Row
					{
						NextTestID = "0";
					}else{
						NextTestID = TestId.get(String.valueOf(i+1));
					}
					//SKIP = Y
					if(!Skip.get(Key).equalsIgnoreCase("Y")) // if SKIP = N
					{	// SkipNextIfPass = Y(If Pass)
						if(!GetSKIPNextFlag()) // if STEP is SKIPNEXT command is False
						{
							SetStartDate(i);
							ExecuteCommand(Command.get(Key),TextKey,FindBy.get(Key),FieldKey,Skip.get(Key), SkipNextIfPass.get(Key), Key);
							SetEndDate(i);
							RegStatus.put(String.valueOf(i), this.GetStat());
							Print("Status","","","","","","","","","");
							SetDuration(Key);

							CheckSummaryStatus(TestId.get(Key),NextTestID,this.GetStat()); // check if test condition status will be updated
							TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(i+1) + "_" + TestId.get(Key) + "_" +  this.GetStat() + ".jpg");
							SetReportStatus(this.GetStat(), "[Row: " + String.valueOf(i+1) + "] - " + Description.get(Key) + " [" + Duration.get(Key) + "]",TextKey, this.ActualResult);
							
							//this.GetStat() StopAllIfFail
							if (StopAllIfFail.get(Key).equalsIgnoreCase("Y") && this.GetStat().equalsIgnoreCase("Fail"))
							{	
							    QuitAllWebDrivers();
							    UpdatingEXCEL(InputFile);
							    TimeUnit.SECONDS.sleep(3);

							    if(Main.lstFile.size() == 1) {
								    OpenExcel();		    	
							    }

							    ReportClosing();
							    Print("StopAllFail","","","","","","","","","");
							    
							    if(Main.lstFile.size() > 1) {
							    	break;
							    }else {
							    	System.exit(0);
							    }
							    
							}
												
						}else
						{
							RegStatus.put(String.valueOf(i), "SKIP"); // Skip by Rules
							CheckSummaryStatus(TestId.get(Key),NextTestID,this.GetStat());
							SetSKIPNextFlag(false);// Reset into False
							TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(i+1) + "_" + TestId.get(Key) + "_" +  "SKIP" + ".jpg");
							SetReportStatus("SKIP", "[Row: " + String.valueOf(i+1) + "] - " + Description.get(Key) + " [00.000]", TextKey, "");
							Print("Skip","","","","","","","","","");
						}
						
					}else {
						RegStatus.put(String.valueOf(i), "SKIP"); // Skip by Manual
						CheckSummaryStatus(TestId.get(Key),NextTestID,"SKIP");
						TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(i+1) + "_" + TestId.get(Key) + "_" +  "SKIP" + ".jpg");
						SetReportStatus("SKIP", "[Row: " + String.valueOf(i+1) + "] - " + Description.get(Key) + " [00.000]", TextKey, "");
						Print("Skip","","","","","","","","","");
					}

		        }catch(Exception e)
		        {
				    UpdatingEXCEL(InputFile);
				    TimeUnit.SECONDS.sleep(3);
				   
				    if(Main.lstFile.size() == 1) {
					    OpenExcel();		    	
				    }
				    
				    ReportClosing();
		        	e.printStackTrace();
		        }
				
		    }     

		    QuitAllWebDrivers();
		    Print("Finishing1","","","","","","","","","");
		    TimeUnit.SECONDS.sleep(3);
		    UpdatingEXCEL(InputFile);
		    
		    if(Main.lstFile.size() == 1) {
			    OpenExcel();		    	
		    }
		    
		    ReportClosing();
		    Print("Finishing2","","","","","","","","","");
		    WriteLog(GetErrorMessage());
		  
		}

	
	public void QuitAllWebDrivers() {
		if(BrowserFactory.ChromeDriver!=null){
			BrowserFactory.ChromeDriver.quit();
		}
			
		if(BrowserFactory.FireFoxDriver!=null){
			BrowserFactory.FireFoxDriver.quit();
		}
		
		if(BrowserFactory.IEDriver!=null){
			BrowserFactory.IEDriver.quit();
		}
		
		if(BrowserFactory.ChromeDriver1!=null){
			BrowserFactory.ChromeDriver1.quit();
		}
		
		if(BrowserFactory.FireFoxDriver1!=null){
			BrowserFactory.FireFoxDriver1.quit();
		}
		
		if(BrowserFactory.IEDriver1!=null){
			BrowserFactory.IEDriver1.quit();
		}
		
		if(BrowserFactory.BsDriver!=null){
			BrowserFactory.BsDriver.quit();
		}
		
		if(BrowserFactory.wd!=null){
			BrowserFactory.wd.quit();
		}
		
		BrowserFactory.PrevBrowser = "";
	}
	
	public void Print(String PrintType,String row, String desc, String cmd, String text, String findby, String field, String skip, String skipnextiffail, String stopalliffail)
	{
		String ConsoleLog = "";
		
		if(PrintType.equalsIgnoreCase("Details"))
		{
			ConsoleLog = "\nRow #:" + row + "\nDescription/Field: " + desc + "\nCommand: " + cmd + "\nText: " + text + "\nFindBy: " + findby + "\nField: " + field + "\nSkip: " + skip + "\nSkipNextIfFail: " + skipnextiffail + "\nStopAllIfFail: " + stopalliffail + "\n";			
		}else if(PrintType.equalsIgnoreCase("Status")) 
		{
			ConsoleLog = "Actual Result: " + this.ActualResult + "\nStatus: " + this.GetStat() + "\n";
		}else if(PrintType.equalsIgnoreCase("StopAllFail")) 
		{
			ConsoleLog = "Execution is Stopped by StopAllIfFail rule\n";
		}else if(PrintType.equalsIgnoreCase("Skip")) 
		{
			ConsoleLog = "Actual Result: N/A \nStatus: SKIP\n";
		}else if(PrintType.equalsIgnoreCase("StartRepeat")) 
		{
			ConsoleLog = "\n-------------------------- S T A R T  O F  R E P E A T  S T E P S\n";
		}else if(PrintType.equalsIgnoreCase("EndRepeat")) 
		{
			ConsoleLog = "\n-------------------------- E N D  O F  R E P E A T  S T E P S\n";	
		}else if(PrintType.equalsIgnoreCase("StartExternalRepeat")) 
		{
			ConsoleLog = "\n-------------------------- S T A R T  O F  E X T E R N A L  R E P E A T  S T E P S\n";
		}else if(PrintType.equalsIgnoreCase("EndExternalRepeat")) 
		{
			ConsoleLog = "\n-------------------------- E N D  O F  E X T E R N A L  R E P E A T  S T E P S\n";	
		}else if(PrintType.equalsIgnoreCase("Finishing1")) 
		{
			ConsoleLog = "\n# Updating EXCEL File...\n";
		}else if(PrintType.equalsIgnoreCase("Finishing2")) 
		{
			ConsoleLog = "# Execution is COMPLETED.\n";
		}
			
		SetErrorMessage(ConsoleLog);
		
		if(!getUIMode()) {
			System.out.print(ConsoleLog);
		}
		
	}
	
	public String RepeatSteps(String WebFieldVal,String RepeatStepsKey)
	{
		ThreadFactory TF = new ThreadFactory();
		Boolean RStatus = true;
		String[] Steps = null;
		String StepList = "";
		
		if(WebFieldVal.contains("-")) { // range
			String FromRange[] = WebFieldVal.trim().split("-"); 
			for(int i=Integer.parseInt(FromRange[0]); i<=Integer.parseInt(FromRange[1]); i++)
			{
				StepList += String.valueOf(i) + ",";
			}
		}
		
		if(StepList.isEmpty()) {
			Steps = WebFieldVal.trim().split(","); // single convert to series for looping
		}else {
			Steps = StepList.trim().split(","); // range convert to series for looping
		}
		
		String key = "", FieldKey = "", TextKey = "", SkipKey = "N";
		if(!Field.get(RepeatStepsKey).isEmpty()) { // check if repeat steps command has Field value

			// multiple text         Text[4=SSMS];Text[6=Eclipse]
			if(Field.get(RepeatStepsKey).contains(";")) {
				String[] SplitField = Field.get(RepeatStepsKey).split("\\;");
				
				for(String s: SplitField) {
					String[] SplitFieldByEqual = s.trim().split("=");
					
					if(s.toLowerCase().trim().contains("text[")) {
						TempText.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));										
					}
				
					if(s.toLowerCase().trim().contains("field[")) {
						TempField.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));										
					}
					
				}
			
			}else {
				// single text
				String[] SplitFieldByEqual = Field.get(RepeatStepsKey).trim().split("=");
			
				if(Field.get(RepeatStepsKey).toLowerCase().trim().contains("text[")) {
					TempText.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));					
				}

				if(Field.get(RepeatStepsKey).toLowerCase().trim().contains("field[")) {
					TempField.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));					
				}

			}
		}
		
        //System.out.println("TempText: " + TempText);
		Print("StartRepeat","","","","","","","","",""); // start of repeat steps
		for(String s : Steps)
		{

			 try {
				 	key = String.valueOf(Integer.parseInt(s) - 1); // convert row number to hash map key
				 	SetCurrentHashMapKey(key);
				 	
				 	if(TempText.get(key)!=null) {
				 		TextKey = ExtractKeyVal(TempText.get(key));
				 	}else {
				 		TextKey = ExtractKeyVal(Text.get(key));	
				 	}
			        
				 	if(TempField.get(key)!=null) {
				 		FieldKey = ExtractKeyVal(TempField.get(key));
				 	}else {
				 		FieldKey = ExtractKeyVal(Field.get(key));	
				 	}
			        
			        
					//Print("Details",s,Description.get(key),Command.get(key),TextKey,FindBy.get(key),FieldKey,Skip.get(key),SkipNextIfPass.get(key),StopAllIfFail.get(key));
			        Print("Details",s,Description.get(key),Command.get(key),TextKey,FindBy.get(key),FieldKey,SkipKey,SkipNextIfPass.get(key),StopAllIfFail.get(key));
			        TF.SetLog(GetErrorMessage()); 
					//SKIP = Y
					if(!SkipKey.equalsIgnoreCase("Y")) // if SKIP = N
					{	// SkipNextIfPass = Y(If Pass)
						if(!GetSKIPNextFlag()) // if STEP is SKIPNEXT command is False
						{
							//ExecuteCommand(Command.get(key), TextKey,FindBy.get(key),FieldKey,Skip.get(key), SkipNextIfPass.get(key),key);
							ExecuteCommand(Command.get(key), TextKey,FindBy.get(key),FieldKey,SkipKey, SkipNextIfPass.get(key),key);
							Print("Status","","","","","","","","","");
							RStatus &= ConvStatToBool(Stat);
							//TimeUnit.SECONDS.sleep(1);
							//TakeScreenShot(this.ScreenShotCurrentDir + "\\" + String.valueOf(key) + "_" + TestId.get(key) + "_" +  Stat + "_" + RepeatStepsRowNum + "_Repeat" + ".jpg");
							TakeScreenShot(ScreenShotCurrentDir + "\\" + (Integer.parseInt(RepeatStepsKey)+1)  + "_Repeat_" + String.valueOf(Integer.parseInt(key)+1) + "_" + TestId.get(key) + "_" +  Stat + ".jpg");
							//TimeUnit.SECONDS.sleep(1);
							
							//this.GetStat() StopAllIfFail
							
							if (StopAllIfFail.get(key).equalsIgnoreCase("Y") && this.GetStat().equalsIgnoreCase("Fail"))
							{
								
								RStatus &= false;
							}			
							
						}else
						{
							Print("Skip","","","","","","","","","");
						}
						
					}else {
						
						Print("Skip","","","","","","","","","");
					}

		        }catch(Exception e)
		        {
		        	e.printStackTrace();
		        }
		}
		
		// Clearing TempText & TempField
		
		if(TempText!=null) {
			TempText.clear();
		}
		
		if(TempField!=null) {
			TempField.clear();
		}
		
		Print("EndRepeat","","","","","","","","","");
		if(RStatus)
		{
			return "Pass";
		}else {
			return "Fail";
		}
		
  }

	public String RepeatStepsEx(String WebFieldVal,String RepeatStepsKey)
	{
		ThreadFactory TF = new ThreadFactory();
		Boolean RStatus = true;
		String[] Steps = null;
		String StepList = "";
		
		if(WebFieldVal.contains("-")) { // range
			String FromRange[] = WebFieldVal.trim().split("-"); 
			for(int i=Integer.parseInt(FromRange[0]); i<=Integer.parseInt(FromRange[1]); i++)
			{
				StepList += String.valueOf(i) + ",";
			}
		}
		
		if(StepList.isEmpty()) {
			Steps = WebFieldVal.trim().split(","); // single convert to series for looping
		}else {
			Steps = StepList.trim().split(","); // range convert to series for looping
		}
		
		String key = "", FieldKey = "", TextKey = "", SkipKey = "N";
		if(!FieldEx.get(RepeatStepsKey).isEmpty()) { // check if repeat steps command has Field value

			// multiple text/field value         Text[4=SSMS];Text[6=Eclipse]
			if(FieldEx.get(RepeatStepsKey).contains(";")) {
				String[] SplitField = FieldEx.get(RepeatStepsKey).split("\\;");
				
				for(String s: SplitField) {
					String[] SplitFieldByEqual = s.trim().split("=");
					
					if(s.toLowerCase().trim().contains("text[")) {
						TempText.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));										
					}
				
					if(s.toLowerCase().trim().contains("field[")) {
						TempField.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));										
					}
					
				}
			
			}else {
				// single text/field value
				String[] SplitFieldByEqual = FieldEx.get(RepeatStepsKey).trim().split("=");
			
				if(FieldEx.get(RepeatStepsKey).toLowerCase().trim().contains("text[")) {
					TempText.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));					
				}

				if(FieldEx.get(RepeatStepsKey).toLowerCase().trim().contains("field[")) {
					TempField.put(String.valueOf(Integer.parseInt(SplitFieldByEqual[0].trim().replaceAll("[a-z|A-Z]+\\[",""))-1), SplitFieldByEqual[1].trim().replace("]",""));					
				}

			}
		}
		
        //System.out.println("TempText: " + TempText);
		Print("StartRepeat","","","","","","","","",""); // start of repeat steps
		for(String s : Steps)
		{

			 try {
				 	key = String.valueOf(Integer.parseInt(s) - 1); // convert row number to hash map key
				 	SetCurrentHashMapKey(key);
				 	
				 	if(TempText.get(key)!=null) {
				 		TextKey = ExtractKeyVal(TempText.get(key));
				 	}else {
				 		TextKey = ExtractKeyVal(TextEx.get(key));	
				 	}
			        
				 	if(TempField.get(key)!=null) {
				 		FieldKey = ExtractKeyVal(TempField.get(key));
				 	}else {
				 		FieldKey = ExtractKeyVal(FieldEx.get(key));	
				 	}
			        
			        
					//Print("Details",s,Description.get(key),Command.get(key),TextKey,FindBy.get(key),FieldKey,Skip.get(key),SkipNextIfPass.get(key),StopAllIfFail.get(key));
			        Print("Details",s,DescriptionEx.get(key),CommandEx.get(key),TextKey,FindByEx.get(key),FieldKey,SkipKey,SkipNextIfPassEx.get(key),StopAllIfFailEx.get(key));
			        TF.SetLog(GetErrorMessage()); 
					//SKIP = Y
					if(!SkipKey.equalsIgnoreCase("Y")) // if SKIP = N
					{	// SkipNextIfPass = Y(If Pass)
						if(!GetSKIPNextFlag()) // if STEP is SKIPNEXT command is False
						{
							//ExecuteCommand(Command.get(key), TextKey,FindBy.get(key),FieldKey,Skip.get(key), SkipNextIfPass.get(key),key);
							ExecuteCommand(CommandEx.get(key), TextKey,FindByEx.get(key),FieldKey,SkipKey, SkipNextIfPassEx.get(key),key);
							Print("Status","","","","","","","","","");
							RStatus &= ConvStatToBool(Stat);
							//TimeUnit.SECONDS.sleep(1);
							//TakeScreenShot(this.ScreenShotCurrentDir + "\\" + String.valueOf(key) + "_" + TestId.get(key) + "_" +  Stat + "_" + RepeatStepsRowNum + "_Repeat" + ".jpg");
							TakeScreenShot(ScreenShotCurrentDir + "\\" + (Integer.parseInt(RepeatStepsKey)+1)  + "_Repeat_" + String.valueOf(Integer.parseInt(key)+1) + "_" + TestIdEx.get(key) + "_" +  Stat + ".jpg");
							//TimeUnit.SECONDS.sleep(1);
							
							//this.GetStat() StopAllIfFail
							
							if (StopAllIfFailEx.get(key).equalsIgnoreCase("Y") && this.GetStat().equalsIgnoreCase("Fail"))
							{
								
								RStatus &= false;
							}			
							
						}else
						{
							Print("Skip","","","","","","","","","");
						}
						
					}else {
						
						Print("Skip","","","","","","","","","");
					}

		        }catch(Exception e)
		        {
		        	e.printStackTrace();
		        }
		}
		
		// Clearing TempText & TempField
		
		if(TempText!=null) {
			TempText.clear();
		}
		
		if(TempField!=null) {
			TempField.clear();
		}
		
		Print("EndRepeat","","","","","","","","","");
		if(RStatus)
		{
			return "Pass";
		}else {
			return "Fail";
		}
		
  }

	
	public void ImportRepository(String InputFile, boolean ExternalFile, String sheetName) throws IOException
	{
		File file =  new File(InputFile);
	    FileInputStream inputStream = new FileInputStream(file);
	    Workbook MarWorkbook = null;
	    MarWorkbook = new XSSFWorkbook(inputStream);
	    Sheet MarSheet = MarWorkbook.getSheet(sheetName);
	    
	    if(ExternalFile) {
	    	ImportingExternalRepository(MarSheet, MarSheet.getLastRowNum()-MarSheet.getFirstRowNum());	    	
	    }else {
	    	ImportingRepository(MarSheet, MarSheet.getLastRowNum()-MarSheet.getFirstRowNum());
	    }
	    
	    MarWorkbook.close();
	}
	
	public void ImportingExternalRepository(Sheet s, int rowCount)
	{
		Row row;
		//String TestDataKey = "", TestDataText = "", ObjectKey = "", ObjectLocator = "";
	    for (int i = 1; i <= rowCount; i++) {
	        row = s.getRow(i);
	        
	        try {

	        	DataRepositoryEx.put(row.getCell(4).getStringCellValue().toLowerCase() + "", row.getCell(5).getStringCellValue() + "");
				}catch(Exception e) {
	        	//e.printStackTrace();
	        }

		} 
	   // System.out.println("DataRepositoryEx: " + DataRepositoryEx);
	}
	
	public void ImportingRepository(Sheet s, int rowCount)
	{
		Row row;
		//String TestDataKey = "", TestDataText = "", ObjectKey = "", ObjectLocator = "";
	    if(DataRepository.size()!=0) {
	    	DataRepository.clear();
	    }
	    
	    String key = "", keyValue = "";
		for (int i = 1; i <= rowCount; i++) {
	        row = s.getRow(i);
	        
	        try {
	        	key = row.getCell(4).getStringCellValue().toLowerCase() + "";
	        	keyValue = row.getCell(5).getStringCellValue() + "";
	        	
	        	if(!key.isEmpty() && !keyValue.isEmpty()) {
	        		DataRepository.put(key, keyValue);
	        	}
	        	
				}catch(Exception e) {
	        	//e.printStackTrace();
	        }

		} 

	}
	
	public void ImportExcelData(String InputFile, boolean ExternalFile, String sheetName) throws IOException
	{ 
			File file =  new File(InputFile);
		    FileInputStream inputStream = new FileInputStream(file);
		    Workbook MarWorkbook = null;
		    MarWorkbook = new XSSFWorkbook(inputStream);
		    Sheet MarSheet = MarWorkbook.getSheet(sheetName);
		   
		    
		    if(ExternalFile) {
		    	ImportingExternalData(MarSheet, MarSheet.getLastRowNum()-MarSheet.getFirstRowNum());
		    }else {
		    	
		    	if(getUIMode()) {
		    		SetErrorMessage("# Importing Excel Data for Execution... \n");
		    	}else {
		    		System.out.println("# Importing Excel Data for Execution...");
		    	}
		    	
		    	
		    	ImportingPerSheet(MarSheet, MarSheet.getLastRowNum()-MarSheet.getFirstRowNum());
		    }
		    
		    MarWorkbook.close();
		    
		    
	    	if(getUIMode()) {
	    		SetErrorMessage("# Done importing. \n");
	    	}else {
	    		System.out.println("# Done importing.");
	    	}
	    	
	}
	
	public void importTestConditions(String InputFile, boolean ExternalFile, String sheetName) throws IOException
	{ 
			File file =  new File(InputFile);
		    FileInputStream inputStream = new FileInputStream(file);
		    Workbook MarWorkbook = null;
		    MarWorkbook = new XSSFWorkbook(inputStream);
		    Sheet MarSheet = MarWorkbook.getSheet(sheetName);

		    	
		    int rowCount = MarSheet.getLastRowNum()-MarSheet.getFirstRowNum();
		    
			if(TestConditions.size()!=0) {
				TestConditions.clear();
			}
			
			Row row;
		    for (int i = 5; i <= rowCount; i++) {
		        row = MarSheet.getRow(i);
		        
		        try {
		        	TestConditions.put(row.getCell(0).getStringCellValue() + "", row.getCell(2).getStringCellValue() + "");
					}catch(Exception e) {
		        	e.printStackTrace();
		        }
				
			} 
		    MarWorkbook.close();
	    	
	}
	
	public void ImportingExternalData(Sheet s, int rowCount) {
		
		if(TestIdEx.size()!=0) {
			ClearRegressionKeysEx();
		}
		
		Row row;
	    for (int i = 1; i <= rowCount; i++) {
	        row = s.getRow(i);
	        
	        try {
	        	DescriptionEx.put(String.valueOf(i), row.getCell(0).getStringCellValue() + "");
	        	TestIdEx.put(String.valueOf(i), row.getCell(1).getStringCellValue() + "");
	        	CommandEx.put(String.valueOf(i), row.getCell(2).getStringCellValue() + "");
	        	TextEx.put(String.valueOf(i), row.getCell(3).getStringCellValue() + "");
	        	FindByEx.put(String.valueOf(i), row.getCell(4).getStringCellValue() + "");
	        	FieldEx.put(String.valueOf(i), row.getCell(5).getStringCellValue() + "");
	        	SkipEx.put(String.valueOf(i), row.getCell(6).getStringCellValue() + "");
	        	SkipNextIfPassEx.put(String.valueOf(i), row.getCell(7).getStringCellValue() + "");
	        	StopAllIfFailEx.put(String.valueOf(i), row.getCell(8).getStringCellValue() + "");
	        	//System.out.println("CommandEx.size(): " + CommandEx.size());
	        	//System.out.println("CommandEx value: " + row.getCell(2).getStringCellValue());
	        	
				}catch(Exception e) {
	        	e.printStackTrace();
	        }
			
		} 
	}
	
	public void ImportingPerSheet(Sheet s, int rowCount)
	{
		if(TestId.size()!=0) {
			ClearRegressionKeys();
		}
		
		Row row;
	    for (int i = 1; i <= rowCount; i++) {
	        row = s.getRow(i);
	
	        if(row!=null) {
		        try {
		        	Description.put(String.valueOf(i), row.getCell(0).getStringCellValue() + "");
		        	TestId.put(String.valueOf(i), row.getCell(1).getStringCellValue() + "");
		        	Command.put(String.valueOf(i), row.getCell(2).getStringCellValue() + "");
		        	Text.put(String.valueOf(i), row.getCell(3).getStringCellValue() + "");
		        	FindBy.put(String.valueOf(i), row.getCell(4).getStringCellValue() + "");
		        	Field.put(String.valueOf(i), row.getCell(5).getStringCellValue() + "");
		        	Skip.put(String.valueOf(i), row.getCell(6).getStringCellValue() + "");
		        	SkipNextIfPass.put(String.valueOf(i), row.getCell(7).getStringCellValue() + "");
		        	StopAllIfFail.put(String.valueOf(i), row.getCell(8).getStringCellValue() + "");
					}catch(Exception e) {
		        	e.printStackTrace();
		        }	        	
	        }

			
		} 
	}
	
	public void ClearRegressionKeys() {
		Description.clear();
		TestId.clear();
		Command.clear();
		Text.clear();
		FindBy.clear();
		Field.clear();
		SumStatus.clear();
		Skip.clear();
		SkipNextIfPass.clear();
		StopAllIfFail.clear();
		
		//RegStatus.clear();
		//Start.clear();
		//End.clear();
		//Duration.clear();
	}

	public void ClearRegressionKeysEx() {
		DescriptionEx.clear();
		TestIdEx.clear();
		CommandEx.clear();
		TextEx.clear();
		FindByEx.clear();
		FieldEx.clear();
		SkipEx.clear();
		SkipNextIfPassEx.clear();
		StopAllIfFailEx.clear();
	}
	
	public void ClearSumRegStatus() {
		// clearing for restart the execution
		SumStatus.clear();
		RegStatus.clear();
		
		Start.clear();
		End.clear();
		Duration.clear();
		
	}
	
	public void UpdatingEXCEL(String fName) throws IOException
	{
			File file = new File(fName);
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook MarWorkbook = null;
	        MarWorkbook = new XSSFWorkbook(inputStream);
	        
	        String[] sht = {"Regression","Test Conditions"};
	        Sheet RegSheet = MarWorkbook.getSheet(sht[0]);
	        Sheet SumSheet = MarWorkbook.getSheet(sht[1]);
	       
	        CellStyle styleG = MarWorkbook.createCellStyle();  
	        styleG.setFillForegroundColor(IndexedColors.GREEN.getIndex());  
	        styleG.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
	        CellStyle styleR = MarWorkbook.createCellStyle();  
	        styleR.setFillForegroundColor(IndexedColors.RED.getIndex());  
	        styleR.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
	        

	        
	        try {
	        	 for(int i=0; i<=sht.length-1; i++) // 0 = Regression, 1= Test Conditions
	 	        {  
	 	        	if(sht[i].equalsIgnoreCase("Regression")) {// EndRow = EndRow = RegSheet.getLastRowNum()-RegSheet.getFirstRowNum()
	 	        		int StartRow = 1, EndRow = RegStatus.size(), StatusColumn = 9, StartColumn = 10, EndColumn = 11, DurationColumn = 12;
	 	        		Row rowNum;
	 	        		Cell cellStatusReg, cellStartDt, cellEndDt, cellDuration;
	 	        		for(int row = StartRow; row <= EndRow; row++) // Reading Cell for status, start and end date updating.
	 	        		{
	 	        			rowNum = RegSheet.getRow(row);
	 	        			cellStatusReg = rowNum.getCell(StatusColumn);
	 	        			cellStartDt = rowNum.getCell(StartColumn);
	 	        			cellEndDt = rowNum.getCell(EndColumn);
	 	        			cellDuration = rowNum.getCell(DurationColumn);
	 	        			
	 	        			if(RegStatus.get(String.valueOf(row))!=null) {
		 	        			if(!RegStatus.get(String.valueOf(row)).equalsIgnoreCase("SKIP"))
		 	        			{
		 	        				cellStatusReg.setCellValue(RegStatus.get(String.valueOf(row)));
		 		        			cellStartDt.setCellValue(Start.get(String.valueOf(row)));
		 		        			cellEndDt.setCellValue(End.get(String.valueOf(row)));
		 		        			cellDuration.setCellValue(Duration.get(String.valueOf(row)));
		 		        			
		 		    			    if(RegStatus.get(String.valueOf(row)).equalsIgnoreCase("Pass"))
		 		    			    {
		 		    			    	cellStatusReg.setCellStyle(styleG);
		 		    			    }else if(RegStatus.get(String.valueOf(row)).equalsIgnoreCase("Fail")){
		 		    			    	cellStatusReg.setCellStyle(styleR);	
		 		    			    }
		 	        			}else {
		 	        				cellStatusReg.setCellValue("SKIP");
		 	        			}
	 	        			}

	 	        			
	 	        		}
	 	        	}
	 	        }	
	 			    inputStream.close();
	 			    FileOutputStream outputStream = new FileOutputStream(file);
	 			    MarWorkbook.write(outputStream);
	 			    outputStream.close(); 
	        }catch(Exception e)
	        {
	        	
	        	if(MarWorkbook!=null) {
	        		MarWorkbook.close();
	        	}
	        	
	        	//e.printStackTrace();
	        }
	       
	        
	        try{
        		int Row, StatusColumn = 10;
        		
        		for(String sTestId : SumStatus.keySet()) // loop grouped test id
        		{
	        		Row rowNum;
	        		Cell cellStatusSum;
	        		boolean FoundFlag = false;
	        		Row = 5;

	        		while(!FoundFlag) // loop on each grouped test id to scenario id/1st column
	        		{
	        			rowNum = SumSheet.getRow(Row);
	        			cellStatusSum = rowNum.getCell(StatusColumn);

	        			if(sTestId.equalsIgnoreCase(rowNum.getCell(0).getStringCellValue() + "") && rowNum.getCell(StatusColumn).getStringCellValue().isEmpty()) // if Test ID found
	        			{
	        				cellStatusSum.setCellValue(SumStatus.get(sTestId));
	        				FoundFlag = true;
	        				
		    			    if(SumStatus.get(sTestId).equalsIgnoreCase("Pass"))
		    			    {
		    			    	cellStatusSum.setCellStyle(styleG);
		    			    }else if(SumStatus.get(sTestId).equalsIgnoreCase("Fail")){
		    			    	cellStatusSum.setCellStyle(styleR);	
		    			    }
	        			}
	        			
	    			    Row++;
	        		}
	        		
        		}
        		
 			    inputStream.close();
 			    FileOutputStream outputStream = new FileOutputStream(file);
 			    MarWorkbook.write(outputStream);
 			    outputStream.close(); 
 			    
	        }catch(Exception e)
	        {
	        	if(MarWorkbook!=null) {
	        		MarWorkbook.close();
	        	}
	        	//e.printStackTrace();
	        }
	        
	        MarWorkbook.close();
	        
	}
	
	public void CheckSummaryStatus(String CurrentTestID, String NextTestID,String CurrentDetailStat)
	{
		/*
		if(CurrentDetailStat.equalsIgnoreCase("Pass")){
			SummaryStat = SummaryStat & true;
		}else {
			SummaryStat = SummaryStat & false;
		}*/
		
		if(CurrentDetailStat.equalsIgnoreCase("Pass")){
			SummaryStat = SummaryStat & true;
		}
		
		if(CurrentDetailStat.equalsIgnoreCase("Fail")){
			SummaryStat = SummaryStat & false;
		}
		
		
		if(CurrentDetailStat.equalsIgnoreCase("Skip") && !StatusIsSkip){
			StatusIsSkip = true;
		}
		
		if(!CurrentTestID.equalsIgnoreCase(NextTestID)) // If NOT same testID
		{		

			
			if(StatusIsSkip) {
				// summary/test conditions/scenario status = "Skip" if all steps is Skip
				SumStatus.put(CurrentTestID, "SKIP");
				
			}else {
				SumStatus.put(CurrentTestID, ConvStat(SummaryStat));
			} 
			
			 // reset
			 SummaryStat = true;
			 StatusIsSkip = false;
			 
			 // add new extent scenario
			 addScenario(NextTestID);
			 
			 // if close all browsers (prior each scenario) check box in the UI Mode is checked.
			 if(getUIMode()) {
				 ThreadFactory TF = new ThreadFactory();
				 UIMode UIM = new UIMode();
				 if(UIM.chckbxclosebrowsers.isSelected()) {
					 TF.QuitAllWebDrivers();
				 }
			 }
	 
		}

	}
	
	public void ClearStatusDateTimeRegression()
	{
		try {
	        File file = new File(GetFName());
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook MarWorkbook = null;
	        MarWorkbook = new XSSFWorkbook(inputStream);
	        Sheet Regsheet = MarWorkbook.getSheet("Regression");

	        ClearColumn(MarWorkbook, Regsheet,9, 1,Regsheet.getLastRowNum()-Regsheet.getFirstRowNum());
	        ClearColumn(MarWorkbook, Regsheet,10, 1,Regsheet.getLastRowNum()-Regsheet.getFirstRowNum());
	        ClearColumn(MarWorkbook, Regsheet,11, 1,Regsheet.getLastRowNum()-Regsheet.getFirstRowNum());
	        ClearColumn(MarWorkbook, Regsheet,12, 1,Regsheet.getLastRowNum()-Regsheet.getFirstRowNum());

		    inputStream.close();
		    FileOutputStream outputStream = new FileOutputStream(file);
		    MarWorkbook.write(outputStream);
		    outputStream.close();
		    MarWorkbook.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void ClearStatusDateTimeTestConditions()
	{
		try {
	        File file = new File(GetFName());
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook MarWorkbook = null;
	        MarWorkbook = new XSSFWorkbook(inputStream);
	        Sheet Tessheet = MarWorkbook.getSheet("Test Conditions");

	        ClearColumn(MarWorkbook, Tessheet,10, 5,Tessheet.getLastRowNum()-Tessheet.getFirstRowNum());

		    inputStream.close();
		    FileOutputStream outputStream = new FileOutputStream(file);
		    MarWorkbook.write(outputStream);
		    outputStream.close();
		    MarWorkbook.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void ClearColumn(Workbook wb,Sheet s,int columnNum, int startRow, int rowCount)
	{
		CellStyle styleClear = wb.createCellStyle();
		styleClear = SetCellDefaultFormat(styleClear);
		Row row;
		Cell cell;
	    for (int i = startRow; i < rowCount+1; i++) {
	        
	    	if(s.getRow(i)==null) {
	    		s.createRow(i);
	    		
	    	}
	    	
	    	row = s.getRow(i);	
	        
	    	if(row.getCell(columnNum)==null) {
	    		row.createCell(columnNum);
	    	}
	    	
	        cell = row.getCell(columnNum);
	        cell.setCellStyle(styleClear);
	        cell.setBlank();
	    }
	}
	
	public CellStyle SetCellDefaultFormat(CellStyle CS)
	{
		CS.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex()); 
		CS.setBorderTop(BorderStyle.THIN);
		CS.setBorderLeft(BorderStyle.THIN);
		CS.setBorderRight(BorderStyle.THIN);
		CS.setBorderBottom(BorderStyle.THIN);
		
		return CS;
	}
	
	public String ConvStat(boolean Stat)
	{
		String RStat;
		if(Stat) {
			RStat = "Pass";
		}else{
			RStat = "Fail";
		}
	return RStat;	
	}
	
	public String GetStat()
	{
		return Stat;
	}
	
	public boolean ConvStatToBool(String Stat)
	{
		Boolean fLag = false;
		if(Stat.equalsIgnoreCase("Pass"))
		{
			fLag = true;
		}
		return fLag;
	}
	
	public void SetStartDate(int row)
	{
		//this.SDate = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());	
		Start.put(String.valueOf(row), new SimpleDateFormat("MM-dd-yyyy hh:mm:ss:SSS").format(new Date()));
	}

	public void SetEndDate(int row)
	{
		//this.EDate = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());
		End.put(String.valueOf(row), new SimpleDateFormat("MM-dd-yyyy hh:mm:ss:SSS").format(new Date()));
	}
	
	public String  GetDate(String Dt)
	{
		String rDt;
		if(Dt.equalsIgnoreCase("Start")) {
			rDt = SDate;
		}else
		{
			rDt = EDate;
		}
		return rDt;
	}
	
	public boolean CheckExcelAvail(String fName)
	{
		boolean Flag = false;
		if(new File(fName).exists())
		{
			Flag = true;
		}
		SetErrorFreeFlag(Flag);
		return Flag;
	}
	
	public void SetSKIPNextFlag(Boolean fLag)
	{
		SKIPNextFlag = fLag;
	}
	
	public boolean GetSKIPNextFlag()
	{
		return SKIPNextFlag;
	}
	
	
	public void ClearErrorFreeFlag()
	{
		ErrFree = true;
	}
	
	public void SetErrorFreeFlag(boolean ErrFlag)
	{
		ErrFree = ErrFree && ErrFlag;
	}
	
	public boolean GetErrorFreeFlag()
	{
		return ErrFree;
	}
	
	public boolean CheckFileIsOpen(String fName)
	{
		boolean Flag = false;
		File file = new File(fName);
		File sameFileName = new File(fName);

		if(!file.renameTo(sameFileName)){
			Flag = true;
		}
		
		return Flag;
	}
	
	public void OpenExcel()
	{
	     try{  
	    	 if(CheckExcelAvail(GetFName()))
	    	 {
	    		 Desktop.getDesktop().open(new File(GetFName()));  
	    	 }
	     }
	     catch(IOException e){
	           e.printStackTrace();
	     } 
	}
	
	public static void SetErrorMessage(String Err)
	{
		if(Err.equalsIgnoreCase("Empty()")) {
			ErrorMsg = "";
		}else {
			ErrorMsg = ErrorMsg + Err;
		}
		
	}
	
	public static String GetErrorMessage()
	{
		return ErrorMsg;
	}
	
	public void TakeScreenShot(String filePath)
	{
		try {
			
			if(MainDriver!=null) {
				File srcFile = ((TakesScreenshot)MainDriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(srcFile, new File(filePath));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void CreateLogDirectory()
	{
        File file = new File(GetFParentName() + "\\AutomationLOG");
        if (!file.exists()) {
             file.mkdir();
        }
	}
	
	public void CreateScreenshortDir()
	{
        String SfName = GetFParentName() +"\\AutomationLOG\\" + String.valueOf(new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date())).replace(":","") + "_Screenshots";
        File file = new File(SfName);
        if (!file.exists()) {
             file.mkdir();
        }
        
        SetScreenShotCurrentDir(SfName);
	}
	
	public void SetScreenShotCurrentDir(String SfName) {
		ScreenShotCurrentDir = SfName;
	}
	
	public void WriteLog(String logMessage) throws IOException
	{
		PrintWriter out = new PrintWriter(GetFParentName() + "\\AutomationLOG\\" + String.valueOf(new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date())).replace(":","") + ".TXT");
		out.print(logMessage);
		out.close();
	}
	
	public void CheckRequiredColumns(String fName)
	{
		boolean Flag = false;
		        
		try{
			File file =  new File(fName);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook MarWorkbook = null;
			MarWorkbook = new XSSFWorkbook(inputStream);
			Sheet MarSheet = MarWorkbook.getSheet("Regression");
			int rowCount = MarSheet.getLastRowNum()-MarSheet.getFirstRowNum();
			Row row;
			boolean cFlag=true;
			boolean cFlagPerRow = true;
			 //This command variable will only holds a commands which are not required to check required columns (except skip, skip next if pass and stop all if fail columns) if has a value, otherwise, log an error.
			String Command = " TitleEqual, SetVerSecurityCode, AlertTextEqual, SwitchTab, SwitchFrame, LoginACNFederation, ScrollVertical, ScrollHorizontal, RichSetText, NavigatePage, PageContains, Freeze, RepeatSteps, CheckDuration, Open, CheckFile, CheckWebAccessibility, ";
			for (int i = 1; i < rowCount+1; i++) {
				row = MarSheet.getRow(i);
				
				if(!Command.toLowerCase().contains(" " + row.getCell(2).getStringCellValue().toLowerCase() + ",")) // if commands does not belong for required field validation
				{
					cFlagPerRow = !row.getCell(1).getStringCellValue().isEmpty() && !row.getCell(2).getStringCellValue().isEmpty() &&
							!row.getCell(5).getStringCellValue().isEmpty() && !row.getCell(6).getStringCellValue().isEmpty() && !row.getCell(7).getStringCellValue().isEmpty()&& !row.getCell(8).getStringCellValue().isEmpty();
					//cFlag = cFlag && cFlagPerRow;
				}else{
					// check skip, skip next if pass and stop all fail columns for all commands (no exception)
					cFlagPerRow = cFlagPerRow && !row.getCell(6).getStringCellValue().isEmpty() && !row.getCell(7).getStringCellValue().isEmpty()&& !row.getCell(8).getStringCellValue().isEmpty();
										
				}
				
				cFlag = cFlag && cFlagPerRow;
				if (!cFlagPerRow)
				{
					SetErrorMessage("# Required column/s at row: " + String.valueOf(i+1) + " should NOT be EMPTY. \n");
					cFlagPerRow = true; //reset
				}
				
			}
			if(cFlag){
				Flag = true;
			}
			MarWorkbook.close();
		}catch(Exception e){
		   e.printStackTrace();
			Flag = false;
		}
		
		SetErrorFreeFlag(Flag);
		
	}
	
	public boolean CheckCommmandRequireTEXT(String CommandName, String Text)
	{
		boolean Flag = true;
		//This command variable will only holds a commands which are required to check TEXT column to have a value, otherwise log an error.
		String Command = " Open, SetText, SetTextEnter, Select, TextEqual, TextNotEqual, TextContains, TextNotContains, WaitUntilVisible, WaitUntilInvisible, CheckAvail, CheckSelect, "
				+ "CheckEnable, CheckEnableDropDownItem, AlertTextEqual, TitleEqual, CheckAllImagesAvail, NewTab, TooltipTextEqual, TooltipTextNotEqual, JSONEqual, JSONContains, JSONNotEqual, "
				+ "StoreValToken(Service), StoreValJSON(Service), LoginACNFederation, SwitchTab, ScrollVertical, ScrollHorizontal, AttributeValEqual, AttibuteValNotEqual, "
				+ "CSSValEqual, CSSValContains, CSSValNotEqual, NavigatePage, LabelEqual, LabelContains, LabelNotEqual, LabelNotContains, CheckDropdownVal, SetKey, DPSelectDay, PageContains, Freeze, RepeatSteps, CheckNumberOfElements, SetLabel, CheckFile, ";
		if(Command.toLowerCase().contains(" " + CommandName + ","))
		{
			 if(Text.isEmpty())
			 {
				 Flag = false;
			 }
		}
		
		return Flag;
	}
	
	public boolean CheckTextStaticVal(String CommandName, String Text)
	{
		boolean Flag = false;
		//This command variable will only holds a commands that has an specific TEXT value convention and it should have a corresponding statement below. If statement is false, it will log an error.
		String Command = " CheckAvail, CheckSelect, CheckEnable, CheckEnableDropDownItem, NavigatePage, NewTab, SwitchTab, Open, CheckDropdownVal, DPSelectDay, Freeze, CheckNumberOfElements, SetKey, ScrollVertical, ScrollHorizontal, CheckDuration, CheckFile, ";
		
		if(Command.toLowerCase().contains(" " + CommandName + ",")) // if command belong to the list for static value validation
		{
			if(!Pattern.matches("^key\\([a-z|0-9]+\\)$", Text.toLowerCase())) { // This is to ignore static value check if Key(*) is observed.
				if(CommandName.equalsIgnoreCase("CheckEnableDropDownItem"))
				{
					 if(Pattern.matches("^(true|false)\\([0-9]+\\)$", Text))
					 {
						 Flag = true;
					 }else {
						 ErrorMsgPerRow = " Should be true(integer)/false(integer) ";
					 }
				}else if(CommandName.equalsIgnoreCase("NavigatePage"))
				{
					 if(Pattern.matches("^(refresh|forward|back)\\([0-9]+\\)$", Text))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be refresh(integer)/forward(integer)/back(integer) ";
					 } 
				/*}else if(CommandName.equalsIgnoreCase("LoginACNFederation")) // fur further investigation (command is removed from the list)
				{
					 if(Pattern.matches("\\|", Text))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [EnterpriseID]|[Password] ";
					 } */				
				}else if(CommandName.equalsIgnoreCase("SetKey"))
				{
					if(Text.contains("pasteviaclipboard")) {
						Flag = true;
					}else {
						if(Text.contains(",")) { // exception for validation if multiple values are observed.
							Flag = true;
						}else {
							 if(Pattern.matches("^(tab|arrowdown|arrowup|arrowright|arrowleft|enter|backspace|escape|space|control|robot)\\(([0-9]+|[a-z]+)\\)$", Text))
							 {
								 Flag = true;
							 }else{
								 ErrorMsgPerRow = " Should be Tab/ArrowDown/ArrowUp/ArrowRight/ArrowLeft/Enter/BackSpace/Escape/Space(integer) or Control(character)/Robot(string)|pasteviaclipboard[string]";
							 } 
						}

					}
					
				}else if(CommandName.equalsIgnoreCase("DPSelectDay"))
				{
					 if(Pattern.matches("^[0-9]+$", Text))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [Interger] ";
					 } 
				}else if(CommandName.equalsIgnoreCase("NewTab"))
				{
					 if(Pattern.matches("^[0-9]+$", Text))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [Integer] ";
					 } 				
				}else if(CommandName.equalsIgnoreCase("SwitchTab"))
				{
					 if(Pattern.matches("^[0-9]+$", Text))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [Integer] ";
					 } 				
				}else if(CommandName.equalsIgnoreCase("Open"))
				{
					if(Pattern.matches("^(chrome|ie|firefox|webmobile-chrome[^\\]]+\\]|changeurl|switchbrowser[^\\)]+)(\\-incognito|\\-private)?\\(?[0-9]*\\)?$", Text.toLowerCase()) || Text.toLowerCase().contains("browserstack"))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be chrome/ie/firefox/webmobile-chrome[deviceName]/changeurl/switchbrowser([Browser]) or chrome-incognito/firefox-private/webmobile-chrome-incognito ";
					 } 
				}else if(CommandName.equalsIgnoreCase("CheckFile"))
				{
				 if(Pattern.matches("^(exist\\(true\\)|exist\\(false\\)|size\\([0-9]+\\))$", Text.toLowerCase()))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be exist(true)/exist(false)/size([integer]) ";
					 } 
				}else if(CommandName.equalsIgnoreCase("Freeze"))
				{
					 if(Pattern.matches("^[0-9]+$", Text.toLowerCase()))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [Integer] ";
					 } 				
				}else if(CommandName.equalsIgnoreCase("CheckDropdownVal"))
				{
					 if(Text.contains(","))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [Dropdown value1],[Dropdown value2].... ";
					 } 				
				}else if(CommandName.equalsIgnoreCase("CheckNumberOfElements"))
				{
					 if(Pattern.matches("^[0-9]+$", Text.toLowerCase()))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be [Integer] ";
					 } 							
				}else if(CommandName.equalsIgnoreCase("ScrollVertical"))
				{
					 if(Pattern.matches("^(top|bottom)$", Text.toLowerCase()))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be top/bottom ";
					 } 							
				}else if(CommandName.equalsIgnoreCase("ScrollHorizontal"))
				{
					 if(Pattern.matches("^(right|left)$", Text.toLowerCase()))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be right/left ";
					 } 							
				}else if(CommandName.equalsIgnoreCase("CheckDuration"))
				{
					if(Pattern.matches("^[0-9]+$", Text.toLowerCase())) {
						Flag = true;
					}else {
						ErrorMsgPerRow = " for Performance Mode Should be [Integer] ";
					}
				}else if(CommandName.equalsIgnoreCase("CheckAvail"))
				{
					
					if(Pattern.matches("^(true|false|false\\([0-9]+\\))$", Text.toLowerCase())) {
						Flag = true;
					}else {
						ErrorMsgPerRow = " Should be true/false/false(waiting time)";
					}
					
				}else { // for true/false TEXT static value
					if(Pattern.matches("^(true|false)$", Text.toLowerCase()))
					 {
						 Flag = true;
					 }else{
						 ErrorMsgPerRow = " Should be true/false ";
					 }
				}
			}else {
				Flag = true;
			}
			
		}else
		{
			// commands not belong/excluded for Text Static Validation
			Flag = true;
		}
		
		return Flag;
	}

	
	
	public Boolean CheckFindBy_Field(String Command,String FindBy, String Field)
	{
		Boolean Flag = true;
		//This Excepted command variable will only holds a command that are EXCEPTED in identifying FindBy column as required.
		String ExceptedCommands = " Open, NewTab, TitleEqual, SetVerSecurityCode, AlertTextEqual, SwitchTab, LoginACNFederation, ScrollVertical, ScrollHorizontal, RichSetText, NavigatePage, PageContains, Freeze, RepeatSteps, JSONEqual, JSONContains, JSONNotEqual, CheckDuration, RepeatExternalSteps, RepeatInternalSteps, ConnectSQLServer(Database), StoreValResult(Database), ResultValEqual, ResultValContains, CheckFile, CheckWebAccessibility, ";
		
		if(!ExceptedCommands.toLowerCase().contains(" "+ Command + ", "))
		{
			if(FindBy.isEmpty() && !Field.isEmpty())
			{
				Flag = false;
				ErrorMsgPerRow = " Should NOT be EMPTY ";
			}
			
			if(!FindBy.isEmpty() && Field.isEmpty())
			{
				Flag = false;
				ErrorMsgPerRow = " Should NOT be EMPTY ";
			}
		}
		
		return Flag;
	}
	
	
	public void CheckValidValuePerCommand(String fName)
	{
		boolean Flag = false;
		//this.FailedCommmands = "";
		try{
			File file =  new File(fName);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook MarWorkbook = null;
			MarWorkbook = new XSSFWorkbook(inputStream);
			Sheet MarSheet = MarWorkbook.getSheet("Regression");
			int rowCount = MarSheet.getLastRowNum()-MarSheet.getFirstRowNum();
			Row row;
			String Text = "";
			String FindBy = "";
			String Field = "";
			String Command = "";
			
			boolean cFlag = true;
			boolean cFlagPerRow = true;
			for (int i = 1; i < rowCount+1; i++) {
				
				
				row = MarSheet.getRow(i);
				try{Command = row.getCell(2).getStringCellValue().toLowerCase().trim();}catch(Exception e){Field = "";}
				try{Text = row.getCell(3).getStringCellValue().toLowerCase().trim();}catch(Exception e){Text = "";}
				try{FindBy = row.getCell(4).getStringCellValue().toLowerCase().trim();}catch(Exception e){FindBy = "";}
				try{Field = row.getCell(5).getStringCellValue().toLowerCase().trim();}catch(Exception e){Field = "";}

				// Check command required TEXT
				cFlagPerRow = CheckCommmandRequireTEXT(Command, Text);
				cFlag = cFlag && cFlagPerRow; 
				if(!cFlagPerRow)
				{
					SetErrorMessage("# TEXT column at row: " + String.valueOf(i+1) + " should NOT be EMPTY for Command " + Command + "\n");					
				}
				
				// Check TEXT static value
				cFlagPerRow = CheckTextStaticVal(Command, Text);
				cFlag = cFlag && cFlagPerRow; 
				if(!cFlagPerRow)
				{
					SetErrorMessage("# TEXT column at row: " + String.valueOf(i+1) + ErrorMsgPerRow + "for [Command] " + Command + "\n");					
				}
				
				// Check FindBy and Field
				cFlagPerRow = CheckFindBy_Field(Command, FindBy, Field);
				cFlag = cFlag && cFlagPerRow; 
				if(!cFlagPerRow)
				{
					SetErrorMessage("# FindBy/Field column at row: " + String.valueOf(i+1) + ErrorMsgPerRow + "for [Command] " + Command + "\n");					
				}
					
			}
			
			if(cFlag)
			{
				Flag = true;
			}

			MarWorkbook.close();
		}catch(Exception e){
		   e.printStackTrace();
			Flag = false;
		}
		SetErrorFreeFlag(Flag);

		
	}
	
	public static int LocalCMDsCount(Sheet sht, int CmdCount)
	{
		
    	Row newRow = null;
    	//Cell cell = null;
    	CmdCount = 0;
    	String str = "";
    	for (int i=0;i<=sht.getLastRowNum();i++) {
	        newRow = sht.getRow(i);
	        try {str = newRow.getCell(0).getStringCellValue() + "";}catch(Exception e) {str="";}
	        if(!str.isEmpty())
	        {
	        	CmdCount++;
	        }
	    }

    	
		return CmdCount;
	}
	
	 public static boolean CheckDataSource(String fName, String[] CMD, String[] FindBy)
		{
		 	// commands
			boolean IsDSUpdated = false;
			try {
		        File file = new File(fName);
		        FileInputStream inputStream = new FileInputStream(file);
		        Workbook MarWorkbook = null;
		        MarWorkbook = new XSSFWorkbook(inputStream);
		        Sheet DSSheet = MarWorkbook.getSheet("Data Source");
		        
		        if(getUIMode()) {
		        	SetErrorMessage("# Checking local Data Source..... \n");
		        }else {
		        	System.out.println("# Checking local Data Source.....");
		        }
		        
		        
		       
		        if(LocalCMDsCount(DSSheet,0) != CMD.length)
		        {
		        	
			        if(getUIMode()) {
			        	SetErrorMessage("# Local Data Source is out-dated..... \n" + "# Performing local Data Source Updating..... \n");
			        }else {
			        	System.out.println("# Local Data Source is out-dated..... \n" + "# Performing local Data Source Updating.....");
			        }
		        	
		        	Row newRow = null;
		        	Cell cell = null;
		        	Arrays.sort(CMD);
		        	int i = 0;
		        	for (String s: CMD) {
				        
				        if(DSSheet.getRow(i)==null) {
				        	newRow.createCell(i);
				        }else {
				        	newRow = DSSheet.getRow(i);
				        }
				        
				        cell = newRow.getCell(0);
				        cell.setCellValue(s.trim());
				        i++;
				    }
		        	
				    inputStream.close();
				    FileOutputStream outputStream = new FileOutputStream(file);
				    MarWorkbook.write(outputStream);
				    outputStream.close(); 
				    
			        if(getUIMode()) {
			        	SetErrorMessage("# Done updating... \n" + "# Please check updated data source and apply changes if necessary, otherwise, please proceed. \n" );
			        }else {
					    System.out.println("# Done updating... \n" + "# Please check updated data source and apply changes if necessary, otherwise, please execute again.");
			        }

		        	
		        }else {
		        	
			        if(getUIMode()) {
			        	SetErrorMessage("# Data Source is updated. \n");
			        }else {
			        	System.out.println("# Data Source is updated.");
			        }
		        	IsDSUpdated = true;
		        	
		        }
		        MarWorkbook.close();
		        
			}catch(Exception e)
			{
				e.printStackTrace();

				if(getUIMode()) {
		        	SetErrorMessage("# FATAL ERROR!, You are required to update your template, Please access the following link for the STEPS. \n https://myoffice.accenture.com/:w:/r/personal/leomar_a_pitogo_accenture_com/Documents/20.4%20QA%20Release%20-%20Internal%20Testers/MarsWTA/Steps%20for%20Updating%20Template.docx?d=wd6c6f54721de4fd2a892c16ecb073b06&csf=1&e=iyB3SH \n\n"
							+ "# Once template is updated based on the provided STEPS, you can now perform execution. \n# The essence of the update is to SYNC your local template for any updates found from REPOSITORY. \n\n# If you still encountering any issue/s after template updating, please contact your Automation Lead. \n");
		        }else {
					System.out.println("# FATAL ERROR!, You are required to update your template, Please access the following link for the STEPS. \n https://myoffice.accenture.com/:w:/r/personal/leomar_a_pitogo_accenture_com/Documents/20.4%20QA%20Release%20-%20Internal%20Testers/MarsWTA/Steps%20for%20Updating%20Template.docx?d=wd6c6f54721de4fd2a892c16ecb073b06&csf=1&e=iyB3SH \n\n"
							+ "# Once template is updated based on the provided STEPS, you can now perform execution. \n# The essence of the update is to SYNC your local template for any updates found from REPOSITORY. \n\n# If you still encountering any issue/s after template updating, please contact your Automation Lead.");

		        }
			}
			
			return IsDSUpdated;
		}
	 
		public static String[] Commands()
		{
			String[] Commands = {"Open","AlertTextEqual","AttributeValEqual","AttributeValNotEqual",
					"CheckAllImagesAvail","CheckAvail","CheckDropdownVal","CheckEnable",
					"CheckEnableDropDownItem","CheckSelect","Click","CSSValContains",
					"CSSValEqual","DoubleClick","Hover","HoverClick","JSONEqual","JSONContains",
					"JSONNotEqual","LabelContains","LabelEqual","LoginACNFederation",
					"NavigatePage","NewTab","RightClick","ScrollHorizontal","ScrollVertical",
					"Select","SetText","SetTextEnter","SetVerSecurityCode","StoreValJSON(Service)",
					"StoreValToken(Service)","SwitchFrame","SwitchTab","TextContains","TextEqual",
					"TextNotContains","TextNotEqual","TitleEqual","TooltipTextEqual","TooltipTextNotEqual",
					"WaitUntilVisible","WaitUntilInvisible","SetKey","DPSelectDay","PageContains","SetAttribute","Freeze","RepeatSteps",
					"CheckNumberOfElements","SetLabel","CSSValNotEqual","LabelNotEqual","LabelNotContains","CheckDuration","CheckFile","RepeatExternalSteps","RepeatInternalSteps","ConnectSQLServer(Database)",
					"StoreValResult(Database)","ResultValEqual","ResultValContains","CheckWebAccessibility"};
				
			return Commands;
		}
		
		public static String[] FindBy()
		{
			String[] FindBy = {"","id","xpath","partialLinkText","tagname(for img only)","POST","GET","PUT"};
			
			return FindBy;
		}
		
		
		public void ReportINI()
		{
			String nMe = "";
			try {
				
				nMe = FileName.replace(".xlsx", "");
			}catch(Exception e)
			{
				nMe = "UnName";
				e.printStackTrace();
			}
			
			ExtentReportPath = GetFParentName() + "\\AutomationLOG\\AutomationReport_" + nMe + "_" + String.valueOf(new SimpleDateFormat("MM-dd-yyyy").format(new Date())) + ".html";
			report = new ExtentReports(ExtentReportPath);
			//test = report.startTest(nMe);
		}
		
		
		public void addScenario(String id) {
			
			String scenarioName ="";
			if(TestConditions.get(id)!=null) {
				scenarioName = TestConditions.get(id);
			}else {
				scenarioName = "Scenario: " + id;
			}
			
			if(!id.equals("0")) {
				test = report.startTest(scenarioName);
			}
			
		}
		
		public void SetReportStatus(String Status, String Description, String expected, String actual) throws IOException
		{
				if(Status.equalsIgnoreCase("Pass"))
				{
					test.log(LogStatus.PASS, "Test Passed - " + Description);
				}else if(Status.equalsIgnoreCase("Fail"))
				{
					if(MainDriver!=null) {
						test.log(LogStatus.FAIL, test.addScreenCapture(capture()) + "Test Failed - " + Description + "<br>" + "Expected: \n" + expected + "<br> Actual: \n" + actual);
					}else {
						test.log(LogStatus.FAIL, "Test Failed - " + Description);
					}
					
				}else if(Status.equalsIgnoreCase("Skip" ))
				{
					test.log(LogStatus.SKIP, "Test Skipped -" + Description);
				}else 
				{
					test.log(LogStatus.INFO, "Test Info - " + Description);
				}
				
		}
		
		public String capture() throws IOException {
			
			String scnShot = ((TakesScreenshot) MainDriver).getScreenshotAs(OutputType.BASE64);
			return "data:image/jpg;base64, " + scnShot;
			
		}
		
		
		public void ReportClosing()
		{
			report.endTest(test);
			report.flush();
		}
}
