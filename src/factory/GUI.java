package factory;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch; 

public class GUI {
	
	public class CellRenderer extends JLabel implements TableCellRenderer {
		public CellRenderer() {
			super.setOpaque(true);
		}
		
		public Component getTableCellRendererComponent(JTable tab, Object val, boolean hasSelected, boolean hasValue, int row, int col ) {
			Station station = (Station)val;
			if(station == null) {
				super.setBackground(Color.BLACK);
			}
			else if(station.getFlavor() == 1) {
				super.setBackground(Color.BLUE);
			}
			else if(station.getFlavor() == 2) {
				super.setBackground(Color.RED);
			}
			return this;
		}
						
	}

	Station [][] factory;
    static JFrame frame = new JFrame("Factory");
    static JTable table;
    static boolean tableIsDisplayed = false;
    ConcurrentLinkedDeque<Station [][]> floors = new ConcurrentLinkedDeque<>();
    JScrollPane scroll = new JScrollPane();
	
	public void addComponentsToPane(Container pane) {
		

		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		/*JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Main.startGenAlg();
				}
				catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		});
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.add(startButton); */
	} 
	
	public void createAndShowGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setPreferredSize(new Dimension(600, 600));
		
		addComponentsToPane(frame.getContentPane());
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void updateGUI(Station [][] newFac) {
		//floors.add(newFac);
		//factory = null;
		factory = newFac;
		frame.remove(scroll);
		display_table();
	}
	
	public void display_table() {
		AbstractTableModel dataModel = new AbstractTableModel() {
			
            public int getColumnCount() {
                return 6;
            }

            public int getRowCount() {
                return 6;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex){
            	return Station.class;
            }

            public Object getValueAt(int row, int col) {
            	if(factory == null)return null;
            	if(factory[row][col] == null)
            		return null;
            	return factory[row][col];
            }
        };
		
		CellRenderer r = new CellRenderer();
		
		table = new JTable(dataModel);
		table.setRowHeight(50);
		table.setDefaultRenderer(Station.class, r);
		
		scroll.setViewportView(table);
		frame.add(scroll);
		frame.setVisible(true);
	}
	
}
