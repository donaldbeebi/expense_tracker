package edu.cuhk.expensetracker.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.Random;

import edu.cuhk.expensetracker.R;

public class GameObject implements DrawableObject, UpdatableObject, Comparable<GameObject> {
    public static final int MAX_NUMBER_OF_ANIMALS_PER_SECTION = 10;
    public static final int TOTAL_NUMBER_OF_ANIMAL_TYPES = 9;
    public static final int NUMBER_OF_ANIMAL_TYPES_PER_HABITAT = 3;
    public static final String[][] ANIMAL_TYPES = new String[][] {
            { "rabbit", "cow", "unicorn" }, //farm
            { "giraffe", "lion", "gryphon" }, //savanna
            { "gorilla", "panda", "lemur" } //jungle
    };

    public static final float[][] SPEED = new float[][] {
            { 30.0f, 15.0f, 100.0f },
            { 20.0f, 25.0f, 70.0f },
            { 10.0f, 5.0f, 50.0f }
    };

    public static final float[][] ROTATION_SPEED = new float[][] {
            { 110.0f, 40.0f, 60.0f },
            { 50.0f, 40.0f, 60.0f },
            { 30.0f, 30.0f, 110.0f }
    };

    public static final float[][] MAX_DEGREE = new float[][] {
            { 6.0f, 5.0f, 10.0f },
            { 5.0f, 5.0f, 7.0f },
            { 7.0f, 3.0f, 6.0f }
    };

    //private static final float MAX_DEGREE = 10.0f;
    //private static final float ROTATION_SPEED = 60.0f;
    //private static final float SPEED = 100.0f;

    private static final int SPRITE_DIMENSION = 96;
    private Bitmap m_Bitmap;

    private final int m_Type;
    private float m_PositionX;
    private float m_PositionY;
    private float m_VelocityX;
    private float m_VelocityY;

    private final float m_Speed;
    private final float m_RotationSpeed;
    private final float m_MaxDegree;
    private float m_Rotation;
    private boolean m_RotatingRight;
    private boolean m_InMotion;
    private float m_ScaleX;

    private int m_CurrentStepNumber;
    private int m_MaxStepNumber;

    private float m_CurrentIdleTime;
    private float m_MaxIdleTime;

    private final FloatRect m_Boundary;

    GameObject(int type, FloatRect boundary, Resources res) {
        m_Type = type;
        //loading bitmap and attributes based on type
        if(type == 10) {
            //if it is a human
            Random random = new Random();
            int x = random.nextInt(8) * SPRITE_DIMENSION;
            m_Bitmap = BitmapFactory.decodeResource(res, R.drawable.human_spritesheet);
            m_Bitmap = Bitmap.createBitmap(m_Bitmap, x, 0, SPRITE_DIMENSION, SPRITE_DIMENSION);
            m_Speed = 60.0f;
            m_RotationSpeed = 120.0f;
            m_MaxDegree = 5.0f;
        }
        else {
            //if it is an animal
            int x = (type % NUMBER_OF_ANIMAL_TYPES_PER_HABITAT) * SPRITE_DIMENSION;
            int y = (type / NUMBER_OF_ANIMAL_TYPES_PER_HABITAT) * SPRITE_DIMENSION;
            m_Bitmap = BitmapFactory.decodeResource(res, R.drawable.animal_spritesheet);
            m_Bitmap = Bitmap.createBitmap(m_Bitmap, x, y, SPRITE_DIMENSION, SPRITE_DIMENSION);
            m_Speed = SPEED[m_Type / NUMBER_OF_ANIMAL_TYPES_PER_HABITAT][m_Type % NUMBER_OF_ANIMAL_TYPES_PER_HABITAT];
            m_RotationSpeed = ROTATION_SPEED[m_Type / NUMBER_OF_ANIMAL_TYPES_PER_HABITAT][m_Type % NUMBER_OF_ANIMAL_TYPES_PER_HABITAT];
            m_MaxDegree = MAX_DEGREE[m_Type / NUMBER_OF_ANIMAL_TYPES_PER_HABITAT][m_Type % NUMBER_OF_ANIMAL_TYPES_PER_HABITAT];
        }

        m_VelocityX = 0.0f;
        m_VelocityY = 0.0f;

        m_Rotation = 0.0f;
        m_RotatingRight = true;
        m_InMotion = false;
        if(Math.random() > 0.5d) m_ScaleX = -1.0f;
        else m_ScaleX = 1.0f;

        m_Boundary = new FloatRect(
                boundary.left,
                boundary.top - m_Bitmap.getHeight() / 2.0f,
                boundary.right - m_Bitmap.getWidth(),
                boundary.bottom - m_Bitmap.getHeight());
        m_PositionX = (float) Math.random() * (m_Boundary.right - m_Boundary.left) + m_Boundary.left;
        m_PositionY = (float) Math.random() * (m_Boundary.bottom - m_Boundary.top) + m_Boundary.top;

        randomize();
    }

    private void randomize() {
        /*
         * Randomizing the walking direction
         */
        float direction = (float) (Math.random() * 360.0d);
        m_VelocityX = m_Speed * (float) Math.cos(Math.toRadians(direction));
        m_VelocityY = m_Speed * (float) Math.sin(Math.toRadians(direction));

        /*
         * Randomizing the number of steps to take
         */
        m_MaxStepNumber = (int) (Math.random() * 9.0d) + 1;

        /*
         * Randomizing the idle time
         */
        m_MaxIdleTime = (float) (Math.random() * 10.0d);
    }

    @Override
    public void update(float dt) {
        if(m_InMotion) {
            //face the correct direction
            if(m_VelocityX > 0.0f) m_ScaleX = -1.0f;
            else m_ScaleX = 1.0f;
            if(m_RotatingRight) {
                //counting the number of steps
                if(m_Rotation < 0.0f && m_Rotation + m_RotationSpeed * dt > 0.0f) m_CurrentStepNumber++;
                //time to go idle
                if(m_CurrentStepNumber == m_MaxStepNumber) {
                    m_InMotion = false;
                    m_CurrentStepNumber = 0;
                    m_Rotation = 0.0f;
                    randomize();
                }
                //time to swing to the right
                else {
                    m_Rotation += m_RotationSpeed * dt;
                    if(m_Rotation > m_MaxDegree) {
                        //float excess = m_Rotation - m_MaxDegree;
                        //m_RotatingRight = false;
                        //m_Rotation = m_MaxDegree - excess;
                        m_Rotation = m_MaxDegree;
                        m_RotatingRight = false;
                    }
                }
            }
            //time to swing to the left
            else {
                m_Rotation -= m_RotationSpeed * dt;
                if(m_Rotation < -m_MaxDegree) {
                    //float excess = -(m_MaxDegree + m_Rotation);
                    //m_RotatingRight = true;
                    //m_Rotation = -m_MaxDegree + excess;
                    m_Rotation = -m_MaxDegree;
                    m_RotatingRight = true;
                }
            }
            //strolling around while detecting collision
            m_PositionX = Math.max(m_Boundary.left, Math.min(m_Boundary.right, m_PositionX + m_VelocityX * dt));
            m_PositionY = Math.max(m_Boundary.top, Math.min(m_Boundary.bottom, m_PositionY + m_VelocityY * dt));
        }
        //idling
        if(!m_InMotion) {
            m_CurrentIdleTime += dt;
            if(m_CurrentIdleTime > m_MaxIdleTime) {
                m_CurrentIdleTime = 0.0f;
                m_InMotion = true;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.setRotate(m_Rotation, m_Bitmap.getWidth() / 2.0f, m_Bitmap.getHeight());
        matrix.postScale(m_ScaleX, 1.0f, m_Bitmap.getWidth() / 2.0f, m_Bitmap.getHeight());
        matrix.postTranslate(m_PositionX, m_PositionY);
        canvas.drawBitmap(m_Bitmap, matrix, null);
    }

    @Override
    public int compareTo(GameObject gameObject) {
        return Float.compare(this.getPhysicalCoordinateY(), gameObject.getPhysicalCoordinateY());
    }

    public float getPhysicalCoordinateX() {
        return m_PositionX + m_Bitmap.getWidth() / 2.0f;
    }

    public float getPhysicalCoordinateY() {
        return m_PositionY + m_Bitmap.getHeight();
    }
}
