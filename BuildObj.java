package Project2;

/**
 *
 * @author Thanakrit Jomhong 6713118
 */

import java.util.*;
import java.io.*;
import java.util.concurrent.*;


class Freight_num_max
{
	private	int	capacity;
	private	int	max;
	private	ArrayList<Freight> fr;

        public ArrayList<Freight> getArrayFreight() { return fr; }
	public	Freight_num_max(int c, int m)
	{
		capacity = c;
		max = m;
	}
	public	int getmax()
	{
		return (max);
	}
	public void build()
	{
		String	name;
		int	i;
	
		fr = new ArrayList<>();
		i = 0;
		while (i < capacity)
		{
			name = String.format("Freight_%d", i);
			fr.add(new Freight(name, max));
			i++;
		}
	}
	public ArrayList<String> show()
	{
		ArrayList<String> name;
		int	i;
		
		name = new ArrayList<>();
		i = 0;
		while (i < fr.size())
		{
			name.add(fr.get(i).getName());	
			i++;
		}
		return (name);
	}
}

class Factory_num_max
{
        private int     days;
	private	int	num;
	private	int	max;
	private	ArrayList<FactoryThread> ft;
        //private CyclicBarrier barrier;
        
        private CyclicBarrier startFactoryBarrier;
        private CyclicBarrier afterGetMatBarrier;
        private CyclicBarrier afterTotalBarrier;
        private CyclicBarrier afterShipBarrier;
        private CyclicBarrier afterUnshippedBarrier;
        
        public CyclicBarrier getStartFactoryBarrier() { return startFactoryBarrier; }
        public CyclicBarrier getAfterGetMatBarrier() { return afterGetMatBarrier; }
        public CyclicBarrier getAfterTotalBarrier() { return afterTotalBarrier; }
        public CyclicBarrier getAfterShipBarrier() { return afterShipBarrier; }
        public CyclicBarrier getAfterUnshippedBarrier() { return afterUnshippedBarrier; }

	public	Factory_num_max(int d, int c, int m)
	{
                days = d;
		num = c;
		max = m;
                startFactoryBarrier = new CyclicBarrier(num + 1);
                afterGetMatBarrier = new CyclicBarrier(num + 1);
                afterTotalBarrier = new CyclicBarrier(num + 1);
                afterShipBarrier = new CyclicBarrier(num + 1);
                afterUnshippedBarrier = new CyclicBarrier(num + 1);
                
	}
	public int getmax()     { return (max); }
        public ArrayList<FactoryThread> getArrayFactory() { return ft; }
        //public CyclicBarrier getBarrier() { return barrier; }
        
	public void buildthread(ArrayList<Warehouse> wh, ArrayList<Freight> fr)
	{
		String	name;
		int	i;

		i = 0;
		ft = new ArrayList<>();
		while (i < num)
		{
			name = String.format("FactoryThread_%d", i);
			ft.add(new FactoryThread(days, name, max, wh, fr, startFactoryBarrier,
                                afterGetMatBarrier, afterTotalBarrier, afterShipBarrier, afterUnshippedBarrier));
			i++;
		}
	}
	public ArrayList<String> showthread()
	{
		ArrayList<String> Threadname;
		int	i;	
		
		Threadname = new ArrayList<>();
		i = 0;
		while (i < ft.size())
		{
			Threadname.add(ft.get(i).getName());
			i++;	
		}
		return (Threadname);
	}
}

class Supplier_num_min_max
{
        private int     days;
	private	int	num;
	private	int	min;
	private	int	max;
	private	ArrayList<SupplierThread> st;
        private CyclicBarrier reportBarrier;
        private CyclicBarrier afterSupplierBarrier;
	
	public	Supplier_num_min_max(int d, int n, int mi, int ma)
	{
                days = d;
		num = n;
		min = mi;
		max = ma;
                reportBarrier = new CyclicBarrier(num + 1);
                afterSupplierBarrier = new CyclicBarrier(num + 1);
	}
	public int getmin()     { return min; }
	public int getmax()     { return max; }
        public int getNum()     { return num; }
        public CyclicBarrier getReportBarrier()   { return reportBarrier; }
        public CyclicBarrier getAfterSupplierBarrier()   { return afterSupplierBarrier; }
        public	ArrayList<SupplierThread> getArraySupplier()    { return st; }
        
        public void setArrayWarehouse(ArrayList<Warehouse> whse)     { 
            int i = 0;
            while (i < num)
            {
                st.get(i).setArrayWarehouse(whse);
                i++;
            }
        }

	public void buildthread()
	{
		String	name;
		int	i;

		i = 0;
		st = new ArrayList<>();
		while (i < num)
		{
			name = String.format("SupplierThread_%d", i);
			st.add(new SupplierThread(days, name, min, max, reportBarrier, afterSupplierBarrier));
			i++;
		}
	}
	public ArrayList<String> showthread()
	{
		ArrayList<String> Threadname;
		int	i;	
		
		Threadname = new ArrayList<>();
		i = 0;
		while (i < st.size())
		{
			Threadname.add(st.get(i).getName());
			i++;	
		}
		return (Threadname);
	}
}

class Warehouse_num
{
	private int num;
	private ArrayList<Warehouse> wh;

	public Warehouse_num(int n)
	{
		num = n;
	}
        
        public ArrayList<Warehouse> getArrayWarehouse()      { return wh; }
        public int getNum()                                  { return num; }
        
	public void build()
	{
		String	name;
		int	i;
	
		wh = new ArrayList<>();
		i = 0;
		while (i < num)
		{
			name = String.format("Warehouse_%d", i);
			wh.add(new Warehouse(name));
			i++;
		}
	}
	public ArrayList<String> show()
	{
		ArrayList<String> name;
		int	i;
		
		name = new ArrayList<>();
		i = 0;
		while (i < wh.size())
		{
			name.add(wh.get(i).getName());	
			i++;
		}
		return (name);
	}
}
