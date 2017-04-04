
public class ServersDistance {
	public String DestinationServerId;
	public float distanceInMiles;
	public ServersDistance(String string, float d) {
		this.DestinationServerId = string;
		this.distanceInMiles = d;
	}
	public String getDestinationServerId() {
		return DestinationServerId;
	}
	public void setDestinationServerId(String destinationServerId) {
		DestinationServerId = destinationServerId;
	}
	public float getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(float distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}
}
