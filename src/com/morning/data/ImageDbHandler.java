package com.morning.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageDbHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 3; /* Only increase */
	private static final String DATABASE_NAME = "alarm_app";
	private static final String TABLE_NAME = "images";

	/* Table Columns names */
	private static final String KEY_URL = "url";
	private static final String KEY_IMAGE = "image";

	protected ImageDbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_create_table = String.format(
				"CREATE TABLE %s (%s TEXT PRIMARY KEY, %s BLOB)", TABLE_NAME,
				KEY_URL, KEY_IMAGE);
		db.execSQL(sql_create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void addImage(String url, Bitmap image) {
		SQLiteDatabase db = this.getWritableDatabase();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		ContentValues values = new ContentValues();
		values.put(KEY_URL, url);
		values.put(KEY_IMAGE, baos.toByteArray());

		db.insert(TABLE_NAME, null, values);
		db.close();
	}

	public Bitmap getImage(String url) {
		String selectQuery = String.format(
				"SELECT image FROM %s WHERE url = %s", TABLE_NAME, url);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		Bitmap image = null;
		if (cursor.moveToFirst()) {
			byte[] photo = cursor.getBlob(0);
			ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
			image = BitmapFactory.decodeStream(imageStream);
		}

		db.close();
		return image;
	}
}
