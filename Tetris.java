import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



interface blockInfo{
	public final int[][][][] blockShape={
		{ {{0,0},{1,0},{0,1},{1,1}},{{0,0},{1,0},{0,1},{1,1}},{{0,0},{1,0},{0,1},{1,1}},{{0,0},{1,0},{0,1},{1,1}} },
		{ {{0,0},{0,1},{0,2},{0,3}},{{-1,1},{0,1},{1,1},{2,1}},{{0,0},{0,1},{0,2},{0,3}},{{-2,1},{-1,1},{0,1},{1,1}} },
		{ {{0,0},{0,1},{1,1},{1,2}},{{0,1},{0,2},{-1,2},{1,1}},{{0,0},{0,1},{1,1},{1,2}},{{0,1},{0,2},{-1,2},{1,1}} },
		{ {{-1,1},{0,1},{0,2},{1,2}},{{0,0},{0,1},{-1,1},{-1,2}},{{-1,1},{0,1},{0,2},{1,2}},{{0,0},{0,1},{-1,1},{-1,2}} },
	};//TODO:把不顶头的设置成初始顶头配置（-1）
}

class Tetris{
	JButton cubes[];
	GameOperator go;
	final static Color COLORS[]={new Color(255,255,255),new Color(255,100,100),new Color(100,255,100),new Color(100,100,255),new Color(255,255,50),new Color(255,50,255),new Color(50,255,255)};

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
		gamePanel.setLayout(new GridLayout(20,9));
		mainPanel.add(gamePanel);
		gamePanel.setPreferredSize(new Dimension(360,800));

		cubes = new JButton[180];
		for(int i=0;i<180;i++){
			cubes[i] = new JButton();
			//cubes[i].setPreferredSize(new Dimension(50,50));
			cubes[i].setEnabled(false);
			cubes[i].setBackground(new Color(255,255,255));
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



		jfrm.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){
				switch(e.getKeyChar()){
					case 'a': case 'A':
						go.goingLeft();
						break;
					case 'd': case 'D':
						go.goingRight();
						break;
					case 's': case 'S':
						go.goingDown();
						break;
					default:
						System.out.println(e.getKeyChar());
				}
			}
			public void keyPressed(KeyEvent e){
				// System.out.println(e);
			}
			public void keyReleased(KeyEvent e){
				
			}
		});


		jfrm.setVisible(true);
	}

	void printSheet(int[][] sheet){
		int k=0; 
		
		for(int j=0;j<20;j++){
			for(int i=0;i<9;i++){
				if(sheet[i][j]>=0)
					cubes[k].setBackground(COLORS[sheet[i][j]]);
				else
					cubes[k].setBackground(COLORS[go.thisBlockColor]);
				//System.out.println("cubes["+k+"].setBackground(COLORS[sheet["+i+"]["+j+"]]);");
				//System.out.println(sheet[i][j]);
				k++;
			}
		}
			
		return;
				
	}

	public static void main(String args[]){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Tetris t = new Tetris();
				t.init();
			}
		});
	}

	void init(){
		go = new GameOperator();
		go.init(this);
		printSheet(go.getSheet());
	}
}

class GameOperator{
	Tetris t;
	int sheet[][];
	int thisBlockStatus[];
	int nextBlockStatus[];
	public int thisBlockColor;



	static final int EMPTY=0;
	static final int COLOR1=1;

	GameOperator(){
		sheet = new int[9][20];
		thisBlockStatus = new int[5];//0->x,1->y,2->type,3->posture,4->color
		nextBlockStatus = new int[5];//0->x,1->y,2->type,3->posture,4->color
	}

	int[][] getSheet(){
		return sheet;
	}


	void init(Tetris tt){
		t=tt;

		for(int[] sheetLine : sheet)
			for(int sheetContent : sheetLine)
				sheetContent=EMPTY;
		

		thisBlockStatus[4]=-1;
		nextBlockStatus[2]=(int)(Math.random()*4);//type-random
		nextBlockStatus[3]=(int)(Math.random()*4);//posture-random
		nextBlockStatus[4]=(int)(Math.random()*6)+1;//color-random
		generateNewBlock();
	}

	void generateNewBlock(){
		//TODO:generate random number
		int colorId=COLOR1;//random
		int x,y;
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			sheet[x][y]=thisBlockColor;
		}
		thisBlockStatus[1]=0;//y
		thisBlockStatus[2]=nextBlockStatus[2];//type
		thisBlockStatus[3]=nextBlockStatus[3];//posture
		thisBlockColor=nextBlockStatus[4];//color

		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			//System.out.println("x="+x);
			while(x<0){
				thisBlockStatus[0]++;
				x++;
			}
			while(x>8){
				thisBlockStatus[0]--;
				x--;
			}
		}

		nextBlockStatus[2]=(int)(Math.random()*4);//type-random
		nextBlockStatus[3]=(int)(Math.random()*4);//posture-random
		nextBlockStatus[4]=(int)(Math.random()*6)+1;//color-random

		//System.out.println("nextBlockStatus[2,3,4]:"+nextBlockStatus[2]+"~"+nextBlockStatus[3]+"~"+nextBlockStatus[4]);

		gameStatusUpdate();
	}

	void gameStatusUpdate(){
		//recompute block on the sheet
		int x,y;
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			//System.out.println("sheet["+x+"]["+y+"]");
			sheet[x][y]=-1;
		}

		//judge game status

		t.printSheet(sheet);

		return;
	}

	synchronized void goingLeft(){//TODO:synchronized
		int x,y;
		boolean possible=true;
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			if(x-1<0||sheet[x-1][y]>0){
				possible=false;
				break;
			}
		}
		if(possible){
			for(int i=0;i<4;i++){
				x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
				y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
				sheet[x][y]=0;
			}
			thisBlockStatus[0]--;
			gameStatusUpdate();
		}

		return;
	}

	synchronized void goingRight(){//TODO:synchronized
		int x,y;
		boolean possible=true;
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			if(x+1>8||sheet[x+1][y]>0){
				possible=false;
				break;
			}
		}
		if(possible){
			for(int i=0;i<4;i++){
				x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
				y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
				sheet[x][y]=0;
			}
			thisBlockStatus[0]++;
			gameStatusUpdate();
		}

		return;
	}

	synchronized void goingDown(){//TODO:synchronized
		int x,y;
		boolean possible=true;
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			if(y+1>19||sheet[x][y+1]>0){
				possible=false;
				break;
			}
		}
		if(possible){
			for(int i=0;i<4;i++){
				x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
				y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
				sheet[x][y]=0;
			}
			thisBlockStatus[1]++;
			goingDown();
		}else{
			gameStatusUpdate();
			generateNewBlock();
		}

		return;
	}

}