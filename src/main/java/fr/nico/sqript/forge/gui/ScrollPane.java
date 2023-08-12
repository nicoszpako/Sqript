package fr.nico.sqript.forge.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ScrollPane extends Container {

	private boolean wheelControlEnabled = true;
	private int mouseX,mouseY;
	public double baseH = 0;
	public double temp = 0;
	public double prevY = 0;
	public int scrollValue = 0;
	public double buffY = 0;
	public boolean reversed=false;
	boolean hold = false;
	private boolean isStopped = false;
	private int scrollStyle=0;
	public static final int SCROLL_BASE=0,SCROLL_THIN=1,SCROLL_THIN_BASE=2,HIDDEN=3;
	public ScrollPane(int x, int y, int width, int height) {
		super(x, y, width, height);
		super.setLayout(EnumLayout.BLOCK);
	}

	public ScrollPane(int width, int height) {
		super(width, height);
		super.setLayout(EnumLayout.BLOCK);

	}

	public ScrollPane(Style style) {
		super(style.width,style.height);
		super.setLayout(EnumLayout.BLOCK);
		this.style=style;
	}

	public int getContentHeight() {
		int result = 0;

		if (this.layout == EnumLayout.BLOCK){
			int lastTop=0;
			int i = 0;
			for (;i<this.content.size();i++) {
					Widget w = content.get(i);
					result += w.style.bordersize * 2 + w.style.height + w.style.margin_top + w.style.margin_bottom;
					lastTop=w.style.bordersize * 2 + w.style.height + w.style.margin_top + w.style.margin_bottom;

			}
			result+=15;
		}
		if (this.layout == EnumLayout.FLOW) {
			int mleft = 0, mtop = 0;
			int bestHeight = 0;
			int lastTop=0;
			for (int i = 0; i < content.size(); i++) {
				Widget w = content.get(i);
				mleft += w.style.getWidth() + w.style.margin_left + w.style.bordersize;
				if (w.style.getHeight() + w.style.margin_top + 2 * w.style.bordersize > bestHeight) {
					bestHeight = w.style.getHeight() + w.style.margin_top + w.style.bordersize;
				}
				if (mleft + w.style.getWidth() + w.style.margin_left + 2 * w.style.bordersize >= this.style.width - 20) {
					mtop += bestHeight;
					bestHeight = 0;
					mleft = 0;
				}
				lastTop=w.style.margin_top;
			}
			mtop+=bestHeight+lastTop;
			result = mtop;
		}
		return result;
	}

	@Override
	public void draw(int mouseX, int mouseY) {


		drawBorders(style.bordercolor);

		if (this.getParent() instanceof ScrollPane) {
			GL11.glScissor(getParent().x * scaledResolution.getScaleFactor(),
					mc.displayHeight - (getParent().y + getParent().style.height) * scaledResolution.getScaleFactor(),
					getParent().style.width * scaledResolution.getScaleFactor(),
					getParent().style.height * scaledResolution.getScaleFactor());

		} else if (this.getParent() != null && takeParent) {
			GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(),
					mc.displayHeight - (getHighestParent().y + getHighestParent().style.height)
							* scaledResolution.getScaleFactor(),
					getHighestParent().style.width * scaledResolution.getScaleFactor(),
					getHighestParent().style.height * scaledResolution.getScaleFactor());

		} else {
			GL11.glScissor((x - style.bordersize) * scaledResolution.getScaleFactor(),
					mc.displayHeight - (y + style.height + style.bordersize) * scaledResolution.getScaleFactor(),
					(2 * style.bordersize + style.width) * scaledResolution.getScaleFactor(),
					(style.height + 2 * style.bordersize) * scaledResolution.getScaleFactor());

		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		

		
		this.mouseX=mouseX;
		this.mouseY=mouseY;
		{
			
			GL11.glPushMatrix();

			if (style.isDrawBackground()){
				drawRect(x, y, x + style.width, y + style.height, style.getBackgroundcolor());
			}
			boolean isScroll = getContentHeight() > this.style.height;
			int addH = 0;
			int mleft = 0, mtop = 0;
			int bestHeight = 0;
			int contentHeight = getContentHeight();
			if (contentHeight > this.style.height) {

				double sliderWidth =   (double)this.getContentHeight()/this.style.height;
				sliderWidth=this.style.height*(1/sliderWidth);
				if(scrollStyle==0){
					drawRect(getX2() - 10, getY2(), getX2(), getY(), 0xFF444444);

				}else if(scrollStyle==1){
					drawRect(getX2() - 7, getY2()-3, getX2()-3, getY()+3, 0xFF444444);

				}else if(scrollStyle==2){
					drawRect(getX2() - 10, getY2(), getX2(), getY(), 0xFF444444);

				}
					
				if (Mouse.isButtonDown(0)) {

					this.temp = 0;
					if ((mouseX >= getX2() - 10 && mouseX <= getX2() && mouseY <= getY() + sliderWidth + this.buffY
							&& mouseY >= getY() + this.buffY) || (hold == true)) {

						if (hold == false) {

							this.hold = true;
						} else if (this.prevY != mouseY) {
							// System.out.println(mouseY);
							if (this.prevY == 0)
								this.prevY = mouseY;
							this.temp = mouseY - this.prevY;
							this.prevY = mouseY;
							if (temp < 0) {
								this.buffY += temp;
							} else {
								this.buffY += temp;
							}

						}
					}

				} else {
					this.prevY = 0;
					this.hold = false;
					isStopped = false;

				}

				this.temp = 0;
				if (this.buffY < 0) {

					this.buffY = 0;

				}
				if (this.buffY > this.style.height - (int)sliderWidth)
					this.buffY = this.style.height - (int)sliderWidth;

				
				
				this.scrollValue = (int) (this.buffY * 100 / (this.style.height - (sliderWidth)));
				if ((mouseX >= getX2() - 10 && mouseX <= getX2() && mouseY <= getY() + sliderWidth + this.buffY
						&& mouseY >= getY() + this.buffY) || (hold == true)) {

					if(scrollStyle==0){
						drawRect(getX2() - 10, getY() +(int) this.buffY, getX2(), getY() + (int)sliderWidth + (int)this.buffY,
								0xFF222222);
					}else if(scrollStyle==1){
						drawRect(getX2() - 9, getY() +(int) this.buffY+1, getX2()-1, getY() + (int)sliderWidth +(int) this.buffY-1,
								0xFF222222);
					}else if(scrollStyle==2){
						drawRect(getX2() - 10, getY() +(int) this.buffY, getX2(), getY() + (int)sliderWidth + (int)this.buffY,
								0xFF222222);
					}
					
				

				} else {
					if(scrollStyle==0){
						drawRect(getX2() - 10, getY() +(int) this.buffY, getX2(), getY() + (int)sliderWidth +(int) this.buffY,
								0xFF000000);

					}else if(scrollStyle==1){
						drawRect(getX2() - 9, getY() +(int) this.buffY+1, getX2()-1, getY() + (int)sliderWidth +(int) this.buffY-1,
								0xFF000000);

					}else if(scrollStyle==2){
						drawRect(getX2() - 10, getY() +(int) this.buffY, getX2(), getY() + (int)sliderWidth +(int) this.buffY,
								0xFF000000);

					}
					
				
				}
			}else if(scrollStyle!=3){
				drawRect(getX2() - 10, getY2(), getX2(), getY(), 0xFF444444);

			}
			GL11.glPopMatrix();

			for (int i = 0; i < content.size(); i++) {
				Widget w = content.get(i);
				w.setX(this.x + w.style.margin_left);
				w.setY(this.y + w.style.margin_top);
			
				double removeY = ((double) this.scrollValue* (contentHeight-style.height)/100);
				if (layout == EnumLayout.FLOW) {

					if(!reversed) {
						w.setX(this.x + mleft + w.style.margin_left + w.style.bordersize+this.style.padding_left);
						w.setY((int) (this.y + mtop + w.style.margin_top + w.style.bordersize +this.style.padding_top+ (isScroll ? - removeY : 0)));
						mleft += w.style.getWidth() + w.style.margin_left + w.style.bordersize;
						if (w.style.getHeight() + w.style.margin_top + 2 * w.style.bordersize > bestHeight) {
							bestHeight = w.style.getHeight() + w.style.margin_top + w.style.bordersize;
						}
						
						if (mleft + w.style.getWidth() + w.style.margin_left + 2 * w.style.bordersize >= this.style.width - 10) {
							mtop += bestHeight;
							bestHeight = 0;
							mleft = 0;
						}

					}else {
						w.setX(this.x + mleft + w.style.margin_left + w.style.bordersize+this.style.padding_left);
						w.setY((int) ((this.y+this.style.height) - (w.style.height+mtop + w.style.margin_top + w.style.bordersize +this.style.padding_top+ (isScroll ? - removeY : 0))));
						mleft += w.style.getWidth() + w.style.margin_left + w.style.bordersize;
						if (w.style.getHeight() + w.style.margin_top + 2 * w.style.bordersize > bestHeight) {
							bestHeight = w.style.getHeight() + w.style.margin_top + w.style.bordersize;
						}
						
						if (mleft + w.style.getWidth() + w.style.margin_left + 2 * w.style.bordersize >= this.style.width - 10) {
							mtop += bestHeight;
							bestHeight = 0;
							mleft = 0;
						}
					}
				
					w.draw(mouseX, mouseY);

				} else if (layout == EnumLayout.NONE) {
					w.setY((int) (w.getY() + (isScroll ? - removeY : 0)));
					w.draw(mouseX, mouseY);

				} else if (layout == EnumLayout.BLOCK) {
					if(!reversed) {

					w.x = this.x + w.style.bordersize + w.style.margin_left+this.style.padding_left;
					w.y = (int) (this.y + w.style.bordersize +this.style.padding_top+w.style.margin_top + mtop + (isScroll ? - removeY : 0));
					mtop += w.style.bordersize + w.style.margin_top + w.style.height+w.style.margin_bottom;
					w.draw(mouseX, mouseY);
					}else {
						w.x = this.x + w.style.bordersize + w.style.margin_left+this.style.padding_left;
						w.y = (int) ((this.y+this.style.height) -( w.style.height+w.style.bordersize +this.style.padding_top+w.style.margin_top + mtop + (isScroll ? - removeY : 0)));
						mtop += w.style.bordersize + w.style.margin_top + w.style.height+w.style.margin_bottom;
						w.draw(mouseX, mouseY);
					}
				}
			}

			GL11.glDisable(GL11.GL_SCISSOR_TEST);

		}

	}
	
	
	
	@Override
	public void mouseWheelInput(int i) {
		super.mouseWheelInput(i);
		if(isMouseOnWidget(mouseX, mouseY) && wheelControlEnabled){
			if(i<0){
				buffY+=this.getContentHeight()/this.style.height*2.8;
			}if(i>0){
				buffY-=this.getContentHeight()/this.style.height*2.8;
			}
		}
	}

	public int getScrollStyle() {
		return scrollStyle;
	}

	public void setScrollStyle(int scrollStyle) {
		this.scrollStyle = scrollStyle;
	}

	public boolean isWheelControlEnabled() {
		return wheelControlEnabled;
	}

	public void setWheelControlEnabled(boolean wheelControlEnabled) {
		this.wheelControlEnabled = wheelControlEnabled;
	}

	
}
