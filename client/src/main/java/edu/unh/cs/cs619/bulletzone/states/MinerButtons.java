package edu.unh.cs.cs619.bulletzone.states;

import android.view.View;
import android.widget.Button;

import edu.unh.cs.cs619.bulletzone.R;

public class MinerButtons extends VehicleButtons {

    public MinerButtons() {
        canSelectTank = true;
        canSelectMiner = false;
        canSelectBuilder = true;

        canMine = true;
    }
}
