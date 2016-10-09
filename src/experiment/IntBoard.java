package experiment;

import java.util.*;

import clueGame.BoardCell;

public class IntBoard {
	private Map<BoardCell, Set<BoardCell>> adjMtx;
	private Set<BoardCell> visited;
	private Set<BoardCell> targets;
	private BoardCell[][] grid;
	public static final int ROWS = 4;
	public static final int COLS = 4; 
	public IntBoard() {
		super();
		adjMtx = new HashMap<BoardCell, Set<BoardCell>>();
		grid = new BoardCell[ROWS][COLS];
		for (int i = 0; i<ROWS; i++ ){
			for(int j = 0; j<COLS; j++){
				grid[i][j] = new BoardCell(i,j);
				// should it be row, col in th second part above or i,j?
			}
		}
		calcAdjacencies();
	}

	public void calcAdjacencies(){

		for (int i = 0; i<ROWS; i++ ){
			for(int j = 0; j<COLS; j++){
				Set<BoardCell> adjList = new HashSet<BoardCell>();
				if(i > 0){
					adjList.add(grid[i-1][j]);
				}
				if(i < 3){
					adjList.add(grid[i+1][j]);
				}
				if(j > 0){
					adjList.add(grid[i][j-1]);
				}
				if(j < 3){
					adjList.add(grid[i][j+1]);
				}
				adjMtx.put(grid[i][j], adjList);
			}
		}
	}
	public void calcTargets(BoardCell startCell, int pathLength){
		visited = new HashSet<BoardCell>();
		targets = new HashSet<BoardCell>();
		visited.add(startCell);
		findAllTargets(startCell, pathLength);
	}

	public void findAllTargets(BoardCell thisCell, int numSteps){
		for(BoardCell c: adjMtx.get(thisCell)){
			if(visited.contains(c)){
			}
			else{
				visited.add(c);
				if(numSteps == 1){
					targets.add(c);
				}
				
				else{
					findAllTargets(c, numSteps - 1);
				}
				visited.remove(c);
			}
		}
	}
	public Set<BoardCell> getTargets(){
		return targets;
	}
	public BoardCell getCell(int i, int j){
		return grid[i][j];
	}

	public Set<BoardCell> getAdjList(BoardCell cell) {
		return adjMtx.get(cell);
	}
}
