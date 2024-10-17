/*
Common methods shared by all DecisionMakers when trying to find the emergency site and creeks
 */

package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public abstract class PhaseOneCommonDecisions implements DecisionMaker{
    protected Drone drone;
    protected Action lastAction = null;
    protected boolean turned; //indicates whether a turn was just made or not
    protected String lastTurn; //Used to determine next turn
    protected String turnDirection; //The current turn being made
    protected boolean flyToGround; //flag to see if the drone should be flying to a separate piece of land
    protected Integer turnCount; //used for determining how many more turns need to be made

    public void setDrone(Drone drone){this.drone = drone;}
    public void setTurned(boolean turned){this.turned = turned;}
    public void setLastAction(Action lastAction){this.lastAction = lastAction;}
    public void setLastTurn(String lastTurn){this.lastTurn = lastTurn;}
    public void setTurnDirection(String turnDirection){this.turnDirection = turnDirection;}
    public void setFlyToGround(boolean flyToGround){this.flyToGround = flyToGround;}
    public void setTurnCount(Integer turnCount){this.turnCount = turnCount;}

    public Drone getDrone(){return drone;}
    public boolean isTurned(){return turned;}
    public Action getLastAction(){return lastAction;}
    public String getLastTurn(){return lastTurn;}
    public String getTurnDirection(){return turnDirection;}
    public boolean isFlyToGround(){return flyToGround;}
    public Integer getTurnCount(){return turnCount;}

    @Override
    public JSONObject sendDecision(Action action, JSONObject parameters){
        JSONObject decision = new JSONObject();
        decision.put("action", action).put("parameters", parameters);
        return decision;
    }

    @Override
    public JSONObject sendDecision(Action action){
        JSONObject decision = new JSONObject();
        decision.put("action", action);
        return decision;
    }

    public boolean foundGround(Drone drone){return Objects.equals(drone.radar.found, "GROUND");}
    public boolean overOcean(){
        for (int i = 0; i < drone.currentBiomes.length(); i++) {
            if (!"OCEAN".equals(drone.currentBiomes.get(i))) {
                return false;
            }
        }
        return true;
    }


    //Turns in the drone's right direction
    public JSONObject turnRight() {
        if (shouldChangeLastAction()){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        drone.droneActions.turnRight(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }

    //Turns in the drone's left direction
    public JSONObject turnLeft() {
        if (shouldChangeLastAction()){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        drone.droneActions.turnLeft(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }

    public JSONObject flyForward(){
        if (shouldChangeLastAction()){lastAction = Action.fly;}
        drone.droneActions.forward(drone); //update position of drone
        return sendDecision(Action.fly);
    }

    public JSONObject scanPosition(){
        if (shouldChangeLastAction()){lastAction = Action.scan;}
        return sendDecision(Action.scan);
    }

    public JSONObject echoAhead(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        return sendDecision(lastAction, parameter);
    }

    public JSONObject stop() {
        lastAction = Action.stop;
        return sendDecision(lastAction);
    }

    public JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }

    private boolean shouldChangeLastAction(){
        return !(Objects.equals(lastAction,Action.uTurn) || Objects.equals(lastAction,Action.reAlign) || Objects.equals(lastAction,Action.uTurn2) || Objects.equals(lastAction,Action.returnToRadius));
    }

     /*
    Weird U-Turn to make it go row by row instead of being offset by one
        1. If last turn was right and turn count is 0 then turn right
        2. If turn count is 1 and do opposite of last turn
        3. If turn count is 2 then do same turn
        4. if turn count is 3 fly forward
        5. if turn count is 5 then turn same again.
     */

    //flips the drones direction
    public JSONObject uTurn() {
        lastAction = Action.uTurn;
        turned = true;
        switch (turnCount) {
            case 0 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    turnDirection = "RIGHT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    turnDirection = "LEFT";
                    return turnRight();
                }
            }
            case 1, 2, 4 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    return turnRight();
                }
            }
            case 3, 5 -> {
                turnCount++;
                return flyForward();
            }
            default -> {
                return null;
            }
        }
    }
    public JSONObject uTurn2(){
        lastAction = Action.uTurn2;
        turned = true;
        switch (turnCount){
            case 0, 1, 2, 4 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    return turnRight();
                }
            }
            case 3 -> {
                turnCount++;
                if (Objects.equals(lastTurn, "LEFT")) {
                    turnDirection = "RIGHT";
                } else {
                    turnDirection = "LEFT";
                }
                return flyForward();
            }
            case 5 -> {
                turnCount++;
                return scanPosition();
            }
            default -> {return null;}
        }
    }

    public JSONObject reAlign() {
        lastAction = Action.reAlign;
        if (turnCount == 0) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount++;
                return turnRight();
            } else {
                turnCount++;
                return turnLeft();
            }
        } else if (turnCount == 1){
            turnCount++;
            return flyForward();
        } else if (turnCount == 2) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount = 0;
                lastAction = Action.heading;
                lastTurn = "LEFT";
                return turnLeft();
            } else {
                turnCount = 0;
                lastAction = Action.heading;
                lastTurn = "RIGHT";
                return turnRight();
            }
        }
        else {
            return echoAhead();
        }
    }
}
