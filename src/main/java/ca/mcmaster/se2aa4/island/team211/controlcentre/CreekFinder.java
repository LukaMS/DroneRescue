/*
Similar logic to Gridsearch while scanning, except it turns around once it reaches OCEAN,
or it exits the range of the closest creek
 */

package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.DistanceCalculator;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Objects;

public class CreekFinder extends PhaseOneCommonDecisions {
    DistanceCalculator distanceCalculator;
    private float minRadius;
    private float droneDistanceToSite;
    private boolean outOfRadiusPermanent = false;
    private int creekSize;
    private int flyCount = 0;

    private final Logger logger = LogManager.getLogger();


    public CreekFinder(Drone drone, String lastTurn) {
        setDrone(drone);
        setLastTurn(lastTurn);
        setTurned(false);
        setLastAction(Action.fly);
        setFlyToGround(false);
        setTurnCount(0);
        initialize();
    }

    public void initialize(){
        distanceCalculator = new DistanceCalculator(drone);
        distanceCalculator.calculateDistances();
        creekSize = drone.creeks.size();
        setMinRadius();
        setDroneDistanceToSite();
    }

    private void setMinRadius(){
        distanceCalculator.calculateDistances();
        String closestCreek = distanceCalculator.determineClosest();
        minRadius = distanceCalculator.getDistances().get(closestCreek);
    }

    private void setDroneDistanceToSite(){droneDistanceToSite = distanceCalculator.distanceToSite(drone); }

    private boolean outOfRange(){return  droneDistanceToSite > minRadius; }

    @Override
    public JSONObject makeDecision() {
        setDroneDistanceToSite();
        logger.info("Drone to Site: " + droneDistanceToSite);
        logger.info("Closest Creek: " + minRadius);
        if (drone.creeks.size() > creekSize){
            creekSize = drone.creeks.size();
            setMinRadius();
        }


        //stop conditions
        if (outOfRadiusPermanent || drone.battery.batteryLevel < 1000 || drone.y_cord == 0 || drone.x_cord == 0) {return stop();}

        switch (lastAction){
            case null:
            case fly, heading: {
                return super.scanPosition(); // lastAction := heading
            }// lastAction := scan
            case echo: {
                if(outOfRange()){outOfRadiusPermanent = true;}
                //if found ground fly to it
                if (super.foundGround(drone)){
                    turned = false;
                    return super.flyToGround(); // lastAction := fly
                } //if didn't find ground, but just turned, then reAlign position
                else {
                    if (turned){
                        turned = false;
                        return super.reAlign(); // shifts position
                    } else { // if didn't find ground, and didn't just turn, then turn
                        if (Objects.equals(lastTurn, "RIGHT")) {
                            turnDirection = "RIGHT";
                        } // lastAction := heading
                        else{
                            turnDirection = "LEFT";
                        }
                        turnCount = 0;
                        if (drone.radar.range > 3 && Objects.equals(drone.radar.found,"OUT_OF_RANGE")){
                            return super.uTurn();
                        } else {
                            return super.uTurn2();
                        }
                    }
                }
            }
            case scan: {
                if (outOfRange()){ //if the drone is out of min range, then turn around
                    if (Objects.equals(lastTurn, "RIGHT")) {turnDirection = "RIGHT";}
                    else{turnDirection = "LEFT";}
                    turnCount = 0;
                    return super.uTurn();
                }
                if (super.overOcean()){
                    if (flyToGround){return super.flyToGround();} // lastAction := fly
                    else {return super.echoAhead();} // lastAction := echo
                } else {
                    flyToGround = false;
                    return super.flyForward(); // lastAction := fly
                }

            }
            case reAlign: {
                return super.reAlign();
            }
            case uTurn:{
                if (turnCount < 6) {
                    return super.uTurn(); //lastAction := uTurn
                } else {
                    turnCount = 0;
                    return returnToRadius(); // lastAction := echo
                }
            }
            case uTurn2:{
                if (turnCount < 6) {
                    return super.uTurn2(); //lastAction := uTurn
                } else {
                    turnCount = 0;
                    return super.echoAhead(); // lastAction := echo
                }
            }
            case returnToRadius:{
                if (outOfRange()){
                    return returnToRadius();
                }
                flyCount = 0;
                return super.echoAhead();
            }
            default: {return null;}
        }
    }

    private JSONObject returnToRadius(){
        if (flyCount > 10){return stop();}
        lastAction = Action.returnToRadius;
        flyCount++;
        return super.flyForward();
    }

    public void setOutOfRadiusPermanent(boolean input){
        this.outOfRadiusPermanent = input;
    }

}
