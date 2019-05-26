package com.fairysupport.run;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void dispatchTest() {

		try {

			String currentPath = getClass().getResource(".").getFile();
			File currentObj = new File(currentPath);
			String currentDir = currentObj.getAbsolutePath();
			
			List<List<String[]>> serverFileList = new ArrayList<List<String[]>>();
			List<String[]> serverFile = new ArrayList<String[]>();
			serverFile.add(new String[] {"server", "server1"});
			serverFile.add(new String[] {"server", "server2"});
			serverFileList.add(serverFile);
			serverFile = new ArrayList<String[]>();
			serverFile.add(new String[] {"server2", "server1"});
			serverFile.add(new String[] {"server2", "server2"});
			serverFileList.add(serverFile);
			serverFile = new ArrayList<String[]>();
			serverFile.add(new String[] {"server3", "server1"});
			serverFile.add(new String[] {"server4", "server2"});
			serverFileList.add(serverFile);
			serverFile = new ArrayList<String[]>();
			serverFile.add(new String[] {"server3", "server1"});
			serverFile.add(new String[] {"server4", "server2"});
			serverFileList.add(serverFile);
			serverFile = new ArrayList<String[]>();
			serverFile.add(new String[] {"server2", "server1"});
			serverFile.add(new String[] {"server2", "server2"});
			serverFileList.add(serverFile);
			
			List<String[]> argsList = new ArrayList<String[]>();
			argsList.add(new String[] {"sample", "run sample", "run sample2", "aaa"});
			argsList.add(new String[] {"-f", "server2.properties" , "sample", "run sample", "run sample2", "aaa"});
			argsList.add(new String[] {"-f", "server3.properties,server4.properties" , "sample", "run sample", "run sample2", "aaa"});
			argsList.add(new String[] {"-i", "server.txt" , "sample", "run sample", "run sample2", "aaa"});
			argsList.add(new String[] {"group.txt" , "dummy", "\"run sample2\"", "aaa"});
			
			String[] args = null;
			for (int i = 0; i < argsList.size(); i++) {
				
				args = argsList.get(i);
				
				List<AppSub> appList = App.dispatch(args, currentDir, true, AppSub.class);
				AppSub app = (AppSub)appList.get(0);
	
				List<Conf> confList = app.getSrvList();
	
				StringBuilder sb = new StringBuilder();
				for (Conf conf : confList) {
					sb.append("-----------------------------------------------------------------------------------------------------------------------");
					sb.append(System.lineSeparator());
					sb.append("[start][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("[upload file][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("mkdir /home/" + conf.getUser() + "/com_fairysupport_run");
					sb.append(System.lineSeparator());
					sb.append("mkdir /home/" + conf.getUser() + "/com_fairysupport_run/common");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "common" + File.separator + "common.sh -> /home/" + conf.getUser() + "/com_fairysupport_run/common/common.sh");
					sb.append(System.lineSeparator());
					sb.append("mkdir /home/" + conf.getUser() + "/com_fairysupport_run/sample");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "get.txt -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/get.txt");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "include.txt -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/include.txt");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "main.sh -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/main.sh");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "sample.sh -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/sample.sh");
					sb.append(System.lineSeparator());
					sb.append("[run main.sh][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("cd /home/" + conf.getUser() + "/com_fairysupport_run/sample && ./main.sh \"run sample\" \"run sample2\" \"aaa\"");
					sb.append(System.lineSeparator());
					sb.append("run common");
					sb.append("\n");
					sb.append("run sample");
					sb.append("\n");
					sb.append("run sample2");
					sb.append("\n");
					sb.append("[get file][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("download ./include.txt -> " + app.currentDir + File.separator + "sample" + File.separator + ".." + File.separator + "get" + File.separator+ conf.getServer() + File.separator + conf.getFile().split("\\.")[0] + File.separator + "aaa" + File.separator + "include.txt");
					sb.append(System.lineSeparator());
					sb.append("[delete file][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("delete /home/" + conf.getUser() + "/com_fairysupport_run/common/common.sh");
					sb.append(System.lineSeparator());
					sb.append("rmdir /home/" + conf.getUser() + "/com_fairysupport_run/common");
					sb.append(System.lineSeparator());
					sb.append("delete /home/" + conf.getUser() + "/com_fairysupport_run/sample/get.txt");
					sb.append(System.lineSeparator());
					sb.append("delete /home/" + conf.getUser() + "/com_fairysupport_run/sample/include.txt");
					sb.append(System.lineSeparator());
					sb.append("delete /home/" + conf.getUser() + "/com_fairysupport_run/sample/main.sh");
					sb.append(System.lineSeparator());
					sb.append("delete /home/" + conf.getUser() + "/com_fairysupport_run/sample/sample.sh");
					sb.append(System.lineSeparator());
					sb.append("rmdir /home/" + conf.getUser() + "/com_fairysupport_run/sample");
					sb.append(System.lineSeparator());
					sb.append("rmdir /home/" + conf.getUser() + "/com_fairysupport_run");
					sb.append(System.lineSeparator());
					sb.append("[end][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("-----------------------------------------------------------------------------------------------------------------------");
					sb.append(System.lineSeparator());
				}
	
				String expect = sb.toString();
				String actual = app.getByteOut().toString();
	
				assertEquals(expect, actual);
				
				serverFile = serverFileList.get(i);
				for (String[] svPair : serverFile) {

					File includeFile = new File(currentObj, "get/" + svPair[1] + "/" + svPair[0] + "/aaa/include.txt");
					BufferedReader reader = new BufferedReader(new FileReader(includeFile));
					String line = null;
					StringBuilder getSb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						getSb.append(line);
					}
					reader.close();

					assertEquals("common", getSb.toString());
					
					includeFile.delete();
					
				}
				
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
