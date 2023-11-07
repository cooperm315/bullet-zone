package edu.unh.cs.cs619.bulletzone.states;

import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.Background;

import edu.unh.cs.cs619.bulletzone.R;

public class TankButtons extends VehicleButtons {
    public TankButtons() {
        canSelectTank = false;
        canSelectMiner = true;
        canSelectBuilder = true;
    }
}
