package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;

public abstract class MovableEntity extends FieldEntity{

    protected Game game;

    protected Direction direction;

    /**
     * This method is called when this entity collides with another entity
     * @param other the entity that this entity collided with
     * @return true if the collision allows for the entity to move, false otherwise
     */
    public abstract boolean collide(FieldEntity other);

    /**
     * This method checks if the entity will collide with another entity in the given direction
     * @param direction the direction to check
     */
    public boolean isColliding(Direction direction){
        FieldHolder parent = getParent();
        FieldHolder next = parent.getNeighbor(direction);

        if(next.isPresent()){
            return true;
        }
        return false;
    }

    /**
     * This method moves the entity in the given direction
     * @param direction the direction to move the entity
     * @return true if the entity moved, false otherwise
     */
    public abstract boolean move(Direction direction);

    /**
     * This method turns the entity in the given direction
     * @param direction the direction to turn the entity
     * @return true if the entity turned, false otherwise
     *
     * Non abstract due to some MovableEntities not needing to turn (i.e. bullets)
     */
    public boolean turn(Direction direction){
        return true;
    }

    public Direction getDirection() {
        return direction;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
