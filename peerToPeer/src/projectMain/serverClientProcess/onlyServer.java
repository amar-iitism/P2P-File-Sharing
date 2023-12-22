package projectMain.serverClientProcess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ListIterator;

import logInformation.loggerInformation;
import peerDetails.handshakeMechanism;
import peerDetails.peerBin;
import peerDetails.peerHasCompleteFile;
import peerDetails.messageDetails.bitFieldLogger;
import projectMain.mainPeerProcessing;
import projectMain.ProcessSteps.MessageReceivePiece;
import projectMain.ProcessSteps.MessageRequestPiece;
import projectMain.ProcessSteps.MessageSendPiece;

public class onlyServer extends Thread {

	public int serverPort;
	public int selfPeerId;
	public int totalPieces;
	public boolean isCompleteFile;
	public long fileSize;
	public long pieceSize;

	// constructor
	public onlyServer(int serverPort, int selfPeerId, int totalPieces, boolean isCompleteFile, long fileSize,
			long pieceSize) {
		this.serverPort = serverPort;
		this.selfPeerId = selfPeerId;
		this.totalPieces = totalPieces;
		this.isCompleteFile = isCompleteFile;
		this.fileSize = fileSize;
		this.pieceSize = pieceSize;
	}

	// send the bitfield
	public void postBitfield(Socket clientSocket) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.writeObject(bitFieldLogger.bitfield);
	}

	// get the bitfield
	public byte[] getBitfield(Socket clientSocket) throws IOException, ClassNotFoundException {

		byte[] bitfield = null;
		ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
		bitfield = (byte[]) inputStream.readObject();
		return bitfield;
	}

	// handshake request from client
	public byte[] establishHandshakeRequest(Socket clientSocket) throws IOException, ClassNotFoundException {

		byte[] requestHandshake = null;
		ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
		requestHandshake = (byte[]) input.readObject();
		return requestHandshake;
	}

	// handshake request from server
	private void requestHandshakeServer(Socket socket, byte[] handshake) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(handshake);
	}

	// start the server
	public void initiateServer() throws IOException, ClassNotFoundException {
		// System.out.println("Server Port is :\t" + serverPort);
		ServerSocket serverListener = new ServerSocket(serverPort);
		Socket clientSocket = serverListener.accept();

		// establish a handshake
		byte[] requestReceived = establishHandshakeRequest(clientSocket);

		// server sends handshake
		handshakeMechanism send = new handshakeMechanism(selfPeerId);
		requestHandshakeServer(clientSocket, send.handshakeMechanism);

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
			peerBin peerDetails = new peerBin();
			peerDetails.setSelfPeerID(selfPeerId);
			peerDetails.setSocket(clientSocket);
			peerDetails.setPeer_ID(peerId);

			// send the bitfield
			postBitfield(clientSocket);

			// request the bitfield
			peerDetails.setBitfield(getBitfield(clientSocket)); //

			// add the peerDetails info. to the peer
			mainPeerProcessing.peers.add(peerDetails);

			// chk for complete file109
			/*
			 *  
			 */
			
			peerHasCompleteFile completeFile = new peerHasCompleteFile();
			completeFile.setSocket(clientSocket);
			completeFile.setHasDownLoadedCompleteFile(false);
			
			mainPeerProcessing.hasDownloadedCompleteFile.add(completeFile);
			
			System.out.println("Connection request from " + peerId);
			System.out.println();
			loggerInformation.madeTCPConnectionFunction(peerId);
			

			MessageSendPiece sendMessage = new MessageSendPiece();
			sendMessage.start();
			
			MessageRequestPiece pieceReq = new MessageRequestPiece(peerId, totalPieces, isCompleteFile, fileSize, pieceSize);
			pieceReq.start();
			
			MessageReceivePiece messageReceiver = new MessageReceivePiece(clientSocket, pieceSize);
			messageReceiver.start();


			// establish TCP connection with peerId
		}
	}


	/*
	 * public static void main(String[] args) { // TODO Auto-generated method stub
	 * 
	 * }
	 */
}
