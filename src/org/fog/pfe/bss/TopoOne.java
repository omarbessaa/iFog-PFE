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

public class TopoOne {

	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	
	public static void main(String[] args) {
		Log.printLine("Demmarage de simulation ...");

		try {
			Log.disable();
			
			int num_user = 1; // number of cloud users
			
			Calendar calendar = Calendar.getInstance();
			
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			String appId = "TopoOne"; // identifier of the application
			
			FogBroker broker = new FogBroker("broker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
			
	//*******************************************************************
			//cloud 
			FogDevice cloud = createFogDevice("cloud", 448000 , 40000, 100, 1000000, 0, 0.0, 16*103, 16*38.25);
			cloud.setParentId(-1);
			fogDevices.add(cloud);
			moduleMapping.addModuleToDevice("DataCenter", "cloud");
			
			//Noeud Level 1
			FogDevice nd1 = createFogDevice("L1:N1", 2000, 4000, PrintConfig.BANDWITH, 1000000, 1, 0.0, 103, 83.25);
			nd1.setParentId(cloud.getId());
			nd1.setUplinkLatency(1);
			fogDevices.add(nd1);
			
			FogDevice nd2 = createFogDevice("L1:N2", 2000, 4000, PrintConfig.BANDWITH, 1000000, 1, 0.0, 103, 83.25);
			nd2.setParentId(cloud.getId());
			nd2.setUplinkLatency(1);
			fogDevices.add(nd2);
			
			
			//Noeud Level 2
			FogDevice ndd1 = createFogDevice("L2:N1", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			ndd1.setParentId(nd1.getId());
			ndd1.setUplinkLatency(0.00006);
			fogDevices.add(ndd1);
			AjouterCapteurs(ndd1.getName(), ndd1.getId(), broker.getId(), appId);
			
			FogDevice ndd2 = createFogDevice("L2:N2", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			ndd2.setParentId(nd1.getId());
			ndd2.setUplinkLatency(0.00006);
			fogDevices.add(ndd2);
			AjouterCapteurs(ndd2.getName(), ndd2.getId(), broker.getId(), appId);
			
			FogDevice ndd3 = createFogDevice("L2:N3", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			ndd3.setParentId(nd2.getId());
			ndd3.setUplinkLatency(0.00006);
			fogDevices.add(ndd3);
			AjouterCapteurs(ndd3.getName(), ndd3.getId(), broker.getId(), appId);
			
			FogDevice ndd4 = createFogDevice("L2:N4", 2000, 4000, PrintConfig.BANDWITH, 1000000, 2, 0.0, 103, 83.25);
			ndd4.setParentId(nd2.getId());
			ndd4.setUplinkLatency(0.00006);
			fogDevices.add(ndd4);
			AjouterCapteurs(ndd4.getName(), ndd4.getId(), broker.getId(), appId);
			
			
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
	
private static Application createApplication(String appId, int userId){
		
		Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)
		
		/*
		 * Adding modules (vertices) to the application model (directed graph)
		 */
		application.addAppModule("DataCenter", 10); // adding module to the application model
		/*
		 * Connecting the application modules (vertices) in the application model (directed graph) with edges
		 */
			application.addAppEdge("SENSOR", "DataCenter", 0, 5000000, "SENSOR", Tuple.UP, AppEdge.SENSOR);
		return application;
	}

	private static void AjouterCapteurs(String deviceName, int parentId, int userId, String appId) {
		
		FogDevice obj1 = createFogDevice(deviceName+":OBJ1", 2000, 4000, 10000, 10000, 3, 0.0, 103, 83.25);
		obj1.setParentId(parentId);
		obj1.setUplinkLatency(0.0000006);
		fogDevices.add(obj1);
		Sensor s1 = new Sensor(deviceName+":S1", "SENSOR", userId, appId, new DeterministicDistribution(PrintConfig.DATA_TRANSMISSION_TIME));
		sensors.add(s1);
		s1.setGatewayDeviceId(obj1.getId());
		s1.setLatency(0.0);
		
		FogDevice obj2 = createFogDevice(deviceName+":OBJ2", 2000, 4000, 1000000, 1000000, 3, 0.0, 103, 83.25);
		obj2.setParentId(parentId);
		obj2.setUplinkLatency(0.0000006);
		fogDevices.add(obj2);
		Sensor s2 = new Sensor(deviceName+":S2", "SENSOR", userId, appId, new DeterministicDistribution(PrintConfig.DATA_TRANSMISSION_TIME));
		sensors.add(s2);
		s2.setGatewayDeviceId(obj2.getId());
		s2.setLatency(0.0);
		
		FogDevice obj3 = createFogDevice(deviceName+":OBJ3", 2000, 4000, 1000000, 1000000, 3, 0.0, 103, 83.25);
		obj3.setParentId(parentId);
		obj3.setUplinkLatency(0.0000006);
		fogDevices.add(obj3);
		Sensor s3 = new Sensor(deviceName+":S3", "SENSOR", userId, appId, new DeterministicDistribution(PrintConfig.DATA_TRANSMISSION_TIME));
		sensors.add(s3);
		s3.setGatewayDeviceId(obj3.getId());
		s3.setLatency(0.0);
	}

}
