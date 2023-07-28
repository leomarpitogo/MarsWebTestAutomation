package MarsWTA;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.JTable;


public class ThreadFactoryFlexSteps extends MainExtension {
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
	private static int RunAtCounter;
	static HashMap<String, String> RunAtHolder = new HashMap<String, String>();	
	//public MainExtension MainExt = new MainExtension();	
    
	public void addRunAt(String selectedSteps) {
		
		if(RunAtHolder.size()!=0) {
			RunAtHolder.clear();
		}
		
		int i = 0;
		if(selectedSteps.contains(",")) {
			// 1-2,4,5-6
			String splittedSteps[] = selectedSteps.split(",");
			
			for(String x:splittedSteps) {
				i = ExtractHyphen(x,i);
			}
			
		}else {
			
			if(selectedSteps.contains("-")) {
				ExtractHyphen(selectedSteps,i);
			}else {
				ExtractHyphen(selectedSteps,i);
			}
			
		}
		
	}
	
	
	public int ExtractHyphen(String x, Integer i) {
		if(x.contains("-")){
			// extract hyphen
			
			String splitHyphen[] = x.trim().split("-");
			
			for(int a = Integer.parseInt(splitHyphen[0]);a<=Integer.parseInt(splitHyphen[1]);a++) {
				RunAtHolder.put(String.valueOf(i), String.valueOf(a-2));
				i++;
			}
		}else {
			//extractedSteps+=x;
			
			RunAtHolder.put(String.valueOf(i), String.valueOf(Integer.parseInt(x)-2));
			i++;
		}
		
		return i;
	}
	
	public void SetVal(String FSelectedRow, JTable Ftable, boolean FPauseifFail, int pauseAt) {
		
		
		addRunAt(FSelectedRow);
		SetSelectedRow(Integer.parseInt(RunAtHolder.get("0")));
		SetLastProcessRow(Integer.parseInt(RunAtHolder.get("0")));
		SetRunAtCounter(0);
		
		PauseAt = pauseAt;
		table = Ftable;
		isStop = false;
		isPause = false;
		isStopTimer = false;
		forceStop = false;
		log = "";
		PauseifFail = FPauseifFail;
		AutoPause = false;
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
	
	Thread threadFlexSteps = new Thread(){
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
							
							if(Row <= Integer.parseInt(RunAtHolder.get(String.valueOf(RunAtHolder.size()-1)))) {
								
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
											NextTestID = TestId.get(String.valueOf(Row+2));
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
												CheckSummaryStatus(TestId.get(String.valueOf(Row+1)),NextTestID,GetStat()); // check if test condition status will be updated
												
												//if(!(PauseifFail && GetStat().equalsIgnoreCase("fail"))) { // If status is fail and "pause if fail" is FALSE									
													TakeScreenShot(ScreenShotCurrentDir + "\\" + String.valueOf(Row+2) + "_" + TestId.get(String.valueOf(Row+1)) + "_" +  GetStat() + ".jpg");
													SetReportStatus(GetStat(), "[Row: " + String.valueOf(Row+2) + "] - " + Description.get(String.valueOf(Row+1)) + " [" + Duration.get(String.valueOf(Row+1)) + "]", TextKey, ActualResult);											
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
							
								if(RunAtHolder.get(String.valueOf(GetRunAtCounter()+1))!=null) {
									Row = Integer.parseInt(RunAtHolder.get(String.valueOf(GetRunAtCounter()+1)));	
								}else {
									Row++;
								}	
									
								//Row++;
								SetSelectedRow(Row);
								SetRunAtCounter(GetRunAtCounter()+1);
								Thread.sleep(500);
								
								if(forceStop) {
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
								//break; // end the thread
							}
							
							if(!table.getValueAt(Row-1, 3).toString().equalsIgnoreCase("CheckDuration")) { // will only apply auto pause when command is not "CheckDuration"
								if(PauseifFail && GetStat().equalsIgnoreCase("fail")) { // If status is fail and configuration pause if fail is true
									isPause(true);
									SetAutoPause(true);
									
									//SetSelectedRow(GetLastProcessRow());
									
									
									Row = Integer.parseInt(RunAtHolder.get(String.valueOf(GetRunAtCounter()-1)));	
									SetSelectedRow(Row);
									SetRunAtCounter(GetRunAtCounter()-1);
									
									
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
				e.printStackTrace();
			}
		    
		    
		}catch(Exception e) {
			e.printStackTrace();
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
	
	public void SetRunAtCounter(int row) {
		RunAtCounter = row;
	}
	
	public int GetRunAtCounter() {
		return RunAtCounter;
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