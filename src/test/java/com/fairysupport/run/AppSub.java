package com.fairysupport.run;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class AppSub extends App {

	protected OutputStream byteOut;

	public AppSub(String currentDir, String[] args) {
		super(currentDir, args);
	}

	protected void init() {

		this.byteOut = new ByteArrayOutputStream();
		this.out = new PrintStream(this.byteOut);
	}

	public OutputStream getByteOut() {
		return byteOut;
	}

	public void setByteOut(OutputStream byteOut) {
		this.byteOut = byteOut;
	}


}