package player;

import ship.Ghostship;
import world.World;

import java.util.ArrayList;
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
  private int targetsHit = 0;
  private int currentTargetRow;
  private int currentTargetColumn;
  World.Coordinate currentTarget;
  private ArrayList<World.Coordinate> huntingTargets;

  @Override
  public void initialisePlayer(World world) {
    this.world = world;
    shipsLoc = world.shipLocations;
    Ghostship ghostship;
    currentTarget = world.new Coordinate();

    for (World.ShipLocation s : shipsLoc) {
      shipsRemaining = shipsRemaining + s.ship.len();
      System.out.println(shipsRemaining);
      System.out.println(s.ship.name());
      ghostship = new Ghostship(s.ship.name(), s.ship.len());
      ghostShips.put(s.ship.name(), ghostship);
    }

    for (int i = 0; i < world.numColumn; i++) {
      for (int j = 0; j < world.numRow; j++) {
        World.Coordinate cd = world.new Coordinate();
        cd.column = i;
        cd.row = j;
        coordSet.add(cd);
      }
    }
  }

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
          shipsRemaining--;
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

  @Override
  public Guess makeGuess() {
    Guess guess = new Guess();

    if (targetingMode) {
      boolean a = true;

      while (true) {
        huntingTargets = new ArrayList<>();
        if (targetsHit == 0) {
          World.Coordinate coordinate1 = world.new Coordinate();
          coordinate1.row = currentTarget.row - 1;
          coordinate1.column = currentTarget.column;
          guess.row = coordinate1.row;
          guess.column = coordinate1.column;
        } else if (targetsHit == 1) {
          World.Coordinate coordinate2 = world.new Coordinate();
          coordinate2.row = currentTarget.row + 1;
          coordinate2.column = currentTarget.column;
          guess.row = coordinate2.row;
          guess.column = coordinate2.column;
        } else if (targetsHit == 2) {
          World.Coordinate coordinate3 = world.new Coordinate();
          coordinate3.row = currentTarget.row;
          coordinate3.column = currentTarget.column + 1;
          guess.row = coordinate3.row;
          guess.column = coordinate3.column;
        } else if (targetsHit == 3) {
          World.Coordinate coordinate4 = world.new Coordinate();
          coordinate4.row = currentTarget.row;
          coordinate4.column = currentTarget.column - 1;
          guess.row = coordinate4.row;
          guess.column = coordinate4.column;
          targetingMode = false;
        }

        if (guessValidation(guess)){
          targetsHit++;
          return guess;
        }
      }

    } else {
      ArrayList<World.Coordinate> hits = coordSet;
      World.Coordinate nextGuessVal;

      int c = 1;

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
    }
    return guess;
  }

  public boolean guessValidation(Guess guess){

    return guess.row > 0 || guess.row < 10 || guess.column > 0 ||
            guess.column < 10;
  }


  @Override
  public void update(Guess guess, Answer answer) {

    if(answer.isHit){
      targetingMode = true;
      currentTargetRow = guess.row;
      currentTargetColumn = guess.column;
      targetsHit = 0;
    }
  }

  @Override
  public boolean noRemainingShips() {
    return shipsRemaining == 0;
  }

}