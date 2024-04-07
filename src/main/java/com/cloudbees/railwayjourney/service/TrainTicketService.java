package com.cloudbees.railwayjourney.service;

import java.util.List;

import com.cloudbees.railwayjourney.model.PurchaseTicketRequest;
import com.cloudbees.railwayjourney.model.Seat;
import com.cloudbees.railwayjourney.model.Ticket;
import com.cloudbees.railwayjourney.model.TrainSection;

public interface TrainTicketService {

    public Ticket purchaseTicket(PurchaseTicketRequest purchaseTicketRequest);

    public Ticket getTicketReceipt(long userId);

    public List<Seat> getUserByTicketSection(TrainSection section);

    public boolean removeUser(long userId);

    public boolean modifyUserSeat(long userId);

}
