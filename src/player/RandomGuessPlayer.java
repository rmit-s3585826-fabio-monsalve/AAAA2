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
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Banu and Fabio Monsalve s3585826
 */
public class RandomGuessPlayer implements Player{
  private int shipsremaining = 0;
  private ArrayList <ShipLocation> shipsloc;
  private HashMap<String, Ghostship> ghostShips =
          new HashMap<>();
  private ArrayList <Coordinate> coordset = new ArrayList<>();

  @Override
  public void initialisePlayer(World world) {
    shipsloc = world.shipLocations;
    Ghostship ghostship;

    for (ShipLocation s: shipsloc) {
      shipsremaining = shipsremaining + s.ship.len();
      ghostship = new Ghostship(s.ship.name(), s.ship.len());
      ghostShips.put(s.ship.name(), ghostship);
    }

    for (int i =0; i< world.numColumn; i++) {
      for (int j= 0; j< world.numRow; j++) {
        Coordinate cd = world.new Coordinate();
        cd.column = i;
        cd.row = j;
        coordset.add(cd);
      }
    }
    Collections.shuffle(coordset);
  }

  @Override
  public Answer getAnswer(Guess guess) {
    Answer answer = new Answer();
    Iterator<Coordinate> it;

    for (ShipLocation sl: shipsloc) {
      it = sl.coordinates.iterator();
      while (it.hasNext()) {
        Coordinate sh = it.next();
        if (sh.column == guess.column && sh.row == guess.row) {
          answer.isHit = true;
          shipsremaining--;
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

  @Override
  public Guess makeGuess() {
    Guess myguess = new Guess();
    ArrayList<Coordinate> hits = coordset;
    Coordinate guessval = hits.remove(hits.size()-1);

    myguess.column = guessval.column;
    myguess.row = guessval.row;

    return myguess;
  }

  @Override
  public void update(Guess guess, Answer answer) {
  }

  @Override
  public boolean noRemainingShips() {
    return shipsremaining == 0;
  }
}