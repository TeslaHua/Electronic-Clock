/**
���ӣ�����ʵ�ֿ�ʼ����ͣ�Ĺ��ܣ���ʱ������ת��
�߳����⣺�Լ���֤�̵߳ĳ�ͻ���⣬��Ƶ��������ӻ�ͼ��ͬһ���߳���ʱ�п��ܻᷢ����ͻ�����¹�����ʱ��
		  �����ҪΪ��Ƶ���ŵ�������һ���̡߳�
����֪ʶ�� ʮ���¡�ʮ����
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
import java.awt.geom.GeneralPath;   //�ڶ�άͼ����GeneralPath ���ʾ����ֱ�ߡ��������ߺ����� (B��zier) ���߹���ļ���·�� 
import java.awt.geom.Line2D;        //�ڶ�ά�ռ��е�Line2D ��ʾ (x,y) ����ռ��е��߶�
import java.awt.geom.Rectangle2D;   //�ڶ�ά�ռ��е�Rectangle2D������ͨ��λ�� (x,y) �ͳߴ� (w x h) ����ľ���
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
	private stillClockpanel stillclockpanel = new stillClockpanel("Time Clock");   //�������ڲ����һ��ʵ��
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
				stillclockpanel.timer.start();    //�ⲿ�����ͨ���ڲ����ʵ�����������ݣ�����private��
				stillclockpanel.calendar = stillclockpanel.calendar.getInstance();  //���»�ȡһ��������ʵ��
				//���»�ȡʱ����
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
		private double radius ;                             //����Բ�İ뾶
		private Calendar calendar = Calendar.getInstance(); //Calendar ��һ�������࣬���Բ���ʵ������ͨ�����ַ�ʽ�õ�һ��������ʵ��
		private int hour = calendar.get(Calendar.HOUR);  
		private int minute = calendar.get(Calendar.MINUTE);  
		private int second = calendar.get(Calendar.SECOND); 
		private static final double rate = 0.03;  //����һ����������������

		//���´��봴��������Ƶ�������������ӵ�ʱ�򲥷�

		private URL Audiourl1 = getClass().getResource("music.wav");    
		private AudioClip audioClip1 = Applet.newAudioClip(Audiourl1);  
		
		private URL Audiourl2 = getClass().getResource("musicc.wav");    
		private AudioClip audioClip2 = Applet.newAudioClip(Audiourl2);   

		private URL Audiourl3 = getClass().getResource("musiccc.wav");    
		private AudioClip audioClip3 = Applet.newAudioClip(Audiourl3);  

		private Runnable audioplay = new AudioPlay();  //����һ��AudioPlay������

		//���ϴ��봴��������Ƶ�������������ӵ�ʱ�򲥷�

		private Timer timer = new Timer(1000,new ActionListener(){     //����һ�루1000���룩����һ�Σ����Ѽ���������ע���Դ����
			public void actionPerformed(ActionEvent e)
			{
				second++;
				if(second==60)
				{
					//���´���Ϊ���̲߳�����Ƶ
					/*audioClip1.play();  //��Ƶһ
					try
					{
						Thread.sleep(1500);  //���߳���ʱ��ȷ����Ƶһ����Ƶ����ʼ֮ǰ����
						audioClip2.play();	
						Thread.sleep(1500); //���߳���ʱ��ȷ����Ƶ������Ƶ����ʼ֮ǰ����
					}
					catch (InterruptedException ex)
					{
					}
					audioClip3.play();*/
					//���ϴ���Ϊ���̲߳�����Ƶ
					new Thread(audioplay).start();//ÿ�θ��������ַ�һ���߳�,��Ϊÿһ��Thread��ֻ��startһ��


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
				repaint();  //���»�ͼ
			}
		});

		public stillClockpanel(String message)
		{
			this.message = message;
			TitledBorder titled =new TitledBorder(message);
			titled.setTitleFont(new Font("DialogInput",Font.ITALIC,20));
			titled.setTitleColor(new Color(100,200,100));
			setBorder(titled);
			timer.start();   //����ʱ��
		}
		public Dimension getPreferredSize() {   //��ʼ��stillClockpanel��ʵ���Ĵ�СΪ(350,400)
			return new Dimension(350, 400);  
		}  
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;   //����ϵת��


			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
			Insets insets = getInsets();                         //�õ�������ı߿�հ׾���
			int wid = getWidth() - insets.left - insets.right;   //�������ʵ�ʿ��
			int hei = getHeight() - insets.top - insets.bottom;  //�������ʵ�ʸ߶�
			double radius = (Math.min(wid, hei)) / 2 / (1 + rate);   
			//�������������ǰһλ��ƽ��ָ���ľ���,tx��ʾ��x��ƽ�Ƶľ���,ty��ʾ��y��ƽ�Ƶľ��룬
			g2d.translate(insets.left + radius * (1 + rate), insets.top + radius * (1 + rate)); 
			//������tx��ʾʵ�ʿ�ȵ�һ�룬ty��ʾʵ�ʸ߶ȵ�һ��
			g2d.scale(1, -1);                                                   //y����x�᾵��

			paintAllPoint(radius, g2d);
			paintHourPointer(radius,g2d,hour,minute,second);
			paintMinutePointer(radius,g2d,minute,second);
			paintSecondPointer(radius,g2d,second);
			paintCenterPoint(g2d);

			g2d.scale(1, -1);   //���Ѿ�ת������������ϵ��ת������
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
				int angle = 90 - i * 6;              //��������ĵ㿪ʼ
				double pos[] = getPointIndex(radius, angle);  
				paintOnePoint(radius, g2d, pos[0], pos[1], i % 5 == 0);  
			}  
		}
		private void paintOnePoint(double radius,Graphics2D g2d,double x,double y,boolean flag)
		{
			g2d.setColor(flag ? Color.RED : Color.BLACK);  
			if (flag) {    //������ʱ�̾ͺ�ɫ��Բ��
				//(���϶���x���꣬���϶���y���꣬��Ӿ��εĿ�ȣ���Ӿ��εĸ߶�)
				Ellipse2D rect = new Ellipse2D.Double(x - radius * rate, y - radius * rate, radius * rate * 2, radius * rate * 2);  
				//����Ellipse2D��̳�Shape�ӿ�,��Ellipse2D.Double���Ǽ̳�����Ellipse2D
				//Ellipse2D ������������ζ������Բ,Double ���� double ���ȶ�����Բ
				g2d.fill(rect);   //fill(shape);  �β���Ҫһ���̳���Shape�ӿڵ�ʵ��
			} 
			else {      //������Ǻ�ɫСԲ��
				Ellipse2D rect = new Ellipse2D.Double(x - radius * 0.02, y - radius * 0.02, radius * 0.04, radius * 0.04);  
				g2d.fill(rect);  
			}  
		}
		private double[] getPointIndex(double radius, double angle)
		{
			double radian = Math.toRadians(angle);   //���ýǶȱ�ʾ�Ľ�ת��Ϊ������ȵ��û��ȱ�ʾ�Ľ�
			double x = radius * Math.cos(radian);         //ÿ�����ӵ��x����
			double y = radius * Math.sin(radian);         //ÿ�����ӵ��y����
			return new double[] { x, y };  
		}

		private Shape createPointerShape(double r1, double r2, double r3, double angle)
		{
			GeneralPath gp = new GeneralPath();  
			double[] pos = getPointIndex(r1, angle);//ָ�붨��  
			double[] pos1 = getPointIndex(r2, angle + 90);//ָ���߶ϵ�  
			gp.append(new Line2D.Double(pos[0], pos[1], pos1[0], pos1[1]), true);  //����ָ�����깹�첢��ʼ�� Line2D
			double[] pos2 = getPointIndex(r3, angle + 180);//ָ��β�� 
			
			//ͨ������һ���ӵ�ǰ����(pos1[0],pos1[1])��ָ��������(��float����ָ��)��ֱ�ߣ���һ������ӵ�·����
			gp.lineTo((float) pos2[0], (float) pos2[1]);  
			double[] pos3 = getPointIndex(r2, angle + 270);//ָ�����һ��߶ϵ�  
			gp.lineTo((float) pos3[0], (float) pos3[1]);  
			gp.closePath();  //ͨ������һ��������쵽���һ�� moveTo �������ֱ�ߣ���յ�ǰ��·��
			return gp;  
		}

		//����������AudioPlay�̳�Runnable�ӿ�
		class AudioPlay implements Runnable
		{
			public AudioPlay(){
			}
			public void run()
			{
				audioClip1.play();  //��Ƶһ
				try
				{
					Thread.sleep(1500);  //���߳���ʱ��ȷ����Ƶһ����Ƶ����ʼ֮ǰ����
					audioClip2.play();	
					Thread.sleep(3000); //���߳���ʱ��ȷ����Ƶ������Ƶ����ʼ֮ǰ����
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


