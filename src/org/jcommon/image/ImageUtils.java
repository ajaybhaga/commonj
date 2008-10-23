package org.jcommon.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {
	/**
	 * Takes an input image file and writes an output image with the dimensions specified in width and height.
	 * 
	 * @param input
	 * @param output
	 * @param size
	 * @throws IOException
	 */
	public static final void createThumbnail(File input, File output, int size) throws IOException {
		// Read the source image
		Image source = ImageIO.read(input);
		
		// Create a target image from the source using width
		int width = -1;
		int height = -1;
		if (source.getWidth(null) > source.getHeight(null)) {
			width = size;
		} else {
			height = size;
		}
		System.out.println("Width and Height: " + width + "x" + height);
		Image target = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		
		// Draw target to a RenderedImage
		BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB); {
			Graphics g = bi.getGraphics(); {
				// Draw a white background
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, size, size);
				
				System.out.println("NewSize: " + target.getWidth(null) + "x" + target.getHeight(null));
				
				// Determine location to draw at
				int x = (size - target.getWidth(null)) / 2;
				int y = (size - target.getHeight(null)) / 2;
				
				// Draw image to canvas
				g.drawImage(target, x, y, null);
			}
		}
		
		// Output the image to the output file
		ImageIO.write(bi, "jpeg", output);
	}
	
	public static void main(String[] args) throws Exception {
		long time = System.currentTimeMillis();
		createThumbnail(new File("temp.jpg"), new File("temp_thumb.jpg"), 75);
		System.out.println("Time: " + (System.currentTimeMillis() - time));
	}
}