package framework;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author chizl
 */
public class Sprite extends Node{
    
    Image s_Image = null;
    
    Image s_Frames[] = null;
    int s_FrameTimes = -1;
    int s_FrameCurDelayTime = -1;
    int s_FrameTotalDelayTime = -1;
    int s_curFrameIndex = 0;
    
    private Sprite(){}

    protected void drawSelf(Graphics g){
        if(s_Image != null){
            g.setClip(this.x - s_Image.getWidth()/2, this.y - s_Image.getHeight()/2, s_Image.getWidth(), s_Image.getHeight());
            g.setColor(n_Color);
            g.drawImage(s_Image, this.x - s_Image.getWidth()/2, this.y - s_Image.getHeight()/2, 0);
        }
    }
    
    public static Sprite create(String file){
        return create(makeImage(file));
    }
    
    public static Sprite create(Image image){
        Sprite sp = new Sprite();
        sp.setImage(image);
        return sp;
    }
    
    public static Image makeImage(String file){
        Image img = null;
        try {
            img = Image.createImage(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return img;
    }
    
    public Sprite setImage(Image image){
        this.s_Image = image;
        if(this.s_Image != null){
            this.n_rect = new Rect(this.x - s_Image.getWidth()/2, this.y - s_Image.getHeight()/2, s_Image.getWidth(), s_Image.getHeight());
        }else{
            this.n_rect = Rect.ZERO;
        }
        return this;
    }
    
    public Image getImage(){
        return this.s_Image;
    }
    
    /**
     * Sprite帧动画
     * @param 图片数组
     * @param 每一帧的间隔时间
     * @param 循环次数，<=0 : 无限循环， 
     */
    public void runSpriteFrames(Image[] images,int delayTime,int times){
        this.s_Frames = images;
        this.s_FrameTotalDelayTime = delayTime;
        this.s_FrameTimes = times - 1;
        this.s_curFrameIndex = 0;
    }
    
    protected void animUpdate(long dt){
        super.animUpdate(dt);
        if(s_Frames != null){
            s_FrameCurDelayTime += dt;
            if(s_FrameCurDelayTime >= s_FrameTotalDelayTime){
                if(s_curFrameIndex == s_Frames.length){
                    s_curFrameIndex = 0;
                    if(s_FrameTimes < 0){
                        s_Image = s_Frames[s_curFrameIndex];
                    }else if(s_FrameTimes == 0){
                        s_Frames = null;
                    }else{
                        s_FrameTimes--;
                        s_Image = s_Frames[s_curFrameIndex];
                    }
                }else{
                    s_Image = s_Frames[s_curFrameIndex++];
                }
                s_FrameCurDelayTime = 0;
            }
        }
    }
    
}
