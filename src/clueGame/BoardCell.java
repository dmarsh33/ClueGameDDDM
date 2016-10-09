package clueGame;

public class BoardCell {

	private int row;
	private int col;
	private char initial;
	private boolean isDoor;
	private boolean isRoom;
	private boolean isWalk;
	private boolean isCloset;
	private DoorDirection doorDir;
	private String type; //Card, other, walkway
	
	public BoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		isDoor = false;
		isRoom = false;
		isWalk = false;
		isCloset = false;
	}
	//The following function are all setters/getters for board cell characteristics
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}
	public void setType(String type){ //set type booleans for cells
		this.type = type;
		if (this.type.equalsIgnoreCase("card")){
			isRoom = true;
		}
		else if (this.type.equalsIgnoreCase("walkway")){
			isWalk = true;
		}
		else if (this.type.equalsIgnoreCase("closet")){
			isCloset = true;
		}
	}

	public void setDoor(boolean door){
		isDoor = door;
	}
	public void setInitial(Character initial){ //sets room initial
		this.initial = initial;
	}
	public boolean isWalkway(){
		return isWalk;
	}
	public boolean isRoom(){
		return isRoom;
	}
	public boolean isDoorway(){
		return isDoor; 
	}
	public DoorDirection getDoorDirection() {
		return doorDir;
	}
	public void setDoorDirection(DoorDirection d){
		doorDir = d;
	}
	public char getInitial() {
		return initial;
	}
}
