package configurationFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import peerDetails.messageDetails.pieceFunction;

public class ProcessFileParser {
	
	private int ID;
	private long pieceSize;
	private HashMap <Integer, pieceFunction> map = new HashMap <Integer, pieceFunction>();
	private int pieceNum = 1;
	private String fileName;
	
	public ProcessFileParser(int ID, long pieceSize, String fileName) {
		this.ID = ID;
		this.pieceSize = pieceSize;
		this.fileName = fileName;
	}
	
	public HashMap<Integer, pieceFunction> readFile() {

		fileName = (new File(System.getProperty("user.dir")).getParent() + "/peerToPeer/src" + "/peer_" + ID + "/" + fileName);

		File f = new File(fileName);
		
		try {
			
			InputStream in = new FileInputStream(f);
			byte[] buf = new byte[(int)pieceSize];
			
			int len;
			
			while((len = in.read(buf)) > 0) {
			
				map.put(pieceNum, new pieceFunction(pieceNum, buf));
				pieceNum++;
			}
			
			in.close();
			
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		
		return map;
	}


}
