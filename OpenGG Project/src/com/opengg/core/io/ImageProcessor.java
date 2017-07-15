/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io;

/**
 *
 * @author warren
 */

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcessor {
	
	private BufferedImage image;
	private int[] pixels;
	private int h, w;

	public BufferedImage loadImage(String filename) {
		try {
			image = ImageIO.read(ImageProcessor.class.getResourceAsStream("/" + filename));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
                return image;
	}

	/*private void processImage() {
		for (int i = 0; i < pixels.length; i++) {
			int pixel = pixels[i];
			int alpha = ((pixel & 0xff000000) >>> 24);

			if (alpha != 0x00) {

				//info(alpha + ": " + Integer.toHexString(pixel));
				pixel = 0xffff00ff;
			}

			pixels[i] = pixel;

		}
	}*/

	public void saveImage(BufferedImage image,String filename) {
		image.setRGB(0, 0, w, h, pixels, 0, w);
		try {
			ImageIO.write(image, "PNG", new FileOutputStream("res/new_" + filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
