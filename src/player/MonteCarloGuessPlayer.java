package player;

import ship.Ghostship;
import world.World;

import java.util.*;

/**
 * Montecarlo guess player (task C).
 * Please implement this class.
 *
 * @authors Banu and Fabio Monsalve s3585826
 */
public class MonteCarloGuessPlayer  implements Player{
  private World world;
  private int shipsRemaining = 0;
  private ArrayList<World.ShipLocation> shipsLoc;
  private HashMap<String, Ghostship> ghostShips = new HashMap<>();
  private HashMap<World.Coordinate, Integer> coordinateConfigurations = new
          HashMap<>();
  private boolean huntMode;

  // Initial number of configurations values for the first column in the board
  private Queue<Integer> configurationsQueue = new LinkedList();

  // Sequence of numbers which are added or subtracted to the initial values of
  // the first column as the each column is iterated through
  private Queue<Integer> configurationsQueueSequence = new LinkedList();

  private World.Coordinate currentTarget;

  /**
   * In this method we create HashMap of what we call ghostships. This hashmap
   * gets used to return the number of ships remaining, we specifically use the
   * length of all ghostships and substract 1 from it every time that we know a
   * ship has been hit.
   *
   * Furthermore for every cell that is iterated through from World a copy is
   * is put into a HashMap called coordinateConfigurations along with a
   * corresponding integer which details the number of configuration that the
   * coordinate can have. Each configuration is taken from the values of two
   * Queues; configurationsQueue and configurationsQueueSequence (each are
   * explained above)
   *
   * @param world world object contains the configuration and ship locations
   */
  @Override
  public void initialisePlayer(World world) {
    this.world = world;
    currentTarget = world.new Coordinate();
    int counter = 0;
    enque();
    enqueCounter();

    shipsLoc = world.shipLocations;
    Ghostship ghostship;

    // Make copies of each ship and add them to ghostShips and add the length of
    // all ships to shipsRemaining
    for (World.ShipLocation s: shipsLoc) {
      shipsRemaining = shipsRemaining + s.ship.len();
      System.out.println(shipsRemaining);
      System.out.println(s.ship.name());
      ghostship = new Ghostship(s.ship.name(), s.ship.len());
      ghostShips.put(s.ship.name(), ghostship);
    }

    // Add all current possible coordinates from world to coordSet
    // make configurations value for each coordinate
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

  /*
  Used to add values when needed in each iteration in initialise player
   */
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

  /*
  Used to add values when needed in each iteration in initialise player
  */
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

  /**
   * If in hunt mode this method will go through the surrounding cells of the
   * current target and work out which cell is the most likely to have a ship,
   * this cell becomes the bestTarget.
   * @return
   */
  @Override
  public Guess makeGuess() {

    Guess guess = new Guess();

    if(huntMode){
      ArrayList<World.Coordinate> huntingTargets = new ArrayList<>();
      World.Coordinate coordinate [] = new World.Coordinate [4];

      // Make surrounding coordinates of current target
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

      // Check if any coordinate is out of bounds
      for(int i = 0; i< coordinate.length; i++){
        if(coordinate[i].column > 0 || coordinate[i].row < 10
                && coordinate[i].column < 10 || coordinate[i].row > 0){
          huntingTargets.add(coordinate [i]);
        }
      }

      int highestValue = 0;

      // Iterate through each coordinate and work out which has the highest
      // number of configurations, this then becomes the best target
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

      // If not in hunting mode hit the cell with the most configurations
      // overall
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

      //remove coordinate that has been used
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

  /**
   * @return true if there are no more ships to sink
   */
  @Override
  public boolean noRemainingShips() {
    return shipsRemaining == 0;
  }
}