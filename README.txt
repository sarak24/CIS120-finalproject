=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README (UPDATED)
PennKey: sarak24
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays
  The underlying grid structure implements a 2D array. My proposal didn't include what type of
  data would be stored in this array, but after giving it some thought, I decided that I would
  create a new class called "Coord" and each element of the 2D array would be a Coord object 
  (see below for a more detailed description of the Coord class). A 2D array is appropriate to 
  use in this context since Battle Ship is built on a grid structure. Furthermore, this 2D array
  contains all of the information necessary for game play (i.e. for both players), so only one
  "Board" (see class description below) is needed for the entire game.
  

  2. JUnit Testable Component
  As demonstrated by BattleShipTest.java, my methods in BattleShip.java, Board.java, Coord.java,
  Player.java, and Ship.java are JUnit testable. In other words, you can create those objects and
  simulate game play without the actual visuals. In the main method of BattleShip.java, I "run" an 
  entire game and have printGameState() narrate the game play. In BatteShipTest.java I test the
  individual methods of each class by first creating an object of that class and then calling
  methods on them with various inputs. For example, I make sure that when initially setting up
  the board, each Coord is correctly assigned a letter-number pair and when a ship is added, the
  relevant Coord(s) contain that ship in their list of ships. Other features I tested include the
  player's "fire" method, when the game is over (i.e. one player has lost), and when a ship is 
  sunk (i.e. all of the ships coordinates are hit). I also took advantage of the "testability" of 
  my code to check that FileIterator and the BattleShip constructor handled all improper file 
  formatting and I/O issues appropriately.  


  3. Collections or Maps
  My game implements several collections/maps to contain information about the game. First of all,
  RunBattleShip.java keeps a list of UserInputs (see class description below). This list is 
  initially empty and is "grown" every time either player declares a position/orientation for
  a particular Ship. The size of this list will ultimately be equal to the total number of 
  ships on the board (i.e. 2 x #ofgamePieces). The response returned from the subsequent 
  showConfirmDialog box may remove the most recently added UserInput (if the user selects no) or 
  move on to getting the input of the next player(if the user selects yes). Thus, add() and 
  remove() are both implemented, and the use of collections is justified. Additionally, Coord has 
  a linkList of ships that are present at that position on the grid. When ships are added by the 
  players, this list is updated. Each Ship has a list of coordinates that have been sunk. These 
  coordinates are accessed when determining if a ship is sunk because if this list contains all the 
  coordinates that contain the ship, that ship is sunk. The linked lists in BattleShip.java 
  maintain the data from the input file and are used when setting up the game (e.g. creating the 
  game pieces, placing ships on board). Also in BattleShip is a linkedList that collects the 
  strings to be written to the game report. Lastly, every time a player fires, the coordinate they 
  guessed is stored either in their guessedCorrect or guessedWrong. These 2 lists are mainly used 
  to determine what color the square should be when drawn. 
  

  4. File I/O
  A file is read line by line to extract game setup information. The first line is the grid 
  dimension and any subsequent lines are the lengths of the ships (one integer per line). 
  To place their ships on the board, the players input coordinates and orientations in dialog
  boxes that appear at the start of the game. If there's an error (ex. grid size is < 1 or > 10, 
  ship length is < 1 or > 4, incorrect file format), the program will throw and catch the thrown
  exception. Another dialog box will open asking the user to try again (3 more attempts). Likewise
  if the coordinates/orientation that a user inputs are invalid (coordinate is not on grid, 
  the selected orientation makes the ship go off the board), the program will display an error
  message and ask the user to try again. It will continue to ask for a coordinate/orientation 
  until the user inputs a valid one. For the file output, my game writes a turn by turn game 
  report to a file (default is "files/game_report"). This game report describes what happened on
  each turn (ex. p1 hit opponent at A3, p2 unlucky guess).
  

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
* BattleShip.java
	This class sets up the model for the game. It contains the board, the players, who's turn
	it is, the game pieces, all the ships and their sizes, and the number of rounds played. The
	constructor takes a file as an argument and uses a FileLineIterator to read it it. It then
	uses the information it extracts from the file to initialize the ships and board. Most 
	importantly, it contains the play() method, which advances the game and switches whose turn
	it is. This method checks whether the game has been won by calling isGameOver(). 
	The other feature it implements is the file output (the game report) by maintaining
	a list of strings that will be written to the game report file at the end of the game. 
* Board.java
	It's only field is a 2D array of Coord. A board is basically the game board and has the
	methods needed to set up the board. These methods include setting up the coordinates (i.e.
	giving each Coord a letter-number position) and adding ships to the board (i.e. adding ships
	to the list of ships of each appropriate Coord element).
* BottomNumbering.java
	A simple class extending JPanel to number the columns at the bottom of the board.
* Coord.java
	This is the class I created to represent a grid square on the board. It is defined by a 
	letter-number pair, and each Coord object has a list of ships (empty if none have been added).
	Coord bridges the gap between the user input (letter-number pair) and the grid (2D array).
* FileLineIterator.java
	Much like the FileLineIterator used in TwitterBot, this class reads in a file line by line.
	Since BattleShip must read a file line by line to know the dimensions of the board and the
	number of ships and their lengths, a FileLineIterator object is instantiated in the 
	BattleShip constructor. BattleShip keeps adding game pieces until hasNext() returns false.
* MainScreen.java
	The primary class that manages the users' view of the game state, including two grids and
	two labels where instructions/updates are displayed. This class has two inner 
	classes, one for the left board (PlayerBoard) and the right board (OpponentBoard). The
	PlayerBoard shows the current player's own board and marks their ships that have been
	hit in blue. The OpponentBoard shows where the current player has guessed and where they 
	have hit their opponent's ships (yellow if partially sunk, red if completely sunk). 
	Thus, they have different paintComponent() implementations. MainScreen.java contains most of 
	the methods called in RunBattleShip.java for setting up the BattleShip model from the users'
	inputs. Furthermore, it implements a keyListener, so when a player types 'f', it pulls up
	a JOptionPane where user guesses a coordinate. Then, it updates the boards accordingly. 
	It displays a black screen between turns to allow players to pass the computer without
	seeing the other one's board. 
* Player.java
	A class to represent a player (one of two users) in the game. A player has a name and a list
	of guessedCorrect coordinates and guessedWrong coordinates. Most importantly, this class 
	contains the fire() method called in play() in BattleShip.java. There's also a method for
	checking if the player has lost (i.e. all the coordinates that their ships occupy are 
	contained in the sunkCoord list of their respective ships). 
* RunBattleShip.java
	A class that implements Runnable. It creates a JFrame and adds all the Java Swing components
	to it. First, it pulls up a message dialog that displays the instructions of the game. 
	Then, it implements JOptionPane.showInputDialog() to gather user input, including the game
	setup file and the starting coordinate/orientation for each of the players' ships. It stores
	this information in a list of UserInput objects (see below for description of UserInput).
	The one important method in this class is setShipPositions(), which takes a MainScreen object
	as an argument in order to get the grid size and the number of ships.
* Ship.java
	Represents the ships used for game play. A ship has a number ID, owner, length, orientation,
	list of sunk coordinates, and a starting coordinate (upper, leftmost position). A ship can be
	created without a start coordinate or orientation, and then when the user input is collected,
	the start coordinate and orientation are set. Once they are set (not null), they cannot 
	be changed (will throw an IllegalStateException if the set methods are called on a ship
	with a non-null start/orientation). 
* SideLettering.java
	A simple class extending JPanel to letter the rows on the left side of the board.
* UserInput.java
	A UserInput contains the position (String) and orientation (char) that a user has input. This
	class serves as the link between the data the user enters and the BattleShip model. 


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  I found the separation between the model and the view to be very helpful when creating my game.
  It allowed me to first make sure my game was functioning "under the hood" before trying to 
  translate game play into visuals. In this process I realized how much goes into something
  seemingly small or trivial. For example, checking that a ship doesn't go off the board based
  on the user input requires the program to know the start position, orientation, length of the 
  ship, and size of the board. Depending on the orientation, it must make sure the ship doesn't 
  occupy positions that exceed the max number of columns/rows. In general, checking for valid
  input was challenging and forced me to work through a lot of coding logic. The other major 
  road block I encountered was figuring out how to maintain the two board view. I first had one
  class for the PlayerBoard and one for the OpponentBoard but had trouble making them work
  together since they needed to share information. Then, I realized out that a MainScreen class
  with two inner classes would resolve this issue.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
  The classes I've created demonstrate a good separation of functionality. If given a chance, 
  I would try to refactor how I keep track of the mode, which determines which view (player 1's 
  or player 2's) to display. Right now I'm using a sequence of boolean tests, but there might be 
  a way to use dynamic dispatch. Having said that, because there will probably not be any more 
  player views as Battle Ship is a two player game, these if-statements are sufficient. 

  UPDATE: I revisited my getter methods and improved my encapsulation. Now I have getters and 
  that do not allow for modification of the original objects. Instead, they return a copy of
  whatever data is requested. In order to alter the game state, I had to introduce setters and
  "adders" that changed the data only in allowable ways. This was particularly difficult with
  the copies of the ships because different cells of the grid holding the same ship needed
  to both point to one Ship object, which updated based on the game play.
  

========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  For info on JOPtionPane: 
  * https://www.javatpoint.com/java-joptionpane
  * https://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html
  * https://www.youtube.com/watch?v=arcTW_znJYY
  For info on JLabel:
  * https://docs.oracle.com/javase/7/docs/api/javax/swing/JLabel.html
  For info on Components and Graphics:
  * https://docs.oracle.com/javase/7/docs/api/java/awt/Component.html
  * https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html
  General Java Swing information:
  * https://beginnersbook.com/2015/07/java-swing-tutorial/
  How to manually close a JFrame:
  * https://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
  
  
  
