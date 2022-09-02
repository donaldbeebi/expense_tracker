package edu.cuhk.expensetracker.game;

public class FloatRect {
    public float left;
    public float top;
    public float right;
    public float bottom;

    FloatRect() {
        left = 0.0f;
        top = 0.0f;
        right = 0.0f;
        bottom = 0.0f;
    }

    FloatRect(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    FloatRect(FloatRect floatRect) {
        this.left = floatRect.left;
        this.top = floatRect.top;
        this.right = floatRect.right;
        this.bottom = floatRect.bottom;
    }
}
