package fr.nico.sqript.forge.gui;

public class DisplayInfos {
	
	public double x,y,scale;
	public double width,height;
	public double u,v;
	
	public DisplayInfos(double x,double y, double width,double height,double u,double v ) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.u=u;
		this.v=v;
	}
	
	public DisplayInfos(double x,double y,double scale, double width,double height ) {
		this.x=x;
		this.y=y;
		this.scale=scale;
		this.width=width;
		this.height=height;
	}
}
