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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField txtURL;
	private JTable tblFormats;
	private JButton btnDownload;
	private JButton btnGetInfo;
	
	private YTDLWrapper ytdl;
	
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
		ytdl = new YTDLWrapper();
		
		this.setTitle("QuickYT-DL (youtube-dl version: " + ytdl.getVersion() + ")" );
		
		//GUI initialization
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
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
		
		JProgressBar prbDownload = new JProgressBar();
		prbDownload.setBounds(10, 322, 638, 28);
		contentPane.add(prbDownload);
		
		JLabel lblProgress = new JLabel("Progress: ");
		lblProgress.setBounds(10, 297, 141, 14);
		contentPane.add(lblProgress);
		
		btnGetInfo = new JButton("Get Info");
		btnGetInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fetchYTInfo(txtURL.getText());
			}
		});
		btnGetInfo.setBounds(409, 34, 89, 23);
		contentPane.add(btnGetInfo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 66, 764, 220);
		contentPane.add(scrollPane);
		
		tblFormats = new JTable();
		scrollPane.setViewportView(tblFormats);
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
			
			ytdl.downloadVideo(txtURL.getText(), formatCode);
			guiDownloading(true);
		} else{
			//button is cancel download
			
		}
		
		
	}
	
	private void guiDownloading(boolean enabled){
		if (enabled){
			btnDownload.setText("Cancel");
			txtURL.setEnabled(false);
			btnGetInfo.setEnabled(false);
			tblFormats.setEnabled(false);
		}
		else{
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
		
		List<YTDLWrapper.YTFormat> formats = ytdl.getFormats(url);

		//clear formats table
		clearTable();
		
		
		//populate formats table
		DefaultTableModel model = (DefaultTableModel) tblFormats.getModel();
		for(YTDLWrapper.YTFormat f : formats){
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
}
