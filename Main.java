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
	public	int	capacity;
	public	int	max;

	public	Freight_num_max(int c, int m)
	{
		capacity = c;
		max = m;
	}
}
class Factory_num_max
{
	public	int	capacity;
	public	int	max;

	public	Factory_num_max(int c, int m)
	{
		capacity = c;
		max = m;
	}
}
class Supplier_num_min_max
{
	public	int	capacity;
	public	int	min;
	public	int	max;

	public	Supplier_num_min_max(int c, int mi, int ma)
	{
		capacity = c;
		min = mi;
		max = ma;
	}
}
public class Main 
{
	private	int	days;
	private	int	warehouse_num;
	private	Freight_num_max freight_num_max;
	private	Supplier_num_min_max supplier_num_min_max;
	private	Factory_num_max factory_num_max;

	public static void main(String[] args)
	{
		this.input("config_1.txt");
		this.print();
	}    
	public void print()
	{
		System.out.printf("\n%s  >> Day of simulation : %d\n", Thread.currentThread().getName());
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
                        	System.out.println("New file name = ");
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
			warehouse_num = Integer.parseInt(cols[1].trim());
		}
		else if (cols[0].equalsIgnoreCase("freight_num_max"))
		{
			freight_num_max = new Freight_num_max(Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()));
		}
		else if (cols[0].equalsIgnoreCase("supplier_num_min_max"))
		{
			supplier_num_min_max = new Supplier_num_min_max(Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()), Integer.parseInt(cols[3].trim()));
		}
		else if (cols[0].equalsIgnoreCase("factory_num_max"))
		{
			factory_num_max = new Factory_num_max(Integer.parseInt(cols[1].trim()), Integer.parseInt(cols[2].trim()));
		}
	}
}
