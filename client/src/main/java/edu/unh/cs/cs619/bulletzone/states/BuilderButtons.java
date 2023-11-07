package edu.unh.cs.cs619.bulletzone.states;

import android.view.View;
import android.widget.Button;

import edu.unh.cs.cs619.bulletzone.R;

public class BuilderButtons extends VehicleButtons {

    public BuilderButtons() {
        canSelectTank = true;
        canSelectMiner = true;
        canSelectBuilder = false;

        canBuild = true;
        canDismantle = false;
    }

}
