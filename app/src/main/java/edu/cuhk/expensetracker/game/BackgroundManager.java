package edu.cuhk.expensetracker.game;

import android.content.res.Resources;

import java.util.ArrayList;

public class BackgroundManager {
    private ArrayList<Background> m_Backgrounds;

    public void loadBackgrounds(int[] data, Resources res) {
        m_Backgrounds = new ArrayList<>();

        //loading the first placeholder section
        m_Backgrounds.add(new Background(0, 0, 0, res));

        //loading the rest of the sections
        int backgroundPosition = 1;
        int lastBackgroundType = 0;
        for(int i = 0; i < data.length; i++) {
            int animalCount = data[i];
            while(animalCount > 0) {
                m_Backgrounds.add(new Background(i / GameObject.NUMBER_OF_ANIMAL_TYPES_PER_HABITAT, Background.BACKGROUND_WIDTH * backgroundPosition, 0, res));
                backgroundPosition++;
                animalCount -= 10;
                lastBackgroundType = i / GameObject.NUMBER_OF_ANIMAL_TYPES_PER_HABITAT;
            }
        }
        //if no background is added, add a middle placeholder section
        if(backgroundPosition == 1) {
            m_Backgrounds.add(new Background(0, Background.BACKGROUND_WIDTH * backgroundPosition, 0, res));
            backgroundPosition++;
        }

        //loading the last placeholder section
        m_Backgrounds.add(new Background(lastBackgroundType, Background.BACKGROUND_WIDTH * backgroundPosition, 0, res));
    }

    public ArrayList<Background> getBackgrounds() {
        return m_Backgrounds;
    }
}
