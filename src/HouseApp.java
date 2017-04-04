import java.io.IOException;
import java.net.DatagramSocket;

public class HouseApp {

	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		if(args.length < 1 || Integer.parseInt(args[0]) > 5 || Integer.parseInt(args[0]) < 1)
		  {
			  System.out.println("Usage ERROR, please provide the id {1-5} of the server instance");
			  System.exit(0);
		  }
		
		ServerSimulatingHouse house = new ServerSimulatingHouse(Integer.parseInt(args[0]));
		System.out.println("Server "+args[0]+" is running - Represents House "+args[0]);
		switch(Integer.parseInt(args[0]))
		{
		case 1:
			house.serverPort = Integer.parseInt(Constants.SERVER1_PORT);
			break;
		case 2:
			house.serverPort = Integer.parseInt(Constants.SERVER2_PORT);
			break;
		case 3:
			house.serverPort = Integer.parseInt(Constants.SERVER3_PORT);
			break;
		case 4:
			house.serverPort = Integer.parseInt(Constants.SERVER4_PORT);
			break;
		case 5:
			house.serverPort = Integer.parseInt(Constants.SERVER5_PORT);
			break;
		}
		
		house.server_socket = new DatagramSocket(house.serverPort);
		
		Thread t1 = new Thread(new Runnable()
				{
				public void run(){
					try{
						while(true)
						{
						house.listen();
						}
					} catch(NumberFormatException | IOException e)
					{
						e.printStackTrace();
					}
				}
				});
		
		Thread t2 = new Thread(new Runnable(){
			public void run(){
				try{
					house.broadCastState("state");
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		
		Thread t3 = new Thread(new Runnable()
		{
		public void run(){
			try{
				house.checkServersState();
				Thread.sleep(10000);
			} catch(NumberFormatException | InterruptedException | IOException e)
			{
				e.printStackTrace();
			}
		}
		});
		
		t1.start();
		t2.start();
		t3.start();
		
		t1.join();
		t2.join();
		t3.join();
} 
}
