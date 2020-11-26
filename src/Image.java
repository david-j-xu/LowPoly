import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Image {

	private int[][][] matrix;
	private int width;
	private int height;

	public Image(String fileName) throws IOException {
		BufferedImage buffer = ImageIO.read(new File(fileName));	
		
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
	
	public Image(int width, int height) {
	    matrix = new int[3][width][height];
	    this.width = width;
        this.height = height;
	}
	
	public void setRGB(int i, int j, int rgb) {
	    matrix[0][i][j] = (rgb >> 16) & 0xFF;
        matrix[1][i][j] = (rgb >> 8) & 0xFF;
        matrix[2][i][j] = rgb & 0xFF;
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
	
	public Image blackAndWhite() {
	   Image output = new Image(width, height);
	    
	    for (int i = 0; i < width; i++) {
	        for (int j = 0 ; j < height; j++) {
	            int avg = (matrix[0][i][j] + matrix[1][i][j] + matrix[2][i][j]) / 3;
	            
	            output.setRGB(i, j, bitShift(avg, avg, avg));
	        }
	    }
	    
	    return output;
	}
	
	public static int bitShift(int r, int g, int b) {
	    return (r << 16) + (g << 8) + b;
	}	

    public static void main(String[] args) throws IOException { 
        Image lena = new Image("lena.png");
        lena.writeImage("original"); 
    }
}
