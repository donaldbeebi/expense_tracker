package edu.cuhk.expensetracker.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import edu.cuhk.expensetracker.R;

public class Background implements DrawableObject {
    public static final int BACKGROUND_WIDTH = 480;
    public static final int BACKGROUND_HEIGHT = 1056;
    public static final int SECTOR_HEIGHT = 864 - 168;
    public static final String[] HABITAT_TYPES = new String[] { "farm", "savanna", "jungle" };

    public static FloatRect getAnimalBoundary(int index) {
        return new FloatRect(index * BACKGROUND_WIDTH + 6, 48.0f, index * BACKGROUND_WIDTH + 473.0f, 770.0f - 144.0f);
    }

    public static FloatRect getHumanBoundary(int sectors) {
        return new FloatRect(0.0f, SECTOR_HEIGHT, BACKGROUND_WIDTH * (sectors + 2), BACKGROUND_HEIGHT);
    }

    public static int getCameraPositionX(int index) {
        return index * BACKGROUND_WIDTH + BACKGROUND_WIDTH / 2;
    }

    private final int m_PositionX, m_PositionY;
    private Bitmap m_Bitmap;

    Background(int type, int positionX, int positionY, Resources res) {
        m_PositionX = positionX;
        m_PositionY = positionY;

        switch(type) {
            case 0:
                m_Bitmap = BitmapFactory.decodeResource(res, R.drawable.background_0);
                break;
            case 1:
                m_Bitmap = BitmapFactory.decodeResource(res, R.drawable.background_1);
                break;
            case 2:
                m_Bitmap = BitmapFactory.decodeResource(res, R.drawable.background_2);
                break;
        }
        m_Bitmap = Bitmap.createBitmap(m_Bitmap);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(m_Bitmap, m_PositionX, m_PositionY, null);
    }

}
