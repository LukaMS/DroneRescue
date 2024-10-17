/*
Stores emergency sites found on the island
 */

package ca.mcmaster.se2aa4.island.team211.locations;

import java.util.Map;

public class EmergSite {
    public Object id;

    public EmergSite (Object id){this.id = id;}

    public static Map<EmergSite, Coordinate> sites;
}
