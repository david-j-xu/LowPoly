import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Image {

	private int[][][] matrix;

	public Image(String fileName) throws IOException {
		BufferedImage buffer = ImageIO.read(new File(fileName));	

		matrix = new int[3][buffer.getWidth(null)][buffer.getHeight(null)];

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
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

	@Override
	public String toString() {
		String output = "";
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				output += "(" + Integer.toString(matrix[0][i][j]) + ", " +
					Integer.toString(matrix[1][i][j]) + ", " +
					Integer.toString(matrix[2][i][j]) + ") ";
			}
		}
		return output;
	}

	public static void main(String[] args) throws IOException {
		Image lena = new Image("lena.png");
		System.out.println(lena);
	}
}
