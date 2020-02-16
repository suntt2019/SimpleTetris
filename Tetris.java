import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Tetris{
	Tetris(){
		JFrame jfrm = new JFrame("Tetris demo");
		jfrm.setLayout(new BorderLayout());
		jfrm.setSize(600,880);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jfrm.setLayout(new BorderLayout());

		JPanel headPanel = new JPanel();
		headPanel.setLayout(new FlowLayout());
		jfrm.add(headPanel,BorderLayout.NORTH);

		JLabel titleLabel = new JLabel("SimpleTetris!");
		headPanel.add(titleLabel);


		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		jfrm.add(mainPanel);

		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(20,8));
		mainPanel.add(gamePanel);
		gamePanel.setPreferredSize(new Dimension(320,800));

		JButton cubes[] = new JButton[160];
		for(int i=0;i<160;i++){
			cubes[i] = new JButton(Integer.toString(i));
			//cubes[i].setPreferredSize(new Dimension(50,50));
			gamePanel.add(cubes[i]);
		}

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(2,1));
		mainPanel.add(statusPanel);
		statusPanel.setPreferredSize(new Dimension(160,400));

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new FlowLayout());
		statusPanel.add(textPanel);

		JLabel scoreLabel = new JLabel("score:666");
		textPanel.add(scoreLabel);

		jfrm.setVisible(true);
	}

	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new Tetris();
			}
		});
	}
}