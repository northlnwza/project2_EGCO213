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
        private int days;
        private int min;
        private int max;
        private ArrayList<Warehouse> wareHouse;
        private CyclicBarrier reportBarrier;
        private CyclicBarrier afterSupplierBarrier;
        private boolean canRun;
        private boolean isEnd;
        
	public SupplierThread(int d, String n, int mi, int ma, CyclicBarrier report, CyclicBarrier after)
	{
            super(n);
            this.days = d;
            this.min = mi; 
            this.max = ma;
            this.reportBarrier = report;
            this.afterSupplierBarrier = after;
	}
        
        public void setArrayWarehouse(ArrayList<Warehouse> whse) { wareHouse = whse; }
        public void setPermits(boolean r, boolean e)             { canRun = r; isEnd = e;}
                
        public void run() {
            while(!isEnd) {
                for(int i = 0; i < days; i++) {
                    this.setPermits(true, false);
                    Random rand = new Random();
                    int numWhse = wareHouse.size();
                    int chooseWhse = rand.nextInt(numWhse);
                    
                    try { reportBarrier.await(); } catch(Exception e) { } 
                    
                    wareHouse.get(chooseWhse).putMaterial(min, max);
                    
                    try { afterSupplierBarrier.await(); } catch (Exception e) { }

                    while(canRun) { try { Thread.sleep(100); } catch(Exception e) {} }
                }
            } 
        }
        
}
///
class FactoryThread extends Thread implements Comparable<FactoryThread>
{
    private int days;
    private int num;
    private int max;
    private ArrayList<Warehouse> wareHouse;
    private ArrayList<Freight> freights;
    //private CyclicBarrier cbFactory;
    private boolean isEnd;
    private Random rand = new Random();
    
    private CyclicBarrier startFactoryBarrier;
    private CyclicBarrier afterGetBarrier;
    private CyclicBarrier afterTotalBarrier;
    private CyclicBarrier afterShipBarrier;
    private CyclicBarrier afterUnshippedBarrier;
    
    private int totalCreated = 0;
    private int totalShipped = 0;
    private int unshippedPrev = 0;
    
        public int getTotalCreated() { return totalCreated; }
        public int getTotalShipped() { return totalShipped; }
    
        public void setEnd(boolean e) { isEnd = e; }
        //public void setArrayWarehouse(ArrayList<Warehouse> whse) { wareHouse = whse; }
        //public void setArrayFreight(ArrayList<Freight> fr) { freights = fr; }
	public FactoryThread(int d, int num, String n, int maxP, ArrayList<Warehouse> wh, ArrayList<Freight> fr,
                CyclicBarrier start, CyclicBarrier afterGet, CyclicBarrier afterTotal, CyclicBarrier afterShip, CyclicBarrier afterUnshipped)
	{
                super(n);
                this.days = d;
                this.num = num;
                this.max = maxP;
                this.wareHouse = wh;
                this.freights = fr;
                this.startFactoryBarrier = start;
                this.afterGetBarrier = afterGet;
                this.afterTotalBarrier = afterTotal;
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
                    for(int i = 0; i < days; i++) {
                        //phase1
                        startFactoryBarrier.await();

                        //get materials from random warehouse
                        int numWhse = wareHouse.size();
                        int chooseWhse = rand.nextInt(numWhse);
                        int got = wareHouse.get(chooseWhse).getMatetial(max,num); //// get random value

                        afterGetBarrier.await();

                        int created = got;
                        totalCreated += created;
                        //print total to ship
                        int totalToShip = created + unshippedPrev;
                        System.out.printf("   %s  >>  total products to ship = %5d\n", getName(), totalToShip);

                        afterTotalBarrier.await();

                        int fIdx = rand.nextInt(freights.size());
                        Freight f = freights.get(fIdx);
                        int shipped = f.ship(totalToShip);
                        totalShipped += shipped;
                        int unshippedNow = totalToShip - shipped;

                        // print ship line and freight remaining capacity
                        System.out.printf("   %s  >>  ship %3d products      %s remaining capacity = %4d\n",
                                getName(), shipped, f.getName(), f.getRemaining());

                        afterShipBarrier.await();

                        unshippedPrev = unshippedNow;
                        System.out.printf("   %s  >>  unshipped products = %5d\n", getName(), unshippedPrev);

                        afterUnshippedBarrier.await();
                    }
                }
                catch(Exception e) { }  
            }
        }
        
        @Override
        public int compareTo(FactoryThread other)
        {
            if (this.totalCreated < other.totalCreated)       return 1;	
            else if (this.totalCreated > other.totalCreated)  return -1;	
            else {
                int x = (this.getName()).compareToIgnoreCase(other.getName());
                if (x>0) return 1;
                else if (x<0) return -1;
                else return 0;
            }
        }

        @Override
        public boolean equals(Object param)
        {
            FactoryThread other = (FactoryThread) param;
            return (this.getName()) == (other.getName());
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
        
        synchronized public void putMaterial(int min, int max) {
            Thread current = Thread.currentThread();
            Random rand = new Random();
            int randNum = rand.nextInt(max - min) + min;
            balance += randNum;
            System.out.printf("  %s  >>  put%4d materials      %s balance =%6d\n", current.getName(), randNum, name, balance);
        }
        
        private static int totalCount = 0;
        synchronized public int getCount() {totalCount++; return totalCount;}
        
        synchronized public int getMatetial(int want, int num) {
            Thread current = Thread.currentThread();
            int got = Math.min(want, balance);
            balance -= got;
            System.out.printf("   %s  >>  get%4d materials      %s balance =%6d\n", current.getName(), got, name, balance);
            int count = getCount();
            if (count % num == 0) System.out.printf("   %s  >>\n", current.getName());
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
            
            supplier_num_min_max.setArrayWarehouse(warehouse_num.getArrayWarehouse());
            ArrayList<Warehouse> wareHouses = warehouse_num.getArrayWarehouse();
            ArrayList<SupplierThread> suppliers = supplier_num_min_max.getArraySupplier();
            ArrayList<Freight> freights = freight_num_max.getArrayFreight();
            factory_num_max.buildthread(wareHouses, freights);
            ArrayList<FactoryThread> factories = factory_num_max.getArrayFactory();

            
            CyclicBarrier startSupplier = supplier_num_min_max.getReportBarrier();
            CyclicBarrier afterSupplier = supplier_num_min_max.getAfterSupplierBarrier();
            CyclicBarrier startFactory = factory_num_max.getStartFactoryBarrier();
            CyclicBarrier afterGetMat = factory_num_max.getAfterGetMatBarrier();
            CyclicBarrier afterTotal = factory_num_max.getAfterTotalBarrier();
            CyclicBarrier afterShip = factory_num_max.getAfterShipBarrier();
            CyclicBarrier afterUnshipped = factory_num_max.getAfterUnshippedBarrier();
            
            // start supplier threads
            if (suppliers != null) {
            for (SupplierThread st : suppliers) {
                st.setPermits(true, false);
                st.start();
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
                
                // --- Reset & Report everyday ---
		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  \n", Thread.currentThread().getName());
                System.out.print(" ".repeat(14));
                System.out.printf("%s  >>  ", Thread.currentThread().getName());
                System.out.print("=".repeat(52));
                System.out.printf("\n");
                System.out.print(" ".repeat(14));
                System.out.printf("%s  >>  Day %d\n", Thread.currentThread().getName(), i+1);

                for(Warehouse whse : wareHouses) {
                    System.out.print(" ".repeat(14));
                    System.out.printf("%s  >>  %s balance  =%6d\n", Thread.currentThread().getName(), whse.getName(), whse.getBalance());
                }
 
                // reset freight capacities at day start
                for (Freight f : freights) {
                    f.reset(freight_num_max.getmax());
                    System.out.print(" ".repeat(14));
                    System.out.printf("%s  >>  %s   capacity =%6d\n", Thread.currentThread().getName(), f.getName(), f.getRemaining());
                }
                
                System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  \n", Thread.currentThread().getName());
                
                // --- report phase done: main + suppliers sync on reportBarrier ---
                try { startSupplier.await(); } catch (Exception e) { }
                
                // --- supplier phase done : main + suppliers sync on startBarrier ---
                try { afterSupplier.await(); } catch (Exception e) { }

                // --- factory phased sequence ---
                // 1) let factories start create (they will get materials and print totals)
                try { startFactory.await(); } catch (Exception e) { }

                // 2) wait until all factories finished get matetials (they each call afterGetMat.await())
                try { afterGetMat.await(); } catch (Exception e) { }
                
                // 3) wait until all factories finished Total previous (they each call afterToal.await())
                try { afterTotal.await(); } catch (Exception e) { }

                // 4) wait until factories finished ship
                try { afterShip.await(); } catch (Exception e) { }

                // 5) wait until factories finished unshipped printing
                try { afterUnshipped.await(); } catch (Exception e) { }

                // small pause to keep output readable
                try { Thread.sleep(50); } catch (Exception e) {}
                
                // manage supplier permits for next day like original logic
                for (SupplierThread st : suppliers) {
                    st.setPermits(false, false);
                }
            }
            
            // stop supplier threads
            if (suppliers != null) {
                for (SupplierThread st : suppliers) {
                    st.setPermits(false, true);
                    try { st.join(100); } catch (InterruptedException ex) {}
                }
            }
            // stop factory threads
            if (factories != null) {
                for (FactoryThread ft : factories) {
                    ft.setEnd(true);
                    try { ft.join(100); } catch (InterruptedException ex) {}
                }
            }
            // --- Summary ---
            System.out.print(" ".repeat(14));
            System.out.printf("%s  >>  \n", Thread.currentThread().getName());
            System.out.print(" ".repeat(14));
            System.out.printf("%s  >>  ", Thread.currentThread().getName());
            System.out.print("=".repeat(52));
            System.out.printf("\n");
            System.out.print(" ".repeat(14));
            System.out.printf("%s  >>  Summary\n", Thread.currentThread().getName());
            
            if (factories != null) {
                Collections.sort(factories);
                for (FactoryThread ft : factories) {
                    int created = ft.getTotalCreated();
                    int shipped = ft.getTotalShipped();
                    float ratio = ((float)shipped/created)*100;
                    System.out.print(" ".repeat(14));
                    System.out.printf("%s  >>  %s    total products = %5d    shipped = %5d (%5.2f%%)\n"
                            ,Thread.currentThread().getName(),ft.getName(),created,shipped,ratio);
                }
            }
        }
        
	public void print()
	{
		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  ", Thread.currentThread().getName());
		System.out.print("=".repeat(20));
		System.out.print(" Parameters ");
		System.out.println("=".repeat(20));

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Days of simulation : %d\n", Thread.currentThread().getName(), days);

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Warehouses         : %s\n", Thread.currentThread().getName(), warehouse_num.show().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Freights           : %s\n", Thread.currentThread().getName(), freight_num_max.show().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Freight capacity   : max = %d\n", Thread.currentThread().getName(), freight_num_max.getmax());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  SupplierThreads    : %s\n", Thread.currentThread().getName(), supplier_num_min_max.showthread().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Daily supply       : min = %d, max = %d\n", Thread.currentThread().getName(), supplier_num_min_max.getmin(), supplier_num_min_max.getmax());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  FactoryThreads     :%s\n", Thread.currentThread().getName(), factory_num_max.showthread().toString());

		System.out.print(" ".repeat(14));
		System.out.printf("%s  >>  Daily production   : max = %d\n", Thread.currentThread().getName(), factory_num_max.getmax());

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
			supplier_num_min_max = new Supplier_num_min_max(days, Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()), Integer.parseInt(cols[3].trim()));
			supplier_num_min_max.buildthread();
		}
		else if (cols[0].equalsIgnoreCase("factory_num_max"))
		{
			factory_num_max = new Factory_num_max(days, Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()));
			//factory_num_max.buildthread();
		}
	}
}
