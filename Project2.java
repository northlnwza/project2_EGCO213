package Project2_6713118;

/**
 *
 * @author Thanakrit Jomhong 6713118
 */
import java.util.*;
import java.io.*;
import java.util.concurrent.*;

class SupplierThread extends Thread
{
	public SupplierThread(String n)
	{
		super(n);
	}
}
class FactoryThread extends Thread
{
	public FactoryThread(String n)
	{
		super(n);
	}
}
class Warehouse
{
	private	String	name;
	public Warehouse(String n)
	{
		name = n;
	}
	public String getName()
	{
		return (name);
	}
}

class Freight
{
	private String name;
	public Freight(String n)
	{
		name = n;
	}
	public String getName()
	{
		return (name);
	}
}
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
	public int getmax()
	{
		return (max);
	}
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
	private	int	capacity;
	private	int	min;
	private	int	max;
	public	ArrayList<SupplierThread> st;
	
	public	Supplier_num_min_max(int c, int mi, int ma)
	{
		capacity = c;
		min = mi;
		max = ma;
	}
	public int getmin()
	{
		return (min);
	}
	public int getmax()
	{
		return (max);
	}
	public void buildthread()
	{
		String	name;
		int	i;

		i = 0;
		st = new ArrayList<>();
		while (i < capacity)
		{
			name = String.format("SupplierThread_%d", i);
			st.add(new SupplierThread(name));
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
	public ArrayList<Warehouse> wh;

	public Warehouse_num(int n)
	{
		num = n;
	}
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
		obj.print();
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
					this.input2(line);
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
	public	void input2(String line)
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
			factory_num_max.buildthread();
		}
	}
}
