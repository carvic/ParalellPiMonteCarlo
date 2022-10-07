import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.*;

public class ParalellPiMonteCarlo {
	
	public static double estimatePi(long precision) {
		// TODO Auto-generated method stub
		
		// circle area / square area =   pi r2 / (2r) 2 =  pi r 2 = 4 r2  = nCircle/nTotal, 
		// pi= nCircle/nTotal *4
		
		Random rand = new Random();
		
		double nTotal=0;
		double nCircle=0;
			
		for (int i=0;i<precision;i++) {
		
			double px = rand.nextDouble();
			double py = rand.nextDouble();
			
			//if (Math.sqrt(px*px + py*py)<=1)
     		if (px*px + py*py<=1)				
				nCircle++;
			
			nTotal++;
			
			//System.out.println("-> iter " + i + " px = " + px + " py=" + py +  " pi = " + (nCircle/nTotal)*4.0 );	
		}
		
		return (nCircle/nTotal)*4.0;

	}
	
	
	
	
	/**
	 * @param precision
	 * @param n
	 */
	public static void parallelProcessing(long precision, int n) {
	    try {
	        ExecutorService executorService = Executors.newWorkStealingPool(10);
	        

	        List<CompletableFuture<Double>> futuresList = new ArrayList<CompletableFuture<Double>>();
	        
	        for (int i=0;i<n;i++) 
	        	futuresList.add(CompletableFuture.supplyAsync(()->(estimatePi(precision)), executorService));

	        
	        
	        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
	        CompletableFuture<List<Double>> allCompletableFuture = allFutures.thenApply(future -> futuresList.stream().map(completableFuture -> completableFuture.join())
	                .collect(Collectors.toList()));
	        CompletableFuture<List<Double>> completableFuture = allCompletableFuture.toCompletableFuture();
	        List<Double> finalList = (List<Double>) completableFuture.get();
	        
	        
	        double accum=0;
	        for (int i=0; i<finalList.size();i++) {
	        	
	        	double subRes=finalList.get(i).doubleValue();
	        	accum+=subRes;
	        	
		        //System.out.println(subRes);		        
	        }
	        
	        
	        
	        System.out.println(accum/n);		        

	        

	        
	    } catch (Exception ex) {

	    }
	}

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		


		int cores = Runtime.getRuntime().availableProcessors();
		
		System.out.println("cores = " + cores);

		parallelProcessing(20000000,cores);
		

	}

}
