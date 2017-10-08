package com.quotegraph;

import com.quotegraph.controller.DbConfig;
import com.quotegraph.controller.DropDownAction;
import com.quotegraph.controller.FileLoader;
import com.quotegraph.controller.SqlLoader;
import com.quotegraph.model.DataLoader;
import com.quotegraph.model.ISqlConnection;
import com.quotegraph.view.DataSelect;
import com.quotegraph.view.LineGraph;
import com.quotegraph.view.UserInterface;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/**
 * This Program displays data from various sources as graphs.
 * Right now, there are line graphs, column graphs and candlestick graphs available.
 * The most complete and functional one ist the line graph.
 * TODO:
 * Improve column and candlestick graphs.
 * Let the user change the graph types via menu controls.
 *
 * @author d.peters
 * @version 13.01.2017
 */
public class Main {

  private final String defaultSource = "data/vw";

  /**
   * JPanel for drawing graphs. There are currently 3 types.
   */
  private JPanel graph;

  /**
   * Sql Database configuration (hots, user, password, db, port).
   */
  private DbConfig config;

  /**
   * Any type of sql connection.
   */
  private ISqlConnection conn;

  /**
   * Loads data from a source. There are two types of DataLoaders. One loads
   * data from an MySQL DB. Another loads the data from a file.
   */
  private DataLoader loader;

  /**
   * Action listener, that listens to dropdown menu change.
   */
  private DropDownAction action;

  /**
   * Dropown from which the user can select different data.
   */
  private DataSelect dropDown;

  /**
   * The JFrame window containing all other components.
   */
  private UserInterface ui;

  /**
   * Default constructor. Initializes all attributes and loads up the UI.
   */
  public Main() {
    initData();

    if (!this.loader.hasFailed()) {
      String[] sources = {"vw", "blackrock", "goldman", "cac40"};
      this.graph = new LineGraph(loader);
      // this.graph = new CandleStickGraph(loader);
      // this.graph = new ColumnGraph(loader);
      this.action = new DropDownAction(loader, graph);
      this.dropDown = new DataSelect(action, sources);
      this.ui = new UserInterface(dropDown);
      this.ui.add(graph, BorderLayout.CENTER);
      this.ui.pack();
      this.ui.setVisible(true);
    } else {
      dataErrorDialog();
    }
  }

  /**
   * Try to initialize the DataLoader object, first try MySQL, then files.
   */
  private void initData() {
    this.loader = new SqlLoader(
        "localhost", "postgres", "dp", "boersendaten", this.defaultSource
    );

    if (this.loader.hasFailed()) {
      System.out.println("Trying to use files as fallback.");

      this.loader = new FileLoader(this.defaultSource);
      if (this.loader.hasFailed()) {
        System.out.println("No data could be loaded at the Moment. Please try again later.");
      } else {
        System.out.println("Using files as data source");
      }
    } else {
      System.out.println("Using MySql as data source.");
    }
  }

  /**
   * Displays a dialog with error message, when no data could be loaded.
   */
  private void dataErrorDialog() {
    JFrame frame = new JFrame();
    int result;

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    result = JOptionPane.showConfirmDialog(
        frame,
        "Failed To Load any Data..",
        "Error",
        JOptionPane.DEFAULT_OPTION
    );

    if (result == JOptionPane.YES_OPTION) {
      frame.dispose();
    }
  }

  /**
   * Change look and feel to a more modern look.
   */
  public void modifyLookAndFeel() {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e) {
      System.out.println("Failed to set look and feel.");
    }
    SwingUtilities.updateComponentTreeUI(this.ui);
  }

  /**
   * Main method.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Main()::modifyLookAndFeel);
  }
}
