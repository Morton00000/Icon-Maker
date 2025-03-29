import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class IconMaker {
   
   public static void main(String[] args) throws IOException {
      
      List<String> files = new ArrayList<String>();
      List<String> filesname = new ArrayList<String>();

      // find all files in folder and find duplicate names based on files type     
      File folder = new File("./");

      for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
        } else {
            String filetmp = fileEntry.getName();
            if (Objects.equals(IconMaker.getFileExtension(filetmp), "config")) {
               files.add(filetmp.substring(0, filetmp.length() - 7));
            } else if (Objects.equals(IconMaker.getFileExtension(filetmp), "png")) {
               if (files.contains(filetmp.substring(0, filetmp.length() - 4))) {
                  filesname.add(filetmp.substring(0, filetmp.length() - 4));  
               }
            }
         }
      }
      
      unstich(filesname);
   }
      
   // unstiching loop per file set
   public static void unstich(List<String> names) throws IOException {
      
      int[] change = {0};
      int[] newfile = {0};
      int count = 0;
      
      for (int x = 0; x < names.size(); x++) {
      
         String line = null;
      
         BufferedImage img = ImageIO.read(new File(names.get(x) + ".png"));
         BufferedReader config = new BufferedReader(new FileReader(names.get(x) + ".config"));
         
         System.out.println(names.get(x) + ".config being read");  
      
         int spritesize = img.getHeight()/16;
      
         // firstline directory create
         String[] firstline = config.readLine().split("=");
         String dir = "." + firstline[1].substring(0, firstline[1].length() - 1);

         File theDir = new File(dir);
         theDir.mkdirs();
         
         count++;
         System.out.println("line " + count + " read");
         List<String> colorcodes = new ArrayList<String>();
         List<String> colornames = new ArrayList<String>();    
      
         // get colors
         while ((line = config.readLine()) != null) {
            if ((line.length() < 4) || (line.charAt(0) == '*')) {
               count++;
               System.out.println("line " + line +" skiped");  
            } else if (line.charAt(0) == '#') {
            
               count++;
               System.out.println("line " + line + " read");
                           
               // split line
               String[] tmpline = line.split("=");
               colorcodes.add(tmpline[0]);
               colornames.add(tmpline[1]);
               
            // unstiching
            } else if ((line.length() < 4) || (line.charAt(0) == '*')) {
               count++;
               System.out.println("line " + line +" skiped");  
            } else {
            
               count++;
               System.out.println("line " + line + " read");
            
               // split line
               String[] tmpline = line.split("=");
               String[] tmppath = tmpline[1].split("/");
               String[] rcstring = tmpline[0].split("\\.");
            
               // setup locations
               int[] rc = {((Integer.parseInt(rcstring[0]) - 1) * spritesize), ((Integer.parseInt(rcstring[1]) - 1) * spritesize)};
               
               output(dir, rc, tmppath, img, spritesize, spritesize, change, newfile, colorcodes, colornames);
            }
         }
         count = 0; 
      }
      
      System.out.println(change[0] + " change(s)");
      System.out.println(newfile[0] + " new file(s)");
   }
   
   // output function
   public static void output(String basedir, int[] rc, String[] basepath, BufferedImage img, int spriteh, int spritew, int[] change, int[] newfile, List<String> ccodes, List<String> cnames) throws IOException {
            
      // for all colors
      for (int x = 0; x < cnames.size(); x++) {
      
      String[] path = Arrays.copyOf(basepath, basepath.length);
      String dir = basedir;
      
      // replace <#>
      for (int i = 0; i < path.length; i++)  {
         path[i] = path[i].replace("<#>", cnames.get(x));
      System.out.println(cnames.get(x));
      System.out.println(Arrays.toString(path));
      }
      
      // output folder(s)
      for (int i = 0; i < (path.length - 1); i++) {
         dir = dir + "/" + path[i];
      System.out.println(dir);
      }
           
      File theSubDir = new File(dir);
      theSubDir.mkdirs();
           
      // setup output image for transparency
      BufferedImage tmpimg = new BufferedImage(spriteh, spritew, Transparency.BITMASK);

      // split and output images
      Graphics2D sprite = tmpimg.createGraphics();
      sprite.drawImage(img.getSubimage(rc[1], rc[0], spriteh, spritew),0,0, null);
      tmpimg = multiplyImageColors(tmpimg, Color.decode(ccodes.get(x)));
          
      // does exsist
      boolean check = new File(dir, path[path.length - 1] + ".png").exists();
     
      if(check) {
         BufferedImage isthere = ImageIO.read(new File(dir + "/" + path[path.length - 1] + ".png"));
         if (bufferedImagesEqual(isthere, tmpimg)) {
            // no change
            System.out.println("no change");
         } else {
            // changed
            ImageIO.write(tmpimg, "png", new File(dir + "/" + path[path.length - 1] + ".png"));
            System.out.println("change");
            change[0]++;
         }
      } else {
         // new file
         ImageIO.write(tmpimg, "png", new File(dir + "/" + path[path.length - 1] + ".png"));
         System.out.println("new file");
         newfile[0]++;
      }
      }
   }
   
    // Multiply image by color
    public static BufferedImage multiplyImageColors(BufferedImage image, Color colorMultiplier) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Get color multiplier components
        float rMult = colorMultiplier.getRed() / 255f;
        float gMult = colorMultiplier.getGreen() / 255f;
        float bMult = colorMultiplier.getBlue() / 255f;

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                Color originalColor = new Color(pixel, true);

                // Multiply RGB values
                int newRed = Math.min(255, (int) (originalColor.getRed() * rMult));
                int newGreen = Math.min(255, (int) (originalColor.getGreen() * gMult));
                int newBlue = Math.min(255, (int) (originalColor.getBlue() * bMult));

                // Preserve alpha
                int newAlpha = originalColor.getAlpha();

                // Set new pixel color
                Color newColor = new Color(newRed, newGreen, newBlue, newAlpha);
                newImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return newImage;
    }
   
   // get file type
   public static String getFileExtension(String fullName) {
      String fileName = new File(fullName).getName();
      int dotIndex = fileName.lastIndexOf('.');
      return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
   }
   
   // is buffered image same
   public static boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
      if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
         for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
               if (img1.getRGB(x, y) != img2.getRGB(x, y))
                  return false;
            }
         }
      } else {
         return false;
      }
      return true;
   }
}