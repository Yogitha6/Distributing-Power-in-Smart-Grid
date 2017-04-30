package simulation;

public class TransferDecision {
	public String SourceServerId;
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
	public Float PowerToTransfer;
	public String DestinationServerId;
	public int decisionsReceived;
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
