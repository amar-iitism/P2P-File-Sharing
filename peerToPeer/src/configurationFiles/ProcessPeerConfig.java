package configurationFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ProcessPeerConfig {

	// define data members

	// Initialize the file name
	public String fileName; // = "src/configurationFiles/PeerInfo.cfg";
	public int peerId;
	public int selfId;
	public String peerHostname;
	public int peerPort;
	public boolean completeFile;
	public ArrayList<Integer> totalPeerId = new ArrayList<Integer>();;

	// constructor
	public ProcessPeerConfig(int selfId) {
		this.selfId = selfId;
	}

	public void getPeerProp() throws NumberFormatException, IOException {
		
		fileName = (new File(System.getProperty("user.dir")).getParent() + "\\peerToPeer\\src\\configurationFiles\\PeerInfo.cfg");
		// read the peerInformation
		BufferedReader bufferReader = new BufferedReader(new FileReader(fileName));
		String iterator = null;

		while ((iterator = bufferReader.readLine()) != null) {
			// covert the read line into a string array
			String[] peerInfo = iterator.split(" ");

			// chk if it's selfId information
			if (selfId == Integer.parseInt(peerInfo[0])) {
				peerId = Integer.parseInt(peerInfo[0]);
				peerHostname = peerInfo[1];
				peerPort = Integer.parseInt(peerInfo[2]);

				if (peerInfo[3].equals("1"))
					completeFile = true;
				else
					completeFile = false;
			} else {
				// if not the selfId, add that peerId to the ArrayList
				totalPeerId.add(Integer.parseInt(peerInfo[0]));
			}
		}
		bufferReader.close();
	}

	public ArrayList<String[]> getPeerDetails() throws NumberFormatException, IOException {

		ArrayList<String[]> peerDetails = new ArrayList<String[]>();
		
		BufferedReader inputReader;
		inputReader = new BufferedReader(new FileReader(fileName));
		
		String curentLine = null;
		
		while ((curentLine = inputReader.readLine()) != null) {
			String[] info = curentLine.split(" ");

			if (selfId != Integer.parseInt(info[0])) {
				peerDetails.add(info);
			}
			else {
				break;
			}
		}
		inputReader.close();
		return peerDetails;
	}

	/*
	 * public static void main(String[] args) throws NumberFormatException,
	 * IOException { // TODO Auto-generated method stub // test peerid -- 1002
	 * ProcessPeerConfig ppc = new ProcessPeerConfig(1002); ppc.getPeerProp();
	 * System.out.println("SelfId is:\t"+ppc.selfId);
	 * System.out.println("PeerId is:\t"+ppc.peerId);
	 * System.out.println("PeerHostname is:\t"+ppc.peerHostname);
	 * System.out.println("PeerPort is:\t"+ppc.peerPort);
	 * System.out.println("CompleteFile is:\t"+ppc.completeFile);
	 * System.out.println("AllPeerId are:\t"+ppc.allPeerId);
	 * 
	 * }
	 */
}
