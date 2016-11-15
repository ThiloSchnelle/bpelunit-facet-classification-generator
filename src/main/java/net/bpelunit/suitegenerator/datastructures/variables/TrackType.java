package net.bpelunit.suitegenerator.datastructures.variables;

public enum TrackType {
	CLIENT("clientTrack"), PARTNER("partnerTrack"), HUMANTASK("humanPartnerTrack");

	private String name;

	private TrackType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}