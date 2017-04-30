package simulation;
import java.io.* ;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class ServerSimulatingHouse {
	public int serverId; //this house server id
	public int serverPort; // this house server runs on a port
	public DatagramSocket server_socket; // this house will need a socket to communicate
	public HashMap<String, HouseProperties> serversInfo; // information received from all servers
	public TransferDecision decision; // decision made by this server
	public TransferDecision final_decision; // final decision made by all servers
	public TransferDecision multi_decision;
	public HashMap<String, Float> deficitServers = new HashMap<String, Float>(); // this house servers list of house servers with deficit power
	public HashMap<String, Float> excessServers = new HashMap<String, Float>(); // this house servers list of house servers with excess power
	public HashMap<String, Float> multiplesenders = new HashMap<String, Float>(); // multiple senders

	public ServerSimulatingHouse(int id)
	{
		this.serverId = id;
		this.serversInfo = new HashMap<String, HouseProperties>();
		this.final_decision = new TransferDecision();
		this.decision = new TransferDecision();
		this.multi_decision = new TransferDecision();
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
				  if(this.excessServers.containsKey(sourceId) && this.deficitServers.containsKey(destinationId))
				  {
					  if(this.serverId == Integer.parseInt(sourceId))
					  {
						  System.out.println("Power - "+power+" has been transfered to "+destinationId);
						  this.final_decision.setSourceServerId(sourceId);
						  this.final_decision.setDestinationServerId(destinationId);
						  this.final_decision.setPowerToTransfer(Float.parseFloat(power));
						  
						  // update our own serversinfo object
						  //System.out.println("call to update as it is source");
						  updateInformation();
						  broadCastState("final transfer information");
					  }
					  if(this.serverId == Integer.parseInt(destinationId))
					  {
						  this.final_decision.setSourceServerId(sourceId);
						  this.final_decision.setDestinationServerId(destinationId);
						  this.final_decision.setPowerToTransfer(Float.parseFloat(power));
						  System.out.println("Power - "+power+" has been received from "+sourceId);
						  
						  // update our own serversinfo object
						  //System.out.println("call to update as it is destination");
						  updateInformation();
						  //broadCastState("final transfer information");
						  this.final_decision.setPowerToTransfer(Float.parseFloat("0"));
						  this.final_decision.setDestinationServerId("");
						  this.final_decision.setSourceServerId("");
					  }
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
			  if(final_decision.DestinationServerId.equals("") && final_decision.SourceServerId.equals(""))
			  {
				  if(this.serverId!=Integer.parseInt(finaldestinationId) && this.serverId!=Integer.parseInt(finalsourceId))
				  {
				  //System.out.println("updating final_decision values "+finalsourceId+" "+finaldestinationId+" "+finalpower);
				  this.final_decision.setDestinationServerId(finaldestinationId);
				  this.final_decision.setSourceServerId(finalsourceId);
				  this.final_decision.setPowerToTransfer(Float.parseFloat(finalpower));
				  // update our own serverinfo object
				  //System.out.println("call to update as a spectator");
				  updateInformation();
				  this.final_decision.setPowerToTransfer(Float.parseFloat("0"));
				  this.final_decision.setDestinationServerId("");
				  this.final_decision.setSourceServerId("");
				  }
			  }
		  }
		  else if(received.startsWith("D"))
		  {
			  /*String[] info = received.split(";");
			  String finaldestinationId = info[2].substring(info[0].indexOf(": ")+2);
			  System.out.println("Here is the scenario -------"+finaldestinationId);*/
			  this.multi_decision.decisionsReceived= this.multi_decision.decisionsReceived+1;
			  if(multi_decision.decisionsReceived>2)
			  {
				  multipleSendersWork();
			  }
		  }
	 }
	
	public void multipleSendersWork()
	{
		for(String serverId: this.multiplesenders.keySet())
		  {
		   if(this.deficitServers.containsKey(multi_decision.DestinationServerId))
			{
				this.deficitServers.remove(multi_decision.DestinationServerId); // deficit server got power so is definitely removed
			}
			// excess server might need have more power so check and remove only if applicable
			if(this.excessServers.containsKey(serverId))
			{
				this.excessServers.remove(serverId);
			}
			if(this.serverId == Integer.parseInt(serverId))
			{
				System.out.println("Power "+multiplesenders.get(serverId)+"has been transferred to "+multi_decision.DestinationServerId);
			}
			if(this.serverId == Integer.parseInt(multi_decision.DestinationServerId))
			{
				System.out.println("Power "+ multiplesenders.get(serverId) +" has been received from "+serverId);
			}
			//update the serversInfo list maintained by this server
			 serversInfo.get(serverId).setPowerGenerated(serversInfo.get(serverId).getPowerGenerated()-multiplesenders.get(serverId));
			}
		  if(this.serverId == Integer.parseInt(multi_decision.DestinationServerId))
		  {
			System.out.println("Total power received from multiple senders is "+multi_decision.getPowerToTransfer());
		  }
		  if(!multi_decision.DestinationServerId.equals(""))
		  {
		  this.serversInfo.get(this.multi_decision.DestinationServerId).setPowerGenerated(this.serversInfo.get(this.multi_decision.DestinationServerId).getPowerGenerated()+this.multi_decision.getPowerToTransfer());
		  }
		  this.multi_decision.DestinationServerId="";
		  this.multi_decision.PowerToTransfer=(float) 0;
		  this.multi_decision.SourceServerId="";
		  this.multiplesenders.clear();
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
			this.final_decision.setPowerToTransfer(Float.parseFloat("0"));
			  this.final_decision.setDestinationServerId("");
			  this.final_decision.setSourceServerId("");
		}
		else if(type.equals("multiple senders"))
		{
			houseState = "DS: ";
			for (String serverId : this.multiplesenders.keySet()) 
			{
				houseState= houseState+serverId+"-"+this.multiplesenders.get(serverId)+";";
			}
			houseState = houseState+"DD: "+this.multi_decision.getDestinationServerId();
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
		//System.out.println("removing from deficit and exccess Servers list "+final_decision.DestinationServerId+" "+final_decision.SourceServerId);
		// need to update the excess and deficit servers lists
		if(this.deficitServers.containsKey(final_decision.DestinationServerId))
		{
			this.deficitServers.remove(final_decision.DestinationServerId); // deficit server got power so is definitely removed
		}
		// excess server might need have more power so check and remove only if applicable
		if(this.excessServers.containsKey(final_decision.getSourceServerId()))
		{
			float excessPower = this.excessServers.get(final_decision.SourceServerId);
			if(excessPower == final_decision.PowerToTransfer)
			{
				this.excessServers.remove(final_decision.SourceServerId);
			}
			else if(excessPower > final_decision.PowerToTransfer)
			{
				// if the source has excess power even after donating then we update the existing entry in excess Servers list
				this.excessServers.put(final_decision.SourceServerId, (excessPower - final_decision.PowerToTransfer));
			}
		}
		
		//System.out.println("updating serversInfo for "+final_decision.DestinationServerId+" and "+final_decision.SourceServerId);
		//update the serversInfo list maintained by this server
		for (String serverId : this.serversInfo.keySet()) {
				  //Decreasing the power generated as some of the power has been transferred
				  if(serverId.equalsIgnoreCase(final_decision.SourceServerId))
				  {
					  serversInfo.get(serverId).setPowerGenerated(serversInfo.get(serverId).getPowerGenerated()-final_decision.PowerToTransfer);
				  }
				  //Increasing the power generated as some of the power has been received
				  if(serverId.equalsIgnoreCase(final_decision.DestinationServerId))
				  {
					  serversInfo.get(serverId).setPowerGenerated(serversInfo.get(serverId).getPowerGenerated()+final_decision.PowerToTransfer);
				  }
		}
	 }
	
	// function that monitors the state of all servers and makes a decision if there is a power deficit and power excess
	public void checkServersState() throws IOException
	{
		  //System.out.println("checking server state");
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
					  System.out.println("Adding "+serverId+" to deficit servers list ");
					  this.deficitServers.put(serverId, serverInfo.powerGenerated-serverInfo.powerConsumed);
				  }
				  else if(serverInfo.powerGenerated - serverInfo.powerConsumed > 0)
				  {
					  System.out.println("Adding "+serverId+" to excess servers list ");
					  this.excessServers.put(serverId, serverInfo.powerGenerated-serverInfo.powerConsumed);
				  }
			  }
		  
			  // if deficitservers and excess servers are not empty, then make decision on who is going to send power to whom
			  if(!this.deficitServers.isEmpty() && !this.excessServers.isEmpty())
			  {
				  Iterator<Entry<String, Float>> iterator_deficitInfo = this.deficitServers.entrySet().iterator();
				  
				  Comparator<String> comparator = new ValueComparator<String, Float>(this.excessServers);
				  TreeMap<String, Float> resultExcessServers = new TreeMap<String, Float>(comparator);
				  resultExcessServers.putAll(this.excessServers);
				  
				  Iterator<Entry<String, Float>> iterator_excessInfo = resultExcessServers.entrySet().iterator();
				  System.out.println(resultExcessServers.size());
				  while(iterator_deficitInfo.hasNext())
				  {
					  String type = "transfer decision";
					  Map.Entry<String, Float> deficitTuple = (Map.Entry<String, Float>) iterator_deficitInfo.next();
					  float sum = 0;
					  while(iterator_excessInfo.hasNext())
					  {
						  Map.Entry<String, Float> excessTuple = (Map.Entry<String, Float>) iterator_excessInfo.next();
						  //System.out.println("excess tuple value is less "+excessTuple.getValue()+" than "+ Math.abs(deficitTuple.getValue()));
						  if(excessTuple.getValue() >= Math.abs(deficitTuple.getValue()))
						  {
							  if(decision.getSourceServerId().equals(""))
							  {
								  decision.setPowerToTransfer(Math.abs(deficitTuple.getValue()));
								  decision.setSourceServerId(excessTuple.getKey());
								  decision.setDestinationServerId(deficitTuple.getKey());
							  }
							  else
							  {
								System.out.println("Getting distance between "+excessTuple.getKey()+" and "+deficitTuple.getKey()+"; and distance between "+ decision.SourceServerId +" and "+ deficitTuple.getKey());
								if(getDistance(excessTuple.getKey(),deficitTuple.getKey()) <= getDistance(decision.getSourceServerId(), deficitTuple.getKey()))
								{
									decision.setPowerToTransfer(Math.abs(deficitTuple.getValue()));
									decision.setSourceServerId(excessTuple.getKey());
									decision.setDestinationServerId(deficitTuple.getKey());
								}
							  }
						  }
						  else
						  {
							  System.out.println("excess tuple value is less "+excessTuple.getValue());
							  if(sum < Math.abs(deficitTuple.getValue()))
							  {
							  sum = sum + excessTuple.getValue();
							  System.out.println("adding values to multiple senders" + excessTuple.getKey()+" - "+excessTuple.getValue());
							  this.multiplesenders.put(excessTuple.getKey(), excessTuple.getValue());
							 }
						   }
					    }
						 
						  if(sum!= 0.0 && sum <  Math.abs(deficitTuple.getValue()))
						  {
							  System.out.println("Power insufficient in the system - algorithm can't work");
						  }
						  else if(sum!= 0.0 && sum >= Math.abs(deficitTuple.getValue()))
						  {
							//System.out.println("Type is set to multiple senders");
							this.multi_decision.setDestinationServerId(deficitTuple.getKey());
							this.multi_decision.setPowerToTransfer(sum);
							this.multi_decision.setSourceServerId("multiple");
							type="multiple senders";
						  }
					  broadCastState(type);
					  //System.out.println("Final Decision from this server is to transfer "+ decision.getPowerToTransfer()+" power from "+deficitTuple.getKey()+" to "+decision.getServerId());
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