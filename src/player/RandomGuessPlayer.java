package player;

import ship.Ghostship;
import world.World;
import world.World.Coordinate;
import world.World.ShipLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @authors Banu and Fabio Monsalve s3585826
 */
public class RandomGuessPlayer implements Player{
  private int shipsRemaining = 0;
  private ArrayList <ShipLocation> shipLocations;
  private HashMap<String, Ghostship> ghostShips =
          new HashMap<>();
  private ArrayList <Coordinate> coordSet = new ArrayList<>();

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
    shipLocations = world.shipLocations;
    Ghostship ghostship;

    // Make copies of each ship and add them to ghostShips and add the length of
    // all ships to shipsRemaining
    for (ShipLocation s: shipLocations) {
      shipsRemaining = shipsRemaining + s.ship.len();
      ghostship = new Ghostship(s.ship.name(), s.ship.len());
      ghostShips.put(s.ship.name(), ghostship);
    }

    // Add all current possible coordinates from world to coordSet
    for (int i =0; i< world.numColumn; i++) {
      for (int j= 0; j< world.numRow; j++) {
        Coordinate cd = world.new Coordinate();
        cd.column = i;
        cd.row = j;
        coordSet.add(cd);
      }
    }
    Collections.shuffle(coordSet);
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
    Iterator<Coordinate> it;

    for (ShipLocation sl: shipLocations) {
      it = sl.coordinates.iterator();
      while (it.hasNext()) {
        Coordinate sh = it.next();
        if (sh.column == guess.column && sh.row == guess.row) {
          answer.isHit = true;

          // Reduces number of ships remaining
          shipsRemaining--;

          // Reduces length of ghostShip (ship hit)
          ghostShips.get(sl.ship.name()).len --;
          if(ghostShips.get(sl.ship.name()).len == 0){
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
   * Returns unrepeated guess by using all coordSet
   * @return guess
   */
  @Override
  public Guess makeGuess() {
    Guess myguess = new Guess();
    ArrayList<Coordinate> hits = coordSet;
    Coordinate guessval = hits.remove(hits.size()-1);

    myguess.column = guessval.column;
    myguess.row = guessval.row;

    return myguess;
  }

  @Override
  public void update(Guess guess, Answer answer) {
  }

  /**
   * @return true if there are no more ships to sink
   */
  @Override
  public boolean noRemainingShips() {
    return shipsRemaining == 0;
  }
}