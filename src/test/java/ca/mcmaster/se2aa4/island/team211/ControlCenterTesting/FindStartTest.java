package ca.mcmaster.se2aa4.island.team211.ControlCenterTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.Action;
import ca.mcmaster.se2aa4.island.team211.controlcentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class FindStartTest  {

    @Test
    void testInitialFindStartState() {
        Drone mockDrone = new Drone();
        FindStart findStart = new FindStart(mockDrone);

        // Test initial state
        assertFalse(findStart.isFoundStart());
        assertNull(findStart.getLastAction());
        assertEquals(mockDrone, findStart.getDrone());
    }

    @Test
    void testFindStartWhenNotAlreadyFound() {
        Drone mockDrone = new Drone();
        FindStart findStart = new FindStart(mockDrone);
        mockDrone.direction = "E";
        JSONObject decision = new JSONObject();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", "N");
        decision.put("action", Action.echo).put("parameters", parameter);

        JSONObject decisionTest = findStart.makeDecision();
        assertEquals(Action.echo, findStart.getLastAction());
        assertEquals(decision.toString(), decisionTest.toString());
    }

    @Test
    void testFindStartWhenAlreadyFound() {
        Drone mockDrone = new Drone();
        FindStart findStart = new FindStart(mockDrone);
        findStart.setLastAction(Action.echo);
        mockDrone.direction = "E";
        mockDrone.radar.range = 10;
        JSONObject decision = new JSONObject();
        decision.put("action", Action.scan);

        JSONObject decisionTest = findStart.makeDecision();
        assertEquals(Action.scan, findStart.getLastAction());
        assertTrue(findStart.isFoundStart());
        assertEquals(decision.toString(), decisionTest.toString());
        assertEquals(11, mockDrone.y_cord);
        assertEquals(1, mockDrone.x_cord);
    }
}
