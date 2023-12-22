package peerDetails.messageDetails;

import java.nio.ByteBuffer;

public class bitFieldLogger {

	private static int allPieces;
	private static boolean isCompleteFile = false;
	private static byte[] payload;
	private static byte[] messageLength = new byte[4];
	private static byte type = 5;
	public static byte[] bitfield;

	public static void setBitfield(boolean hasFile, int num) {
		isCompleteFile = hasFile;
		allPieces = num;
		int payloadLength = (int) Math.ceil((double) allPieces / 8);
		int remaining = allPieces % 8;
		messageLength = ByteBuffer.allocate(4).putInt(payloadLength).array();
		payload = new byte[payloadLength];
		bitfield = new byte[payloadLength + 5];

		int i = 0;
		for (; i < messageLength.length; i++) {
			bitfield[i] = messageLength[i];
		}

		bitfield[i] = type;

		if (isCompleteFile == false) {

			for (int j = 0; j < payload.length; j++) {
				i++;
				bitfield[i] = 0;
			}
		}

		else {
			for (int j = 0; j < payload.length - 1; j++) {

				i++;

				for (int k = 0; k < 8; k++) {
					bitfield[i] = (byte) (bitfield[i] | (1 << k));
				}
			}

			i++;
			for (int j = 0; j < remaining; j++) {
				bitfield[i] = (byte) (bitfield[i] | (1 << (7 - j)));
			}
		}
	}

	public static void updateBitField(int pieceIndex) {
		int i = (pieceIndex - 1) / 8;
		int k = 7 - ((pieceIndex - 1) % 8);
		bitfield[i + 5] = (byte) (bitfield[i + 5] | (1 << k));
	}

}
