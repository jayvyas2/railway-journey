package com.cloudbees.railwayjourney.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "from_location")
    private String fromLocation;

    @JsonProperty(value = "to_destination")
    private String toDestination;

    @JsonProperty(value = "amount")
    private double price;

    @JsonProperty(value = "seat_details")
    private Seat seat;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getFromLocation() {
	return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
	this.fromLocation = fromLocation;
    }

    public String getToDestination() {
	return toDestination;
    }

    public void setToDestination(String toDestination) {
	this.toDestination = toDestination;
    }

    public double getPrice() {
	return price;
    }

    public void setPrice(double price) {
	this.price = price;
    }

    public Seat getSeat() {
	return seat;
    }

    public void setSeat(Seat seat) {
	this.seat = seat;
    }

}
