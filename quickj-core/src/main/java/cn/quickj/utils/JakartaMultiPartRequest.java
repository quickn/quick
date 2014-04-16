package cn.quickj.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Multipart form data request adapter for Jakarta's file upload package.
 * 
 * @author Bruce Ritchie
 */
@SuppressWarnings("unchecked")
public class JakartaMultiPartRequest extends MultiPartRequest {
	private static Log log = LogFactory.getLog(JakartaMultiPartRequest.class);

	// any errors while processing this request
	private List<String> errors = new ArrayList<String>();
	// maps parameter name -> List of FileItem objects
	private Map<String, List> files = new HashMap<String, List>();
	// maps parameter name -> List of param values
	private Map<String, List> params = new HashMap<String, List>();

	/**
	 * Creates a new request wrapper to handle multi-part data using methods
	 * adapted from Jason Pell's multipart classes (see class description).
	 * 
	 * @param maxSize
	 *            maximum size post allowed
	 * @param saveDir
	 *            the directory to save off the file
	 * @param servletRequest
	 *            the request containing the multipart
	 * @throws java.io.IOException
	 *             is thrown if encoding fails.
	 */
	public JakartaMultiPartRequest(HttpServletRequest servletRequest,
			String saveDir, int maxSize) throws IOException {
		DiskFileItemFactory fac = new DiskFileItemFactory();
		fac.setSizeThreshold(0);
		if (saveDir != null) {
			File file = new File(saveDir);
			if (!file.exists())
				file.mkdirs();
			fac.setRepository(file);
		}

		// Parse the request
		try {
			ServletFileUpload upload = new ServletFileUpload(fac);
			List items = upload
					.parseRequest(createRequestContext(servletRequest));

			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				if (log.isDebugEnabled())
					log.debug("Found item " + item.getFieldName());
				if (item.isFormField()) {
					log.debug("Item is a normal form field");
					List<String> values;
					if (params.get(item.getFieldName()) != null) {
						values = params.get(item.getFieldName());
					} else {
						values = new ArrayList<String>();
					}

					// note: see http://jira.opensymphony.com/browse/WW-633
					// basically, in some cases the charset may be null, so
					// we're just going to try to "other" method (no idea if
					// this
					// will work)
					String charset = servletRequest.getCharacterEncoding();
					if (charset != null) {
						values.add(item.getString(charset));
					} else {
						values.add(item.getString());
					}
					params.put(item.getFieldName(), values);
				} else if (item.getSize() == 0) {
					log.warn("Item is a file upload of 0 size, ignoring");
				} else {
					log.debug("Item is a file upload");

					List<FileItem> values;
					if (files.get(item.getFieldName()) != null) {
						values = files.get(item.getFieldName());
					} else {
						values = new ArrayList<FileItem>();
					}

					values.add(item);
					files.put(item.getFieldName(), values);
				}
			}
		} catch (FileUploadException e) {
			errors.add(e.getMessage());
		}
	}

	public String[] getContentType(String fieldName) {
		List items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> contentTypes = new ArrayList<String>(items.size());
		for (int i = 0; i < items.size(); i++) {
			FileItem fileItem = (FileItem) items.get(i);
			contentTypes.add(fileItem.getContentType());
		}

		return contentTypes.toArray(new String[contentTypes.size()]);
	}

	public List<String> getErrors() {
		return errors;
	}

	public File[] getFile(String fieldName) {
		List items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<File> fileList = new ArrayList<File>(items.size());
		for (int i = 0; i < items.size(); i++) {
			DiskFileItem fileItem = (DiskFileItem) items.get(i);
			fileList.add(fileItem.getStoreLocation());
		}

		return fileList.toArray(new File[fileList.size()]);
	}

	public String[] getFileNames(String fieldName) {
		List items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> fileNames = new ArrayList<String>(items.size());
		for (int i = 0; i < items.size(); i++) {
			DiskFileItem fileItem = (DiskFileItem) items.get(i);
			fileNames.add(getCanonicalName(fileItem.getName()));
		}

		return fileNames.toArray(new String[fileNames.size()]);
	}

	public Enumeration<String> getFileParameterNames() {
		return Collections.enumeration(files.keySet());
	}

	public String[] getFilesystemName(String fieldName) {
		List items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> fileNames = new ArrayList<String>(items.size());
		for (int i = 0; i < items.size(); i++) {
			DiskFileItem fileItem = (DiskFileItem) items.get(i);
			fileNames.add(fileItem.getStoreLocation().getName());
		}

		return fileNames.toArray(new String[fileNames.size()]);
	}

	public String getParameter(String name) {
		List v = params.get(name);
		if (v != null && v.size() > 0) {
			return (String) v.get(0);
		}

		return null;
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	public String[] getParameterValues(String name) {
		List v = params.get(name);
		if (v != null && v.size() > 0) {
			return (String[]) v.toArray(new String[v.size()]);
		}

		return null;
	}

	/**
	 * Creates a RequestContext needed by Jakarta Commons Upload.
	 * 
	 * @param req
	 *            the request.
	 * @return a new request context.
	 */
	private RequestContext createRequestContext(final HttpServletRequest req) {
		return new RequestContext() {
			public String getCharacterEncoding() {
				return req.getCharacterEncoding();
			}

			public int getContentLength() {
				return req.getContentLength();
			}

			public String getContentType() {
				return req.getContentType();
			}

			public InputStream getInputStream() throws IOException {
				return req.getInputStream();
			}
		};
	}

	/**
	 * Returns the canonical name of the given file.
	 * 
	 * @param filename
	 *            the given file
	 * @return the canonical name of the given file
	 */
	private String getCanonicalName(String filename) {
		int forwardSlash = filename.lastIndexOf("/");
		int backwardSlash = filename.lastIndexOf("\\");
		if (forwardSlash != -1 && forwardSlash > backwardSlash) {
			filename = filename.substring(forwardSlash + 1, filename.length());
		} else if (backwardSlash != -1 && backwardSlash >= forwardSlash) {
			filename = filename.substring(backwardSlash + 1, filename.length());
		}

		return filename;
	}

}