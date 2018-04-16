package com.readboy.textbook.util;

import java.util.Calendar;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Attachment implements Parcelable{

	private Uri uri;
	private Long id;
	private String uriPath;
	private String name;
	private long size;
	private long length;
	private String mime_type;

	public Attachment() {
		this.id = Long.valueOf(Calendar.getInstance().getTimeInMillis());
	}

	public Attachment(String uri, String mime_type) {
		this(Calendar.getInstance().getTimeInMillis(), uri, null, 0, 0,
				mime_type);
	}

	public Attachment(Long id, String uri, String name, long size, long length,
			String mime_type) {
		this.id = id;
		this.uriPath = uri;
		this.name = name;
		this.size = size;
		this.length = length;
		this.mime_type = mime_type;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUriPath() {
		return this.uriPath;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLength() {
		return this.length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getMime_type() {
		return this.mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	private Attachment(Parcel in) {
		setId(in.readLong());
		setUri(Uri.parse(in.readString()));
		setMime_type(in.readString());
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
		setUriPath(uri.toString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(getId());
		parcel.writeString(getUri().toString());
		parcel.writeString(getMime_type());
	}

	/*
	 * Parcelable sdinterface must also have a static field called CREATOR, which
	 * is an object implementing the Parcelable.Creator sdinterface. Used to
	 * un-marshal or de-serialize object from Parcel.
	 */
	public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {

		public Attachment createFromParcel(Parcel in) {
			return new Attachment(in);
		}

		public Attachment[] newArray(int size) {
			return new Attachment[size];
		}
	};
}
