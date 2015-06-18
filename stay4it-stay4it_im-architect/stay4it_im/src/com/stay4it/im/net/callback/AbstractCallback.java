package com.stay4it.im.net.callback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.stay4it.im.net.AppException;
import com.stay4it.im.net.AppException.ExceptionStatus;
import com.stay4it.im.net.IRequestListener;
import com.stay4it.im.net.OnUploadProgressChangedListener;
import com.stay4it.im.utilities.IOUtilities;
import com.stay4it.im.utilities.TextUtil;

/** 
 * @author Stay  
 * @version create time：Sep 15, 2014 12:25:23 PM 
 * @param <T>
 */
public abstract class AbstractCallback<T> implements ICallback<T>{
	protected String path;
	private boolean isCancelled;
	private boolean isForceCancelled;
	
	public void checkIfIsCancelled() throws AppException{
		if (isCancelled) {
			throw new AppException(ExceptionStatus.CancelException,"the request has been cancelled");
		}
	}
	
	@Override
	public T preRequest() {
		return null;
	}
	
	@Override
	public T postRequest(T t) {
		return t;
	}

	@Override
	public int retryCount() {
		return 0;
	}
	
	@Override
	public T handle(HttpResponse response) throws AppException{
		return handle(response, null);
	}
	
	@Override
	public T handle(HttpURLConnection connection) throws AppException {
		return handle(connection, null);
	}
	
	@Override
	public T handle(HttpURLConnection connection, IRequestListener listener) throws AppException{
		try {
			int statusCode = connection.getResponseCode();
			switch (statusCode) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_CREATED:
				if (TextUtil.isValidate(path)) {
					FileOutputStream fos = new FileOutputStream(path);
					InputStream is = connection.getInputStream();
					byte[] buffer = new byte[2048];
					int len = -1;
					long contentLength = connection.getContentLength();
					long curPos = 0;
					while ((len = is.read(buffer)) != -1) {
						checkIfIsCancelled();
						if (listener != null) {
							curPos += len;
							listener.onProgressUpdate((int)(curPos/1024),(int)(contentLength/1024));
						}
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.flush();
					fos.close();
					return bindData(path);
				}else {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					InputStream is = connection.getInputStream();
					byte[] buffer = new byte[2048];
					int len = -1;
					long contentLength = connection.getContentLength();
					long curPos = 0;
					while ((len = is.read(buffer)) != -1) {
						checkIfIsCancelled();
						if (listener != null) {
							curPos += len;
							listener.onProgressUpdate((int)(curPos/1024),(int)(contentLength/1024));
						}
						out.write(buffer, 0, len);
					}
					is.close();
					out.flush();
					out.close();
					return bindData(new String(out.toByteArray(),"UTF-8"));
				}
			default:
				throw new AppException(connection.getResponseMessage(),statusCode,IOUtilities.readStreamToMemory(connection.getErrorStream()));
			}
		} catch (IOException e) {
			throw new AppException(ExceptionStatus.IOException, e.getMessage());
		}
	}
	
	@Override
	public T handle(HttpResponse response,IRequestListener listener) throws AppException{
//		file, json, xml, string, image
		try {
			checkIfIsCancelled();
			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			switch (response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_CREATED:
				if (TextUtil.isValidate(path)) {
					FileOutputStream fos = new FileOutputStream(path);
					InputStream is = entity.getContent();
					byte[] buffer = new byte[2048];
					int len = -1;
					long contentLength = entity.getContentLength();
					long curPos = 0;
					while ((len = is.read(buffer)) != -1) {
						checkIfIsCancelled();
						if (listener != null) {
							curPos += len;
							listener.onProgressUpdate((int)(curPos/1024),(int)(contentLength/1024));
						}
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.flush();
					fos.close();
					return bindData(path);
				}else {
					return bindData(EntityUtils.toString(entity,"UTF-8"));
				}
			default:
				String detailMessage = response.getStatusLine().getReasonPhrase();
				String errorInfo = EntityUtils.toString(entity, "UTF-8");
				throw new AppException(detailMessage,statusCode, errorInfo);
			}
		} catch (FileNotFoundException e) {
			throw new AppException(ExceptionStatus.FileNotFoundException,e.getMessage());
		} catch (IllegalStateException e) {
			throw new AppException(ExceptionStatus.IllegalStateException,e.getMessage());
		} catch (ParseException e) {
			throw new AppException(ExceptionStatus.ParseException,e.getMessage());
		} catch (IOException e) {
			throw new AppException(ExceptionStatus.IOException,e.getMessage());
		}
	}
	
	
	public AbstractCallback<T> cache(String path){
		this.path = path;
		return this;
	}
	
	public void cancel(boolean force){
		this.isForceCancelled = force;
		isCancelled = true;
	}
	
	@Override
	public boolean isForceCancelled() {
		return isForceCancelled;
	}
	
	@Override
	public boolean onCustomOutput(OutputStream out,OnUploadProgressChangedListener listener) throws AppException{
		return false;
	}

	
}
