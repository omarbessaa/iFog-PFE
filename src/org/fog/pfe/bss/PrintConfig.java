package org.fog.pfe.bss;

import java.util.ArrayList;
import java.util.List;


public class PrintConfig {

	public static int packetCounter = 0;
	
	
	public static int LEVEL_PRINT = 1;
	public static int PACKET_PRIORITY_PRINT = 3;
	
	public static int DROP_LEVEL = 4;
	public static int DROP_PRIORITY = 3;
	
	public static int NUM_NODE_PER_LEVEL = 4;
	public static int NUM_OF_LEVEL = 5;
	public static int NUM_OF_SENSORS = 3;
	public static double DATA_TRANSMISSION_TIME = 1.3;
	
	public static int BANDWITH = 8000000;
	
	
	public static double ld = 0;
	public static double energie = 0;
	public static double BP = 0;

	public static double ld2 = 0;
	public static double energie2 = 0;
	public static double BP2 = 0;
	
	public static double ld3 = 0;
	public static double energie3 = 0;
	public static double BP3 = 0;
	
	public static double ld4 = 0;
	public static double energie4 = 0;
	public static double BP4 = 0;
	
	
	public static List<Mesure> msr = new ArrayList<Mesure>();
	//**************************************************************
	public static int getPacketCounter() {
		return packetCounter;
	}
	public static void setLEVEL_PRINT(int lEVEL_PRINT) {
		LEVEL_PRINT = lEVEL_PRINT;
	}
	public static void setPACKET_PRIORITY_PRINT(int pACKET_PRIORITY_PRINT) {
		PACKET_PRIORITY_PRINT = pACKET_PRIORITY_PRINT;
	}
	public static void setPacketCounter() {
		packetCounter++;
	}
	
}
