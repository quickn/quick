package cn.quickj.utils.freeimage4j;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class FreeImage {

    // initialize free image library
    static {
        System.loadLibrary("freeimage4j");
        FreeImageWrapper.FreeImage_Initialise(false);
    }

	private final static Map<String, int[]> FILE_EXTENSION_MAP	= new HashMap<String, int[]>();
	static {
		FILE_EXTENSION_MAP.put("bmp", new int[]{FREE_IMAGE_FORMAT.FIF_BMP});
		FILE_EXTENSION_MAP.put("cut", new int[]{FREE_IMAGE_FORMAT.FIF_CUT});
		FILE_EXTENSION_MAP.put("dds", new int[]{FREE_IMAGE_FORMAT.FIF_DDS});
		FILE_EXTENSION_MAP.put("exr", new int[]{FREE_IMAGE_FORMAT.FIF_EXR});
		FILE_EXTENSION_MAP.put("g3",  new int[]{FREE_IMAGE_FORMAT.FIF_FAXG3});
		FILE_EXTENSION_MAP.put("gif", new int[]{FREE_IMAGE_FORMAT.FIF_GIF});
		FILE_EXTENSION_MAP.put("hdr", new int[]{FREE_IMAGE_FORMAT.FIF_HDR});
		FILE_EXTENSION_MAP.put("ico", new int[]{FREE_IMAGE_FORMAT.FIF_ICO});
		FILE_EXTENSION_MAP.put("iff", new int[]{FREE_IMAGE_FORMAT.FIF_IFF});
		FILE_EXTENSION_MAP.put("ilbm",new int[]{FREE_IMAGE_FORMAT.FIF_LBM});
		FILE_EXTENSION_MAP.put("lbm", new int[]{FREE_IMAGE_FORMAT.FIF_LBM});
		FILE_EXTENSION_MAP.put("j2c", new int[]{FREE_IMAGE_FORMAT.FIF_J2K});
		FILE_EXTENSION_MAP.put("j2k", new int[]{FREE_IMAGE_FORMAT.FIF_J2K});
		FILE_EXTENSION_MAP.put("jng", new int[]{FREE_IMAGE_FORMAT.FIF_JNG});
		FILE_EXTENSION_MAP.put("jp2", new int[]{FREE_IMAGE_FORMAT.FIF_JP2});
		FILE_EXTENSION_MAP.put("jif", new int[]{FREE_IMAGE_FORMAT.FIF_JPEG});
		FILE_EXTENSION_MAP.put("jpe", new int[]{FREE_IMAGE_FORMAT.FIF_JPEG});
		FILE_EXTENSION_MAP.put("jpeg",new int[]{FREE_IMAGE_FORMAT.FIF_JPEG});
		FILE_EXTENSION_MAP.put("jpg", new int[]{FREE_IMAGE_FORMAT.FIF_JPEG});
		FILE_EXTENSION_MAP.put("koa", new int[]{FREE_IMAGE_FORMAT.FIF_KOALA});
		FILE_EXTENSION_MAP.put("mng", new int[]{FREE_IMAGE_FORMAT.FIF_MNG});
		FILE_EXTENSION_MAP.put("pbm", new int[]{FREE_IMAGE_FORMAT.FIF_PBMRAW, FREE_IMAGE_FORMAT.FIF_PBM});
		FILE_EXTENSION_MAP.put("pcd", new int[]{FREE_IMAGE_FORMAT.FIF_PCD});
		FILE_EXTENSION_MAP.put("pcx", new int[]{FREE_IMAGE_FORMAT.FIF_PCX});
		FILE_EXTENSION_MAP.put("pgm", new int[]{FREE_IMAGE_FORMAT.FIF_PGMRAW, FREE_IMAGE_FORMAT.FIF_PGM});
		FILE_EXTENSION_MAP.put("png", new int[]{FREE_IMAGE_FORMAT.FIF_PNG});
		FILE_EXTENSION_MAP.put("ppm", new int[]{FREE_IMAGE_FORMAT.FIF_PPMRAW, FREE_IMAGE_FORMAT.FIF_PPM});
		FILE_EXTENSION_MAP.put("psd", new int[]{FREE_IMAGE_FORMAT.FIF_PSD});
		FILE_EXTENSION_MAP.put("ras", new int[]{FREE_IMAGE_FORMAT.FIF_RAS});
		FILE_EXTENSION_MAP.put("sgi", new int[]{FREE_IMAGE_FORMAT.FIF_SGI});
		FILE_EXTENSION_MAP.put("tga", new int[]{FREE_IMAGE_FORMAT.FIF_TARGA});
		FILE_EXTENSION_MAP.put("targa",new int[]{FREE_IMAGE_FORMAT.FIF_TARGA});
		FILE_EXTENSION_MAP.put("tif", new int[]{FREE_IMAGE_FORMAT.FIF_TIFF});
		FILE_EXTENSION_MAP.put("tiff", new int[]{FREE_IMAGE_FORMAT.FIF_TIFF});
		FILE_EXTENSION_MAP.put("wap", new int[]{FREE_IMAGE_FORMAT.FIF_WBMP});
		FILE_EXTENSION_MAP.put("wbm", new int[]{FREE_IMAGE_FORMAT.FIF_WBMP});
		FILE_EXTENSION_MAP.put("wbmp", new int[]{FREE_IMAGE_FORMAT.FIF_WBMP});
		FILE_EXTENSION_MAP.put("xbm", new int[]{FREE_IMAGE_FORMAT.FIF_XBM});
		FILE_EXTENSION_MAP.put("xpm", new int[]{FREE_IMAGE_FORMAT.FIF_XPM});
	}

    // start cleanup thread
    private final static FreeImageCleanup CLEANUP_THREAD	= new FreeImageCleanup();

    private	final int width;
    private final int height;
    private final int pitch;
    private final int bpp;

    protected final ByteBuffer byteBuffer;
    protected final SWIGTYPE_p_FIBITMAP fiBitmap;

    /**
	 * Tries to load the specified image and create a new FreeImage instance.
	 * If the format is not supported an FreeImageException will be thrown.
	 * 
	 * @param file
	 *            The image to be loaded.
	 * 
	 * @throws FreeImageException
	 *             if the format is not supported
	 */
	public FreeImage(File file) throws FreeImageException {
		this(FreeImage.load(file));
	}

	public FreeImage(int width,int height,int bpp, int redMask,int greenMask,int blueMask) {
		this(FreeImage.allocate(width, height, bpp, redMask, greenMask, blueMask));
	}

    /**
     * constructor
     */
    private FreeImage(SWIGTYPE_p_FIBITMAP fiBitmap) {

        this.width		= FreeImageWrapper.FreeImage_GetWidth(fiBitmap);
        this.pitch		= FreeImageWrapper.FreeImage_GetPitch(fiBitmap);
        this.height		= FreeImageWrapper.FreeImage_GetHeight(fiBitmap);
        this.bpp		= FreeImageWrapper.FreeImage_GetBPP(fiBitmap);
        this.fiBitmap	= fiBitmap;

        this.byteBuffer	= FreeImageWrapper.FreeImage_GetBits(fiBitmap);

        // register directbytebuffer and fiBitmap for GC cleanup
        CLEANUP_THREAD.register(this);
    }

    /**
     * Returns a string containing the current version of the library.
     * 
     * @return a string containing the current version of the library.
     */
    public static String getVersion() {
        return FreeImageWrapper.FreeImage_GetVersion();
    }

    /**
     * Returns a string containing a standard copyright message you can show in your program.
     * 
     * @return a string containing a standard copyright message you can show in your program.
     */
    public static String getCopyrightMessage() {
        return FreeImageWrapper.FreeImage_GetCopyrightMessage();
    }

    /**
	 * This function returns true if the platform running FreeImage uses the
	 * Little Endian convention (Intel processors) and returns false if it uses
	 * the Big Endian convention (Motorola processors).
	 * 
	 * @return true on intel machine, true on motorola cpus
	 */
    public static boolean isLittleEndian() {
    	return FreeImageWrapper.FreeImage_IsLittleEndian();
    }

    /**
	 * Orders FreeImage to analyze the bitmap signature. The function then
	 * returns one of the predefined FREE_IMAGE_FORMAT constants. Because 
	 * not all formats can be identified by their header (some images don't
	 * have a header or one at the end of the file), getFileType may return
	 * FIF_UNKNOWN.
	 * 
	 * @param file The file to be examined.
	 * @return one of the predefined FREE_IMAGE_FORMAT constants.
	 */
    public static int getFileType(File file) {
        // is file type supported ?
        return FreeImageWrapper.FreeImage_GetFileType(file.getAbsolutePath(), 0);
    }

    private static SWIGTYPE_p_FIBITMAP load(File file) throws FreeImageException {

        String fileName = file.getAbsolutePath();

        // is file type supported ?
        int fileType    = FreeImage.getFileType(file);
        if ( fileType == FREE_IMAGE_FORMAT.FIF_UNKNOWN ) {
            throw new FreeImageException("Unsupported file type, file =  " + fileName );
        }

        // load file
        SWIGTYPE_p_FIBITMAP fiBitmap	= FreeImageWrapper.FreeImage_Load(fileType, fileName, 0);
        // successfully loaded ?
        if ( fiBitmap == null ) {
            throw new FreeImageException("Loading of file \"" + fileName + "\" failed.");            
        }

        return fiBitmap;
    }

    private static SWIGTYPE_p_FIBITMAP allocate(int width, int height, int bpp, int redMask,int greenMask,int blueMask) {

    	// allocate image
    	SWIGTYPE_p_FIBITMAP fiBitmap	= FreeImageWrapper.FreeImage_Allocate(width, height, bpp, redMask, greenMask, blueMask);
        // successfully allocated ?
        if ( fiBitmap == null ) {
            throw new FreeImageRuntimeException("Allocation of image failed.");            
        }

        return fiBitmap;
    }

    /**
	 * Converts a raw bitmap somewhere in memory to a FreeImage. The parameters
	 * in this function are used to describe the raw bitmap. The first parameter
	 * is a pointer to the start of the raw bits wrapped in a ByteBuffer object.
	 * The width and height parameter describe the size of the bitmap. The pitch
	 * defines the total width of a scanline in the source bitmap, including padding
	 * bytes that may be applied. The bpp parameter tells FreeImage what the bit
	 * depth of the bitmap is. The red_mask, green_mask and blue_mask parameters tell
	 * FreeImage the bit-layout of the color components in the bitmap. The last
	 * parameter, topdown, will store the bitmap top-left pixel first when it is
	 * true or bottom-left pixel first when it is false. When the source bitmap
	 * uses a 32-bit padding, you can calculate the pitch using the following
	 * formula: int pitch = ((((bpp * width) + 31) / 32) * 4);
	 * 
	 * @param buffer pointer to the raw bits in memory 
	 * @param width width of bitmap in pixel units
	 * @param height height of bitmap in pixel units
	 * @param pitch actual width in bytes
	 * @param bpp bits per pixel
	 * @param redMask mask for the red channel
	 * @param greenMask mask for the green channel
	 * @param blueMask mask for the blue channel
	 * @param topDown flag to signal whether to store image top down or not
	 * 
	 * @return a FreeImage instance
	 */
    public static FreeImage convertFromRawBits(ByteBuffer buffer, int width,int height,int pitch,int bpp, int redMask,int greenMask,int blueMask, boolean topDown) {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertFromRawBits(buffer, width,height,pitch, bpp, redMask, greenMask, blueMask, topDown);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("convertFromRawBits failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
	 * Converts a FreeImage instance to a raw piece of memory. The layout of the memory is
	 * described in the passed parameters, which are the same as in the previous
	 * function. The last parameter, topdown, will store the bitmap top-left
	 * pixel first when it is true or bottom-left pixel first when it is false.
	 * 
	 * @param buffer pointer to the raw bits in memory 
	 * @param pitch actual width in bytes
	 * @param bpp bits per pixel
	 * @param redMask mask for the red channel
	 * @param greenMask mask for the green channel
	 * @param blueMask mask for the blue channel
	 * @param topDown flag to signal whether to store image top down or not
	 */
    public final void convertToRawBits(ByteBuffer buffer, int pitch, int bpp, int redMask,int greenMask,int blueMask, boolean topDown) {
    	FreeImageWrapper.FreeImage_ConvertToRawBits(buffer, this.fiBitmap, pitch, bpp, redMask, greenMask, blueMask, topDown);
    }

    /**
	 * Tries to determine the bitmap file by its extension. This function is particularly useful
	 * when saving images. If the filetype is unknown an exception will be thrown<br><br>
	 * NB: This function returns an array containing possible image formats. Usually there is only
	 * one entry in this array because extensions and file formats can be mapped one to one. But there
	 * are some special cases, e.g. ppm, where two possible format can be associated, e.g. ppm(ascii) or
	 * ppm(raw).
	 * 
	 * @param file
	 *            The file to be examined.
	 * @return one of the predefined FREE_IMAGE_FORMAT constants.
	 * 
	 * @throws FreeImageException
	 *             if the format cannot be determined by its extension
	 */
	public static int[] getFileFormatsByExtension(File file) throws FreeImageException {

		String name = file.getName();
		int idx = name.lastIndexOf('.');
		// file extension found ?
		if ( idx == -1 ) {
			// no -> could not determine file format
			throw new FreeImageException("File does not have an extension: \"" + file.getAbsolutePath() + "\"");
		}

		String ext		= name.substring(idx+1).toLowerCase(); 
		int[] formats	= FILE_EXTENSION_MAP.get(ext);
		// supported file format ?
		if ( formats == null ) {
			// no -> could not determine file format
			throw new FreeImageException("File format is not supported: \"" + file.getAbsolutePath() + "\"");
		}

		return formats;
	}

	/**
	 * Returns a pointer wrapped in a DirectByteBuffer object to the data-bits
	 * of the bitmap. It is up to you to interpret these bytes correctly, according
	 * to the results of getBitsPerPixel, getRedMask, getGreenMask and
	 * getBlueMask. For a performance reason, the address returned 
	 * is aligned on a 16 bytes alignment boundary.
	 * 
	 * @return a pointer wrapped in a DirectByteBuffer object to the data-bits of the bitmap
	 */
    public final ByteBuffer getPixelBuffer() {
    	return this.byteBuffer;
    }

    /**
	 * Saves the image. It tries to identify the desired file type by calling
	 * the method getFileFormatByExtension. If the file type cannot be
	 * determined by its extension or the image format is not supported for
	 * saving this image an exception will be thrown.
	 * 
	 * @param file
	 *            the destination file name
	 * @throws FreeImageException
	 *             If the file type cannot be determined by its extension or the
	 *             image format is not supported for saving this image an
	 *             exception will be thrown.
	 */
    public final void save(File file) throws FreeImageException {
    	this.save(file, FreeImage.getFileFormatsByExtension(file)[0]);
    }

    /**
	 * Saves the image using the specified image format from FREE_IMAGE_FORMAT.
	 * If the image format is not supported for saving this image an exception
	 * will be thrown.
	 * 
	 * @param file
	 *            the destination file name
	 * @param format
	 *            the desired destination image format. Must be one of the
	 *            FREE_IMAGE_FORMAT constants
	 * @throws FreeImageException
	 *             If the file type cannot be determined by its extension or the
	 *             image format is not supported for saving this image an
	 *             exception will be thrown.
	 */
    public final void save(File file, int format) throws FreeImageException {
        String fileName = file.getAbsolutePath();
        if ( !FreeImageWrapper.FreeImage_Save(format, this.fiBitmap, fileName, 0) ) {
            throw new FreeImageException("Could not save file: " + fileName );
        }
    }

    /**
     * Returns the width of the bitmap in pixel units.
     * 
     * @return the width of the bitmap in pixel units.
     */
    public final int getWidth() {
    	return this.width;
    }

    /**
	 * Returns the width of the bitmap in bytes, rounded to the next 32-bit
	 * boundary, also known as pitch or stride or scan width.<br>
	 * In FreeImage each scanline starts at a 32-bit boundary for performance
	 * reasons. This accessor is essential when using low level pixel manipulation
	 * functions.
	 * 
	 * @return the width of the bitmap in bytes, rounded to the next 32-bit boundary
	 */
    public final int getPitch() {
    	return this.pitch;
    }
    /**
     * Returns the height of the bitmap in pixel units.
     * 
     * @return the height of the bitmap in pixel units.
     */
    public final int getHeight() {
    	return this.height;
    }
    /**
	 * Returns the size of one pixel in the bitmap in bits. For example when
	 * each pixel takes 32-bits of space in the bitmap, this function returns
	 * 32. Possible bit depths are 1, 4, 8, 16, 24, 32 for standard bitmaps and
	 * 16-, 32-, 48-, 64-, 96- and 128-bit for non standard bitmaps.
	 * 
	 * @return the size of one pixel in the bitmap in bits
	 */
    public final int getBitsPerPixel() {
    	return this.bpp;
    }

    /**
	 * Returns true when the transparency table is enabled (1-, 4- or 8-bit
	 * images) or when the input dib contains alpha values (32-bit images).
	 * Returns false otherwise.<br>
	 * <br>
	 * 
	 * NB: This method really examines the pixels! So if you change a pixel by
	 * directly modifying it in the memory and call this method afterwards the
	 * result can change! This is pretty useful. If you think this is too much
	 * of a performance penalty go ahead an cache the result yourself ;-)
	 * 
	 * @return true if there is any transparent pixel, false otherwise.
	 */
    public final boolean isTransparent() {
    	return FreeImageWrapper.FreeImage_IsTransparent(this.fiBitmap);
    }
    // color manipulation routines (point operations)
	/**
	 * Performs gamma correction on a 8-, 24- or 32-bit image. The gamma
	 * parameter represents the gamma value to use (gamma > 0). A value of 1.0
	 * leaves the image alone, less than one darkens it, and greater than one
	 * lightens it. The function returns true on success. It returns false when
	 * gamma is less than or equal to zero or when the bitdepth of the source
	 * dib cannot be handled.
	 * 
	 * @param gamma
	 *            The adjustment amount
	 * @return true on success false otherwise
	 */
    public final boolean adjustGamma(double gamma) {
        return FreeImageWrapper.FreeImage_AdjustGamma(this.fiBitmap, gamma);
    }

	/**
	 * Adjusts the brightness of a 8-, 24- or 32-bit image by a certain amount.
	 * This amount is given by the percentage parameter, where percentage is a
	 * value between [-100..100]. A value 0 means no change, less than 0 will
	 * make the image darker and greater than 0 will make the image brighter.
	 * The function returns true on success, false otherwise (e.g. when the
	 * bitdepth of the source dib cannot be handled).
	 * 
	 * @param percentage
	 *            The adjustment amount
	 * @return true on success false otherwise
	 */
    public final boolean adjustBrightness(double percentage) {
        return FreeImageWrapper.FreeImage_AdjustBrightness(this.fiBitmap, percentage);
    }
    /**
	 * Adjusts the contrast of a 8-, 24- or 32-bit image by a certain amount.
	 * This amount is given by the percentage parameter, where percentage is a
	 * value between [-100..100]. A value 0 means no change, less than 0 will
	 * decrease the contrast and greater than 0 will increase the contrast of
	 * the image. The function returns true on success, false otherwise (e.g.
	 * when the bitdepth of the source dib cannot be handled).
	 * 
	 * @param percentage
	 *            The adjustment amount
	 * @return true on success false otherwise
	 */
    public final boolean adjustContrast(double percentage) {
        return FreeImageWrapper.FreeImage_AdjustContrast(this.fiBitmap, percentage);
    }

    /**
	 * Inverts each pixel data.
	 * 
	 * @return true on success, false otherwise
	 */
    public final boolean invert() {
    	return FreeImageWrapper.FreeImage_Invert(this.fiBitmap);
    }

    // rotation and flipping
	/**
	 * This method rotates the image counter clockwise by the desired angle.
	 * The angle must be specified in degrees. Note that the rotated image
	 * may be larger than the original image because this method will always
	 * adjust the size of the result so that all pixels are visible.
	 * 
	 * @param angle
	 *            degrees to be rotated in counter clockwise order
	 * @return new instance of FreeImage
	 */
    public final FreeImage rotateClassic(double angle) {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_RotateClassic(this.fiBitmap, angle);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("Classic Rotation failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
	 * Counter clockwise rotates the image by some angle around an arbitrary
	 * origin, while an additional translation can be performed afterwards. The
	 * angle must be given in degrees. For example if you want to rotate an
	 * image around its center you can do it this way:<br>
	 * <br>
	 * 
	 * fi.rotateEx(10, 0,0, fi.getWidth()*0.5,fi.getHeight()*0.5, false);
	 * 
	 * <br>
	 * <br>
	 * Note that the size of the original image will always be preserved. The
	 * useMask flag signals whether empty corners shall be filled with the
	 * original image or be just masked away.
	 * 
	 * @param angle
	 *            degrees to be rotated in counter clockwise order
	 * @param xShift
	 *            Horizontal translation in pixel units
	 * @param yShift
	 *            Vertical translation in pixel units
	 * @param xOrigin
	 *            The horizontal origin of the rotation
	 * @param yOrigin
	 *            The vertical origin of the rotation
	 * @param useMask
	 *            true means empty corners will be masked away.
	 * @return new instance of FreeImage
	 */
    public final FreeImage rotateEx(double angle,double xShift,double yShift, double xOrigin,double yOrigin, boolean useMask) {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_RotateEx(this.fiBitmap, angle, xShift, yShift, xOrigin, yOrigin, useMask);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("Extended Rotation failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
     * Flip the image horizontally along the vertical axis.
     * 
	 * @return true on success false otherwise
     */
    public final boolean flipHorizontal() {
    	return FreeImageWrapper.FreeImage_FlipHorizontal(this.fiBitmap);
    }
    /**
     * Flip the image vertically along the horizontal axis.
     * 
	 * @return true on success false otherwise
     */
    public final boolean flipVertical() {
    	return FreeImageWrapper.FreeImage_FlipVertical(this.fiBitmap);
    }

    // upsampling / downsampling
	/**
	 * This function performs resampling (or scaling, zooming) of a greyscale or
	 * RGB(A) image to the desired destination width and height. A
	 * FreeImageRuntimeException is thrown when the bitdepth cannot be handled
	 * or when there’s not enough memory (this may happen with very large
	 * images). 16-bit RGB bitmap are returned as 24-bit. Palettized and 4-bit
	 * bitmap are returned as 8-bit palettized images, using an internal
	 * conversion to 24-bit followed by a color quantization, or are returned as
	 * 32-bit if they contain transparency. Resampling refers to changing the
	 * pixel dimensions (and therefore display size) of an image. When you
	 * downsample (or decrease the number of pixels), information is deleted
	 * from the image. When you upsample (or increase the number of pixels), new
	 * pixels are added based on color values of existing pixels. You specify an
	 * interpolation filter to determine how pixels are added or deleted. The
	 * following filters can be used as resampling filters:
	 * 
	 * FILTER_BOX Box, pulse, Fourier window, 1st order (constant) B-Spline<br>
	 * FILTER_BILINEAR Bilinear filter<br>
	 * FILTER_BSPLINE 4th order (cubic) B-Spline<br>
	 * FILTER_BICUBIC Mitchell and Netravali's two-param cubic filter<br>
	 * FILTER_CATMULLROM Catmull-Rom spline, Overhauser spline<br>
	 * FILTER_LANCZOS3 Lanczos-windowed sinc filter<br>
	 * 
	 * @return new instance of FreeImage
	 * 
	 */
    public final FreeImage rescale(int dstWidth,int dstHeight, int filter) {
    	SWIGTYPE_p_FIBITMAP fiBitmap = FreeImageWrapper.FreeImage_Rescale(this.fiBitmap, dstWidth, dstHeight, filter);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("rescale failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
	 * Creates a thumbnail from a greyscale or RGB(A) image so that the output
	 * image fits inside a square of size max_pixel_size, keeping aspect ratio.
	 * Downsampling is done using a bilinear filter (see FreeImage_Rescale).
	 * 16-bit RGB bitmap are returned as 24-bit. Palettized and 4-bit bitmap are
	 * returned as 8-bit or as 32-bit if they contain transparency. When the
	 * convert parameter is set to true, High Dynamic Range images (FIT_UINT16,
	 * FIT_RGB16, FIT_RGBA16, FIT_FLOAT) are transparently converted to standard
	 * images (i.e. 8-, 24 or 32-bit images), using one of the
	 * convertToXXX conversion function. As for RBG[A]F images, they
	 * are converted to 24-bit using the FreeImage_TmoDrago03 function with
	 * default options.
	 * 
	 * @param size the destination size in pixel units
	 * @param convertHDR
	 * @return new instance of FreeImage
	 */
    public final FreeImage makeThumbnail(int size, boolean convertHDR) {
    	SWIGTYPE_p_FIBITMAP fiBitmap = FreeImageWrapper.FreeImage_MakeThumbnail(this.fiBitmap, size, convertHDR);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("make thumbnail failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    

    // Smart conversion routines
    /**
     * Converts a bitmap to 4 bits. If the bitmap was a high-color bitmap (16, 24 or 32-bit) or if it was
     * a monochrome or greyscale bitmap (1 or 8-bit), the end result will be a greyscale bitmap,
     * otherwise (1-bit palletised bitmaps) it will be a palletised bitmap. A clone of the input bitmap is
     * returned for 4-bit bitmaps.<br>
     * NB: here "greyscale" means that the resulting bitmap will have grey colors, but the palette
     * won’t be a linear greyscale palette. Thus, FreeImage_GetColorType will return
     * FIC_PALETTE.
     * 
     * @return new instance of FreeImage
     */
    public final FreeImage convertTo4Bits() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertTo4Bits(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    
    /**
	 * Converts a bitmap to 8 bits. If the bitmap was a high-color bitmap (16,
	 * 24 or 32-bit) or if it was a monochrome or greyscale bitmap (1 or 4-bit),
	 * the end result will be a greyscale bitmap, otherwise (1 or 4-bit
	 * palletised bitmaps) it will be a palletised bitmap. A clone of the input
	 * bitmap is returned for 8-bit bitmaps.<br>
	 * When creating the greyscalepalette, the greyscale intensity of a result 
	 * pixel is based on red, green, and blue levels of the corresponding source
	 * pixel using the following formula:<br>
	 * grey = 0.299 x R + 0.587 x G + 0.114 x B<br>
	 * The values 0.299, 0.587 and 0.114 represent the relative red, green, and blue
	 * intensities.<br>
	 * For 16-bit greyscale images (images whose type is
	 * FIT_UINT16), conversion is done by dividing the 16-bit channel by 256
	 * (see also convertToStandardType). A FreeImageRuntimeException is thrown for
	 * other non-standard bitmap types.
	 * 
     * @return new instance of FreeImage
	 */
    public final FreeImage convertTo8Bits() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertTo8Bits(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }

    /**
	 * Converts a bitmap to a 8-bit greyscale image with a linear ramp. Contrary
	 * to the FreeImage_ConvertTo8Bits function, 1-, 4- and 8-bit palletised
	 * images are correctly converted, as well as images with a FIC_MINISWHITE
	 * color type.
	 * 
     * @return new instance of FreeImage
	 */
    public final FreeImage convertToGreyscale() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertToGreyscale(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }

    /**
	 * Converts a bitmap to 16 bits, where each pixel has a color pattern of 5
	 * bits red, 5 bits green and 5 bits blue. One bit in each pixel is unused.
	 * A clone of the input bitmap is returned for 16- bit 555 bitmaps
	 *
     * @return new instance of FreeImage
	 */
    public final FreeImage convertTo16Bits555() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertTo16Bits555(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
	 * Converts a bitmap to 16 bits, where each pixel has a color pattern of 5
	 * bits red, 6 bits green and 5 bits blue. A clone of the input bitmap is
	 * returned for 16-bit 565 bitmaps
	 * 
	 * @return new instance of FreeImage
	 */
    public final FreeImage convertTo16Bits565() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertTo16Bits565(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
	 * Converts a bitmap to 24 bits. A clone of the input bitmap is returned for
	 * 24-bit bitmaps. For 48-bit RGB images, conversion is done by dividing
	 * each 16-bit channel by 256. A FreeImageRuntimeException is thrown for
	 * other non-standard bitmap types.
	 * 
	 * @return new instance of FreeImage
	 */
    public final FreeImage convertTo24Bits() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertTo24Bits(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }
    /**
	 * Converts a bitmap to 32 bits. A clone of the input bitmap is returned for
	 * 32-bit bitmaps. For 48-bit RGB images, conversion is done by dividing
	 * each 16-bit channel by 256 and by setting the alpha channel to an opaque
	 * value (0xFF). For 64-bit RGBA images, conversion is done by dividing each
	 * 16-bit channel by 256. A FreeImageRuntimeException is thrown for other non-standard
	 * bitmap types.
	 * 
	 * @return new instance of FreeImage
	 */
    public final FreeImage convertTo32Bits() {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ConvertTo32Bits(this.fiBitmap);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("conversion failed");
    	}
    	return new FreeImage(fiBitmap);
    }

    /**
     * Quantizes a high-color 24-bit bitmap to an 8-bit palette color bitmap. The quantizeAlgorithm parameter
     * specifies the color reduction algorithm to be used:<br>
     * 
     * FIQ_WUQUANT       Xiaolin Wu color quantization algorithm<br>
     * FIQ_NNQUANT       NeuQuant neural-net quantization algorithm by Anthony Dekker<br>
     * 
     * References:
     * Wu, Xiaolin, Efficient Statistical Computations for Optimal Color Quantization. In Graphics
     * Gems, vol. II, p. 126-133. [Online] http://www.ece.mcmaster.ca/~xwu/<br>
     * Dekker A. H., Kohonen neural networks for optimal color quantization. Network: Computation
     * in Neural Systems, Volume 5, Number 3, Institute of Physics Publishing, 1994. [Online] http://
     * members.ozemail.com.au/~dekker/NEUQUANT.HTML<br>
     *
     * @param quantizeAlgorithm The algorithm to use (see also FREE_IMAGE_QUANTIZE)
	 * @return new instance of FreeImage
     */
    public final FreeImage colorQuantize(int quantizeAlgorithm) {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_ColorQuantize(this.fiBitmap, quantizeAlgorithm);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("colorQuantize failed");
    	}
    	return new FreeImage(fiBitmap);
    }

    /**
	 * Converts a bitmap to 1-bit monochrome bitmap using a threshold T between
	 * [0..255]. The function first converts the bitmap to a 8-bit greyscale
	 * bitmap. Then, any brightness level that is less than T is set to zero,
	 * otherwise to 1. For 1-bit input bitmaps, the function clones the input
	 * bitmap and builds a monochrome palette.
	 * 
	 * @param threshold The threshold to use
	 * @return new instance of FreeImage
	 */
    public final FreeImage threshold(int threshold) {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_Threshold(this.fiBitmap, (short)threshold);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("threshold failed");
    	}
    	return new FreeImage(fiBitmap);
    }

    /**
	 * Converts a bitmap to 1-bit monochrome bitmap using a dithering algorithm.
	 * For 1-bit input bitmaps, the function clones the input bitmap and builds
	 * a monochrome palette. The algorithm parameter specifies the dithering
	 * algorithm to be used. The function first converts the bitmap to a 8-bit
	 * greyscale bitmap. Then, the bitmap is dithered using one of the following
	 * algorithms:<br>
	 * <br>
	 * FID_FS           Floyd & Steinberg error diffusion algorithm<br>
	 * FID_BAYER4x4     Bayer ordered dispersed dot dithering (order 2 – 4x4 -dithering matrix)<br>
	 * FID_BAYER8x8     Bayer ordered dispersed dot dithering (order 3 – 8x8 -dithering matrix)<br>
	 * FID_BAYER16x16   Bayer ordered dispersed dot dithering (order 4 – 16x16 dithering matrix)<br>
	 * FID_CLUSTER6x6   Ordered clustered dot dithering (order 3 - 6x6 matrix)<br>
	 * FID_CLUSTER8x8   Ordered clustered dot dithering (order 4 - 8x8 matrix)<br>
	 * FID_CLUSTER16x16 Ordered clustered dot dithering (order 8 - 16x16 matrix)<br>
	 * <br>
	 * References<br>
	 * Ulichney, R., Digital Halftoning. The MIT Press, Cambridge, MA, 1987.<br>
	 * Hawley S., Ordered Dithering. Graphics Gems, Academic Press, 1990.<br>
	 * 
	 * @param ditherAlgorithm dithering algorithm to use (see also FREE_IMAGE_DITHER)
	 * @return new instance of FreeImage
	 */
    public final FreeImage dither(int ditherAlgorithm) {
    	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_Dither(this.fiBitmap, ditherAlgorithm);
    	if ( fiBitmap == null ) {
    		throw new FreeImageRuntimeException("dither failed");
    	}
    	return new FreeImage(fiBitmap);
    }

    /**
	 * Get the pixel color of a 16-, 24- or 32-bit image at position (x, y),
	 * including range check (slow access). Parameter x is the pixel position in
	 * horizontal direction, and parameter y is the pixel position in vertical
	 * direction.
	 * 
	 * @param x
	 *            horizontal position
	 * @param y
	 *            vertical position
	 * @return the color of a pixel
	 */
    public final int getPixelColor(int x, int y) {
    	return FreeImageWrapper.GetPixelColor(this.fiBitmap, x, y);
    }

    private void checkPaletteIndex(int idx) {
    	int bpp	= this.bpp;
    	if ( bpp > 8 ) {
    		throw new NullPointerException("This image does not have a palette");
    	}

    	if ( idx < 0 || idx >= (1<<bpp) ) {
    		throw new ArrayIndexOutOfBoundsException(idx);
    	}
    }
    public final int getPaletteColor(int idx) {
    	this.checkPaletteIndex(idx);

    	return FreeImageWrapper.GetPaletteColor(this.fiBitmap, idx);
    }
    public final void setPaletteColor(int idx, int color) {
    	this.checkPaletteIndex(idx);

    	FreeImageWrapper.SetPaletteColor(this.fiBitmap, idx, color);
    }

    /**
	 * Get the pixel index of a palettized image at position (x, y), including
	 * range check (slow access). Parameter x is the pixel position in
	 * horizontal direction, and parameter y is the pixel position in vertical
	 * direction.
	 * 
	 * @param x
	 *            horizontal position
	 * @param y
	 *            vertical position
	 * @return pixel index of a palettized image
	 */
    public final short getPixelIndex(int x, int y) {
    	return FreeImageWrapper.GetPixelIndex(this.fiBitmap, x, y);
    }

	/**
	 * Set the pixel color of a 16-, 24- or 32-bit image at position (x, y),
	 * including range check (slow access). Parameter x is the pixel position in
	 * horizontal direction, and parameter y is the pixel position in vertical
	 * direction.
	 * 
	 * @param x
	 *            horizontal position
	 * @param y
	 *            vertical position
	 * @param color
	 *            the RGB color
	 */
    public final void setPixelColor(int x,int y, int color) {
    	FreeImageWrapper.SetPixelColor(this.fiBitmap, x, y, color);
    }
    /**
	 * Set the pixel index of a palettized image at position (x, y), including
	 * range check (slow access). Parameter x is the pixel position in
	 * horizontal direction, and parameter y is the pixel position in vertical
	 * direction.
	 * 
	 * @param x
	 *            horizontal position
	 * @param y
	 *            vertical position
	 * @param index
	 *            the pixel index
	 * 
	 */
    public final void setPixelIndex(int x,int y, short index) {
    	FreeImageWrapper.SetPixelIndex(this.fiBitmap, x, y, index);
    }

    /**
     * Copy a sub part of the current dib image. The rectangle defined by the (left, top, right, bottom)
     * parameters is first normalized such that the value of the left coordinate is less than the right
     * and the top is less than the bottom. Then, the returned bitmap is defined by a width equal to
     * (right - left) and a height equal to (bottom - top).
     * 
     * @param left specifies the left position of the cropped rectangle.
     * @param top specifies the top position of the cropped rectangle.
     * @param right specifies the right position of the cropped rectangle.
     * @param bottom specifies the bottom position of the cropped rectangle.
     * 
     * @return The subimage if successful and null otherwise.
     */
    public final FreeImage copy(int left,int top, int right,int bottom) {
       	SWIGTYPE_p_FIBITMAP fiBitmap= FreeImageWrapper.FreeImage_Copy(this.fiBitmap, left,top,right,bottom);
       	if ( fiBitmap == null ) {
       		throw new FreeImageRuntimeException("copy failed");
       	}
       	return new FreeImage(fiBitmap);
    }
}
