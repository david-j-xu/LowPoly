public class ConvolveTool {

	public static Image convolve(Image image, int[][] kernel) {
	    if (kernel.length <= 0 || 
	            kernel.length % 2 == 0 || 
	            kernel.length != kernel[0].length) {
	        throw new IllegalArgumentException("Invalid kernel size");
	    }
	    
	    int kernelSize = (int) Math.pow(kernel.length, 2); 
	    int half = kernel.length / 2;
	    int width = image.getWidth();
		int height = image.getHeight();

	    Image output = new Image(width, height);
	    
		int[][][] matrix = image.getMatrix();

	    for (int i = 0; i < width; i++) {
	        for (int j = 0 ; j < height; j++) {
	            int convolvedR = 0;
	            int convolvedG = 0;
	            int convolvedB = 0;
	            
	            // (i,j) center with implicit 0 padding
	            for (int r = -half; r <= half; r++) {
	                for (int c = -half; c <= half; c++) {
	                    if (output.inBounds(i + r, j + c)) {
	                        convolvedR += matrix[0][i + r][j + c] * kernel[r + half][c + half];
	                        convolvedG += matrix[1][i + r][j + c] * kernel[r + half][c + half];
	                        convolvedB += matrix[2][i + r][j + c] * kernel[r + half][c + half];
	                    }
	                }
	            }
	            
	            output.setRGB(i, j, Image.bitShift(convolvedR / kernelSize, 
	                                            convolvedG / kernelSize, 
	                                            convolvedB / kernelSize));
	        }
	    }
	    
	    return output;
	}

	// Double handling for kernel
	public static Image convolve(Image image, double[][] kernel) {
	    if (kernel.length <= 0 || 
	            kernel.length % 2 == 0 || 
	            kernel.length != kernel[0].length) {
	        throw new IllegalArgumentException("Invalid kernel size");
	    }
	    
	    int kernelSize = (int) Math.pow(kernel.length, 2); 
	    int half = kernel.length / 2;
	    int width = image.getWidth();
		int height = image.getHeight();

	    Image output = new Image(width, height);
	    
		int[][][] matrix = image.getMatrix();

	    for (int i = 0; i < width; i++) {
	        for (int j = 0 ; j < height; j++) {
	            int convolvedR = 0;
	            int convolvedG = 0;
	            int convolvedB = 0;
	            
	            // (i,j) center with implicit 0 padding
	            for (int r = -half; r <= half; r++) {
	                for (int c = -half; c <= half; c++) {
	                    if (output.inBounds(i + r, j + c)) {
	                        convolvedR += (int) matrix[0][i + r][j + c] * kernel[r + half][c + half];
	                        convolvedG += (int) matrix[1][i + r][j + c] * kernel[r + half][c + half];
	                        convolvedB += (int) matrix[2][i + r][j + c] * kernel[r + half][c + half];
	                    }
	                }
	            }
	            
	            output.setRGB(i, j, Image.bitShift(convolvedR / kernelSize, 
	                                            convolvedG / kernelSize, 
	                                            convolvedB / kernelSize));
	        }
	    }
	    
	    return output;
	}

	/** Generate nxn Gaussian blur kernel with standard deviation sigma
	 * @param n Size of the kernel
	 * @param sigma Standard deviation of the used Gaussian distribution
	 * @return nxn Gaussian kernel
	 */
	public static double[][] getGaussian(int n, double sigma) {	
		if (n <= 0 || n % 2 == 0) {
			throw new IllegalArgumentException("Gaussian size must be odd positive integer.");
		} else if (sigma <= 0) {
			// From OpenCV docs
			sigma = 0.3 * ((n - 1) * 0.5 - 1) + 0.8;
		}

		double[][] gaussian = new double[n][n];

		int half = n / 2;

		for (int i = -half; i <= half; i++) {
			for (int j = -half; j <= half; j++) {
				// Joint PDF of two independent Gaussian distributions
				gaussian[i + half][j + half] = (1d / (2d * Math.PI * Math.pow(sigma, 2))) * 
					Math.exp(-(Math.pow(i, 2) + Math.pow(j, 2)) / (2 * Math.pow(sigma, 2)));
			}
		}

		return gaussian;
	}	

	public static void main(String[] args) throws java.io.IOException {
		int[][] identity = new int[][] {{0, 0, 0},
										{0, 1, 0},
										{0, 0, 0}};

		int[][] sobel = new int[][] {{1, 0, -1}, 
                                     {2, 0, -2}, 
                                     {1, 0, -1}};
                                    
        int[][] laplacian = new int[][] {{0, -1, 0},
                                         {-1, 4, -1},
                                         {0, -1, 0}};
        // int[][] gaussian = new int[][] {{1, 2, 1},
        //                                 {2, 3, 2},
        //                                 {1, 2, 1}};

		// Check that Gaussian kernel matches the Wikipedia sample kernel
		double[][] gaussian = getGaussian(7, 0.84089642);

		StringBuilder out = new StringBuilder();
		for (int i = 0; i < gaussian.length; i++) {
			for (int j = 0; j < gaussian.length; j++) {
				out.append(Double.toString(gaussian[i][j]));
				out.append(" ");
			}
			out.append("\n");
		}
		System.out.println(out.toString());
		
		gaussian = getGaussian(3, 0);
		Image lena = new Image("lena.png");
		convolve(lena, identity).writeImage("identity");
		//Image convolved = lena.convolve(laplacian);
        //convolved.writeImage("plsWorkOMG");
        
        Image bw = lena.blackAndWhite();
    	bw.writeImage("blackAndWhite");	
        //bw.convolve(gaussian).convolve(laplacian).writeImage("bwConvolved");
        
        Image blurred = convolve(bw, gaussian);	
		blurred.writeImage("gaussian");
		convolve(blurred, laplacian).writeImage("laplacian");
	}
}
