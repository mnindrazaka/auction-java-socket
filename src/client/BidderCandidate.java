/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Data.Data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Auction;

/**
 *
 * @author aka
 */
public class BidderCandidate {
    
    Socket socket;
    InetAddress serverAddress;
    int serverPort;
    ObjectOutputStream output;
    ObjectInputStream input;
    ArrayList<Auction> auctions = new ArrayList<>();

    public BidderCandidate() {
        try {
            serverAddress = InetAddress.getByName("localhost");
            serverPort = 3000;
            socket = new Socket(serverAddress, serverPort);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException ex) {
            Logger.getLogger(BidderCandidate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BidderCandidate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendAuctionListRequest() {
        try {
            Data data = new Data(Data.REQUEST_AUCTIONS);
            output.writeObject(data);
        } catch (IOException ex) {
            Logger.getLogger(BidderCandidate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void listenAuctionList(Runnable callback) {
        new Thread(() -> {
            while (true) {
                try {
                    Data data = (Data) input.readObject();
                    
                    if (data.getType() == Data.RESPONSE_AUCTIONS) {
                        auctions = (ArrayList<Auction>) data.getPayload();
                        callback.run();
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(BidderCandidate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    public ArrayList<Auction> getAuctions() {
        return auctions;
    }
}
