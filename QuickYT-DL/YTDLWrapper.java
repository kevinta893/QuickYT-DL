import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
				version = line;
			}
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
				version = line;
			}
			System.out.println("Found Youtube-dl at: " + ytDLPath);

			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not find command youtube-dl: " + ytDLPath);
		}

		throw new IllegalStateException("Error! Could not find youtube-dl installed or in current working folder.");
	}

	//======================================
	//youtube-dl processing
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
	
	
	//======================================
	//utilities
	
	
	public String getVersion() {
		return version;
	}

	public List<YTFormat> getFormats(String url) {
		LinkedList<YTFormat> ret = new LinkedList<YTFormat>();

		try {
			Process fetchFormats = startYTDLProcess("-F", url);
			List<String> formatResults = getProcessResults(fetchFormats);
			
			//take the output and convert to rows
			for (String f : formatResults){
				System.out.println(f);
				
				Matcher m;
				
				
				//get format code
				Pattern formatCodePattern = Pattern.compile("[0-9]{1,}(?=[ ]{1})");
				m = formatCodePattern.matcher(f);
				String formatCode = "";
				if (m.find()){
					formatCode = m.group();
				}else{
					continue;
				}

				//get extension
				Pattern extensionPattern = Pattern.compile("(?![0-9]{1,}[ ]+)[\\w]+(?=[ ]+)");
				m = extensionPattern.matcher(f);
				String extension = "";
				if (m.find()){
					extension = m.group();
				}
				
				//get resolution
				Pattern resolutionPattern = Pattern.compile("(?![ ]+)audio only(?=[ ]*)|[0-9]+x[0-9]+(?=[ ]*)");
				m = resolutionPattern.matcher(f);
				String resolution = "";
				if (m.find()){
					resolution = m.group();
					f = f.substring(m.end()).trim();		//get the rest of the string after the resolution 
				}
				
				//get notes
				String notes = new String(f);
				
				
				//get filesize
				Pattern filesizePattern = Pattern.compile("(?=[ ]*)[0-9.]*MiB");
				m = filesizePattern.matcher(f);
				String fileSize = "";
				if (m.find()){
					fileSize = m.group();
				}
				else{
					fileSize = "-";
				}
				//add format to list
				ret.add(new YTFormat(Integer.parseInt(formatCode), extension, resolution, notes, fileSize));

			}
			System.out.println(formatResults);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	//Downloads the video right to the current directory
	public void downloadVideo(String url, String formatcode){
		try {
			startYTDLProcess("-f", formatcode ,"\"" + url +"\"");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error! Download failed");
		}
	}
	

	public class YTFormat {
		public int formatCode;
		public String extension;
		public String resolution;
		public String note;
		public String fileSize;
		
		public YTFormat(int formatCode, String extension, String resolution, String note, String fileSize) {
			this.formatCode = formatCode;
			this.extension = extension;
			this.resolution = resolution;
			this.note = note;
			this.fileSize = fileSize;
		}
	}
}
