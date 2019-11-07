package express.sch.importer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import objects.Circuit;

/**
 * This is a simple demo to experiment with expresssch files
 */
public class Test {
	public static void main(String[] a) {
		final JFrame f = new JFrame("ExpressSCH Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open...");
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(openItem);
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		f.setJMenuBar(menuBar);
		f.getContentPane().setLayout(new BorderLayout());
		final JTabbedPane tp = new JTabbedPane();
		f.getContentPane().add(BorderLayout.CENTER, tp);
		
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile(tp);
			}
		});
		
		f.setSize(800, 800);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				f.setVisible(true);
			}
		});
	}
	
	static void loadFile(JTabbedPane tp) {
		JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
		if(fc.showOpenDialog(tp) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			ExpressSchReader reader = new ExpressSchReader();
			try {
				Circuit circuit = reader.readFile(file);
				JTree tree = new JTree(circuit);
				JScrollPane sp = new JScrollPane(tree);
				tp.add(file.getName(), sp);
				tp.setSelectedComponent(sp);
			} catch(IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(tp, ex.getClass().getName() + " " + ex.getMessage());
			}
		}
	}
}
