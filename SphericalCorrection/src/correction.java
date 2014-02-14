
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
//import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codecimpl.TIFFImage;

public class correction extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2685655382409831974L;

	JFileChooser chooser;
	String choosertitle;
	JButton go;
	JFrame frame;

	public static void main(String[] foo) {
		new correction();
	}

	private void marchThroughImages(List<String> fileName, String directoryName, List<String> o_names)
			throws IOException {


		Raster raster[] = new Raster[fileName.size()];
		//String message = "";
		WritableRaster wRaster = null;
		//WritableRaster wRaster_upper8 = null;
		ColorModel cm = null;
		

		//byte[] map = new byte[] {(byte)0x00, (byte)0xff};
	   // ColorModel cm = new IndexColorModel(1, 2, map, map, map);
	    
		//final BufferedImage png_i = ImageIO.read(new File(directoryName + "\\cm.png"));
		//cm = png_i.getColorModel();
		
		
			
		//wRaster = image.getData().createCompatibleWritableRaster();
		//wRaster = png_i.getData().createCompatibleWritableRaster(); //@TODO don't read it from file (build in program and user choice)
		//wRaster_upper8 = png_i.getData().createCompatibleWritableRaster(); //@TODO don't read it from file (build in program and user choice)
	    
		int totalFiles = fileName.size();

		/*
		for (int i = 0; i < totalFiles; i++) {

			
			
	
			
			
			
			File file = new File(fileName.get(i));
			SeekableStream s = new FileSeekableStream(file);

			TIFFDecodeParam param = null;

			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

			raster[i] = dec.decodeAsRaster();

			TIFFImage image = (TIFFImage) dec.decodeAsRenderedImage();

			
			

			message += "Images Processed " + fileName.get(i) + " width: "
					+ raster[0].getWidth() + " height: "
					+ raster[0].getHeight() + " Pixel Size: "
					+ image.getColorModel().getPixelSize() + "\n";
			s.close();

		}*/
		
		System.out.println("OtMARCZ_C");
		
		TIFFEncodeParam encParam = null;
		File file_o = null;
		FileOutputStream fileoutput = null;
		ImageEncoder enc = null;
		
		
		//int w = raster[0].getWidth(), h = raster[0].getHeight();
		//System.out.println("Oto kurwa : " + w + " " + h);
		//int imgout[][][] = new int[w][h][3]; //change 3 to 4 for png with alpha
		
		double theta,phi,phi2;
		int i2;
		for (int i = 0; i < totalFiles; i++) {
			
			File file = new File(fileName.get(i));
			SeekableStream s = new FileSeekableStream(file);

			TIFFDecodeParam param = null;

			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

			raster[0] = dec.decodeAsRaster();

			TIFFImage image = (TIFFImage) dec.decodeAsRenderedImage();

			wRaster = image.getData().createCompatibleWritableRaster();
			cm = image.getColorModel();

			
			s.close();
			
			int w = raster[0].getWidth(), h = raster[0].getHeight();
			
			int imgout[][][] = new int[w][h][3]; //change 3 to 4 for png with alpha
			
			for (int height = 0; height < h; height++) {
				theta = Math.PI * (height -(h-1)/2.0)/(double)(h-1);
				for (int width = 0; width < w; width++) {
					phi = 2*Math.PI*(width - w/2.0)/(double)w;
					phi2 = phi * Math.cos(theta);
					i2 = (int)(phi2 * w/(2*Math.PI) + w/2);
					if(i2 < 0 || i2 > w-1){
						//Should not happen so, RED PIXEL
						imgout[width][height][0] = 255;
						imgout[width][height][1] = 0;
						imgout[width][height][2] = 0;
					}	else {
						int[] pixelA = null;

						pixelA = raster[0].getPixel(i2, height, pixelA);

						
						imgout[width][height][0] = pixelA[0];
						imgout[width][height][1] = pixelA[1];
						imgout[width][height][2] = pixelA[2];
						//imgout[width][height][3] = 255; //if alpha
						
					}
					
					//if(width == 20 && height == 31) System.out.println("oto lowerA : " +  Integer.toBinaryString(imgout[width][height][0])); //for checking @TODO write 00.es from left to make 8 size
					
					
					
					
					wRaster.setPixel(width, height,
							imgout[width][height]);
					
					//wRaster_upper8.setPixel(width, height,
						//	upper8Pixel[width][height]);

				}
			}
			
			
			if(i==0){
			File theDir = new File(directoryName + "/corrected");
			if (!theDir.exists()) theDir.mkdir();
			}
			
			file_o = new File(directoryName + "/corrected" +"\\"
					+ o_names.get(i) + ".tif");
			fileoutput = new FileOutputStream(file_o);

			encParam = null;

			enc = ImageCodec.createImageEncoder("tiff", fileoutput,
					encParam);
			enc.encode(wRaster, cm);

			fileoutput.close();
			
			
			
			System.out.format("Processed : %d" + "/%d  " + "%.3f", (i+1), totalFiles, ((float)(i+1)/(float)totalFiles)*100);
			System.out.println("%");
		}

		
		//System.out.println(message);
	}

	public correction() {

		frame = new JFrame("TIFF Image Calibrator");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.getContentPane().add(this, "Center");
		frame.setSize(this.getPreferredSize());
		frame.setVisible(true);
		go = new JButton("Select Folder");
		go.addActionListener(this);
		add(go);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		go.setEnabled(false);

		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File(
				"."));
		chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		String directoryName = "";
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			List<String> results = new ArrayList<String>();
			List<String> onlynames = new ArrayList<String>();
			File[] files = null;
			try {
				directoryName = chooser.getSelectedFile().getCanonicalPath();
				files = new File(directoryName).listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if (name.toLowerCase().endsWith("tif")
								|| name.toLowerCase().endsWith("tiff"))
							return true;
						else
							return false;
					}
				});

				for (File file : files) {
					if (file.isFile()) {
						System.out.println(file.getCanonicalPath());
						results.add(file.getCanonicalPath());
						onlynames.add(FilenameUtils.removeExtension(file.getName()));
						//onlynames.add(FilenameUtils.getBaseName(file)); //not a string
					}
				}

				marchThroughImages(results, directoryName, onlynames);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else {
			System.out
					.println("No Selection.Please Select the Folder containing TIFF Images.");
		}

		go.setEnabled(true);
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}

}