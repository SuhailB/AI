import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Math;


class State
{
	char[] board;
	
	State()
	{
        board = new char[9];
        for (int a = 0; a < 9; a++) 
        {
			board[a] = (char)(a+(int)'0');
		}
	}
	State(State copy)
	{
		board = new char[9];
		for(int i=0; i<9; i++)
		{
			this.board[i] = copy.board[i];
		}
	}
	char checkWinner() {
		for (int a = 0; a < 8; a++) {
			StringBuilder line = new StringBuilder();
			switch (a) {
			case 0:
                line.append(board[0]);
                line.append(board[1]);
                line.append(board[2]);
				break;
			case 1:
                line.append(board[3]);
                line.append(board[4]);
                line.append(board[5]);
				break;
            case 2:
                line.append(board[6]);
                line.append(board[7]);
                line.append(board[8]);
				break;
            case 3:
                line.append(board[0]);
                line.append(board[3]);
                line.append(board[6]);
				break;
            case 4:
                line.append(board[1]);
                line.append(board[4]);
                line.append(board[7]);
				break;
            case 5:
                line.append(board[2]);
                line.append(board[5]);
                line.append(board[8]);
				break;
            case 6:
                line.append(board[0]);
                line.append(board[4]);
                line.append(board[8]);
				break;
            case 7:
                line.append(board[2]);
                line.append(board[4]);
                line.append(board[6]);
				break;
            }
            String str = line.toString();
			if (str.equals("XXX")) {
				return 'X';
			} else if (str.equals("OOO")) {
				return 'O';
			}
        }
        boolean flag = true;
        for(int i=0; i<9; i++)
        {
            if(getAvailableSlots()[i]) flag = false;
        }
        if(flag) return '=';
        return '-';
	}
	
	void populateEmptyBoard() {
		// for (int a = 0; a < 9; a++) {
		// 	board[a] = String.valueOf(a+1);
		// }
		board[0]='O';
		board[1]='1';
		board[2]='X';
		board[3]='X';
		board[4]='4';
		board[5]='X';
		board[6]='6';
		board[7]='O';
		board[8]='O';
	}
	
	void printBoard() {

		System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
		System.out.println("|---+---+---|");
		System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
		System.out.println("|---+---+---|");
		System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");

	}
	
	boolean[] getAvailableSlots()
	{
		boolean [] AvailableSlot = new boolean[9];
		for(int i=0; i<9; i++)
		{
			if(board[i]!='X'&&board[i]!='O')
			{
				AvailableSlot[i]=true;
			}
		
		}
		return AvailableSlot;
	}
}

class Algorithm
{
    // static public int MM (State state, char player, int[] move) // X AI maximizer
    // {
    //     // char human = 0;
    //     // char computer = 0;
    //     // if(player == 'X')
    //     // {
    //     //     human = 'O';
    //     //     computer = 'X';
    //     // }
    //     // else if(player == 'O')
    //     // {
    //     //     human = 'X';
    //     //     computer = 'O';
    //     // }
    //     if      (state.checkWinner()=='X') { return 10 ;}
    //     else if (state.checkWinner()=='O') { return -10;}
    //     else if (state.checkWinner()=='=') { return 0  ;}
        
    //     if(player == 'X')
    //     {
    //         int score = -1000000;
    //         for(int i=0; i<9; i++)
    //         {
    //             if(state.getAvailableSlots()[i]==true)
    //             {
    //                 state.board[i] = 'X';
    //                 int tmp = Math.max(score, MM(state,'O',move));
    //                 System.out.println(tmp);
    //                 if(tmp>score)
    //                 {
    //                     score = tmp;
    //                     move[0] = i;
    //                 }
    //                 //undo the move
    //                 state.board[i]= (char)(i + (int)'0');
    //             }
    //         }
    //         return score;
    //     }
    //     else if(player == 'O')
    //     {
    //         int score = 1000000;
    //         for(int i=0; i<9; i++)
    //         {
    //             if(state.getAvailableSlots()[i]==true)
    //             {
    //                 state.board[i] = 'O';
    //                 int tmp = Math.min(score, MM(state,'X',move));
    //                 System.out.println(tmp);
    //                 if(tmp<score)
    //                 {
    //                     score = tmp;
    //                     move[0] = i;
    //                 }
    //                 //undo the move
    //                 state.board[i]= (char)(i + (int)'0');
    //             }
    //         }
    //         return score;

    //     }
    //     return move[0];
    // }
    static public int MiniMax(State state, char player) // X
    {
        int move=-1;
        if(player == 'O')  //O is minimizer
        {
            int score = 1000000;
            for(int i=0; i<9; i++)
            {
                if(state.getAvailableSlots()[i]==true)
                {
                    state.board[i] = 'O';
                    int tmp = maxSearch(state);
                    System.out.println("tmp"+tmp);
                    if(tmp<score)
                    {
                        score = tmp;
                        move = i;
                    }
                    state.board[i]= (char)(i + (int)'0');
                }
            }
            System.out.println(score);
        }
        else if(player == 'X')  //X is maximizer
        {
            int score = -1000000;
            for(int i=0; i<9; i++)
            {
                if(state.getAvailableSlots()[i]==true)
                {
                    state.board[i] = 'X';
                    int tmp = minSearch(state);
                    System.out.println(tmp);
                    if(tmp>score)
                    {
                        score = tmp;
                        move = i;
                    }
                    state.board[i]= (char)(i + (int)'0');
                }
            }
            System.out.println(score);
        }
        
        return move;
    }
    static int maxSearch(State state) 
    {
        if (state.checkWinner()=='X') { return 10; }
        else if (state.checkWinner()=='O') { return -10; }
        else if (state.checkWinner()=='=') { return 0; }

        int score = -1000000;
        for(int i=0; i<9; i++)
        {
                if (state.getAvailableSlots()[i]==true)
                {
                    state.board[i] = 'X';
                    score = Math.max(score, minSearch(state));
                    state.board[i] = (char)(i + (int)'0');
                }
            
        }

        return score;
    }

    static int minSearch(State state)
    {
        if (state.checkWinner()=='X') { return 10; }
        else if (state.checkWinner()=='O') { return -10; }
        else if (state.checkWinner()=='=') { return 0; }

        int score = 1000000;
        for(int i=0; i<9; i++)
        {
                if (state.getAvailableSlots()[i]==true)
                {
                    state.board[i]= 'O';
                    score = Math.min(score, maxSearch(state));
                    state.board[i] = (char)(i + (int)'0');
                }
        }

        return score;
    }
}

public class Game {
	
	public static void main(String[] args) {
        State state = new State();
        play(state);
    }

    static void getHumanMove(State state, char player)
    {
        boolean fail = true;
        int move = -1;

        do
        {
            try 
			{
                Scanner input = new Scanner(System.in);
                move = input.nextInt();
               
				if (!(move >= 0 && move <= 8)||state.getAvailableSlots()[move]==false) 
				{
					System.out.print("Invalid input; re-enter slot number: ");
					continue;
                }
                fail = false;
			} 
			catch (InputMismatchException e) 
			{
				System.out.print("Invalid input; re-enter slot number: ");
				continue;
			}
           

        } while (fail);
        
        state.board[move] = player;
    }

    static void play(State state)
    {
        int turn = 0;
        boolean exit = false;
        // state.populateEmptyBoard();
        // Algorithm.MiniMax(state, 'O');
        // Algorithm.MiniMax(state, 'X');
        //System.out.println(Algorithm.MiniMax(state, 'O'));
        System.out.print("Choose 'X' or 'O to represent you: ");
        char player=0, computer=0;
        try 
			{
                Scanner input = new Scanner(System.in);
                player = input.next().charAt(0);
               
                while(player != 'X' && player != 'O')
                {
                    System.out.print("Invalid input; Enter 'X' or 'O': ");
                    player = input.next().charAt(0);
                }
                if(player == 'O') computer = 'X';
                else if(player == 'X') computer = 'O';

			} 
			catch (InputMismatchException e) 
			{
				System.out.print("Invalid input; Enter 'X' or 'O': ");
			}
          
            state.printBoard();
       
        do
        {
            System.out.println();
            // human move
            if (turn == 0)
            {
                System.out.print("Your turn: ");
                getHumanMove(state, player);

                if (state.checkWinner()==player)
                {
                    System.out.println("Human Wins");
                    exit = true;
                }
            }
            else 
            {
               
                
                int aimove = Algorithm.MiniMax(state,computer);
                
                state.board[aimove]= computer;

                if (state.checkWinner()== computer)
                {
                    System.out.println("Computer Wins");
                    exit = true;
                }
            }
            if (state.checkWinner()=='=')
            {
                System.out.println("---------Tie---------");
                exit = true;
            }

            turn ^= 1;
            state.printBoard();
           


        } while (!exit);
    }
}