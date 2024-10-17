/*
List of available actions the drone can take
 */

package ca.mcmaster.se2aa4.island.team211.controlcentre;

public enum Action {
    fly,
    stop,
    heading,
    echo,
    scan,
    uTurn, uTurn2,
    reAlign, returnToRadius;
    public static int cost;
}