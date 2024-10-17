package ca.mcmaster.se2aa4.island.team211.DroneTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.drone.DroneActions;
import ca.mcmaster.se2aa4.island.team211.drone.Radar;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.notification.RunListener;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DroneTest {

    private Drone drone;

    void setUpDrone(String direction){
        drone = new Drone();
        this.drone.direction = direction;
        this.drone.initialDirection = drone.direction;
        this.drone.x_cord = 0;
        this.drone.y_cord = 0;
        this.drone.decisionMaker = new FindStart(drone);
    }

    void setUpRadar(Drone drone, boolean setFound){
        if(setFound){
            this.drone.radar.found = true;
            this.drone.radar.range = 10;
        } else {
            this.drone.radar.found = false;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "E, 1, 1, S",
            "W, -1, -1, N",
            "N, 1, -1, E",
            "S, -1, 1, W"
    })
    void testTurnRight(String initialDirection, int expectedX, int expectedY, String expectedDirection) {
        setUpDrone(initialDirection);
        drone.droneActions.turnRight(drone);

        assertEquals(expectedX, drone.x_cord);
        assertEquals(expectedY, drone.y_cord);
        assertEquals(expectedDirection, drone.direction);
    }

    @ParameterizedTest
    @CsvSource({
            "E, 1, -1, N",
            "W, -1, 1, S",
            "N, -1, -1, W",
            "S, 1, 1, E"
    })
    void testTurnLeft(String initialDirection, int expectedX, int expectedY, String expectedDirection) {
        setUpDrone(initialDirection);
        drone.droneActions.turnLeft(drone);

        assertEquals(expectedX, drone.x_cord);
        assertEquals(expectedY, drone.y_cord);
        assertEquals(expectedDirection, drone.direction);
    }

    @ParameterizedTest
    @CsvSource({
            "N, 0, -1, N",
            "S, 0, 1, S",
            "E, 1, 0, E",
            "W, -1, 0, W"
    })
    void testForward(String initialDirection, int expectedX, int expectedY, String expectedDirection) {
        setUpDrone(initialDirection);
        drone.droneActions.forward(drone);

        assertEquals(expectedX, drone.x_cord);
        assertEquals(expectedY, drone.y_cord);
        assertEquals(expectedDirection, drone.direction);
    }

    @ParameterizedTest
    @CsvSource({
            "N, W, E",
            "E, N, S",
            "S, E, W",
            "W, S, N"
    })
    void testGetSides(String initial, String expectedLeft, String expectedRight){
        setUpDrone(initial);
        drone.droneActions.getSides(drone);

        assertEquals(expectedLeft, drone.left);
        assertEquals(expectedRight, drone.right);
    }


    @Test
    void testPrintCoords(){
        setUpDrone("N");
        drone.x_cord = 5;
        drone.y_cord = 10;

        Integer[] coords = drone.droneActions.printCoords(drone);

        Assertions.assertNotNull(coords);
        assertEquals(2, coords.length);
        assertEquals(Integer.valueOf(5), coords[0]);
        assertEquals(Integer.valueOf(10), coords[1]);
    }

    @Test
    void testSetStartNorthSouth(){
        setUpDrone("N");
        setUpRadar(drone, true);

        drone.droneActions.setStart(drone);
        int expected_X = drone.radar.range + 1;
        assertEquals(expected_X, drone.x_cord);
        assertEquals(1, drone.y_cord);
    }

    @Test
    void testSetStartEastWest(){
        setUpDrone("E");
        setUpRadar(drone, true);

        drone.droneActions.setStart(drone);
        int expected_Y = drone.radar.range + 1;
        assertEquals(1, drone.x_cord);
        assertEquals(expected_Y, drone.y_cord);
    }

    @Test
    void testGetCoordinates(){
        setUpDrone("N");

        Coordinate coords = DroneActions.getCordinates(drone);

        assertEquals(coords.xCoordinate, drone.x_cord);
        assertEquals(coords.yCoordinate, drone.y_cord);
    }
    
    @Test
    void testGetDecision(){
        setUpDrone("N");

        JSONObject expectedDecision = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", "W");
        expectedDecision.put("action", "echo").put("parameters", parameters);
        
        JSONObject decision = drone.droneActions.getDecision(drone);
        assertEquals(expectedDecision.toString(), decision.toString());
    }
}
