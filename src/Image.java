import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Image {

	private int[][][] matrix;

	public Image(String fileName) throws IOException {
		BufferedImage buffer = ImageIO.read(new File(fileName));	

		matrix = new int[3][buffer.getWidth(null)][buffer.getHeight(null)];

		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix[0][i].length; j++) {
				int rgb = buffer.getRGB(i, j);
				matrix[0][i][j] = (rgb >> 16) & 0xFF;
				matrix[1][i][j] = (rgb >> 8) & 0xFF;
				matrix[2][i][j] = rgb & 0xFF;
			}
		}
	}

	public int[][][] getMatrix() {
		return matrix;
	}
	
	public void writeImage() {
	    try {
	        BufferedImage output = new BufferedImage(matrix[0].length, 
	                                                    matrix[0][0].length, 
	                                                    BufferedImage.TYPE_INT_RGB);
	        int curr;
	        for (int i = 0; i < output.getWidth(); i++) {
	            for (int j = 0; j < output.getHeight(); j++) {
	                curr = (matrix[0][i][j] << 16) + 
	                            (matrix[1][i][j] << 8) + 
	                            (matrix[2][i][j]);
	                output.setRGB(i, j, curr);
	            }
	        }
	        
	        File outputFile = new File("output.png");
	        ImageIO.write(output, "png", outputFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix[0][i].length; j++) {
				output.append("(" + Integer.toString(matrix[0][i][j]) + ", " +
	                    Integer.toString(matrix[1][i][j]) + ", " +
	                    Integer.toString(matrix[2][i][j]) + ") ") ;
			}
			output.append("\n");
		}
		return output.toString();
	}

	public static void main(String[] args) throws IOException {
		Image lena = new Image("lena.png");
		lena.writeImage();
	}
}
