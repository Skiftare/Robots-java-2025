package game.mods.extensions_points;

import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.formula.Formula;

import java.awt.*;

public interface IGameMechanic {
    void onObjectDestroyed(GameObject destroyedObject, MovementHandler handler);
    void afterMovement(MovementHandler handler);
    void beforeMovement(MovementHandler handler);
    void onFormulaProcessed(Formula formula, MovementHandler handler);
}