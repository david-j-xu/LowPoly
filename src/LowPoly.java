import java.io.IOException;

public class LowPoly {
    
    public static void main(String[] args) throws IOException {
        LowPolyTransformer transformer = new LowPolyTransformer("lena.png");
        transformer.transformImage(30, 30);
        transformer.writeImage("period30");
    }
}
