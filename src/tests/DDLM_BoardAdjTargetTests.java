package tests;

/*
 * This program tests that adjacencies and targets are calculated correctly.
 */

import java.util.Set;

//Doing a static import allows me to write assertEquals rather than
//assertEquals
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class DDLM_BoardAdjTargetTests {
	// We make the Board static because we can load it one time and 
	// then do all the tests. 
	private static Board board;
	@BeforeClass
	public static void setUp() {
		// Board is singleton, get the only instance and initialize it		
		board = Board.getInstance();
		board.setConfigFiles("boardLayout.csv", "layout.txt");		
		board.initialize();
	}

	// Ensure that player does not move around within room
	// These cells are RED on the planning spreadsheet
	@Test
	public void testAdjacenciesInsideRooms()
	{
		// Test a corner
		Set<BoardCell> testList = board.getAdjList(5, 19);
		assertEquals(0, testList.size());
		// Test one that has walkway underneath, corner of room 
		testList = board.getAdjList(5, 24);
		assertEquals(0, testList.size());
		// Test middle of the room
		testList = board.getAdjList(9, 22);
		assertEquals(0, testList.size());
		//has walkway above
		testList = board.getAdjList(11, 3);
		assertEquals(0, testList.size());
		// walkway on the left
		testList = board.getAdjList(19, 9);
		assertEquals(0, testList.size());
		// test with door above, walkway left and below
		testList = board.getAdjList(18,19);
		assertEquals(0, testList.size());
	}

	// Ensure that the adjacency list from a doorway is only the
	// walkway. NOTE: This test could be merged with door 
	// direction test. 
	// These tests are WHITE on the planning spreadsheet
	@Test
	public void testAdjacencyRoomExit()
	{
		// TEST DOORWAY RIGHT 
		Set<BoardCell> testList = board.getAdjList(3, 16);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCellAt(3, 17)));
		// TEST DOORWAY LEFT 
		testList = board.getAdjList(9, 19);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCellAt(9, 18)));
		//TEST DOORWAY DOWN
		testList = board.getAdjList(14, 3);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCellAt(15, 3)));
		//TEST DOORWAY UP
		testList = board.getAdjList(15, 13);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCellAt(14, 13)));
		
	}
	
	// Test adjacency at entrance to rooms
	// These tests are YELLOW in planning spreadsheet
	@Test
	public void testAdjacencyDoorways()
	{
		// Test beside a door direction RIGHT
		Set<BoardCell> testList = board.getAdjList(7, 6);
		assertTrue(testList.contains(board.getCellAt(7, 5)));
		assertTrue(testList.contains(board.getCellAt(7, 7)));
		assertTrue(testList.contains(board.getCellAt(8, 6)));
		assertTrue(testList.contains(board.getCellAt(6, 6)));
		assertEquals(4, testList.size());
		// Test beside a door direction DOWN
		testList = board.getAdjList(7, 11);
		assertTrue(testList.contains(board.getCellAt(6, 11)));
		assertTrue(testList.contains(board.getCellAt(7, 12)));
		assertTrue(testList.contains(board.getCellAt(7, 10)));
		assertEquals(3, testList.size());
		// Test beside a door direction LEFT
		testList = board.getAdjList(17, 18);
		assertTrue(testList.contains(board.getCellAt(17, 19)));
		assertTrue(testList.contains(board.getCellAt(17, 17)));
		assertTrue(testList.contains(board.getCellAt(16, 18)));
		assertTrue(testList.contains(board.getCellAt(18, 18)));
		assertEquals(4, testList.size());
		// Test beside a door direction UP
		testList = board.getAdjList(14, 14);
		assertTrue(testList.contains(board.getCellAt(14, 15)));
		assertTrue(testList.contains(board.getCellAt(14, 13)));
		assertTrue(testList.contains(board.getCellAt(13, 14)));
		assertTrue(testList.contains(board.getCellAt(15, 14)));
		assertEquals(4, testList.size());
	}

	// Test a variety of walkway scenarios
	// These tests are GREY on the planning spreadsheet
	@Test
	public void testAdjacencyWalkways()
	{
		// Test on right edge of board, just one walkway piece
		Set<BoardCell> testList = board.getAdjList(6,24);
		assertTrue(testList.contains(board.getCellAt(6,23)));
		assertEquals(1, testList.size());
		
		// Test on top edge of board, two walkway pieces
		testList = board.getAdjList(0,8);
		assertTrue(testList.contains(board.getCellAt(0,7)));
		assertTrue(testList.contains(board.getCellAt(1,8)));
		assertEquals(2, testList.size());

		// Test between two rooms, walkways right and left
		testList = board.getAdjList(10,2);
		assertTrue(testList.contains(board.getCellAt(10,1)));
		assertTrue(testList.contains(board.getCellAt(10,3)));
		assertEquals(2, testList.size());

		// Test surrounded by 4 walkways
		testList = board.getAdjList(15,7);
		assertTrue(testList.contains(board.getCellAt(15,6)));
		assertTrue(testList.contains(board.getCellAt(15, 8)));
		assertTrue(testList.contains(board.getCellAt(14, 7)));
		assertTrue(testList.contains(board.getCellAt(16, 7)));
		assertEquals(4, testList.size());
		
		// Test on left edge, two walkway pieces
		testList = board.getAdjList(5,0);
		assertTrue(testList.contains(board.getCellAt(4,0)));
		assertTrue(testList.contains(board.getCellAt(5,1)));
		assertEquals(2, testList.size());
		
		// Test on room corner, two walkways available
		testList = board.getAdjList(19,19);
		assertTrue(testList.contains(board.getCellAt(19,18)));
		assertTrue(testList.contains(board.getCellAt(20,19)));
		assertEquals(2, testList.size());

		// Test on walkway next to  door that is not in the needed
		// direction to enter
		testList = board.getAdjList(2,6);
		assertTrue(testList.contains(board.getCellAt(2,7)));
		assertTrue(testList.contains(board.getCellAt(1,6)));
		assertEquals(2, testList.size());
	}
		
	// Tests of just walkways, 1 step, includes on edge of board
	// and beside room
	// Have already tested adjacency lists on all four edges, will
	// only test two edges here
	// These are NEON GREEN on the planning spreadsheet
	@Test
	public void testTargetsOneStep() {
		board.calcTargets(23, 18, 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCellAt(23, 17)));
		assertTrue(targets.contains(board.getCellAt(22, 18)));	
		
		board.calcTargets(15, 0, 1);
		targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCellAt(15, 1)));
		assertTrue(targets.contains(board.getCellAt(16, 0)));				
	}
	
	// Tests of just walkways, 2 steps
	// These are NEON GREEN on the planning spreadsheet
	@Test
	public void testTargetsTwoSteps() {
		board.calcTargets(23, 18, 2);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCellAt(22, 17)));
		assertTrue(targets.contains(board.getCellAt(22, 19)));
		assertTrue(targets.contains(board.getCellAt(21, 18)));
		
		board.calcTargets(15, 0, 2);
		targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCellAt(15, 2)));
		assertTrue(targets.contains(board.getCellAt(16, 1)));				
	}
	
	// Tests of just walkways, 4 steps
	// These are NEON GREEN on the planning spreadsheet
	@Test
	public void testTargetsFourSteps() {
		board.calcTargets(23, 18, 4);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCellAt(20, 19)));
		assertTrue(targets.contains(board.getCellAt(21, 18)));
		assertTrue(targets.contains(board.getCellAt(20, 17)));
		assertTrue(targets.contains(board.getCellAt(19, 18)));
		assertTrue(targets.contains(board.getCellAt(22, 19)));
		assertTrue(targets.contains(board.getCellAt(22, 17)));
		
		// Includes a path that doesn't have enough length
		board.calcTargets(15, 0, 4);
		targets= board.getTargets();
		assertEquals(5, targets.size());
		assertTrue(targets.contains(board.getCellAt(15, 4)));
		assertTrue(targets.contains(board.getCellAt(16, 3)));	
		assertTrue(targets.contains(board.getCellAt(15, 2)));	
		assertTrue(targets.contains(board.getCellAt(16, 1)));	
		assertTrue(targets.contains(board.getCellAt(14, 3)));
	}	
	
	// Tests of just walkways plus one door, 6 steps
	// These are NEON GREEN on the planning spreadsheet

	@Test
	public void testTargetsSixSteps() {
		board.calcTargets(23,18, 6);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCellAt(18, 17)));
		assertTrue(targets.contains(board.getCellAt(17, 18)));	
		assertTrue(targets.contains(board.getCellAt(21,18)));		
		assertTrue(targets.contains(board.getCellAt(22, 19)));	
		assertTrue(targets.contains(board.getCellAt(20, 19)));	
		assertTrue(targets.contains(board.getCellAt(22, 17)));
		assertTrue(targets.contains(board.getCellAt(19,18)));
		assertTrue(targets.contains(board.getCellAt(20,17)));
	}	
	
	// Test getting into a room
	// These are NEON GREEN on the planning spreadsheet

	@Test 
	public void testTargetsIntoRoom()
	{
		// One room is exactly 2 away
		board.calcTargets(20,7, 2);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(7, targets.size());
		// directly left (can't go right 2 steps)
		assertTrue(targets.contains(board.getCellAt(20,5)));
		// directly up and down
		assertTrue(targets.contains(board.getCellAt(18,7)));
		assertTrue(targets.contains(board.getCellAt(22,7)));
		// one up/down, one left/right
		assertTrue(targets.contains(board.getCellAt(21,8)));
		assertTrue(targets.contains(board.getCellAt(19,8)));
		assertTrue(targets.contains(board.getCellAt(19,6)));
		assertTrue(targets.contains(board.getCellAt(21,6)));
	}
	
	// Test getting into room, doesn't require all steps
	// These are NEON GREEN on the planning spreadsheet
	@Test
	public void testTargetsIntoRoomShortcut() 
	{
		board.calcTargets(13,21,3);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(10, targets.size());
		// directly right and left
		assertTrue(targets.contains(board.getCellAt(13,24)));
		assertTrue(targets.contains(board.getCellAt(13,18)));
		//into rooms
		assertTrue(targets.contains(board.getCellAt(15, 21)));
		assertTrue(targets.contains(board.getCellAt(15, 22)));
		//right then down
		assertTrue(targets.contains(board.getCellAt(14,23)));
		//right down left
		assertTrue(targets.contains(board.getCellAt(14,21)));
		// down left up
		assertTrue(targets.contains(board.getCellAt(13,20)));
		// right down right
		assertTrue(targets.contains(board.getCellAt(14,23)));		
		// down left left
		assertTrue(targets.contains(board.getCellAt(14,19)));		
		//left left up
		assertTrue(targets.contains(board.getCellAt(12,19)));
		
	}

	// Test getting out of a room
	// These are PINK on the planning spreadsheet
	@Test
	public void testRoomExit()
	{
		// Take one step, essentially just the adj list
		board.calcTargets(7,5, 1);
		Set<BoardCell> targets= board.getTargets();
		// Ensure doesn't exit through the wall
		assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCellAt(7,6)));
		// Take two steps
		board.calcTargets(7,5, 2);
		targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCellAt(7,7)));
		assertTrue(targets.contains(board.getCellAt(6,6)));
		assertTrue(targets.contains(board.getCellAt(8,6)));
	}

}