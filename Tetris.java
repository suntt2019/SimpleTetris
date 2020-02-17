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
	int score=0;
	JButton cubes[];
	JLabel scoreLabel;
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

		scoreLabel = new JLabel("score:"+score);
		textPanel.add(scoreLabel);



		jfrm.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){
				if(!go.gameEnded){
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
						case 'w': case 'W':
							go.changingPosture();
							break;
						default:
							System.out.println(e.getKeyChar());
					}
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

	public void upadteGameData(){
		scoreLabel.setText("score:"+score);
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
	boolean gameEnded=false;
	long aftId=0;
	Thread afThread;


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
		
		thisBlockStatus[0]=4;
		thisBlockStatus[4]=-1;
		nextBlockStatus[2]=(int)(Math.random()*4);//type-random
		nextBlockStatus[3]=(int)(Math.random()*4);//posture-random
		nextBlockStatus[4]=(int)(Math.random()*6)+1;//color-random
		generateNewBlock();

		startAutoFallingTread();
	}

	void startAutoFallingTread(){
		afThread = new Thread(new Runnable(){
			public void run(){
				long thisaftId=aftId;
				while(!gameEnded&&thisaftId==aftId){
					falling();
					try{
						Thread.sleep(1000);
					}
					catch(InterruptedException exc){
						System.out.println("InterruptedException");
					}
					
				}
			}
		});
		aftId=afThread.getId();
		new Thread(new Runnable(){
			public void run(){
				try{
					Thread.sleep(1000);
					afThread.start();
				}
				catch(InterruptedException exc){
					System.out.println("InterruptedException");
				}
				catch(IllegalThreadStateException exc){
					//不知道为啥有这个excption，先catch了得了
					//TODO:分析原因避免该Excption
				}
			}
		}).start();
	}


	void generateNewBlock(){
		//TODO:generate random number
		int colorId=COLOR1;//random
		int x,y,relativeY;
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			sheet[x][y]=thisBlockColor;
		}
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

		thisBlockStatus[1]=-1;//y

		for(int i=0;i<4;i++){
			relativeY=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1];
			if(relativeY==0)
				thisBlockStatus[1]=0;//y
		}

		

		nextBlockStatus[2]=(int)(Math.random()*4);//type-random
		nextBlockStatus[3]=(int)(Math.random()*4);//posture-random
		nextBlockStatus[4]=(int)(Math.random()*6)+1;//color-random

		//System.out.println("nextBlockStatus[2,3,4]:"+nextBlockStatus[2]+"~"+nextBlockStatus[3]+"~"+nextBlockStatus[4]);

		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			if(sheet[x][y]>0)
				gameEnded=true;
		}

		startAutoFallingTread();
		gameStatusUpdate();
	}

	void gameStatusUpdate(){
		
		for(int i=0;i<19;i++){
			boolean achieveScore=true;
			for(int j=0;j<9;j++){
				if(sheet[j][i]<=0){
					achieveScore=false;
					break;
				}
			}
			if(achieveScore){
				for(int k=i-1;k>0;k--){
					for(int j=0;j<9;j++){
						sheet[j][k+1]=sheet[j][k];
					}
				}
				t.score+=10;
				t.upadteGameData();
			}
		}

		int x,y;//recompute this block on the sheet
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			//System.out.println("sheet["+x+"]["+y+"]");
			sheet[x][y]=-1;
		}

		for(int i=0;i<9;i++){
			if(sheet[i][0]>0)
				gameEnded=true;
		}
		
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

	synchronized void falling(){//TODO:synchronized
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
		}
		gameStatusUpdate();
		if(!possible)
			generateNewBlock();
		return;
	}

	synchronized void changingPosture(){
		int x,y;
		boolean possible=true;

		for(int i=0;i<4;i++){
				x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
				y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
				sheet[x][y]=0;
		}

		if(thisBlockStatus[3]<3)
			thisBlockStatus[3]++;
		else
			thisBlockStatus[3]=0;
		
		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			if(x<0||x>8||y>19||y<0||sheet[x][y]>0){
				possible=false;
				break;
			}
		}
		
		if(!possible){
			if(thisBlockStatus[3]>0)
				thisBlockStatus[3]--;
			else
				thisBlockStatus[3]=3;
		}

		gameStatusUpdate();

		return;
	}
}

