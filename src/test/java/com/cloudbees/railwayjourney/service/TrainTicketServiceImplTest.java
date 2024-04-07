package com.cloudbees.railwayjourney.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudbees.railwayjourney.model.PurchaseTicketRequest;
import com.cloudbees.railwayjourney.model.Seat;
import com.cloudbees.railwayjourney.model.Ticket;
import com.cloudbees.railwayjourney.model.TrainSection;
import com.cloudbees.railwayjourney.model.User;

@ExtendWith(MockitoExtension.class)
class TrainTicketServiceImplTest {

    @Mock
    private TrainTicketServiceImpl ticketService;

    private PurchaseTicketRequest purchaseTicketRequest;
    private User user;
    private Ticket ticket;
    private Seat seat;

    @BeforeEach
    void setUp() {
	purchaseTicketRequest = new PurchaseTicketRequest("Sam", "Singh", "sam@gmail.com", "London", "France");
	user = new User(1L, "Ram", "Kumar", "ram@gmail.com");
	ticket = new Ticket();
	ticket.setFromLocation("London");
	ticket.setToDestination("France");
	ticket.setPrice(20);
	seat = new Seat(1, TrainSection.SECTION_A);
	seat.setUser(user);
    }

    @Test
    void testGetTicketReceipt() {
	when(ticketService.getTicketReceipt(1L)).thenReturn(ticket);

	Ticket receipt = ticketService.getTicketReceipt(1L);

	assertNotNull(receipt);
	assertEquals(ticket, receipt);
    }

    @Test
    void testGetUserByTicketSection() {
	List<Seat> seatsInSection = new ArrayList<>();
	seatsInSection.add(seat);
	when(ticketService.getUserByTicketSection(TrainSection.SECTION_A)).thenReturn(seatsInSection);

	List<Seat> result = ticketService.getUserByTicketSection(TrainSection.SECTION_A);

	assertEquals(1, result.size());
	assertEquals(seat, result.get(0));
    }

    @Test
    void testRemoveUser() {
	when(ticketService.removeUser(1L)).thenReturn(true);

	boolean result = ticketService.removeUser(1L);

	assertTrue(result);
    }

    @Test
    void testModifyUserSeat() {
	when(ticketService.modifyUserSeat(1L)).thenReturn(true);

	boolean result = ticketService.modifyUserSeat(1L);

	assertTrue(result);
    }
}
