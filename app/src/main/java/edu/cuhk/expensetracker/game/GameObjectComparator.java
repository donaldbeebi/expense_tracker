package edu.cuhk.expensetracker.game;

import java.util.Comparator;

public class GameObjectComparator implements Comparator<GameObject> {
    @Override
    public int compare(GameObject gameObject1, GameObject gameObject2) {
        return gameObject1.compareTo(gameObject2);
    }
}
