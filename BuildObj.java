package Project2_6713118;

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
	public	ArrayList<Freight> fr;

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
			fr.add(new Freight(name));
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
	private	int	capacity;
	private	int	max;
	public	ArrayList<FactoryThread> ft;

	public	Factory_num_max(int c, int m)
	{
		capacity = c;
		max = m;
	}
	public int getmax()     { return (max); }
        
	public void buildthread()
	{
		String	name;
		int	i;

		i = 0;
		ft = new ArrayList<>();
		while (i < capacity)
		{
			name = String.format("FactoryThread_%d", i);
			ft.add(new FactoryThread(name));
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
	private	int	num;
	private	int	min;
	private	int	max;
	private	ArrayList<SupplierThread> st;
        private CyclicBarrier barrier;
	
	public	Supplier_num_min_max(int n, int mi, int ma)
	{
		num = n;
		min = mi;
		max = ma;
                barrier = new CyclicBarrier(num + 1);
	}
	public int getmin()     { return min; }
	public int getmax()     { return max; }
        public int getNum()     { return num; }
        public CyclicBarrier getBarrier()   { return barrier; }
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
			st.add(new SupplierThread(name, min, max, barrier));
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