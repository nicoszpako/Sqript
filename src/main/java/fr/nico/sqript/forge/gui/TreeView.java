package fr.nico.sqript.forge.gui;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class TreeView<T> extends Container {

    List<TreeItem<T>> items = new ArrayList<TreeItem<T>>();
    ScrollPane s;


    public TreeView(List<TreeItem<T>> items, Style style) {
        super(style);
        this.items = items;
        this.style = style;
        this.setLayout(EnumLayout.BLOCK);
        s = new ScrollPane(Styles.basic(this.style.width, this.style.height));
        for (TreeItem<T> i : items) {
            TreeSelector drop = new TreeSelector(Styles.basic(this.style.width - 12, 15), i.label, (Item) i.item);
            drop.style.setBackgroundcolor(0xFF333333);
            for (TreeItem<T> item : i.nodes) {
                TreeSelector drop2 = new TreeSelector(Styles.basic(this.style.width - 10, 10), item.label, (Item) item.item);
                drop.choices.add(drop2);
            }
            drop.selected = drop.choices.get(0);
            s.addWidget(drop);
            s.addWidget(new Return(2));

        }

        this.addWidget(s);
    }


}
