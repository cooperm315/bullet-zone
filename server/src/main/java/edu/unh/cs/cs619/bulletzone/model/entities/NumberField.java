package edu.unh.cs.cs619.bulletzone.model.entities;

/**
 * @author Bence Cserna (bence@cserna.net)
 */

public class NumberField extends StaticEntity {

    private static final String TAG = "NumberField";
    private final int value;

    public NumberField(int value) {
        this.value = value;
    }

    @Override
    public long getIntValue() {
        return 0;
    }

    @Override
    public FieldEntity copy() {
        return null;
    }

    @Override
    public String toString() {
        return Integer.toString(value == 1000 ? 1 : 2);
    }

}
