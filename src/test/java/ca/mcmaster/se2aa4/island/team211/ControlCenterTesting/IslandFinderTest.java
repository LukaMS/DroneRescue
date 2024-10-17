package ca.mcmaster.se2aa4.island.team211.ControlCenterTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.Action;
import ca.mcmaster.se2aa4.island.team211.controlcentre.IslandFinder;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class IslandFinderTest  {

    @Test
    void testInitialIslandFinderState() {
        Drone mockDrone = new Drone();
        IslandFinder islandFinder = new IslandFinder(mockDrone);

        // Test initial state
        assertEquals(0, islandFinder.getTurnCount());
        assertFalse(islandFinder.isFlyToGround());
        assertEquals(mockDrone, islandFinder.getDrone());
        assertEquals("RIGHT", islandFinder.getLastEchoDirection());
    }

    @Test
    void testMakeDecision() {
        Drone mockDrone = new Drone();
        IslandFinder islandFinder = new IslandFinder(mockDrone);
        mockDrone.x_cord = 1;
        mockDrone.y_cord = 1;
        mockDrone.direction = "E";

        // Test cases for each possible last action
        nullTest(islandFinder,mockDrone);

        echoTest(islandFinder, mockDrone);

        flyTest(islandFinder, mockDrone);

        scanTest(islandFinder, mockDrone);

        headingTest(islandFinder, mockDrone);

        for (int i = 0; i < 7; i++) {
            islandFinder.setTurnCount(i);
            islandFinder.setLastAction(Action.reAlign);
            reAlignTest(islandFinder, mockDrone); //also tests adjustHeading method
        }

        defaultTest(islandFinder);
    }


    void nullTest(IslandFinder islandFinder, Drone mockDrone){
        islandFinder.setLastAction(null);

        JSONObject decisionTest = islandFinder.makeDecision();
        assertEquals("RIGHT", islandFinder.getLastEchoDirection());
        assertEquals(Action.echo, islandFinder.getLastAction());

        JSONObject decision = new JSONObject();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", mockDrone.right);
        decision.put("action", Action.echo).put("parameters", parameter);
        assertEquals(decision.toString(), decisionTest.toString());
    }

    void echoTest(IslandFinder islandFinder, Drone mockDrone){
        islandFinder.setLastAction(Action.echo);
        mockDrone.radar.found = "GROUND";
        islandFinder.setAdjust(true);

        JSONObject decisionTest = islandFinder.makeDecision();
        assertEquals(Action.reAlign, islandFinder.getLastAction());
        assertEquals(1, islandFinder.getTurnCount());

        JSONObject decision = new JSONObject();
        decision.put("action", Action.fly);
        assertEquals(decision.toString(), decisionTest.toString());
        assertEquals(2, mockDrone.x_cord);
        assertEquals(1, mockDrone.y_cord);
    }

    void flyTest(IslandFinder islandFinder, Drone mockDrone){
        islandFinder.setLastAction(Action.fly);
        islandFinder.setFlyToGround(true);

        JSONObject decisionTest = islandFinder.makeDecision();
        assertEquals(Action.scan, islandFinder.getLastAction());

        JSONObject decision = new JSONObject();
        decision.put("action", Action.scan);
        assertEquals(decision.toString(), decisionTest.toString());
        assertEquals(2, mockDrone.x_cord);
        assertEquals(1, mockDrone.y_cord);
    }

    void scanTest(IslandFinder islandFinder, Drone mockDrone){
        islandFinder.setLastAction(Action.scan);
        islandFinder.setFlyToGround(true);
        mockDrone.currentBiomes = new JSONArray().put(0, "OCEAN");

        JSONObject decisionTest = islandFinder.makeDecision();
        assertEquals(Action.fly, islandFinder.getLastAction());

        JSONObject decision = new JSONObject();
        decision.put("action", Action.fly);
        assertEquals(decision.toString(), decisionTest.toString());
        assertEquals(3, mockDrone.x_cord);
        assertEquals(1, mockDrone.y_cord);

        islandFinder.setFlyToGround(false);

    }

    void headingTest(IslandFinder islandFinder, Drone mockDrone){
        islandFinder.setLastAction(Action.heading);

        JSONObject decisionTest = islandFinder.makeDecision();
        assertEquals(Action.fly, islandFinder.getLastAction());

        JSONObject decision = new JSONObject();
        decision.put("action", Action.fly);
        assertEquals(decision.toString(), decisionTest.toString());
        assertEquals(4, mockDrone.x_cord);
        assertEquals(1, mockDrone.y_cord);
    }

    void reAlignTest(IslandFinder islandFinder, Drone mockDrone){
        JSONObject decisionTest;
        switch (islandFinder.getTurnCount()){
            case 0 -> {
                decisionTest = islandFinder.makeDecision();
                assertEquals(Action.reAlign, islandFinder.getLastAction());
                JSONObject decision = new JSONObject();
                decision.put("action", Action.fly);
                assertEquals(decision.toString(), decisionTest.toString());
                assertEquals(5, mockDrone.x_cord);
                assertEquals(1, mockDrone.y_cord);

                assertEquals(1, islandFinder.getTurnCount());
            }
            case 1, 2-> {
                decisionTest = islandFinder.makeDecision();
                assertEquals(Action.reAlign, islandFinder.getLastAction());
                assertEquals("RIGHT", islandFinder.getLastEchoDirection());

                JSONObject parameter = new JSONObject();
                parameter.put("direction", mockDrone.right);
                JSONObject decision = new JSONObject();
                decision.put("action", Action.heading).put("parameters", parameter);

                assertEquals(decision.toString(), decisionTest.toString());
                if (islandFinder.getTurnCount() == 2) {
                    assertEquals(6, mockDrone.x_cord);
                    assertEquals(2, mockDrone.y_cord);
                    assertEquals("S", mockDrone.direction);
                }else{
                    assertEquals(5, mockDrone.x_cord);
                    assertEquals(3, mockDrone.y_cord);
                    assertEquals("W", mockDrone.direction);
                }

                assertTrue(islandFinder.getTurnCount() == 2 || islandFinder.getTurnCount() == 3);

            }
            case 3 -> {
                decisionTest = islandFinder.makeDecision();
                assertEquals(Action.reAlign, islandFinder.getLastAction());
                assertEquals("RIGHT", islandFinder.getLastEchoDirection());

                JSONObject parameter = new JSONObject();
                parameter.put("direction", mockDrone.left);
                JSONObject decision = new JSONObject();
                decision.put("action", Action.heading).put("parameters", parameter);

                assertEquals(decision.toString(), decisionTest.toString());
                assertEquals(4, mockDrone.x_cord);
                assertEquals(4, mockDrone.y_cord);
                assertEquals("S", mockDrone.direction);

                assertEquals(4, islandFinder.getTurnCount());


            }
            case 4 -> {
                decisionTest = islandFinder.makeDecision();

                assertEquals(Action.reAlign, islandFinder.getLastAction());

                JSONObject decision = new JSONObject();
                decision.put("action", Action.scan);
                assertEquals(decision.toString(), decisionTest.toString());
                assertEquals(4, mockDrone.x_cord);
                assertEquals(4, mockDrone.y_cord);
                assertEquals("S", mockDrone.direction);

                assertEquals(5, islandFinder.getTurnCount());

            }
            case 5 -> {
                decisionTest = islandFinder.makeDecision();

                assertFalse(islandFinder.isAdjust());
                assertEquals(Action.echo, islandFinder.getLastAction());

                JSONObject parameter = new JSONObject();
                parameter.put("direction", mockDrone.direction);
                JSONObject decision = new JSONObject();
                decision.put("action", Action.echo).put("parameters", parameter);

                assertEquals(decision.toString(), decisionTest.toString());
                assertEquals(0, islandFinder.getTurnCount());

            }
        }
    }
    void defaultTest(IslandFinder islandFinder){
        islandFinder.setLastAction(Action.uTurn);
        JSONObject decisionTest = islandFinder.makeDecision();
        assertNull(decisionTest);

    }


}
