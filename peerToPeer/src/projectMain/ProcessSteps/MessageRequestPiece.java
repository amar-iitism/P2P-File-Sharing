package projectMain.ProcessSteps;
import java.net.Socket;
import java.util.ListIterator;
import java.util.Random;

import projectMain.mainPeerProcessing;
import peerDetails.messageStructure;
import peerDetails.peerBin;
import peerDetails.peerHasCompleteFile;
import logInformation.FileMerging;
import logInformation.loggerInformation;
import peerDetails.messageDetails.*;

public class MessageRequestPiece extends Thread {
	
	private Socket socket; 
	private int idOfPeer;
	private int SelfPeerID;
	private int noOfPieces;
	private boolean isTotalPieces;
	private long sizeOfFile; 
	private long sizeOfPiece;
	private int flagIndex = 0;
	
	public MessageRequestPiece(int idOfPeer, int noOfPieces,boolean isTotalPieces, long sizeOfFile, long sizeOfPiece ) {
		this.idOfPeer = idOfPeer;
		this.isTotalPieces = isTotalPieces;
		this.noOfPieces = noOfPieces;
		this.sizeOfFile = sizeOfFile;
		this.sizeOfPiece = sizeOfPiece;
	}
	
	public void run() {
		if(isTotalPieces!= true) {
			peerBin pr = null;
			byte[] fields;
			int getPieces;
			
			synchronized(mainPeerProcessing.peers) {
            ListIterator<peerBin> it = mainPeerProcessing.peers.listIterator();
				
				while(it.hasNext()) {
					pr = (peerBin)it.next();
					
					if(pr.getPeer_ID() == idOfPeer) {
						SelfPeerID = pr.getSelfPeerID();
						socket = pr.getSocket();
						break;
					}
				}

			}
			
		    while(true) {
		    	try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					System.err.println(e);
				}
				
				boolean totalFiles = hasAlltheFiles();
				if(totalFiles) {

					
					if(!loggerInformation.fileFlags) {
						loggerInformation.fileFlags = true;
												
						System.out.println("Download complete");
						loggerInformation.downloadFinished();
						
						FileMerging assembles = new FileMerging();
						assembles.filereassemble(noOfPieces, SelfPeerID, sizeOfFile, sizeOfPiece);
						
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							System.err.println(e);
						}
					}
					
					break;
				
				} else {
					if(pr.isInterested()) {
						fields = pr.getBitfield();
						getPieces = getPieceDetails(fields, bitFieldLogger.bitfield);
						if(getPieces == 0) {
							pr.setInterested(false);
							notInterestedFunction not = new notInterestedFunction();
							synchronized (mainPeerProcessing.messageBody) {
								messageStructure msg = new messageStructure();
								msg.setSocket(socket);
								msg.setMessage(not.notInterestedFunction);
								mainPeerProcessing.messageBody.add(msg);
							}
							flagIndex = 1;
						}
						
						else {
							requestFunction req = new requestFunction(getPieces);
							synchronized (mainPeerProcessing.messageBody) {
								messageStructure msg = new messageStructure();
								msg.setSocket(socket);
								msg.setMessage(req.request);
								mainPeerProcessing.messageBody.add(msg);
							}
						}
					} else {

						
						fields = pr.getBitfield();
						getPieces = getPieceDetails(fields, bitFieldLogger.bitfield);
						
						if(getPieces == 0) {
							if(flagIndex == 0) {
								notInterestedFunction not = new notInterestedFunction();
								
								synchronized (mainPeerProcessing.messageBody) {
									messageStructure msg = new messageStructure();
									msg.setSocket(socket);
									msg.setMessage(not.notInterestedFunction);
									mainPeerProcessing.messageBody.add(msg);
								}
							}
						}
						
						else {
							pr.setInterested(true);
							flagIndex = 0;
							
							interestedFunction interested = new interestedFunction();
							
							synchronized (mainPeerProcessing.messageBody) {
								messageStructure msg = new messageStructure();
								msg.setSocket(socket);
								msg.setMessage(interested.interestedFunction);
								mainPeerProcessing.messageBody.add(msg);
							}
							requestFunction req = new requestFunction(getPieces);
							synchronized (mainPeerProcessing.messageBody) {
								messageStructure msg = new messageStructure();
								msg.setSocket(socket);
								msg.setMessage(req.request);
								mainPeerProcessing.messageBody.add(msg);
							}
						}
						
					
					}
				}
				
			}
		}
		byte[] downloadFinishedFile = new byte[5];
		for (int j = 0; j < downloadFinishedFile.length - 1; j++) {
			downloadFinishedFile[j] = 0;
		}
		
		downloadFinishedFile[4]= 8;
		sendHasDownloadedCompleteFile(downloadFinishedFile);
		while(true) {
			boolean checks = checksAllPeersFileDownloadedFunction();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			
			if(checks == true && mainPeerProcessing.messageBody.isEmpty())
				break;
		}
		if(!loggerInformation.wholeFileFlag)
		{
			loggerInformation.wholeFileFlag = true;
		
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
				
			loggerInformation.closedLogger();
		}
	
		System.exit(0);
		
	}
	
private boolean checksAllPeersFileDownloadedFunction() {
		
		boolean flaged = true;
		
		ListIterator<peerHasCompleteFile> it = mainPeerProcessing.hasDownloadedCompleteFile.listIterator();
		
		while(it.hasNext()) {
		
			peerHasCompleteFile peer = (peerHasCompleteFile)it.next();
			if(peer.isHasDownLoadedCompleteFile()) {
				flaged = false;
				break;
			}
		}
		
		return flaged;
	}
	
private void sendHasDownloadedCompleteFile(byte[] downLoadedCompleteFile) {
	
	ListIterator<peerHasCompleteFile> it = mainPeerProcessing.hasDownloadedCompleteFile.listIterator();
	
	while(it.hasNext()) {
	
		peerHasCompleteFile peer = (peerHasCompleteFile)it.next();
		
		synchronized (mainPeerProcessing.messageBody) {
			messageStructure msg = new messageStructure();
			msg.setSocket(peer.getSocket());
			msg.setMessage(downLoadedCompleteFile);
			mainPeerProcessing.messageBody.add(msg);
		}
	}
	
}
	
	
	
 private boolean hasAlltheFiles() {
		
		int flaged = 1;
		
		byte[] field = bitFieldLogger.bitfield;
		
		for (int i = 5; i < field.length - 1; i++) {
			if(field[i] != -1) {
				flaged = 0;
				break;
			}
		}
		
		if(flaged == 1) {
			
			int remaining = noOfPieces % 8;
			int x = field[field.length - 1];
			String x1 = Integer.toBinaryString(x & 255 | 256).substring(1);
			char[] x2 = x1.toCharArray();
			int[] x3 = new int[8];
			for (int j = 0;j<x2.length;j++) {
				x3[j] = x2[j] - 48;
			}
			for (int j = 0; j < remaining; j++) {
				if(x3[j] == 0) {
					flaged = 0;
					break;
				}
			}
		}
		return (flaged == 1);
	}
	
	
 private int getPieceDetails(byte[] field, byte[] bitfield) {
		
		int[] temp = new int[noOfPieces];
		int k = 0;
		int total_missing_pieces = 0;
		int remaining  = noOfPieces % 8;

		for (int i = 5; i < bitfield.length; i++) {
			
			int p = bitfield[i];
			int q = field[i];
			
			String p1 = Integer.toBinaryString(p & 255 | 256).substring(1);
			char[] p2 = p1.toCharArray();
			int[] p3 = new int[8];
					
			for (int j = 0; j < p2.length; j++) {
				p3[j] = p2[j] - 48;
			}
			
			String q1 = Integer.toBinaryString(q & 255 | 256).substring(1);
			char[] q2 = q1.toCharArray();
			int[] q3 = new int[8];
					
			for (int j = 0; j < q2.length; j++) {
				q3[j] = q2[j] - 48;
			}				
			

			if(i < bitfield.length - 1) {
				
				for (int j = 0; j < q3.length; j++) {
					if(q3[j] == 0 && q3[j] == 1) {
						temp[k] = 0;
						k++;
						total_missing_pieces++;
					}
					
					if(q3[j] == 0 && q3[j] == 0) {
						temp[k] = 1;
						k++;
					}
					
					if(p3[j] == 1) {
						temp[k] = 1;
						k++;
					}
				}
			}
			
			else {
				for (int j = 0; j < remaining; j++) {
					if(q3[j] == 0 && q3[j] == 1) {
						temp[k] = 0;
						k++;
						total_missing_pieces++;
					}
					
					if(q3[j] == 0 && q3[j] == 0) {
						temp[k] = 1;
						k++;
					}
					
					if(q3[j] == 1) {
						temp[k] = 1;
						k++;
					}
				}
			}
			
		}

		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		

		if(total_missing_pieces == 0)
			return 0;
		
		int[] selectFrom = new int[total_missing_pieces];
		
		int x = 0;
		for (int kp = 0; kp < temp.length; kp++) {
			if(temp[kp] == 0) {
				selectFrom[x] = kp;
				x++;
			}
		}
		int indexes = randomPiecesSelection(total_missing_pieces);
		int pieces = selectFrom[indexes];
		

		return (pieces + 1);
	}
	
 private int randomPiecesSelection(int total_missing_pieces) {
		
		Random rands = new Random();
	    int randomNumber = rands.nextInt(total_missing_pieces);

	    return randomNumber;
	    
	}
	
	
	
	
	
	
	
	
}
