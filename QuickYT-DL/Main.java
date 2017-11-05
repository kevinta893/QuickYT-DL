import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField txtURL;
	private JTable tblFormats;

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
		
		//GUI initialization
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 400);
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
		
		JButton btnDownload = new JButton("Download");
		btnDownload.setBounds(508, 322, 116, 28);
		contentPane.add(btnDownload);
		
		JProgressBar prbDownload = new JProgressBar();
		prbDownload.setBounds(10, 322, 488, 28);
		contentPane.add(prbDownload);
		
		JLabel lblProgress = new JLabel("Progress: ");
		lblProgress.setBounds(10, 297, 141, 14);
		contentPane.add(lblProgress);
		
		JButton btnGetInfo = new JButton("Get Info");
		btnGetInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fetchYTInfo(txtURL.getText());
			}
		});
		btnGetInfo.setBounds(409, 34, 89, 23);
		contentPane.add(btnGetInfo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 66, 614, 220);
		contentPane.add(scrollPane);
		
		tblFormats = new JTable();
		scrollPane.setViewportView(tblFormats);
		tblFormats.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tblFormats.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Format Code", "Extension", "Resolution", "Description"
			}
		));
		

	}

	
	private void guiDownloading(boolean b){

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
		model.addRow(new Object[]{"Column 1", "Column 2", "Column 3", "column3"});
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
