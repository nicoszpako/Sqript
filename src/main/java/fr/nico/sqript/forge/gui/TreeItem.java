package fr.nico.sqript.forge.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

public class TreeItem<T> {
	public String label;
	public T item;
	public List<TreeItem<T>> nodes = new ArrayList<TreeItem<T>>();
	
	public void addNode(TreeItem<T> node) {
		nodes.add(node);
	}
	
	public boolean hasNode() {
		return !nodes.isEmpty();
	}
		
	public boolean hasItem() {
		return item!=null;
	}
	
	public TreeItem(String label,T item) {
		this.item=item;
		this.label=label;
	}

	public TreeItem(String label) {
		this.label=label;
	}
	
	
	
}
