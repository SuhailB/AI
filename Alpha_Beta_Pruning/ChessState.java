import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
/// Represents the state of a chess game
class ChessState {
	public static final int MAX_PIECE_MOVES = 27;
	public static final int None = 0;
	public static final int Pawn = 1;
	public static final int Rook = 2;
	public static final int Knight = 3;
	public static final int Bishop = 4;
	public static final int Queen = 5;
	public static final int King = 6;
	public static final int PieceMask = 7;
	public static final int WhiteMask = 8;
	public static final int AllMask = 15;
	public static Random rand = new Random();

	int[] m_rows;

	ChessState() {
		m_rows = new int[8];
		resetBoard();
	}

	ChessState(ChessState that) {
		m_rows = new int[8];
		for(int i = 0; i < 8; i++)
			this.m_rows[i] = that.m_rows[i];
	}

	int getPiece(int col, int row) {
		return (m_rows[row] >> (4 * col)) & PieceMask;
	}

	boolean isWhite(int col, int row) {
		return (((m_rows[row] >> (4 * col)) & WhiteMask) > 0 ? true : false);
	}

	/// Sets the piece at location (col, row). If piece is None, then it doesn't
	/// matter what the value of white is.
	void setPiece(int col, int row, int piece, boolean white) {
		m_rows[row] &= (~(AllMask << (4 * col)));
		m_rows[row] |= ((piece | (white ? WhiteMask : 0)) << (4 * col));
	}

	/// Sets up the board for a new game
	void resetBoard() {
		setPiece(0, 0, Rook, true);
		setPiece(1, 0, Knight, true);
		setPiece(2, 0, Bishop, true);
		setPiece(3, 0, Queen, true);
		setPiece(4, 0, King, true);
		setPiece(5, 0, Bishop, true);
		setPiece(6, 0, Knight, true);
		setPiece(7, 0, Rook, true);
		for(int i = 0; i < 8; i++)
			setPiece(i, 1, Pawn, true);
		for(int j = 2; j < 6; j++) {
			for(int i = 0; i < 8; i++)
				setPiece(i, j, None, false);
		}
		for(int i = 0; i < 8; i++)
			setPiece(i, 6, Pawn, false);
		setPiece(0, 7, Rook, false);
		setPiece(1, 7, Knight, false);
		setPiece(2, 7, Bishop, false);
		setPiece(3, 7, Queen, false);
		setPiece(4, 7, King, false);
		setPiece(5, 7, Bishop, false);
		setPiece(6, 7, Knight, false);
		setPiece(7, 7, Rook, false);
	}

	/// Positive means white is favored. Negative means black is favored.
	int heuristic(Random rand)
	{
		int score = 0;
		for(int y = 0; y < 8; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				int p = getPiece(x, y);
				int value;
				switch(p)
				{
					case None: value = 0; break;
					case Pawn: value = 10; break;
					case Rook: value = 63; break;
					case Knight: value = 31; break;
					case Bishop: value = 36; break;
					case Queen: value = 88; break;
					case King: value = 500; break;
					default: throw new RuntimeException("what?");
				}
				if(isWhite(x, y))
					score += value;
				else
					score -= value;
			}
		}
		return score + rand.nextInt(3) - 1;
	}

	/// Returns an iterator that iterates over all possible moves for the specified color
	ChessMoveIterator iterator(boolean white) {
		return new ChessMoveIterator(this, white);
	}

	/// Returns true iff the parameters represent a valid move
	boolean isValidMove(int xSrc, int ySrc, int xDest, int yDest) {
		ArrayList<Integer> possible_moves = moves(xSrc, ySrc);
		for(int i = 0; i < possible_moves.size(); i += 2) {
			if(possible_moves.get(i).intValue() == xDest && possible_moves.get(i + 1).intValue() == yDest)
				return true;
		}
		return false;
	}

	/// Print a representation of the board to the specified stream
	void printBoard(PrintStream stream)
	{
		stream.println("  A  B  C  D  E  F  G  H");
		stream.print(" +");
		for(int i = 0; i < 8; i++)
			stream.print("--+");
		stream.println();
		for(int j = 7; j >= 0; j--) {
			stream.print(Character.toString((char)(49 + j)));
			stream.print("|");
			for(int i = 0; i < 8; i++) {
				int p = getPiece(i, j);
				if(p != None) {
					if(isWhite(i, j))
						stream.print("w");
					else
						stream.print("b");
				}
				switch(p) {
					case None: stream.print("  "); break;
					case Pawn: stream.print("p"); break;
					case Rook: stream.print("r"); break;
					case Knight: stream.print("n"); break;
					case Bishop: stream.print("b"); break;
					case Queen: stream.print("q"); break;
					case King: stream.print("K"); break;
					default: stream.print("?"); break;
				}
				stream.print("|");
			}
			stream.print(Character.toString((char)(49 + j)));
			stream.print("\n +");
			for(int i = 0; i < 8; i++)
				stream.print("--+");
			stream.println();
		}
		stream.println("  A  B  C  D  E  F  G  H");
	}

	/// Pass in the coordinates of a square with a piece on it
	/// and it will return the places that piece can move to.
	ArrayList<Integer> moves(int col, int row) {
		ArrayList<Integer> pOutMoves = new ArrayList<Integer>();
		int p = getPiece(col, row);
		boolean bWhite = isWhite(col, row);
		int nMoves = 0;
		int i, j;
		switch(p) {
			case Pawn:
				if(bWhite) {
					if(!checkPawnMove(pOutMoves, col, inc(row), false, bWhite) && row == 1)
						checkPawnMove(pOutMoves, col, inc(inc(row)), false, bWhite);
					checkPawnMove(pOutMoves, inc(col), inc(row), true, bWhite);
					checkPawnMove(pOutMoves, dec(col), inc(row), true, bWhite);
				}
				else {
					if(!checkPawnMove(pOutMoves, col, dec(row), false, bWhite) && row == 6)
						checkPawnMove(pOutMoves, col, dec(dec(row)), false, bWhite);
					checkPawnMove(pOutMoves, inc(col), dec(row), true, bWhite);
					checkPawnMove(pOutMoves, dec(col), dec(row), true, bWhite);
				}
				break;
			case Bishop:
				for(i = inc(col), j=inc(row); true; i = inc(i), j = inc(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				for(i = dec(col), j=inc(row); true; i = dec(i), j = inc(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				for(i = inc(col), j=dec(row); true; i = inc(i), j = dec(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				for(i = dec(col), j=dec(row); true; i = dec(i), j = dec(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				break;
			case Knight:
				checkMove(pOutMoves, inc(inc(col)), inc(row), bWhite);
				checkMove(pOutMoves, inc(col), inc(inc(row)), bWhite);
				checkMove(pOutMoves, dec(col), inc(inc(row)), bWhite);
				checkMove(pOutMoves, dec(dec(col)), inc(row), bWhite);
				checkMove(pOutMoves, dec(dec(col)), dec(row), bWhite);
				checkMove(pOutMoves, dec(col), dec(dec(row)), bWhite);
				checkMove(pOutMoves, inc(col), dec(dec(row)), bWhite);
				checkMove(pOutMoves, inc(inc(col)), dec(row), bWhite);
				break;
			case Rook:
				for(i = inc(col); true; i = inc(i))
					if(checkMove(pOutMoves, i, row, bWhite))
						break;
				for(i = dec(col); true; i = dec(i))
					if(checkMove(pOutMoves, i, row, bWhite))
						break;
				for(j = inc(row); true; j = inc(j))
					if(checkMove(pOutMoves, col, j, bWhite))
						break;
				for(j = dec(row); true; j = dec(j))
					if(checkMove(pOutMoves, col, j, bWhite))
						break;
				break;
			case Queen:
				for(i = inc(col); true; i = inc(i))
					if(checkMove(pOutMoves, i, row, bWhite))
						break;
				for(i = dec(col); true; i = dec(i))
					if(checkMove(pOutMoves, i, row, bWhite))
						break;
				for(j = inc(row); true; j = inc(j))
					if(checkMove(pOutMoves, col, j, bWhite))
						break;
				for(j = dec(row); true; j = dec(j))
					if(checkMove(pOutMoves, col, j, bWhite))
						break;
				for(i = inc(col), j=inc(row); true; i = inc(i), j = inc(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				for(i = dec(col), j=inc(row); true; i = dec(i), j = inc(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				for(i = inc(col), j=dec(row); true; i = inc(i), j = dec(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				for(i = dec(col), j=dec(row); true; i = dec(i), j = dec(j))
					if(checkMove(pOutMoves, i, j, bWhite))
						break;
				break;
			case King:
				checkMove(pOutMoves, inc(col), row, bWhite);
				checkMove(pOutMoves, inc(col), inc(row), bWhite);
				checkMove(pOutMoves, col, inc(row), bWhite);
				checkMove(pOutMoves, dec(col), inc(row), bWhite);
				checkMove(pOutMoves, dec(col), row, bWhite);
				checkMove(pOutMoves, dec(col), dec(row), bWhite);
				checkMove(pOutMoves, col, dec(row), bWhite);
				checkMove(pOutMoves, inc(col), dec(row), bWhite);
				break;
			default:
				break;
		}
		return pOutMoves;
	}

	/// Moves the piece from (xSrc, ySrc) to (xDest, yDest). If this move
	/// gets a pawn across the board, it becomes a queen. If this move
	/// takes a king, then it will remove all pieces of the same color as
	/// the king that was taken and return true to indicate that the move
	/// ended the game.
	boolean move(int xSrc, int ySrc, int xDest, int yDest) {
		if(isValidMove(xSrc, ySrc, xDest, yDest))
		{
			if(xSrc < 0 || xSrc >= 8 || ySrc < 0 || ySrc >= 8)
				throw new RuntimeException("out of range");
			if(xDest < 0 || xDest >= 8 || yDest < 0 || yDest >= 8)
				throw new RuntimeException("out of range");
			int target = getPiece(xDest, yDest);
			int p = getPiece(xSrc, ySrc);
			if(p == None)
				throw new RuntimeException("There is no piece in the source location");
			if(target != None && isWhite(xSrc, ySrc) == isWhite(xDest, yDest))
				throw new RuntimeException("It is illegal to take your own piece");
			if(p == Pawn && (yDest == 0 || yDest == 7))
				p = Queen; // a pawn that crosses the board becomes a queen
			boolean white = isWhite(xSrc, ySrc);
			setPiece(xDest, yDest, p, white);
			setPiece(xSrc, ySrc, None, true);
			if(target == King) {
				// If you take the opponent's king, remove all of the opponent's pieces. This
				// makes sure that look-ahead strategies don't try to look beyond the end of
				// the game (example: sacrifice a king for a king and some other piece.)
				int x, y;
				for(y = 0; y < 8; y++) {
					for(x = 0; x < 8; x++) {
						if(getPiece(x, y) != None) {
							if(isWhite(x, y) != white) {
								setPiece(x, y, None, true);
							}
						}
					}
				}
				return true;
			}
			return false;
		}
		else
		{
			System.out.println("Invalid Move...");
			return false;
		}
	}

	static int inc(int pos) {
		if(pos < 0 || pos >= 7)
			return -1;
		return pos + 1;
	}

	static int dec(int pos) {
		if(pos < 1)
			return -1;
		return pos -1;
	}

	boolean checkMove(ArrayList<Integer> pOutMoves, int col, int row, boolean bWhite) {
		if(col < 0 || row < 0)
			return true;
		int p = getPiece(col, row);
		if(p > 0 && isWhite(col, row) == bWhite)
			return true;
		pOutMoves.add(col);
		pOutMoves.add(row);
		return (p > 0);
	}

	boolean checkPawnMove(ArrayList<Integer> pOutMoves, int col, int row, boolean bDiagonal, boolean bWhite) {
		if(col < 0 || row < 0)
			return true;
		int p = getPiece(col, row);
		if(bDiagonal) {
			if(p == None || isWhite(col, row) == bWhite)
				return true;
		}
		else {
			if(p > 0)
				return true;
		}
		pOutMoves.add(col);
		pOutMoves.add(row);
		return (p > 0);
	}

	boolean isTerminal()
	{
		ChessMoveIterator WhiteItr = this.iterator(true);
		ChessMoveIterator BlackItr = this.iterator(false);
		if(!WhiteItr.hasNext()||!BlackItr.hasNext()) return true;
		else return false;
	}
	/// Represents a possible  move
	static class ChessMove {
		int xSource;
		int ySource;
		int xDest;
		int yDest;
	}

	/// Iterates through all the possible moves for the specified color.
	static class ChessMoveIterator
	{
		int x, y;
		ArrayList<Integer> moves;
		ChessState state;
		boolean white;

		/// Constructs a move iterator
		ChessMoveIterator(ChessState curState, boolean whiteMoves) {
			x = -1;
			y = 0;
			moves = null;
			state = curState;
			white = whiteMoves;
			advance();
		}

		private void advance() {
			if(moves != null && moves.size() >= 2) {
				moves.remove(moves.size() - 1);
				moves.remove(moves.size() - 1);
			}
			while(y < 8 && (moves == null || moves.size() < 2)) {
				if(++x >= 8) {
					x = 0;
					y++;
				}
				if(y < 8) {
					if(state.getPiece(x, y) != ChessState.None && state.isWhite(x, y) == white)
						moves = state.moves(x, y);
					else
						moves = null;
				}
			}
		}

		/// Returns true iff there is another move to visit
		boolean hasNext() {
			return (moves != null && moves.size() >= 2);
		}

		/// Returns the next move
		ChessState.ChessMove next() {
			ChessState.ChessMove m = new ChessState.ChessMove();
			m.xSource = x;
			m.ySource = y;
			m.xDest = moves.get(moves.size() - 2);
			m.yDest = moves.get(moves.size() - 1);
			advance();
			return m;
		}
	}

	static class Algorithm
	{
		
		static public ChessMove MiniMax(final ChessState state, int depth, boolean maximizer) //white is maximizer
		{	
			ChessMove bestMove = null;
			ChessMove move = null;
			if(!maximizer)
			{
				int score = 1000000;
				ChessMoveIterator itr = state.iterator(false); //iterator over the black pieces
				while(itr.hasNext())
				{
					ChessState copy = new ChessState(state);
					move = itr.next();
					copy.move(move.xSource, move.ySource, move.xDest, move.yDest);
					
					int tmp = maxSearch(copy, depth, -1000000, 1000000);
					if(tmp<score)
					{
						score = tmp;
						bestMove = move;
					}

				}
			}
			else if(maximizer)
			{
				int score = -1000000;
				ChessMoveIterator itr = state.iterator(true);//iterator over the white pieces
				while(itr.hasNext())
				{
					ChessState copy = new ChessState(state);
					move = itr.next();
					copy.move(move.xSource, move.ySource, move.xDest, move.yDest);

					int tmp = minSearch(copy, depth, -1000000, 1000000);
					if(tmp>score)
					{
						score = tmp;
						bestMove = move;
					}
				}
			}
			return bestMove;
		}
		
		static int maxSearch(ChessState state, int depth, int alpha, int beta) 
		{
			if(depth == 0 || state.isTerminal()) return state.heuristic(rand);

			int score = -1000000;
			ChessMoveIterator itr = state.iterator(true); //white possible moves
			ChessMove move = null;

			while(itr.hasNext())
			{
				ChessState copy = new ChessState(state);
				move = itr.next();
				copy.move(move.xSource, move.ySource, move.xDest, move.yDest);
				score = Math.max(score, minSearch(copy, depth - 1, alpha, beta));
				alpha = Math.max(alpha, score);
				if(alpha >= beta) break;
			}

			return score;
		}

		static int minSearch(ChessState state, int depth, int alpha, int beta)
		{
			if(depth == 0 || state.isTerminal()) return state.heuristic(rand);

			int score = 1000000;
			ChessMoveIterator itr = state.iterator(false); //black
			ChessMove move = null;

			while(itr.hasNext())
			{
				ChessState copy = new ChessState(state);
				move = itr.next();
				copy.move(move.xSource, move.ySource, move.xDest, move.yDest);
				score = Math.min(score, maxSearch(copy, depth - 1, alpha, beta));
				beta = Math.min(beta, score);
				if(alpha >= beta) break;
				
			}

			return score;
		}
	}

	public static void main(String[] args) {
		int whiteDepth = Integer.parseInt(args[0]);
		int blackDepth = Integer.parseInt(args[1]);
		ChessState s = new ChessState();
        s.resetBoard();
        Scanner input = new Scanner(System.in);
        int turn = 1;
		String in;

        while(true)
        {
			s.printBoard(System.out);
			
            if(turn == 1) //white
            {
				if(whiteDepth==0) //if human
				{
					ChessMove move = new ChessMove();
					System.out.print("Light Turn: ");
					in = input.next();

					if(in.equals("q")) break;

					move.xSource = (int)in.charAt(0) - (int)'a';
					move.ySource = (int)in.charAt(1) - (int)'1';
					move.xDest   = (int)in.charAt(2) - (int)'a';
					move.yDest   = (int)in.charAt(3) - (int)'1';

					boolean isWhite = s.isWhite(move.xSource, move.ySource);
					
					if(isWhite)
					{
						s.move(move.xSource, move.ySource, move.xDest, move.yDest);
						if(s.isTerminal())
						{
							s.printBoard(System.out);
							System.out.println("Light wins!");
							break;
						}
						turn ^= 1;
					}
					else System.out.println("Invalid Move...");
				}
				else
				{
					ChessMove move = ChessState.Algorithm.MiniMax(s, whiteDepth, true);
					if(s.move(move.xSource, move.ySource, move.xDest, move.yDest))
					{
						s.printBoard(System.out);
						System.out.println("Light wins!");
						break;
					}
					turn ^= 1;
				}
            }
            else if(turn == 0) //black
            {
				if(blackDepth==0) //if human
				{
					System.out.print("Dark Turn: ");
					in = input.next();

					if(in.equals("q")) break;

					ChessMove move = new ChessMove();
					move.xSource = (int)in.charAt(0) - (int)'a';
					move.ySource = (int)in.charAt(1) - (int)'1';
					move.xDest   = (int)in.charAt(2) - (int)'a';
					move.yDest   = (int)in.charAt(3) - (int)'1';

					boolean isWhite = s.isWhite(move.xSource, move.ySource);
					
					if(!isWhite)
					{
						s.move(move.xSource, move.ySource, move.xDest, move.yDest);
						if(s.isTerminal())
						{
							s.printBoard(System.out);
							System.out.println("Dark wins!");
							break;
						}
						turn ^= 1;
					}
					else System.out.println("Invalid Move...");
				}
				else
				{
					ChessMove move = ChessState.Algorithm.MiniMax(s, blackDepth, false);
					if(s.move(move.xSource, move.ySource, move.xDest, move.yDest))
					{
						s.printBoard(System.out);
						System.out.println("Dark wins!");
						break;
					}
					turn ^= 1;
				}
            }

			
		}
		

	}
}
