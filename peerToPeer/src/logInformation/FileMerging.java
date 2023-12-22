package logInformation;

import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;

import projectMain.mainPeerProcessing;
import peerDetails.messageDetails.pieceFunction;

public class FileMerging {
	
	public void filereassemble(int noOfPieces, int SelfPeerID, long sizeOfFile, long sizeOfPiece) {
		System.out.println("%%%%");
		String dirs = (new File(System.getProperty("user.dir")).getParent() + "/peer_" + SelfPeerID);
		File theDirs = new File(dirs);
    if (!theDirs.exists()) {
			try {
				theDirs.mkdir();	
			} catch(SecurityException e) {
				System.err.println(e);
			}        
		}
    String nameOfFile = (dirs + "/" + mainPeerProcessing.fileName);

	File file = new File(nameOfFile);
	try {

		OutputStream out = new FileOutputStream(file);
		
		for (int k = 1; k <= noOfPieces - 1; k++) {
			
			Integer num = new Integer(k);
			pieceFunction pr = mainPeerProcessing.hm.get(num);
			byte[] temp = new byte[4];
			
			for (int l = 0; l < 4; l++) {
				temp[l] = pr.piece[l];
			}
			
			int sizes = ByteBuffer.wrap(temp).getInt();
			sizes = sizes - 4;
			
			System.out.println(k + " = " + sizes);
			
			byte[] buffer = new byte[sizes];
			
			for (int b = 0, a = 9; b < buffer.length && a < pr.piece.length; b++, a++) {
				buffer[b] = pr.piece[a];
			}
			
			out.write(buffer);
		}
		
		Integer num = new Integer(noOfPieces);
		pieceFunction pr = mainPeerProcessing.hm.get(num);
		
		int sizes = (int) (sizeOfFile % sizeOfPiece);
		System.out.println(sizes);
		
		byte[] buffer = new byte[sizes];
		
		for (int a = 0, b = 9; a < buffer.length && b < pr.piece.length; a++, b++) {
			buffer[a] = pr.piece[b];
		}
		
		out.write(buffer);
		out.close();
	} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	
	
	}
}