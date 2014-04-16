package cn.quickj.utils.freeimage4j.converters;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import cn.quickj.utils.freeimage4j.FreeImage;

public abstract class AWTImageConverter {

	public abstract BufferedImage convert(FreeImage image);
	public abstract FreeImage convert(BufferedImage image);

	protected final void copyPalette(BufferedImage bufferedImage, FreeImage fi, int numColors) {
		ColorModel cm	= bufferedImage.getColorModel();
		for ( int i = 0; i < numColors; i++ ) {
			fi.setPaletteColor(i, cm.getRGB(i));
		}
	}

	protected final IndexColorModel copyPalette(FreeImage src, int numColorBits) {

		int numColors	= 1<<numColorBits;

		// copy palette
		byte[] rs		= new byte[numColors];
		byte[] gs		= new byte[numColors];
		byte[] bs		= new byte[numColors];
		byte[] as		= new byte[numColors];
		for ( int i = 0; i < numColors; i++ ) {
			int color	= src.getPaletteColor(i);
			bs[i]		= (byte)color;
			color		>>=8;
			gs[i]		= (byte)color;
			color		>>=8;
			rs[i]		= (byte)color;
			color		>>=8;
			as[i]		= (byte)color;
		}
		// create color model
		return new IndexColorModel(numColorBits,numColors, rs,gs,bs,as);
	}
}
