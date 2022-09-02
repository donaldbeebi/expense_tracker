package edu.cuhk.expensetracker.game;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class GameView extends SurfaceView implements Runnable {

    private Thread m_Thread;
    private boolean m_Running;

    private long m_TimeNow;
    private long m_TimeLast;
    private double m_DeltaTime;
    DecimalFormat m_Formatter1; //#,###
    DecimalFormat m_Formatter2; //#,##0.0

    private final float m_CanvasScale;
    private int m_CurrentBackground;

    private final SharedPreferencesManager m_SPM;
    private final CanvasCamera m_CanvasCamera;
    private final BackgroundManager m_BackgroundManager;
    private final GameObjectManager m_GameObjectManager;
    private final UpdatableObjectManager m_UpdatableObjectManager;
    private final DrawableObjectManager m_DrawableObjectManager;
    private final CoinManager m_CoinManager;

    private final Button m_LeftButton;
    private final Button m_RightButton;
    private final TextView m_CoinsTextView;
    private final TextView m_BucksTextView;
    private final TextView m_AnimalNameTextView;
    private final TextView m_AnimalCPMTextView;
    private final TextView m_TotalCPMInfoTextView;


    //The constructor is called in onCreate() in GameActivity
    public GameView(Context context, int screenSizeX, int screenSizeY, Button leftButton, Button rightButton, TextView bucksTextView, TextView coinsTextView, TextView animalNameTextView, TextView animalCPMTextView, TextView totalCPMInfoTextView) {
        super(context);
        m_LeftButton = leftButton;
        m_RightButton = rightButton;
        m_BucksTextView = bucksTextView;
        m_CoinsTextView = coinsTextView;
        m_AnimalNameTextView = animalNameTextView;
        m_AnimalCPMTextView = animalCPMTextView;
        m_TotalCPMInfoTextView = totalCPMInfoTextView;
        m_Formatter1 = new DecimalFormat("#,###");
        m_Formatter2 = new DecimalFormat("#,##0.0");

        m_SPM = SharedPreferencesManager.getInstance();
        m_SPM.init(getContext().getSharedPreferences("edu.cuhk.expensetracker", Context.MODE_PRIVATE));

        //setting up the sprites and game objects
        int[] gameObjectData = m_SPM.getGameObjectData();

        m_BackgroundManager = new BackgroundManager();
        m_BackgroundManager.loadBackgrounds(gameObjectData, getResources());

        m_GameObjectManager = new GameObjectManager();
        m_GameObjectManager.loadGameObjects(gameObjectData, getResources());

        m_UpdatableObjectManager = new UpdatableObjectManager();
        m_UpdatableObjectManager.setGameObjects(m_GameObjectManager.getAnimalGameObjectArray(), m_GameObjectManager.getHumanGameObjectArray());

        m_DrawableObjectManager = new DrawableObjectManager();
        m_DrawableObjectManager.setBackgrounds(m_BackgroundManager.getBackgrounds());
        m_DrawableObjectManager.setGameObjects(m_GameObjectManager.getAnimalGameObjectArray(), m_GameObjectManager.getHumanGameObjectArray());

        //setting up the camera and info
        m_CurrentBackground = 1;
        updateLeftRightButton();
        updateAnimalName();
        updateAnimalCPM();
        m_CanvasScale = (float) screenSizeY / (float) Background.BACKGROUND_HEIGHT;
        m_CanvasCamera = new CanvasCamera(Background.getCameraPositionX(m_CurrentBackground), screenSizeX);

        //setting up the currency
        m_CoinManager = CoinManager.getInstance();
        m_CoinManager.initialize(m_SPM.getCoins(), m_SPM.getGameObjectData());
        //free coins!
        //m_CoinManager.initialize(200_000.0f, m_SPM.getGameObjectData());
        m_CoinManager.compensate(m_SPM.getTimeLastOpened());
        m_TotalCPMInfoTextView.post(new Runnable() {
            @Override
            public void run() {
                m_TotalCPMInfoTextView.setText(m_Formatter2.format(m_CoinManager.getCPM()));
            }
        });

        m_LeftButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_CanvasCamera.setCameraTargetPosition(Background.getCameraPositionX(--m_CurrentBackground));
                updateAnimalName();
                updateAnimalCPM();
                updateLeftRightButton();
            }
        });

        m_RightButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_CanvasCamera.setCameraTargetPosition(Background.getCameraPositionX(++m_CurrentBackground));
                updateAnimalName();
                updateAnimalCPM();
                updateLeftRightButton();
            }
        });

        if(m_GameObjectManager.getTotalNumberOfAnimals() == 0) {
            Toast.makeText(getContext(), "You don't have any animals. Click the wheel button in the top right corner!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void run() {
        m_TimeLast = System.nanoTime();

        //the main game loop
        while(m_Running) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        m_TimeNow = System.nanoTime();
        m_DeltaTime = (m_TimeNow - m_TimeLast) / 1_000_000_000.0d;
        m_TimeLast = m_TimeNow;
        float dt = (float) m_DeltaTime;

        m_UpdatableObjectManager.update(dt);
        m_CanvasCamera.update(dt);
        m_CoinManager.update(dt);
        m_CoinsTextView.post(new Runnable() {
            @Override
            public void run() {
                m_CoinsTextView.setText(m_Formatter1.format((int) m_CoinManager.getTotalNumberOfCoins()));
            }
        });
    }

    private void draw() {
        if(getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.translate(m_CanvasCamera.getCanvasPositionX(), 0.0f);
            canvas.scale(m_CanvasScale, m_CanvasScale, m_CanvasCamera.getPivotPositionX(), 0.0f);
            m_DrawableObjectManager.draw(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(long timeLastPaused) {
        //updating the currency number
        m_CoinManager.compensate(timeLastPaused);
        m_BucksTextView.post(new Runnable() {
            @Override
            public void run() {
                m_BucksTextView.setText(m_Formatter1.format(m_SPM.getBucks()));
            }
        });
        m_Running = true;
        m_Thread = new Thread(this);
        m_Thread.start();
    }

    public void pause() {
        m_SPM.saveCoinInfo(m_CoinManager.getTotalNumberOfCoins(), System.nanoTime());
        try {
            m_Running = false;
            m_Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //this is called every time it resumes from the spinning wheel transaction activity to the game activity
    public void updateGameStates() {
        int[] gameObjectData = m_SPM.getGameObjectData();
        m_BackgroundManager.loadBackgrounds(gameObjectData, getResources());
        m_GameObjectManager.loadGameObjects(gameObjectData, getResources());
        m_UpdatableObjectManager.setGameObjects(m_GameObjectManager.getAnimalGameObjectArray(), m_GameObjectManager.getHumanGameObjectArray());
        m_DrawableObjectManager.setBackgrounds(m_BackgroundManager.getBackgrounds());
        m_DrawableObjectManager.setGameObjects(m_GameObjectManager.getAnimalGameObjectArray(), m_GameObjectManager.getHumanGameObjectArray());
        updateAnimalName();
        updateAnimalCPM();
        updateLeftRightButton();
        m_CoinManager.updateAnimalNumberList(gameObjectData);
        m_BucksTextView.post(new Runnable() {
            @Override
            public void run() {
                m_BucksTextView.setText(m_Formatter1.format(m_SPM.getBucks()));
            }
        });
        m_TotalCPMInfoTextView.post(new Runnable() {
            @Override
            public void run() {
                m_TotalCPMInfoTextView.setText(m_Formatter2.format(m_CoinManager.getCPM()));
            }
        });
    }

    private void updateAnimalName() {
        String name;
        if(m_GameObjectManager.getTotalNumberOfAnimals() > 0) {
            //if there are animals
            int type = m_GameObjectManager.getBackgroundToAnimalList().get(m_CurrentBackground);
            name = GameObject.ANIMAL_TYPES[type / GameObject.NUMBER_OF_ANIMAL_TYPES_PER_HABITAT][type % GameObject.NUMBER_OF_ANIMAL_TYPES_PER_HABITAT];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        else name = "Empty";
        String finalName = name;

        m_AnimalNameTextView.post(new Runnable() {
            @Override
            public void run() {
                m_AnimalNameTextView.setText(finalName);
            }
        });
    }

    private void updateAnimalCPM() {
        String cpm;
        if(m_GameObjectManager.getTotalNumberOfAnimals() > 0) {
            //if there are animals
            int type = m_GameObjectManager.getBackgroundToAnimalList().get(m_CurrentBackground);
            cpm = m_Formatter2.format(CoinManager.ANIMAL_RATE_LIST[type]);
        }
        //if there are no animals
        else cpm = "0";

        m_AnimalCPMTextView.post(new Runnable() {
            @Override
            public void run() {
                m_AnimalCPMTextView.setText(cpm);
            }
        });
    }

    private void updateLeftRightButton() {
        if(m_CurrentBackground == 1) {
            m_LeftButton.setEnabled(false);
        }
        else m_LeftButton.setEnabled(true);
        if(m_CurrentBackground == m_BackgroundManager.getBackgrounds().size() - 2) {
            m_RightButton.setEnabled(false);
        }
        else m_RightButton.setEnabled(true);
    }
}
