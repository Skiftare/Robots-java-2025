package model.object;

import model.object.abstractions.Word;

import java.awt.*;

// Keep your existing Operator and Property classes but extend from Word
public class OperatorWord extends Word {
    public OperatorWord(String name, Color color) {
        super(name, color);
    }
}
