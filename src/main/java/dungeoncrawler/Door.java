package dungeoncrawler;

public class Door extends Obstacle {

    boolean orientation;
    // Room goesTo;

    public Door(int x, int y, int h, int w /*, Room r */) {
        super(x, y, h, w);
        //this.goesTo = r;
    }

}
