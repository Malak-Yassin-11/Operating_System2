package project_os;
public class BR implements Runnable {
     private Shop BrShop;
     private int BrId;
 
    public BR(Shop brshop, int BrId) {
    
        this.BrShop = brshop;
        this.BrId = BrId;
    }
    @Override
    public void run() {
    
        while(true) {
        
            BrShop.GetHairCut(BrId);
        }
    }
}


