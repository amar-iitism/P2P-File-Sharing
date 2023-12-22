package configurationFiles;
import java.io.*;
import java.util.*;

public class ProcessConfigurationCfg {
	
	// define data members
	public int numberOfPrefferedNeighbors;
	public int unchokingInterval;
	public int optimisticUnchokingInterval;
	public String fileName;
	public long fileSize;
	public long pieceSize;
	
	public void getConfigProp() throws IOException {
		
		//File file = new File(".");
		//for(String fileNames : file.list()) System.out.println(fileNames);
		
		// Initialize the file name
		// fileName = (new File(System.getProperty("user.dir")).getParent() + "/peerToPeer/src" + "/peer_" + ID + "/" + fileName);

		// String setfileName = "src/configurationFiles/Common.cfg";
		String setfileName = (new File(System.getProperty("user.dir")).getParent() + "\\peerToPeer\\src\\configurationFiles\\Common.cfg");
		
		// System.out.println("Set File name is :\t"+ setfileName);

		try {
			
			FileInputStream commonCfg = new FileInputStream(setfileName);
			Properties prop = new Properties();
			
			// Initialize the data members
			prop.load(new BufferedInputStream(new FileInputStream(setfileName)));
			
			numberOfPrefferedNeighbors =  Integer.parseInt(prop.getProperty("NumberOfPreferredNeighbors"));
			unchokingInterval = Integer.parseInt(prop.getProperty("UnchokingInterval"));
			optimisticUnchokingInterval = Integer.parseInt(prop.getProperty("OptimisticUnchokingInterval"));
			fileName = prop.getProperty("FileName");
			fileSize = Long.parseLong(prop.getProperty("FileSize"));
			pieceSize = Long.parseLong(prop.getProperty("PieceSize"));
			
			commonCfg.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * public static void main(String[] args) throws IOException {
	 * ProcessConfigurationCfg pcc = new ProcessConfigurationCfg();
	 * pcc.getConfigProp(); System.out.println("1. Number of Preffered Neighbors:\t"
	 * + pcc.numberOfPrefferedNeighbors);
	 * System.out.println("2. Unchoking Interval:\t" + pcc.unchokingInterval);
	 * System.out.println("3. Optimistic Unchoking Interval:\t" +
	 * pcc.optimisticUnchokingInterval); System.out.println("4. FileName:\t" +
	 * pcc.fileName); System.out.println("5. FileSize:\t" + pcc.fileSize);
	 * System.out.println("6. PieceSize:\t" + pcc.pieceSize); }
	 */
}