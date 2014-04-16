package cn.quickj.utils.freeimage4j.converters;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import cn.quickj.utils.freeimage4j.FreeImage;
import cn.quickj.utils.freeimage4j.FreeImageRuntimeException;

/**
 * @author gru
 *
 */
public final class AWTImageConverter_24_RGB extends AWTImageConverter {

	private final static AWTImageConverter_24_RGB INSTANCE	= new AWTImageConverter_24_RGB();

	private AWTImageConverter_24_RGB(){}

	protected static AWTImageConverter_24_RGB getInstance() {
		return INSTANCE;
	}

	@Override
	public final FreeImage convert(BufferedImage bufferedImage) {

		DataBuffer db	= bufferedImage.getData().getDataBuffer();
		if ( !(db instanceof DataBufferByte) ) {
			throw new FreeImageRuntimeException("Unsupported DataBuffer class \""+db.getClass()+"\" for 24 bit RGB image type!");
		}

		// allocate image
		int w			= bufferedImage.getWidth();
		int h			= bufferedImage.getHeight();
		FreeImage fi	= new FreeImage(w,h,24, 0x00FF0000,0x0000FF00,0x000000FF);

		// copy pixels
		ByteBuffer buf	= fi.getPixelBuffer();
		byte[] pixels	= ((DataBufferByte)db).getData();
		int pitch		= w*3;
		for ( int y = (h-1)*pitch; y >= 0; y-=pitch ) {
			buf.put(pixels,y,pitch);
		}

		return fi;
	}

	@Override
	public BufferedImage convert(FreeImage image) {

		// create buffered image
		int w	= image.getWidth();
		int h	= image.getHeight();
		BufferedImage bufferedImage	= new BufferedImage (w, h, BufferedImage.TYPE_3BYTE_BGR);
		DataBuffer db				= bufferedImage.getRaster().getDataBuffer();
		if ( !(db instanceof DataBufferByte) ) {
			throw new FreeImageRuntimeException("Unexpected DataBuffer class \""+db.getClass()+"\" for 24 bit RGB image type!");
		}
		byte[] pixels	= ((DataBufferByte)db).getData();

		// copy pixels
		ByteBuffer buf	= image.getPixelBuffer();
		int pitch		= image.getPitch();
		int dst			= 0;
		for ( int y = (h-1)*pitch; y >= 0; y-=pitch ) {
			int x1	= y + w*3;
			for ( int x = y; x < x1; x++ ) {
				pixels[dst++]	= buf.get(x);
			}
		}

		return bufferedImage;
	}
}
