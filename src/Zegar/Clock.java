package Zegar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Clock {
	int x = 0;
	int y = 0;
	private JFrame frame;

//	public void stworzTabele() {
//	
//	 try {
//	 Connection conn = DriverManager.getConnection("jdbc:sqlite:zegar.db");
//	 Statement stmt = conn.createStatement();
//	 String tabelaSQL = "CREATE TABLE `polozenie` (`X` NUMERIC NOT NULL,`Y` NUMERIC NOT NULL)";
//	
//	
//	 stmt.executeUpdate(tabelaSQL);
//	
//	 stmt.close();
//	 conn.close();
//	 } catch (SQLException e) {
//	 e.printStackTrace();
//	 }
//	 }

	public void zapisPolozenia() {

		 
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:zegar.db");
			Statement stmt = conn.createStatement();

			String sql = "insert into polozenie values (" + x + "," + y + ")";

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void odczytPolozenia() {

		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:zegar.db");
			Statement stmt = conn.createStatement();

			ResultSet liczba = stmt.executeQuery("select x,y from polozenie");
			while (liczba.next()) {

				x = liczba.getInt(1);
				y = liczba.getInt(2);
			}

			liczba.close();
			stmt.close();

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Clock window = new Clock();
					window.frame.pack();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Clock() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
//		stworzTabele();
		odczytPolozenia();
		frame = new JFrame();
		frame.setLocation(x, y);

		frame.setType(JFrame.Type.UTILITY);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ClockPanel zp = new ClockPanel(250, 250);
		zp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				zapisPolozenia();
				System.exit(0);

			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				x = e.getXOnScreen() - x;
//				System.out.println("wartosc x przy wcisnieciu: "+x);
				y = e.getYOnScreen() - y;
//				System.out.println("wartosc y przy wcisnieciu: "+y);

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				
				x = e.getXOnScreen() - x;
//				System.out.println("wartosc x przy puszczeniu: "+x);
				y = e.getYOnScreen() - y;
//				System.out.println("wartosc y przy puszczeniu: "+y);
			}
		});

		zp.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {

				int b = e.getXOnScreen() - x;
				int c = e.getYOnScreen() - y;
								
				

				frame.setLocation(b, c);

			}
		});

		frame.setContentPane(zp);
		zp.start();

	}

}
