package game.model;


public enum ObjectProperty {
    PLAYER,    // Объект управляется игроком
    PUSHABLE,  // Объект можно толкать
    STOP,      // Объект нельзя пройти
    WIN,       // Объект представляет собой цель
    KILL       // Объект убивает игрока при контакте
}