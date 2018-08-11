package Zegar;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

public class ClockPanel extends JPanel implements Runnable {
	private BufferedImage bi;
	private Graphics2D g2bi;
	private Calendar time = Calendar.getInstance();
	private volatile boolean start = false;
	private float transparency= 0.45f;
	private Color color = Color.DARK_GRAY;
	int x;
	int y;
	
	
	
	public ClockPanel (int width,int height)
	{
		
		initialize(new Dimension(width,height));
	}
	
	public void initialize(Dimension size)
	{
		this.setSize(size);
		this.setPreferredSize(size);
		this.setOpaque(false);
		if (bi==null || bi.getWidth() != this.getWidth()
				||bi.getHeight() != this.getHeight())
		{
			this.bi = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
			this.g2bi = bi.createGraphics();
			paintClock(g2bi,time);
		}
		
	}
	public void paintClock(Graphics2D g2, Calendar time)
	{
		int second = time.get(Calendar.SECOND);
		int minute = time.get(Calendar.MINUTE);
		int hour = time.get(Calendar.HOUR_OF_DAY);
		//odczyt minut/godzin co sekunde/minute
		double minute2=minute+(second*(1/60.0)) ;
		double hour2=hour+minute2*(1/60.0);
		//zamiana sekund minut i godzin na typ string z dwiema cyframi
		String s=String.format("%02d", second);
		String m=String.format("%02d", minute);
		String h=String.format("%02d", hour);
		// wygladzanie krawedzi
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING	, RenderingHints.VALUE_ANTIALIAS_ON);
		// czyszczenie
		Composite defaultCopmosite = g2.getComposite();
		
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		
		g2.setComposite(defaultCopmosite);
		
		//ustawienie linii			
		float strokeWidth= 3f;
		//ustawienie koloru i przezroczystosci
		
		//rysowanie z double
		Stroke stroke = new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);
		Point2D punktSrodkowy = new Point2D.Double(((bi.getHeight()-5)/2)+1,((bi.getWidth()-5)/2)+1);
		
		
		
		double promien = (((bi.getHeight()-5)/2.1)+1);
		double kat=0 ;
		
		// rysowanie linii na tarczy zegara
				g2.setColor(Color.LIGHT_GRAY);
		for (int i=0;i<60;i++)
		{
		
		kat = (2*Math.PI/60.0)*i-Math.PI/2.0;
		Point2D punktM2 = getPunktNaTarczy(kat, punktSrodkowy, promien);
		kat = (2*Math.PI/60.0)*i-Math.PI/2.0;
		Point2D punktM = getPunktNaTarczy(kat, punktSrodkowy, promien/1.05);
		
		g2.draw(new Line2D.Double(punktM2, punktM));
		}
		g2.setColor(color);
		for (int i=0;i<12;i++)
		{
		
		kat = (2*Math.PI/12.0)*i-Math.PI/2.0;
		Point2D punktH = getPunktNaTarczy(kat, punktSrodkowy, promien);
		kat = (2*Math.PI/12.0)*i-Math.PI/2.0;
		Point2D punktH2 = getPunktNaTarczy(kat, punktSrodkowy, promien/1.15);
		
		g2.draw(new Line2D.Double(punktH, punktH2));
		}

		
	
		//rysowanie wskazowek
		g2.setColor(color);
		kat = (2*Math.PI/60.0)*second-Math.PI/2.0;
		Point2D punktSekundy = getPunktNaTarczy(kat, punktSrodkowy, promien);
		g2.draw(new Line2D.Double(punktSrodkowy, punktSekundy));
		kat = (2*Math.PI/60.0)*minute2-Math.PI/2.0;
		Point2D punktMinuty = getPunktNaTarczy(kat, punktSrodkowy, promien/1.35);
		g2.draw(new Line2D.Double(punktSrodkowy, punktMinuty));
		
		kat = (2*Math.PI/12.0)*hour2-Math.PI/2.0;
		Point2D punktGodziny = getPunktNaTarczy(kat, punktSrodkowy, promien/2);
		g2.draw(new Line2D.Double(punktSrodkowy, punktGodziny));
		// rysowanie tarczy zegara
		g2.drawOval(1,1, (bi.getWidth()-5), (bi.getHeight()-5));
		
		// wyswietlenie godziny
				g2.setColor(new Color(0,0,0,255));
				String pelnaGodzina="";
				pelnaGodzina = new StringBuffer(pelnaGodzina).append(h+":"+m+":"+s).toString();
				Font font = new java.awt.Font("Tahoma",Font.BOLD+Font.ITALIC,bi.getHeight()/12);
				g2.setFont(font);
				g2.drawString(pelnaGodzina, 80, 60);
		
		
//		g2.setColor(new Color(0,0,0,255));
//		//ustawianie czcionki
//		Font font = new java.awt.Font("Tahoma",Font.BOLD+Font.ITALIC,bi.getHeight()/12);
//		g2.setFont(font);
//		//rozmiar tekstu
//		String str = String.valueOf("Time: "+second);
//		Rectangle2D stringBounds = font.getStringBounds(str,g2.getFontRenderContext());
//		g2.drawString(String.valueOf(stringBounds.getWidth()), 100, 100);
		
	}
	private Point2D getPunktNaTarczy(double kat, Point2D srodek,double promien)
	{
		double x = srodek.getX() + promien * Math.cos(kat);
		double y = srodek.getY() + promien *Math.sin(kat);
		return new Point2D.Double(x,y);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(bi, 0, 0, this);
	
		
	}

	public void start()
	{	this.start=true;
		Thread thread = new Thread(this);
		thread.start();
	}
	public void stop()
	{ 
		this.start = false;	
	}
	@Override
	public void run() {
		int prevSecond = -1;
		while(start)
		{
			time.setTimeInMillis(System.currentTimeMillis());
			int second = time.get(Calendar.SECOND);
			if (second != prevSecond)
			{
				prevSecond = second;
				paintClock(g2bi,time);
				repaint();
			}
			try
			{
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e)
			{
				
			}
		}
		
	}
}
