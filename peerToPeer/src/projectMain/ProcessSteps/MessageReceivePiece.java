package projectMain.ProcessSteps;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ListIterator;

import logInformation.loggerInformation;
import peerDetails.messageStructure;
import peerDetails.peerBin;
import peerDetails.peerHasCompleteFile;
import peerDetails.messageDetails.bitFieldLogger;
import peerDetails.messageDetails.haveFunction;
import peerDetails.messageDetails.pieceFunction;
import projectMain.mainPeerProcessing;

public class MessageReceivePiece extends Thread {

	private Socket socket;
	private int remotePeerID;
	private long pieceSize;

	public MessageReceivePiece(Socket socket, long pieceSize) {
		this.socket = socket;
		this.pieceSize = pieceSize;

		ListIterator<peerBin> it = mainPeerProcessing.peers.listIterator();

		while (it.hasNext()) {
			peerBin p = (peerBin) it.next();

			if (p.getSocket().equals(socket)) {
				remotePeerID = p.getPeer_ID();
			}
		}
	}

	private byte[] peerReceivesMessage() {

		byte[] message = null;
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			message = (byte[]) in.readObject();
		}

		catch (Exception e) {
			System.exit(0);
		}
		return message;
	}
	
	public byte[] updateBitField(byte[] field, int pieceIndex) {
		int i = (pieceIndex - 1) / 8;
		int k = 7 - ((pieceIndex - 1) % 8);
		field[i + 5] = (byte) (field[i + 5] | (1<<k));
		return field;
	}
	
	@Override
	public void run() {
		
		while(true) {
			
			byte[] message = peerReceivesMessage();
			int type = message[4];
			
			if(type == 0) {
				//choke
			}
			
			else if(type == 1) {
				//unchoke
			}
			
			else if(type == 2) {
				//interested
				
				System.out.println("Interested message received from " + remotePeerID);
				System.out.println();
				
				// write the logger file
				loggerInformation.interestedReceiveFunction(remotePeerID);
			}
			
			else if(type == 3) {
				//not interested
				
				System.out.println("Not Interested message received from " + remotePeerID);
				System.out.println();
				
				// write the logger file
				loggerInformation.notInterestedReceiveFunction(remotePeerID);
			}
			
			else if(type == 4) {
				//have
				
				byte[] temp = new byte[4];
				
				int x = 5;
				for (int i = 0; i < temp.length; i++) {
					temp[i] = message[x];
					x++;
				}
				
				int pieceNum = ByteBuffer.wrap(temp).getInt();
				
				ListIterator<peerBin> it = mainPeerProcessing.peers.listIterator();

				while(it.hasNext()) {
					peerBin p = (peerBin)it.next();

					if(p.getSocket().equals(socket)) {
						byte[] field = p.getBitfield();

						try {
							synchronized(field) {
								field = updateBitField(field, pieceNum);
								p.setBitfield(field);
							}
						} catch (Exception e) {
							System.err.println(e);
						}
					}
				}

				System.out.println("Have message received from " + remotePeerID + " for piece " + pieceNum);
				System.out.println();
				
				// write the logger file
				loggerInformation.receiveHaveFunction(remotePeerID, pieceNum);
			}
			
			else if(type == 6) {
				//request
				
				byte[] temp = new byte[4];
				
				int x = 5;
				for (int i = 0; i < temp.length; i++) {
					temp[i] = message[x];
					x++;
				}
				int pieceNum = ByteBuffer.wrap(temp).getInt();
				int i = pieceNum;
				
				//send piece
				pieceFunction piece = mainPeerProcessing.hm.get(i);
				
				//debugging start
				System.out.println("piece " + pieceNum + " requested from " + remotePeerID);
				System.out.println();
				//debugging end
				
				synchronized (mainPeerProcessing.messageBody) {
					messageStructure m = new messageStructure();
					m.setSocket(socket);
					m.setMessage(piece.piece);
					mainPeerProcessing.messageBody.add(m);
				}
			}
			
			else if(type == 7) {
				//piece
				
				byte index[] = new byte[4];
				
				int x = 5;
				for (int i = 0; i < index.length; i++) {
					index[i] = message[x];
					x++;
				}
				int pieceIndex = ByteBuffer.wrap(index).getInt();
				int num = pieceIndex;
				byte[] piece = new byte[message.length - 9];
				for (int i = 0; i < piece.length; i++) {
					piece[i] = message[x];
					x++;
				}
			
				if(piece.length == pieceSize && !mainPeerProcessing.hm.containsKey(num)) {
					
					pieceFunction p1 = new pieceFunction(pieceIndex, piece);
					
					try {
						synchronized(mainPeerProcessing.hm) {
							mainPeerProcessing.hm.put(num, p1);
							Thread.sleep(30);
						}
					} catch (Exception e) {
						System.err.println(e);
					}
					
					
					System.out.println("piece " + pieceIndex + " received from " + remotePeerID);
					System.out.println();
					
					// write the logger file
					loggerInformation.downloadPiece(remotePeerID, pieceIndex);
					
					
					try {
						synchronized(bitFieldLogger.bitfield) {					
							bitFieldLogger.updateBitField(pieceIndex);
							Thread.sleep(20);
						}
					} 
					catch (InterruptedException e) {
						System.err.println(e);
					}
					
					//send have to all peers
					haveFunction have = new haveFunction(pieceIndex);
					
					ListIterator<peerBin> it = mainPeerProcessing.peers.listIterator();
					
					while(it.hasNext()) {
						peerBin peer = (peerBin)it.next();
						//Socket s = peer.getSocket();
						
						synchronized (mainPeerProcessing.messageBody) {
							messageStructure m = new messageStructure();
							m.setSocket(peer.getSocket());
							m.setMessage(have.haveFunction);
							mainPeerProcessing.messageBody.add(m);
						}
					}
				}
				
			}
			
			else if(type == 8) {
				
				synchronized(mainPeerProcessing.hasDownloadedCompleteFile) {
					
					ListIterator<peerHasCompleteFile> it = mainPeerProcessing.hasDownloadedCompleteFile.listIterator();
					
					while(it.hasNext()) {
						peerHasCompleteFile peer = (peerHasCompleteFile)it.next();
						
						if(peer.getSocket().equals(socket)) {
							peer.setHasDownLoadedCompleteFile(true);
							break;
						}
					}
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						System.err.println(e);
					}
				}
			}
		}
	}
	
}