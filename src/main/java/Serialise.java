import java.util.ArrayList;

public class Serialise {
    enum ObjectType{
        Player,
        Missile
    }

    static class Obj {
        int x;
        int y;
        ObjectType type;
    }

    static String manyToStr(Obj[] objs){
        StringBuilder sb = new StringBuilder();
        for(Obj obj : objs){
            sb.append(objToStr(obj));
            sb.append("$");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    static Obj[] strToMany(String data){
        ArrayList<Obj> out = new ArrayList<>();
        for(String objStr : data.split("\\$")){
            out.add(strToObj(objStr));
        }
        Obj[] outArray = new Obj[out.size()];
        out.toArray(outArray);
        return outArray;
    }

    static String objToStr(Obj obj){
        char objChr;
        if(obj.type == ObjectType.Player){
            objChr = 'p';
        }else if(obj.type == ObjectType.Missile){
            objChr = 'm';
        }else{
            objChr = 0;
        }

        return String.format("%d;%d;%d", obj.x, obj.y, (int)objChr);
    }

    static Obj strToObj(String in){
        String[] parsed = in.split(";");
        Obj data = new Obj();
        data.x = Integer.parseInt(parsed[0]);
        data.y = Integer.parseInt(parsed[1]);
        char typeChr = (char) Integer.parseInt(parsed[2]);
        if(typeChr == 'p'){
            data.type = ObjectType.Player;
        }else if(typeChr == 'm'){
            data.type = ObjectType.Missile;
        }else{
            data.type = null;
        }

        return data;
    }
}
