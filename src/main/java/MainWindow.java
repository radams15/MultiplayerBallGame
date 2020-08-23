import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class MainWindow {
    private final int loopDelay = 0;
    private final int missileSpeed = 5;

    ArrayList<int[]> missiles = new ArrayList<>();
    boolean buttonClick = false;
    Network network;

    private final int[] size = new int[]{800, 600};

    public MainWindow(Network network){
        this.network = network;
    }

    public void run(){
        try {
            Display.setDisplayMode(new DisplayMode(size[0], size[1]));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 0, 600, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        while (!Display.isCloseRequested()) {
            if(loop()){
                break;
            }

            try {
                Thread.sleep(loopDelay);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Display.destroy();
    }

    private void drawPlayer(int x, int y, int w, int h){
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x,y);
        GL11.glVertex2f(x+w,y);
        GL11.glVertex2f(x+w,y+h);
        GL11.glVertex2f(x,y+h);
        GL11.glEnd();
    }

    private void drawMissile(int x, int y, int w, int h){
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x,y);
        GL11.glVertex2f(x+w,y);
        GL11.glVertex2f(x+w,y+h);
        GL11.glVertex2f(x,y+h);
        GL11.glEnd();
    }

    private void updateMissiles(){
        GL11.glColor3f(0.0f,1.0f,0.0f);

        int i=0;
        for(int[] missile : (ArrayList<int[]>) missiles.clone()){ // clone it to not have concurrentmodificationexception when deleting object
            if(missile.length == 4) {
                missile[0] += missileSpeed;
                if(missile[0] > size[0] || missile[1] > size[1]){
                    missiles.remove(i);
                }else {
                    drawMissile(missile[0], missile[1], missile[2], missile[3]);
                }
            }
            i++;
        }

    }

    private void updatePlayer(int x, int y){
        GL11.glColor3f(0.0f,1.0f,0.0f);

        drawPlayer(x, y, 50, 50);
    }

    private void addMissile(int x, int y){
        int[] missile = new int[]{x, y, 20, 20};
        missiles.add(missile);
    }

    private void sendData(){
        int size = missiles.size()+1;
        Serialise.Obj[] toSend = new Serialise.Obj[size]; // size+1 for missiles and player
        for(int i=0 ; i<size-1 ; i++){
            int[] missile = missiles.get(i);
            toSend[i] = new Serialise.Obj();
            toSend[i].x = missile[0];
            toSend[i].y = missile[1];
            toSend[i].type = Serialise.ObjectType.Missile;
        }
        toSend[size-1] = new Serialise.Obj();
        toSend[size-1].x = Mouse.getX();
        toSend[size-1].y = Mouse.getY();
        toSend[size-1].type = Serialise.ObjectType.Player;

        String sendStr = Serialise.manyToStr(toSend);

        network.write(sendStr);
    }

    private boolean overlap(int[] p1, int[] p2){
        if ((p1[2] > p2[2] || p2[0] > p1[2]) || (p1[3] < p2[1] || p2[3] < p1[1])) {
            return false;
        }
        return true;
    }

    private boolean receiveData(){
        int myX = Mouse.getX();
        int myY = Mouse.getY();
        String dataStr = network.read();
        if(dataStr.equals("FIN")){
            return true;
        }
        Serialise.Obj[] data = Serialise.strToMany(dataStr);

        GL11.glColor3f(1.0f,0.0f,0.0f);

        for(Serialise.Obj obj : data){
            int x = size[0]-obj.x;
            int y = size[1]-obj.y;

            int[] enemy = new int[]{x, y, x+20, y+20};
            int[] me = new int[]{myX, myY, myX+50, myY+50};
            if(obj.type == Serialise.ObjectType.Missile){
                drawMissile(x-20, y-20, 20, 20);

                if(overlap(enemy, me)){
                    network.write("FIN");
                    return true;
                }
            }else{
                drawPlayer(x-50, y-50, 50, 50);
            }
        }
        return false;
    }

    private boolean loop(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        int x = Mouse.getX();
        int y = Mouse.getY();

        if(Mouse.isButtonDown(0)){
            if(!buttonClick) {
                addMissile((x+x+50)/2, (y+y+50)/2);
                buttonClick = true;
            }
        }else{
            buttonClick = false;
        }

        sendData();
        if(receiveData()){
            return true;
        }

        updateMissiles();
        updatePlayer(x, y);

        Display.update();

        return false;
    }
}
