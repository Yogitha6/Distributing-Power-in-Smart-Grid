public class HouseProperties {
 public Float powerGenerated;
 public Float powerConsumed;
 
 public HouseProperties(String G, String C)
 {
	 this.powerConsumed = Float.parseFloat(C);
	 this.powerGenerated = Float.parseFloat(G); 
 }
 public Float getPowerGenerated() {
	return powerGenerated;
  }
 
 public void setPowerGenerated(Float powerGenerated) {
	this.powerGenerated = powerGenerated;
  }

 public Float getPowerConsumed() {
	return powerConsumed;
  }
 public void setPowerConsumed(Float powerConsumed) {
	this.powerConsumed = powerConsumed;
  }
}
