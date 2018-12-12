/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Data.Bid;
import Data.Data;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import server.Auction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 *
 * @author aka
 */
public class Bidder {

    private String username;
    private MulticastSocket socket;
    public Auction auction;

    public Bidder(String username, Auction auction) {
        try {
            this.username = username;
            this.auction = auction;
            this.socket = new MulticastSocket(auction.getPort());
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void joinAuction() {
        try {
            socket.joinGroup(auction.getAddress());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void leaveAuction() {
        try {
            socket.leaveGroup(auction.getAddress());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean makeBid(int price) {
        if (isPriceEnough(price)) {
            try {
                ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
                ObjectOutput outputObject = new ObjectOutputStream(outputBytes);

                outputObject.writeObject(new Data(Data.REQUEST_BID, new Bid(price, username)));
                byte[] bytesData = outputBytes.toByteArray();
                DatagramPacket packet = new DatagramPacket(bytesData, bytesData.length);
                socket.send(packet);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return isPriceEnough(price);
    }

    public boolean isPriceEnough(int price) {
        return price > auction.getLastBid().getPrice();
    }

    public void listenBid() {
        new Thread(() -> {
            try {
                byte[] incomingData = new byte[1024];
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);

                ByteArrayInputStream inputBytes = new ByteArrayInputStream(incomingPacket.getData());
                ObjectInputStream inputObject = new ObjectInputStream(inputBytes);

                Data data = (Data) inputObject.readObject();

                if (data.getType() == Data.RESPONSE_BID) {
                    auction.setLastBid((Bid) data.getPayload());
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }).start();
    }
}