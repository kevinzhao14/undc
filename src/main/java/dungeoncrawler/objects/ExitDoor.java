package dungeoncrawler.objects;

public class ExitDoor extends Door {
    public ExitDoor(int x, int y, int w, int h) {
        super(x, y, w, h, null, DoorOrientation.TOP);
    }
}
