import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;

class State
{
	String[] board;
	
	State()
	{
		board = new String[9];
	}
	State(State copy)
	{
		board = new String[9];
		for(int i=0; i<9; i++)
		{
			this.board[i] = copy.board[i];
		}
	}
	String checkWinner() {
		for (int a = 0; a < 8; a++) {
			String line = null;
			switch (a) {
			case 0:
				line = board[0] + board[1] + board[2];
				break;
			case 1:
				line = board[3] + board[4] + board[5];
				break;
			case 2:
				line = board[6] + board[7] + board[8];
				break;
			case 3:
				line = board[0] + board[3] + board[6];
				break;
			case 4:
				line = board[1] + board[4] + board[7];
				break;
			case 5:
				line = board[2] + board[5] + board[8];
				break;
			case 6:
				line = board[0] + board[4] + board[8];
				break;
			case 7:
				line = board[2] + board[4] + board[6];
				break;
			}
			if (line.equals("XXX")) {
				return "X";
			} else if (line.equals("OOO")) {
				return "O";
			}
		}

		for (int a = 0; a < 9; a++) {
			if (Arrays.asList(board).contains(String.valueOf(a+1))) {
				break;
			}
			else if (a == 8) return "draw";
		}

		//System.out.println(player + "'s player; enter a slot number to place " + player + " in:");
		return null;
	}
	
	void populateEmptyBoard() {
		// for (int a = 0; a < 9; a++) {
		// 	board[a] = String.valueOf(a+1);
		// }
		board[0]="O";
		board[1]="2";
		board[2]="X";
		board[3]="X";
		board[4]="5";
		board[5]="X";
		board[6]="7";
		board[7]="O";
		board[8]="O";
	}
	
	void printBoard() {
	//	System.out.println("/---|---|---\\");
		System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
		System.out.println("|---+---+---|");
		System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
		System.out.println("|---+---+---|");
		System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
	//	System.out.println("/---|---|---\\");
	}
	
	int Score()
	{
		int score = 0;
		if		(checkWinner()=="X") score = 1;
		else if (checkWinner()=="O") score = -1;
		return score;
	}
	
	boolean isGameOver()
	{
		if(checkWinner() == null) return false;
		else return true;
	}
	
	ArrayList<State> getNextStates(String p)
	{
		ArrayList<State> nextStates = new ArrayList<State>();
		
		if(p=="X")
		{
			for(int i=0; i<9; i++)
			{
				State tmpState = new State(this);
				if(getAvailableSlots()[i]==true)
				{
					tmpState.board[i] = "X";
					nextStates.add(tmpState);
				}
			}
		}
		else if(p=="O")
		{
			for(int i=0; i<9; i++)
			{
				State tmpState = new State(this);
				if(getAvailableSlots()[i]==true)
				{
					tmpState.board[i] = "O";
					nextStates.add(tmpState);
				}
			}
		}
		else System.out.println("failed");
		return nextStates;
	}
	
	boolean[] getAvailableSlots()
	{
		boolean [] AvailableSlot = new boolean[9];
		for(int i=0; i<9; i++)
		{
			if(board[i]!="X"&&board[i]!="O")
			{
				AvailableSlot[i]=true;
			}
			//System.out.println(AvailableSlot[i]);
		}
		return AvailableSlot;
	}
}

class Algorithm
{
	static public State MinMax(State state, String player)
	{
		boolean [] availSpots = state.getAvailableSlots();
		if(state.isGameOver())
		{
			return state.Score();
		}
		if(player == "X")
		{
			ArrayList<State> moves = state.getNextStates("X");
			int scores[] = new int[moves.size()];
			for(int i=0; i<moves.size(); i++)
			{
				scores[i] = MinMax(moves.get(i), "O");
			}
			getMaxValue(scores);
			return 
		}
		else if(player == "O")
		{
			ArrayList<State> moves = state.getNextStates("O");
			int scores[] = new int[moves.size()];
			for(int i=0; i<moves.size(); i++)
			{
				scores[i] = MinMax(moves.get(i), "X");
			}
			return getMinValue(scores);
		}
		return 0;
	}
	static int getMaxValue(int[] numbers){
		int maxValue = numbers[0];
		for(int i=1;i < numbers.length;i++){
			if(numbers[i] > maxValue){
				maxValue = numbers[i];
				index = i;
			}
		}
		return maxValue;
	}
	static int getMinValue(int[] numbers){
		int minValue = numbers[0];
		for(int i=1;i<numbers.length;i++){
			if(numbers[i] < minValue){
				minValue = numbers[i];
			}
		}
		return minValue;
	}
}
public class Main {
	static Scanner in;
	
	static String player;

	public static void main(String[] args) {
		State state = new State();
		in = new Scanner(System.in);
		String player = "X";
		String winner = null;
		state.populateEmptyBoard();

		System.out.println("Welcome to 2 Player Tic Tac Toe.");
		System.out.println("--------------------------------");
		state.printBoard();
		//System.out.println("X's will play first. Enter a slot number to place X in:");
		//System.out.println(Algorithm.MinMax(state,"X"));
		Algorithm.MinMax(state,"X");
		// while (winner == null) {
			// state.getAvailableSlots();
			// int numInput;
			// try 
			// {
				// numInput = in.nextInt();
				// if (!(numInput > 0 && numInput <= 9)) 
				// {
					// System.out.println("Invalid input; re-enter slot number:");
					// continue;
				// }
			// } 
			// catch (InputMismatchException e) 
			// {
				// System.out.println("Invalid input; re-enter slot number:");
				// continue;
			// }
			// if (state.board[numInput-1].equals(String.valueOf(numInput))) 
			// {
				// state.board[numInput-1] = state.player;
				// if (state.player.equals("X")) 
				// {
					// state.player = "O";
				// } 
				// else 
				// {
					// state.player = "X";
				// }
				// state.printBoard();
				// winner = state.checkWinner();
			// } 
			// else
			// {
				// System.out.println("Slot already taken; re-enter slot number:");
				// continue;
			// }
		// }
		// ArrayList<State> s = state.getNextStates();
		// for(int i=0; i<s.size(); i++)
		// {
			// s.get(i).printBoard();
			// System.out.println();
		// }
		
		// state.printBoard();
		// if (winner.equalsIgnoreCase("draw")) 
		// {
			// System.out.println("It's a draw! Thanks for playing.");
		// } 
		// else 
		// {
			// System.out.println("Congratulations! " + winner + "'s have won! Thanks for playing.");
		// }
		
	}

	

	

	
}