/*
Stores creeks found along the perimeter of the island
 */

package ca.mcmaster.se2aa4.island.team211.locations;

import java.util.Map;

public class Creek {
    public Object id;
    public Creek (Object id){
        this.id = id;
    }
    public static Map<Creek, Coordinate> creeks;
}
