package edu.oregonstate.cs361.battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by michaelhilton on 1/4/17.
 */
public class BattleshipModel {
    private static int MIN = 1;
    private static int MAX = 10;
    private MilitaryShip aircraftCarrier = new MilitaryShip("AircraftCarrier",5, new Coordinate(0,0),new Coordinate(0,0));
    private StealthShip battleship = new StealthShip("Battleship",4, new Coordinate(0,0),new Coordinate(0,0));
    private CivilianShip clipper = new CivilianShip("Clipper",3, new Coordinate(0,0),new Coordinate(0,0));
    private CivilianShip dinghy = new CivilianShip("Dinghy",1, new Coordinate(0,0),new Coordinate(0,0));
    private StealthShip submarine = new StealthShip("Submarine",2, new Coordinate(0,0),new Coordinate(0,0));

    private MilitaryShip computer_aircraftCarrier = new MilitaryShip("Computer_AircraftCarrier",5, new Coordinate(2,2),new Coordinate(2,6));
    private StealthShip computer_battleship = new StealthShip("Computer_Battleship",4, new Coordinate(2,8),new Coordinate(5,8));
    private CivilianShip computer_clipper = new CivilianShip("Computer_Clipper",3, new Coordinate(4,1),new Coordinate(4,3));
    private CivilianShip computer_dinghy = new CivilianShip("Computer_Dinghy",1, new Coordinate(7,3),new Coordinate(7,3));
    private StealthShip computer_submarine = new StealthShip("Computer_Submarine",2, new Coordinate(9,6),new Coordinate(9,7));

    ArrayList<Coordinate> playerHits;
    private ArrayList<Coordinate> playerMisses;
    ArrayList<Coordinate> computerHits;
    private ArrayList<Coordinate> computerMisses;

    boolean scanResult = false;
    private String errorMessage = "none";



    public BattleshipModel() {
        playerHits = new ArrayList<>();
        playerMisses= new ArrayList<>();
        computerHits = new ArrayList<>();
        computerMisses= new ArrayList<>();
    }


    public Ship getShip(String shipName) {
        if (shipName.equalsIgnoreCase("aircraftcarrier")) {
            return aircraftCarrier;
        } if(shipName.equalsIgnoreCase("battleship")) {
            return battleship;
        } if(shipName.equalsIgnoreCase("clipper")) {
            return clipper;
        } if(shipName.equalsIgnoreCase("dinghy")) {
            return dinghy;
        }if(shipName.equalsIgnoreCase("submarine")) {
            return submarine;
        } else {
            return null;
        }
    }

    public BattleshipModel placeShip(String shipName, String row, String col, String orientation) {
        Coordinate start = new Coordinate(Integer.parseInt(row), Integer.parseInt(col));
        Coordinate end;
        Ship ship  = getShip(shipName);

        if(orientation.equals("horizontal")) {
            end = new Coordinate(start.getRow(), start.getCol() + ship.getLength() - 1);
        } else {
            end = new Coordinate(start.getRow() + ship.getLength() - 1, start.getCol());
        }

        ship.setLocation(start, end);

        if(start.getRow() < this.MIN || start.getCol() < this.MIN
            || end.getRow() > this.MAX || end.getCol() > this.MAX || overlapLoop(ship)) {
            this.errorMessage = "Cannot place ship here.";
            ship.removeCoordinates();
            return this;
        }

        return this;
    }

    private boolean overlapLoop(Ship s) {
        return aircraftCarrier.overlapTest(s)
                || battleship.overlapTest(s)
                || clipper.overlapTest(s)
                || dinghy.overlapTest(s)
                || submarine.overlapTest(s);
    }

    public void shootAtComputer(int row, int col) {
        Coordinate shot = new Coordinate(row, col);

        if(!validShotTest(shot))
            errorMessage = "This shot is invalid.";

        if(computer_aircraftCarrier.covers(shot))
            computer_aircraftCarrier.shipHit(shot, computerHits);

        else if (computer_battleship.covers(shot))
            computer_battleship.shipHit(shot, computerHits);

        else if (computer_clipper.covers(shot))
            computer_clipper.shipHit(shot, computerHits);

        else if (computer_dinghy.covers(shot))
            computer_dinghy.shipHit(shot, computerHits);

        else if (computer_submarine.covers(shot))
            computer_submarine.shipHit(shot, computerHits);

        else
            computerMisses.add(shot);
    }

    public boolean validShotTest(Coordinate shot) {
        //loops to check shot validity
        for(Coordinate c: computerHits) {
            if(shot.sameCoordinateTest(c))
                return false;
        }
        for(Coordinate c: computerMisses) {
            if(shot.sameCoordinateTest(c))
                return false;
        }
        if(shot.getRow() > this.MAX || shot.getRow() < this.MIN
                || shot.getCol() > this.MAX || shot.getCol() < this.MIN)
            return false;

        return true;
    }

    public void shootAtPlayer() {
        Random random = new Random();
        Coordinate shot;
        do {
            int randRow = random.nextInt(this.MAX - this.MIN + 1) + this.MIN;
            int randCol = random.nextInt(this.MAX - this.MIN + 1) + this.MIN;
            shot = new Coordinate(randRow, randCol);
        } while (!validShotTest(shot));

        playerShot(shot);
    }

    void playerShot(Coordinate coor) {
        if(playerMisses.contains(coor)){
            System.out.println("Dupe");

        }

        //checks all ships, inputing playerHits for the sake
        //of civilian ships
        if(aircraftCarrier.covers(coor)){
            aircraftCarrier.shipHit(coor, playerHits);
        }else if (battleship.covers(coor)){
            battleship.shipHit(coor, playerHits);
        }else if (clipper.covers(coor)){
            clipper.shipHit(coor, playerHits);
        }else if (dinghy.covers(coor)){
            dinghy.shipHit(coor, playerHits);
        }else if (submarine.covers(coor)){
            submarine.shipHit(coor, playerHits);
        } else {
            playerMisses.add(coor);
        }
    }


    public void scan(int row, int col) {
        Coordinate c = new Coordinate(row, col);
        scanResult = false;
        if(computer_aircraftCarrier.scan(c)
                || computer_battleship.scan(c)
                || computer_clipper.scan(c)
                || computer_dinghy.scan(c)
                || computer_submarine.scan(c)) {
            scanResult = true;
        } else {
            scanResult = false;
        }
    }
}