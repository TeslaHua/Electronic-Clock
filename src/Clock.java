/**
挂钟：可以实现开始和暂停的功能，有时分秒针转动
线程问题：以及验证线程的冲突问题，音频播放与挂钟绘图在同一个线程中时有可能会发生冲突而导致挂钟延时；
		  因此需要为音频播放单独开启一个线程。
运用知识： 十五章、十六章
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;   //在二维图形中GeneralPath 类表示根据直线、二次曲线和三次 (Bézier) 曲线构造的几何路径 
import java.awt.geom.Line2D;        //在二维空间中的Line2D 表示 (x,y) 坐标空间中的线段
import java.awt.geom.Rectangle2D;   //在二维空间中的Rectangle2D类描述通过位置 (x,y) 和尺寸 (w x h) 定义的矩形
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.RenderingHints;  
import java.awt.Shape; 
import java.util.Calendar;
import java.net.URL;
import java.applet.*;

public class Clock extends JFrame
{

	private JButton jbtstart = new JButton("start");
	private JButton jbtstop = new JButton("stop");
	private JPanel buttonpanel = new JPanel(new FlowLayout()) ;
	private stillClockpanel stillclockpanel = new stillClockpanel("Time Clock");   //生成其内部类的一个实例
	public Clock()
	{

		jbtstart.setFont(new Font("Serif",Font.BOLD,20));
		jbtstop.setFont(new Font("Serif",Font.BOLD,20));
		jbtstart.setSize(20,20);
		jbtstop.setSize(20,20);
		jbtstart.setToolTipText("Start the Clock.");
		jbtstop.setToolTipText("Stop the Clock.");
	
		buttonpanel.add(jbtstart);
		buttonpanel.add(jbtstop);

		add(buttonpanel,BorderLayout.SOUTH);
		add(stillclockpanel,BorderLayout.CENTER);

		jbtstart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				stillclockpanel.timer.start();    //外部类可以通过内部类的实例调用其数据（包括private）
				stillclockpanel.calendar = stillclockpanel.calendar.getInstance();  //重新获取一个日历的实例
				//重新获取时分秒
				stillclockpanel.hour = stillclockpanel.calendar.get(Calendar.HOUR);  
				stillclockpanel.minute = stillclockpanel.calendar.get(Calendar.MINUTE);  
				stillclockpanel.second = stillclockpanel.calendar.get(Calendar.SECOND); 
			}
		});

		jbtstop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				stillclockpanel.timer.stop();
			}
		});
	}
	
	class stillClockpanel extends JPanel
	{
		private String message = "Time Clock";
		private double radius ;                             //设置圆的半径
		private Calendar calendar = Calendar.getInstance(); //Calendar 是一个抽象类，所以不能实例化，通过这种方式得到一个日历的实例
		private int hour = calendar.get(Calendar.HOUR);  
		private int minute = calendar.get(Calendar.MINUTE);  
		private int second = calendar.get(Calendar.SECOND); 
		private static final double rate = 0.03;  //设置一个基本的缩减比例

		//以下代码创建三个音频剪辑，在整分钟的时候播放

		private URL Audiourl1 = getClass().getResource("music.wav");    
		private AudioClip audioClip1 = Applet.newAudioClip(Audiourl1);  
		
		private URL Audiourl2 = getClass().getResource("musicc.wav");    
		private AudioClip audioClip2 = Applet.newAudioClip(Audiourl2);   

		private URL Audiourl3 = getClass().getResource("musiccc.wav");    
		private AudioClip audioClip3 = Applet.newAudioClip(Audiourl3);  

		private Runnable audioplay = new AudioPlay();  //创建一个AudioPlay的任务

		//以上代码创建三个音频剪辑，在整分钟的时候播放

		private Timer timer = new Timer(1000,new ActionListener(){     //设置一秒（1000毫秒）触发一次，并把监听器对象注册给源对象
			public void actionPerformed(ActionEvent e)
			{
				second++;
				if(second==60)
				{
					//以下代码为单线程播放音频
					/*audioClip1.play();  //音频一
					try
					{
						Thread.sleep(1500);  //此线程延时，确保音频一在音频二开始之前结束
						audioClip2.play();	
						Thread.sleep(1500); //此线程延时，确保音频二在音频三开始之前结束
					}
					catch (InterruptedException ex)
					{
					}
					audioClip3.play();*/
					//以上代码为单线程播放音频
					new Thread(audioplay).start();//每次给这个任务分发一个线程,因为每一个Thread都只能start一次


					second=0;
					minute++;
					if(minute==60)
					{
						minute=0;
						hour++;
						if(hour==24)
						{
							hour=0;
							minute=0;
							second=0;
						}
					}
				}
				repaint();  //重新绘图
			}
		});

		public stillClockpanel(String message)
		{
			this.message = message;
			TitledBorder titled =new TitledBorder(message);
			titled.setTitleFont(new Font("DialogInput",Font.ITALIC,20));
			titled.setTitleColor(new Color(100,200,100));
			setBorder(titled);
			timer.start();   //启动时钟
		}
		public Dimension getPreferredSize() {   //初始化stillClockpanel的实例的大小为(350,400)
			return new Dimension(350, 400);  
		}  
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;   //坐标系转换


			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
			Insets insets = getInsets();                         //得到这个面板的边框空白距离
			int wid = getWidth() - insets.left - insets.right;   //这个面板的实际宽度
			int hei = getHeight() - insets.top - insets.bottom;  //这个面板的实际高度
			double radius = (Math.min(wid, hei)) / 2 / (1 + rate);   
			//后续呈现相对于前一位置平移指定的距离,tx表示沿x轴平移的距离,ty表示沿y轴平移的距离，
			g2d.translate(insets.left + radius * (1 + rate), insets.top + radius * (1 + rate)); 
			//在这里tx表示实际宽度的一半，ty表示实际高度的一半
			g2d.scale(1, -1);                                                   //y轴沿x轴镜像

			paintAllPoint(radius, g2d);
			paintHourPointer(radius,g2d,hour,minute,second);
			paintMinutePointer(radius,g2d,minute,second);
			paintSecondPointer(radius,g2d,second);
			paintCenterPoint(g2d);

			g2d.scale(1, -1);   //将已经转换过来的坐标系再转换回来
			g2d.translate(-(insets.left + radius * (1 + rate)),-(insets.top + radius * (1 + rate)));
		}

		private void paintHourPointer(double radius,Graphics2D g2d,int hour,int minute,int second)
		{ 
			double angle = 90 - (hour + minute / 60.0 + second / 3600.0) * 30;  
			Shape pointerShape = createPointerShape(radius * 0.6, radius * 0.06, radius * 0.1, angle);  
			g2d.setColor(Color.LIGHT_GRAY);  
			g2d.fill(pointerShape);  
			g2d.setColor(Color.DARK_GRAY);  
			g2d.draw(pointerShape);  
		}

		private void paintMinutePointer(double radius,Graphics2D g2d,int minute,int second)
		{
			double angle = 90 - (minute + second / 60.0) * 6;  
			Shape pointerShape = createPointerShape(radius * 0.8, radius * 0.04, radius * 0.08, angle);  
			g2d.setColor(Color.LIGHT_GRAY);  
			g2d.fill(pointerShape);  
			g2d.setColor(Color.DARK_GRAY);  
			g2d.draw(pointerShape);
		}
		private void paintSecondPointer(double radius,Graphics2D g2d,int second)
		{
			g2d.setColor(Color.BLACK);   
			int angle = 90 - second * 6;  
			double pos[] = getPointIndex(radius * 0.9, angle);  
			double pos1[] = getPointIndex(radius * 0.2, angle + 180);  
			Line2D line = new Line2D.Double(pos1[0], pos1[1], pos[0], pos[1]);  
			g2d.draw(line);
		}

		private void paintCenterPoint(Graphics2D g2d)
		{
			g2d.setColor(Color.BLUE);  
			Rectangle2D rect = new Rectangle2D.Double(-2, -2, 4, 4);  
			g2d.fill(rect); 
		}
		private void paintAllPoint(double radius, Graphics2D g2d)
		{
			for (int i = 0; i < 60; i++) {  
				int angle = 90 - i * 6;              //从最上面的点开始
				double pos[] = getPointIndex(radius, angle);  
				paintOnePoint(radius, g2d, pos[0], pos[1], i % 5 == 0);  
			}  
		}
		private void paintOnePoint(double radius,Graphics2D g2d,double x,double y,boolean flag)
		{
			g2d.setColor(flag ? Color.RED : Color.BLACK);  
			if (flag) {    //是整点时刻就红色大圆点
				//(左上顶点x坐标，左上顶点y坐标，外接矩形的宽度，外接矩形的高度)
				Ellipse2D rect = new Ellipse2D.Double(x - radius * rate, y - radius * rate, radius * rate * 2, radius * rate * 2);  
				//由于Ellipse2D类继承Shape接口,而Ellipse2D.Double类是继承了类Ellipse2D
				//Ellipse2D 类描述窗体矩形定义的椭圆,Double 类以 double 精度定义椭圆
				g2d.fill(rect);   //fill(shape);  形参需要一个继承了Shape接口的实例
			} 
			else {      //否则就是黑色小圆点
				Ellipse2D rect = new Ellipse2D.Double(x - radius * 0.02, y - radius * 0.02, radius * 0.04, radius * 0.04);  
				g2d.fill(rect);  
			}  
		}
		private double[] getPointIndex(double radius, double angle)
		{
			double radian = Math.toRadians(angle);   //将用角度表示的角转换为近似相等的用弧度表示的角
			double x = radius * Math.cos(radian);         //每个分钟点的x坐标
			double y = radius * Math.sin(radian);         //每个分钟点的y坐标
			return new double[] { x, y };  
		}

		private Shape createPointerShape(double r1, double r2, double r3, double angle)
		{
			GeneralPath gp = new GeneralPath();  
			double[] pos = getPointIndex(r1, angle);//指针定点  
			double[] pos1 = getPointIndex(r2, angle + 90);//指针侧边断点  
			gp.append(new Line2D.Double(pos[0], pos[1], pos1[0], pos1[1]), true);  //根据指定坐标构造并初始化 Line2D
			double[] pos2 = getPointIndex(r3, angle + 180);//指针尾部 
			
			//通过绘制一条从当前坐标(pos1[0],pos1[1])到指定新坐标(以float精度指定)的直线，将一个点添加到路径中
			gp.lineTo((float) pos2[0], (float) pos2[1]);  
			double[] pos3 = getPointIndex(r2, angle + 270);//指针的另一侧边断点  
			gp.lineTo((float) pos3[0], (float) pos3[1]);  
			gp.closePath();  //通过绘制一条向后延伸到最后一个 moveTo 的坐标的直线，封闭当前子路径
			return gp;  
		}

		//创建任务类AudioPlay继承Runnable接口
		class AudioPlay implements Runnable
		{
			public AudioPlay(){
			}
			public void run()
			{
				audioClip1.play();  //音频一
				try
				{
					Thread.sleep(1500);  //此线程延时，确保音频一在音频二开始之前结束
					audioClip2.play();	
					Thread.sleep(3000); //此线程延时，确保音频二在音频三开始之前结束
				}
				catch (InterruptedException ex)
				{
				}
				audioClip3.play();
			}
		}
	}

	public static void main(String[] args)
	{
		Clock myframe = new Clock();
		myframe.setTitle("Myframe");
		myframe.setLocationRelativeTo(null);
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.pack();
		myframe.setVisible(true);
	}
}


