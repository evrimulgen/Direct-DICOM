package ua.ieeta.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class StreamUtil {

	public static void copy(InputStream src, OutputStream dst) {
		final ReadableByteChannel inChannel = Channels.newChannel(src);
		final WritableByteChannel outChannel = Channels.newChannel(dst);
		
		try {	
			final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
			while (inChannel.read(buffer) != -1) {
				buffer.flip();
				outChannel.write(buffer);
				buffer.compact();
			}

			buffer.flip();
			while (buffer.hasRemaining())
				outChannel.write(buffer);
			
			inChannel.close();
			outChannel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
