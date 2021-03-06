package simulation;
import java.io.* ;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class ServerSimulatingHouse {
	public int serverId; //this house server id
	public int serverPort; // this house server runs on a port
	public DatagramSocket server_socket; // this house will need a socket to communicate
	public HashMap<String, HouseProperties> serversInfo; // information received from all servers
	public TransferDecision decision; // decision made by this server
	public TransferDecision final_decision; // final decision made by all servers
	public HashMap<String, Float> deficitServers = new HashMap<String, Float>(); // this house servers list of house servers with deficit power
	public HashMap<String, Float> excessServers = new HashMap<String, Float>(); // this house servers list of house servers with excess power

	public ServerSimulatingHouse(int id)
	{
		this.serverId = id;
		this.serversInfo = new HashMap<String, HouseProperties>();
		this.final_decision = new TransferDecision();
		this.decision = new TransferDecision();
	}
	
	// function which helps this house server to keep listening for any incoming messages
	public void listen() throws NumberFormatException, IOException
	{
		  //System.out.println("listening");
		  byte[] server_buf = new byte[256];
	      DatagramPacket client_packet = new DatagramPacket(server_buf, server_buf.length);
		  this.server_socket.receive(client_packet);
		  String received = new String(client_packet.getData(), 0, client_packet.getLength());
		  System.out.println(received);
		  if(received.startsWith("Id"))
		  {
			  String[] info = received.split(";");
			  HouseProperties value = new HouseProperties(info[1].substring(info[1].indexOf(": ")+2),info[2].substring(info[2].indexOf(": ")+2));
			  String key = info[0].substring(info[0].indexOf(": ")+2);
			  this.serversInfo.put(key,value);
		  }
		  else if(received.startsWith("S"))
		  {
			  decision.decisionsReceived = decision.decisionsReceived+1;
			  if(decision.decisionsReceived>2)
			  {
				  //check if this is the sender or receiver and do the corresponding action
				  // before doing the corresponding action please broad cast a message about its action
				  String[] info = received.split(";");
				  String sourceId = info[0].substring(info[0].indexOf(": ")+2); 
				  String destinationId = info[1].substring(info[0].indexOf(": ")+2); 
				  String power = info[2].substring(info[0].indexOf(": ")+2); 
				  if(this.serverId == Integer.parseInt(sourceId))
				  {
					  System.out.println("Power - "+power+" has been transfered to "+destinationId);
					  this.final_decision.setSourceServerId(sourceId);
					  this.final_decision.setDestinationServerId(destinationId);
					  this.final_decision.setPowerToTransfer(Float.parseFloat(power));
				  }
				  else if(this.serverId == Integer.parseInt(destinationId))
				  {
					  this.final_decision.setSourceServerId(sourceId);
					  this.final_decision.setDestinationServerId(destinationId);
					  this.final_decision.setPowerToTransfer(Float.parseFloat(power));
					  System.out.println("Power - "+power+" has been received from "+sourceId);
				  }
				  if(final_decision.SourceServerId!="" && final_decision.DestinationServerId!="")
				  {
					  // update our own serversinfo object
					  updateInformation();
					  broadCastState("final transfer information");
				  }  
			  }
		  }
		  // on receiving the final decision of transfer then update the values in excess tuples and deficit tuples
		  else if(received.startsWith("F"))
		  {
			  String[] info = received.split(";");
			  String finalsourceId = info[0].substring(info[0].indexOf(": ")+2); 
			  String finaldestinationId = info[1].substring(info[0].indexOf(": ")+2); 
			  String finalpower = info[2].substring(info[0].indexOf(": ")+2);
			  this.final_decision.setDestinationServerId(finaldestinationId);
			  this.final_decision.setSourceServerId(finalsourceId);
			  this.final_decision.setPowerToTransfer(Float.parseFloat(finalpower));
			  // update our own serverinfo object
			  updateInformation();
		  }
	 }
	
	// function to help this house server to broadcast - send messages to other house servers
	public void broadCastState(String type) throws IOException
	{
		byte[] sendBuffer = new byte[256];
		String houseState = "";
		if(type.equals("state"))
		{
		Scanner reader = new Scanner(System.in);
		System.out.println("Please enter the amount of power generated by this house");
		float powerGenerated = reader.nextFloat();
		System.out.println("Please enter the amount of power consumed by this house");
		float powerConsumed = reader.nextFloat(); 
		houseState = "Id: "+this.serverId+";G: "+powerGenerated+";C: "+powerConsumed;
		reader.close();
		}
		else if(type.equals("transfer decision"))
		{
			houseState = "S: "+this.decision.SourceServerId+";D: "+this.decision.DestinationServerId+";P: "+this.decision.PowerToTransfer;
		}
		else if(type.equals("final transfer information"))
		{
			houseState = "FS: "+this.final_decision.SourceServerId+";FD: "+this.final_decision.DestinationServerId+";FP: "+this.final_decision.PowerToTransfer;
		}
		sendBuffer = houseState.getBytes();
		InetAddress server1_address = InetAddress.getByName(Constants.SERVER1_ADDRESS);
		InetAddress server2_address = InetAddress.getByName(Constants.SERVER2_ADDRESS);
		InetAddress server3_address = InetAddress.getByName(Constants.SERVER3_ADDRESS);
		InetAddress server4_address = InetAddress.getByName(Constants.SERVER4_ADDRESS);
		InetAddress server5_address = InetAddress.getByName(Constants.SERVER5_ADDRESS);
		DatagramPacket broadcastPacket1 = new DatagramPacket(sendBuffer, sendBuffer.length, server1_address, Integer.parseInt(Constants.SERVER1_PORT));
		this.server_socket.send(broadcastPacket1);
		DatagramPacket broadcastPacket2 = new DatagramPacket(sendBuffer, sendBuffer.length, server2_address, Integer.parseInt(Constants.SERVER2_PORT));
		this.server_socket.send(broadcastPacket2);
		DatagramPacket broadcastPacket3 = new DatagramPacket(sendBuffer, sendBuffer.length, server3_address, Integer.parseInt(Constants.SERVER3_PORT));
		this.server_socket.send(broadcastPacket3);
		DatagramPacket broadcastPacket4 = new DatagramPacket(sendBuffer, sendBuffer.length, server4_address, Integer.parseInt(Constants.SERVER4_PORT));
		this.server_socket.send(broadcastPacket4);
		DatagramPacket broadcastPacket5 = new DatagramPacket(sendBuffer, sendBuffer.length, server5_address, Integer.parseInt(Constants.SERVER5_PORT));
		this.server_socket.send(broadcastPacket5);
	}
	
	// function to get distance between two house servers
	public Float getDistance(String serverId1, String serverId2)
	{
		Float distance = null;
		//System.out.println("Getting distance from "+serverId1+" and "+serverId2);
		ArrayList<ServersDistance> distances = Constants.serverDistanceInfo.get(serverId1);
		for(ServersDistance serverInstance : distances)
		{
			//System.out.println("checking "+serverInstance.DestinationServerId+" : "+serverInstance.distanceInMiles);
			if(serverInstance.DestinationServerId.equals(serverId2))
			{
				distance = serverInstance.getDistanceInMiles();
				break;
			}
		}
		//System.out.println("Distance is "+distance);
		return distance;
	}
	
	// function to update information after power has been transferred
	public void updateInformation()
	{
		Iterator<Entry<String, HouseProperties>> iterator_serversInfo = this.serversInfo.entrySet().iterator();
		while (iterator_serversInfo.hasNext())
		  {
			  Map.Entry<String, HouseProperties> tuple = (Map.Entry<String, HouseProperties>) iterator_serversInfo.next();
			  if(tuple.getKey()==this.final_decision.SourceServerId)
			  {
				  tuple.getValue().setPowerGenerated(tuple.getValue().getPowerGenerated()-final_decision.PowerToTransfer);
			  }
			  else if(tuple.getKey()==this.final_decision.DestinationServerId)
			  {
				  tuple.getValue().setPowerGenerated(tuple.getValue().getPowerGenerated()+final_decision.PowerToTransfer);
			  }
		  }
		
		// need to update the excess and deficit lists
		this.deficitServers.remove(final_decision.DestinationServerId); // deficit server got power so is definitely removed
		
		// excess server might need have more power so check and remove only if applicable
		float excessPower = this.excessServers.get(final_decision.SourceServerId);
		if(excessPower == final_decision.PowerToTransfer)
		{
			this.excessServers.remove(final_decision.SourceServerId);
		}
		else if(excessPower > final_decision.PowerToTransfer)
		{
			this.excessServers.put(final_decision.SourceServerId, (excessPower - final_decision.PowerToTransfer));
		}
	 }
	
	// function that monitors the state of all servers and makes a decision if there is a power deficit and power excess
	public void checkServersState() throws IOException
	{
		  System.out.println("checking server state");
		  Iterator<Entry<String, HouseProperties>> iterator_serversInfo = this.serversInfo.entrySet().iterator();
		  
		  // if at least 3 other house servers Info is received 
		  if(this.serversInfo.size()>=5)
		  {
			  // for all the servers process data and find deficit servers and excess servers
			  while (iterator_serversInfo.hasNext())
			  {
				  Map.Entry<String, HouseProperties> tuple = (Map.Entry<String, HouseProperties>) iterator_serversInfo.next();
				  String serverId = tuple.getKey();
				  HouseProperties serverInfo = tuple.getValue();
				  if(serverInfo.powerGenerated - serverInfo.powerConsumed < 0)
				  {
					  this.deficitServers.put(serverId, serverInfo.powerGenerated-serverInfo.powerConsumed);
				  }
				  else if(serverInfo.powerGenerated - serverInfo.powerConsumed > 0)
				  {
					  this.excessServers.put(serverId, serverInfo.powerGenerated-serverInfo.powerConsumed);
				  }
			  }
		  
			  // if deficitservers and excess servers are not empty, then make decision on who is going to send power to whom
			  if(!this.deficitServers.isEmpty() && !this.excessServers.isEmpty())
			  {
				  Iterator<Entry<String, Float>> iterator_deficitInfo = this.deficitServers.entrySet().iterator();
				  Iterator<Entry<String, Float>> iterator_excessInfo = this.excessServers.entrySet().iterator();
				  while(iterator_deficitInfo.hasNext())
				  {
					  Map.Entry<String, Float> deficitTuple = (Map.Entry<String, Float>) iterator_deficitInfo.next();
					  while(iterator_excessInfo.hasNext())
					  {
						  Map.Entry<String, Float> excessTuple = (Map.Entry<String, Float>) iterator_excessInfo.next();
						  if(excessTuple.getValue() > deficitTuple.getValue())
						  {
							  if(decision.getSourceServerId()=="")
							  {
								  decision.setPowerToTransfer(excessTuple.getValue());
								  decision.setSourceServerId(excessTuple.getKey());
								  decision.setDestinationServerId(deficitTuple.getKey());
							  }
							  else
							  {
								System.out.println("Getting distance between "+excessTuple.getKey()+" and "+deficitTuple.getKey()+"; and distance between "+ decision.SourceServerId +" and "+ deficitTuple.getKey());
								if(getDistance(excessTuple.getKey(),deficitTuple.getKey()) < getDistance(decision.getSourceServerId(), deficitTuple.getKey()))
								{
									decision.setPowerToTransfer(excessTuple.getValue());
									decision.setSourceServerId(excessTuple.getKey());
									decision.setDestinationServerId(deficitTuple.getKey());
								}
							  }
						  }
					  }
					  //System.out.println("Final Decision from this server is to transfer "+ decision.getPowerToTransfer()+" power from "+deficitTuple.getKey()+" to "+decision.getServerId());
					  broadCastState("transfer decision");
					  //updateInformation();
				  }
			  }
		  else
		  {
			  if(!this.deficitServers.isEmpty())
			  {
				  System.out.println("Not enough power in the system");
			  }
			  else if(!this.excessServers.isEmpty())
			  {
				  System.out.println("Excess power in the system - No deficit - System STABLE");
			  }
		  }
		}
		  else
		  {
			  //System.out.print("Still waiting for more info");
		  }
	}
}