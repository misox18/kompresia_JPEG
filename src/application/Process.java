package application;

import java.net.URL;
import java.util.ResourceBundle;

import Jama.Matrix;
import ij.ImagePlus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;

/*Controller aplikacie*/
public class Process implements Initializable {
	private ImagePlus imagePlus;
	private Transform transform;
	private Transform transformOrig;
	private int sampleType;
	private Quality quality;

	public static final int Red = 1;
	public static final int Green = 2;
	public static final int Blue = 3;
	public static final int Y = 4;
	public static final int Cb = 5;
	public static final int Cr = 6;
	public static final int S444 = 7;
	public static final int S422 = 8;
	public static final int S420 = 9;
	public static final int S411 = 10;

	@FXML
	public RadioButton B2x2;

	@FXML
	public RadioButton B4x4;

	@FXML
	public RadioButton B8x8;

	@FXML
	public RadioButton B16x16;

	@FXML
	public RadioButton BDCT;

	@FXML
	public RadioButton BWHT;

	@FXML
	public Label labelMSE;

	@FXML
	public Label labelPSNR;

	@FXML
	public Label labelKva;

	@FXML
	public Slider sliderKva;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		nactiOrigObraz();
		imagePlus.setTitle("Original Image");
		imagePlus.show("Original image");

		sliderKva.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				labelKva.setText(String.valueOf(newValue.intValue()));

			}
		});

	}
	
	/*nacitanie obrazku*/
	private void nactiOrigObraz() {
		this.imagePlus = new ImagePlus("lena_std.jpg");
		this.transformOrig = new Transform(imagePlus.getBufferedImage());
		this.transform = new Transform(imagePlus.getBufferedImage());
		transform.getRGB();
		transformOrig.getRGB();
		this.transform.convertRGBtoYCbCr();
	}

	/*ziskanie RGB a YCbCr obrazku*/
	private ImagePlus getComponent(int component) {
		ImagePlus imagePlus = null;
		switch (component) {
		case Red:
			imagePlus = transform.setImageFromRGB(transform.getImageHeight(), transform.getImageWidth(),
					transform.getRed(), "Red");
			break;
		case Green:
			imagePlus = transform.setImageFromRGB(transform.getImageHeight(), transform.getImageWidth(),
					transform.getGreen(), "Green");
			break;
		case Blue:
			imagePlus = transform.setImageFromRGB(transform.getImageHeight(), transform.getImageWidth(),
					transform.getBlue(), "Blue");
			break;
		case Y:
			imagePlus = transform.setImageFromRGB(transform.getImageHeight(), transform.getImageWidth(),
					transform.getY(), "Y");
			break;
		case Cb:
			imagePlus = transform.setImageFromRGB(transform.getcB().getRowDimension(),
					transform.getcB().getColumnDimension(), transform.getcB(), "Cb");
			break;
		case Cr:
			imagePlus = transform.setImageFromRGB(transform.getcR().getRowDimension(),
					transform.getcR().getColumnDimension(), transform.getcR(), "Cr");
			break;
		}

		return imagePlus;
	}

	/*podvzorkovanie obrazu*/
	private void downsample(int downsampleType) {
		// ImagePlus imagePlus = null;
		nactiOrigObraz();
		Matrix tmpCb = new Matrix(transform.getcB().getArray());
		Matrix tmpCr = new Matrix(transform.getcR().getArray());

		switch (downsampleType) {
		case S444:
			transform.setcB(tmpCb);
			transform.setcR(tmpCr);
			sampleType = Process.S444;
			break;
		case S422:
			transform.setcB(transform.downSample(tmpCb));
			transform.setcR(transform.downSample(tmpCr));
			sampleType = Process.S422;
			break;
		case S420:
			transform.setcB(transform.downSample(transform.downSample(tmpCb).transpose()).transpose());
			transform.setcR(transform.downSample(transform.downSample(tmpCr).transpose()).transpose());
			sampleType = Process.S420;
			break;
		case S411:
			transform.setcB(transform.downSample(transform.downSample(tmpCb)));
			transform.setcR(transform.downSample(transform.downSample(tmpCr)));
			sampleType = Process.S411;
			break;
		}
	}

	/*nadvzorkovanie obrazu*/
	public void oversample(int oversampleType) {
		Matrix tmpCb = new Matrix(transform.getcB().getArray());
		Matrix tmpCr = new Matrix(transform.getcR().getArray());

		switch (oversampleType) {
		case S444:
			transform.setcB(tmpCb);
			transform.setcR(tmpCr);
			transform.convertYCbCrtoRGB();
			break;
		case S422:
			transform.setcB(transform.overSample(tmpCb));
			transform.setcR(transform.overSample(tmpCr));
			transform.convertYCbCrtoRGB();
			break;
		case S420:
			transform.setcB(transform.overSample(transform.overSample(tmpCb.transpose()).transpose()));
			transform.setcR(transform.overSample(transform.overSample(tmpCr.transpose()).transpose()));
			transform.convertYCbCrtoRGB();
			break;
		case S411:
			transform.setcB(transform.overSample(transform.overSample(tmpCb)));
			transform.setcR(transform.overSample(transform.overSample(tmpCr)));
			transform.convertYCbCrtoRGB();
			break;
		}

	}

	/*volanie metod pre jednotlive komponenty gui*/
	public void rButtonPressed(ActionEvent event) {
		getComponent(Process.Red).show("Red Component");
	}

	public void gButtonPressed(ActionEvent event) {
		getComponent(Process.Green).show("Green Component");
	}

	public void bButtonPressed(ActionEvent event) {
		getComponent(Process.Blue).show("Blue Component");
	}

	public void yButtonPressed(ActionEvent event) {
		getComponent(Process.Y).show("Y Component");
	}

	public void CbButtonPressed(ActionEvent event) {
		getComponent(Process.Cb).show("Cb Component");
	}

	public void CrButtonPressed(ActionEvent event) {
		getComponent(Process.Cr).show("Cr Component");
	}

	public void S444ButtonPressed(ActionEvent event) {
		downsample(Process.S444);
	}

	public void S422ButtonPressed(ActionEvent event) {
		downsample(Process.S422);
	}

	public void S420ButtonPressed(ActionEvent event) {
		downsample(Process.S420);
	}

	public void S411ButtonPressed(ActionEvent event) {
		downsample(Process.S411);
	}

	public void OverSampleButtonPressed(ActionEvent event) {
		oversample(sampleType);
	}

	public void QualityButtonPressed(ActionEvent event) {
		quality.getQuality(transformOrig.getRed(), transformOrig.getGreen(), transformOrig.getBlue(),
				transform.getRed(), transform.getGreen(), transform.getBlue());
		labelMSE.setText(String.valueOf(quality.getMSE()));
		labelPSNR.setText(String.valueOf(quality.getPSNR()));
	}

	public void FullButtonPressed(ActionEvent event) {
		ImagePlus imagePlus = null;
		imagePlus = transform.setImageFromRGB(transform.getImageHeight(), transform.getImageWidth(), transform.getRed(),
				transform.getGreen(), transform.getBlue());
		imagePlus.show();

	}

	public void TransButtonPressed(ActionEvent event) {
		if (BDCT.isSelected() && B2x2.isSelected()) {
			transform.setY(transform.tranform(2, 0, transform.getY()));
			transform.setcB(transform.tranform(2, 0, transform.getcB()));
			transform.setcR(transform.tranform(2, 0, transform.getcR()));
		} else if (BDCT.isSelected() && B4x4.isSelected()) {
			transform.setY(transform.tranform(4, 0, transform.getY()));
			transform.setcB(transform.tranform(4, 0, transform.getcB()));
			transform.setcR(transform.tranform(4, 0, transform.getcR()));
		} else if (BDCT.isSelected() && B8x8.isSelected()) {
			transform.setY(transform.tranform(8, 0, transform.getY()));
			transform.setcB(transform.tranform(8, 0, transform.getcB()));
			transform.setcR(transform.tranform(8, 0, transform.getcR()));
		} else if (BWHT.isSelected() && B2x2.isSelected()) {
			transform.setY(transform.tranform(2, 1, transform.getY()));
			transform.setcB(transform.tranform(2, 1, transform.getcB()));
			transform.setcR(transform.tranform(2, 1, transform.getcR()));
		} else if (BWHT.isSelected() && B4x4.isSelected()) {
			transform.setY(transform.tranform(4, 1, transform.getY()));
			transform.setcB(transform.tranform(4, 1, transform.getcB()));
			transform.setcR(transform.tranform(4, 1, transform.getcR()));
		} else if (BWHT.isSelected() && B8x8.isSelected()) {
			transform.setY(transform.tranform(8, 1, transform.getY()));
			transform.setcB(transform.tranform(8, 1, transform.getcB()));
			transform.setcR(transform.tranform(8, 1, transform.getcR()));
		}
	}

	public void InvTransButtonPressed(ActionEvent event) {
		if (BDCT.isSelected() && B2x2.isSelected()) {
			transform.setY(transform.inverseTransform(2, 0, transform.getY()));
			transform.setcB(transform.inverseTransform(2, 0, transform.getcB()));
			transform.setcR(transform.inverseTransform(2, 0, transform.getcR()));
		} else if (BDCT.isSelected() && B4x4.isSelected()) {
			transform.setY(transform.inverseTransform(4, 0, transform.getY()));
			transform.setcB(transform.inverseTransform(4, 0, transform.getcB()));
			transform.setcR(transform.inverseTransform(4, 0, transform.getcR()));
		} else if (BDCT.isSelected() && B8x8.isSelected()) {
			transform.setY(transform.inverseTransform(8, 0, transform.getY()));
			transform.setcB(transform.inverseTransform(8, 0, transform.getcB()));
			transform.setcR(transform.inverseTransform(8, 0, transform.getcR()));
		} else if (BWHT.isSelected() && B2x2.isSelected()) {
			transform.setY(transform.inverseTransform(2, 1, transform.getY()));
			transform.setcB(transform.inverseTransform(2, 1, transform.getcB()));
			transform.setcR(transform.inverseTransform(2, 1, transform.getcR()));
		} else if (BWHT.isSelected() && B4x4.isSelected()) {
			transform.setY(transform.inverseTransform(4, 1, transform.getY()));
			transform.setcB(transform.inverseTransform(4, 1, transform.getcB()));
			transform.setcR(transform.inverseTransform(4, 1, transform.getcR()));
		} else if (BWHT.isSelected() && B8x8.isSelected()) {
			transform.setY(transform.inverseTransform(8, 1, transform.getY()));
			transform.setcB(transform.inverseTransform(8, 1, transform.getcB()));
			transform.setcR(transform.inverseTransform(8, 1, transform.getcR()));
		}

	}

	public void KvaButtonPressed(ActionEvent event) {
		if (B2x2.isSelected()) {
			transform.setY(transform.Kvant(2, (int) sliderKva.getValue(), 0, transform.getY()));
			transform.setcB(transform.Kvant(2, (int) sliderKva.getValue(), 1, transform.getcB()));
			transform.setcR(transform.Kvant(2, (int) sliderKva.getValue(), 1, transform.getcR()));
		} else if (B4x4.isSelected()) {
			transform.setY(transform.Kvant(4, (int) sliderKva.getValue(), 0, transform.getY()));
			transform.setcB(transform.Kvant(4, (int) sliderKva.getValue(), 1, transform.getcB()));
			transform.setcR(transform.Kvant(4, (int) sliderKva.getValue(), 1, transform.getcR()));
		} else if (B8x8.isSelected()) {
			transform.setY(transform.Kvant(8, (int) sliderKva.getValue(), 0, transform.getY()));
			transform.setcB(transform.Kvant(8, (int) sliderKva.getValue(), 1, transform.getcB()));
			transform.setcR(transform.Kvant(8, (int) sliderKva.getValue(), 1, transform.getcR()));
		}

	}

	public void InvKvaButtonPressed(ActionEvent event) {
		if (B2x2.isSelected()) {
			transform.setY(transform.invKvant(2, (int) sliderKva.getValue(), 0, transform.getY()));
			transform.setcB(transform.invKvant(2, (int) sliderKva.getValue(), 1, transform.getcB()));
			transform.setcR(transform.invKvant(2, (int) sliderKva.getValue(), 1, transform.getcR()));
		} else if (B4x4.isSelected()) {
			transform.setY(transform.invKvant(4, (int) sliderKva.getValue(), 0, transform.getY()));
			transform.setcB(transform.invKvant(4, (int) sliderKva.getValue(), 1, transform.getcB()));
			transform.setcR(transform.invKvant(4, (int) sliderKva.getValue(), 1, transform.getcR()));
		} else if (B8x8.isSelected()) {
			transform.setY(transform.invKvant(8, (int) sliderKva.getValue(), 0, transform.getY()));
			transform.setcB(transform.invKvant(8, (int) sliderKva.getValue(), 1, transform.getcB()));
			transform.setcR(transform.invKvant(8, (int) sliderKva.getValue(), 1, transform.getcR()));
		}
	}
}
