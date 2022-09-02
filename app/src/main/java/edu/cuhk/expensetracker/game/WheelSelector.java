package edu.cuhk.expensetracker.game;

public class WheelSelector {
    private int m_SelectedWheel;

    public WheelSelector() {
        m_SelectedWheel = 0;
    }

    public void selectWheel(int wheel) {
        m_SelectedWheel = wheel;
    }

    public int getSelectedWheel() {
        return m_SelectedWheel;
    }
}
