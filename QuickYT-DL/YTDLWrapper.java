import java.io.*;
import java.lang.annotation.Retention;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.print.DocFlavor.STRING;

public class YTDLWrapper {

	private String ytDLPath = "youtube-dl";

	// about variables
	private String version;

	public YTDLWrapper() throws IllegalStateException {
		// check if youtube-dl is installed on the current OS in PATH variable
		Process process = null;
		ytDLPath = "youtube-dl";

		try {
			process = new ProcessBuilder(ytDLPath, "--version").start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			System.out.printf("Output of running %s is: \n", ytDLPath);

			// read one line
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			version = line;
			System.out.println("Found Youtube-dl at: " + ytDLPath);

			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not find command youtube-dl: " + ytDLPath);
		}

		// try to see if its in the local directory
		System.out.println("Could not find youtube-dl on system PATH. Trying current working directory...");

		process = null;
		ytDLPath = System.getProperty("user.dir") + File.separator + "youtube-dl.exe";
		try {
			process = new ProcessBuilder(ytDLPath, "--version").start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			System.out.printf("Output of running %s is: \n", ytDLPath);

			// read one line
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			version = line;
			System.out.println("Found Youtube-dl at: " + ytDLPath);

			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not find command youtube-dl: " + ytDLPath);
		}

		throw new IllegalStateException("Error! Could not find youtube-dl installed or in current working folder.");
	}

	public String getVersion() {
		return version;
	}

	public List<YTFormat> getFormats(String url) {
		LinkedList<YTFormat> ret = new LinkedList<YTFormat>();

		try {
			Process fetchFormats = startYTDLProcess("-F", url);
			List<String> formatResults = getProcessResults(fetchFormats);
			System.out.println(formatResults);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	private Process startYTDLProcess(String... args) throws IOException {

		List<String> command = new LinkedList<String>();
		command.add(ytDLPath);
		command.addAll(Arrays.asList(args));
		Process ret = new ProcessBuilder(command).start();
		return ret;
	}

	private List<String> getProcessResults(Process p) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		LinkedList<String> lines = new LinkedList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		
		return lines;
		
	}

	public class YTFormat {
		public int formatCode;
		public String extension;
		public String resolution;
		public String note;
	}
}
