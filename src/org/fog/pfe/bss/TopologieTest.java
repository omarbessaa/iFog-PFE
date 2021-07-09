package org.fog.pfe.bss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;

public class TopologieTest {

	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	static int curent_level = 0;
	static List<FogDevice> lstTemp = new ArrayList<FogDevice>();
	
	public static void main(String[] args) {

		Log.printLine("Demmarage de simulation ...");

		try {
			Log.disable();
			
			int num_user = 1; // number of cloud users
			
			Calendar calendar = Calendar.getInstance();
			
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			String appId = "TopologieTest"; // identifier of the application
			
			FogBroker broker = new FogBroker("broker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
			
	//*******************************************************************
			//architecture
			creerTopo();
			//les capteurs
			creerIoT(broker.getId(), appId);
			//Update Devices' list
			for (FogDevice dvc : lstTemp) {
				fogDevices.add(dvc);
			}
			
			//ajout des VM
			moduleMapping.addModuleToDevice("Centre_de_Surveillance", "cloud");
	//*******************************************************************
			
			Controller controller = new Controller("master-controller", fogDevices, sensors, actuators);
			
			controller.submitApplication(application, 0, (new ModulePlacementMapping(fogDevices, application, moduleMapping)));

			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

			CloudSim.startSimulation();

			//CloudSim.stopSimulation();

			Log.printLine("Fin de simulation !");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//create FogDevice (help consrecteur)
	private static FogDevice createFogDevice(String nodeName, long mips,int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) 
	{
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new StreamOperatorScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
				arch, os, vmm, host, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fogdevice.setLevel(level);
		return fogdevice;
	}
	
	//createApplication : partie logique
	
@SuppressWarnings("serial")
private static Application createApplication(String appId, int userId){
		
		Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)
		
		/*
		 * Adding modules (vertices) to the application model (directed graph)
		 */
		application.addAppModule("Centre_de_Surveillance", 10); // adding module to the application model
		
		
		/*
		 * Connecting the application modules (vertices) in the application model (directed graph) with edges
		 */
			application.addAppEdge("DATA", "Centre_de_Surveillance", 0, 2500000, "DATA", Tuple.UP, AppEdge.SENSOR);
			//application.addAppEdge("DataCenter", "ACTR", 3000, 5000, "UpDate", Tuple.DOWN, AppEdge.ACTUATOR);
			//application.addAppEdge("DataCenter", "SENSOR", 3000, 1000, "Repense", Tuple.DOWN, AppEdge.SENSOR);
		
			
			
			
		/*
		 * Defining the input-output relationships (represented by selectivity) of the application modules. 
		 */
		   //application.addTupleMapping("DataCenter", "SENSOR", "Repense", new FractionalSelectivity(1));
	
		/*
		 * Defining application loops to monitor the latency of. 
		*/ 
		final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("DATA"); add("Centre_de_Surveillance");}});
		List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
		application.setLoops(loops);
		
		return application;
	}

//********************************************************************

	private static void creerTopo() {
		//cloud 
		FogDevice cloud = createFogDevice("cloud", 448000 , 40000, 100, 1000000, 0, 0.0, 16*103, 16*38.25);
		cloud.setParentId(-1);
		fogDevices.add(cloud);
		
		
		for (int i = 1; i <= PrintConfig.NUM_NODE_PER_LEVEL; i++) {
			creerNoeud(cloud.getId(), i, 1);
			curent_level = 0;
		}
	}

	private static void creerNoeud(int parentId, int num, double latency) {
		curent_level++;
		FogDevice nd = createFogDevice("L"+curent_level+":N"+num, 2000, 4000, PrintConfig.BANDWITH, 1000000, curent_level, 0.0, 103, 83.25);
		nd.setParentId(parentId);
		nd.setUplinkLatency(latency);
		fogDevices.add(nd);
		
		if ( curent_level < PrintConfig.NUM_OF_LEVEL) {
			//creerNoeud(nd.getId(), num, curent_level == PrintConfig.NUM_OF_LEVEL ? 0.01 : 1);
			creerNoeud(nd.getId(), num, 0.0000006);
			
		}
	}
	
	private static void creerIoT(int userId, String appId) {

		for (FogDevice dvc : fogDevices) {
				if (dvc.getLevel() == PrintConfig.NUM_OF_LEVEL) {
					for (int i = 1; i <= PrintConfig.NUM_OF_SENSORS; i++){
					AjouterCapteur(dvc.getName(), dvc.getId(), i, userId, appId);
				}
			}
		}
	}

	private static void AjouterCapteur(String deviceName, int parentId, int numObj, int userId, String appId) {
	
		FogDevice obj = createFogDevice(deviceName+":OBJ"+numObj, 2000, 4000, 2000000, 2000000, ((PrintConfig.NUM_OF_LEVEL) + 1), 0.0, 103, 83.25);
		obj.setParentId(parentId);
		obj.setUplinkLatency(0.0000006);
		lstTemp.add(obj);
		
		Sensor capt = new Sensor(deviceName+":CAPT"+numObj, "DATA", userId, appId, new DeterministicDistribution(PrintConfig.DATA_TRANSMISSION_TIME));
		sensors.add(capt);
		capt.setGatewayDeviceId(obj.getId());
		capt.setLatency(0.0);
	
	}
	

}
