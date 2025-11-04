package Project2;

/**
 *
 * @author Thanakrit Jomhong 6713118
 */

import java.util.*;
import java.io.*;
import java.util.concurrent.*;

class SupplierThread extends Thread
{
        private int min;
        private int max;
        private ArrayList<Warehouse> wareHouse;
        private CyclicBarrier cbSupplier;
        private boolean canRun;
        private boolean isEnd;
        
	public SupplierThread(String n, int mi, int ma, CyclicBarrier b)
	{
            super(n);
            min = mi; 
            max = ma;
            cbSupplier = b;
	}
        
        public void setArrayWarehouse(ArrayList<Warehouse> whse) { wareHouse = whse; }
        public void setPermits(boolean r, boolean e)             { canRun = r; isEnd = e;}
                
        public void run() {
            while(!isEnd) {
                Random rand = new Random();
                int numWhse = wareHouse.size();
                int chooseWhse = rand.nextInt(numWhse);
                wareHouse.get(chooseWhse).put(min, max);
                try { 
                    cbSupplier.await(); 
                    Thread.sleep(10);
                } catch(Exception e) { }  
                
                while(canRun) { try { Thread.sleep(100); } catch(Exception e) {} }
            } 
        }
        
}
///
class FactoryThread extends Thread
{
    
    private int max;
    private ArrayList<Warehouse> wareHouse;
    private ArrayList<Freight> freights;
    //private CyclicBarrier cbFactory;
    private boolean isEnd;
    private Random rand = new Random();
    
    private CyclicBarrier factoryStartBarrier;
    private CyclicBarrier afterCreateBarrier;
    private CyclicBarrier afterShipBarrier;
    private CyclicBarrier afterUnshippedBarrier;
    
    private int totalCreated = 0;
    private int totalShipped = 0;
    private int unshippedPrev = 0;
    
        public int getTottalCreated() { return totalCreated; }
        public int getTotalShipped() { return totalShipped; }
    
        public void setEnd(boolean e) { isEnd = e; }
        //public void setArrayWarehouse(ArrayList<Warehouse> whse) { wareHouse = whse; }
        //public void setArrayFreight(ArrayList<Freight> fr) { freights = fr; }
	public FactoryThread(String n, int maxP, ArrayList<Warehouse> wh, ArrayList<Freight> fr,
                CyclicBarrier start, CyclicBarrier afterCreate, CyclicBarrier afterShip, CyclicBarrier afterUnshipped)
	{
		super(n);
                max = maxP;
                wareHouse = wh;
                freights = fr;
                this.factoryStartBarrier = start;
                this.afterCreateBarrier = afterCreate;
                this.afterShipBarrier = afterShip;
                this.afterUnshippedBarrier = afterUnshipped;
                //cbFactory = b;
	}
        
        public void run()
        {
            while(!isEnd)
            {
                try
                {
                    //phase1
                    factoryStartBarrier.await();
                    
                //get materials from random warehouse
                
                int numWhse = wareHouse.size();
                int chooseWhse = rand.nextInt(numWhse);
                int got = wareHouse.get(chooseWhse).get(max); //// get random value   
                int created = got;
                totalCreated += created;
                
                //print total to ship
                int totalToShip = created + unshippedPrev;
                System.out.printf("%s  >>  total products to ship = %4d\n", getName(), totalToShip);
                
                afterCreateBarrier.await();
                
                int fIdx = rand.nextInt(freights.size());
                Freight f = freights.get(fIdx);
                int shipped = f.ship(totalToShip);
                totalShipped += shipped;
                int unshippedNow = totalToShip - shipped;

                // print ship line and freight remaining capacity
                System.out.printf("%s  >>  ship %4d products      %s remaining capacity = %4d\n",
                        getName(), shipped, f.getName(), f.getRemaining());
                
                afterShipBarrier.await();
                
                unshippedPrev = unshippedNow;
                System.out.printf("%s  >>  unshipped products = %4d\n", getName(), unshippedPrev);
                
                afterUnshippedBarrier.await();
                
                }
                catch(Exception e) { }  
            }
        }
}
class Warehouse
{
	private	String	name;
        private int     balance;
         
	public Warehouse(String n)      { 
            name = n; 
            balance = 0;
        }
	public String getName()         { return name; }
        public int getBalance()         { return balance; }
        
        synchronized public void put(int min, int max) {
            Thread current = Thread.currentThread();
            Random rand = new Random();
            int randNum = rand.nextInt(max - min + 1) + min;
            
            balance += randNum;
            System.out.print(" ".repeat(2));
            System.out.printf("%s  >>  put%4d materials      %s balance =%7d\n", current.getName(), randNum, name, balance);
        }
        
        synchronized public int get(int want) {
            Thread current = Thread.currentThread();
            int got = Math.min(want, balance);
            balance -= got;
            System.out.print(" ".repeat(3));
            System.out.printf("%s  >>  get%4d materials      %s balance =%7d\n", current.getName(), got, name, balance);
            return got;
}
}

class Freight
{
	private String name;
        private int max;
        private int remaining;
	public Freight(String n , int m)        { name = n; max = m; remaining = max;}
	public String getName()         { return name; }
        
        //reset everyday
        public synchronized void reset(int m)
        {
            this.max = m;
            this.remaining = m; 
                 
        }
        
        public synchronized int ship(int amount)
        {
            int shipped = Math.min(amount, remaining);
            remaining -= shipped;
            return shipped;
        }
        public synchronized int getRemaining() { return remaining; }
        public synchronized int getMaxCapacity() { return max; }
        
}

public class Project2 
{
	private	int	days;
	private	Warehouse_num warehouse_num;
	private	Freight_num_max freight_num_max;
	private	Supplier_num_min_max supplier_num_min_max;
	private	Factory_num_max factory_num_max;

	public static void main(String[] args)
	{
            Project2	obj;

            obj = new Project2();
            obj.input("config_1.txt"); // correct name is config_1.txt
            
            ArrayList<Warehouse> wh = obj.warehouse_num.getArrayWarehouse();
            ArrayList<Freight> fr = obj.freight_num_max.getArrayFreight();
            obj.factory_num_max.buildthread(wh, fr);
            obj.print();
            obj.runDaySimulation();
	}    
        
        public void runDaySimulation() {
            CyclicBarrier cbSupplier = supplier_num_min_max.getBarrier();
            supplier_num_min_max.setArrayWarehouse(warehouse_num.getArrayWarehouse());
            ArrayList<Warehouse> wareHouses = warehouse_num.getArrayWarehouse();
            ArrayList<SupplierThread> suppliers = supplier_num_min_max.getArraySupplier();
            
            ArrayList<Freight> freights = freight_num_max.getArrayFreight();
            factory_num_max.buildthread(wareHouses, freights);
            ArrayList<FactoryThread> factories = factory_num_max.getArrayFactory();

            
            
            CyclicBarrier factoryStart = factory_num_max.getFactoryStartBarrier();
            CyclicBarrier afterCreate = factory_num_max.getAfterCreateBarrier();
            CyclicBarrier afterShip = factory_num_max.getAfterShipBarrier();
            CyclicBarrier afterUnshipped = factory_num_max.getAfterUnshippedBarrier();
            
            // start supplier threads
            if (suppliers != null) {
            for (SupplierThread st : suppliers) {
                st.start();
                st.setPermits(true, false);
                }
            }   

            // start factory threads
            if (factories != null) {
            for (FactoryThread ft : factories) {
                ft.start();
                }
            }
                        
            // Loop days
            for(int i = 0; i < days; i++) {
                System.out.print(" ".repeat(14));
                System.out.printf("%s  >>  ", Thread.currentThread().getName());
		System.out.print("=".repeat(52));
                System.out.printf("\n");
                System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Day %d\n", Thread.currentThread().getName(), i+1);
                
                for(Warehouse whse : wareHouses) {
                    System.out.print(" ".repeat(14));
                    System.out.printf("%s  >>  %s balance  =%7d\n", Thread.currentThread().getName(), whse.getName(), whse.getBalance());
                }
                
                // reset freight capacities at day start
                for (Freight f : freights) f.reset(freight_num_max.getmax());

                // --- supplier phase: main + suppliers sync on cbSupplier ---
                try { cbSupplier.await(); } catch (Exception e) { }

                // --- factory phased sequence ---
                // 1) let factories start create (they will get materials and print totals)
                try { factoryStart.await(); } catch (Exception e) { }

                // 2) wait until all factories finished create (they each call afterCreate.await())
                try { afterCreate.await(); } catch (Exception e) { }

                // 3) wait until factories finished ship
                try { afterShip.await(); } catch (Exception e) { }

                // 4) wait until factories finished unshipped printing
                try { afterUnshipped.await(); } catch (Exception e) { }

                // small pause to keep output readable
                try { Thread.sleep(50); } catch (Exception e) {}
                // manage supplier permits for next day like original logic
                for (SupplierThread st : suppliers) {
                st.setPermits(true, false);
                    }
                }

            
            // stop supplier threads
            if (suppliers != null) {
                for (SupplierThread st : suppliers) {
                    st.setPermits(false, true);
                    }
                }
            // stop factory threads
            if (factories != null) {
                for (FactoryThread ft : factories) {
                    ft.setEnd(true);
                    }
                }

    // join threads (optional)
    if (suppliers != null) {
        for (SupplierThread st : suppliers) {
            try { st.join(100); } catch (InterruptedException ex) {}
        }
    }
    if (factories != null) {
        for (FactoryThread ft : factories) {
            try { ft.join(100); } catch (InterruptedException ex) {}
        }
    }
                
//                if(i != 0) {
//                    for(SupplierThread st : suppliers) {
//                        st.setPermits(true, false);
//                    }
//                }
                                
                // Start Thread
                /*
                if(i == 0) {
                    for(SupplierThread st : suppliers) {
                        st.start();
                        st.setPermits(true, false);
                    }
                } else {
                    for(SupplierThread st : suppliers) {
                        st.setPermits(false, false);
                    }
                }
               
                try { cbSupplier.await(); } catch(Exception e) { }
                for(SupplierThread st : suppliers) {
                        st.setPermits(true, false);
                }
            }
            
            for(SupplierThread st : suppliers) {
                st.setPermits(false, true);
            }*/
        }
        
	public void print()
	{
		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  ", Thread.currentThread().getName());
		System.out.print("=".repeat(20));
		System.out.print(" Parameters ");
		System.out.println("=".repeat(20));

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Day of simulation : %d\n", Thread.currentThread().getName(), days);

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Warehouse         : %s\n", Thread.currentThread().getName(), warehouse_num.show().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Freights          : %s\n", Thread.currentThread().getName(), freight_num_max.show().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Freights capacity : max = %d\n", Thread.currentThread().getName(), freight_num_max.getmax());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  SupplierThreads   : %s\n", Thread.currentThread().getName(), supplier_num_min_max.showthread().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Daily supply      : min = %d, max = %d\n", Thread.currentThread().getName(), supplier_num_min_max.getmin(), supplier_num_min_max.getmax());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  FactoryThreads    :%s\n", Thread.currentThread().getName(), factory_num_max.showthread().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  FactoryThreads    : max = %d\n", Thread.currentThread().getName(), factory_num_max.getmax());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  \n", Thread.currentThread().getName());
	}
	public void input(String filename)
	{
		Scanner	scan;
		Scanner	scan_again;
		String	line;
		String	path;
		boolean	opensuccess;

		opensuccess = false;
		path = "src/main/Java/Project2/";
		while (!opensuccess)
		{
			try
			{
				scan = new Scanner(new File(path + filename));
				opensuccess = true;
				while (scan.hasNext())
				{
					line = scan.nextLine();
					this.buildAndSetObj(line);
				}
			}
			catch (FileNotFoundException e)
			{
				scan_again = new Scanner(System.in);
				System.out.print(e + " --> ");
                        	System.out.println("\nNew file name = ");
                        	filename = scan_again.next();
			}	
		}
	}
	public	void buildAndSetObj(String line)
	{
		String[] cols;

		cols = new String[4];
		cols = line.split(",");
		if (cols[0].equalsIgnoreCase("days"))
		{
			days = Integer.parseInt(cols[1].trim());
		}
		else if (cols[0].equalsIgnoreCase("warehouse_num"))
		{
			warehouse_num = new Warehouse_num(Integer.parseInt(cols[1].trim()));
			warehouse_num.build();
		}
		else if (cols[0].equalsIgnoreCase("freight_num_max"))
		{
			freight_num_max = new Freight_num_max(Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()));
			freight_num_max.build();
		}
		else if (cols[0].equalsIgnoreCase("supplier_num_min_max"))
		{
			supplier_num_min_max = new Supplier_num_min_max(Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()), Integer.parseInt(cols[3].trim()));
			supplier_num_min_max.buildthread();
		}
		else if (cols[0].equalsIgnoreCase("factory_num_max"))
		{
			factory_num_max = new Factory_num_max(Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()));
			//factory_num_max.buildthread();
		}
	}
}
