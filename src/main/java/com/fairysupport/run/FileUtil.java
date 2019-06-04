package com.fairysupport.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class FileUtil {

	public FileUtil() {
	}

	public static String read(String path) throws IOException {

		char[] containts = new char[128];

		StringBuilder sb = new StringBuilder();
		int readNum = -1;

		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		while ((readNum = reader.read(containts)) != -1) {
			sb.append(containts, 0, readNum);
			Arrays.fill(containts, '\u0000');
		}
		reader.close();

		return sb.toString();

	}

	public static void write(String path, String text) throws IOException  {

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
		writer.write(text);
		writer.flush();
		writer.close();

	}
}
