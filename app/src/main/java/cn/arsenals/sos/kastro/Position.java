package cn.arsenals.sos.kastro;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Objects;

public class Position {
    private Point point;
    private Rect screenRect;

    public Position(Point point, Rect screenRect) {
        this.point = point;
        this.screenRect = screenRect;
    }

    public Position(int x, int y, int screenWidth, int screenHeight) {
        this(new Point(x, y), new Rect(0, 0, screenWidth, screenHeight));
    }

    public Point getPoint() {
        return point;
    }

    public Rect getScreenRect() {
        return screenRect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return Objects.equals(point, position.point)
                && Objects.equals(screenRect, position.screenRect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, screenRect);
    }

    @Override
    public String toString() {
        return "Position{"
                + "point=" + point
                + ", screenRect=" + screenRect
                + '}';
    }

}
