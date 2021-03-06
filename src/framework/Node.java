
package framework;

import framework.action.Animation;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author chizl
 */
public /*abstract*/ class Node {
    
    public static final int ALIGN_H_CENTER = 1;
    public static final int ALIGN_H_LEFT = 1 << 1;
    public static final int ALIGN_H_RIGHT = 1 << 2;
    public static final int ALIGN_V_CENTER = 1 << 3;
    public static final int ALIGN_V_TOP = 1 << 4;
    public static final int ALIGN_V_BOTTOM = 1 << 5;
    
    protected int n_align_model = ALIGN_H_CENTER & ALIGN_V_CENTER;
    
    protected boolean n_bEntered = false;
    protected boolean n_bExited = false;
    protected boolean n_bRemoved = false;
    
    short n_childNums = 0;
    
    protected Node n_prev = null;
    protected Node n_next = null;
    protected Node n_chd_head = null;
    protected Node n_parent = null;
    protected short n_zorder = 0;
    protected short n_tag = 0;
    
    protected Rect n_rect = Rect.ZERO;
    protected short x = 0;
    protected short y = 0;
    
    protected Rect n_clip_rect = null;
    boolean n_b_clip = false;
    boolean n_b_can_cip_self = true;
    
    protected int n_Color = 0xffffff;
    
    protected boolean n_bEnableUpdate = false;
    
    protected boolean n_bShowed = true;
            
    protected void onDraw(Graphics g) {
        if(!n_bEntered){
            n_bEntered = true;
            onEnter();
            Node node = n_chd_head;
            while(node != null){
                if(!node.n_bEntered){
                    node.n_bEntered = true;
                    node.onEnter();
                }
                node = node.n_next;
            }
        }else if(!n_bExited){
            if(n_bRemoved){
                romoveNow();
            }else{
                if(n_bShowed && (n_parent == null || (n_parent != null && n_parent.getVisible()))){
                    n_b_can_cip_self = !clip(g);
                    drawSelf(g);
                }
                drawChilds(g);
            }
        }
    }
    
    protected static int drawCell = 0;
    protected void drawSelf(Graphics g){
    }
    private void drawChilds(Graphics g){
        Node node = n_chd_head;
        while(node != null){
            node.onDraw(g);
            node = node.n_next;
        }
    }
    
    protected boolean clip(Graphics g){
        if(n_b_clip && n_clip_rect != null){
            g.setClip(n_clip_rect.x + this.x, n_clip_rect.y + this.y, n_clip_rect.width, n_clip_rect.height);
        }
        return n_b_clip || n_clip_rect != null;
    }

    public void addChild(Node n,short zorder){
        n.n_zorder = zorder;
        addChild(n);
    }
    public void addChild(Node n) {
        n_childNums++;
        addNode(n);
    }
    private void addNode(Node n) {
        
        if(n.n_parent != null){
            System.err.println("Node is be added!");
            return;
        }
        
        n.n_parent = this;
        
        if(n_chd_head == null){
            n_chd_head = n;
            return;
        }
        
        Node node = n_chd_head;
        short zorder = n.n_zorder;
                
        while(true){
            short tmp = node.n_zorder;
            if(zorder < tmp){
                Node prev = node.n_prev;
                if(prev != null){
                    n.n_prev = prev;
                    prev.n_next = n;
                }else{
                    n = n_chd_head;
                }
                n.n_next = node;
                node.n_prev = n;
                return;
            }
            if(node.n_next == null)break;
            node = node.n_next;
        }
        node.n_next = n;
        n.n_prev = node;
    }

    public Node getChildByTag(short tag) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Node node = n_chd_head;
        while(node != null){
            if(node.n_tag == tag){
                return node;
            }
        }
        return null;
    }
    
    public void setTag(short tag){
        this.n_tag = tag;
    }
    public short getTag(){
        return n_tag;
    }

    protected void onEnter() {
        
    }

    protected void onExit() {
        
    }
    
    protected void onCleanup(){
        n_parent = null;
        n_chd_head = null;
        n_prev = null;
        n_next = null;
        n_rect = null;
        n_clip_rect = null;
    }

    public void romoveNow(){
        n_bExited = true;
        n_bRemoved = true;
        n_KeyEvent = null;
        Node node = n_chd_head;
        while(node != null){
            if(!node.n_bRemoved){
                node.romoveNow();
            }
            node = node.n_next;
        }
        if(n_parent != null){
            if(this.n_prev != null){
                this.n_prev.n_next = this.n_next;
            }else{
                n_parent.n_chd_head = this.n_next;
            }
            if(this.n_next != null){
                this.n_next.n_prev = this.n_prev;
            }
            this.n_prev = null;
            this.n_next = null;
        }
        onExit();
        onCleanup();
    }
    
    public void removeSelf() {
        n_bRemoved = true;
    }

    public void removeChild(short tag) {
        Node node = n_chd_head;
        while(node != null){
            if(node.n_tag == tag){
                n_childNums--;
                node.removeSelf();
            }
            return;
        }
    }

    public Rect getRect() {
        return n_rect;
    }
    public void setRect(Rect rect){
        n_rect = rect;
    }
    
    public Rect getBoundBox(){
        return new Rect(this.x - n_rect.width/2,this.y - n_rect.height/2,n_rect.width,n_rect.height);
    }
    
    public void setCliped(Rect rect){
        n_clip_rect = rect;
        setCliped(rect != null);
    }
    private void setCliped(boolean cliped){
        n_b_clip = (n_clip_rect != null || cliped);
        Node node = n_chd_head;
        while(node != null){
            node.setCliped(cliped);
            node = node.n_next;
        }
    }
    protected void setGraphicsCip(Graphics g,int x,int y,int width,int height){
        if(n_b_can_cip_self){
            g.setClip(x,y,width,height);
        }
    }
    
    public Node getParent() {
        return n_parent;
    }

    public void setZorder(int z) {
        n_zorder = (short)z;
        if(n_parent != null){
            if(this.n_prev != null){
                this.n_prev.n_next = this.n_next;
            }else{
                n_parent.n_chd_head = this.n_next;
            }
            if(this.n_next != null){
                this.n_next.n_prev = this.n_prev;
            }
            this.n_next = null;
            this.n_prev = null;
            n_parent.addNode(this);
        }
    }
    
    private void updatePos(short x,short y){
        this.x += x;
        this.y += y;
        if(n_chd_head != null){
            Node node = n_chd_head;
            while(node != null){
                node.updatePos(x, y);
                node = node.n_next;
            }
        }
    }
    private void updatePos(int x,int y){
        this.x += (short)x;
        this.y += (short)y;
        if(n_chd_head != null){
            Node node = n_chd_head;
            while(node != null){
                node.updatePos(x, y);
                node = node.n_next;
            }
        }
    }
    
    public void setPos(short x,short y){
        short dx = (short)(x - this.x);
        short dy = (short)(y - this.y);
        updatePos(dx,dy);
    }
    public void setPos(int x,int y){
        int dx = x - this.x;
        int dy = y - this.y;
        updatePos(dx,dy);
    }
    public short getPosX(){
        return this.x;
    }
    public short getPosY(){
        return this.y;
    }
    public void setLocalPos(short x,short y){
        if(n_parent != null){
            x += n_parent.getPosX();
            y += n_parent.getPosY();
        }
        setPos(x,y);
    }
    public void setLocalPos(int x,int y){
        if(n_parent != null){
            x += n_parent.getPosX();
            y += n_parent.getPosY();
        }
        setPos(x,y);
    }
    public short getLocalPosX(){
        short x = this.x;
        if(n_parent != null){
            x -= n_parent.getPosX();
        }
        return x;
    }
    public short getLocalPosY(){
        short y = this.y;
        if(n_parent != null){
            y -= n_parent.getPosY();
        }
        return y;
    }

    public int getColor(){
        return n_Color;
    }
    public void setColor(int color){
        n_Color = color;
    }
    
    public boolean getEnableUpdate(){
        return n_bEnableUpdate;
    }
    public void setEnableUpdate(boolean enable){
        n_bEnableUpdate = enable;
    }
    
    protected void animUpdate(long dt){
        if(n_Animations != null){
            boolean flg = true;
            for(byte i=0;i<n_ani_nums;i++){
                if(n_Animations[i] != null){
                    n_Animations[i].update(dt);
                    if(n_Animations[i].isCompleted()){
                        n_Animations[i] = null;
                    }else{
                        flg = false;
                    }
                }
            }
            if(flg){
                n_Animations = null;
                n_ani_nums = 0;
            }
        }
    }
    
    Animation n_Animations[] = null;
    byte n_ani_nums = 0;
    
    public void update (long dt){
        
    }
    
    public void runAction(Animation ani){
        ani.setNode(this);
        if(n_Animations == null){
            n_Animations = new Animation[3];
            n_ani_nums = 0;
        }else{
            if(n_ani_nums == n_Animations.length){
                Animation tmp[] = n_Animations;
                n_Animations = new Animation[n_ani_nums+3];
                for(byte i=0;i<n_ani_nums;i++){
                    n_Animations[i] = tmp[i];
                }
            }
        }
        n_Animations[n_ani_nums++] = ani;
    }
    
    public void setVisible(boolean visible){
        n_bShowed = visible;
    }
    
    public boolean getVisible(){
        return n_bShowed;
    }
    
    protected IKeyEvent n_KeyEvent;
    
    public void setKeyEventListener(IKeyEvent listener){
        this.n_KeyEvent = listener;
    }
    
    public void setAlignModel(int m){
        this.n_align_model = m;
    }
    public void setAlignModel(int h,int v){
        this.n_align_model = h | v;
    }

    public Node getChildHead(){
        return this.n_chd_head;
    }
    public Node[] getChilds(){
        int len = 0;
        Node node = this.n_chd_head;
        while(node != null){
            len++;
            node = node.n_next;
        }
        Node[] chds = new Node[len];
        len = 0;
        node = this.n_chd_head;
        while(node != null){
            chds[len++] = node;
            node = node.n_next;
        }
        return chds;
    }
}
