package edu.unh.cs.cs619.bulletzone.model.entities;

public class ResourceItem extends Item{

    private ResourceType resourceType;

    public ResourceItem(ResourceType resourceType) {
        super(resourceType.getID(), resourceType.getCreditValue());
        this.resourceType = resourceType;
    }

    @Override
    public FieldEntity copy() {
        return null;
    }

    @Override
    public void pickup(VehicleEntity vehicleEntity) {
        vehicleEntity.game.addResource(vehicleEntity.getUsername(), this.resourceType, this.resourceType.getResourceValue());
    }

    public static ResourceItem getRandomResource() {
        int id = (int) (Math.random() * 4);
        // 0 = clay, 1 = rock, 2 = iron, 3 = wood
        if (id == 0) {
            return new ResourceItem(ResourceType.CLAY);
        } else if (id == 1) {
            return new ResourceItem(ResourceType.ROCK);
        } else if (id == 2) {
            return new ResourceItem(ResourceType.IRON);
        } else {
            return new ResourceItem(ResourceType.WOOD);
        }
    }
}
