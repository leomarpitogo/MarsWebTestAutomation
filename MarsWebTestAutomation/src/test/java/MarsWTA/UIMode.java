package MarsWTA;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.filechooser.FileSystemView;

import java.awt.SystemColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.JTabbedPane;

public class UIMode extends JFrame {
	protected static final JFrame JFrame = null;
	public static List<String> lstFile = new ArrayList<>();	
	public JPanel contentPane;
	public JFrame frame;
	public  JTable table;
	public Boolean RunFlag;
	public static boolean openDashboard;
	public static boolean isHighlightElement;
	boolean flag = false;
	static boolean moveRow = false;
	public static boolean AutoPauseTriggered;
	public static JTable UIModeJTable;
	JButton btnStart = new JButton("Run");
	JButton btnPause = new JButton("Pause");
	JButton btnSave = new JButton("Update EXCEL");
	JButton btnBrowse = new JButton("Browse (xlsx)");
	JLabel lblPassNum = new JLabel("0");
	JLabel lblSkipNum = new JLabel("0");
	JLabel lblFailNum = new JLabel("0");
	JLabel lblDurationNum = new JLabel("0");
	public ThreadFactory Thread1 = new ThreadFactory();
	public ThreadFactoryFlexSteps Thread2 = new ThreadFactoryFlexSteps();
	public  MainExtension MainExt = new MainExtension();
	public JTextArea textArea = new JTextArea();	
	public Timer timer = null;
	private DefaultTableModel dataModel;
	private DefaultTableModel handlingModel;
	private DefaultTableModel scenarioModel;	
	JLabel lblSteps = new JLabel("Steps:");
	JLabel lblStepsNum = new JLabel("0/0");
	JButton btnDeleteStep = new JButton("Delete Step");
	JButton btnDataRepo = new JButton("DATA REPO");
	JButton btnAddStep = new JButton("Add Step");
	JButton btnUp = new JButton("Move Up");
	JButton btnDown = new JButton("Move Down");
	JButton btnSetPauseAt = new JButton("Pause At");
	JButton btnRestartAt = new JButton("Restart At");
	JButton btnJumpNextStep = new JButton("Jump Next Step");
	JLabel lblPauseAt = new JLabel("N/A");
	private JTable table_1;
	private JTable table_2;
	private JButton btnDeleteScenario = new JButton("Delete Scenario");
	private JButton btnAddScenario = new JButton("Add Scenario");
	public final JCheckBox chckbxclosebrowsers = new JCheckBox("Close All Browsers (Prior Each Scenario)");
	public JCheckBox chckbxOpenDashboard = new JCheckBox("Open Dashboard (If Done)");
	private final JCheckBox chckbxAutoPauseStep = new JCheckBox("Auto Pause Step (If Fail)");
	private final JCheckBox chckbxRunAt = new JCheckBox("Run At (Steps/Rows)");
	public JCheckBox chckbxhighlightelement = new JCheckBox("Highlight Failed Element");
	private JTextField textFieldRunAt;
	private final JCheckBox chckbxRunViaBackend = new JCheckBox("Run via backend");
	JCheckBox chckbxfiltersteps = new JCheckBox("Auto Filter Steps");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIMode frame = new UIMode();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */	
	
    public void setTextTitleValFromDataRepo(int row, String textVal, String fieldVal, JTable table) {

    	this.table = table;
    	
    	if(!textVal.isEmpty()) {
    		this.table.setValueAt(textVal, row, 4);
    	}
    	
    	if(!fieldVal.isEmpty()) {
    		this.table.setValueAt(fieldVal, row, 6);
    	}
    		
    }
		
	public static Boolean openDashboard() {
		return openDashboard;
	}
    
	public boolean ConvertYNtoBoolean(String YN) {
		Boolean Flag = false;
		if(YN.equalsIgnoreCase("Y")) {
			Flag = true;
		}
	return Flag;	
	}
	
	public String ConvertYNtoVal(Boolean Flag) {
		String YN = "N";
		if(Flag) {
			YN="Y";
		}
	return YN;	
	}
	
	/*
	public void UpdateRowFromDataRepo(int row, int col, String val) {
		//System.out.println(row + "|" + col + "|" + val);
		//table = GetUIModeJTable(); AMBOT
		dataModel = (DefaultTableModel) table.getModel();
		System.out.println("table.getRowCount(): " + dataModel.getRowCount());
		dataModel.setValueAt("fdsafdas", 3, 4);
	}
	*/
	
	public void ClearingRegTestStatus() throws IOException {

		MainExt.ClearStatusDateTimeRegression();
		MainExt.ClearStatusDateTimeTestConditions();


	}
	
	public void CreatingDirectories() {
		MainExt.CreateLogDirectory();
		MainExt.CreateScreenshortDir();
	}
	
	
	public void ImportingExcelAndDataSource() throws IOException {	
		MainExtension.CheckDataSource(MainExt.GetFName(),MainExtension.Commands(), MainExtension.FindBy());
		MainExt.ImportExcelData(MainExt.GetFName(), false, "Regression");
		MainExt.ImportRepository(MainExt.GetFName(), false, "Data Source");
		MainExt.importTestConditions(MainExt.GetFName(), false, "Test Conditions");
	}
	
	public void ValidatingReg() {
		MainExt.CheckRequiredColumns(MainExt.GetFName());
		MainExt.CheckValidValuePerCommand(MainExt.GetFName());
	}
	
	public String SystemColumns(String Row, String Column) {
		String Str = "";
		
		if(Column.equalsIgnoreCase("Start")) {
			if(MainExtension.Start.get(Row)!=null) {
				Str = MainExtension.Start.get(Row);
			}
		}
		
		if(Column.equalsIgnoreCase("End")) {
			if(MainExtension.End.get(Row)!=null) {
				Str = MainExtension.End.get(Row);
			}
		}

		if(Column.equalsIgnoreCase("Duration")) {
			if(MainExtension.Duration.get(Row)!=null) {
				Str = MainExtension.Duration.get(Row);
			}
		}

		
		return Str;
	}
	
	public void AddRow(HashMap<String,String> testid,HashMap<String,String> desc,HashMap<String,String> cmd,HashMap<String,String> text,HashMap<String,String> findby,HashMap<String,String> field,HashMap<String,String> skip, HashMap<String,String> skipnip,HashMap<String,String> stopaif, String[] commands, String[] findbys) {

		try {
			
			// initialization prior adding record to the table
			MainExt.SetErrorFreeFlag(true);
			ImportingExcelAndDataSource();
			ValidatingReg();
			RemoveAllRows();

			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			String Row= "";
			//"Row", "Description", "TestID", "Command", "Text", "Find By", "Field", "Status", "Skip", "SkipNIP", "StopAIF", "Started", "Ended", "Duration"
			for(int i=1; i<=testid.size();i++) {
				
				Row = String.valueOf(i);
				model.addRow(new Object[]{
					String.valueOf(i+1),
					desc.get(Row),
					testid.get(Row),
					cmd.get(Row),
					text.get(Row),
					findby.get(Row),
					field.get(Row),
					MainExtension.RegStatus.get(Row),
					ConvertYNtoBoolean(skip.get(Row)),
					ConvertYNtoBoolean(skipnip.get(Row)),
					ConvertYNtoBoolean(stopaif.get(Row)),
					SystemColumns(Row,"Start"),
					SystemColumns(Row,"End"),
					SystemColumns(Row,"Duration")
					});
				
				
				// set not editable columns			
				}		
			
			// adding items to dropdown type columns
			TableColumn commandColumn = table.getColumnModel().getColumn(3);
			TableColumn findByColumn = table.getColumnModel().getColumn(5);
			
			JComboBox commandCmbBox = new JComboBox();
			JComboBox findByCmbBox = new JComboBox();
			
			Arrays.sort(commands);
			for(String s: commands) {
				commandCmbBox.addItem(s);
			}
			
			Arrays.sort(findbys);
			for(String s: findbys) {
				findByCmbBox.addItem(s);
			}

			commandColumn.setCellEditor(new DefaultCellEditor(commandCmbBox));
			findByColumn.setCellEditor(new DefaultCellEditor(findByCmbBox));
					

			
			
			// focus on 1st row
			table.changeSelection(0, 0, false, false);
			
			this.setTitle("Test Automation (One-Stop-Shop) - " + MainExt.GetFName());
			
			if(table.getRowCount()!=0 && !btnPause.getText().equalsIgnoreCase("Resume")) { // all buttons will only be enabled if not running.
				UpdateAllbuttonsState(true);
			}
			
			if(btnPause.getText().equalsIgnoreCase("Resume")) { // update table in the thread factory
				ThreadFactory.SetFTable(table);
				updateStepsCounter();
			}else {
				// default counters
				Thread1.SetLastProcessRow(0);
				lblPassNum.setText("0");
				lblSkipNum.setText("0");
				lblFailNum.setText("0");
				lblDurationNum.setText("0");
				lblStepsNum.setText("0/" + table.getRowCount());	
			}
			
			// adding test scenarios table
			scenarioModel = (DefaultTableModel) table_2.getModel();
			for(String i: MainExtension.TestConditions.keySet()) {
				if(!MainExtension.TestConditions.get(i).isEmpty()) {
					
					scenarioModel.addRow(new Object[]{
							i,
							MainExtension.TestConditions.get(i),
							"",
							});
				}

			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
public void updateExcelTestCondition() {
		
		try {
				
				File file = new File(MainExt.GetFName());
		        FileInputStream inputStream = new FileInputStream(file);
		        Workbook MarWorkbook = null;
		        MarWorkbook = new XSSFWorkbook(inputStream);
		        Sheet MarSheet = MarWorkbook.getSheet("Test Conditions");
		   
	     		Row rowNum;
	     		Cell id = null, desc = null;
	     		int rowStart = 4;
	     		
	     		for(int i=0;i<table_2.getRowCount();i++) {
	     			
	     			
	     			if(MarSheet.getRow(rowStart+1)==null) { // created row and cell if exceeded 100 rows
	     				rowNum = MarSheet.createRow((rowStart+1));
	     				//for(int r = 0; r<=6; r++) {
	     					rowNum.createCell(0);
	     					rowNum.createCell(2);
	     				//}
	     			}else {
	     				rowNum = MarSheet.getRow(rowStart+1);
	     			}
	     			
	         		
	         		
	         		if(rowNum.getCell(0) ==null) {
	         			id = rowNum.createCell(0);
	         			desc = rowNum.createCell(2);
	         		}else {
		         		id = rowNum.getCell(0);
		         		desc = rowNum.getCell(2);	         			
	         		}
	        
	         		id.setCellValue(table_2.getValueAt(i, 0).toString());
	         		desc.setCellValue(table_2.getValueAt(i, 1).toString());

	         		rowStart++;
	     		}
		        
	     		
				    inputStream.close();
				    FileOutputStream outputStream = new FileOutputStream(file);
				    MarWorkbook.write(outputStream);
				    outputStream.close(); 
				    MarWorkbook.close();
					
				    MainExt.importTestConditions(MainExt.GetFName(), false, "Test Conditions");
		}catch(Exception e) {
			//textArea.setText(e.getMessage());
			e.printStackTrace();
		}

	}
	
	public void defaultObjectState() {
		btnStart.setText("Run");
		btnStart.setEnabled(true);
		btnPause.setText("Pause");
		btnPause.setEnabled(false);
		btnSave.setEnabled(true);
		btnBrowse.setEnabled(true);
		
		btnDeleteStep.setEnabled(true);
		btnDataRepo.setEnabled(true);
		btnAddStep.setEnabled(true);
		
		btnUp.setEnabled(true);
		btnDown.setEnabled(true);
		btnSetPauseAt.setEnabled(true);
		//btnRestartAt.setEnabled(true);
		
		chckbxAutoPauseStep.setEnabled(true);
		chckbxRunViaBackend.setEnabled(true);
		chckbxRunAt.setEnabled(true);
		chckbxclosebrowsers.setEnabled(true);
		chckbxOpenDashboard.setEnabled(true);
		chckbxhighlightelement.setEnabled(true);
		
		btnAddScenario.setEnabled(true);
		btnDeleteScenario.setEnabled(true);
	}
	
	public void UpdateAllbuttonsState(boolean Flag) {
		btnStart.setEnabled(Flag);
		//btnPause.setEnabled(Flag);
		btnSave.setEnabled(Flag);
		btnDeleteStep.setEnabled(Flag);
		btnDataRepo.setEnabled(Flag);
		btnAddStep.setEnabled(Flag);
		
		btnUp.setEnabled(Flag);
		btnDown.setEnabled(Flag);
		btnSetPauseAt.setEnabled(Flag);
		//btnRestartAt.setEnabled(Flag);
		//btnMarkAsFail.setEnabled(Flag);
	}
	
	public void UpdateStatusCounterFlexSteps() {
		
		try {
			
			int Pass = 0, Skip = 0, Fail = 0;
			float Duration = 0;
			
			for(int i=0;i<table.getRowCount();i++) {
				
				if(table.getValueAt(i, 7)!=null) {
					if(table.getValueAt(i, 7).toString().equalsIgnoreCase("Pass")) {
						Pass++;
					}
					
					if(table.getValueAt(i, 7).toString().equalsIgnoreCase("Skip")) {
						Skip++;
					}
					
					if(table.getValueAt(i, 7).toString().equalsIgnoreCase("Fail")) {
						Fail++;
					}
				}

				
				if(table.getValueAt(i, 13)!=null) {
					if(!table.getValueAt(i, 13).toString().isEmpty()) {
						Duration += Float.parseFloat(table.getValueAt(i, 13).toString());
					}
				}
				
			}
			
			lblPassNum.setText(String.valueOf(Pass));
			lblSkipNum.setText(String.valueOf(Skip));
			lblFailNum.setText(String.valueOf(Fail));
			lblStepsNum.setText(String.valueOf(Thread2.GetLastProcessRow() + 1) + "/" + table.getRowCount());
			//lblDurationNum.setText(String.valueOf(Duration));
			lblDurationNum.setText(convertDuration(Duration));
		}catch(Exception e) {
			e.printStackTrace();
		}

		
	}
	
	public void UpdateStatusCounter() {
		
		try {
			
			int Pass = 0, Skip = 0, Fail = 0;
			float Duration = 0;
			
			for(int i=0;i<table.getRowCount();i++) {
				
				if(table.getValueAt(i, 7)!=null) {
					if(table.getValueAt(i, 7).toString().equalsIgnoreCase("Pass")) {
						Pass++;
					}
					
					if(table.getValueAt(i, 7).toString().equalsIgnoreCase("Skip")) {
						Skip++;
					}
					
					if(table.getValueAt(i, 7).toString().equalsIgnoreCase("Fail")) {
						Fail++;
					}
				}

				
				if(table.getValueAt(i, 13)!=null) {
					if(!table.getValueAt(i, 13).toString().isEmpty()) {
						Duration += Float.parseFloat(table.getValueAt(i, 13).toString());
					}
				}
				
			}
			
			lblPassNum.setText(String.valueOf(Pass));
			lblSkipNum.setText(String.valueOf(Skip));
			lblFailNum.setText(String.valueOf(Fail));
			lblStepsNum.setText(String.valueOf(Thread1.GetLastProcessRow() + 1) + "/" + table.getRowCount());
			//lblDurationNum.setText(String.valueOf(Duration));
			lblDurationNum.setText(convertDuration(Duration));
		}catch(Exception e) {
			e.printStackTrace();
		}

		
	}
	
	public String convertDuration(float duration) {
    	return String.valueOf((Math.round(duration)/3600)) + ":" + String.valueOf((Math.round(duration)%3600)/60) + ":" + String.valueOf(Math.round(duration)%60);
	}
	
	public void updateExcel() {
		
		try {
			
			if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
				
				File file = new File(MainExt.GetFName());
		        FileInputStream inputStream = new FileInputStream(file);
		        Workbook MarWorkbook = null;
		        MarWorkbook = new XSSFWorkbook(inputStream);
		        Sheet RegSheet = MarWorkbook.getSheet("Regression");
		   
	     		Row rowNum = null;
	     		Cell Description = null, TestId = null, Command = null, Text = null, FindBy = null, Field = null, Skip = null, SkipNIF = null, SkipAIF = null;
	     	
	     		for(int i=0;i<table.getRowCount();i++) {
	     			
	     			
	     			//System.out.println("Row #: " + (i+1) + " Value: " + RegSheet.getRow((i+1)) + " Last row count is: " + RegSheet.getLastRowNum());
	     			if(RegSheet.getRow((i+1))==null) {
	     				//rowNum.createCell(i+1);
	     				//rowNum = RegSheet.getLastRowNum();
	     				//int lstrow = RegSheet.getLastRowNum()-1;
	     				//RegSheet.shiftRows(1, lstrow, 1);
	     				//RegSheet.createRow(i);
	     				
	     				
	     				rowNum = RegSheet.createRow((i+1));
	     				for(int r = 0; r<=12; r++) {
	     					rowNum.createCell(r);
	     				}

	     				//rowNum = RegSheet.getRow(1);
	     			}else {
	     				rowNum = RegSheet.getRow(i+1);
	     			}
	         		
	         		
	         		Description = rowNum.getCell(0);
	         		TestId = rowNum.getCell(1);
		        	Command = rowNum.getCell(2);
		       		Text = rowNum.getCell(3);
		       		FindBy = rowNum.getCell(4);
		       		Field = rowNum.getCell(5);
		       		Skip = rowNum.getCell(6);
		       		SkipNIF = rowNum.getCell(7);
		       		SkipAIF = rowNum.getCell(8);	         			
	         		

	        		//	"Row", "Description", "TestID", "Command", "Text", "Find By", "Field", "Status", "Skip", "SkipNIP", "StopAIF", "Started", "Ended", "Duration"
	        		Description.setCellValue(table.getValueAt(i, 1).toString());
	        		TestId.setCellValue(table.getValueAt(i, 2).toString());
	        		Command.setCellValue(table.getValueAt(i, 3).toString());
	        		Text.setCellValue(table.getValueAt(i, 4).toString());
	        		FindBy.setCellValue(table.getValueAt(i, 5).toString());
	        		Field.setCellValue(table.getValueAt(i, 6).toString());
	        		Skip.setCellValue(ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(i, 8).toString())));
	        		SkipNIF.setCellValue(ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(i, 9).toString())));
	        		SkipAIF.setCellValue(ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(i, 10).toString())));
	        		
	     		}
		        
				    inputStream.close();
				    FileOutputStream outputStream = new FileOutputStream(file);
				    MarWorkbook.write(outputStream);
				    outputStream.close(); 
				    MarWorkbook.close();
				    
				    JOptionPane.showMessageDialog(frame, "Update Successfully!");
				    MainExt.ImportExcelData(MainExt.GetFName(), false, "Regression");	
				    updateStepsCounter();
				    MainExtension.SetErrorMessage("Empty()");
				    ValidatingReg();
				    
				    
			}else {
					JOptionPane.showMessageDialog(frame, "Excel File is currently Open. Please close and try again.");
			}
			
		}catch(Exception e) {
			//textArea.setText(e.getMessage());
			e.printStackTrace();
		}

	}
	
	
	public void updateStepsCounter() {
		
		Matcher m = Pattern.compile("([0-9]+)/[0-9]+").matcher(lblStepsNum.getText());
		
		if(m.find()) {
			lblStepsNum.setText(m.group(1) + "/" + table.getRowCount());
		}else {
			lblStepsNum.setText("0/" + table.getRowCount());			
		}
		
	}
	
	public void updateStatusStartEndDurationColumns() {
		
		// Status, start, end, duration
		String key = String.valueOf(Thread1.GetLastProcessRow()+1);
		String Status = MainExtension.RegStatus.get(key);
		String Start = MainExtension.Start.get(key);
		String End = MainExtension.End.get(key);
		String Duration = MainExtension.Duration.get(key); 
		
		if(Status!=null) {
			table.setValueAt(Status, Thread1.GetLastProcessRow(), 7);		
		}

		
		if(Duration!=null) {
			table.setValueAt(Start, Thread1.GetLastProcessRow(), 11);
			table.setValueAt(End, Thread1.GetLastProcessRow(), 12);
			table.setValueAt(Duration, Thread1.GetLastProcessRow(), 13);
			
			if(MainExt.GetSKIPNextFlag() && Status.equalsIgnoreCase("Pass")) { // skip next if SKIP Next If pass if TRUE in the current step
				table.setValueAt("SKIP", (Thread1.GetLastProcessRow() + 1), 7);
			}
			
		}
		
		try {
			// Update status of scenario table
			String scenarioId = table.getValueAt(table.getSelectedRow(), 2).toString();
			if(MainExtension.SumStatus.get(scenarioId)!=null) {
				table_2.setValueAt(MainExtension.SumStatus.get(scenarioId), Integer.parseInt(scenarioId)-1, 2);
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
		

		
	}
	
	public void updateStatusStartEndDurationColumnsFlexSteps() {
		
		// Status, start, end, duration
		String key = String.valueOf(Thread2.GetLastProcessRow()+1);
		String Status = MainExtension.RegStatus.get(key);
		String Start = MainExtension.Start.get(key);
		String End = MainExtension.End.get(key);
		String Duration = MainExtension.Duration.get(key); 
		
		if(Status!=null) {
			table.setValueAt(Status, Thread2.GetLastProcessRow(), 7);		
		}

		
		if(Duration!=null) {
			table.setValueAt(Start, Thread2.GetLastProcessRow(), 11);
			table.setValueAt(End, Thread2.GetLastProcessRow(), 12);
			table.setValueAt(Duration, Thread2.GetLastProcessRow(), 13);
			
			if(MainExt.GetSKIPNextFlag() && Status.equalsIgnoreCase("Pass")) { // skip next if SKIP Next If pass if TRUE in the current step
				table.setValueAt("SKIP", (Thread2.GetLastProcessRow() + 1), 7);
			}
			
		}
		
	}
	
	public void ClearSystemColumns() {
		
		for(int i=0;i<table.getRowCount();i++) {
			table.setValueAt("", i, 7);
			table.setValueAt("", i, 11);
			table.setValueAt("", i, 12);
			table.setValueAt("", i, 13);
			
		}
		
		for(int i=0;i<table_2.getRowCount();i++) {
			table_2.setValueAt("", i, 2);
		}
		
		
		if(MainExtension.SumStatus.size()!=0) {
			MainExtension.SumStatus.clear();		
		}
		
		if(MainExtension.RegStatus.size()!=0) {
			MainExtension.RegStatus.clear();		
		}

		if(MainExtension.Start.size()!=0) {
			MainExtension.Start.clear();		
		}
		if(MainExtension.End.size()!=0) {
			MainExtension.End.clear();		
		}
		if(MainExtension.Duration.size()!=0) {
			MainExtension.Duration.clear();		
		}
		
		if(MainExtension.TempText.size()!=0) {
			MainExtension.TempText.clear();
		}
		
		if(MainExtension.TempField.size()!=0) {
			MainExtension.TempField.clear();
		}
	}
	
	public void ObjectRunningState() {
		chckbxAutoPauseStep.setEnabled(false);
		chckbxRunViaBackend.setEnabled(false);
		chckbxRunAt.setEnabled(false);
		textFieldRunAt.setEnabled(false);
		chckbxclosebrowsers.setEnabled(false);
		chckbxOpenDashboard.setEnabled(false);
		chckbxhighlightelement.setEnabled(false);
		btnBrowse.setEnabled(false);
		btnSave.setEnabled(false);
		btnAddScenario.setEnabled(false);
		btnDeleteScenario.setEnabled(false);
		
		
		btnDeleteStep.setEnabled(false);
		btnDataRepo.setEnabled(false);
		btnAddStep.setEnabled(false);
		btnUp.setEnabled(false);
		btnDown.setEnabled(false);
		
		btnUp.setEnabled(false);
		btnDown.setEnabled(false);
		btnSetPauseAt.setEnabled(false);
		//btnRestartAt.setEnabled(false);
		
		lblPassNum.setText("0");
		lblSkipNum.setText("0");
		lblFailNum.setText("0");
		lblStepsNum.setText("0/0");
		lblDurationNum.setText("0");
	}
	
	public void RemoveAllRows() {
		dataModel = (DefaultTableModel) table.getModel();
		int rowCount = table.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			dataModel.removeRow(i);
		}
		
		scenarioModel = (DefaultTableModel) table_2.getModel();
		int rowCount1 = table_2.getRowCount();
		for (int i = rowCount1 - 1; i >= 0; i--) {
			scenarioModel.removeRow(i);
		}
	}
	

	public void SelectFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setMultiSelectionEnabled(true);
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {

			File[] files = jfc.getSelectedFiles();

            for(File file: files){
                lstFile.add(file.getAbsolutePath() + "|" + file.getParent() + "|" + file.getName());
            }
		}
	}
	
	public boolean SubmitFile() throws InterruptedException, IOException{
		boolean flag = false;
		if(!lstFile.isEmpty()) {
			for(String file: lstFile) {
				
				String[] subFile = file.split("\\|");
			    if(Main.argsEmpty(subFile[0]))
				{
			    	MainExt.SetFName(subFile[0], subFile[1], subFile[2]);
			    	flag = true;
				}else{
					System.out.println("# Invalid File/Execution");
					TimeUnit.SECONDS.sleep(5);
				}
			}
			
			lstFile.clear();			
			
		}
		
		return flag;
	}
	
	/*public boolean updateKeys() {
		boolean Flag = false;
		try {
			MainExtension.SetErrorMessage("Empty()");		
			if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
				MainExt.ImportRepository(MainExt.GetFName(), false);
				Flag = true;
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
	
		return Flag;
	}	*/
		
	
	public void RemoveRowFromExcel(int SelectedRow) {

		try {
			//System.out.println(SelectedRow);
			File file = new File(MainExt.GetFName());
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook MarWorkbook = null;
	        MarWorkbook = new XSSFWorkbook(inputStream);
	        Sheet RegSheet = MarWorkbook.getSheet("Regression");
	        
	        //Cell key = null, keyValue = null;
	        Row rowNum;		
	        rowNum = RegSheet.getRow(SelectedRow-1);
	        RegSheet.removeRow(rowNum);
	        
	        
		    inputStream.close();
		    FileOutputStream outputStream = new FileOutputStream(file);
		    MarWorkbook.write(outputStream);
		    outputStream.close(); 
		    MarWorkbook.close();
		    
		    
		    // update repository
		    MainExt.ImportRepository(MainExt.GetFName(), false, "Data Source");
		    
		}catch(Exception e) {
			e.printStackTrace();
		}
	    
	}
	
	
	public void RemoveRowFromExcelTest(int SelectedRow) {
		try {
			File file = new File(MainExt.GetFName());
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook MarWorkbook = null;
	        MarWorkbook = new XSSFWorkbook(inputStream);
	        Sheet RegSheet = MarWorkbook.getSheet("Regression");

	        reArrangeExcelValues(SelectedRow, RegSheet.getLastRowNum(), RegSheet);

			
		    inputStream.close();
		    FileOutputStream outputStream = new FileOutputStream(file);
		    MarWorkbook.write(outputStream);
		    outputStream.close(); 
		    MarWorkbook.close();
		    
		    
		    updateStepsCounter();
		    
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//https://www.javatpoint.com/apache-poi-styling-shapes
	}

	
	public void reArrangeExcelValues(int deleteRow, int lastRow, Sheet sheet) {
		
 		Row rowNum = null;
 		Cell Description = null, TestId = null, Command = null, Text = null, FindBy = null, Field = null, Skip = null, SkipNIF = null, SkipAIF = null, Status = null, Start = null, End = null, Duration = null ;

		int ExcelRow = 1;
 		for(int i = 0; i<table.getRowCount(); i++) {
	        
			if(i!=deleteRow) {
				rowNum = sheet.getRow(ExcelRow);
				
	     		Description = rowNum.getCell(0);
	     		TestId = rowNum.getCell(1);
	        	Command = rowNum.getCell(2);
	       		Text = rowNum.getCell(3);
	       		FindBy = rowNum.getCell(4);
	       		Field = rowNum.getCell(5);
	       		Skip = rowNum.getCell(6);
	       		SkipNIF = rowNum.getCell(7);
	       		SkipAIF = rowNum.getCell(8);
	       		
	       		if(rowNum.getCell(9)== null) {
	       			rowNum.createCell(9);
	       		}

	       		if(rowNum.getCell(10)== null) {
	       			rowNum.createCell(10);
	       		}

	       		
	       		if(rowNum.getCell(11)== null) {
	       			rowNum.createCell(11);
	       		}

	       		
	       		if(rowNum.getCell(12)== null) {
	       			rowNum.createCell(12);
	       		}

	       		Status = rowNum.getCell(9);
	       		Start = rowNum.getCell(10);
	       		End = rowNum.getCell(11);
	       		Duration = rowNum.getCell(12);
	       		
	    		Description.setCellValue(table.getValueAt(i, 1).toString());
	    		TestId.setCellValue(table.getValueAt(i, 2).toString());
	    		Command.setCellValue(table.getValueAt(i, 3).toString());
	    		Text.setCellValue(table.getValueAt(i, 4).toString());
	    		FindBy.setCellValue(table.getValueAt(i, 5).toString());
	    		Field.setCellValue(table.getValueAt(i, 6).toString());
	    		Skip.setCellValue(ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(i, 8).toString())));
	    		SkipNIF.setCellValue(ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(i, 9).toString())));
	    		SkipAIF.setCellValue(ConvertYNtoVal(Boolean.parseBoolean(table.getValueAt(i, 10).toString())));
	    		Status.setCellValue(table.getValueAt(i, 6).toString());
	    		Start.setCellValue(table.getValueAt(i, 11).toString());
	    		End.setCellValue(table.getValueAt(i, 12).toString());
	    		Duration.setCellValue(table.getValueAt(i, 13).toString());
	    		
				ExcelRow++;
			}


		}
		
        Row removingRow = sheet.getRow(lastRow);
        sheet.removeRow(removingRow);
	}
	
	public int GetPauseAt() {
		int row = 0;
		if(!lblPauseAt.getText().isEmpty() && !lblPauseAt.getText().equalsIgnoreCase("N/A")) {
			row = Integer.parseInt(lblPauseAt.getText())-2;
		}
		
		return row;
	}
	
	
	public void ShowDataRepo() {
		new UIDataRepo(this,table);
		
	}
	
	
	public void ClearSelectedTableStatus(int startRow, int endRow) {
		for(int i=startRow;i<endRow; i++) {
			table.setValueAt("", i, 11);	
			table.setValueAt("", i, 12);	
			table.setValueAt("", i, 13);
			table.setValueAt("", i, 7);
	
		}

	}
	
	public void pauseBtnFlexSteps() {
		
	if(textFieldRunAt.getText().isEmpty()) {
		JOptionPane.showMessageDialog(frame, "Run At field should have a value when Run At checkbox is checked.");
	}else {
		if(btnPause.getText().equalsIgnoreCase("Pause")) {
			Thread2.isPause(true);
			btnStart.setEnabled(false);
			btnPause.setText("Resume");
			
			btnDeleteStep.setEnabled(true);
			btnDataRepo.setEnabled(true);
			btnAddStep.setEnabled(true);
			btnSave.setEnabled(true);
			
			btnUp.setEnabled(true);
			btnDown.setEnabled(true);
			btnSetPauseAt.setEnabled(true);
			btnRestartAt.setEnabled(true);
			
		}else {
			//"Resume");
			if((table.getRowCount()==MainExtension.TestId.size() && !moveRow)) { // check if there's any added or move step/s
				
				if(Thread2.GetAutoPause() && AutoPauseTriggered) {
					Thread2.SetAutoPause(false);
					AutoPauseTriggered = false;
				}
							
				Thread2.isPause(false);
				btnStart.setEnabled(true);
				btnPause.setText("Pause");	
				
				btnDeleteStep.setEnabled(false);
				btnDataRepo.setEnabled(false);
				btnAddStep.setEnabled(false);
				btnSave.setEnabled(false);
				
				btnUp.setEnabled(false);
				btnDown.setEnabled(false);
				btnSetPauseAt.setEnabled(false);
				btnRestartAt.setEnabled(false);
				
				
				if(GetPauseAt()==Thread2.GetLastProcessRow()) { // reset pause at if defined pause already same with current process row
					lblPauseAt.setText("N/A");
					Thread2.SetPauseAt(GetPauseAt());
				}else {
					Thread2.SetPauseAt(GetPauseAt());
				}

				
			}else {
				JOptionPane.showMessageDialog(frame, "There were step/s changes which wasn't updated yet into EXCEL. Please click first UPDATE EXCEL");
			}
		}
	}	
	

		
		
		
		
		
	}
	
	public void timerFlexSteps() {
	  	updateStatusStartEndDurationColumnsFlexSteps();
		UpdateStatusCounterFlexSteps(); 
		
		if(Thread2.GetIsStopTimer()) {
	
				defaultObjectState(); 
				textFieldRunAt.setEnabled(true);
			//timer.stop();
			
		}
	
		if(!Thread2.getPause() && btnStart.getText().equalsIgnoreCase("Stop")) { // if pause is false
			table.changeSelection(Thread2.GetLastProcessRow(), 0, false, false);
			textArea.setText(Thread2.GetLog());
			//System.out.println("here....");
		}
		
		if(Thread2.GetAutoPause() && !AutoPauseTriggered) {
			AutoPauseTriggered = true;
			Thread2.isPause(true);
			btnStart.setEnabled(false);
			btnPause.setText("Resume");
			
			btnSave.setEnabled(true);
			btnDeleteStep.setEnabled(true);
			btnDataRepo.setEnabled(true);
			btnAddStep.setEnabled(true);
			
			btnUp.setEnabled(true);
			btnDown.setEnabled(true);
			btnSetPauseAt.setEnabled(true);
			btnRestartAt.setEnabled(true);
			
		}
	}
	
	  
	public void filterHandling(String query) {
		
		try {
			TableRowSorter<DefaultTableModel> tr= new TableRowSorter<DefaultTableModel>(handlingModel);
			table_1.setRowSorter(tr);
			tr.setRowFilter(RowFilter.regexFilter(query.replaceAll("\\([^\\)]*\\)",""),0));
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void filterSteps(String query) {
		
		
		try {
			TableRowSorter<DefaultTableModel> tr= new TableRowSorter<DefaultTableModel>(dataModel);
			table.setRowSorter(tr);
			tr.setRowFilter(RowFilter.regexFilter(query,2));
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
	}
	
	public void importHandling() {
		String filePath = System.getProperty("java.library.path") + "\\handling.txt";
		File file = new File(filePath);
		
		try {
			BufferedReader br= new BufferedReader(new FileReader(file));
			String firstLine = br.readLine().trim();
			String[] columnsName = firstLine.split("\\_");
			handlingModel = (DefaultTableModel)table_1.getModel();
			handlingModel.setColumnIdentifiers(columnsName);
			
			Object[] tableLines = br.lines().toArray();
			
			for(int i=0 ; i< tableLines.length; i++) {
				String line = tableLines[i].toString().trim();
				String[] dataRow = line.split("/");
				handlingModel.addRow(dataRow);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
				
	}
	
	
	public UIMode() {
		setTitle("Test Automation (One-Stop-Shop)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1386, 729);
		//setExtendedState(MAXIMIZED_BOTH);
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//pack();
		//setSize(screenSize.width,screenSize.height);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaption);
		//panel.setBounds(10, 11, 1350, 501); 
		panel.setBounds(10, 11, 1350, 501); 
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(364, 37, 976, 412); 
		//scrollPane.setBounds(10, 37, screenSize.width - 300, 412); 
		panel.add(scrollPane);
		
		//System.out.println(scrollPane.getWidth());

		
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(table.getRowCount()!=0 && btnPause.getText().equalsIgnoreCase("Resume")) {
					if(!table.getValueAt(table.getSelectedRow(), 7).toString().isEmpty() && Thread1.GetSelectedRow()==table.getSelectedRow()) {
						btnJumpNextStep.setEnabled(true);
					}else {
						btnJumpNextStep.setEnabled(false);
					}		
				}

				filterHandling(table.getValueAt(table.getSelectedRow(), 3).toString());
				

			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
				try {
					
					// highlight scenario table
					table_2.changeSelection(Integer.parseInt(table.getValueAt(table.getSelectedRow(), 2).toString())-1, 0, false, false);

				}catch(Exception r) {
					//r.printStackTrace();
				}
				
			}
		});
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
 		table.setModel(new DefaultTableModel(
 			new Object[][] {
 			},
 			new String[] {
 				"Row", "Description", "ID", "Command", "Text", "Find By", "Field", "Status", "Skip", "SkipNIP", "StopAIF", "Started", "Ended", "Duration"
 			}
 		) {
 			Class[] columnTypes = new Class[] {
 				Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Boolean.class, Boolean.class, Boolean.class, Object.class, Object.class, Object.class
 			};
 			public Class getColumnClass(int columnIndex) {
 				return columnTypes[columnIndex];
 			}
 		});
 		table.getColumnModel().getColumn(0).setPreferredWidth(35);
 		table.getColumnModel().getColumn(1).setPreferredWidth(272);
 		table.getColumnModel().getColumn(2).setPreferredWidth(47);
 		table.getColumnModel().getColumn(3).setPreferredWidth(306);
 		table.getColumnModel().getColumn(4).setPreferredWidth(316);
 		table.getColumnModel().getColumn(5).setPreferredWidth(55);
 		table.getColumnModel().getColumn(6).setPreferredWidth(328);
 		table.getColumnModel().getColumn(7).setPreferredWidth(46);
 		table.getColumnModel().getColumn(8).setPreferredWidth(35);
 		table.getColumnModel().getColumn(9).setPreferredWidth(52);
 		table.getColumnModel().getColumn(10).setPreferredWidth(53);
 		table.getColumnModel().getColumn(11).setPreferredWidth(170);
 		table.getColumnModel().getColumn(12).setPreferredWidth(166);
 		table.getColumnModel().getColumn(13).setPreferredWidth(61);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(chckbxRunAt.isSelected() && textFieldRunAt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Run At field should have a value when Run At checkbox is checked.");
					
				}else {
					if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {

						if((table.getRowCount()==MainExtension.TestId.size() && !moveRow)) {
							if(MainExt.GetErrorFreeFlag()) {
								
								if(chckbxRunViaBackend.isSelected()) {
									try {
										MainExtension.SetUIMode(false);
										MainExt.AutomationEngine(MainExt.GetFName());
									}catch(Exception ex) {
										ex.printStackTrace();
									}
									
								}else {
									
									try {
										ClearingRegTestStatus(); // clearing excel statuses
									} catch (IOException e1) {
										e1.printStackTrace();
									} 
									
		
									if(btnStart.getText().equalsIgnoreCase("Run")) {	
										
										MainExt.ReportINI(); // extent report initialization
										MainExt.addScenario(table.getValueAt(0, 2).toString());
										CreatingDirectories(); // screenshot and log
										
										AutoPauseTriggered = false;
										
										ObjectRunningState();
										ClearSystemColumns();
										btnStart.setText("Stop");
										btnPause.setEnabled(true);
										openDashboard = chckbxOpenDashboard.isSelected();
										isHighlightElement = chckbxhighlightelement.isSelected();
										
										if(chckbxRunAt.isSelected()) { // Run the selected steps
												
											Thread2.SetVal(textFieldRunAt.getText().trim(), table, chckbxAutoPauseStep.isSelected(), GetPauseAt());
												
											if(!Thread2.withPreviousRun()) { // if does NOT have previous Run
												timer.start();
												Thread2.threadFlexSteps.start();
											}

										}else { // run the steps from the table
											
											Thread1.SetVal(0, table, chckbxAutoPauseStep.isSelected(), GetPauseAt());
											
											if(!Thread1.withPreviousRun()) { // if does NOT have previous Run
												timer.start();
												Thread1.thread.start();
											}
										}
									
										
									}else {
										// Stop
										
										if(chckbxRunAt.isSelected()) {
											//Thread2.isPause(true);
											//Thread2.ForceStop();
											Thread1.enableForceStop();
										}else {
											//Thread1.isPause(true);
											//Thread1.ForceStop();
											Thread1.enableForceStop();
										}

										defaultObjectState();
									}		
								}

							}else {
								JOptionPane.showMessageDialog(frame, "Script has an error. Please correct all the error/s.");
							}
							

						}else {
								JOptionPane.showMessageDialog(frame, "There were step/s changes which wasn't updated yet into EXCEL. Please click first UPDATE EXCEL");
						}
						
						
					}else {
						JOptionPane.showMessageDialog(frame, "EXCEL is currently opened. Please close first and try again.");
					}
				}
		        

			
				
				

			
			
			
			}});
		
		
		btnStart.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStart.setBounds(364, 454, 103, 36);
		panel.add(btnStart);
		
		timer = new Timer(300, new ActionListener(){ 
	        public void actionPerformed(ActionEvent e) {
	     
	         updateStatusStartEndDurationColumns();
	         UpdateStatusCounter(); 
	         
	        if(chckbxRunAt.isSelected()) {
	        	timerFlexSteps();
	        }else {
	        	if(Thread1.GetIsStopTimer()) {
	        		   
	       			defaultObjectState();      		
	        		//timer.stop();
	        		
	        	}
	        	
	        	if(!Thread1.getPause()) { // if pause is false
	        		table.changeSelection(Thread1.GetLastProcessRow(), 0, false, false);
	        		table_2.changeSelection(Integer.parseInt(table.getValueAt(table.getSelectedRow(), 2).toString())-1, 0, false, false);
	        		textArea.setText(Thread1.GetLog());
	        	}
	        	
	        	if(Thread1.GetAutoPause() && !AutoPauseTriggered) {
	        		AutoPauseTriggered = true;
					Thread1.isPause(true);
					btnStart.setEnabled(false);
					btnPause.setText("Resume");
					
					btnSave.setEnabled(true);
					btnDeleteStep.setEnabled(true);
					btnDataRepo.setEnabled(true);
					btnAddStep.setEnabled(true);
					
					btnUp.setEnabled(true);
					btnDown.setEnabled(true);
					btnSetPauseAt.setEnabled(true);
					btnRestartAt.setEnabled(true);
					
	        	}
	        }	
	        

	        	
	        }
	    });
		
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
			if(chckbxRunAt.isSelected()) {
				pauseBtnFlexSteps();
			}else {
				
				if(btnPause.getText().equalsIgnoreCase("Pause")) {
					Thread1.isPause(true);
					btnStart.setEnabled(false);
					btnPause.setText("Resume");
					
					btnDeleteStep.setEnabled(true);
					btnDataRepo.setEnabled(true);
					btnAddStep.setEnabled(true);
					btnSave.setEnabled(true);
					
					btnUp.setEnabled(true);
					btnDown.setEnabled(true);
					btnSetPauseAt.setEnabled(true);
					btnRestartAt.setEnabled(true);
					
				}else {
					//"Resume");
					if((table.getRowCount()==MainExtension.TestId.size() && !moveRow)) { // check if there's any added or move step/s
						
						if(Thread1.GetAutoPause() && AutoPauseTriggered) {
							Thread1.SetAutoPause(false);
							AutoPauseTriggered = false;
						}
									
						Thread1.isPause(false);
						btnStart.setEnabled(true);
						btnPause.setText("Pause");	
						
						btnDeleteStep.setEnabled(false);
						btnDataRepo.setEnabled(false);
						btnAddStep.setEnabled(false);
						btnSave.setEnabled(false);
						
						btnUp.setEnabled(false);
						btnDown.setEnabled(false);
						btnSetPauseAt.setEnabled(false);
						btnRestartAt.setEnabled(false);
						
						
						if(GetPauseAt()==Thread1.GetLastProcessRow()) { // reset pause at if defined pause already same with current process row
							lblPauseAt.setText("N/A");
							Thread1.SetPauseAt(GetPauseAt());
						}else {
							Thread1.SetPauseAt(GetPauseAt());
						}

						
					}else {
						JOptionPane.showMessageDialog(frame, "There were step/s changes which wasn't updated yet into EXCEL. Please click first UPDATE EXCEL");
					}
					
				}
			}
	
				
			}
		});
		
		
		btnPause.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPause.setBounds(478, 454, 103, 36);
		panel.add(btnPause);
		
		JLabel lblFail = new JLabel("Fail: ");
		lblFail.setForeground(new Color(255, 0, 0));
		lblFail.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblFail.setBounds(1269, 12, 26, 14);
		panel.add(lblFail);
		
		JLabel lblPass = new JLabel("Pass: ");
		lblPass.setForeground(new Color(50, 205, 50));
		lblPass.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPass.setBounds(1115, 12, 33, 14);
		panel.add(lblPass);
		
		JLabel lblSkip = new JLabel("Skip: ");
		lblSkip.setForeground(new Color(0, 0, 255));
		lblSkip.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSkip.setBounds(1196, 11, 31, 14);
		panel.add(lblSkip);
		
		
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				try {
					
					SelectFile();
					
					if(SubmitFile()) {
						MainExtension.SetErrorMessage("Empty()");
						MainExt.ClearErrorFreeFlag();
						if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
							ClearSelectedTableStatus(0,table.getRowCount());
							AddRow(MainExtension.TestId,MainExtension.Description,MainExtension.Command,MainExtension.Text,MainExtension.FindBy,MainExtension.Field,MainExtension.Skip,MainExtension.SkipNextIfPass,MainExtension.StopAllIfFail,MainExtension.Commands(),MainExtension.FindBy());
						}else {
							MainExtension.SetErrorMessage("# Input Excel is currently opened, please close the file and browse again.");
						}
						
						
						textArea.setText(MainExtension.GetErrorMessage());
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnBrowse.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnBrowse.setEnabled(true);
		btnBrowse.setBounds(1219, 454, 121, 36);
		panel.add(btnBrowse);
		

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
					
					if(!chckbxfiltersteps.isSelected()) {
						if(JOptionPane.showConfirmDialog(frame, "Do you want to proceed?","Confirmation",JOptionPane.YES_NO_OPTION)==0) {
							MainExtension.SetErrorMessage("Empty()");
							MainExt.ClearErrorFreeFlag();
							updateExcel();
							updateExcelTestCondition();
							// refresh table data
							if(moveRow) {
								AddRow(MainExtension.TestId,MainExtension.Description,MainExtension.Command,MainExtension.Text,MainExtension.FindBy,MainExtension.Field,MainExtension.Skip,MainExtension.SkipNextIfPass,MainExtension.StopAllIfFail,MainExtension.Commands(),MainExtension.FindBy());
								moveRow = false;
							}
						}
					}else {
						JOptionPane.showMessageDialog(frame, "Cannot perform updating if FILTER STEPS is checked");	
					}
					
					
				}else {
					JOptionPane.showMessageDialog(frame, "EXCEL is currently opened. Please close first and try again.");
				}
				
				textArea.setText(MainExtension.GetErrorMessage());
				
			}
		});
		
	    
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSave.setEnabled(true);
		btnSave.setBounds(1088, 454, 121, 36);
		panel.add(btnSave);
		
		lblPassNum.setForeground(new Color(50, 205, 50));
		lblPassNum.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPassNum.setBounds(1147, 8, 45, 16);
		panel.add(lblPassNum);
		
		
		lblSkipNum.setForeground(new Color(0, 0, 255));
		lblSkipNum.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblSkipNum.setBounds(1226, 8, 45, 16);
		panel.add(lblSkipNum);
		
		lblFailNum.setForeground(new Color(255, 0, 0));
		lblFailNum.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblFailNum.setBounds(1295, 6, 45, 20);
		panel.add(lblFailNum);
		lblSteps.setForeground(new Color(0, 0, 0));
		lblSteps.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSteps.setBounds(781, 12, 45, 14);
		
		panel.add(lblSteps);
		lblStepsNum.setForeground(Color.BLACK);
		lblStepsNum.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblStepsNum.setBounds(823, 12, 121, 14);
		
		panel.add(lblStepsNum);
		JLabel lblDuration = new JLabel("Duration:");
		lblDuration.setForeground(Color.BLACK);
		lblDuration.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDuration.setBounds(948, 12, 65, 14);
		panel.add(lblDuration);
		
		lblDurationNum.setForeground(Color.BLACK);
		lblDurationNum.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblDurationNum.setBounds(1011, 10, 94, 14);
		panel.add(lblDurationNum);
		
		btnDataRepo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
	
				//if(MainExtension.DataRepository.size()!=0) {
					ShowDataRepo();	
				//}else {
				//	JOptionPane.showMessageDialog(frame, "Data Repository is EMPTY.");
			//	}

			}
		});
		btnDataRepo.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnDataRepo.setEnabled(true);
		btnDataRepo.setBounds(851, 454, 103, 19);
		panel.add(btnDataRepo);
		

		btnAddStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				if(!chckbxfiltersteps.isSelected()) {
					if((table.getRowCount()==MainExtension.TestId.size() && !moveRow)) {
						dataModel = (DefaultTableModel) table.getModel();
						dataModel.addRow(new Object[]{String.valueOf(table.getRowCount()+2), "", "", "", "", "", "", "", false, false, false, "", "", ""});
						table.changeSelection(table.getRowCount(), 0, false, false);
						
					}else {
						JOptionPane.showMessageDialog(frame, "There were step/s changes which wasn't updated yet into EXCEL. Please click first UPDATE EXCEL");
					}
				}else {
					JOptionPane.showMessageDialog(frame, "Cannot perform updating if FILTER STEPS is checked");	
				}
				
				


			}
		});
		btnAddStep.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnAddStep.setEnabled(true);
		btnAddStep.setBounds(964, 454, 103, 19);
		panel.add(btnAddStep);
		btnDeleteStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				//JOptionPane.showMessageDialog(frame, "Not working yet!");
		
				
				try {
			        
					if(!chckbxfiltersteps.isSelected()) {

						if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
							if(table.getRowCount()==MainExtension.TestId.size() && !moveRow) {
									
									if(JOptionPane.showConfirmDialog(frame, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION)==0){
										int r = table.getSelectedRow();
										if(table.getRowCount()!=0) {
											//RemoveRowFromExcel(Integer.parseInt(dataModel.getValueAt(table.getSelectedRow(), 0).toString()));
											RemoveRowFromExcelTest(r);
											dataModel.removeRow(r);
											AddRow(MainExtension.TestId,MainExtension.Description,MainExtension.Command,MainExtension.Text,MainExtension.FindBy,MainExtension.Field,MainExtension.Skip,MainExtension.SkipNextIfPass,MainExtension.StopAllIfFail,MainExtension.Commands(),MainExtension.FindBy());
											
											if(table.getRowCount()>0) { // auto select 
												table.changeSelection(r, 0, false, false);
											}
											
										}
									}
									
							}else {
									JOptionPane.showMessageDialog(frame, "There were step/s changes which wasn't updated yet into EXCEL. Please click first UPDATE EXCEL");
							}
						}else {
							JOptionPane.showMessageDialog(frame, "EXCEL is currently opened. Please close first and try again.");
						}
						
					}else {
						JOptionPane.showMessageDialog(frame, "Cannot perform updating if FILTER STEPS is checked");	
					}

				}catch(Exception ex)
				{
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
		
				
			}
		});
		btnDeleteStep.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnDeleteStep.setEnabled(true);
		btnDeleteStep.setBounds(964, 471, 103, 19);
		
		panel.add(btnDeleteStep);
		

		btnUp.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnUp.setEnabled(true);
		btnUp.setBounds(614, 454, 114, 19);
		panel.add(btnUp);
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int index = table.getSelectedRow();
			    if(index > 0){
			    	
				    dataModel.moveRow(index, index, index - 1);
				    table.setRowSelectionInterval(index - 1, index - 1);
					moveRow = true;

				}
				
			}
		});
		
		

		btnDown.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnDown.setEnabled(true);
		btnDown.setBounds(614, 471, 114, 19);
		panel.add(btnDown);
		
		lblPauseAt.setBackground(SystemColor.inactiveCaption);
		lblPauseAt.setForeground(Color.BLACK);
		lblPauseAt.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPauseAt.setBounds(662, 10, 39, 14);
		panel.add(lblPauseAt);
		
		JLabel lblPauseAt_1 = new JLabel("Pause At :");
		lblPauseAt_1.setForeground(Color.BLACK);
		lblPauseAt_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPauseAt_1.setBounds(597, 12, 65, 14);
		panel.add(lblPauseAt_1);
		

		btnSetPauseAt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				if(chckbxRunAt.isSelected()) {
					JOptionPane.showMessageDialog(frame, "This is not applicable when Run At CheckBox is Checked.");
				}else {
					try {
						String PauseAt = JOptionPane.showInputDialog("Enter Row #");
						
						if(PauseAt!=null && Integer.parseInt(PauseAt)!=0) {
							
							if((Integer.parseInt(PauseAt)-1)<=table.getRowCount()) {
								lblPauseAt.setText(PauseAt);
								Thread1.SetPauseAt(GetPauseAt());

							}else {
								JOptionPane.showMessageDialog(frame, "Pause At should not be greater than row number!");
							}
						
						}else {
							lblPauseAt.setText("N/A");
							Thread1.SetPauseAt(GetPauseAt());	
						}
					}catch(Exception ex) {
						JOptionPane.showMessageDialog(frame, ex.getMessage());
					}
				}
				
			}
		});
		btnSetPauseAt.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnSetPauseAt.setEnabled(false);
		btnSetPauseAt.setBounds(738, 454, 103, 19);
		panel.add(btnSetPauseAt);
		btnRestartAt.setEnabled(false);
		
		btnRestartAt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(chckbxRunAt.isSelected()) {
					JOptionPane.showMessageDialog(frame, "This is not applicable when Run At CheckBox is Checked.");
				}else {
					try {
						String resumeAndStartAt = JOptionPane.showInputDialog("Enter Row #");
						
						if(resumeAndStartAt!=null && Integer.parseInt(resumeAndStartAt)!=0 && btnPause.getText().equalsIgnoreCase("Resume")) {
							
							if((Integer.parseInt(resumeAndStartAt)-1)<=table.getRowCount()) {

								if(table.getValueAt(Integer.parseInt(resumeAndStartAt)-2, 7)!=null) {
									if(!table.getValueAt(Integer.parseInt(resumeAndStartAt)-2, 7).toString().isEmpty()) {
										 //timer.stop();
										 ClearSelectedTableStatus(Integer.parseInt(resumeAndStartAt)-2,table.getRowCount());
										 Thread1.SetSelectedRow(Integer.parseInt(resumeAndStartAt)-2);
										 btnPause.doClick();
										 Thread1.SetLastProcessRow(Integer.parseInt(resumeAndStartAt)-2);
										
										 //timer.restart();
										
									}else {
										JOptionPane.showMessageDialog(frame, "Row should already have status.");
									}
								}
								
							}else {
								JOptionPane.showMessageDialog(frame, "Restart At should not be greater than row number!");
							}
						
						}else {
							JOptionPane.showMessageDialog(frame, "Automation should be in the PAUSE state and entered row should be valid!");
						}
					}catch(Exception ex) {
						JOptionPane.showMessageDialog(frame, ex.getMessage());
					}
				}	
				

				
				
			}
		});
		btnRestartAt.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnRestartAt.setBounds(738, 471, 103, 19);
		panel.add(btnRestartAt);
		btnJumpNextStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnPause.getText().equalsIgnoreCase("Resume")) {
					btnPause.doClick();
					btnJumpNextStep.setEnabled(false);
					
					if(chckbxRunAt.isSelected()) {
						Thread2.SetSelectedRow(Thread2.GetSelectedRow() + 1);
					}else {
						Thread1.SetSelectedRow(Thread1.GetSelectedRow() + 1);
					}
					
				}
			}
		});
		
		btnJumpNextStep.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnJumpNextStep.setEnabled(false);
		btnJumpNextStep.setBounds(851, 471, 103, 19);
		panel.add(btnJumpNextStep);
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        int index = table.getSelectedRow();
		        
		        if(index < dataModel.getRowCount() - 1){
			        dataModel.moveRow(index, index, index + 1);
			        table.setRowSelectionInterval(index + 1, index + 1);
			        moveRow = true;
		        }
			}
		});
		
		//initialization
		defaultObjectState();
		
		// informing other main and main extension that it is UIMode
		MainExtension.SetUIMode(true);
		UpdateAllbuttonsState(false);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setBounds(10, 37, 344, 412);
		panel.add(scrollPane_3);
		
		table_2 = new JTable();
		table_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(chckbxfiltersteps.isSelected()) {
					String getId = table_2.getValueAt(table_2.getSelectedRow(), 0).toString();
					filterSteps(getId);	
				}
			}
		});
		scrollPane_3.setViewportView(table_2);
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Scenario", "Status"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, true, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table_2.getColumnModel().getColumn(0).setPreferredWidth(23);
		table_2.getColumnModel().getColumn(1).setPreferredWidth(264);
		
		btnDeleteScenario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//table_2.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());-
				
				
				
			}
		});
		btnDeleteScenario.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnDeleteScenario.setEnabled(true);
		btnDeleteScenario.setBounds(229, 454, 96, 19);
		panel.add(btnDeleteScenario);
		
		
		btnAddScenario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

					scenarioModel = (DefaultTableModel) table_2.getModel();
					scenarioModel.addRow(new Object[]{String.valueOf(table_2.getRowCount()+1), "", ""});
					table_2.changeSelection(table_2.getRowCount(), 0, false, false);	
					
			}
		});
		btnAddScenario.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnAddScenario.setEnabled(true);
		btnAddScenario.setBounds(129, 454, 96, 19);
		panel.add(btnAddScenario);
		chckbxfiltersteps.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(!chckbxfiltersteps.isSelected()) {
					filterSteps("");
				}
			}
		});
		
		chckbxfiltersteps.setFont(new Font("Tahoma", Font.BOLD, 9));
		chckbxfiltersteps.setEnabled(true);
		chckbxfiltersteps.setBackground(SystemColor.inactiveCaption);
		chckbxfiltersteps.setBounds(6, 451, 117, 23);
		panel.add(chckbxfiltersteps);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 523, 1350, 161);
		contentPane.add(tabbedPane);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Log", null, panel_2, null);
		panel_2.setLayout(null);
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(0, 0, 1345, 133);
		panel_2.add(scrollPane_1);
		textArea.setBackground(SystemColor.inactiveCaption);
		textArea.setFont(new Font("Monospaced", Font.BOLD, 11));
		scrollPane_1.setViewportView(textArea);
		textArea.setEditable(false);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Handling", null, panel_1, null);
		panel_1.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(0, 0, 1345, 133);
		panel_1.add(scrollPane_2);
		
		table_1 = new JTable();
		scrollPane_2.setViewportView(table_1);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Setting", null, panel_3, null);
		panel_3.setLayout(null);
		chckbxclosebrowsers.setFont(new Font("Tahoma", Font.BOLD, 11));
		chckbxclosebrowsers.setBounds(239, 7, 256, 23);
		
		panel_3.add(chckbxclosebrowsers);
		chckbxAutoPauseStep.setFont(new Font("Tahoma", Font.BOLD, 11));
		chckbxAutoPauseStep.setEnabled(true);
		chckbxAutoPauseStep.setBackground(SystemColor.control);
		chckbxAutoPauseStep.setBounds(6, 7, 163, 23);
		
		panel_3.add(chckbxAutoPauseStep);
		chckbxRunAt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(chckbxRunAt.isSelected()) {
					textFieldRunAt.setEnabled(true);
				}else {
					textFieldRunAt.setEnabled(false);
				}
			}
		});
		chckbxRunAt.setFont(new Font("Tahoma", Font.BOLD, 11));
		chckbxRunAt.setEnabled(true);
		chckbxRunAt.setBackground(SystemColor.control);
		chckbxRunAt.setBounds(239, 33, 178, 23);
		
		panel_3.add(chckbxRunAt);
		
		textFieldRunAt = new JTextField();
		textFieldRunAt.setBackground(SystemColor.window);
		textFieldRunAt.setEnabled(false);
		textFieldRunAt.setColumns(10);
		textFieldRunAt.setBounds(261, 62, 221, 20);
		panel_3.add(textFieldRunAt);
		
		JLabel lblRunAt = new JLabel("e.g. 2,5-6,10");
		lblRunAt.setForeground(Color.BLACK);
		lblRunAt.setFont(new Font("Tahoma", Font.PLAIN, 7));
		lblRunAt.setBounds(261, 81, 74, 14);
		panel_3.add(lblRunAt);
		chckbxRunViaBackend.setFont(new Font("Tahoma", Font.BOLD, 11));
		chckbxRunViaBackend.setEnabled(true);
		chckbxRunViaBackend.setBackground(SystemColor.control);
		chckbxRunViaBackend.setBounds(6, 33, 121, 23);
		
		panel_3.add(chckbxRunViaBackend);
		
		chckbxOpenDashboard.setFont(new Font("Tahoma", Font.BOLD, 11));
		chckbxOpenDashboard.setEnabled(true);
		chckbxOpenDashboard.setBackground(SystemColor.menu);
		chckbxOpenDashboard.setBounds(6, 61, 178, 23);
		panel_3.add(chckbxOpenDashboard);
		
		chckbxhighlightelement.setFont(new Font("Tahoma", Font.BOLD, 11));
		chckbxhighlightelement.setEnabled(true);
		chckbxhighlightelement.setBackground(SystemColor.menu);
		chckbxhighlightelement.setBounds(6, 87, 178, 23);
		panel_3.add(chckbxhighlightelement);
		
		
				textArea.addContainerListener(new ContainerAdapter() {
					@Override
					public void componentAdded(ContainerEvent e) {
					
					}
				});
				
	    importHandling();		
	    
		/*
		table = new JTable(dataModel){
			
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false; //Disallow the editing of any cell
			}
		};*/
	}
}
