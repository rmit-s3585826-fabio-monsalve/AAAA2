package player;

import ship.Ghostship;
import world.World;

import java.util.*;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Banu and Fabio Monsalve s3585826
 */
public class MonteCarloGuessPlayer  implements Player{
  World world;
  private int shipsRemaining = 0;
  private ArrayList<World.ShipLocation> shipsLoc;
  private HashMap<String, Ghostship> ghostShips = new HashMap<>();
  private HashMap<World.Coordinate, Integer> coordinateConfigurations = new
          HashMap<>();
  private boolean huntMode;
  private Queue configurationsQueue = new LinkedList();
  private Queue configurationsQueueSequence = new LinkedList();
  World.Coordinate currentTarget;
  private ArrayList<World.Coordinate> huntingTargets;

  @Override
  public void initialisePlayer(World world) {
    this.world = world;
    currentTarget = world.new Coordinate();
    int counter = 0;
    enque();
    enqueCounter();

    shipsLoc = world.shipLocations;
    Ghostship ghostship;

    for (World.ShipLocation s: shipsLoc) {
      shipsRemaining = shipsRemaining + s.ship.len();
      System.out.println(shipsRemaining);
      System.out.println(s.ship.name());
      ghostship = new Ghostship(s.ship.name(), s.ship.len());
      ghostShips.put(s.ship.name(), ghostship);
    }

    for (int i =0; i< world.numColumn; i++) {
      for (int j= 0; j< world.numRow; j++) {
        World.Coordinate cd = world.new Coordinate();
        cd.column = i;
        cd.row = j;

        coordinateConfigurations.put(cd, counter + (Integer)
                configurationsQueue.element() +
                (Integer) configurationsQueueSequence.element());

        configurationsQueue.remove();
      }
      configurationsQueueSequence.remove();
      enque();
      if(counter == 10){
        counter = 0;
      }
      counter ++;
    }

  }
  private void enqueCounter(){
    configurationsQueueSequence.add(0);
    configurationsQueueSequence.add(0);
    configurationsQueueSequence.add(1);
    configurationsQueueSequence.add(1);
    configurationsQueueSequence.add(1);
    configurationsQueueSequence.add(0);
    configurationsQueueSequence.add(-2);
    configurationsQueueSequence.add(-4);
    configurationsQueueSequence.add(-7);
    configurationsQueueSequence.add(-9);
  }
  private void enque(){
    configurationsQueue.add(10);
    configurationsQueue.add(11);
    configurationsQueue.add(13);
    configurationsQueue.add(14);
    configurationsQueue.add(15);
    configurationsQueue.add(15);
    configurationsQueue.add(14);
    configurationsQueue.add(13);
    configurationsQueue.add(11);
    configurationsQueue.add(10);
  }

  @Override
  public Answer getAnswer(Guess guess) {
    Answer answer = new Answer();
    Iterator<World.Coordinate> it;

    for (World.ShipLocation sl: shipsLoc) {
      it = sl.coordinates.iterator();
      while (it.hasNext()) {
        World.Coordinate sh = it.next();
        if (sh.column == guess.column && sh.row == guess.row) {
          answer.isHit = true;
          shipsRemaining--;
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

    Guess guess = new Guess();

    if(huntMode){

      huntingTargets = new ArrayList<>();
      World.Coordinate coordinate [] = new World.Coordinate [4];

      World.Coordinate coordinate1 = world.new Coordinate();
      coordinate1.row = currentTarget.row +1;
      coordinate1.column = currentTarget.column;
      World.Coordinate coordinate2 = world.new Coordinate();
      coordinate2.row = currentTarget.row -1;
      coordinate2.column = currentTarget.column;
      World.Coordinate coordinate3 = world.new Coordinate();
      coordinate3.row = currentTarget.row;
      coordinate3.column = currentTarget.column +1;
      World.Coordinate coordinate4 = world.new Coordinate();
      coordinate4.row = currentTarget.row;
      coordinate4.column = currentTarget.column -1;

      World.Coordinate bestTarget = world.new Coordinate();

      coordinate[0] = coordinate1;
      coordinate[1] = coordinate2;
      coordinate[2] = coordinate3;
      coordinate[3] = coordinate4;

      for(int i = 0; i< coordinate.length; i++){
        if(coordinate[i].column > 0 || coordinate[i].row < 10
                && coordinate[i].column < 10 || coordinate[i].row > 0){
          huntingTargets.add(coordinate [i]);
        }
      }

      int highestValue = 0;

      for(World.Coordinate e: huntingTargets){

        for(World.Coordinate r : coordinateConfigurations.keySet()) {
          if (e.row == r.row && e.column == r.column) {
            if (coordinateConfigurations.get(r) > highestValue) {
              highestValue = coordinateConfigurations.get(r);
              guess.row = r.row;
              guess.column = r.column;
              bestTarget.row = r.row;
              bestTarget.column = r.column;
            }
          }
        }
      }

    } else {
      int highestValue = 0;
      World.Coordinate coordinate = world.new Coordinate();

      for(Map.Entry<World.Coordinate, Integer> e :
              coordinateConfigurations.entrySet()){

        for(int i = 0; i < coordinateConfigurations.size(); i ++){
          if(e.getValue() > highestValue){
            highestValue = e.getValue();
            coordinate = e.getKey();
          }
        }
        guess.column = coordinate.column;
        guess.row = coordinate.row;
      }
      coordinateConfigurations.remove(coordinate);
    }
    return guess;
  }

  @Override
  public void update(Guess guess, Answer answer) {
    if (answer.isHit) {
      huntMode = true;
      currentTarget.row = guess.row;
      currentTarget.row = guess.column;
    } else{
      huntMode = false;

    }
  }

  @Override
  public boolean noRemainingShips() {
    return shipsRemaining == 0;
  }
}