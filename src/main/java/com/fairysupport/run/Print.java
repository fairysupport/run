package com.fairysupport.run;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Print extends FilterOutputStream {

	private OutputStream printOut;

	public Print(OutputStream out, OutputStream printOut) {
		super(out);
		this.printOut = printOut;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		this.printOut.write(b, off, len);
	}

}
