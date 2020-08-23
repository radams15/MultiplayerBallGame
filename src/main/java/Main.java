import java.io.File;

public class Main {
    public static void main(String[] args){
        System.setProperty("org.lwjgl.librarypath", new File("target/natives").getAbsolutePath());
        new Main().init(args);
    }

    public void init(String[] args){
        Network network = new Network("127.0.0.1", 12349);

        MainWindow window = new MainWindow(network);
        window.run();

        network.close();
    }
}
