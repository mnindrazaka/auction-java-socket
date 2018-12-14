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
            auction.setStatus(Auction.STARTED);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void leaveAuction() {
        try {
            sendEndAuction();
            socket.leaveGroup(auction.getAddress());
            auction.setStatus(Auction.FINISHED);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void listenBid(Runnable callback) {
        new Thread(() -> {
            while (true) {
                try {
                    byte[] incomingData = new byte[1024];
                    DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                    socket.receive(incomingPacket);
                    
                    ByteArrayInputStream inputBytes = new ByteArrayInputStream(incomingPacket.getData());
                    ObjectInputStream inputObject = new ObjectInputStream(inputBytes);
                    
                    Data data = (Data) inputObject.readObject();
                    Bid bidRequest = (Bid) data.getPayload();
                    
                    if (data.getType() == Data.REQUEST_BID) {
                        if (bidRequest.getPrice() > auction.getLastBid().getPrice()) {
                            auction.setLastBid(bidRequest);
                            sendLastBid();
                            callback.run();
                        }
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    public void sendLastBid() {
        try {
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
            ObjectOutputStream outputObject = new ObjectOutputStream(outputBytes);

            outputObject.writeObject(new Data(Data.RESPONSE_BID, auction.getLastBid()));
            byte[] bytesData = outputBytes.toByteArray();
            DatagramPacket packet = new DatagramPacket(bytesData, bytesData.length, auction.getAddress(), auction.getPort());
            socket.send(packet);
            System.out.println("Lastbid Send");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void sendEndAuction() {
        try {
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
            ObjectOutputStream outputObject = new ObjectOutputStream(outputBytes);

            outputObject.writeObject(new Data(Data.AUCTION_END));
            byte[] bytesData = outputBytes.toByteArray();
            DatagramPacket packet = new DatagramPacket(bytesData, bytesData.length, auction.getAddress(), auction.getPort());
            socket.send(packet);
            System.out.println("Lastbid Send");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
