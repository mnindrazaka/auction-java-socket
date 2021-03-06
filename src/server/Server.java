/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Data.Data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aka
 */
public class Server {

    private ServerSocket server;
    private Socket socket;
    private static final int PORT = 3000;
    private ArrayList<Auction> auctions = new ArrayList<>();

    public void startServer() {
        try {
            server = new ServerSocket(PORT);
            listenRequest();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void stopServer() {
        try {
            server.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void listenRequest() {
        new Thread(() -> {
            while (true) {
                try {
                    socket = server.accept();
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                    new Thread(() -> handleRequest(input, output)).start();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
        
    }

    public void handleRequest(ObjectInputStream input, ObjectOutputStream output) {
        try {
            while (true) {
                Data request = (Data) input.readObject();
                if (request.getType() == Data.REQUEST_AUCTIONS) {
                    sendAuctions(output);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void sendAuctions(ObjectOutputStream output) {
        try {
            output.writeObject(new Data(Data.RESPONSE_AUCTIONS, auctions));
            output.reset();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addAuction(Auction auction) {
        auctions.add(auction);
    }

    public void deleteAuction(int index) {
        auctions.remove(index);
    }

    public ArrayList<Auction> getAuctions() {
        return auctions;
    }
    
    public void print() {
        try {
            PrintWriter writer = new PrintWriter("output.pdf");
            writer.println("Product Name | Price | Address | Port | Last Bid | Last Bidder");
            
            auctions.forEach((auction) -> {
                if (auction.getStatus() == Auction.FINISHED) {
                    writer.print(auction.getProduct().getName() + " | ");
                    writer.print(auction.getProduct().getPrice() + " | ");
                    writer.print(auction.getAddress().toString() + " | ");
                    writer.print(auction.getPort() + " | ");
                    writer.print(auction.getLastBid().getPrice() + " | ");
                    writer.println(auction.getLastBid().getUsername());   
                }
            });
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
