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
    World.Coordinate coordinate [] = new World.Coordinate [4];

    if (targetingMode) {
      if(targets.size() != 0){
        guess.row = targets.get(0).row;
        guess.column = targets.get(0).column;
        targets.remove(0);
        return guess;
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


        for (World.Coordinate aCoordinate : coordinate) {
          if (aCoordinate.column >= 0 && aCoordinate.row < 10
                  && aCoordinate.column < 10 && aCoordinate.row >= 0) {
            targets.add(aCoordinate);
          }
        }

        ArrayList<World.Coordinate> tempTargets = targets;

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

  @Override
  public void update(Guess guess, Answer answer) {

    if(answer.isHit){
      targetingMode = true;
      currentTarget.row = guess.row;
      currentTarget.column = guess.column;
      targets = new ArrayList<>();
    }else if(targets.size() == 0){
      targetingMode = false;
    }
  }

  @Override
  public boolean noRemainingShips() {
    return shipsRemaining == 0;
  }

}