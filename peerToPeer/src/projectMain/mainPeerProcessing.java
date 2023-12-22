package projectMain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import configurationFiles.ProcessConfigurationCfg;
import configurationFiles.ProcessFileParser;
import configurationFiles.ProcessPeerConfig;
import logInformation.loggerInformation;
import peerDetails.messageStructure;
import peerDetails.peerBin;
import peerDetails.peerHasCompleteFile;
import peerDetails.messageDetails.bitFieldLogger;
import projectMain.serverClientProcess.clientServerListner;
import projectMain.serverClientProcess.onlyServer;
import peerDetails.messageDetails.pieceFunction;

public class mainPeerProcessing {

	public static String fileName;
	public long fileSize;
	public long pieceSize;
	public int getPieces;
	public static ArrayList<Integer> totalPeerId;
	public static ArrayList<peerBin> peers = new ArrayList<peerBin>();
	public int peerId;
	public int peerPort;
	public boolean completeFile;
	public static HashMap <Integer, pieceFunction> hm;
	public static LinkedList <messageStructure> messageBody = new LinkedList <messageStructure>();
	public static ArrayList<peerHasCompleteFile> hasDownloadedCompleteFile = new ArrayList<peerHasCompleteFile>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		mainPeerProcessing peerProcConfig = new mainPeerProcessing();

		// A. Get the configuration
		// 1. read the configuration files
		ProcessConfigurationCfg proConfig = new ProcessConfigurationCfg();
		proConfig.getConfigProp();

		// 2. get the file configurations
		peerProcConfig.fileName = proConfig.fileName;
		peerProcConfig.fileSize = proConfig.fileSize;
		peerProcConfig.pieceSize = proConfig.pieceSize;

		// 3. chk for pieces
		peerProcConfig.getPieces = (int) (peerProcConfig.fileSize / peerProcConfig.pieceSize);

		// B. Get the peer details
		ProcessPeerConfig getPeerInfo = new ProcessPeerConfig(Integer.parseInt(args[0])); // passing static value 1001,
																							// args[0]
		getPeerInfo.getPeerProp();
		peerProcConfig.peerId = getPeerInfo.peerId;
		peerProcConfig.peerPort = getPeerInfo.peerPort;
		peerProcConfig.completeFile = getPeerInfo.completeFile;
		peerProcConfig.totalPeerId = getPeerInfo.totalPeerId;
		// C. Set the bit-field
		bitFieldLogger.setBitfield(peerProcConfig.completeFile, peerProcConfig.getPieces);

		// log the information
		loggerInformation.loggerStart(peerProcConfig.peerId);

		// D. decide for requesting or responding entity
		if (peerProcConfig.completeFile) {
			// a server -- to add here later

			ProcessFileParser reader = new ProcessFileParser(peerProcConfig.peerId, peerProcConfig.pieceSize, fileName);
			hm = reader.readFile();
			
			// initiate server connection
			onlyServer peerListener = new onlyServer(peerProcConfig.peerPort, peerProcConfig.peerId,
					peerProcConfig.getPieces, peerProcConfig.completeFile, peerProcConfig.fileSize,
					peerProcConfig.pieceSize);
			peerListener.start();

		} else {
			// a client and a server
			hm = new HashMap <Integer, pieceFunction>();

			// initiate server connection
			onlyServer peerListener = new onlyServer(peerProcConfig.peerPort, peerProcConfig.peerId,
					peerProcConfig.getPieces, peerProcConfig.completeFile, peerProcConfig.fileSize,
					peerProcConfig.pieceSize);
			peerListener.start();
			
			// initiate client connection
			clientServerListner connect = new clientServerListner(peerProcConfig.peerId, peerProcConfig.getPieces,
					peerProcConfig.completeFile, peerProcConfig.fileSize, peerProcConfig.pieceSize);
			connect.start();
		}
	}

}
