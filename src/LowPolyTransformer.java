import java.io.File;
import java.util.Random;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class LowPolyTransformer {
    private static Random rand = new Random();
    private int[][][] matrix;
    private int width;
    private int height;

    public LowPolyTransformer(String filename) throws IOException {
        BufferedImage buffer = ImageIO.read(new File(filename));
        width = buffer.getWidth(null);
        height = buffer.getHeight(null);
        
        matrix = new int[3][width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = buffer.getRGB(i, j);
                setRGB(i,j, rgb);
            }
        }
    }
    
    public void transformImage(int xPeriod, int yPeriod) {
        // create new array storing points chosen from grid going from (-xPeriod, -yPeriod) to (width + xPeriod, height + yPeriod)
        int xGridLength = width / xPeriod + 2;
        int yGridLength = height / yPeriod + 2;
        
        int[][][] points = new int[2][xGridLength][yGridLength];
        
        for (int i = 0; i < xGridLength; i++) {
            for (int j = 0; j < yGridLength; j++) {
                points[0][i][j] = xPeriod * (i - 1) + rand.nextInt(xPeriod);
                points[1][i][j] = yPeriod * (j - 1) + rand.nextInt(yPeriod);
            }
        }
        
        for (int i = 1; i < xGridLength - 1; i++) {
            for (int j = 1; j < yGridLength - 1; j++) {
                int x = points[0][i][j];
                int y = points[1][i][j];
                int upX = points[0][i][j - 1];
                int upY = points[1][i][j - 1];
                int rightX = points[0][i + 1][j];
                int rightY = points[1][i + 1][j];
                int leftX = points[0][i - 1][j];
                int leftY = points[1][i - 1][j];
                int downX = points[0][i][j + 1];
                int downY = points[1][i][j + 1];
                
                transformTriangle(x, y, upX, upY, rightX, rightY);
                transformTriangle(x, y, upX, upY, leftX, leftY);
                transformTriangle(x, y, downX, downY, rightX, rightY);
                transformTriangle(x, y, downX, downY, leftX, leftY);
            }
        }
    }
    
    public void transformTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        int xmin = Math.min(x1, Math.min(x2, x3));
        int ymin = Math.min(y1, Math.min(y2, y3));
        int xmax = Math.max(x1, Math.max(x2, x3));
        int ymax = Math.max(y1, Math.max(y2, y3));
        
        long avgR = 0;
        long avgG = 0;
        long avgB = 0;
        int count = 0;
        
        for (int i = xmin; i <= xmax; i++) {
            for (int j = ymin; j <= ymax; j++) {
                if (inTriangle(i, j, x1, y1, x2, y2, x3, y3) && inBounds(i, j)) {
                    avgR += matrix[0][i][j];
                    avgG += matrix[1][i][j];
                    avgB += matrix[2][i][j];
                    count++;
                }
                
            }
        }
        
        avgR /= count;
        avgG /= count;
        avgB /= count;
        
        for (int i = xmin; i <= xmax; i++) {
            for (int j = ymin; j <= ymax; j++) {
                if (inTriangle(i, j, x1, y1, x2, y2, x3, y3) && inBounds(i, j)) {
                    matrix[0][i][j] = (int) avgR;
                    matrix[1][i][j] = (int) avgG;
                    matrix[2][i][j] = (int) avgB;
                }
                
            }
        }
    }
    
    private int getBarySign(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
    }
    
    private boolean inTriangle(int i, int j, int x1, int y1, int x2, int y2, int x3, int y3) {
        int sign1 = getBarySign(i, j, x1, y1, x2, y2);
        int sign2 = getBarySign(i, j, x2, y2, x3, y3);
        int sign3 = getBarySign(i, j, x3, y3, x1, y1);
        
        boolean neg = (sign1 < 0) || (sign2 < 0) || (sign3 < 0);
        boolean pos = (sign1 > 0) || (sign2 > 0) || (sign3 > 0);
        
        return !(neg && pos);
    }
    
    public void writeImage(String outputName) {
        try {
            BufferedImage output = new BufferedImage(width, 
                                                        height, 
                                                        BufferedImage.TYPE_INT_RGB);
            int curr;
            for (int i = 0; i < output.getWidth(); i++) {
                for (int j = 0; j < output.getHeight(); j++) {
                    curr = bitShift(matrix[0][i][j], matrix[1][i][j], matrix[2][i][j]);
                    output.setRGB(i, j, curr);
                }
            }
            
            File outputFile = new File("./output/" + outputName + ".png");
            ImageIO.write(output, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int[][][] getMatrix() {
        return matrix;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public void setRGB(int i, int j, int rgb) {
        matrix[0][i][j] = (rgb >> 16) & 0xFF;
        matrix[1][i][j] = (rgb >> 8) & 0xFF;
        matrix[2][i][j] = rgb & 0xFF;
    }
    
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                output.append("(" + Integer.toString(matrix[0][i][j]) + ", " +
                        Integer.toString(matrix[1][i][j]) + ", " +
                        Integer.toString(matrix[2][i][j]) + ") ") ;
            }
            output.append("\n");
        }
        return output.toString();
    }
    
    public static int bitShift(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }   
}
