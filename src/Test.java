import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;


public class Test extends Storage implements StartPoint {

    @Shared
    int[] array;
    
    @Override
    public void main() throws Throwable {
        if (PCJ.myId() == 0) {
            array = new int[100];
            PCJ.waitFor("array", 1);
            for (int i : array) {
                System.out.println(i);
            }
        }
        
        if (PCJ.myId() == 1) {
            for (int i = 99; i >= 0; --i) {
                PCJ.put(0, "array", i, i);
            }
        }
        
        
    }

}
