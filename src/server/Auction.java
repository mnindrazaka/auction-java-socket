/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Data.Bid;
import java.net.InetAddress;

/**
 *
 * @author aka
 */
public class Auction {
    private final Product product;
    private Bid lastBid;
    private final InetAddress address;
    private final int port;
    private final Auctioneer auctioneer;

    public Auction(Product product, InetAddress address, int port) {
        this.product = product;
        this.lastBid = new Bid(product.getPrice());
        this.address = address;
        this.port = port;
        this.auctioneer = new Auctioneer(this);
    }
    
    public void startAuction() {
        auctioneer.joinAuction();
        auctioneer.listenBid();
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
}
