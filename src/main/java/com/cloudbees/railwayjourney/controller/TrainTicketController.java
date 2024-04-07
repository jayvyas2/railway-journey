package com.cloudbees.railwayjourney.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudbees.railwayjourney.model.PurchaseTicketRequest;
import com.cloudbees.railwayjourney.model.Seat;
import com.cloudbees.railwayjourney.model.Ticket;
import com.cloudbees.railwayjourney.model.TrainSection;
import com.cloudbees.railwayjourney.service.TrainTicketService;

@RestController
@RequestMapping("/tickets")
public class TrainTicketController {

    @Autowired
    private TrainTicketService trainTicketService;

    @PostMapping(value = "/purchase")
    public ResponseEntity<Object> purchaseTrainTicket(@RequestBody PurchaseTicketRequest request) {
	Ticket ticket = trainTicketService.purchaseTicket(request);
	if (ticket == null) {
	    return new ResponseEntity<Object>("{\"response\":\"Can't book ticket due to seat unavailability\"}",
		    HttpStatus.CONFLICT);
	}
	return new ResponseEntity<Object>(ticket, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{userId}/receipt")
    public ResponseEntity<Object> getTicketReceipt(@PathVariable Long userId) {
	Ticket ticket = trainTicketService.getTicketReceipt(userId);
	if (ticket == null) {
	    return new ResponseEntity<Object>(ticket, HttpStatus.NOT_FOUND);
	}
	return new ResponseEntity<Object>(ticket, HttpStatus.OK);
    }

    @GetMapping(value = "/sections/{section}")
    public ResponseEntity<Object> getUsersBySection(@PathVariable TrainSection section) {
	List<Seat> seatList = trainTicketService.getUserByTicketSection(section);
	if (seatList.isEmpty()) {
	    return new ResponseEntity<Object>(seatList, HttpStatus.NOT_FOUND);
	}
	return new ResponseEntity<Object>(seatList, HttpStatus.OK);
    }

    @DeleteMapping(value = "users/{userId}")
    public ResponseEntity<Object> removeUserFromTrain(@PathVariable Long userId) {
	boolean removed = trainTicketService.removeUser(userId);
	if (removed) {
	    return ResponseEntity.noContent().build();
	} else {
	    return ResponseEntity.notFound().build();
	}
    }

    @PutMapping("/users/{userId}/seat")
    public ResponseEntity<Object> modifyUserSeat(@PathVariable Long userId) {
	boolean removed = trainTicketService.modifyUserSeat(userId);
	if (removed) {
	    return ResponseEntity.ok("User seat modified");
	} else {
	    return new ResponseEntity<Object>(
		    "{\"response\":\"User seat not modified due to either no other seat availability or invalid user\"}",
		    HttpStatus.NOT_FOUND);
	}
    }

}
