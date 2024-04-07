package com.cloudbees.railwayjourney.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudbees.railwayjourney.model.PurchaseTicketRequest;
import com.cloudbees.railwayjourney.model.Seat;
import com.cloudbees.railwayjourney.model.Ticket;
import com.cloudbees.railwayjourney.model.TrainSection;
import com.cloudbees.railwayjourney.model.User;

@Service
public class TrainTicketServiceImpl implements TrainTicketService {

    public static org.slf4j.Logger logger = LoggerFactory.getLogger(TrainTicketServiceImpl.class);

    // structures for storing and maintaining relations
    // user id vs user map
    protected static Map<Long, User> users = new HashMap<>();
    // ticket id vs ticket map
    protected static Map<Long, Ticket> tickets = new HashMap<>();
    // train section vs seats map
    protected static Map<TrainSection, List<Seat>> seatsBySection = new HashMap<>();
    // seats which are vacated after booking, can be reused for booking
    protected static List<Seat> reusableSeatList = new ArrayList<>();

    // for auto increment of user id and ticket id
    private static AtomicLong userIdGenerator = new AtomicLong(1);
    private static AtomicLong ticketIdGenerator = new AtomicLong(1);

    static {
	// populate the seatsBySection map
	seatsBySection.put(TrainSection.SECTION_A, new ArrayList<Seat>());
	seatsBySection.put(TrainSection.SECTION_B, new ArrayList<Seat>());
	for (Map.Entry<TrainSection, List<Seat>> entry : seatsBySection.entrySet()) {
	    // 10 seats exists in a section - consideration
	    for (int i = 1; i <= 10; i++) {
		Seat seat = new Seat(i, entry.getKey());
		List<Seat> seatList = seatsBySection.get(entry.getKey());
		seatList.add(seat);
		seatsBySection.put(entry.getKey(), seatList);
	    }
	}
	logger.info("seatsBySection map populated");
    }

    @Override
    public Ticket purchaseTicket(PurchaseTicketRequest purchaseTicketRequest) {
	User user = new User(userIdGenerator.getAndIncrement(), purchaseTicketRequest.getFirstName(),
		purchaseTicketRequest.getLastName(), purchaseTicketRequest.getEmail());
	logger.info("purchase ticket request received");
	Ticket ticket = getTicketForUser(purchaseTicketRequest, user);

	Seat seat = getAvailableSeat();
	if (seat == null) {
	    return null;
	} else {
	    updateSeatDetails(user, ticket, seat);
	    updateStructures(user, ticket, seat);
	    logger.info("purchase ticket request responded");
	    return ticket;
	}

    }

    private void updateStructures(User user, Ticket ticket, Seat seat) {
	List<Seat> seatList = seatsBySection.get(seat.getSection());
	seatList.remove(seat);
	seatList.add(seat);
	// seatBySection map updated with latest seat changes
	seatsBySection.put(seat.getSection(), seatList);
	// users map updated with new user added
	users.put(user.getId(), user);
	// tickets map updated with latest ticket
	tickets.put(ticket.getId(), ticket);
    }

    private void updateSeatDetails(User user, Ticket ticket, Seat seat) {
	seat.setBooked(true);
	seat.setUser(user);
	ticket.setSeat(seat);

    }

    private Ticket getTicketForUser(PurchaseTicketRequest purchaseTicketRequest, User user) {
	logger.info("get ticket for user request received");
	Ticket ticket = new Ticket();
	ticket.setId(ticketIdGenerator.getAndIncrement());
	ticket.setFromLocation(purchaseTicketRequest.getFromLocation());
	ticket.setToDestination(purchaseTicketRequest.getToDestination());
	// hard coded as of now
	ticket.setPrice(20);
	logger.info("get ticket for user request responded");
	return ticket;
    }

    protected Seat getAvailableSeat() {
	logger.info("checking seat availabilty");
	// check for available seats which were canceled after booking
	if (!reusableSeatList.isEmpty()) {
	    Seat seat = reusableSeatList.get(0);
	    reusableSeatList.remove(0);
	    return seat;
	}
	// check for available seats in each section
	for (TrainSection section : TrainSection.values()) {
	    List<Seat> seats = seatsBySection.get(section);
	    for (int i = 0; i < seats.size(); i++) {
		if (!seats.get(i).isBooked()) {
		    return seats.get(i);
		}
	    }
	}
	return null;
    }

    @Override
    public Ticket getTicketReceipt(long userId) {
	logger.info("get ticket receipt for user request received");
	for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
	    if (entry.getValue().getSeat().getUser().getId().equals(userId)) {
		logger.info("get ticket receipt for user responded");
		return entry.getValue();
	    }
	}
	logger.info("get ticket receipt not found");
	return null;
    }

    @Override
    public List<Seat> getUserByTicketSection(TrainSection section) {
	logger.info("get users by train section request received");
	List<Seat> seatsInSection = new ArrayList<>();

	if (seatsBySection.containsKey(section)) {
	    List<Seat> seatList = seatsBySection.get(section);
	    for (int i = 0; i < seatList.size(); i++) {
		if (seatList.get(i).getUser() != null) {
		    seatsInSection.add(seatList.get(i));
		}
	    }
	}
	logger.info("get users by train section request responded");
	return seatsInSection;
    }

    @Override
    public boolean removeUser(long userId) {
	logger.info("remove user request received");
	if (users.containsKey(userId)) {
	    // remove from users map
	    users.remove(userId);
	    for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
		if (entry.getValue().getSeat().getUser().getId().equals(userId)) {
		    // remove from tickets map for the user
		    tickets.remove(entry.getKey());
		    break;
		}
	    }
	    for (Map.Entry<TrainSection, List<Seat>> entry : seatsBySection.entrySet()) {
		List<Seat> seatList = entry.getValue();
		for (int i = 0; i < seatList.size(); i++) {
		    if (seatList.get(i).getUser() != null) {
			if (seatList.get(i).getUser().getId().equals(userId)) {
			    Seat seat = seatList.get(i);
			    seatList.remove(seat);
			    // adding the user seat in reusable seat list to be utilized for future needs
			    reusableSeatList.add(new Seat(seat.getSeatNumber(), seat.getSection()));
			    break;
			}
		    }
		}
	    }
	    logger.info("remove user request responded");
	    return true;
	}
	logger.info("remove user couldn't happen");
	return false;
    }

    @Override
    public boolean modifyUserSeat(long userId) {
	logger.info("remove user seat request received");
	User user = users.get(userId);
	if (user == null) {
	    return false;
	}

	Seat existingSeat = null;
	long seatNumber = 0L;
	// let's add existing seat to reusable seat list
	for (Map.Entry<TrainSection, List<Seat>> entry : seatsBySection.entrySet()) {
	    List<Seat> seatList = entry.getValue();
	    for (int i = 0; i < seatList.size(); i++) {
		if (seatList.get(i).getUser() != null) {
		    if (seatList.get(i).getUser().getId().equals(userId)) {
			existingSeat = seatList.get(i);
			seatList.remove(existingSeat);
			// updating the seatsBySection map with current seat updates
			seatsBySection.put(existingSeat.getSection(), seatList);
			// adding the seat to reusable seat to be utilized for future needs
			seatNumber = existingSeat.getSeatNumber();
			reusableSeatList.add(new Seat(existingSeat.getSeatNumber(), existingSeat.getSection()));
			break;
		    }
		}
	    }
	}
	Seat newSeat = null;
	// Find the next available seat
	if (!reusableSeatList.isEmpty()) {
	    for (int i = 0; i < reusableSeatList.size(); i++) {
		if (existingSeat != null) {
		    // check if the seat is not the same which we recently added to reusable list
		    if (reusableSeatList.get(i).getSeatNumber() != existingSeat.getSeatNumber()) {
			newSeat = reusableSeatList.get(i);
			newSeat.setBooked(true);
			newSeat.setUser(user);

			// remove seat from reusable seat list as using the seat from same
			reusableSeatList.remove(i);
			break;
		    }
		}
	    }
	}
	// if newSeat is not updated yet then check for seats from seatsBySection map
	// now
	if (newSeat == null) {
	    for (TrainSection section : TrainSection.values()) {
		List<Seat> seats = seatsBySection.get(section);
		for (Seat seat : seats) {
		    if (!seat.isBooked()) {
			newSeat = seat;
			newSeat.setBooked(true);
			newSeat.setUser(user);

			break;
		    }
		}
		// if seat updated then break out of loop
		if (newSeat != null) {
		    break;
		}
	    }

	}

	if (newSeat == null) {
	    if (existingSeat != null) {
		for (int i = 0; i < reusableSeatList.size(); i++) {
		    // remove the seat from reusable list as newSeat couldn't be assigned
		    if (reusableSeatList.get(i).getSeatNumber() == seatNumber) {
			if (reusableSeatList.get(i).getSection().equals(existingSeat.getSection())) {
			    reusableSeatList.remove(i);
			    break;
			}
		    }
		}
		// update the seatsBySection map as newSeat couldn't be assigned, revert the
		// operation
		List<Seat> seatList = seatsBySection.get(existingSeat.getSection());
		seatList.add(existingSeat);
		seatsBySection.put(existingSeat.getSection(), seatList);

	    }
	    logger.info("remove user seat couldn't happen");
	    return false;
	} else {
	    // update tickets map with updated seat
	    for (Map.Entry<Long, Ticket> entry : tickets.entrySet()) {
		if (entry.getValue().getSeat().getUser().equals(user)) {
		    Ticket existingticket = entry.getValue();
		    existingticket.setSeat(newSeat);
		    long id = entry.getKey();
		    tickets.put(id, existingticket);
		    break;
		}
	    }
	    logger.info("remove user seat request responded");
	    return true;
	}
    }
}

