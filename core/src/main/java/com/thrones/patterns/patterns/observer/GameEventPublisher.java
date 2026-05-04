package com.thrones.patterns.patterns.observer;

public interface GameEventPublisher {
    void addObserver(GameEventObserver observer);
    void removeObserver(GameEventObserver observer);
    void notifyObservers(GameEventType type, Object data);
}
