package com.cloudbees.railwayjourney.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Seat {

    @JsonProperty(value = "seat_number")
    private int seatNumber;

    @JsonProperty(value = "is_booked")
    private boolean isBooked = false;

    @JsonProperty(value = "user_details")
    private User user = null;

    @JsonProperty(value = "train_section")
    private TrainSection section;

    public Seat(int seatNumber, TrainSection section) {
	super();
	this.seatNumber = seatNumber;
	this.section = section;
    }

    public int getSeatNumber() {
	return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
	this.seatNumber = seatNumber;
    }

    public boolean isBooked() {
	return isBooked;
    }

    public void setBooked(boolean isBooked) {
	this.isBooked = isBooked;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public TrainSection getSection() {
	return section;
    }

    public void setSection(TrainSection section) {
	this.section = section;
    }

}
