package com.github.pierods.gitmarks.widgets;

import com.github.pierods.gitmarks.Gitmarks;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.MenuToolButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleToolButton;
import org.gnome.gtk.ToolButton;
import org.gnome.gtk.ToolButton.Clicked;
import org.gnome.gtk.ToolItem;

/**
 * Created by piero on 3/23/17.
 */
public class GitmarksToolbar {

  public org.gnome.gtk.Toolbar makeToolbar(final Gitmarks.Dispatcher dispatcher) {
    final org.gnome.gtk.Toolbar toolbar;
    final ToolButton buttonNew, buttonOpen;
    final MenuToolButton mtb;
    final Menu openMenu;
    final ToolItem item;
    final ToggleToolButton boldButton, italicButton;

    toolbar = new org.gnome.gtk.Toolbar();

    buttonNew = new ToolButton(Stock.NEW);
    toolbar.add(buttonNew);

    buttonNew.connect(new ToolButton.Clicked() {
      public void onClicked(ToolButton source) {

        dispatcher.dispatch("toolbar-new", "You have clicked NEW ToolButton");
      }
    });

    buttonOpen = new ToolButton(Stock.OPEN);
    toolbar.add(buttonOpen);
    buttonOpen.connect(new ToolButton.Clicked() {
      @Override
      public void onClicked(ToolButton toolButton) {
        dispatcher.dispatch("toolbar", "file-open");
      }
    });
         /*
         * Sometimes you need a ToolButton that also has a dropdown Menu, to
         * allow the user select alternative actions. You can do that with a
         * MenuToolButton.
         */
    mtb = new MenuToolButton(Stock.OPEN);
    toolbar.add(mtb);

        /*
         * You can add your Menu to this kind of ToolButtons
         */
    openMenu = new Menu();
    openMenu.append(new MenuItem("File _A", new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("toolbar-openmenu", "You have selected File A in the Menu");
      }
    }));
    openMenu.append(new MenuItem("File _B", new MenuItem.Activate() {
      public void onActivate(MenuItem source) {
        dispatcher.dispatch("toolbar-openmenu", "You have selected File B in the Menu");
      }
    }));
    openMenu.showAll();
    mtb.setMenu(openMenu);
    return toolbar;
  }
}
