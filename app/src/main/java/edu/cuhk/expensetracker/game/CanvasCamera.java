package edu.cuhk.expensetracker.game;

//yet to involve the y axis
public class CanvasCamera implements UpdatableObject {
    private float m_CanvasPositionX;
    private float m_TargetPositionX;
    private float m_Distance;
    private float m_CurrentSpeed;
    private boolean m_GoingRight;
    private boolean m_InMotion;
    private float m_ScreenSizeX;

    CanvasCamera(float initialCanvasPositionX, int screenSizeX) {
        m_CanvasPositionX = -(initialCanvasPositionX - screenSizeX / 2.0f);
        m_TargetPositionX = m_CanvasPositionX;
        m_GoingRight = false;
        m_InMotion = false;
        m_ScreenSizeX = (float) screenSizeX;
    }

    public void setCameraTargetPosition(float positionX) {
        positionX = -(positionX - m_ScreenSizeX / 2);
        m_Distance = positionX - m_CanvasPositionX;
        if(m_Distance < 0) {
            m_Distance = -m_Distance;
            m_GoingRight = true;
        }
        else {
            m_GoingRight = false;
        }
        m_TargetPositionX = positionX;
        m_InMotion = true;
    }

    public float getCanvasPositionX() {
        return m_CanvasPositionX;
    }

    public float getPivotPositionX() {
        return -m_CanvasPositionX + m_ScreenSizeX / 2;
    }

    public boolean isInMotion() {
        return m_InMotion;
    }

    @Override
    public void update(float dt) {
        if(m_InMotion) {
            m_CurrentSpeed = m_Distance * 8.0f;
            if(m_CurrentSpeed * dt >= m_Distance || m_Distance < 1.0f) {
                m_CanvasPositionX = m_TargetPositionX;
                m_InMotion = false;
            }
            else {
                if (m_GoingRight) {
                    m_CanvasPositionX -= m_CurrentSpeed * dt;
                } else {
                    m_CanvasPositionX += m_CurrentSpeed * dt;
                }
            }
            m_Distance = Math.abs(m_TargetPositionX - m_CanvasPositionX);
        }
    }
}
