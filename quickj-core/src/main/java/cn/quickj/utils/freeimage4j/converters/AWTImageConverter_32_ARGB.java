package cn.quickj.utils.freeimage4j.converters;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

import cn.quickj.utils.freeimage4j.FreeImage;
import cn.quickj.utils.freeimage4j.FreeImageRuntimeException;

/**
 * @author gru
 *
 */
public final class AWTImageConverter_32_ARGB extends AWTImageConverter {

	private final static AWTImageConverter_32_ARGB INSTANCE	= new AWTImageConverter_32_ARGB();

	private AWTImageConverter_32_ARGB(){}

	protected static AWTImageConverter_32_ARGB getInstance() {
		return INSTANCE;
	}

	@Override
	public final FreeImage convert(BufferedImage bufferedImage) {

		// allocate image
		int w			= bufferedImage.getWidth();
		int h			= bufferedImage.getHeight();

		DataBuffer db	= bufferedImage.getData().getDataBuffer();
		if ( db instanceof DataBufferByte ) {
			FreeImage fi	= new FreeImage(w,h,32, 0x00FF0000,0x0000FF00,0x000000FF);
			// copy pixels
			ByteBuffer buf	= fi.getPixelBuffer();
			byte[] pixels	= ((DataBufferByte)db).getData();
			int srcPitch	= w*4;
	        int pitchRest   = fi.getPitch() - srcPitch;
			for ( int y = (h-1)*srcPitch; y >= 0; y-=srcPitch ) {
				buf.put(pixels,y,srcPitch);
	            for ( int i = 0; i < pitchRest; i++ ) {
	                buf.put((byte)0);
	            }
			}
			return fi;
		}

		if ( db instanceof DataBufferInt ) {
			FreeImage fi	= new FreeImage(w,h,32, 0x00FF0000,0x0000FF00,0x000000FF);
			// copy pixels
			ByteBuffer buf	= fi.getPixelBuffer();
			int[] pixels	= ((DataBufferInt)db).getData();
			int srcPitch	= w*4;
	        int pitchRest   = fi.getPitch() - srcPitch;
			for ( int y = (h-1)*w; y >= 0; y-=w ) {
				int xEnd	= y+w;
				for ( int x = y; x < xEnd; x++ ) {
					buf.putInt(pixels[x]);
				}
	            for ( int i = 0; i < pitchRest; i++ ) {
	                buf.put((byte)0);
	            }
			}
			return fi;
		}

		throw new FreeImageRuntimeException("Unsupported DataBuffer class \""+db.getClass()+"\" for 32 bit ARGB image type!");
	}

	@Override
	public final BufferedImage convert(FreeImage image) {

		// create buffered image
		int w	= image.getWidth();
		int h	= image.getHeight();
		BufferedImage bufferedImage	= new BufferedImage (w, h, BufferedImage.TYPE_4BYTE_ABGR);
		DataBuffer db				= bufferedImage.getRaster().getDataBuffer();
		if ( !(db instanceof DataBufferByte) ) {
			throw new FreeImageRuntimeException("Unexpected DataBuffer class \""+db.getClass()+"\" for 32 bit RGB image type!");
		}
		byte[] pixels	= ((DataBufferByte)db).getData();

		// copy pixels
		ByteBuffer buf	= image.getPixelBuffer();
        int srcPitch    = w*4;
        int dstPitch    = image.getPitch();
		int dst			= 0;
		for ( int y = (h-1)*dstPitch; y >= 0; y-=dstPitch ) {
			int x1	= y + srcPitch;
			for ( int x = y; x < x1; x+=4 ) {
				pixels[dst++]	= buf.get(x+3);
				pixels[dst++]	= buf.get(x+0);
				pixels[dst++]	= buf.get(x+1);
				pixels[dst++]	= buf.get(x+2);
			}
		}

		return bufferedImage;
	}
}
