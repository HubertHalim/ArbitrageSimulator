class Data {
	String timeStamp;
	double buy;
	double sell;

	Data (String timeStamp, double buy, double sell) {
		this.timeStamp = timeStamp;
		this.buy = buy;
		this.sell = sell;
	}

	@Override
	public String toString() {
		return this.timeStamp + " " + this.buy + " " + this.sell;
	}
}