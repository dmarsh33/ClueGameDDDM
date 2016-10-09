package clueGame;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class BadConfigFormatException extends Exception{

	public BadConfigFormatException(){
		super("Error: bad formatting, could not load");
	}
	public BadConfigFormatException(String fileName, int num) throws FileNotFoundException {

		super("Error: The file " + fileName + " does not fit the required format");
		PrintWriter out = new PrintWriter("logfile.txt");
		switch(num){
		case 1:
			out.println("The file " + fileName + "does not have the same number of columns per row.");
			System.out.println("The file " + fileName + " does not have the same number of columns per row.");
			break;
		case 2:
			out.println("The file " + fileName + "refers to a room not in the room legend ");
			System.out.println("The file " + fileName + " refers to a room not in the room legend");
			break;
		case 3:
			out.println("The file " + fileName + " does not have the proper format ");
			System.out.println("The file " + fileName + " does not have the proper format ");
		}


		out.close();
	}
}
