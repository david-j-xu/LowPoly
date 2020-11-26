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
	
	public Image convolve(int[][] kernel) {
	    if (kernel.length <= 0 || 
	            kernel.length % 2 == 0 || 
	            kernel.length != kernel[0].length) {
	        throw new IllegalArgumentException("Invalid kernel size");
	    }
	    
	    int kernelSize = (int) Math.pow(kernel.length, 2); 
	    int half = kernel.length / 2;
	    
	    Image output = new Image(width, height);
	    
	    for (int i = 0; i < width; i++) {
	        for (int j = 0 ; j < height; j++) {
	            int convolvedR = 0;
	            int convolvedG = 0;
	            int convolvedB = 0;
	            
	            // (i,j) center
	            for (int r = -half; r <= half; r++) {
	                for (int c = -half; c <= half; c++) {
	                    if (inBounds(i + r,j + c)) {
	                        convolvedR += matrix[0][i + r][j + c] * kernel[r + half][c + half];
	                        convolvedG += matrix[1][i + r][j + c] * kernel[r + half][c + half];
	                        convolvedB += matrix[2][i + r][j + c] * kernel[r + half][c + half];
	                    }
	                }
	            }
	            
	            output.setRGB(i, j, bitShift(convolvedR / kernelSize, 
	                                            convolvedG / kernelSize, 
	                                            convolvedB / kernelSize));
	        }
	    }
	    
	    return output;
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
	
	private static int bitShift(int r, int g, int b) {
	    return (r << 16) + (g << 8) + b;
	}
	
	private boolean inBounds(int x, int y) {
	    return x >= 0 && x < width && y >= 0 && y < height;
	}
	

    public static void main(String[] args) throws IOException {
        int[][] sobel = new int[][] {{1, 0, -1}, 
                                    {2, 0, -2}, 
                                    {1, 0, -1}};
                                    
        int[][] laplacian = new int[][] {{0, -1, 0},
                                        {-1, 4, -1},
                                        {0, -1, 0}};
        int[][] gaussian = new int[][] {{1, 2, 1},
                                        {2, 3, 2},
                                        {1, 2, 1}};
        Image lena = new Image("lena.png");
        lena.writeImage("original");
        Image convolved = lena.convolve(laplacian);
        convolved.writeImage("plsWorkOMG");
        
        Image bw = lena.blackAndWhite();
        bw.writeImage("blackAndWhite");
        bw.convolve(gaussian).convolve(laplacian).writeImage("bwConvolved");
        
        lena.convolve(gaussian).writeImage("gaussianBlur");
    }
}
