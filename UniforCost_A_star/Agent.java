import java.util.*;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;


class Agent {
    
    
    MyPlanner MP;
	static ArrayList<MyState> states;
    static int goalX = 100;
    static int goalY = 100;
	static boolean Mode;
   
	Agent(Model m)
	{
		
		
	}
	
	void drawPlan(Graphics g, Model m) {
	    
        g.setColor(Color.red);
        
        if(states.size()>1)
        {
            for(int i=0; i<states.size()-1;i++)
            {
                g.drawLine((int)states.get(i).x, (int)states.get(i).y, (int)states.get(i+1).x, (int)states.get(i+1).y);
            }
        }
        g.setColor(Color.yellow);
        
		while(!MP.frontier.isEmpty())
        {
         
			MyState current = MP.frontier.remove();
		
            g.fillOval((int) (current.x/10)*10, (int) (current.y/10)*10, (int) 10, (int) 10);
        }
       
	}

	void update(Model m, Controller c) {
	    
		MP = new MyPlanner(m);
	    MouseEvent e = c.nextMouseEvent();
	   
		
	    if(e!=null)
	    {
			if(e.getButton() == MouseEvent.BUTTON1) Mode = true;
			if(e.getButton() == MouseEvent.BUTTON3) Mode = false;
	        goalX = e.getX();
            goalY = e.getY();
	    }
	
		
		MyState s = new MyState(0.0, null);
		s.x = (int) m.getX();
		s.y = (int) m.getY();
		
	    MyState g = new MyState(0.0, null);
	    
	    g.x = goalX;
	    g.y = goalY;
		MyState result = null;
		if(Mode)  result = MP.UCS(s,g);
		if(!Mode) result = MP.AStar(s,g);
	
		
		Stack<MyState> stack = new Stack<MyState>();
		states = new ArrayList<MyState>();
		while(result.parent!=null)
		{
			stack.push(result);
			result = result.parent;
		}
		
		stack.push(result);
		int stackSize = stack.size();
		for(int i=0; i<stackSize; i++)
		{
			MyState tmp = stack.pop();
			states.add(tmp);
		}
	   
	    if(stackSize>1)
	    m.setDestination(states.get(1).x, states.get(1).y);

		
	}

    
	public static void main(String[] args) throws Exception
	{
	    Controller.playGame();
		
	}
}

class MyState {
    public double cost;
	public double h;
    MyState parent;
    int x,y;
	
    
    MyState(double cost, MyState par) {
        this.parent = par;
        this.cost = cost;
    }
    
    MyState(MyState copy)
    {
        this.x = copy.x;
        this.y = copy.y;
    }
    
    void printState()
    {
        System.out.println("("+x+","+y+") Cost: "+ cost);
    }
	
	boolean isEqual(MyState rhs)
	{
		if(Math.abs(this.x-rhs.x)<10 && Math.abs(this.y-rhs.y)<10) return true;
		else return false;
	}
}

class MyPlanner 
{
    Model model;
    PriorityQueue<MyState> frontier;
	TreeSet<MyState> beenthere;
	
    MyPlanner(Model m)
    {
        model = m; 
    }
    
	MyState UCS (MyState startState, MyState goalState) 
    {
    
        StateComparator SC = new StateComparator();
        CostComparator CC = new CostComparator();
        ArrayList<MyState> NextStates;
        
        frontier = new PriorityQueue<MyState>(CC);
        beenthere = new TreeSet<MyState>(SC);
        startState.cost = 0.0;
        startState.parent = null;
        beenthere.add(startState);
        frontier.add(startState);
        
        while(!frontier.isEmpty()) 
        {
            MyState s = frontier.poll(); // get lowest-cost state
            
            NextStates = getNextStates(s,goalState);
            
            if(s.isEqual(goalState))
            {
                return s;
            }
            for(int i = 0; i<NextStates.size(); i++)
		    {
		        MyState child = NextStates.get(i);//transition(s, a); // compute the next state
                double actionCost = child.cost;//action_cost(s, a); // compute the cost of the action
				
                if(beenthere.contains(child)) 
                {
                    MyState oldchild = beenthere.floor(child);
                    if(s.cost + actionCost < oldchild.cost) 
                    {
                        oldchild.cost = s.cost + actionCost;
                        oldchild.parent = s;
                    }
                }
                else 
                {
                  child.cost = s.cost + actionCost;
				  
                  child.parent = s;
                  frontier.add(child);
                  beenthere.add(child);
                }
		    }
		    
        }
     
        return null;
    }
   
	MyState AStar (MyState startState, MyState goalState) 
    {
    
        StateComparator SC = new StateComparator();
        AStarComparator AC = new AStarComparator();
        ArrayList<MyState> NextStates;
        
        frontier = new PriorityQueue<MyState>(AC);
        beenthere = new TreeSet<MyState>(SC);
        startState.cost = 0.0;
		startState.h = 0;
		
        startState.parent = null;
        beenthere.add(startState);
        frontier.add(startState);
        
        while(!frontier.isEmpty()) 
        {
            MyState parent = frontier.poll(); // get lowest_cost state
            
            NextStates = getNextStates(parent,goalState);
            
            if(parent.isEqual(goalState))
            {
                return parent;
            }
			
            for(int i = 0; i<NextStates.size(); i++)
		    {
				
		        MyState child = NextStates.get(i);
				
                double actionCost = child.cost;//action_cost(s, a); // compute the cost of the action
				
                if(beenthere.contains(child)) 
                {
                    MyState oldchild = beenthere.floor(child);
                    if(parent.cost + actionCost + child.h < oldchild.cost + oldchild.h) 
                    {
                        oldchild.cost = parent.cost + actionCost;
						oldchild.h    = child.h;
                        oldchild.parent = parent;
                    }
                }
                else 
                {
                  child.cost = parent.cost + actionCost;
                  child.parent = parent;
                  frontier.add(child);
                  beenthere.add(child);
                }
		    }
		    
        }
     
        return null;
    }
	
	double getH(MyState s, MyState g)
	{
		return (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
	}
	
    ArrayList<MyState> getNextStates(MyState s, MyState g)
    {
		ArrayList<MyState> NextStates = new ArrayList<MyState>();
		int tmpx, tmpy;
		tmpx = s.x;
		tmpy = s.y;
		MyState copy;
		
		s.x+=10;
		s.y+=-10;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = (10*Math.sqrt(2))/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
		}
		s.x = tmpx;
		s.y = tmpy;
		
        
        //start a new move
		
		s.x +=10;
		s.y +=0;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = 10/model.getTravelSpeed(s.x, s.y);
			
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			//System.out.println("cost: "+copy.cost+"heuristic: "+10/model.LOWEST_COST_SQUARE);
			NextStates.add(copy);
		}
		s.x = tmpx;
		s.y = tmpy;
        
		
        //start a new move
		
		s.x +=10;
		s.y +=10;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = (10*Math.sqrt(2))/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
		}
		s.x = tmpx;
		s.y = tmpy;
        
		
        //start a new move
		s.x +=0;
		s.y +=10;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = 10/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
		}
		s.x = tmpx;
		s.y = tmpy;
        
		
        //start a new move
        s.x +=0;
        s.y +=-10;
        if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = 10/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
        }
        s.x = tmpx;
        s.y = tmpy;
        
        //start a new move
        s.x +=-10;
        s.y +=-10;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = (10*Math.sqrt(2))/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
		}
		s.x = tmpx;
        s.y = tmpy;
        
        //start a new move
        s.x +=-10;
        s.y +=0;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = 10/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
		}
        s.x = tmpx;
        s.y = tmpy;
        
        //start a new move
        s.x +=-10;
        s.y +=10;
		if(s.x > 0 && s.y > 0 && s.x < 1200 && s.y < 600)
        {
			copy = new MyState(s);
			copy.cost = (10*Math.sqrt(2))/model.getTravelSpeed(s.x, s.y);
			copy.h = (1/model.LOWEST_COST_SQUARE)*Math.sqrt(((s.x - g.x)*(s.x - g.x)) + ((s.y - g.y)*(s.y - g.y)));
			NextStates.add(copy);
		}
        s.x = tmpx;
        s.y = tmpy;
      
        return NextStates;
        
    }
    
   
    
}

class StateComparator implements Comparator<MyState>
{
    public int compare(MyState a, MyState b)
	{
			if(a.x < b.x)
				return -1;
			else if(a.x > b.x)
				return 1;
				
			if(a.y < b.y)
				return -1;
			else if(a.y > b.y)
				return 1;
		
		return 0;
	}
}

class CostComparator implements Comparator<MyState>
{
 public int compare(MyState a, MyState b)
	{
			if(a.cost < b.cost)
				return -1;
			else if(a.cost > b.cost)
				return 1;
		return 0;
	}   
}

class AStarComparator implements Comparator<MyState>
{
 public int compare(MyState a, MyState b)
	{
			if((a.cost+a.h) < (b.cost+b.h))
				return -1;
			else if((a.cost+a.h) > (b.cost+b.h))
				return 1;
		return 0;
	}   
}
      
      