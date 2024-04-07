package com.cloudbees.railwayjourney.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PurchaseTicketRequest {

    public PurchaseTicketRequest() {
	super();
    }

    public PurchaseTicketRequest(String firstName, String lastName, String email, String fromLocation,
	    String toDestination) {
	super();
	this.firstName = firstName;
	this.lastName = lastName;
	this.email = email;
	this.fromLocation = fromLocation;
	this.toDestination = toDestination;
    }

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "from_location")
    private String fromLocation;

    @JsonProperty(value = "to_destination")
    private String toDestination;

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
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

}
