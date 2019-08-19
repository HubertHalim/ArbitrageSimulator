class Data {
	String timeStamp;
	double buy;
	double sell;
	int ttl;

	Data (String timeStamp, double buy, double sell, int ttl) {
		this.timeStamp = timeStamp;
		this.buy = buy;
		this.sell = sell;
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return this.timeStamp + " " + this.buy + " " + this.sell + " " + this.ttl;
	}
}