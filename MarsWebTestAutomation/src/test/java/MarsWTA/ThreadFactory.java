package MarsWTA;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JTable;


public class ThreadFactory extends MainExtension {
	private static int SelectedRow;
	private static int LastProcessRow;
	private static boolean isStop;
	private static boolean isPause;
	private static boolean isStopTimer;
	private static JTable table;
	private static String log;
	private static boolean withPreviousRun = false;
	private static boolean PauseifFail;
	private static boolean AutoPause;
	private static boolean forceStop;
	private static int PauseAt;
	//public MainExtension MainExt = new MainExtension();	
    
	public void enableForceStop() {
		forceStop = true;
	}
	
	public void SetVal(int FSelectedRow, JTable Ftable, boolean FPauseifFail, int pauseAt) {
		SetSelectedRow(FSelectedRow);
		SetLastProcessRow(FSelectedRow);
		PauseAt = pauseAt;
		table = Ftable;
		isStop = false;
		isPause = false;
		isStopTimer = false;
		log = "";
		PauseifFail = FPauseifFail;
		AutoPause = false;
		forceStop = false;
		// clearing system Hash map
		ClearSumRegStatus();
		
		// clear log
		SetErrorMessage("Empty()");
	}
	
	public void SetAutoPause(boolean flag) {
		AutoPause = flag;
	}
	
	public boolean GetAutoPause() {
		return AutoPause;
	}
	
	public void SetPauseAt(int RowNum) {
		PauseAt = RowNum;
	}
	
	public int GetPauseAt() {
		return PauseAt;
	}
	
	Thread thread = new Thread(){
		public void run() {

			try {
					setWithPreviousRun(true);
					SummaryStat = true;
				    String NextTestID;
					String TextKey= "";
					String FieldKey="";	
					int Row; 
					
					while(!isStop) {

						Row = GetSelectedRow();
						

						if(!getPause()) {
							
							if(Row<table.getRowCount()) {
								
								SetLastProcessRow(Row);
								
								TextKey = ExtractKeyVal(table.getValueAt(Row,4).toString());
								FieldKey = ExtractKeyVal(table.getValueAt(Row, 6).toString());
			
								
									try {
										Print("Details", String.valueOf(Row+2), table.getValueAt(Row, 1).toString(),table.getValueAt(Row, 3).toString(),TextKey,table.getValueAt(Row, 5).toString(),FieldKey,ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(Row, 8).toString())),ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(Row, 9).toString())),ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(Row, 10).toString())));					
										log = GetErrorMessage(); 
										
										if((Row+1)==TestId.size())//Last Row
										{
											NextTestID = "0";
										}else{
											NextTestID = TestId.get(String.valueOf(Row+2)); //row+1 = current index & row+2 = next index
										}
										
										
										//System.out.println("SKIP: " + table.getValueAt(Row, 8).toString());
										if(!ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(Row, 8).toString())).equalsIgnoreCase("Y")) {
											if(!GetSKIPNextFlag()) {
												
												SetCurrentHashMapKey(String.valueOf(Row+1));
												SetStartDate(Row+1);
												ExecuteCommand(table.getValueAt(Row, 3).toString(),TextKey,table.getValueAt(Row, 5).toString(),FieldKey,ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(Row, 8).toString())), ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(Row, 9).toString())), String.valueOf(Row+1));
												SetEndDate(Row+1);
												
												Print("Status","","","","","","","","","");
												log = GetErrorMessage();
												RegStatus.put(String.valueOf(Row+1), GetStat());
												SetDuration(String.valueOf(Row+1));
												
												//if(!(PauseifFail && GetStat().equalsIgnoreCase("fail"))) { // If status is fail and "pause if fail" is FALSE									
												TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(Row+2) + "_" + TestId.get(String.valueOf(Row+1)) + "_" +  GetStat() + ".jpg");
												SetReportStatus(GetStat(), "[Row: " + String.valueOf(Row+2) + "] - " + Description.get(String.valueOf(Row+1)) + " [" + Duration.get(String.valueOf(Row+1)) + "]", TextKey, ActualResult);											
												CheckSummaryStatus(TestId.get(String.valueOf(Row+1)),NextTestID,GetStat()); // check if test condition status will be updated													

												//}
												
												//this.GetStat() StopAllIfFail
												
												if (table.getValueAt(Row, 10).toString().equalsIgnoreCase("True") && GetStat().equalsIgnoreCase("Fail"))
												{	
												    
												    UpdatingEXCEL(GetFName());
												    TimeUnit.SECONDS.sleep(3);

												    /*if(Main.lstFile.size() == 1) {
													    OpenExcel();
												    }*/

												    ReportClosing();
												    QuitAllWebDrivers();
												    Print("StopAllFail","","","","","","","","","");
												    
												    /*if(Main.lstFile.size() > 1) {
												    	break;
												    }else {
												    	System.exit(0);
												    }*/
												    
												    isPause(true);
												    SetIsStopTimer(true);
												    
												}
												
											}else { // if SkipNIP = true
												Stat = "SKIP";
												RegStatus.put(String.valueOf(Row+1), "SKIP"); // Skip by Rules
												CheckSummaryStatus(TestId.get(String.valueOf(Row+1)),NextTestID,GetStat());
												SetSKIPNextFlag(false);// Reset into False
												TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(Row+2) + "_" + TestId.get(String.valueOf(Row+1)) + "_" +  "SKIP" + ".jpg");
												SetReportStatus("SKIP", "[Row: " + String.valueOf(Row+2) + "] - " + Description.get(String.valueOf(Row+1)) + " [00.000]", TextKey, "");
												Print("Skip","","","","","","","","","");
												//System.out.println( "Status: " + RegStatus.get(String.valueOf(Row+1)));
											}
											
										}else { 
												Stat = "SKIP";
												RegStatus.put(String.valueOf(Row+1), "SKIP"); // Skip by Manual
												CheckSummaryStatus(TestId.get(String.valueOf(Row+1)),NextTestID,Stat);
												TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(Row+2) + "_" + TestId.get(String.valueOf(Row+1)) + "_" +  "SKIP" + ".jpg");
												SetReportStatus("SKIP", "[Row: " + String.valueOf(Row+2) + "] - " + Description.get(String.valueOf(Row+1)) + " [00.000]", TextKey, "");
												Print("Skip","","","","","","","","","");
											
										}
										
									}catch(Exception e) { // try catch handling
									   
										try {
											UpdatingEXCEL(GetFName());
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									    
										TimeUnit.SECONDS.sleep(3);
									   
									    /*if(Main.lstFile.size() == 1) {
										    OpenExcel();		    	
									    }*/
									    
									    ReportClosing();
							        	e.printStackTrace();

									}
							
								Row++;
								SetSelectedRow(Row);
								Thread.sleep(500);
								
								if(forceStop) { // triggered by STOP button in UIMode
									isPause(true);
								    SetIsStopTimer(true);
								    ForceStop();
								    
								}
								
							}else {
								
								// Reading J table is completed
								
							    // temporary
								//System.out.println(RegStatus);
								
							    Print("Finishing1","","","","","","","","","");
							    TimeUnit.SECONDS.sleep(3);
							    
							    try {
									UpdatingEXCEL(GetFName());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							    
							    /*if(Main.lstFile.size() == 1) {
								    OpenExcel();		    	
							    }*/
							    
							    ReportClosing();
							    QuitAllWebDrivers();
							    Print("Finishing2","","","","","","","","","");
							    
							    try {
									WriteLog(GetErrorMessage());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							    
							    
							    isPause(true);
							    SetIsStopTimer(true);
							    openDashboard();
								//break; // end the thread
							}
							
							if(!table.getValueAt(Row-1, 3).toString().equalsIgnoreCase("CheckDuration")) { // will only apply auto pause when command is not "CheckDuration"
								if(PauseifFail && GetStat().equalsIgnoreCase("fail")) { // If status is fail and configuration pause if fail is true
									isPause(true);
									SetAutoPause(true);
									SetSelectedRow(GetLastProcessRow());
								}
							}

							
							if(GetPauseAt()!=0) {
								if(!getPause() && GetPauseAt()==GetLastProcessRow()) { // if pause is false and defined get pause row is equal to last process row, then do pause
									isPause(true);
									SetAutoPause(true);
									SetSelectedRow(GetLastProcessRow());
									//System.out.println(!getPause() + "|" + GetPauseAt() + "|" + GetLastProcessRow());
									//System.out.println("2nd role triggered here");
								}
							}

							
							
						}else{ //isPause
							Thread.sleep(5000);
						}
						
						
					}// isStop
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	};
	
	
	public void ForceStop() {
		try {
		    UpdatingEXCEL(GetFName());
		    TimeUnit.SECONDS.sleep(3);
		    ReportClosing();
		    QuitAllWebDrivers();
		    Print("StopAllFail","","","","","","","","","");
		    
		    try {
				WriteLog(GetErrorMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		    
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
		openDashboard();
	}
	
	
	public void openDashboard() {
		try {
			if(UIMode.openDashboard) {
				File htmlFile = new File(ExtentReportPath);
				Desktop.getDesktop().browse(htmlFile.toURI());
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
	}
	
	public void SetLastProcessRow(int row) {
		LastProcessRow = row;
	}

	public int GetLastProcessRow() {
		return LastProcessRow;
	}
	
	public void SetSelectedRow(int row) {
		SelectedRow = row;
	}
	
	public int GetSelectedRow() {
		return SelectedRow;
	}
	
	public void isStop(boolean Flag) {
		isStop = Flag;
	}
	
	public boolean getStop() {
		return isStop;
	}
	
	
	public String GetLog() {
		return log;
	}
	
	public void SetLog(String Log) {
		log = Log;
	}

	public void isPause(boolean Flag) {
		isPause = Flag;
	}
	
	public boolean getPause() {
		return isPause;
	}
	
	public String ConvertYNtoVal(Boolean Flag) {
		String YN = "N";
		if(Flag) {
			YN="Y";
		}
	return YN;	
	}
	
	public static void SetFTable(JTable FTable) {
		table = FTable;
	}
	
	public void SetIsStopTimer(boolean Flag) {
		isStopTimer = Flag;
	}
	
	public boolean GetIsStopTimer() {
		return isStopTimer;
	}
	
	public void setWithPreviousRun(Boolean Flag) {
		withPreviousRun = Flag;
	}
	
	public boolean withPreviousRun() {
		return withPreviousRun;
	}	
	
}