import java.util.ArrayList;
import java.util.HashMap;

public class Constants{
	public static final String SERVER1_ADDRESS = "localhost";
	public static final String SERVER1_PORT = "8091";
	public static final String SERVER2_ADDRESS = "localhost";
	public static final String SERVER2_PORT = "8092";
	public static final String SERVER3_ADDRESS = "localhost";
	public static final String SERVER3_PORT = "8093";
	public static final String SERVER4_ADDRESS = "localhost";
	public static final String SERVER4_PORT = "8094";
	public static final String SERVER5_ADDRESS = "localhost";
	public static final String SERVER5_PORT = "8095";
	public static final HashMap<String, ArrayList<ServersDistance>> serverDistanceInfo;
	static
	{
		serverDistanceInfo = new HashMap<String, ArrayList<ServersDistance>>();
		ArrayList<ServersDistance> serversDistances1 = new ArrayList<ServersDistance>();
		serversDistances1.add(new ServersDistance("2",(float) 60.4));
		serversDistances1.add(new ServersDistance("3",(float) 22.0));
	    serversDistances1.add(new ServersDistance("4",(float) 26.9));
		serversDistances1.add(new ServersDistance("5",(float) 44.7));
		serverDistanceInfo.put("1", serversDistances1);
		ArrayList<ServersDistance> serversDistances2 = new ArrayList<ServersDistance>();
		serversDistances2.add(new ServersDistance("1",(float) 60.4));
		serversDistances2.add(new ServersDistance("3",(float) 77.7));
	    serversDistances2.add(new ServersDistance("4",(float) 82.7));
		serversDistances2.add(new ServersDistance("5",(float) 95.5));
		serverDistanceInfo.put("2", serversDistances2);
		ArrayList<ServersDistance> serversDistances3 = new ArrayList<ServersDistance>();
		serversDistances3.add(new ServersDistance("1",(float) 22.0));
		serversDistances3.add(new ServersDistance("2",(float) 77.7));
	    serversDistances3.add(new ServersDistance("4",(float) 19.7));
		serversDistances3.add(new ServersDistance("5",(float) 37.3));
		serverDistanceInfo.put("3", serversDistances3);
		ArrayList<ServersDistance> serversDistances4 = new ArrayList<ServersDistance>();
		serversDistances4.add(new ServersDistance("1",(float) 26.9));
		serversDistances4.add(new ServersDistance("2",(float) 82.7));
	    serversDistances4.add(new ServersDistance("3",(float) 19.7));
		serversDistances4.add(new ServersDistance("5",(float) 53.6));
		serverDistanceInfo.put("4", serversDistances4);
		
		ArrayList<ServersDistance> serversDistances5 = new ArrayList<ServersDistance>();
		serversDistances5.add(new ServersDistance("1",(float) 44.7));
		serversDistances5.add(new ServersDistance("2",(float) 95.5));
	    serversDistances5.add(new ServersDistance("3",(float) 37.3));
		serversDistances5.add(new ServersDistance("4",(float) 53.6));
		serverDistanceInfo.put("5", serversDistances5);
	}
}