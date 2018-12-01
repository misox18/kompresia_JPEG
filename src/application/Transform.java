package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import Jama.Matrix;
import ij.ImagePlus;

/*Trieda zabezpecujuca transformaciu obrazu*/
public class Transform {
	private int[][] red;
	private int[][] green;
	private int[][] blue;
	private static final double[][] yKva = { { 16, 11, 10, 16, 24, 40, 51, 61 }, { 12, 12, 14, 19, 26, 58, 60, 55 },
			{ 14, 13, 16, 24, 40, 57, 69, 56 }, { 14, 17, 22, 29, 51, 87, 80, 62 },
			{ 18, 22, 37, 56, 68, 109, 103, 77 }, { 24, 35, 55, 64, 81, 104, 113, 92 },
			{ 49, 64, 78, 87, 103, 121, 120, 101 }, { 72, 92, 95, 98, 112, 100, 103, 99 } };
	private static final double[][] cKva = { { 17, 18, 24, 47, 99, 99, 99, 99 }, { 18, 21, 26, 66, 99, 99, 99, 99 },
			{ 24, 26, 56, 99, 99, 99, 99, 99 }, { 47, 66, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 },
			{ 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 },
			{ 99, 99, 99, 99, 99, 99, 99, 99 } };

	private int imageHeight;
	private int imageWidth;

	private Matrix y;
	private Matrix cB;
	private Matrix cR;
	private Matrix myKva;
	private Matrix mcKva;

	private BufferedImage bImage;
	private ColorModel colorModel;

	public Transform(BufferedImage bImage) {
		this.bImage = bImage;
		this.colorModel = bImage.getColorModel();
		this.imageHeight = bImage.getHeight();
		this.imageWidth = bImage.getWidth();
		red = new int[this.imageHeight][this.imageWidth];
		green = new int[this.imageHeight][this.imageWidth];
		blue = new int[this.imageHeight][this.imageWidth];
		y = new Matrix(this.imageWidth, this.imageWidth);
		cB = new Matrix(this.imageWidth, this.imageWidth);
		cR = new Matrix(this.imageWidth, this.imageWidth);
		myKva = new Matrix(yKva);
		mcKva = new Matrix(cKva);
	}

	public int[][] getRed() {
		return red;
	}

	public int[][] getGreen() {
		return green;
	}

	public int[][] getBlue() {
		return blue;
	}

	public Matrix getY() {
		return y;
	}

	public Matrix getcB() {
		return cB;
	}

	public Matrix getcR() {
		return cR;
	}

	public void setcB(Matrix cB) {
		this.cB = cB;
	}

	public void setcR(Matrix cR) {
		this.cR = cR;
	}

	public void setY(Matrix y) {
		this.y = y;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	/* ziskanie RGB zlozky */
	public void getRGB() {
		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				red[i][j] = colorModel.getRed(this.bImage.getRGB(j, i));
				green[i][j] = colorModel.getGreen(this.bImage.getRGB(j, i));
				blue[i][j] = colorModel.getBlue(this.bImage.getRGB(j, i));
			}
		}
	}

	/* podvzorkovanie obrazu */
	public Matrix downSample(Matrix mat) {
		int col = mat.getColumnDimension();
		int row = mat.getRowDimension();
		Matrix tmp = new Matrix(row, col / 2);

		for (int i = 0; i < col; i = i + 2) {
			tmp.setMatrix(0, row - 1, i / 2, i / 2, mat.getMatrix(0, row - 1, i, i));
		}
		return tmp;
	}

	/* nadvzorkovanie obrazu */
	public Matrix overSample(Matrix mat) {
		int col = mat.getColumnDimension();
		int row = mat.getRowDimension();
		Matrix tmp = new Matrix(row, col * 2);

		for (int i = 0; i < col; i++) {
			tmp.setMatrix(0, row - 1, i * 2, i * 2, mat.getMatrix(0, row - 1, i, i));
			tmp.setMatrix(0, row - 1, (i * 2) + 1, (i * 2) + 1, mat.getMatrix(0, row - 1, i, i));
		}
		return tmp;
	}

	/* zostavenie obrazku z RGB zloziek */
	public ImagePlus setImageFromRGB(int height, int width, int[][] r, int[][] g, int[][] b) {
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[][] rgb = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				rgb[i][j] = new Color(r[i][j], g[i][j], b[i][j]).getRGB();
				bImage.setRGB(j, i, rgb[i][j]);
			}
		}
		return new ImagePlus("RGB", bImage);
	}

	/* zostavenie obrazku z RGB zloziek */
	public ImagePlus setImageFromRGB(int height, int width, int[][] x, String component) {
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[][] rgb = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				rgb[i][j] = new Color((int) x[i][j], (int) x[i][j], (int) x[i][j]).getRGB();
				bImage.setRGB(j, i, rgb[i][j]);
			}

		}
		return new ImagePlus(component, bImage);
	}

	/* zostavenie obrazku z RGB zloziek */
	public ImagePlus setImageFromRGB(int height, int width, Matrix x, String component) {
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[][] rgb = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				rgb[i][j] = new Color((int) x.get(i, j), (int) x.get(i, j), (int) x.get(i, j)).getRGB();
				bImage.setRGB(j, i, rgb[i][j]);
			}

		}
		return new ImagePlus(component, bImage);
	}

	/* prekonvertovanie RGB do YCbCr */
	public void convertRGBtoYCbCr() {
		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				y.set(i, j, 0.257 * red[i][j] + 0.504 * green[i][j] + 0.098 * blue[i][j] + 16);
				cB.set(i, j, -0.148 * red[i][j] - 0.291 * green[i][j] + 0.439 * blue[i][j] + 128);
				cR.set(i, j, 0.439 * red[i][j] - 0.368 * green[i][j] - 0.071 * blue[i][j] + 128);
			}
		}
	}

	/* prekonvertovanie YCbCr do RGB */
	public void convertYCbCrtoRGB() {
		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				red[i][j] = (int) Math.round(1.164 * (y.get(i, j) - 16) + 1.596 * (cR.get(i, j) - 128));
				if (red[i][j] > 255)
					red[i][j] = 255;
				if (red[i][j] < 0)
					red[i][j] = 0;
				green[i][j] = (int) Math.round(
						1.164 * (y.get(i, j) - 16) - 0.813 * (cR.get(i, j) - 128) - 0.391 * (cB.get(i, j) - 128));
				if (green[i][j] > 255)
					green[i][j] = 255;
				if (green[i][j] < 0)
					green[i][j] = 0;
				blue[i][j] = (int) Math.round(1.164 * (y.get(i, j) - 16) + 2.018 * (cB.get(i, j) - 128));
				if (blue[i][j] > 255)
					blue[i][j] = 255;
				if (blue[i][j] < 0)
					blue[i][j] = 0;
			}
		}
	}

	/* vyuzite transformovanie obrazku pomocou DCT a WHT */
	public Matrix tranform(int size, int selector, Matrix inputMatrix) {
		Matrix outmatrix = new Matrix(inputMatrix.getRowDimension(), inputMatrix.getColumnDimension());
		Matrix tmp = new Matrix(size, size);
		Matrix dctmatrix = new Matrix(size, size);

		if (selector == 0) {
			dctmatrix = TransformMatrix.getDctMatrix(size);
		} else {
			dctmatrix = TransformMatrix.getWhtMatrix(size);
		}

		for (int i = 0; i < (inputMatrix.getRowDimension() / size); i++) {
			for (int j = 0; j < (inputMatrix.getColumnDimension() / size); j++) {
				tmp.setMatrix(0, size - 1, 0, size - 1,
						inputMatrix.getMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1));
				tmp = dctmatrix.times(tmp).times(dctmatrix.transpose());
				outmatrix.setMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1,
						tmp.getMatrix(0, size - 1, 0, size - 1));
			}
		}

		return outmatrix;
	}

	/* spatna DCT a WHT */
	public Matrix inverseTransform(int size, int selector, Matrix inputMatrix) {
		Matrix outmatrix = new Matrix(inputMatrix.getRowDimension(), inputMatrix.getColumnDimension());
		Matrix tmp = new Matrix(size, size);
		Matrix dctmatrix = new Matrix(size, size);

		if (selector == 0) {
			dctmatrix = TransformMatrix.getDctMatrix(size);
		} else {
			dctmatrix = TransformMatrix.getWhtMatrix(size);
		}

		for (int i = 0; i < (inputMatrix.getRowDimension() / size); i++) {
			for (int j = 0; j < (inputMatrix.getColumnDimension() / size); j++) {
				tmp.setMatrix(0, size - 1, 0, size - 1,
						inputMatrix.getMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1));
				tmp = dctmatrix.transpose().times(tmp).times(dctmatrix);
				outmatrix.setMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1,
						tmp.getMatrix(0, size - 1, 0, size - 1));
			}
		}

		return outmatrix;
	}

	/* metoda zabezpecujuca kvantizaciu */
	public Matrix Kvant(int size, int q, int selector, Matrix inputMatrix) {
		Matrix outMatrix = new Matrix(inputMatrix.getRowDimension(), inputMatrix.getColumnDimension());
		Matrix tmp = new Matrix(size, size);
		Matrix kva = new Matrix(size, size);
		if (selector == 0) {
			kva.setMatrix(0, size - 1, 0, size - 1, myKva.getMatrix(0, size - 1, 0, size - 1));
			kva = kva.times(qHelp(q));
		} else {
			kva.setMatrix(0, size - 1, 0, size - 1, mcKva.getMatrix(0, size - 1, 0, size - 1));
			kva = kva.times(qHelp(q));
		}

		for (int i = 0; i < (inputMatrix.getRowDimension() / size); i++) {
			for (int j = 0; j < (inputMatrix.getColumnDimension() / size); j++) {
				tmp.setMatrix(0, size - 1, 0, size - 1,
						inputMatrix.getMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1));
				tmp = kvaHelp(tmp, kva);
				outMatrix.setMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1,
						tmp.getMatrix(0, size - 1, 0, size - 1));
			}
		}
		return outMatrix;
	}

	/* metoda zabezpecujuca spatnu kvantizaciu */
	public Matrix invKvant(int size, int q, int selector, Matrix inputMatrix) {
		Matrix outMatrix = new Matrix(inputMatrix.getRowDimension(), inputMatrix.getColumnDimension());
		Matrix tmp = new Matrix(size, size);
		Matrix kva = new Matrix(size, size);
		if (selector == 0) {
			kva.setMatrix(0, size - 1, 0, size - 1, myKva.getMatrix(0, size - 1, 0, size - 1));
			kva = kva.times(qHelp(q));
		} else {
			kva.setMatrix(0, size - 1, 0, size - 1, mcKva.getMatrix(0, size - 1, 0, size - 1));
			kva = kva.times(qHelp(q));
		}
		
		for (int i = 0; i < (inputMatrix.getRowDimension() / size); i++) {
			for (int j = 0; j < (inputMatrix.getColumnDimension() / size); j++) {
				tmp.setMatrix(0, size - 1, 0, size - 1,
						inputMatrix.getMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1));
				tmp = tmp.arrayTimes(kva);
				outMatrix.setMatrix(i * size, i * size + size - 1, j * size, j * size + size - 1,
						tmp.getMatrix(0, size - 1, 0, size - 1));
			}
		}
		
		return outMatrix;
	}

	/* pomocne metody pre kvantizaciu */
	private Matrix kvaHelp(Matrix m1, Matrix m2) {
		Matrix out = new Matrix(m1.getRowDimension(), m1.getRowDimension());
		for (int i = 0; i < m1.getRowDimension(); i++) {
			for (int j = 0; j < m1.getColumnDimension(); j++) {
				out.set(i, j,(int) (m1.get(i, j) / m2.get(i, j)));
			}

		}
		return out;
	}

	private double qHelp(int q) {
		double out = 0;
		if (q < 50) {
			out = 50 / q;
			return out;
		} else if (q == 100) {
			return 1;
		} else {
			out = 2 - ((2 * q) / 100);
			return out;
		}
	}

}
