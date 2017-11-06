import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextPane;

public class Main extends JFrame {

	private static final long serialVersionUID = 3954782143417269586L;
	
	private JPanel contentPane;
	private JTextField txtURL;
	private JTable tblFormats;
	private JButton btnDownload;
	private JButton btnGetInfo;
	private JProgressBar prbDownload;
	private JLabel lblProgress;
	private JTextPane txtLogger;
	private JScrollPane scrollPaneLogger;
	
	private YTDLJava ytdl;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		
		//try to find YTDL
		try{
			ytdl = new YTDLJava();
		} catch (IllegalStateException e){
			showMessageBoxError("Cannot find youtube-dl or youtube-dl.exe. Make sure the youtube-dl is installed, added to your PATH variable, or exists in the same folder as this program.\n\nSee youtube-dl at: https://github.com/rg3/youtube-dl");
			System.exit(0);
		}
		
		this.setTitle("QuickYT-DL (youtube-dl version: " + ytdl.getVersion() + ")" );
		
		//GUI initialization
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 530);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtURL = new JTextField();
		txtURL.setBounds(10, 35, 389, 20);
		contentPane.add(txtURL);
		txtURL.setColumns(10);
		txtURL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fetchYTInfo(txtURL.getText());
			}
		});
		JLabel lblYoutubeLink = new JLabel("Youtube Link:");
		lblYoutubeLink.setBounds(10, 10, 162, 14);
		contentPane.add(lblYoutubeLink);
		
		btnDownload = new JButton("Download");
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clickDownload();
			}
		});
		btnDownload.setBounds(658, 322, 116, 28);
		contentPane.add(btnDownload);
		
		prbDownload = new JProgressBar();
		prbDownload.setMaximum(10000);
		prbDownload.setBounds(10, 322, 638, 28);
		contentPane.add(prbDownload);
		
		lblProgress = new JLabel("Progress: ");
		lblProgress.setBounds(10, 297, 638, 14);
		contentPane.add(lblProgress);
		
		btnGetInfo = new JButton("Get Info");
		btnGetInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fetchYTInfo(txtURL.getText());
			}
		});
		btnGetInfo.setBounds(409, 34, 89, 23);
		contentPane.add(btnGetInfo);
		
		JScrollPane scrollPaneTable = new JScrollPane();
		scrollPaneTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneTable.setBounds(10, 66, 764, 220);
		contentPane.add(scrollPaneTable);
		
		tblFormats = new JTable();
		scrollPaneTable.setViewportView(tblFormats);
		tblFormats.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tblFormats.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Format Code", "Extension", "Resolution", "Description", "Size"
			}
		));
		tblFormats.getColumnModel().getColumn(0).setPreferredWidth(70);
		tblFormats.getColumnModel().getColumn(1).setPreferredWidth(60);
		tblFormats.getColumnModel().getColumn(2).setPreferredWidth(68);
		tblFormats.getColumnModel().getColumn(3).setPreferredWidth(361);
		tblFormats.getColumnModel().getColumn(4).setPreferredWidth(62);
		tblFormats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPaneLogger = new JScrollPane();
		scrollPaneLogger.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneLogger.setBounds(10, 360, 764, 119);
		contentPane.add(scrollPaneLogger);
		
		txtLogger = new JTextPane();
		scrollPaneLogger.setViewportView(txtLogger);
		txtLogger.setEditable(false);
		
		/*
		JLabel lblVideoTitle = new JLabel("Video: ");
		lblVideoTitle.setBounds(10, 66, 614, 39);
		Font titleFont = new Font("", Font.PLAIN, 15);
		lblVideoTitle.setFont(new Font("Dialog", Font.PLAIN, 19));
		contentPane.add(lblVideoTitle);
		*/

	}

	private void clickDownload(){
		if (btnDownload.getText().equals("Download")){
			//download selected video
			int rowSelected = tblFormats.getSelectedRow();
			if(rowSelected == -1){
				//no row selected
				showMessageBoxError("Select a download format first!");
				return;
			}
			
			String formatCode = tblFormats.getValueAt(rowSelected, 0).toString();
			YTDLJava.DownloadCallback callback = new YTDLJava.DownloadCallback() {
				
				@Override
				public void downloadUpdate(final String message, final float percent) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							lblProgress.setText(message);
							
							int percentDone = (int) (percent * 100);
							prbDownload.setValue(percentDone);
							
							addLog(message);
						}
					});
					System.out.println(message);
				}
				
				@Override
				public void downloadStarted() {}
				
				@Override
				public void downloadFinished() {
					guiDownloading(false);
					addLog("Download Complete!");
				}
			};
			ytdl.downloadVideo(txtURL.getText(), formatCode, callback);
			guiDownloading(true);
		} else{
			//button is cancel download
			ytdl.stopDownload();
			lblProgress.setText(lblProgress.getText());
			addLog("Download cancelled by user.");
			guiDownloading(false);
		}
		
		
	}
	
	private void guiDownloading(boolean enabled){
		if (enabled){
			//downloading
			prbDownload.setValue(0);
			btnDownload.setText("Cancel");
			txtURL.setEnabled(false);
			btnGetInfo.setEnabled(false);
			tblFormats.setEnabled(false);
			clearLogger();
		}
		else{
			//go back to normal screen
			btnDownload.setText("Download");
			txtURL.setEnabled(true);
			btnGetInfo.setEnabled(true);
			tblFormats.setEnabled(true);
		}
	}
	
	private void showMessageBoxError(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void fetchYTInfo(String url){

		if ((url.indexOf("http://") != 0) && (url.indexOf("https://") != 0)) {
			showMessageBoxError("Invalid youtube URL");
			return;
		}
		
		List<YTDLJava.YTFormat> formats = ytdl.getFormats(url);

		//clear formats table
		clearTable();
		
		
		//populate formats table
		DefaultTableModel model = (DefaultTableModel) tblFormats.getModel();
		for(YTDLJava.YTFormat f : formats){
			model.addRow(new Object[]{f.formatCode, f.extension, f.resolution, f.note, f.fileSize});
		}
	}
	
	private void clearTable(){
		DefaultTableModel model = (DefaultTableModel) tblFormats.getModel();
		int rowCount = model.getRowCount();
		//Remove each row from end of table
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
	
	private void addLog(String message){
		txtLogger.setText(txtLogger.getText() + "\n" + message);
		JScrollBar vertical = scrollPaneLogger.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	private void clearLogger(){
		txtLogger.setText("");
	}
}
