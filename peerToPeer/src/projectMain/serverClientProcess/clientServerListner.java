package projectMain.serverClientProcess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ListIterator;
import peerDetails.handshakeMechanism;
import peerDetails.peerBin;
import peerDetails.messageDetails.bitFieldLogger;
import projectMain.mainPeerProcessing;
import configurationFiles.ProcessPeerConfig;
import projectMain.ProcessSteps.MessageReceivePiece;
import projectMain.ProcessSteps.MessageRequestPiece;
import projectMain.ProcessSteps.MessageSendPiece;
import logInformation.loggerInformation;
import peerDetails.peerHasCompleteFile;


public class clientServerListner extends Thread {
	private int selfPeerId;
	private int myport;
	private String neighbourPeerIP;
	private ArrayList<String[]> clientDetails = new ArrayList<String[]>();
	private long sizeofPiece;
	private long sizeofFile;
	private int allPieces;
	private boolean isAllpieces;

	public clientServerListner(int peerID, int allPieces, boolean isAllpieces, long sizeofFile, long sizeofPiece) {
		selfPeerId = peerID;
		this.allPieces = allPieces;
		this.isAllpieces = isAllpieces;
		this.sizeofFile = sizeofFile;
		this.sizeofPiece = sizeofPiece;
	}

	// receive handshake from the peer
	public byte[] getHandshake(Socket socket) throws IOException, ClassNotFoundException {
		byte[] handshake = null;
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
		handshake = (byte[]) inputStream.readObject();
		return handshake;
	}

	// post the bitfield to the peer
	public void postBitfield(Socket socket) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(bitFieldLogger.bitfield);
	}

	// get the bitfield from the peer
	public byte[] getBitfield(Socket socket) throws IOException, ClassNotFoundException {

		byte[] bitfield = null;
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
		bitfield = (byte[]) inputStream.readObject();
		return bitfield;
	}

	// establish handshake with peers
	public void establishHandshakeRequest(Socket socket, byte[] handshake) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(handshake);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void initiateClient() throws UnknownHostException, IOException, ClassNotFoundException {

		ProcessPeerConfig peerConfig = new ProcessPeerConfig(selfPeerId);
		clientDetails = peerConfig.getPeerDetails();

		ListIterator<String[]> it = clientDetails.listIterator();
		while (it.hasNext()) {
			String[] value = it.next();
			neighbourPeerIP = value[1];
			myport = Integer.parseInt(value[2]);
			Socket socket = new Socket(neighbourPeerIP, myport);

			// Process of Handshake
			handshakeMechanism sendRequest = new handshakeMechanism(selfPeerId);
			establishHandshakeRequest(socket, sendRequest.handshakeMechanism);

			// establish a handshake
			byte[] requestReceived = getHandshake(socket);

			// Create header
			byte[] tempByte = new byte[28];
			for (int i = 0; i < 28; i++) {
				tempByte[i] = requestReceived[i];
			}
			String header = new String(tempByte);

			// Get peerId i.e. last 4 byte
			int j = 0;
			int i = 28;
			byte[] tempPeerId = new byte[4];
			while (i < 32) {
				tempPeerId[j] = requestReceived[i];
				j++;
			}
			
			int peerId = Integer.parseInt(new String(tempPeerId));

			// check for headers
			if (header.equals("P2PFILESHARINGPROJ0000000000")) {
				boolean foundId = false;

				ListIterator<Integer> iter = mainPeerProcessing.totalPeerId.listIterator();

				while (iter.hasNext()) {
					int curr = iter.next().intValue();
					if (curr == peerId)
						foundId = true;
				}

				// found peer id
				// base-case
				if (foundId == false) {
					System.out.println("Invalid peerId connection");
					return;
				}
				// logic-case
				if(foundId) {
					peerBin peerDetails = new peerBin();
					peerDetails.setSelfPeerID(selfPeerId);
					peerDetails.setSocket(socket);
					peerDetails.setPeer_ID(Integer.parseInt(value[0]));
					// send the bitfield
					byte[] fields = getBitfield(socket);
					peerDetails.setBitfield(fields);
					
					postBitfield(socket);
					peerDetails.setInterested(false);
					
					synchronized (mainPeerProcessing.peers) {
						mainPeerProcessing.peers.add(peerDetails);
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							System.err.println(e);
						}
					} 
					peerHasCompleteFile completedFile = new peerHasCompleteFile();
					completedFile.setSocket(socket);
					completedFile.setHasDownLoadedCompleteFile(false);
					
				    mainPeerProcessing.hasDownloadedCompleteFile.add(completedFile);
				    
				    System.out.println("Connection request sent to " + Integer.parseInt(value[0]));
					System.out.println();
					loggerInformation.makeTCPConnectionFunction(Integer.parseInt(value[0]));
				    
					MessageSendPiece sendPieceMessage = new MessageSendPiece();
					sendPieceMessage.start();
					
					MessageRequestPiece requestPiece = new MessageRequestPiece(Integer.parseInt(value[0]), allPieces, isAllpieces,sizeofFile, sizeofPiece);
					requestPiece.start();
					
					MessageReceivePiece receiveMessage = new MessageReceivePiece(socket, sizeofPiece);
					receiveMessage.start();
					
				}
				else {
					System.out.println("Unexpected peer connection");
				}	

				// request the bitfield
				

				// establish multi-threading process108

				// chk for complete file109

				// send the message

				// request for the piece

				// receive for the piece

			}
		} 
	}
}