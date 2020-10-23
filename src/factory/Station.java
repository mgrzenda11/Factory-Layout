package factory;

public class Station {
	private String name;
	private int flavor; //1 = Assembly, 2 = inspection
	
	public Station(String name, int flavor) {
		this.name = name;
		this.flavor = flavor;
		
	}
	
	public void changeFlavor(int newFlav) {
		if(newFlav == 1 || newFlav == 2) {
			flavor = newFlav;
		}
	}
	
	public int getFlavor() {
		return flavor;
	}
	
	public String getName() {
		return name;
	}
}
