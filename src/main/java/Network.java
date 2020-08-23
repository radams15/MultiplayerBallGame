import java.io.*;
import java.net.Socket;

public class Network {
    private Socket socket;
    private PrintWriter writer;
    private InputStreamReader reader;

    public Network(String host, int port){
        try {
            socket = new Socket(host, port);

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            reader = new InputStreamReader(input);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void write(String data){
        writer.println(data);
        writer.flush();
    }

    public String read(){
        BufferedReader r = new BufferedReader(reader);
        try {
            return r.readLine();    // reads a line of text
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public int close(){
        try {
            socket.close();
            return 0;
        }catch(IOException e){
            e.printStackTrace();
            return 1;
        }
    }

}
