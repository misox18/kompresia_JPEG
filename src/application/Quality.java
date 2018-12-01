package application;

/* Trieda pre ziskanie kvality obrazu */
public class Quality {
	private double MSE;
	private double PSNR;

	public Quality() {
		
	}

	public double getMSE() {
		return MSE;
	}

	public double getPSNR() {
		return PSNR;
	}

	public void getQuality(int[][] orR, int[][] orG, int[][] orB, int[][] edR, int[][] edG, int[][] edB) {
		int rows = orR.length;
		int cols = orR[0].length;
		double R = 0;
		double mseR = 0;
		double G = 0;
		double mseG = 0;
		double B = 0;
		double mseB = 0;
		double tmp = Math.pow(Math.pow(2, 8) - 1, 2);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				R = Math.pow(orR[i][j] - edR[i][j], 2);
				mseR += R;
				G = Math.pow(orG[i][j] - edG[i][j], 2);
				mseG += G;
				B = Math.pow(orB[i][j] - edB[i][j], 2);
				mseB += B;
			}

		}
		mseR = mseR / (rows * cols);
		mseG = mseG / (rows * cols);
		mseB = mseB / (rows * cols);
		MSE = (mseR + mseG + mseB) / 3;
		if (MSE == 0) {
			PSNR = 10000;
		} else {
			double PSNRR = 10 * Math.log10(tmp / mseR);
			double PSNRG = 10 * Math.log10(tmp / mseG);
			double PSNRB = 10 * Math.log10(tmp / mseB);
			PSNR = (PSNRR + PSNRG + PSNRB) / 3;
		}
	}

}
