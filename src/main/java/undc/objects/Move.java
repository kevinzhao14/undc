package undc.objects;

/**
 * Class that handles monster movement and gives them a delay.
 */
public class Move {
    private Coords pos;
    private double delay;

    public Move(Coords pos, double delay) {
        this.pos = pos;
        this.delay = delay;
    }

    public Coords getPos() {
        return pos;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }
}
