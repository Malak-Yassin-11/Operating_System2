package project_os;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Customers implements Runnable {
      int customerId;
        Date inTime;
        Shop shooop;
 
    public Customers(Shop shoooopp) {
    
        this.shooop = shoooopp;
    }
 
    public int getCustomertId() {						 //getter and setter methods
        return customerId;
    }
 
    public Date getInTime() {
        return inTime;
    }
 
    public void setcustomerId(int customerId) {
        this.customerId = customerId;
    }
 
    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }
 @Override
    public void run() {
        try {
        goGetHairCut();
        } catch (InterruptedException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private synchronized void goGetHairCut() throws InterruptedException {		//customer is added to the list
       
        shooop.add(this);
    }
}