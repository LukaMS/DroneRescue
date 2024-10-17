package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.controlcentre.Action;
import ca.mcmaster.se2aa4.island.team211.controlcentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataExtractorTest {

    private static class FakeDecisionMaker implements DecisionMaker {
        private final Action lastAction;

        public FakeDecisionMaker(Action action){
            this.lastAction = action;
        }

        @Override
        public Action getLastAction() {
            return lastAction;
        }

        @Override
        public JSONObject sendDecision(Action action, JSONObject parameters) {
            return null;
        }

        @Override
        public JSONObject sendDecision(Action action) {
            return null;
        }

        @Override
        public JSONObject makeDecision(){
            return null;
        }
    }

    private Drone drone;
    private FakeDecisionMaker fakeDecisionMaker;

    void setUp(Action lastaction){
        drone = new Drone();
        drone.decisionMaker = new FakeDecisionMaker(lastaction);
        this.drone.direction = "S";
        this.drone.initialDirection = drone.direction;
        this.drone.x_cord = 0;
        this.drone.y_cord = 0;
    }

    @Test
    void extractTestEcho() {
        setUp(Action.echo);
        JSONObject extraInfo = new JSONObject();
        extraInfo.put("range", 100);
        extraInfo.put("found", "ground");

        drone.extractdata(extraInfo);
        assertEquals(100, drone.radar.range);
        assertEquals( "ground", drone.radar.found);
    }

    @Test
    void extractTestScan() {
        setUp(Action.scan);
        JSONObject extraInfo = new JSONObject();
        extraInfo.put("creeks", new JSONArray().put("creekId"));
        extraInfo.put("sites", new JSONArray().put("siteId"));
        extraInfo.put("biomes", new JSONArray().put("OCEAN"));

        drone.extractdata(extraInfo);
        assertTrue(drone.creeks.containsKey("creekId"));
        assertTrue(drone.emergencySites.containsKey("siteId"));
        assertEquals("OCEAN", drone.currentBiomes.get(0));
    }
}
