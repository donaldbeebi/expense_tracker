package edu.cuhk.expensetracker.game;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;

/**
 * optimize object later
 */

public class GameObjectManager {
    private ArrayList<ArrayList<GameObject>> m_AnimalGameObjectArray;
    private int m_TotalNumberOfAnimals;
    private int m_TotalNumberOfSections;
    private ArrayList<GameObject> m_HumanGameObjectArray;
    private ArrayList<Integer> m_BackgroundToAnimalList;

    public void loadGameObjects(int[] data, Resources res) {
        /*
         * *********************
         * ***LOADING ANIMALS***
         * *********************
         */
        m_BackgroundToAnimalList = new ArrayList<>();
        m_BackgroundToAnimalList.add(-1); //Because the first section is just a placeholder

        m_TotalNumberOfSections = 0;
        m_AnimalGameObjectArray = new ArrayList<>();
        Log.d("Inside GameObjectManager/data.length", String.valueOf(data.length));
        int sectionIndex = 1;
        for (int typeIndex = 0; typeIndex < data.length; typeIndex++) {
            if(data[typeIndex] > 0) {
                //counting the number of animals
                m_TotalNumberOfAnimals += data[typeIndex];

                //recording which type of animal lives here
                m_BackgroundToAnimalList.add(typeIndex);

                //creating new animals
                ArrayList<GameObject> animalGameObjects = new ArrayList<>();
                m_AnimalGameObjectArray.add(animalGameObjects);
                for (int animalIndex = 0; animalIndex < data[typeIndex]; animalIndex++) {
                    if (animalIndex % GameObject.MAX_NUMBER_OF_ANIMALS_PER_SECTION == 0 && animalIndex != 0) {
                        //recording which type of animal lives in the new section
                        m_BackgroundToAnimalList.add(typeIndex);

                        //new section for the same type of animal
                        animalGameObjects = new ArrayList<>();
                        m_AnimalGameObjectArray.add(animalGameObjects);
                        sectionIndex++;
                    }
                    FloatRect boundary = new FloatRect(Background.getAnimalBoundary(sectionIndex));
                    animalGameObjects.add(new GameObject(typeIndex, boundary, res));
                }
                //new section for a new type of animal
                sectionIndex++;
            }
        }
        m_TotalNumberOfSections = sectionIndex - 1;
        //if there are no animals
        if(m_TotalNumberOfAnimals == 0) {
            m_BackgroundToAnimalList.add(-1);
        }

        /*
         * *********************
         * ***LOADING HUMANS***
         * *********************
         */
        m_HumanGameObjectArray = new ArrayList<GameObject>();
        int totalNumberOfHumans = m_TotalNumberOfAnimals;
        FloatRect boundary = Background.getHumanBoundary(m_TotalNumberOfSections);
        for(int i = 0; i < totalNumberOfHumans; i++) {
            m_HumanGameObjectArray.add(new GameObject(10, boundary, res));
        }

    }

    public ArrayList<ArrayList<GameObject>> getAnimalGameObjectArray() {
        return m_AnimalGameObjectArray;
    }

    public ArrayList<GameObject> getHumanGameObjectArray() {
        return m_HumanGameObjectArray;
    }

    public ArrayList<Integer> getBackgroundToAnimalList() {
        return m_BackgroundToAnimalList;
    }

    public int getTotalNumberOfAnimals() {
        return m_TotalNumberOfAnimals;
    }
}
