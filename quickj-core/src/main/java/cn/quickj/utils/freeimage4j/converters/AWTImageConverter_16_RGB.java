package cn.quickj.utils.freeimage4j.converters;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.nio.ByteBuffer;

import cn.quickj.utils.freeimage4j.FreeImage;
import cn.quickj.utils.freeimage4j.FreeImageRuntimeException;

/**
 * @author gru
 *
 */
public final class AWTImageConverter_16_RGB extends AWTImageConverter {

	private final static AWTImageConverter_16_RGB INSTANCE	= new AWTImageConverter_16_RGB();

	private AWTImageConverter_16_RGB(){}

	protected static AWTImageConverter_16_RGB getInstance() {
		return INSTANCE;
	}

	@Override
	public final FreeImage convert(BufferedImage bufferedImage) {

		DataBuffer db	= bufferedImage.getData().getDataBuffer();
		if ( !(db instanceof DataBufferUShort) ) {
			throw new FreeImageRuntimeException("Unsupported DataBuffer class \""+db.getClass()+"\" for 16 bit RGB image type!");
		}

		// allocate image
		int w			= bufferedImage.getWidth();
		int h			= bufferedImage.getHeight();
		FreeImage fi	= new FreeImage(w,h,16, 0x00FF0000,0x0000FF00,0x000000FF);

		// copy pixels
		ByteBuffer buf	= fi.getPixelBuffer();
		short[] pixels	= ((DataBufferUShort)db).getData();
		int src			= 0;
		int pitch		= fi.getPitch();
		for ( int y = (h-1)*pitch; y >= 0; y-=pitch ) {
			for ( int x = y; x < y+pitch; x+=2, src++ ) {
				short pixel	= pixels[src];
				buf.putShort(x, (short)(pixel << 11 | pixel >> 11 & 0x1F | (pixel & 0x07E0)));
			}
		}

		return fi;
	}

	@Override
	public BufferedImage convert(FreeImage image) {

		// create buffered image
		int w	= image.getWidth();
		int h	= image.getHeight();
		BufferedImage bufferedImage	= new BufferedImage (w, h, BufferedImage.TYPE_USHORT_565_RGB);
		DataBuffer db				= bufferedImage.getRaster().getDataBuffer();
		if ( !(db instanceof DataBufferUShort) ) {
			throw new FreeImageRuntimeException("Unexpected DataBuffer class \""+db.getClass()+"\" for 16 bit RGB image type!");
		}
		short[] pixels	= ((DataBufferUShort)db).getData();

		// copy pixels
		ByteBuffer buf	= image.getPixelBuffer();
		int pitch		= image.getPitch();
		int dst			= 0;
		for ( int y = (h-1)*pitch; y >= 0; y-=pitch ) {
			int x1	= y + w*2;
			for ( int x = y; x < x1; x+=2 ) {
				short pixel		= buf.getShort(x);
				pixels[dst++]	= (short)(pixel << 11 | pixel >> 11 & 0x1F | (pixel & 0x07E0));
			}
		}

		return bufferedImage;
	}
}
