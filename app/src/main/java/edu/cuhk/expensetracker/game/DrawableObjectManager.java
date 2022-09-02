package edu.cuhk.expensetracker.game;

import android.graphics.Canvas;

import java.util.ArrayList;

public class DrawableObjectManager {
    GameObjectComparator m_Comparator;
    private ArrayList<Background> m_Backgrounds;
    private ArrayList<ArrayList<GameObject>> m_AnimalGameObjectArray;
    private ArrayList<GameObject> m_HumanGameObjectArray;

    DrawableObjectManager() {
        m_Comparator = new GameObjectComparator();
    }

    public void setBackgrounds(ArrayList<Background> backgrounds) {
        m_Backgrounds = backgrounds;
    }

    public void setGameObjects(ArrayList<ArrayList<GameObject>> animalGameObjectArray, ArrayList<GameObject> humanGameObjectArray) {
        m_AnimalGameObjectArray = animalGameObjectArray;
        m_HumanGameObjectArray = humanGameObjectArray;
    }

    public void draw(Canvas canvas) {
        //Drawing the backgrounds
        for(Background background: m_Backgrounds) {
            background.draw(canvas);
        }

        //Drawing the objects
        for(ArrayList<GameObject> array: m_AnimalGameObjectArray) {
            array.sort(m_Comparator);
            for(GameObject gameObject: array) {
                gameObject.draw(canvas);
            }
        }

        m_HumanGameObjectArray.sort(m_Comparator);
        for(GameObject gameObject: m_HumanGameObjectArray) {
            gameObject.draw(canvas);
        }
    }
}
