package edu.unh.cs.cs619.bulletzone.model;

public enum Direction {
    Up, UpRight, Right, DownRight, Down,  DownLeft, Left, UpLeft;

    public static Direction fromByte(byte direction) throws IllegalArgumentException {
        if (direction < 0 || direction >= Direction.values().length) {
            throw new IllegalArgumentException("Invalid direction");
        }
        return Direction.values()[direction];
    }
    public static byte toByte(Direction direction) {
        return (byte)direction.ordinal();
    }
    public static Direction getRight(Direction currentDirection) {
        return Direction.values()[(currentDirection.ordinal() + 2) % 8];
    }
    public static Direction getBack(Direction currentDirection) {
        return Direction.values()[(currentDirection.ordinal() + 4) % 8];
    }
    public static Direction getLeft(Direction currentDirection) {
        return Direction.values()[(currentDirection.ordinal() + 6) % 8];
    }
}
