package com.fairysupport.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import com.jcraft.jsch.ChannelExec;

public class Output implements Runnable {

	private ChannelExec channelExec;
	private BufferedReader in;
	private BufferedWriter writer;
	private boolean endFlg = false;

	public Output(ChannelExec channelExec, BufferedReader in, BufferedWriter writer) {
		this.setChannelExec(channelExec);
		this.setIn(in);
		this.setWriter(writer);
	}

	public ChannelExec getChannelExec() {
		return channelExec;
	}

	public void setChannelExec(ChannelExec channelExec) {
		this.channelExec = channelExec;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public boolean isEndFlg() {
		return endFlg;
	}

	public void setEndFlg(boolean endFlg) {
		this.endFlg = endFlg;
	}

	@Override
	public void run() {

		try {

			while (true) {
				this.read();
				if (this.channelExec.isClosed()) {
					break;
				}
				Thread.sleep(1000);
			}
			this.writer.close();

		} catch (IOException e) {
			this.setEndFlg(true);
		} catch (Exception e) {
			this.setEndFlg(true);
			throw new RuntimeException(e);
		}

		this.setEndFlg(true);

	}

	private void read() throws IOException {
		String line = null;
		if (this.in.ready()) {

			line = this.in.readLine();
			if (line != null) {
				this.writer.write(line);
				this.writer.write("\n");
				this.writer.flush();
			}
		}

	}

}
