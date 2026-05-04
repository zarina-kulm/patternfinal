package com.thrones.patterns.patterns.observer;

public interface GameEventObserver {
    void onEvent(GameEventType type, Object data);
}
