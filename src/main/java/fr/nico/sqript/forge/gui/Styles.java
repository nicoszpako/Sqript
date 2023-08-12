package fr.nico.sqript.forge.gui;

public final class Styles {

	public static Style basic(int width, int height){
		
		Style s = new Style();
		s.setBackgroundcolor(0xFF888888);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		s.setHoverColor(0xFF555555);
		return s;
		
	}
	
	public static Style modern(int width, int height){
		
		Style s = new Style();
		s.setBackgroundcolor(0xFF111111);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		s.setHoverColor(0xFF555555);
		return s;
		
	}
	
	
	public static Style darknet_button(int width, int height){
		
		Style s = new Style();
		s.setBackgroundcolor(0xFF274E0E);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		s.setHoverColor(0xFF4E9B1B);
		return s;
		
	}
	
	public static Style container_topbar(int width, int height){
		
		Style s = new Style();
		s.setBackgroundcolor(0x88888888);		
		s.setWidth(width);
		s.setHeight(height);
		return s;
		
	}

	public static Style button_delete(int width, int height) {
		Style s= new Style();
		s.setBackgroundcolor(0xFFf28787);
		s.setHoverColor(0xFFf2a9a9);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		return s;
	}
	
	public static Style button_validate(int width, int height) {
		Style s= new Style();
		s.setBackgroundcolor(0xFF3399CC);
		s.setHoverColor(0xFF336699);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		return s;
	}
	
	public static Style button_disabled(int width, int height) {
		Style s= new Style();
		s.setBackgroundcolor(0xFF999999);
		s.setHoverColor(0xFF999999);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		return s;
	}
	
	public static Style button_green(int width, int height) {
		Style s= new Style();
		s.setBackgroundcolor(0xFF4cc445);
		s.setHoverColor(0xFF5ae851);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		return s;
	}
	
	public static Style button_blue(int width, int height) {
		Style s= new Style();
		s.setBackgroundcolor(0xFF4482ff);
		s.setHoverColor(0xFF628adb);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		return s;
	}

	public static Style button_gold(int width, int height) {
		Style s= new Style();
		s.setBackgroundcolor(0xFFFFCC00);
		s.setHoverColor(0xFFCC9900);
		s.setBordersize(1);
		s.setBordercolor(0xFF000000);
		s.setWidth(width);
		s.setHeight(height);
		return s;
	}

	public static Style null_style(int i, int j) {
		Style s= new Style();
		s.setBackgroundcolor(0x00);
		s.setHoverColor(0x00);
		s.setBordersize(0);
		s.setBordercolor(0x00);
		s.setWidth(i);
		s.setHeight(j);
		return s;
	}
	
}
