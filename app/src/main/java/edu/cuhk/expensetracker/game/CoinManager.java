package edu.cuhk.expensetracker.game;

public class CoinManager {
    private static final CoinManager m_Instance = new CoinManager();

    public static final float[] HABITAT_PRICES = new float[] {
            0.0f, //savanna
            50_000.0f, //safari
            500_000.0f //wetland
    };

    private float m_TotalNumberOfCoins;
    private int[] m_AnimalNumberList; //may not be needed
    private float m_CPM;
    public static final float[] ANIMAL_RATE_LIST = new float[] {
            //per minute
            0.5f,   //rabbit
            1.0f,   //cow
            2.0f,   //unicorn

            1.5f,   //giraffe
            3.0f,   //lion
            6.0f,  //gryphon

            3.0f,   //gorilla
            9.0f,  //panda
            18.0f,  //lemur
    };

    //CoinManager(float currentCoins, int[] data) {
    //    m_TotalNumberOfCoins = currentCoins;
    //    m_AnimalNumberList = data;
    //}

    public static CoinManager getInstance() {
        return m_Instance;
    }

    public void initialize(float currentCoins, int[] data) {
        m_CPM = 0;
        m_TotalNumberOfCoins = currentCoins;
        m_AnimalNumberList = data;
        for(int i = 0; i < GameObject.TOTAL_NUMBER_OF_ANIMAL_TYPES; i++) {
            m_CPM += (float) m_AnimalNumberList[i] * (ANIMAL_RATE_LIST[i]);
        }
    }

    public void updateAnimalNumberList(int[] data) {
        m_AnimalNumberList = data;
        m_CPM = 0;
        for(int i = 0; i < GameObject.TOTAL_NUMBER_OF_ANIMAL_TYPES; i++) {
            m_CPM += (float) m_AnimalNumberList[i] * (ANIMAL_RATE_LIST[i]);
        }
    }

    public void update(float dt) {
        //for(int i = 0; i < GameObject.TOTAL_NUMBER_OF_ANIMAL_TYPES; i++) {
        //    m_TotalNumberOfCoins += (float) m_AnimalNumberList[i] * ((ANIMAL_RATE_LIST[i] / 60.0f) * dt);
        //}
        m_TotalNumberOfCoins += (m_CPM / 60.0f) * dt;
    }

    public void compensate(long timeLastPaused) {
        double dt = (System.nanoTime() - timeLastPaused) / 1_000_000_000.0d;
        update((float) dt);
    }

    public boolean deduct(float amount) {
        if(amount > m_TotalNumberOfCoins) {
            return false;
        }
        m_TotalNumberOfCoins -= amount;
        return true;
    }

    public float getTotalNumberOfCoins() {
        return m_TotalNumberOfCoins;
    }

    public float getCPM() {
        return m_CPM;
    }
}
