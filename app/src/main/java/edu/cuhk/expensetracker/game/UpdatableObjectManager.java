package edu.cuhk.expensetracker.game;

import java.util.ArrayList;

public class UpdatableObjectManager {
    private ArrayList<ArrayList<GameObject>> m_AnimalGameObjectArray;
    private ArrayList<GameObject> m_HumanGameObjectArray;

    public void setGameObjects(ArrayList<ArrayList<GameObject>> animalGameObjectArray, ArrayList<GameObject> humanGameObjectArray) {
        m_AnimalGameObjectArray = animalGameObjectArray;
        m_HumanGameObjectArray = humanGameObjectArray;
    }

    public void update(float dt) {
        for(ArrayList<GameObject> array: m_AnimalGameObjectArray) {
            for(GameObject gameObject: array) {
                gameObject.update(dt);
            }
        }
        for(GameObject gameObject: m_HumanGameObjectArray) {
            gameObject.update(dt);
        }
    }
}
