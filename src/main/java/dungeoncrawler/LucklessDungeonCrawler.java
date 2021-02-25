package dungeoncrawler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LucklessDungeonCrawler extends Application {
    private static LucklessDungeonCrawler INSTANCE;

    private Stage stage;
    private Scene scene;

    public void start(Stage stage) {
        INSTANCE = this;

        this.stage = stage;
        this.scene = new Scene(new Pane());

        stage.setTitle("Luckless Dungeon Crawler");
        setScene(scene);

        stage.show();
    }

    /**
     * Used to change the scene.
     * @param scene scene to change to
     */
    public static void setScene(Scene scene) {
        INSTANCE.stage.setScene(scene);
    }
}
