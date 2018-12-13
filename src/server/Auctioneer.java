/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Data.Data;
import Data.Bid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 *
 * @author aka
 */
public class Auctioneer implements Serializable {

    private transient MulticastSocket socket;
    private Auction auction;

    public Auctioneer(Auction auction) {
        try {
            this.auction = auction;
            socket = new MulticastSocket(auction.getPort());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void joinAuction() {
        try {
            socket.joinGroup(auction.getAddress());
            auction.setStatus(true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void leaveAuction() {
        try {
            socket.leaveGroup(auction.getAddress());
            auction.setStatus(false);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void listenBid() {
        new Thread(() -> {
            while (true) {
                try {
                    byte[] incomingData = new byte[1024];
                    DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                    socket.receive(incomingPacket);
                    
                    ByteArrayInputStream inputBytes = new ByteArrayInputStream(incomingPacket.getData());
                    ObjectInputStream inputObject = new ObjectInputStream(inputBytes);
                    
                    Data data = (Data) inputObject.readObject();
                    
                    if (data.getType() == Data.REQUEST_BID) {
                        setLastBid((Bid) data.getPayload());
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    public void setLastBid(Bid bid) {
        if (bid.getPrice() > auction.getLastBid().getPrice()) {
            auction.setLastBid(bid);
            sendLastBid();
        }
    }

    public void sendLastBid() {
        try {
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
            ObjectOutput outputObject = new ObjectOutputStream(outputBytes);

            outputObject.writeObject(new Data(Data.RESPONSE_BID, auction.getLastBid()));
            byte[] bytesData = outputBytes.toByteArray();
            DatagramPacket packet = new DatagramPacket(bytesData, bytesData.length);
            socket.send(packet);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
