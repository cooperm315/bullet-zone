package edu.unh.cs.cs619.bulletzone.states;

import edu.unh.cs.cs619.bulletzone.model.GameUser;

public class VehicleButtonStates {

    private static VehicleButtonStates instance;


    private char activeVehicle;
    private VehicleButtons activeButtons;
    private TankButtons tankButtons;
    private MinerButtons minerButtons;
    private BuilderButtons builderButtons;

    public static synchronized VehicleButtonStates getInstance() {
        if (instance == null)
            instance = new VehicleButtonStates();

        return instance;
    }

    public void rebootInstance() {
        instance = new VehicleButtonStates();
    }

    private VehicleButtonStates() {
        tankButtons = new TankButtons();
        minerButtons = new MinerButtons();
        builderButtons = new BuilderButtons();

        activeButtons = tankButtons;
        activeVehicle = 'T';
    }

    public VehicleButtons getActiveButtons() {
        return activeButtons;
    }

    /**
     * Activate a set of buttons
     * @param activeVehicle
     */
    public void setActiveButtons(char activeVehicle) {
        switch (activeVehicle) {
            case 'T':
                activeButtons = tankButtons;
                activeVehicle = 'T';
                break;
            case 'M':
                activeButtons = minerButtons;
                activeVehicle = 'M';
                break;
            case 'B':
                activeButtons = builderButtons;
                activeVehicle = 'B';
                break;
        }
    }

    public char getActiveVehicle() {
        return activeVehicle;
    }

}
