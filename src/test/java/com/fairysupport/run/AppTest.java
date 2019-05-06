package com.fairysupport.run;

import static org.junit.Assert.*;

import java.io.File;
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
	public void executeTest() {

		try {

			String currentPath = getClass().getResource(".").getFile();
			File currentObj = new File(currentPath);
			String currentDir = currentObj.getAbsolutePath();
			
			List<String[]> argsList = new ArrayList<String[]>();
			argsList.add(new String[] {"sample", "\"run sample\"", "\"run sample2\""});
			argsList.add(new String[] {"-f", "server2.properties" , "sample", "\"run sample\"", "\"run sample2\""});
			argsList.add(new String[] {"-f", "server3.properties,server4.properties" , "sample", "\"run sample\"", "\"run sample2\""});
			argsList.add(new String[] {"-i", "server.txt" , "sample", "\"run sample\"", "\"run sample2\""});
			argsList.add(new String[] {"group.txt" , "\"dummy\"", "\"run sample2\""});
			
			for (String[] args : argsList) {
				
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
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "include.txt -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/include.txt");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "main.sh -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/main.sh");
					sb.append(System.lineSeparator());
					sb.append("upload " + app.currentDir + File.separator + "sample" + File.separator + "sample.sh -> /home/" + conf.getUser() + "/com_fairysupport_run/sample/sample.sh");
					sb.append(System.lineSeparator());
					sb.append("[run main.sh][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("cd /home/" + conf.getUser() + "/com_fairysupport_run/sample && ./main.sh \"run sample\" \"run sample2\"");
					sb.append(System.lineSeparator());
					sb.append("run common");
					sb.append("\n");
					sb.append("run sample");
					sb.append("\n");
					sb.append("run sample2");
					sb.append("\n");
					sb.append("[delete file][" + conf.getAddress() + ":" + conf.getPort() + "]");
					sb.append(System.lineSeparator());
					sb.append("delete /home/" + conf.getUser() + "/com_fairysupport_run/common/common.sh");
					sb.append(System.lineSeparator());
					sb.append("rmdir /home/" + conf.getUser() + "/com_fairysupport_run/common");
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
				
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
