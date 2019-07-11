package com.xzcmapi.util;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

@Slf4j
public class ImageUtil {

    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 根据图片路径生成缩略图 </p>
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public static InputStream thumbnailImage(String name,InputStream inputStream, int w, int h, boolean force){
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;
                // 获取图片后缀
                if(name.contains(".")) {
                    suffix = name.substring(name.lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || !types.toLowerCase().contains(suffix.toLowerCase())){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return null;
                }
                log.debug("target image's size, width:{}, height:{}.",w,h);
                Image img = ImageIO.read(inputStream);
                if(!force){
                    // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if((width*1.0)/w < (height*1.0)/h){
                        if(width > w){
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w/(width*1.0)));
                            log.debug("change image's height, width:{}, height:{}.",w,h);
                        }
                    } else {
                        if(height > h){
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h/(height*1.0)));
                            log.debug("change image's width, width:{}, height:{}.",w,h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bi, suffix, os);
                inputStream = new ByteArrayInputStream(os.toByteArray());
                return inputStream;
//                ImageIO.write(bi, suffix, new File(p.substring(0,p.lastIndexOf(File.separator)) + File.separator + prevfix +imgFile.getName()));
            } catch (IOException e) {
               log.error("generate thumbnail image failed.",e);
               return null;
            }
    }

    public static boolean judgeImage(String name){
        String types = Arrays.toString(ImageIO.getReaderFormatNames());
        String suffix = null;
        // 获取图片后缀
        if(name.contains(".")) {
            suffix = name.substring(name.lastIndexOf(".") + 1);
        }// 类型和图片后缀全部小写，然后判断后缀是否合法
        if(suffix == null || !types.toLowerCase().contains(suffix.toLowerCase())){
//            log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\10130\\Desktop\\timg.jpg");
        try {
            InputStream inputStream = new FileInputStream(file);
            InputStream inputStream1 = thumbnailImage("timg.jpg", inputStream, 250, 250, false);
            byte[] buffer = new byte[inputStream1.available()];
            inputStream1.read(buffer);
            File targetFile = new File("C:\\Users\\10130\\Desktop\\thum.jpg");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
//        new ImageUtil().thumbnailImage("C:\\Users\\10130\\Desktop\\timg.jpg", 200, 250,DEFAULT_PREVFIX,DEFAULT_FORCE);
    }
}