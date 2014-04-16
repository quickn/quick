/*
 * Created on 22.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
public final class AWTImageConverter_8Bit extends AWTImageConverter {

    private final static AWTImageConverter_8Bit INSTANCE   = new AWTImageConverter_8Bit();

    private AWTImageConverter_8Bit(){}

    protected static AWTImageConverter_8Bit getInstance() {
        return INSTANCE;
    }

    @Override
    public final FreeImage convert(BufferedImage bufferedImage)  {

        DataBuffer db   = bufferedImage.getData().getDataBuffer();
        if ( !(db instanceof DataBufferByte) ) {
            throw new FreeImageRuntimeException("Unsupported DataBuffer class \""+db.getClass()+"\" for 8 bit binary image type!");
        }

        // allocate image
        int w           = bufferedImage.getWidth();
        int h           = bufferedImage.getHeight();
        FreeImage fi    = new FreeImage(w,h,8, 0x00FF0000,0x0000FF00,0x000000FF);

        // copy palette
        this.copyPalette(bufferedImage, fi, 256);
        // copy indices
        ByteBuffer buf  = fi.getPixelBuffer();
        byte[] pixels   = ((DataBufferByte)db).getData();
        for ( int y = (h-1)*w; y >= 0; y-=w ) {
            buf.put(pixels,y,w);
        }

        return fi;
    }

	@Override
	public final BufferedImage convert(FreeImage image) {

		// create buffered image
		int w	= image.getWidth();
		int h	= image.getHeight();
		BufferedImage bufferedImage	= new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, this.copyPalette(image, 8));
		DataBuffer db				= bufferedImage.getRaster().getDataBuffer();
		if ( !(db instanceof DataBufferByte) ) {
			throw new FreeImageRuntimeException("Unexpected DataBuffer class \""+db.getClass()+"\" for 8 bit indexed RGB image type!");
		}
		byte[] indices	= ((DataBufferByte)db).getData();

		// copy indices
		ByteBuffer buf	= image.getPixelBuffer();
		int pitch		= image.getPitch();
		int dst			= 0;
		for ( int y = (h-1)*pitch; y >= 0; y-=pitch ) {
			int x1	= y + w;
			for ( int x = y; x < x1; x++ ) {
				indices[dst++]	= buf.get(x);
			}
		}

		return bufferedImage;
	}
}
