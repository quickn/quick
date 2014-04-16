package cn.quickj.utils.freeimage4j;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.Buffer;

import java.util.HashMap;
import java.util.Map;

final class FreeImageCleanup implements Runnable {

	// Maps PhantomReferences to fiBitmaps
	private Map<Reference<Buffer>, SWIGTYPE_p_FIBITMAP> refToBitmapMap;

	// Reference queue which gets notified
	private ReferenceQueue<Buffer> queue;

	FreeImageCleanup() { // package-private
		this.refToBitmapMap	= new HashMap<Reference<Buffer>, SWIGTYPE_p_FIBITMAP>();
		this.queue			= new ReferenceQueue<Buffer>();
		Thread thread		= new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public final void run() {
		while (true) {
			try {
				Reference<? extends Buffer> r	= this.queue.remove();
				SWIGTYPE_p_FIBITMAP fiBitmap;
				synchronized (this) {
					fiBitmap					= this.refToBitmapMap.remove(r);					
				}
				// free memory of free image bitmap in c world
				FreeImageWrapper.FreeImage_Unload(fiBitmap);
				r.clear();
			} catch (InterruptedException e) {
			}
		}
	}

	public final void register(FreeImage freeImage) {

		synchronized (this) {
			// add new phantom reference
			this.refToBitmapMap.put(new PhantomReference<Buffer>(freeImage.byteBuffer, this.queue), freeImage.fiBitmap);			
		}
	}
}