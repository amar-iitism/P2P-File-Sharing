package logInformation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedList;

import peerDetails.*;

public class loggerInformation {

	public int logPeerId;
	public int logFileName;
	private static int selfPeerID;
	private static File file;
	public static boolean fileFlags = false;
	public static boolean wholeFileFlag = false;
	private static BufferedWriter out;
	private static int totalPieces = 0;
	public static LinkedList<Integer> fileWriteOperation = new LinkedList<Integer>();

	// constructor
	public loggerInformation(int logPeerId) {
		this.logPeerId = logPeerId;
	}

	public static void initializeLogger(loggerInformation obj) throws IOException {
		// just create the logger file right now
		String logPath = "src/logInformation/";
		String logName = "log_" + Integer.toString(obj.logPeerId) + ".log";

		// Initialize the file name
		// String fileName = "src/configurationFiles/PeerInfo.cfg";

		File fileObj = new File(logPath + logName);
		if (fileObj.createNewFile()) {
			// create file
			System.out.println("File is created");
		} else {
			// file already exist
		}
	}

	/*
	 * public static void main(String[] args) throws IOException { loggerInformation
	 * logInfo = new loggerInformation(1001);
	 * loggerInformation.initializeLogger(logInfo); }
	 */
	public static void loggerStart(int PeerID) {

		selfPeerID = PeerID;
		String fileName = (new File(System.getProperty("user.dir")).getParent() + "\\peerToPeer\\src\\logInformation\\log_peer_" + selfPeerID + ".log");

		file = new File(fileName);

		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}
	}

	public static void makeTCPConnectionFunction(int PeerID) {

		try {
			String date = new Date().toString();
			String s = date + " : peerBin " + selfPeerID + " makes a connection to Peer " + PeerID + ".";
			out.append(s);
			out.newLine();
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void madeTCPConnectionFunction(int PeerID) {

		try {
			String date = new Date().toString();
			String s = date + " : peerBin " + selfPeerID + " is connected from Peer " + PeerID + ".";
			out.append(s);
			out.newLine();
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void receiveHaveFunction(int PeerID, int pieceIndex) {

		try {
			String date = new Date().toString();
			String s = date + " : peerBin " + selfPeerID + " received the 'have' message from Peer " + PeerID
					+ " for the piece " + pieceIndex + ".";
			out.append(s);
			out.newLine();
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void interestedReceiveFunction(int PeerID) {
		try {
			String date = new Date().toString();
			String s = date + " : peerBin " + selfPeerID + " received the 'interested' message from Peer " + PeerID
					+ ".";
			out.append(s);
			out.newLine();
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void notInterestedReceiveFunction(int PeerID) {

		try {
			String date = new Date().toString();
			String s = date + " : peerBin " + selfPeerID + " received the 'not interested' message from Peer " + PeerID
					+ ".";
			out.append(s);
			out.newLine();
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void downloadPiece(int PeerID, int pieceIndex) {

		totalPieces++;
		try {
			String date = new Date().toString();
			String s = date + " : peerBin " + selfPeerID + " has downloaded the piece " + pieceIndex + " from Peer "
					+ PeerID + ".";
			out.append(s);
			out.newLine();
			s = "Now  the number of pieces it has is " + totalPieces;
			out.append(s);
			out.newLine();
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void downloadFinished() {

		if (fileFlags == true) {

			try {
				String date = new Date().toString();
				String s = date + " : peerBin " + selfPeerID + " has downloaded the complete file.";
				out.append(s);
				out.newLine();
				out.newLine();
				out.flush();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public static void closedLogger() {
		try {
			out.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
