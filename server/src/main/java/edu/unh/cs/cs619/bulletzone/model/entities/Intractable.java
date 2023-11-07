package edu.unh.cs.cs619.bulletzone.model.entities;

public interface Intractable {
    public void pickup(VehicleEntity vehicleEntity);
    public void destroy();
    public long getIntValue();
}
