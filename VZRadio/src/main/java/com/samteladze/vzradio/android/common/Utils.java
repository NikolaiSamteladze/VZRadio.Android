package com.samteladze.vzradio.android.common;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	public static void CopyStream(InputStream inputStream,
			OutputStream outputStream) {
		final int BUFFER_SIZE = 1024;
		try {
			byte[] bytes = new byte[BUFFER_SIZE];
			for (;;) {
				int count = inputStream.read(bytes, 0, BUFFER_SIZE);
				if (count == -1)
					break;
				outputStream.write(bytes, 0, count);
			}
		} catch (Exception e) {
		}
	}
}