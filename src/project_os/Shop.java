package project_os;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
public class Shop {
     private final ReentrantLock mutex1=new ReentrantLock();
     private final ReentrantLock mutex2=new ReentrantLock();
	int no_of_chairs, no_of_BRs, availableBRs;
        int TotalCustomersCounter,CustomersListcounter;   
    List<Customers> listCustomers;
    Semaphore available=new Semaphore(availableBRs);
    Random r = new Random();	 
    List<Customers> BackLaterCustomers;
    int noOfCustomers;
    int waitingcounter;
   int busycount =0;
   
    public Shop(int no_of_BRs, int noOfChairs,int noOfCustomers) throws InterruptedException{
        this.no_of_chairs = noOfChairs;								//number of chairs 
      listCustomers = new LinkedList<Customers>();						//list to store customers
        this.no_of_BRs = no_of_BRs;								//initializing the the total number of Brs
        availableBRs = no_of_BRs;
        this.noOfCustomers=noOfCustomers;
        TotalCustomersCounter=0;
       CustomersListcounter=0;
       this.waitingcounter=0;
       available = new Semaphore(availableBRs);
        BackLaterCustomers=new ArrayList<Customers>(noOfCustomers);
    }
 
    public int getTotalCustomersCounter() {
    	
    	return TotalCustomersCounter;
    }
    
    public int getCustomersList() {
    	
    	return CustomersListcounter;
    }
     public int workingbr(){
    return busycount;
    
    }
     public int getwaitingchair(){
    return waitingcounter;
    }
    
    public void GetHairCut(int BrID)
    {
        Customers customers;
        synchronized (listCustomers) {						// to make customers go to salon one by one 
        									//unexpected errors in the list when multiple threads access it
            while(listCustomers.size()==0) {
            
                System.out.println("\n the BR "+BrID+" is waiting "
                		+ "for the Customers and sleeps in his chair");
                
                try {
                
                    listCustomers.wait();					//Br sleeps if there are no customers 
                }
                catch(InterruptedException iex) {
                
                    iex.printStackTrace();
                }
            }
            
            customers = (Customers)((LinkedList<?>)listCustomers).poll();	//takes the customer from the head of the list
            
            System.out.println("Customer"+customers.getCustomertId()+
            		" wakes up "
            		+ "the Barber: "+BrID);
        }
        
        int millisDelay=0;
                
        try {
        	if(available.tryAcquire()&& listCustomers.size()==no_of_chairs){
                    
        	available.acquire(); 
                mutex1.lock();
                busycount--;
                mutex1.unlock();
                }								//decreases the count of the available Brs as one of them starts 
        																
            System.out.println("the Br"+BrID+" making a hair cut for customer: "+
            		customers.getCustomertId());
        	
            double val = r.nextGaussian() * 2000 + 4000;			//time taken a hair cut for customer
        	millisDelay = Math.abs((int) Math.round(val));			
        	Thread.sleep(millisDelay);
        	
        	System.out.println("\nCompleted hair cut for customer : "+
        			customers.getCustomertId()+" by the BR " + 
        			BrID +" ...in "+millisDelay+ " milliseconds.");
        
                  mutex1.lock();
            try {
                TotalCustomersCounter++;
            } finally {
                mutex1.unlock();
            }
               
            															
            if(listCustomers.size()>0) {									
            	System.out.println("the Br :"+BrID+					
            			" take a customers from the "					
            			+ "waiting chiars");
                
                mutex2.lock();
                waitingcounter++;
                mutex2.unlock();
            }
            
            available.release();						//Br is available for hair cut for  next customer 
            mutex1.lock();
            busycount++;
            mutex1.unlock();
            
            if(busycount>no_of_BRs){
            busycount=no_of_BRs;
            }
            else if(busycount<0){
            busycount=0;
            }
        
        }
        catch(InterruptedException iex) {
        
            iex.printStackTrace();
        }
        
    }
 
    public void add(Customers Customer) throws InterruptedException {
    
        System.out.println("\ncustomer"+Customer.getCustomertId()+
        		" enters the Salon "
        		+Customer.getInTime());
         
 
        synchronized (listCustomers) {
        
            if(listCustomers.size() == no_of_chairs) {				//No chairs are available for the customer so he will back later
                System.out.println("\nNo chair available "
                		+ "for Customer "+Customer.getCustomertId()+
                		" so Customer back later!");
                
              BackLaterCustomers.add(Customer);
              mutex1.lock();
                try {
                    CustomersListcounter++;
                } finally {
                    mutex1.unlock();
                }
              
                return;
            }
            else if (availableBRs > 0) {					//If ta is available then the customer wakes up the br
            	((LinkedList<Customers>)listCustomers).offer(Customer);
				listCustomers.notify();
			}
            else{								//If brs are busy and there are chairs then the customer
            									//sits on the chair 
            	((LinkedList<Customers>)listCustomers).offer(Customer);
                
            	System.out.println("All BR(s) are busy so "+
            			Customer.getCustomertId()+
                		" Waits on the waiting chair");
                 mutex2.lock();
                waitingcounter--;
                mutex2.unlock();
                 
                if(listCustomers.size()==1)
                    listCustomers.notify();
            }
        }
    }
    public List<Customers> Backlater(){
        return BackLaterCustomers;
    }
}

