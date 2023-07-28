package MarsWTA;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;


public class Main 
{
	
	public static List<String> lstFile = new ArrayList<>();	
	public static UIMode UIModeNew = new UIMode();

    public static void main(String[] args)
    {
	    UIModeNew.setVisible(true);
    }

	
public static void SubmitFile() throws Exception {
		
		for(String file: lstFile) {
			
			String[] subFile = file.split("\\|");
		    if(argsEmpty(subFile[0]))
			{
				MainExtension NewME = new MainExtension();	
		    	if(MainExtension.CheckDataSource(subFile[0],MainExtension.Commands(), MainExtension.FindBy()))
		    	{
					NewME.SetFName(subFile[0], subFile[1], subFile[2]);
					NewME.RunValidation();
					if(NewME.GetErrorFreeFlag()) {
						NewME.AutomationEngine(NewME.GetFName());
					}
		    	}

			}else
			{
				System.out.println("# Invalid File/Execution");
			}
		    System.out.println("\n");
		    TimeUnit.SECONDS.sleep(5);
		}
	}
	
	
	public static boolean argsEmpty(String Largs)
	{
		boolean Flag = false;
		if(!Largs.isEmpty())
		{ 
			if(Largs.toLowerCase().contains(".xlsx"))
			{
				Flag = true;
			}
		}
		return Flag;
	}
	
	public static void SelectFile() {
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
	

	public static String Concatenation(String raw) {
		String ripe = "";
				
			String[] splitGetWebField = raw.split("\\+");
				
			for(String s:splitGetWebField) {	
						ripe+=s.trim();
			}
			
		return ripe;
	}
	
	
	
	
}
