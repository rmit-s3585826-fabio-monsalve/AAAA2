package player;

import ship.Ghostship;
import world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @authors Banu and Fabio Monsalve s3585826
 */
public class GreedyGuessPlayer  implements Player {
  private World world;
  private int shipsRemaining = 0;
  private ArrayList<World.ShipLocation> shipsLoc;
  private HashMap<String, Ghostship> ghostShips = new HashMap<>();
  private ArrayList<World.Coordinate> coordSet = new ArrayList<>();
  private boolean targetingMode;
  private World.Coordinate currentTarget;
  private ArrayList<World.Coordinate> targets = new ArrayList<>();;

  /**
   * In this method we create HashMap of what we call ghostships. This hashmap
   * gets used to return the number of ships remaining, we specifically use the
   * length of all ghostships and substract 1 from it every time that we know a
   * ship has been hit.
   *
   * @param world world object contains the configuration and ship locations
   */
  @Override
  public void initialisePlayer(World world) {
    this.world = world;
    shipsLoc = world.shipLocations;
    Ghostship ghostship;
    currentTarget = world.new Coordinate();

    // Make copies of each ship and add them to ghostShips and add the length of
    // all ships to shipsRemaining
    for (World.ShipLocation s : shipsLoc) {
      shipsRemaining = shipsRemaining + s.ship.len();
      System.out.println(shipsRemaining);
      System.out.println(s.ship.name());
      ghostship = new Ghostship(s.ship.name(), s.ship.len());
      ghostShips.put(s.ship.name(), ghostship);
    }

    // Add all current possible coordinates from world to coordSet
    for (int i = 0; i < world.numColumn; i++) {
      for (int j = 0; j < world.numRow; j++) {
        World.Coordinate cd = world.new Coordinate();
        cd.column = i;
        cd.row = j;
        coordSet.add(cd);
      }
    }
  }

  /**
   * This method first checks if any of the ships have been hit, if they have it
   * returns isHit as true and the name of the ship sunk by using the ghostShip
   * HashMap if its length is 0, if it hasn't been sunk but it has been hit, the
   * length of the ship is reduced by 1.
   *
   * @param guess from the opponent.
   *
   * @return answer to opponent
   */
  @Override
  public Answer getAnswer(Guess guess) {
    Answer answer = new Answer();
    Iterator<World.Coordinate> it;

    for (World.ShipLocation sl : shipsLoc) {
      it = sl.coordinates.iterator();

      while (it.hasNext()) {
        World.Coordinate sh = it.next();
        if (sh.column == guess.column && sh.row == guess.row) {
          answer.isHit = true;

          // Reduces number of ships remaining
          shipsRemaining--;

          // Reduces length of ghostShip (ship hit)
          ghostShips.get(sl.ship.name()).len--;
          if (ghostShips.get(sl.ship.name()).len == 0) {
            answer.shipSunk = sl.ship;
          }
          it.remove();
          break;
        }
      }
    }
    return answer;
  }

  /**
   * If in targeting mode this method will create 4 different coordinates
   * according to the currentTarget. These 4 coordinates are then filtered and
   * checked if they are repeated guesses or if they are out of bounds.
   *
   * @return player's guess
   */
  @Override
  public Guess makeGuess() {

    Guess guess = new Guess();
    World.Coordinate coordinate [] = new World.Coordinate [4];

    if (targetingMode) {
      // If it still has not hit a ship but it still has to hit all surrounding
      // cells
      if(targets.size() != 0){
        guess.row = targets.get(0).row;
        guess.column = targets.get(0).column;
        targets.remove(0);
        return guess;

        // Create coordinates for surrounding cells
      }else {
        World.Coordinate coordinate1 = world.new Coordinate();
        coordinate1.row = currentTarget.row - 1;
        coordinate1.column = currentTarget.column;
        World.Coordinate coordinate2 = world.new Coordinate();
        coordinate2.row = currentTarget.row + 1;
        coordinate2.column = currentTarget.column;
        World.Coordinate coordinate3 = world.new Coordinate();
        coordinate3.row = currentTarget.row;
        coordinate3.column = currentTarget.column + 1;
        World.Coordinate coordinate4 = world.new Coordinate();
        coordinate4.row = currentTarget.row;
        coordinate4.column = currentTarget.column - 1;

        coordinate[0] = coordinate1;
        coordinate[1] = coordinate2;
        coordinate[2] = coordinate3;
        coordinate[3] = coordinate4;

        // Checks if coordinates are out of bounds
        for (World.Coordinate aCoordinate : coordinate) {
          if (aCoordinate.column >= 0 && aCoordinate.row < 10
                  && aCoordinate.column < 10 && aCoordinate.row >= 0) {
            targets.add(aCoordinate);
          }
        }

        ArrayList<World.Coordinate> tempTargets = targets;

        // Check if any of the surrounding coordinates are repeated guesses
        for(int i = 0; i<tempTargets.size(); i++){
          if(world.shots.contains(tempTargets.get(i))){
            targets.remove(i);
          }
        }

        guess.row = targets.get(0).row;
        guess.column = targets.get(0).column;
        targets.remove(0);
        return guess;
      }
    }
    else {

      // Implements the parity method of guessing. This will guess every second
      // square as ships are atleast 2 squares in length.
      ArrayList<World.Coordinate> hits = coordSet;
      World.Coordinate nextGuessVal;

      int c = 1;
      boolean a = true;
      while(a) {
        while (!hits.isEmpty()) {
          if (c > hits.size()) {
            System.out.println(hits.get(hits.size() - 1).column + " " +
                    hits.get(hits.size() - 1).row);
            c = 1;
            nextGuessVal = hits.get(hits.size() - c);
            guess.column = nextGuessVal.column;
            guess.row = nextGuessVal.row;
            hits.remove(hits.size() - c);
            break;
          }

          nextGuessVal = hits.get(hits.size() - c);

          if (nextGuessVal.column % 2 == nextGuessVal.row % 2) {
            guess.column = nextGuessVal.column;
            guess.row = nextGuessVal.row;
            hits.remove(hits.size() - c);
            break;
          } else {
            c++;
          }
        }
        if(!world.shots.contains(guess)){
          a = false;
        }
      }
    }
    return guess;
  }

  /**
   * If a ship has been hit the targeting mode becomes true, when true, the
   * current target then becomes the last guess. makeGuess() will use this
   * information accordingly
   *
   * @param guess Guess of this player.
   * @param answer Answer to the guess from opponent.
   */
  @Override
  public void update(Guess guess, Answer answer) {

    if(answer.isHit){
      targetingMode = true;
      currentTarget.row = guess.row;
      currentTarget.column = guess.column;
      targets = new ArrayList<>();

      // If it has hit all surrounding cells of currentTarge it moves on by
      // making targetMode false
    }else if(targets.size() == 0){
      targetingMode = false;
    }
  }

  /**
   * @return true if there are no more ships to sink
   */
  @Override
  public boolean noRemainingShips() {
    return shipsRemaining == 0;
  }

}