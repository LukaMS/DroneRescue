/*
finds the coast of the island, such that it is set up to efficiently use GridSearch (it will not miss any sections of land)
 */
package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class IslandFinder extends PhaseOneCommonDecisions {
    private String lastEchoDirection = "RIGHT";
    private boolean adjust = true;

    public IslandFinder(Drone drone){
        setLastAction(null);
        setTurnCount(0);
        setFlyToGround(false);
        setDrone(drone);
    }

    @Override
    public JSONObject makeDecision() {
        switch(lastAction){
            case null: {
                lastEchoDirection = "RIGHT";
                return echoDirection(drone.right);
            }
            case echo: {
                if (super.foundGround(drone)){
                    if (adjust) {
                        return adjustHeading();
                    }else{
                        return super.flyToGround(); //loops flying and scanning for ground
                    }
                }else{
                    return super.flyForward(); // lastAction := fly
                }
            }
            case fly: {
                if (flyToGround){
                    return super.scanPosition(); // lastAction := scan
                }
                if (!Objects.equals(lastEchoDirection,"LEFT")){
                    lastEchoDirection = "LEFT";
                    return echoDirection(drone.left); // lastAction := echo
                } else{
                    lastEchoDirection = "RIGHT";
                    return echoDirection(drone.right); // lastAction := echo
                }
            }
            case scan: {
                if (flyToGround) {
                    if (super.overOcean()) {
                        return super.flyToGround(); // lastAction := fly
                    } else {
                        drone.decisionMaker = new GridSearch(drone, lastEchoDirection);
                        return super.scanPosition();
                    }
                }
                return null;
            }
            case heading: {
                return super.flyToGround();
            }
            case reAlign:{
                if (turnCount < 5) {
                    return adjustHeading();
                }
                else{
                    turnCount = 0;
                    adjust = false;
                    return echoDirection(drone.direction);
                }
            }
            default: {return null;}
        }
    }

    private JSONObject adjustHeading() {
        lastAction = Action.reAlign;
        switch (turnCount){
            case 0 -> {
                turnCount++;
                return super.flyForward();
            }
            case 1, 2-> {
                turnCount++;
                if (Objects.equals(lastEchoDirection, "LEFT")) {
                    return super.turnLeft();
                } else {
                    return super.turnRight();
                }
            }
            case 3 -> {
                turnCount++;
                if (Objects.equals(lastEchoDirection, "LEFT")) {
                    return super.turnRight();
                } else {
                    return super.turnLeft();
                }
            }
            case 4 -> {
                turnCount++;
                return super.scanPosition();
            }
            default -> {}
        }
        return null;
    }

    private JSONObject echoDirection(String direction){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", direction);
        return super.sendDecision(lastAction, parameter);
    }

    public String getLastEchoDirection() {return lastEchoDirection;}
    public boolean isAdjust(){return adjust;}
    public void setAdjust(boolean set){adjust = set;}

}