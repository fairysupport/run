package com.fairysupport.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class App {

	private static final String MAIN_SH = "main.sh";
	private static final String INCLUDE_LIST_FILE = "include.txt";
	private static final String ROOT_DIR = "com_fairysupport_run";

	protected String currentDir;
	private String mainDirName;
	private String mainShArg;
	private static final String PROP_FILE_NAME = "server.properties";

	private Map<String, Integer> srvMap = new HashMap<>();
	private List<Conf> srvList = new ArrayList<>();

	protected BufferedReader in;
	protected PrintStream out;
	
	private final String SEPARATOR = "-----------------------------------------------------------------------------------------------------------------------";

	public App(String currentDir, String[] rawArgs) {
		
		try {

			this.init();

			if (this.in == null) {
				this.in = new BufferedReader(new InputStreamReader(System.in));
			}
			if (this.out == null) {
				this.out = System.out;
			}

			if (currentDir == null) {
				this.currentDir = (new File(".")).getCanonicalFile().getAbsolutePath();
			} else {
				this.currentDir = currentDir;
			}
			
			List<String> argList = new ArrayList<String>();

			List<String> propFileNameList = new ArrayList<String>();
			
			String arg = null;
			boolean fOpFlg = false;
			boolean iOpFlg = false;
			for (int i = 0; i < rawArgs.length; i++) {
				arg = rawArgs[i];
				if (!"-f".equals(arg) && !fOpFlg && !"-i".equals(arg) && !iOpFlg) {
					argList.add(arg);
				}
				fOpFlg = false;
				iOpFlg = false;
				if ("-f".equals(arg) && (i + 1) < rawArgs.length) {
					String argPropFileName = rawArgs[i + 1];
					String[] propFileNameSplit = argPropFileName.split(",");
					for (String propFileName : propFileNameSplit) {
						propFileNameList.add(propFileName);
					}
					fOpFlg = true;
				} else if ("-i".equals(arg) && (i + 1) < rawArgs.length) {

					String importFileName = rawArgs[i + 1];
					String importFilePath = (new File(this.currentDir, importFileName)).getCanonicalFile().getCanonicalPath();
					BufferedReader reader = new BufferedReader(new FileReader(importFilePath));
					String line = null;
					while ((line = reader.readLine()) != null) {
						if ("".equals(line.trim())) {
							continue;
						}
						propFileNameList.add(line.trim());
					}
					reader.close();
					iOpFlg = true;
				}
			}
			
			if (propFileNameList.size() == 0) {
				propFileNameList.add(PROP_FILE_NAME);
			}
			
			String[] args = argList.toArray(new String[argList.size()]);

			this.validate(args);

			arg = null;
			if (args.length > 1) {
				StringBuilder argSb = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					arg = args[i];
					argSb.append(arg);
					argSb.append(" ");
				}
				argSb.delete(argSb.length() -1, argSb.length());
				this.mainShArg = argSb.toString();
			}

			for (String propFileName : propFileNameList) {
			
				String propFilePath = (new File(this.currentDir, propFileName)).getCanonicalFile().getCanonicalPath();
	
				BufferedReader reader = new BufferedReader(new FileReader(propFilePath));
				Properties props = new Properties();
				props.load(reader);
				reader.close();
	
				Set<Object> keys = props.keySet();
				String strKey = null;
				String[] strKeySplit = null;
				Conf conf = null;
				String confValue = null;
				String serverName = null;
				for (Object key : keys) {
					strKey = (String) key;
					strKeySplit = strKey.split("\\.");
					if (strKeySplit.length != 2) {
						throw new RuntimeException("wrong key " + propFileName + "." + strKey);
					}
					serverName = propFileName + "." + strKeySplit[0];
					if (!srvMap.containsKey(serverName)) {
						srvList.add(new Conf());
						srvMap.put(serverName, srvList.size() - 1);
					}
	
					conf = srvList.get(srvMap.get(serverName));
					confValue = props.getProperty(strKey);
					PropertyUtils.setProperty(conf, strKeySplit[1], confValue == null ? "" : confValue);
	
				}
				
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	

	public static void main(String[] args) {
		App.dispatch(args, null, false, App.class);
		System.exit(0);
	}

	public static <T extends App> List<T> dispatch(String[] args, String currentDir, boolean ret, Class<T> appClass) {

		List<T> resultList = new ArrayList<T>();
		
		BufferedReader reader = null;
		String[] useArgs = null;
		
		try {

			if (args.length < 1) {
				System.out.println("Please input directory name or file name");
				BufferedReader runReader = new BufferedReader(new InputStreamReader(System.in));
				String runArg = runReader.readLine().trim();
				useArgs = runArg.split(" ");
			} else {
				useArgs = args;
			}
			
			if (currentDir == null) {
				currentDir = (new File(".")).getCanonicalFile().getAbsolutePath();
			}
			File argFile = (new File(currentDir, useArgs[0]));
			if (argFile.isFile()) {
				
				reader = new BufferedReader(new FileReader(argFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if ("".equals(line) || line.startsWith("#")) {
						continue;
					}
					
					if (useArgs.length > 1) {
						for (int i = 1; i < useArgs.length; i++) {
							line = line.replaceAll("(?<!\\\\)\\$" + String.valueOf(i), useArgs[i]);
						}
					}

					Constructor<T> constructor = appClass.getConstructor(String.class, String[].class);
					T app = constructor.newInstance(currentDir, line.split(" "));
					app.execute();
					
					if (ret) {
						resultList.add(app);
					}
					
				}
				reader.close();
				
			} else {

				Constructor<T> constructor = appClass.getConstructor(String.class, String[].class);
				T app = constructor.newInstance(currentDir, useArgs);
				app.execute();

				if (ret) {
					resultList.add(app);
				}
				
			}

		} catch (Exception e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			System.err.println("[error]");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		return resultList;

	}

	private void validate(String[] args) {

		try {

			this.mainDirName = args[0];
			if (this.mainDirName.contains(".")) {
				throw new RuntimeException("Can not interpret " + this.mainDirName);
			}
			File mainDir = (new File(this.currentDir, this.mainDirName));
			if (!mainDir.isDirectory()) {
				throw new RuntimeException("not found directory " + mainDir.getAbsolutePath());
			}

			File mainSh = (new File(mainDir, MAIN_SH));
			if (!mainSh.isFile()) {
				throw new RuntimeException("not found main.sh " + mainSh.getAbsolutePath());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	protected void init() {

	}

	public void execute() throws IOException {
		
		try {

			for (Conf conf : srvList) {
	
				this.out.println(SEPARATOR);
				this.printPoint("start", conf);
	
				Session session = this.getSesseion(conf);
	
				this.start(session, conf);
				this.run(session, conf);
				this.end(session, conf);
	
				this.printPoint("end", conf);
				this.out.println(SEPARATOR);
	
			}
			
		} finally {

			try {
				in.close();
			} catch (IOException e) {
			}

		}

	}

	private Session getSesseion(Conf conf) {

		Session session = null;
		boolean inputFlg = false;

		for (int i = 0; i < 3; i++) {

			try {

				String user = conf.getUser();
				if (conf.getUser() == null) {
					this.out.println("Please input user");
					user = in.readLine().trim();
					inputFlg = true;
				}

				String password = conf.getPassword();
				if (conf.getPassword() == null) {
					this.out.println("Please input password");
					password = in.readLine().trim();
					inputFlg = true;
				}

				String passphrase = conf.getPassphrase();
				if (conf.getKeyPath() != null && !conf.getKeyPath().trim().equals("")
						&& (conf.getPassphrase() == null)) {
					this.out.println("Please input passphrase");
					passphrase = in.readLine().trim();
					inputFlg = true;
				}

				JSch jsch = new JSch();
				if (conf.getKeyPath() != null && !conf.getKeyPath().trim().equals("")) {
					if (passphrase != null && !passphrase.trim().equals("")) {
						jsch.addIdentity(conf.getKeyPath(), passphrase);
					} else {
						jsch.addIdentity(conf.getKeyPath());
					}
				}
				session = jsch.getSession(user, conf.getAddress(), Integer.parseInt(conf.getPort()));
				session.setConfig("StrictHostKeyChecking", "no");
				if (password != null && !password.trim().equals("")) {
					session.setPassword(password);
				}

				session.connect();

				conf.setUser(user);
				conf.setPassword(password);
				conf.setPassphrase(passphrase);

				break;

			} catch (Exception e) {
				if (inputFlg) {
					this.out.println("can not connect " + conf.getAddress() + ":" + conf.getPort());
					if (i >= 2) {
						throw new RuntimeException("can not connect " + conf.getAddress() + ":" + conf.getPort());
					}
					this.out.println("Please input it again");
				} else {
					throw new RuntimeException("can not connect " + conf.getAddress() + ":" + conf.getPort());
				}
			}

		}

		return session;

	}

	private void start(Session session, Conf conf) {

		this.printPoint("upload file", conf);

		StringBuilder sb = new StringBuilder();
		sb.append("/home");
		sb.append("/");
		sb.append(conf.getUser());
		sb.append("/");
		sb.append(ROOT_DIR);

		File mainDir = new File(this.currentDir, mainDirName);
		BufferedReader reader = null;

		try {

			ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();

			this.mkdir(sftp, sb.toString());

			File includeListFile = (new File(mainDir, INCLUDE_LIST_FILE));
			if (includeListFile.isFile()) {
				reader = new BufferedReader(new FileReader(includeListFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					this.upDir(sftp, new File(this.currentDir, line.trim()), sb.toString());
				}
				reader.close();
			}

			this.upDir(sftp, mainDir, sb.toString());

			sftp.disconnect();

		} catch (Exception e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			throw new RuntimeException(e);
		}

	}

	private void end(Session session, Conf conf) {

		this.printPoint("delete file", conf);

		StringBuilder sb = new StringBuilder();
		sb.append("/home");
		sb.append("/");
		sb.append(conf.getUser());

		try {

			ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();

			this.rmdir(sftp, ROOT_DIR, sb.toString());
			
			sftp.disconnect();

			session.disconnect();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private boolean run(Session session, Conf conf) {

		this.printPoint("run main.sh", conf);

		boolean errorFlg = false;

		try {

			StringBuilder sb = new StringBuilder();
			sb.append("cd ");
			sb.append("/home/");
			sb.append(conf.getUser());
			sb.append("/");
			sb.append(ROOT_DIR);
			sb.append("/");
			sb.append(mainDirName);
			sb.append(" && ./");
			sb.append(MAIN_SH);
			if (this.mainShArg != null) {
				sb.append(" ");
				sb.append(this.mainShArg);
			}
			errorFlg = this.run(session, conf, sb.toString());
			if (errorFlg) {
				return errorFlg;
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return errorFlg;

	}

	private boolean run(Session session, Conf conf, String command) {

		boolean errorFlg = false;

		try {

			Channel channel = session.openChannel("exec");
			ChannelExec channelExec = (ChannelExec) channel;
			channelExec.setCommand(command);

			this.out.println(command);

			OutputStream out = channelExec.getOutputStream();
			channelExec.setErrStream(new Print(new ByteArrayOutputStream(), this.out));
			channelExec.setOutputStream(new Print(new ByteArrayOutputStream(), this.out));

			channel.connect();

			BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(out));
			Output output = new Output(channelExec, this.in, outputWriter);
			Thread outTh = new Thread(output);
			outTh.start();

			while (!channelExec.isClosed() && !output.isEndFlg()) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
			}

			try {
				outputWriter.close();
			} catch (IOException e) {

			}

			channel.disconnect();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return errorFlg;

	}

	private void upDir(ChannelSftp sftp, File localDir, String srvRootPath) {

		StringBuilder sb = new StringBuilder();
		sb.append(srvRootPath);
		sb.append("/");
		sb.append(localDir.getName());
		String targetDir = sb.toString();

		sb.append("/");
		int remoteFilePreLen = sb.length();
		String remoteFile = null;

		this.mkdir(sftp, targetDir);

		File[] childList = localDir.listFiles();
		for (File child : childList) {
			if (child.isDirectory()) {
				this.upDir(sftp, child, targetDir);
			} else {
				sb.delete(remoteFilePreLen, sb.length());
				sb.append(child.getName());
				remoteFile = sb.toString();
				try {
					this.out.print("upload ");
					this.out.print(child.getAbsolutePath());
					this.out.print(" -> ");
					this.out.println(remoteFile);
					sftp.put(child.getAbsolutePath(), remoteFile);
				} catch (SftpException mkdirException) {
					throw new RuntimeException("can not sftp " + child.getAbsolutePath());
				}
				try {
					sftp.chmod(0777, remoteFile);
				} catch (SftpException mkdirException) {
					throw new RuntimeException("can not chmod " + remoteFile);
				}
			}
		}

	}

	private void mkdir(ChannelSftp sftp, String targetDir) {

		try {
			sftp.ls(targetDir);
			this.out.print("dir already exists ");
			this.out.println(targetDir);
		} catch (SftpException lsException) {
			try {
				this.out.print("mkdir ");
				this.out.println(targetDir);
				sftp.mkdir(targetDir);
			} catch (SftpException mkdirException) {
				throw new RuntimeException("can not mkdir " + targetDir);
			}
		}
		try {
			sftp.chmod(0777, targetDir);
		} catch (SftpException mkdirException) {
			throw new RuntimeException("can not chmod " + targetDir);
		}

	}

	private void rmdir(ChannelSftp sftp, String dirName, String srvRootPath) throws SftpException {

		StringBuilder sb = new StringBuilder();
		sb.append(srvRootPath);
		sb.append("/");
		sb.append(dirName);
		String targetDir = sb.toString();

		sb.append("/");
		int remoteFilePreLen = sb.length();
		String remoteFile = null;
		
		@SuppressWarnings("unchecked")
		Vector<LsEntry> lsList = sftp.ls(targetDir);
		for (LsEntry child : lsList) {
			if (child.getFilename().equals("..") || child.getFilename().equals(".")) {
				continue;
			}
			if (child.getAttrs().isDir()) {
				this.rmdir(sftp, child.getFilename(), targetDir);
			} else {
				sb.delete(remoteFilePreLen, sb.length());
				sb.append(child.getFilename());
				remoteFile = sb.toString();
				try {
					this.out.print("delete ");
					this.out.println(remoteFile);
					sftp.rm(remoteFile);
				} catch (SftpException mkdirException) {
					throw new RuntimeException("can not rm " + child.getLongname());
				}
			}
		}

		this.rmdir(sftp, targetDir);

	}

	private void rmdir(ChannelSftp sftp, String targetDir) {

		try {
			sftp.ls(targetDir);
			try {
				this.out.print("rmdir ");
				this.out.println(targetDir);
				sftp.rmdir(targetDir);
			} catch (SftpException rmdirException) {
				throw new RuntimeException("can not rmdir " + targetDir);
			}
		} catch (SftpException lsException) {
			this.out.print("dir does not exist ");
			this.out.println(targetDir);
		}

	}
	
	private void printPoint(String pointName, Conf conf) {

		this.out.print("[");
		this.out.print(pointName);
		this.out.print("][");
		this.out.print(conf.getAddress());
		this.out.print(":");
		this.out.print(conf.getPort());
		this.out.println("]");

	}

	public List<Conf> getSrvList() {
		return srvList;
	}

}
