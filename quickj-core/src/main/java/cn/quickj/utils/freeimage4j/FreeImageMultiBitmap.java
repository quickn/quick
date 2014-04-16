package cn.quickj.utils.freeimage4j;

import java.io.File;

public final class FreeImageMultiBitmap {

	private final SWIGTYPE_p_FIMULTIBITMAP fiMultiBitmap;
	private boolean isClosed;

	/**
	 * Opens a multi-paged bitmap.
	 * The first parameter specifies the name of the bitmap.
	 * When the second parameter is true, it means that a new bitmap will be created
	 * rather than an existing one being opened. 
	 *
	 * The format of the bitmap-type of bitmap to be opened will be determined by its
	 * file extension. Currently FIF_TIFF, FIF_ICO and FIF_GIF are supported. If
	 * another type is specified an exception will be thrown.
	 * 
	 * @param file the name of the bitmap
	 * @param createNew if true create a new bitmap on disc, otherwise open existing one
	 * @throws FreeImageException if type does not support multiple pages or bitmap creation failed
	 */
	public FreeImageMultiBitmap(File file, boolean createNew) throws FreeImageException {
		this(file, FreeImage.getFileFormatsByExtension(file)[0], createNew);
	}
	/**
	 * Opens a multi-paged bitmap.
	 * The first parameter specifies the name of the bitmap.
	 * The second parameter tells FreeImage the bitmap-type of bitmap to be opened. Currently
	 * FIF_TIFF, FIF_ICO and FIF_GIF are supported. If another type is specified an exception will
	 * be thrown. When the third parameter is true, it means that a new bitmap will be created
	 * rather than an existing one being opened. 
	 *
	 * @param file the name of the bitmap
	 * @param format the bitmap-type of bitmap to be opened
	 * @param createNew if true create a new bitmap on disc, otherwise open existing one
	 * @throws FreeImageException if type does not support multiple pages or bitmap creation failed
	 */
	public FreeImageMultiBitmap(File file, int format, boolean createNew) throws FreeImageException {

		if ( format != FREE_IMAGE_FORMAT.FIF_ICO && format != FREE_IMAGE_FORMAT.FIF_GIF && format != FREE_IMAGE_FORMAT.FIF_TIFF ) {
			throw new IllegalArgumentException("Free image type \"" + format + "\" does not support multiple pages");
		}

		SWIGTYPE_p_FIMULTIBITMAP fiMultiBitmap	= FreeImageWrapper.FreeImage_OpenMultiBitmap(format, file.getAbsolutePath(), createNew, false, false, 0);
		if ( fiMultiBitmap == null ) {
			throw new FreeImageException("The bitmap \""+file.getAbsolutePath()+"\" could not be created!");
		}

		this.fiMultiBitmap	= fiMultiBitmap;
	}

	/**
	 * Closes a previously opened multi-page bitmap and applies any changes made to it.
	 */
	public final void close() {
		if ( this.isClosed ) {
			return;
		}
		FreeImageWrapper.FreeImage_CloseMultiBitmap(this.fiMultiBitmap, 0);
		this.isClosed	= true;
	}

	/**
	 * Finalize is overridden so it automatically calls close() during this objetc's finalization.
	 */
	@Override
	public final void finalize() {
		this.close();
	}

	private void checkState() {
		if ( this.isClosed ) {
			throw new FreeImageRuntimeException("Bitmap has been closed");
		}
	}
	private void checkBounds(int index) {
		if ( index < 0 || index >= this.getPageCount() ) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}

	/**
	 * Returns the number of pages currently available in this multi-paged bitmap.
	 * @return the number of pages currently available
	 */
	public final int getPageCount() {
		this.checkState();
		return FreeImageWrapper.FreeImage_GetPageCount(this.fiMultiBitmap);
	}

	/**
	 * Appends a new page to the end of the bitmap.
	 * 
	 * @param image the image to append
	 */
	public final void appendImage(FreeImage image) {
		this.checkState();
		FreeImageWrapper.FreeImage_AppendPage(this.fiMultiBitmap, image.fiBitmap);
	}

	/**
	 * Inserts a new page before the given position in the bitmap. Page has to be a number smaller
	 * than the current number of pages available in the bitmap.
	 * 
	 * @param image the image to be inserted
	 * @param index the position where to insert this image
	 * @throws FreeImageException if the given index is invalid
	 */
	public final void insertImage(FreeImage image, int index) throws FreeImageException {
		this.checkState();
		this.checkBounds(index);
		FreeImageWrapper.FreeImage_InsertPage(this.fiMultiBitmap, index, image.fiBitmap);
	}

	/**
	 * Deletes the page on the given position.
	 * 
	 * @param index the index of the page to be removed
	 */
	public final void delete(int index) {
		this.checkState();
		this.checkBounds(index);
		FreeImageWrapper.FreeImage_DeletePage(this.fiMultiBitmap, index);
	}

	/**
	 * Moves the source page to the position of the target page. Returns true on success, false
	 * on failure.
	 * 
	 * @param srcIndex the index of the source page
	 * @param dstIndex the index of the target page
	 * @return true on success, false otherwise
	 */
	public final boolean moveImage(int srcIndex, int dstIndex) {
		this.checkState();
		this.checkBounds(srcIndex);
		this.checkBounds(dstIndex);
		return FreeImageWrapper.FreeImage_MovePage(this.fiMultiBitmap, dstIndex, srcIndex);
	}
}
