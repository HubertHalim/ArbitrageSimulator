import java.lang.Math;

public class Pairing implements Comparable<Pairing> {
	String buyC;
	String buyT;
	String sellC;
	String sellT;
	double profit;

	Pairing (String buyC, String buyT, String sellC, String sellT, double profit) {
		this.buyC = buyC;
		this.buyT = buyT;
		this.sellC = sellC;
		this.sellT = sellT;
		this.profit = profit;
	}

	@Override
	public int compareTo(Pairing p) {
		if (this.profit < p.profit) {
			return 1;
		} else if (this.profit > p.profit) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return buyC + "," + buyT + "," + sellC + "," + sellT + "," + profit; 
	}
}