
public class TransferDecision {
	String SourceServerId;
	public String getSourceServerId() {
		return SourceServerId;
	}
	public void setSourceServerId(String sourceServerId) {
		SourceServerId = sourceServerId;
	}
	public Float getPowerToTransfer() {
		return PowerToTransfer;
	}
	public void setPowerToTransfer(Float powerToTransfer) {
		PowerToTransfer = powerToTransfer;
	}
	public String getDestinationServerId() {
		return DestinationServerId;
	}
	public void setDestinationServerId(String destinationServerId) {
		DestinationServerId = destinationServerId;
	}
	Float PowerToTransfer;
	String DestinationServerId;
	int decisionsReceived;
	public int getDecisionsReceived() {
		return decisionsReceived;
	}
	public void setDecisionsReceived(int decisionsReceived) {
		this.decisionsReceived = decisionsReceived;
	}
	public TransferDecision()
	{
		this.SourceServerId = "";
		this.PowerToTransfer = (float) 0;
		this.DestinationServerId = "";
		this.decisionsReceived = 0;
	}
}
