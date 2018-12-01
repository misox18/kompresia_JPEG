package application;

import Jama.Matrix;

/* trieda zabezpecujuca vytvorenie transformacnych matic */
public final class TransformMatrix {
	private static final double[][] m = { { 1., 1. }, { 1., -1. } };

	public static Matrix getDctMatrix(int size) {
		Matrix matrix = new Matrix(size, size);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == 0) {
					double aaa = Math.sqrt(1.0 / size) * Math.cos(((2.0 * j + 1.0) * i * Math.PI) / 2.0 * size);
					matrix.set(i, j, aaa);
				} else {
					double aaa = Math.sqrt(2.0 / size) * Math.cos(((2.0 * j + 1.0) * i * Math.PI) / (2.0 * size));
					matrix.set(i, j, aaa);
				}
			}
		}

		return matrix;
	}

	public static Matrix getWhtMatrix(int size) {
		Matrix mat = new Matrix(m);
		switch (size) {
		case 2:
			return mat.times(1 / Math.sqrt((double) size));
		case 4:
			return WhtHelp(size, mat).times(1 / Math.sqrt((double) size));
		case 8:
			return WhtHelp(size, WhtHelp(size / 2, mat)).times(1 / Math.sqrt((double) size));
		}
		return mat;
	}

	private static Matrix WhtHelp(int size, Matrix m) {
		Matrix matrix = new Matrix(size, size);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (i == 1 && j == 1) {
					matrix.setMatrix(i * (size / 2), i * (size / 2) + ((size / 2) - 1), j * (size / 2),
							j * (size / 2) + ((size / 2) - 1),
							m.getMatrix(0, m.getRowDimension() - 1, 0, m.getColumnDimension() - 1).times(-1.));
				} else {
					matrix.setMatrix(i * (size / 2), i * (size / 2) + ((size / 2) - 1), j * (size / 2),
							j * (size / 2) + ((size / 2) - 1),
							m.getMatrix(0, m.getRowDimension() - 1, 0, m.getColumnDimension() - 1));
				}
			}
		}
		return matrix;
	}

}
