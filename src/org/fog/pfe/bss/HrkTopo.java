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

public class HrkTopo {
	
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	static List<FogDevice> lstTemp = new ArrayList<FogDevice>();

	public static void main(String[] args) {

		Log.printLine("Demmarage de simulation ...");

		try {
			Log.disable();
			
			int num_user = 1; // number of cloud users
			
			Calendar calendar = Calendar.getInstance();
			
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			String appId = "HRKTopo"; // identifier of the application
			
			FogBroker broker = new FogBroker("broker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
			
	//*******************************************************************
			//cloud 
			FogDevice cloud = createFogDevice("cloud", 448000 , 40000, 100, 10000000, 0, 0.0, 16*103, 16*38.25);
			cloud.setParentId(-1);
			fogDevices.add(cloud);
			

			//Noeud Level 1
			FogDevice l1n1 = createFogDevice("L1:N1", 2000, 4000, PrintConfig.BANDWITH, 1000000, 1, 0.0, 103, 83.25);
			l1n1.setParentId(cloud.getId());
			l1n1.setUplinkLatency(1);
			fogDevices.add(l1n1);
			
			FogDevice l1n2 = createFogDevice("L1:N2", 2000, 4000, PrintConfig.BANDWITH, 1000000, 1, 0.0, 103, 83.25);
			l1n2.setParentId(cloud.getId());
			l1n2.setUplinkLatency(1);
			fogDevices.add(l1n2);
			
			FogDevice l1n3 = createFogDevice("L1:N3", 2000, 4000, PrintConfig.BANDWITH, 1000000, 1, 0.0, 103, 83.25);
			l1n3.setParentId(cloud.getId());
			l1n3.setUplinkLatency(1);
			fogDevices.add(l1n3);
			
			//Noeud Level 2
			FogDevice l2n1 = createFogDevice("L2:N1", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			l2n1.setParentId(l1n1.getId());
			l2n1.setUplinkLatency(0.00006);
			fogDevices.add(l2n1);
			
			FogDevice l2n2 = createFogDevice("L2:N2", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			l2n2.setParentId(l1n1.getId());
			l2n2.setUplinkLatency(0.00006);
			fogDevices.add(l2n2);
			
			FogDevice l2n3 = createFogDevice("L2:N3", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			l2n3.setParentId(l1n2.getId());
			l2n3.setUplinkLatency(0.00006);
			fogDevices.add(l2n3);
			
			FogDevice l2n4 = createFogDevice("L2:N4", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			l2n4.setParentId(l1n3.getId());
			l2n4.setUplinkLatency(0.00006);
			fogDevices.add(l2n4);
			
			
			//Noeud Level 3
			FogDevice l3n1 = createFogDevice("L3:N1", 2000, 4000, PrintConfig.BANDWITH, 1000000, 3, 0.0, 103, 83.25);
			l3n1.setParentId(l2n1.getId());
			l3n1.setUplinkLatency(0.00006);
			fogDevices.add(l3n1);
			
			FogDevice l3n2 = createFogDevice("L3:N2", 2000, 4000, PrintConfig.BANDWITH, 1000000, 3, 0.0, 103, 83.25);
			l3n2.setParentId(l2n2.getId());
			l3n2.setUplinkLatency(0.00006);
			fogDevices.add(l3n2);
			
			FogDevice l3n3 = createFogDevice("L3:N3", 2000, 4000, PrintConfig.BANDWITH, 1000000, 3, 0.0, 103, 83.25);
			l3n3.setParentId(l2n2.getId());
			l3n3.setUplinkLatency(0.00006);
			fogDevices.add(l3n3);
			
			FogDevice l3n4 = createFogDevice("L3:N4", 2000, 4000, PrintConfig.BANDWITH, 1000000, 3, 0.0, 103, 83.25);
			l3n4.setParentId(l2n3.getId());
			l3n4.setUplinkLatency(0.00006);
			fogDevices.add(l3n4);
			
			FogDevice l3n5 = createFogDevice("L3:N5", 2000, 4000, PrintConfig.BANDWITH, 1000000, 3, 0.0, 103, 83.25);
			l3n5.setParentId(l2n3.getId());
			l3n5.setUplinkLatency(0.00006);
			fogDevices.add(l3n5);
			
			FogDevice l3n6 = createFogDevice("L3:N6", 2000, 4000, PrintConfig.BANDWITH, 1000000, 3, 0.0, 103, 83.25);
			l3n6.setParentId(l2n4.getId());
			l3n6.setUplinkLatency(0.00006);
			fogDevices.add(l3n6);
			
			//Noeud Level 4
			FogDevice l4n1 = createFogDevice("L4:N1", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n1.setParentId(l3n1.getId());
			l4n1.setUplinkLatency(0.0000006);
			fogDevices.add(l4n1);
			
			FogDevice l4n2 = createFogDevice("L4:N2", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n2.setParentId(l3n1.getId());
			l4n2.setUplinkLatency(0.0000006);
			fogDevices.add(l4n2);
			
			FogDevice l4n3 = createFogDevice("L4:N3", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n3.setParentId(l3n2.getId());
			l4n3.setUplinkLatency(0.0000006);
			fogDevices.add(l4n3);
			
			FogDevice l4n4 = createFogDevice("L4:N4", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n4.setParentId(l3n3.getId());
			l4n4.setUplinkLatency(0.0000006);
			fogDevices.add(l4n4);
			
			FogDevice l4n5 = createFogDevice("L4:N5", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n5.setParentId(l3n4.getId());
			l4n5.setUplinkLatency(0.0000006);
			fogDevices.add(l4n5);
			
			FogDevice l4n6 = createFogDevice("L4:N6", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n6.setParentId(l3n5.getId());
			l4n6.setUplinkLatency(0.0000006);
			fogDevices.add(l4n6);
			
			FogDevice l4n7 = createFogDevice("L4:N7", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n7.setParentId(l3n6.getId());
			l4n7.setUplinkLatency(0.0000006);
			fogDevices.add(l4n7);
			
			FogDevice l4n8 = createFogDevice("L4:N8", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n8.setParentId(l3n6.getId());
			l4n8.setUplinkLatency(0.0000006);
			fogDevices.add(l4n8);
			
			FogDevice l4n9 = createFogDevice("L4:N9", 2000, 4000, PrintConfig.BANDWITH, 1000000, 4, 0.0, 103, 83.25);
			l4n9.setParentId(l3n6.getId());
			l4n9.setUplinkLatency(0.0000006);
			fogDevices.add(l4n9);
			
			//les captuers
			creerIoT(broker.getId(), appId);
			//Update Devices' list
			for (FogDevice dvc : lstTemp) {
				fogDevices.add(dvc);
			}
			
			//ajout des VM
			//moduleMapping.addModuleToDevice("Centre_de_Surveillance", "cloud");
			moduleMapping.addModuleToDevice("DataCenter", "cloud");
  //******************************************************************************************************************			
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
			application.addAppModule("DataCenter", 10); // adding module to the application model
			
			
			/*
			 * Connecting the application modules (vertices) in the application model (directed graph) with edges
			 */
				application.addAppEdge("DATA", "DataCenter", 0, 2000000, "DATA", Tuple.UP, AppEdge.SENSOR);
				//application.addAppEdge("DataCenter", "ACTR", 3000, 5000, "UpDate", Tuple.DOWN, AppEdge.ACTUATOR);
				//application.addAppEdge("DataCenter", "SENSOR", 3000, 1000, "Repense", Tuple.DOWN, AppEdge.SENSOR);
			
				
				
				
			/*
			 * Defining the input-output relationships (represented by selectivity) of the application modules. 
			 */
			   //application.addTupleMapping("DataCenter", "SENSOR", "Repense", new FractionalSelectivity(1));
		
			/*
			 * Defining application loops to monitor the latency of.
			 */
			final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("DATA"); add("DataCenter");}});
			List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
			application.setLoops(loops);
			
			return application;
		}

	
	private static void creerIoT(int userId, String appId) {

		for (FogDevice dvc : fogDevices) {
				if (dvc.getLevel() == 4) {
					for (int i = 1; i <= PrintConfig.NUM_OF_SENSORS; i++){
					AjouterCapteur(dvc.getName(), dvc.getId(), i, userId, appId);
				}
			}
		}
	}

	private static void AjouterCapteur(String deviceName, int parentId, int numObj, int userId, String appId) {
	
		FogDevice obj = createFogDevice(deviceName+":OBJ"+numObj, 2000, 4000, 2000000, 2000000, 5, 0.0, 103, 83.25);
		obj.setParentId(parentId);
		obj.setUplinkLatency(0.0000006);
		lstTemp.add(obj);
		
		Sensor capt = new Sensor(deviceName+":CAPT"+numObj, "DATA", userId, appId, new DeterministicDistribution(PrintConfig.DATA_TRANSMISSION_TIME));
		sensors.add(capt);
		capt.setGatewayDeviceId(obj.getId());
		capt.setLatency(0.0);
	
	}
}
