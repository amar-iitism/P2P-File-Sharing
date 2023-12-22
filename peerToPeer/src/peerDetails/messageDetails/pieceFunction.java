package peerDetails.messageDetails;

import java.nio.ByteBuffer;

public class pieceFunction {
	
	public byte[] piece;
	private byte[] messagePieceLength = new byte[4];
	private byte pieceType = 7;
	private byte[] pieceIndex = new byte[4];
	private byte[] pieceMessage;
	
	public pieceFunction(int index, byte[] data) {
		
		pieceMessage = data;
		int data_len = pieceMessage.length;
		pieceIndex = ByteBuffer.allocate(4).putInt(index).array();
		messagePieceLength = ByteBuffer.allocate(4).putInt(4 + data_len).array();
		piece = new byte[9 + data_len];
		
		int i = 0;
		for (i = 0; i < messagePieceLength.length; i++) {
			piece[i] = messagePieceLength[i];
		}
		
		piece[i] = pieceType;
		
		for (int j = 0; j < pieceIndex.length; j++) {
			i++;
			piece[i] = pieceIndex[j];
		}
		
		for (int j = 0; j < pieceMessage.length; j++) {
			i++;
			piece[i] = pieceMessage[j];
		}
		
	}


}
