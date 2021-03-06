/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Data.Bid;
import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author aka
 */
public class Auction implements Serializable {
    public static final int NOT_STARTED = 1;    
    public static final int STARTED = 2;
    public static final int FINISHED = 3;

    
    private final Product product;
    private Bid lastBid;
    private final InetAddress address;
    private final int port;
    private final Auctioneer auctioneer;
    private int status = Auction.NOT_STARTED;

    public Auction(Product product, InetAddress address, int port) {
        this.product = product;
        this.lastBid = new Bid(product.getPrice());
        this.address = address;
        this.port = port;
        this.auctioneer = new Auctioneer(this);
    }
    
    public void startAuction(Runnable callback) {
        auctioneer.joinAuction();
        auctioneer.listenBid(callback);
    }
    
    public void stopAuction() {
        auctioneer.leaveAuction();
    }

    public Product getProduct() {
        return product;
    }

    public Bid getLastBid() {
        return lastBid;
    }

    public void setLastBid(Bid lastBid) {
        this.lastBid = lastBid;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
    
    public Auctioneer getAuctioneer() {
        return auctioneer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
