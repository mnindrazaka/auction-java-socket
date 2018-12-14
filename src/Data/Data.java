package Data;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aka
 */
public class Data implements Serializable {
    private int type;
    private Object payload;
    
    public static final int REQUEST_AUCTIONS = 0;    
    public static final int RESPONSE_AUCTIONS = 1;
    public static final int REQUEST_BID = 2;
    public static final int RESPONSE_BID = 3;    
    public static final int AUCTION_END = 4;

    public Data(int type) {
        this.type = type;
    }
    
    public Data(int type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
