package MarsWTA;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.RowSorter;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIDataRepo extends JDialog {
	private JTextField textText;
	private JTextField textField;
	private JTextField textFieldPipe;
	public  MainExtension MainExt = new MainExtension();
	public UIMode UIM = new UIMode();
	public static String selectedDataRepo;
	private DefaultTableModel dataModel;
	private JTable tabledatarepo;	
	public JFrame UIMode;
	public JTable table;
	JButton btnSave = new JButton("Update EXCEL");
	JButton btnDelete = new JButton("Delete Key");
	JRadioButton rdbtnField = new JRadioButton("Field");
	JRadioButton rdbtnText = new JRadioButton("Text");
	static boolean addedKey = false;
	/**
	 * Launch the application.
	 */
/*	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIDataRepo dialog = new UIDataRepo();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/


	/*public String GetSelectedDataRepo() {
		return selectedDataRepo;
	}*/

	public boolean updateKeys() {
		boolean Flag = false;
		try {		
			if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
				MainExt.ImportRepository(MainExt.GetFName(), false, "Data Source");
				Flag = true;
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
	
		return Flag;
	}	
	
	public void RemoveRowFromExcel(String Selectedkey) {

		try {
			//System.out.println(Selectedkey);
			File file = new File(MainExt.GetFName());
	        FileInputStream inputStream = new FileInputStream(file);
	        Workbook MarWorkbook = null;
	        MarWorkbook = new XSSFWorkbook(inputStream);
	        Sheet DSSheet = MarWorkbook.getSheet("Data Source");
	        
	        Cell key = null, keyValue = null;
	        Row rowNum;		
	        boolean Flag = true;
	        int i = 1;
	        
	        while(Flag) {
	        	rowNum = DSSheet.getRow(i);
	        	
	        	if(rowNum.getCell(4).toString().equalsIgnoreCase(Selectedkey)){ // if found, update cell into empty string
	        		
	    	 		key = rowNum.getCell(4);
	    	 		keyValue = rowNum.getCell(5);	
	    	 		
	    	 		key.setCellValue("");
	    	 		keyValue.setCellValue("");
	    	 		Flag = false;
	        	}
	        
	        	i++;
	        }

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
	
	public void updateExcel() {
		
		try {
			
			if(!MainExt.CheckFileIsOpen(MainExt.GetFName())) {
				
				File file = new File(MainExt.GetFName());
		        FileInputStream inputStream = new FileInputStream(file);
		        Workbook MarWorkbook = null;
		        MarWorkbook = new XSSFWorkbook(inputStream);
		        Sheet RegSheet = MarWorkbook.getSheet("Data Source");
		   
	     		Row rowNum;
	     		Cell key = null, keyValue = null;
	     		
	     		for(int i=0;i<tabledatarepo.getRowCount();i++) {
	     			
	     			
	     			if(RegSheet.getRow(i+1)==null) { // created row and cell if exceeded 100 rows
	     				rowNum = RegSheet.createRow((i+1));
	     				for(int r = 0; r<=6; r++) {
	     					rowNum.createCell(r);
	     				}
	     			}else {
	     				rowNum = RegSheet.getRow(i+1);
	     			}
	     			
	         		
	         		
	         		if(rowNum.getCell(4) ==null) {
	         			key = rowNum.createCell(4);
	         			keyValue = rowNum.createCell(5);
	         		}else {
		         		key = rowNum.getCell(4);
		         		keyValue = rowNum.getCell(5);	         			
	         		}
	        
	         		key.setCellValue(tabledatarepo.getValueAt(i, 0).toString());
	         		keyValue.setCellValue(tabledatarepo.getValueAt(i, 1).toString());

	        		
	     		}
		        
	     		
				    inputStream.close();
				    FileOutputStream outputStream = new FileOutputStream(file);
				    MarWorkbook.write(outputStream);
				    outputStream.close(); 
				    MarWorkbook.close();
				    

				    
					if(updateKeys()) {
					    JOptionPane.showMessageDialog(null, "Update Successfully!");
					    //btnSave.setEnabled(false);
						// refresh table data
					}
					
					
			}else {
					JOptionPane.showMessageDialog(null, "Excel File is currently Open. Please close and try again.");
			}
			
		}catch(Exception e) {
			//textArea.setText(e.getMessage());
			e.printStackTrace();
		}

	}
	
	
	public void sortTable() {
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabledatarepo.getModel());
		tabledatarepo.setRowSorter(sorter);

		List<RowSorter.SortKey> sortKeys = new ArrayList<>(tabledatarepo.getRowCount());
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
	}
	
	public void AddRow() {
		HashMap<String,String> DataRepo = MainExtension.DataRepository;
		try {
			dataModel = (DefaultTableModel) tabledatarepo.getModel();
			for(String i: DataRepo.keySet()){

				dataModel.addRow(new Object[]{
						i,
						DataRepo.get(i),
						});						
			}
			
			sortTable();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	
	public void addTitlebar() {
		
		int r = table.getSelectedRow();
		//setTitle("Data Repository - Row ["+ UIM.GetUIModeJTable().getValueAt(r, 0) +"] - Desc [" + UIM.GetUIModeJTable().getValueAt(r, 1) +"]");
		setTitle("Data Repository - Row ["+ table.getValueAt(r, 0) +"] - Desc [" + table.getValueAt(r, 1) +"]");
		
	}
	
	public void RemoveAllRows() {
		dataModel = (DefaultTableModel) tabledatarepo.getModel();
		int rowCount = tabledatarepo.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			dataModel.removeRow(i);
		}
	}
	
	public void autoselectRadio() {
		int r = table.getSelectedRow();
		int c = table.getSelectedColumn();
		
		if(c==4) { // text
			rdbtnText.setSelected(true);
		}
		
		if(c==6) { // field
			rdbtnField.setSelected(true);			
		}
		
	}

	
	public UIDataRepo(JFrame UIMode,JTable table) {
		setFont(new Font("Dialog", Font.BOLD, 15));
		setVisible(true);
		setResizable(false);
		this.UIMode = UIMode;
		this.table = table;
		addTitlebar();
		setBounds(100, 100, 1066, 439);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaption);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		rdbtnText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textText.setEditable(true);
				textField.setEditable(false);
				textFieldPipe.setEditable(false);		
			}
		});
		rdbtnText.setSelected(false);
		rdbtnText.setFont(new Font("Tahoma", Font.BOLD, 11));
		rdbtnText.setBackground(SystemColor.inactiveCaption);
		rdbtnText.setBounds(6, 7, 51, 23);
		panel.add(rdbtnText);
		
		textText = new JTextField();
		textText.setEditable(false);
		textText.setColumns(10);
		textText.setBackground(new Color(224, 255, 255));
		textText.setBounds(60, 7, 267, 23);
		panel.add(textText);
		
		rdbtnField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				textText.setEditable(false);
				textField.setEditable(true);
				textFieldPipe.setEditable(false);
				
			}
		});
		rdbtnField.setFont(new Font("Tahoma", Font.BOLD, 11));
		rdbtnField.setBackground(SystemColor.inactiveCaption);
		rdbtnField.setBounds(381, 7, 51, 23);
		panel.add(rdbtnField);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setColumns(10);
		textField.setBackground(new Color(224, 255, 255));
		textField.setBounds(434, 7, 267, 23);
		panel.add(textField);
		
		textFieldPipe = new JTextField();
		textFieldPipe.setEditable(false);
		textFieldPipe.setColumns(10);
		textFieldPipe.setBackground(new Color(224, 255, 255));
		textFieldPipe.setBounds(776, 7, 267, 23);
		panel.add(textFieldPipe);
		
		JRadioButton rdbtnField_1 = new JRadioButton("|");
		rdbtnField_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textText.setEditable(false);
				textField.setEditable(false);
				textFieldPipe.setEditable(true);
			}
		});
		rdbtnField_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		rdbtnField_1.setBackground(SystemColor.inactiveCaption);
		rdbtnField_1.setBounds(743, 7, 33, 23);
		panel.add(rdbtnField_1);
		
		JButton btnAdd = new JButton("Add Key");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				dataModel = (DefaultTableModel) tabledatarepo.getModel();
				dataModel.addRow(new Object[]{"",""});
				tabledatarepo.changeSelection(0, 0, false, false);
				addedKey = true;
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAdd.setBounds(6, 350, 93, 36);
		panel.add(btnAdd);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int r = tabledatarepo.getSelectedRow();
					if(JOptionPane.showConfirmDialog(null, "Do you want to proceed? Note: Once confirmed, excel will be updated too.", "Confirmation", JOptionPane.YES_NO_OPTION)==0){
						if(r>-1) {
							RemoveRowFromExcel(tabledatarepo.getValueAt(r, 0).toString());
							RemoveAllRows();
							AddRow();
							//btnDelete.setEnabled(false);
						}
					}

				}catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
			}
		});
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnDelete.setBounds(109, 350, 93, 36);
		panel.add(btnDelete);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(addedKey) {
					// adding
					if(!tabledatarepo.getValueAt(0, 0).toString().isEmpty() && !tabledatarepo.getValueAt(0, 1).toString().isEmpty()) {
						if(MainExtension.DataRepository.get(tabledatarepo.getValueAt(0, 0).toString())==null) {
							if(JOptionPane.showConfirmDialog(null, "Do you want to proceed?","Confirmation",JOptionPane.YES_NO_OPTION)==0) {
								updateExcel();
								sortTable();
								addedKey = false;
							}
						}else {
							JOptionPane.showMessageDialog(null, "Key is already exist. Please use different key.");
						}
					}else {
						JOptionPane.showMessageDialog(null, "Key and Key value are both required.");
					}
				}else {
					// updating
					updateExcel();
					sortTable();
				}
		}
				
			
		});
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnSave.setBounds(212, 350, 117, 36);
		panel.add(btnSave);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnClose.setBounds(823, 350, 105, 36);
		panel.add(btnClose);
		
		JButton btnApply = new JButton("Apply & Close");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!textText.getText().isEmpty() || !textField.getText().isEmpty()) {

					int r = table.getSelectedRow();
					String text = textText.getText();
					String field = textField.getText();
					
					if(!textFieldPipe.getText().isEmpty()) {
						field = textField.getText() + "|" + textFieldPipe.getText();
					}
					UIM.setTextTitleValFromDataRepo(r,text,field,table);
					dispose();
					
				}else {
					JOptionPane.showMessageDialog(null, "No assigned value on the Text/Field");
				}
			}
		});
		btnApply.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnApply.setBounds(938, 350, 105, 36);
		panel.add(btnApply);

		
	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnText);
	    group.add(rdbtnField);
	    group.add(rdbtnField_1);
	    
	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	    scrollPane.setBounds(6, 33, 1037, 306);
	    panel.add(scrollPane);
	    
	    tabledatarepo = new JTable();
	    tabledatarepo.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
				int i = tabledatarepo.getSelectedRow();
				
				if(i>-1) {
					
					if(rdbtnField.isSelected()) {
						textField.setText("key(" + tabledatarepo.getValueAt(i, 0).toString() + ")");
					}
					
					if(rdbtnText.isSelected()) {
						textText.setText("key(" + tabledatarepo.getValueAt(i, 0).toString() + ")");
					}
					
					if(rdbtnField_1.isSelected()) {
						if(!textField.getText().isEmpty()) {
							textFieldPipe.setText("key(" + tabledatarepo.getValueAt(i, 0).toString() + ")");
						}else {
							JOptionPane.showMessageDialog(null,"Can't assign value. Field value is required.");
						}
						
					}
					
					/*if(!tabledatarepo.getValueAt(i, 0).toString().isEmpty()) {
						btnDelete.setEnabled(true);
					}*/
					
				}
	    		
	    	}
	    });
	    tabledatarepo.setModel(new DefaultTableModel(
	    	new Object[][] {
	    	},
	    	new String[] {
	    		"Key", "Key Value"
	    	}
	    ) {
	    	Class[] columnTypes = new Class[] {
	    		String.class, String.class
	    	};
	    	public Class getColumnClass(int columnIndex) {
	    		return columnTypes[columnIndex];
	    	}
	    });
	    tabledatarepo.getColumnModel().getColumn(0).setPreferredWidth(249);
	    tabledatarepo.getColumnModel().getColumn(1).setPreferredWidth(367);
	    scrollPane.setViewportView(tabledatarepo);
	    tabledatarepo.setFillsViewportHeight(true);

	    
		//adding dataset
		AddRow();
		autoselectRadio();
		addedKey = false;
		
	}
}
