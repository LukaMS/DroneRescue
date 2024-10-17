/*
Find the starting coordinates of the drone
 */

package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

public class FindStart extends PhaseOneCommonDecisions {
    private boolean foundStart = false;

    public FindStart(Drone drone) {
        setDrone(drone);
        setLastAction(null);
    }

    @Override
    public JSONObject makeDecision() {
        JSONObject parameters;
        if(!foundStart){
            if(lastAction == null){
                parameters = findStart();
                setLastAction(Action.echo);
                return super.sendDecision(lastAction, parameters);
            } else {
                drone.droneActions.setStart(drone);
                foundStart = true;
                lastAction = Action.scan;
                return super.sendDecision(lastAction);
            }
        }
        return super.sendDecision(Action.stop);
    }

    public JSONObject findStart(){
        JSONObject params = new JSONObject();
        switch (drone.direction){
            case "N", "S":
                params.put("direction", "W");
                break;
            case "E", "W":
                params.put("direction", "N");
                break;
            default:
        }
        return params;
    }

    public boolean isFoundStart() {return foundStart;}
}
