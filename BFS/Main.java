import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;
import java.io.File;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.Color;
import java.util.ArrayList;
import java.util.*;


class State
{
	State prev;
	byte[] state;
	
	State()
	{
		state = new byte[22];
	}
	State(State copy)
	{
		this.state = new byte[22];
		this.prev = null;
		for(int i=0; i<22; i++)
		{
		this.state[i] = copy.state[i];
		}
	}
	State(byte[] b)
	{
		this.state = new byte[22];
	
		for(int i=0; i<22; i++)
		{
		this.state[i] = b[i];
		}
		//drawGrid();
	}
	public void setState(State copy)
	{
		for(int i=0; i<22; i++)
		{
		this.state[i] = copy.state[i];
		}
	}
	
	void printState()
	{
		for(int i = 0; i < 11; i++)
		System.out.print("(" + state[2 * i] + "," +
		state[2 * i + 1] + ") ");
		System.out.println();
	}
	
	boolean IsEqual(State rhs)
	{
		boolean result = true;
		for(int i=0; i<22; i++)
		{
			if(this.state[i] != rhs.state[i])
			result = false;
		}
		return result;
	}
}


class StateComparator implements Comparator<State>
{
	public int compare(State a, State b)
	{
		for(int i = 0; i < 22; i++)
		{
			if(a.state[i] < b.state[i])
				return -1;
			else if(a.state[i] > b.state[i])
				return 1;
		}
		return 0;
	}
}  

public class Main extends JFrame
{
	int BLOCK = 0;
	State st;
	State [] states;
	byte [][] valid;
	
	public Main() throws Exception
	{
		StateComparator comp = new StateComparator();
		
		st = new State();
		states = new State[115];
		valid = new byte[10][10];
		byte [] b = {4,-2,0,0,0,0,0,0,0,0,0,0,1,-3,-2,0,0,-4,-3,-1,-2,0};
		
		
		State goal = new State(b);
		State com = new State();
		
		
		com = BFS(st, goal);
		
		Stack<State> stack = new Stack<State>();
		
		while(com.prev!=null)
		{
			stack.push(com);
			com = com.prev;
		}
		
		stack.push(com);
		int stackSize = stack.size();
		for(int i=0; i<stackSize; i++)
		{
			State tmp = stack.pop();
			states[i] = tmp;
			//states[i].printState();
		}
		
		View view = new View(this);
		view.addMouseListener(view);
		this.setTitle("Puzzle");
		this.setSize(482, 505);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	
		int j=0;
		while(true)
		{
			try
			{
				Thread.sleep(100);
			} 
			catch(Exception e) 
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			while(view.flag&&j<stackSize)
			{
				
				view.update(j);
			
				// Go to sleep for mi 10 liseconds
				try
				{
					Thread.sleep(100);
				} 
				catch(Exception e) 
				{
					e.printStackTrace();
					System.exit(1);
				}
				j++;
				
			}
			
			j = 0;
			view.flag = false;
		}
		
	}
	
	public void b(int x, int y)
	{
		valid[x][y]++;
	}
	public void shape(byte[] state, int id, int x1, int y1, int x2, int y2, int x3, int y3)
	{
		b(state[2 * id] + x1, state[2 * id + 1] + y1);
		b(state[2 * id] + x2, state[2 * id + 1] + y2);
		b(state[2 * id] + x3, state[2 * id + 1] + y3);
	}

	// Draw a 4-block piece
	public void shape(byte[] state,int id, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4)
	{
		shape(state,id, x1, y1, x2, y2, x3, y3);
		b(state[2 * id] + x4, state[2 * id + 1] + y4);
	}
	
	public void drawGrid(State s)
	{
		for(int i=0; i<10;i++)
		{
			for(int j=0; j<10; j++)
			valid[j][i] = 0;
		}
		for(int i = 0; i < 10; i++) { b(i, 0); b(i, 9); }
		for(int i = 1; i < 9; i++) { b(0, i); b(9, i); }
		b(1, 1); b(1, 2); b(2, 1);
		b(7, 1); b(8, 1); b(8, 2);
		b(1, 7); b(1, 8); b(2, 8);
		b(8, 7); b(7, 8); b(8, 8);
		b(3, 4); b(4, 4); b(4, 3);
		// Draw the pieces
		shape(s.state, 0, 1, 3, 2, 3, 1, 4, 2, 4);
		shape(s.state, 1, 1, 5, 1, 6, 2, 6);
		shape(s.state, 2, 2, 5, 3, 5, 3, 6);
		shape(s.state, 3, 3, 7, 3, 8, 4, 8);
		shape(s.state, 4, 4, 7, 5, 7, 5, 8);
		shape(s.state, 5, 6, 7, 7, 7, 6, 8);
		shape(s.state, 6, 5, 4, 5, 5, 5, 6, 4, 5);
		shape(s.state, 7, 6, 4, 6, 5, 6, 6, 7, 5);
		shape(s.state, 8, 8, 5, 8, 6, 7, 6);
		shape(s.state, 9, 6, 2, 6, 3, 5, 3);
		shape(s.state, 10, 5, 1, 6, 1, 5, 2);
	}
	
	public boolean IsValidState()
	{
		boolean result = true;
		for(int i=0; i<10;i++)
		{
		for(int j=0; j<10; j++)
		if(valid[j][i] >= 2) result = false;
		}
		return result;
	}
	
	public void printGrid()
	{
		for(int i=0; i<10;i++)
		{
			for(int j=0; j<10; j++)
			{
				System.out.print(valid[j][i]+" ");
			}
			System.out.println();
		}
	}
	
	
	public boolean stateCompare(State a, State b, int i)
	{
		if(a.state[2*i] == b.state[2*i] && a.state[2*i+1] == b.state[2*i+1]) return true;
		return false;
	}
	public State BFS(State s, State g)
	{
		StateComparator comp = new StateComparator();
		ArrayList<State> NextStates;
		Queue<State> q = new LinkedList<State>();
		TreeSet<State> set = new TreeSet<State>(comp);
		State current = s;
		
		q.add(current);
		set.add(current);
		while(!q.isEmpty())
		{
			current = q.remove();
			
			NextStates = checkNextState(current);
			
			if(stateCompare(current,g,BLOCK)) return current;
			
			for(int i = 0; i<NextStates.size(); i++)
			{
				State child = NextStates.get(i);
				
				if(!set.contains(child))
			   {
				set.add(child);
				q.add(child);
			   }
			}
			
			
		}
		return null;

    }
	
	public ArrayList<State> checkNextState(State s)
	{
		
		ArrayList<State> arr = new ArrayList<State>();
		
		State temp = new State(s);
		
		//move pieces right
		for(int i=0; i<22;i+=2)
		{
			s.state[i]++;
			drawGrid(s);
				
			if(IsValidState())
			{
				State copy = new State(s);
				copy.prev = s;
				//copy.visited = false;
				arr.add(copy);
			}
			
			s.setState(temp); //reset the state
			
			
		}
		
		//move pieces left
		for(int i=0; i<22;i+=2)
		{
			s.state[i]--;
			drawGrid(s);
				
			if(IsValidState())
			{
				State copy = new State(s);
				copy.prev = s;
				
				arr.add(copy);	
			}
			
			s.setState(temp);
		}
		
		//move pieces down
		for(int i=0; i<22;i+=2)
		{
			s.state[i+1]++;
			drawGrid(s);
				
			if(IsValidState())
			{
				State copy = new State(s);
				copy.prev = s;
				
				//copy.visited = false;
				arr.add(copy);	
			}
			
			s.setState(temp);
		}
		
		//move pieces up
		for(int i=0; i<22;i+=2)
		{
			s.state[i+1]--;
			drawGrid(s);
				
			if(IsValidState())
			{
				State copy = new State(s);
				copy.prev = s;
				
				//copy.visited = false;
				arr.add(copy);	
			}
			
			s.setState(temp);
		}
		
		return arr;
	}
	
	public static void main(String[] args) throws Exception
	{
		new Main();
	}
}

class View extends JPanel implements MouseListener {
	Main viz;
	Random rand;
	byte[] state;
	Graphics graphics;
	int size;
	State[] states;
	boolean flag;
	View(Main v) throws IOException
	{
		viz = v;
		rand = new Random(0);
		state = new byte[22];
		size = 48;
		states = v.states;
	}

	
	public void update(int index)
	{
		state = states[index].state;
		for(int i = 0; i < 11; i++)
		System.out.print("(" + state[2 * i] + "," +
			state[2 * i + 1] + ") ");
		System.out.println();
		viz.repaint();
	}
	public void mousePressed(MouseEvent e){  flag = true;  }
	public void mouseReleased(MouseEvent e) {    }
	public void mouseEntered(MouseEvent e) {    }
	public void mouseExited(MouseEvent e) {    }
	public void mouseClicked(MouseEvent e) {    }

	// Draw a block
	public void b(int x, int y)
	{
		graphics.fillRect(size * x, size * y, size, size);
	}

	// Draw a 3-block piece
	public void shape(int id, int red, int green, int blue,
		int x1, int y1, int x2, int y2, int x3, int y3)
	{
		graphics.setColor(new Color(red, green, blue));
		b(state[2 * id] + x1, state[2 * id + 1] + y1);
		b(state[2 * id] + x2, state[2 * id + 1] + y2);
		b(state[2 * id] + x3, state[2 * id + 1] + y3);
	}

	// Draw a 4-block piece
	public void shape(int id, int red, int green, int blue,
		int x1, int y1, int x2, int y2,
		int x3, int y3, int x4, int y4)
	{
		shape(id, red, green, blue, x1, y1, x2, y2, x3, y3);
		b(state[2 * id] + x4, state[2 * id + 1] + y4);
	}

	public void paintComponent(Graphics g)
	{
		// Draw the black squares
		graphics = g;
		g.setColor(new Color(0, 0, 0));
		for(int i = 0; i < 10; i++) { b(i, 0); b(i, 9); }
		for(int i = 1; i < 9; i++) { b(0, i); b(9, i); }
		b(1, 1); b(1, 2); b(2, 1);
		b(7, 1); b(8, 1); b(8, 2);
		b(1, 7); b(1, 8); b(2, 8);
		b(8, 7); b(7, 8); b(8, 8);
		b(3, 4); b(4, 4); b(4, 3);

		// Draw the pieces
		shape(0, 255, 0, 0, 1, 3, 2, 3, 1, 4, 2, 4);
		shape(1, 0, 255, 0, 1, 5, 1, 6, 2, 6);
		shape(2, 128, 128, 255, 2, 5, 3, 5, 3, 6);
		shape(3, 255, 128, 128, 3, 7, 3, 8, 4, 8);
		shape(4, 255, 255, 128, 4, 7, 5, 7, 5, 8);
		shape(5, 128, 128, 0, 6, 7, 7, 7, 6, 8);
		shape(6, 0, 128, 128, 5, 4, 5, 5, 5, 6, 4, 5);
		shape(7, 0, 128, 0, 6, 4, 6, 5, 6, 6, 7, 5);
		shape(8, 0, 255, 255, 8, 5, 8, 6, 7, 6);
		shape(9, 0, 0, 255, 6, 2, 6, 3, 5, 3);
		shape(10, 255, 128, 0, 5, 1, 6, 1, 5, 2);
	}
}

	// //initialize the state
		// for(int i = 0; i < 10; i++) { b(i, 0); b(i, 9); }
		// for(int i = 1; i < 9; i++) { b(0, i); b(9, i); }
		// b(1, 1); b(1, 2); b(2, 1);
		// b(7, 1); b(8, 1); b(8, 2);
		// b(1, 7); b(1, 8); b(2, 8);
		// b(8, 7); b(7, 8); b(8, 8);
		// b(3, 4); b(4, 4); b(4, 3);
		// // Draw the pieces
		// shape(0, 1, 3, 2, 3, 1, 4, 2, 4);
		// shape(1, 1, 5, 1, 6, 2, 6);
		// shape(2, 2, 5, 3, 5, 3, 6);
		// shape(3, 3, 7, 3, 8, 4, 8);
		// shape(4, 4, 7, 5, 7, 5, 8);
		// shape(5, 6, 7, 7, 7, 6, 8);
		// shape(6, 5, 4, 5, 5, 5, 6, 4, 5);
		// shape(7, 6, 4, 6, 5, 6, 6, 7, 5);
		// shape(8, 8, 5, 8, 6, 7, 6);
		// shape(9, 6, 2, 6, 3, 5, 3);
		// shape(10, 5, 1, 6, 1, 5, 2);
