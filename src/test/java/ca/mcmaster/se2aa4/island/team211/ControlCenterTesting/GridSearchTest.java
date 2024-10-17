package ca.mcmaster.se2aa4.island.team211.ControlCenterTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.Action;
import ca.mcmaster.se2aa4.island.team211.controlcentre.CreekFinder;
import ca.mcmaster.se2aa4.island.team211.controlcentre.GridSearch;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class GridSearchTest  {

    private Drone drone;
    private GridSearch gridSearch;

    @BeforeEach
    void setUp(){
        drone = new Drone();
        this.drone.x_cord = 6;
        this.drone.y_cord = 12;
        this.drone.direction = "N";
        gridSearch = new GridSearch(drone, "RIGHT");
        drone.decisionMaker = gridSearch;
    }

    @Test
    void testGridSearchMakeDecisionFly(){
        drone.battery.batteryLevel = 2000;
        gridSearch.setLastAction(Action.fly);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "scan");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchLowBattery(){
        drone.battery.batteryLevel = 900;

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "stop");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionScanOverOcean(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        drone.currentBiomes = new JSONArray();
        drone.currentBiomes.put("OCEAN");
        gridSearch.setFlyToGround(false);
        gridSearch.setLastAction(Action.scan);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "echo").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionScanNOTOverOcean(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        drone.currentBiomes = new JSONArray();
        drone.currentBiomes.put("BEACH");
        gridSearch.setFlyToGround(false);
        gridSearch.setLastAction(Action.scan);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "fly");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionUTurn(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        gridSearch.setTurnDirection("RIGHT");
        gridSearch.setLastAction(Action.uTurn);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionUTurn2(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        gridSearch.setTurnDirection("LEFT");
        gridSearch.setLastAction(Action.uTurn2);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionReAlign(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        gridSearch.setTurnDirection("RIGHT");
        gridSearch.setLastAction(Action.reAlign);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionEchoFlyToGround(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        drone.radar.found = "GROUND";
        gridSearch.setLastAction(Action.echo);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "fly");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionEchoNotTurned(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        gridSearch.setTurned(false);
        gridSearch.setLastTurn("LEFT");
        drone.radar.range = 2;
        drone.radar.found = "OUT_OF_RANGE";
        gridSearch.setLastAction(Action.echo);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testGridSearchMakeDecisionEchoTurned(){
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        gridSearch.setTurned(true);
        gridSearch.setLastTurn("RIGHT");
        gridSearch.setLastAction(Action.echo);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }



}
