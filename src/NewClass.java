//DecodeMessage.java
 import java.awt.image.*;
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import javax.imageio.*;
 import java.util.regex.Matcher;
import java.util.regex.Pattern;

 
 public class NewClass extends JFrame implements ActionListener
 {
 JButton open = new JButton("Open"), decode = new JButton("Decode"),
    reset = new JButton("Reset");
 JTextArea message = new JTextArea(10,3);
 BufferedImage image = null;
 JScrollPane imagePane = new JScrollPane();
 JLabel passL ;
 JTextField pass ;
 public NewClass() {
    super("Decode stegonographic message in image");
    assembleInterface();
    this.setSize(500, 500);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
 //   this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
   //    getMaximumWindowBounds());
    this.setVisible(true);
    }
 
 private void assembleInterface() {
    JPanel p = new JPanel(new FlowLayout());
    p.add(open);
    p.add(decode);
    p.add(reset);
    passL = new JLabel("PIN");
    p.add(passL);
    pass = new JTextField(10);
    p.add(pass);
    this.getContentPane().add(p, BorderLayout.NORTH);
    open.addActionListener(this);
    decode.addActionListener(this);
    reset.addActionListener(this);
    open.setMnemonic('O');
    decode.setMnemonic('D');
    reset.setMnemonic('R');
    
    p = new JPanel(new GridLayout(1,1));
    p.add(new JScrollPane(message));
    message.setFont(new Font("Arial",Font.BOLD,20));
    p.setBorder(BorderFactory.createTitledBorder("Decoded message"));
    message.setEditable(false);
    this.getContentPane().add(p, BorderLayout.SOUTH);
    
    imagePane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
   this.getContentPane().add(imagePane, BorderLayout.CENTER);
    }
 public void actionPerformed(ActionEvent ae) {
    Object o = ae.getSource();
    if(o == open)
       openImage();
    else if(o == decode)
       decodeMessage();
    else if(o == reset) 
       resetInterface();
    }
 
 private java.io.File showFileDialog(boolean open) {
    JFileChooser fc = new JFileChooser("Open an image");
    javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
       public boolean accept(java.io.File f) {
          String name = f.getName().toLowerCase();
          return f.isDirectory() ||   name.endsWith(".png") || name.endsWith(".bmp");
          }
       public String getDescription() {
          return "Image (*.png, *.bmp)";
          }
       };
    fc.setAcceptAllFileFilterUsed(false);
    fc.addChoosableFileFilter(ff);
 
    java.io.File f = null;
    if(open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    else if(!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    return f;
   }
 
 private void openImage() {
    java.io.File f = showFileDialog(true);
    try {   
       image = ImageIO.read(f);
       JLabel l = new JLabel(new ImageIcon(image));
      imagePane.getViewport().add(l);
       this.validate();
       } catch(Exception ex) { ex.printStackTrace(); }
    }
 String decrypt(String s){
     int l = s.length();
     int i;
     String ans = "";
     for(i=0;i<=l/2;i++){
         ans+=(char)(s.charAt(i)-i);
     }
     for(;i<l;i++){
         ans+=(char)(s.charAt(i)+i);
     }
     System.out.println(" DECRYPTED = "+ans);
     return ans;
 }
 boolean check(String s){
    int i=1;
    int beg=0,end = 0;
    while(i<s.length()){
        if(s.charAt(i-1)=='/'&&s.charAt(i)=='*')
        {
            beg = i-1;
            int j = i;
            while(j<s.length()){
               if(s.charAt(j-1)=='*'&&s.charAt(j)=='/')
               {
                  end = j-1;
                  break;
               }
               j++;
            }
            if(end-beg+1<10)
                break;
              
        }
        i++;
    }
    int x = beg+2;
    String ans = "";
    while(x<=end-1)
    {
        ans+=(char)(s.charAt(x));
        x++;
    }
    System.out.println("ANSWER = "+ans);
    ans = decrypt(ans);
   if(ans.equals(pass.getText()))
   {
      ans="";
       i=0;
      while(i<beg){
          ans+=(char)(s.charAt(i));
          i++;
      }
      message.setText(ans);
       return true;
   }
       
   else
       return false;
 }
 private void decodeMessage() {
    int len = extractInteger(image, 0, 0);
    byte b[] = new byte[len];
    for(int i=0; i<len; i++)
       b[i] = extractByte(image, i*8+32, 0);
    String ext = new String(b);
    if(check(ext)==false){
           JOptionPane.showMessageDialog(this, "Wrong PIN", 
         "Wrong PIN", JOptionPane.ERROR_MESSAGE);
       return;
    }
    
    }
 
 private int extractInteger(BufferedImage img, int start, int storageBit) {
   int maxX = img.getWidth(), maxY = img.getHeight(), 
       startX = 0, startY = 0, count=0;
    int length = 0;
    for(int i=startX; i<maxX && count<32; i++) {
       for(int j=startY; j<maxY && count<32; j++) {
          int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
          length = setBitValue(length, count, bit);
          count++;
          }
       }
    return length;
    }
 
 private byte extractByte(BufferedImage img, int start, int storageBit) {
    int maxX = img.getWidth(), maxY = img.getHeight(), 
       startX = start/maxY, startY = start - startX*maxY, count=0;
    byte b = 0;
    for(int i=startX; i<maxX && count<8; i++) {
       for(int j=startY; j<maxY && count<8; j++) {
          int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
          b = (byte)setBitValue(b, count, bit);
          count++;
          }
       }
    return b;
    }
 
 private void resetInterface() {
    message.setText("");
    pass.setText("");
    imagePane.getViewport().removeAll();
    image = null;
    this.validate();
    }
 
 private int getBitValue(int n, int location) {
    int v = n & (int) Math.round(Math.pow(2, location));
    System.out.println(v+"VV"+n);
    return v==0?0:1;
    }
 
 private int setBitValue(int n, int location, int bit) {
    int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
    
    System.out.println(location+"ssa"+n+"asss"+bit+"asd"+ toggle+"ioio"+bv);
    if(bv == bit)
       return n;
    if(bv == 0 && bit == 1)
       n |= toggle;
    else if(bv == 1 && bit == 0)
       n ^= toggle;
    return n;
    }
 
 public static void main(String arg[]) {
    new NewClass();
    }
 }