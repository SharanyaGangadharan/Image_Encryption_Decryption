import java.io.*;
import java.awt.*;
import java.applet.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class ShowImage extends JPanel
{
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private static Cipher cipher = null;
  	static Image image,img;
    String path;
    static int num;
    String path1=new String();char ch;int i,x;
	String passw=new String();
	String str=new String();
	String plainText = new String();	
	static boolean f;
	boolean flag =false,flag1=false;
	Image cell[] = new Image[10000*10000];
	int iw,ih;
	int tw,th;
	int til = 4;
	SecretKey secretKey ;
	KeyGenerator keyGenerator;
	byte[] plainTextByte;byte[] encryptedBytes ;byte[] decryptedBytes;

	void accKey()throws Exception
	{
		keyGenerator = KeyGenerator.getInstance("DESede");
		keyGenerator.init(168);
		secretKey = keyGenerator.generateKey();
		cipher = Cipher.getInstance("DESede");
		System.out.println("Enter Password");
		plainText = br.readLine();
		System.out.println("Password Before Encryption: " +plainText );
		plainTextByte = plainText.getBytes("UTF8");
	}
    
	void acceptImg()throws IOException
	{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter 1 for accepting an image from URL \n 2. For accepting an image from             Local File");
        num=Integer.parseInt(br.readLine());
        switch(num)
		{
            case 1: System.out.println("Enter path name ");
					path=br.readLine();
					URL url = new URL(path);
					image = ImageIO.read(url);
					break;
            case 2: System.out.println("Enter path name ");
					path1=br.readLine();
					x=0;
					for( i=0;i<path1.length();i++)
					{
						ch=path1.charAt(i);
						if(ch=='\\')
                        { 
							path=path1.substring(x,i)+"\\";
                 			str=str+path;
							x=i;
                        }
                   }
                   str=str+path1.substring(path1.lastIndexOf("\\"),path1.length());
                   image = Toolkit.getDefaultToolkit().getImage(str);
                   break;
            default: System.out.print("Invaild output");
					 System.exit(0);
		}
	}

	public void encryptI()throws InterruptedException,IOException,Exception
	{
		encryptedBytes = encrypt(plainTextByte, secretKey);
		String encryptedText = new String(encryptedBytes, "UTF8");
		System.out.println("Password After Encryption: " + encryptedText);
		flag1=true;
		MediaTracker t = new MediaTracker(this);
		t.addImage(image,0);
		t.waitForID(0);
		iw=image.getWidth(null);
		ih=image.getHeight(null);
		tw=iw/til;
		th=ih/til;
		CropImageFilter f;
		FilteredImageSource fis;
		t = new MediaTracker(this);
		for(int y=0;y<til;y++)
		{
			for(int x=0;x<til;x++)
			{
				f = new CropImageFilter(tw*x, th*y, tw, th);
				fis = new FilteredImageSource(image.getSource(),f);
				int i= y*til+x;
				cell[i]= createImage(fis);
				t.addImage(cell[i],i);
			}
		}
		t.waitForAll();
		for(int i=0;i<((til*til)*2);i++)
		{
			int si=(int)(Math.random()*(til*til));
			int di=(int)(Math.random()*(til*til));
			Image tmp = cell[si];
			cell[si]=cell[di];
			cell[di]=tmp;
		}
	}

	public void decryptI()throws IOException
    {
        if(num==1)
        {
			URL url = new URL(path);
			img = ImageIO.read(url);
        }
		if(num==2)
        {
            img = Toolkit.getDefaultToolkit().getImage(str);
        }
		flag=true;
    }

	public void update(Graphics g)
    {
        paint(g);
    }
	
	public void paint(Graphics g)
    {
        if(flag==true)
        {
            g.drawImage(img,100,100,image.getWidth(null),image.getHeight(null), this);
        }
		if(flag==false)
        {
            for(int y=0;y<til;y++)
            {
                for(int x=0;x<til;x++)
                {
                    g.drawImage(cell[y*til+x],x*tw,y*th,null);
                }
            }
        }
    }
	
	static byte[] encrypt(byte[] plainTextByte, SecretKey secretKey)throws Exception
    {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(plainTextByte);
		return encryptedBytes;
    }

	static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey)throws Exception
    {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return decryptedBytes;
    }
	
	void accept()thrws IOException,Exception
    {
		String decryptedText;
		if(flag1==true)
		{
            decryptedBytes = decrypt(encryptedBytes, secretKey);
            decryptedText = new String(decryptedBytes, "UTF8");
		}
             else
		{
             decryptedText=plainText;
		}
		Scanner input = new Scanner(System.in);
		System.out.print("Password:  ");
		passw=input.next();
		f=decryptedText.equals(passw);
	}
	
	public static void main(String args[])throws InterruptedException, IOException,Exception
    {
		JFrame frame = new JFrame("CHAOS IMAGE ENCRYPTION AND DECRYPTION USING PIXEL ");
		frame.setSize(1000,1000);
		ShowImage panel = new ShowImage();
		panel.acceptImg();
		panel.accKey();
		BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
		char ch;
		String ch1;
		System.out.println("1>Enter 'E' to ENCRYPT the Image \n2>'D' to Directly Display the DECRYPTED Image");
		ch=(char)br.read();
		switch(ch)
        {
			case 'e':
			case'E': panel.encryptI();
					frame.setContentPane(panel);
					frame.setVisible(true);
					Scanner input = new Scanner(System.in);
					System.out.println("Do you wish to Decrypt the Image? Enter YES  or NO to Exit");
					ch1=input.next();
					if(ch1.equalsIgnoreCase("no"))
					{
						System.exit(0);
					}
					else if(ch1.equalsIgnoreCase("yes"))
					{
						System.out.println("Enter Password");
						panel.accept();
						if(f==true)
						{
							System.out.flush();
							JFrame frame1 = new JFrame("CHAOS IMAGE ENCRYPTION AND DECRYPTION USING TILE SHUFFLING ");
							frame1.setSize(1000,1000);
							frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							panel.decryptI();
							frame1.setContentPane(panel);
							frame1.setVisible(true);
						}
						else
						{
							System.out.println("INVALID PASSWORD");
							System.exit(0);
						}
					}
					break;
			case 'd':
			case 'D': System.out.println("Enter Password");
					panel.accept();
					if(f==true)
                    {
						panel.decryptI();
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.setContentPane(panel);
						frame.setVisible(true);
                    }
					break;
			default:System.out.println("invalid input");
			break;
        }
    }
}
