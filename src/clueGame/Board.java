package clueGame;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Board {
	private int numRows;
	private int numColumns;
	public static final int MAX_ROWS = 50;
	private BoardCell[][] board;
	private Map<Character, String> rooms;
	private Map<Character, String> roomType;
	private Map<BoardCell, Set<BoardCell>> adjMatrix;
	private String boardConfigFile;
	private String roomConfigFile;
	private Set<BoardCell> targets;
	private Set<BoardCell> visited;

	// variable used for singleton pattern
	private static Board theInstance = new Board();
	// ctor is private to ensure only one can be created
	private Board() {}
	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}

	public void initialize() {
		board = new BoardCell[numRows][numColumns];
		for (int i = 0; i<numRows; i++ ){
			for(int j = 0; j<numColumns; j++){
				board[i][j] = new BoardCell(i,j);
			}
		}
		try{
			loadRoomConfig();
			loadBoardConfig();
		} catch (FileNotFoundException e){ //if the files cant be found
			System.out.println("Can't open file");
		}
		catch (BadConfigFormatException e){ //if the files are not in the right format
			System.out.println(e.getMessage());
		}
		calcAdjacencies();
	}

	//this function loads the files used to set up the game
	public void setConfigFiles(String boardConfigFile, String roomConfigFile){
		this.boardConfigFile = boardConfigFile;
		this.roomConfigFile = roomConfigFile;
	}
	//This function sets up the room configurations
	public void loadRoomConfig() throws FileNotFoundException, BadConfigFormatException{
		rooms = new HashMap<Character, String>(); //map with room initial and room name
		roomType = new HashMap<Character, String>();//map with room initial and room type
		FileReader reader = new FileReader(roomConfigFile); //to read in room config file
		Scanner in = new Scanner(reader);
		while(in.hasNextLine()){
			String line = in.nextLine();
			Character letter = line.charAt(0);// room initial is first letter of line in file
			int first = line.indexOf(','); //to get the index of the first comma
			int last = line.lastIndexOf(','); //get index of last comma
			String room = line.substring(first+2, last); //room name is inbetween the two comma indexes
			String type = line.substring(last+2, line.length()); //room type is after last comma
			if(!type.equalsIgnoreCase("Card")){ //to throw a badConfigException if the type is not "card" or "other"
				if(!type.equalsIgnoreCase("Other")){
					throw new BadConfigFormatException(roomConfigFile, 3);//the integer is to know what error message to output
				}
			}
			rooms.put(letter, room); //creates room name map
			roomType.put(letter, type);//creates room type map
		}
	}
	//this function sets up the board configuration
	public void loadBoardConfig() throws FileNotFoundException, BadConfigFormatException{
		//opens file to read board configuration
		FileReader reader = new FileReader(boardConfigFile);
		Scanner in = new Scanner(reader);
		String line = "";
		String firstLine = in.nextLine(); //gets first line of csv file
		String[] letters = firstLine.split(","); //creates array of string of all the values inbetween the commas
		numColumns = letters.length;//sets the number of columns equal to length of above array
		for(String s: letters){ //loops thru array of strings
			Character initial = s.charAt(0); //room initial is the first character in the string (Strings should only be length 1 or 2 if there is a door)
			if(!rooms.containsKey(initial)){ //if the room initial map doesnt contain the initial throw a BadConfigFormatException
				throw new BadConfigFormatException(boardConfigFile, 2);
			}
			String typeTemp = roomType.get(initial);  //gets the room type of every initial in the first row
			if(typeTemp.equalsIgnoreCase("other")){ //if the room type is other, change room type to walkway... to know what symbol is for walkways
				roomType.remove(initial);
				roomType.put(initial, "Walkway");
			}
		}

		board = new BoardCell[MAX_ROWS][numColumns]; //set up board array
		for(int j = 0; j < letters.length; j++){ //for every column in the first row
			Character initial = letters[j].charAt(0);
			board[0][j] = new BoardCell(0,j); //create new boardCell
			board[0][j].setInitial(initial); //sets initial for boardCell
			board[0][j].setType(roomType.get(initial)); //sets room type for cell
			if (letters[j].length()>1 && letters[j].charAt(1)!= 'N'){ //if the length of the string is 2 and the 2nd letter is R, L, U, or D, the cell is a door
				board[0][j].setDoor(true);
				char doorDir = letters[j].charAt(1); //sets door direction to second letter in string (L,U,R,D)
				if(doorDir == 'U'){
					board[0][j].setDoorDirection(DoorDirection.UP);
				}
				else if(doorDir == 'L'){
					board[0][j].setDoorDirection(DoorDirection.LEFT);
				}
				else if(doorDir == 'R'){
					board[0][j].setDoorDirection(DoorDirection.RIGHT);
				}
				if(doorDir == 'D'){
					board[0][j].setDoorDirection(DoorDirection.DOWN);
				}
			}
			else{
				board[0][j].setDoorDirection(DoorDirection.NONE); //otherwise, no door = doorDir is NONE
			}
		}
		int i = 1; //to loop through rows in csv file. Start at 1 b/c already read in first line
		while(in.hasNextLine()){ //The following code does the same thing that was done to the first line in the csv file
			line = in.nextLine();
			String[] letters2 = line.split(",");
			int tempCols = letters2.length;
			if(tempCols != numColumns){ //if there is a different number of columns in the specific row... bad format
				throw new BadConfigFormatException(boardConfigFile, 1);
			}
			for(int j = 0; j < letters2.length; j++){
				char initial = letters2[j].charAt(0);
				if(!rooms.containsKey(initial)){
					throw new BadConfigFormatException(boardConfigFile, 2);
				}
				board[i][j] = new BoardCell(i,j);
				board[i][j].setInitial(initial);
				board[i][j].setType(roomType.get(initial));
				if (letters2[j].length()>1 && letters2[j].charAt(1)!= 'N'){
					board[i][j].setDoor(true);
					char doorDir = letters2[j].charAt(1);
					if(doorDir == 'U'){
						board[i][j].setDoorDirection(DoorDirection.UP);
					}
					else if(doorDir == 'L'){
						board[i][j].setDoorDirection(DoorDirection.LEFT);
					}
					else if(doorDir == 'R'){
						board[i][j].setDoorDirection(DoorDirection.RIGHT);
					}
					if(doorDir == 'D'){
						board[i][j].setDoorDirection(DoorDirection.DOWN);
					}
				}
				else{
					board[i][j].setDoorDirection(DoorDirection.NONE);
				}
			}
			i++; //next row
		}
		numRows = i;
	}

	public void calcAdjacencies(){
		adjMatrix = new HashMap<BoardCell, Set<BoardCell>>();
		for (int i = 0; i<numRows; i++ ){
			for(int j = 0; j<numColumns; j++){
				Set<BoardCell> adjList = new HashSet<BoardCell>();
				char in = board[i][j].getInitial();
				String ty = roomType.get(in);
				switch(ty){
				case "Walkway":
				case "walkway":
					//check up
					if(i != 0){
						int rowUp = i - 1;
						char up = board[rowUp][j].getInitial();
						String typeUp = roomType.get(up);
						if(typeUp.equalsIgnoreCase("walkway")){
							adjList.add(board[rowUp][j]);
						}
						else if(board[rowUp][j].isDoorway() && board[rowUp][j].getDoorDirection() == DoorDirection.DOWN){
							adjList.add(board[rowUp][j]);
						}
					}
					//check left
					if(j != 0){
						int colLeft = j - 1;
						char left = board[i][colLeft].getInitial();
						String typeLeft = roomType.get(left);
						if(typeLeft.equalsIgnoreCase("walkway")){
							adjList.add(board[i][colLeft]);
						}
						else if(board[i][colLeft].isDoorway() && board[i][colLeft].getDoorDirection() == DoorDirection.RIGHT){
							adjList.add(board[i][colLeft]);
						}
					}
					//check down
					if(i != numRows-1){
						int rowDown = i + 1;
						char down = board[rowDown][j].getInitial();
						String typeDown = roomType.get(down);
						if(typeDown.equalsIgnoreCase("walkway")){
							adjList.add(board[rowDown][j]);
						}
						else if(board[rowDown][j].isDoorway() && board[rowDown][j].getDoorDirection() == DoorDirection.UP){
							adjList.add(board[rowDown][j]);
						}
					}

					//check right
					if(j != numColumns-1){
						int colRight = j + 1;
						char right = board[i][colRight].getInitial();
						String typeRight = roomType.get(right);
						if(typeRight.equalsIgnoreCase("walkway")){
							adjList.add(board[i][colRight]);
						}
						else if(board[i][colRight].isDoorway() && board[i][colRight].getDoorDirection() == DoorDirection.LEFT){
							adjList.add(board[i][colRight]);
						}
					}
					break;
				default:
					if(board[i][j].isDoorway()){
						DoorDirection d =  board[i][j].getDoorDirection();
						switch(d){
						case UP:
							adjList.add(board[i-1][j]);
							break;
						case RIGHT:
							adjList.add(board[i][j+1]);
							break;
						case DOWN:
							adjList.add(board[i+1][j]);
							break;
						case LEFT:
							adjList.add(board[i][j-1]);
							break;
						}
					}
					break;					
				}
				adjMatrix.put(board[i][j], adjList);
			}
		}
	}

	public Map<Character, String> getLegend(){
		return rooms;
	}
	public int getNumRows() {
		return numRows;
	}
	public int getNumColumns() {
		return numColumns;
	}

	public BoardCell getCellAt(int row, int col){
		return board[row][col];
	}
	public Set<BoardCell> getTargets() {
		return targets;
	}
	public void calcTargets(int i, int j, int pathLength){
		visited = new HashSet<BoardCell>();
		targets = new HashSet<BoardCell>();
		visited.add(board[i][j]);
		findAllTargets(board[i][j], pathLength);
	}

	public void findAllTargets(BoardCell thisCell, int numSteps){
		Set<BoardCell> cells = new HashSet<BoardCell>();
		cells = adjMatrix.get(thisCell);
		for(BoardCell c: cells){
			if(visited.contains(c)){
			}
			else{
				visited.add(c);
				if(numSteps == 1){
					targets.add(c);
				}
				else if(c.isDoorway()){
					targets.add(c);
				}
				else{
					findAllTargets(c, numSteps - 1);
				}
				visited.remove(c);
			}
		}
	}
	public Set<BoardCell> getAdjList(int i, int j) {
		return adjMatrix.get(board[i][j]);
	}


}
