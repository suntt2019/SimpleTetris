import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



interface blockInfo{
	public final int[][][][] blockShape={
		{ {{0,0},{1,0},{0,1},{1,1}},{{0,0},{1,0},{0,1},{1,1}},{{0,0},{1,0},{0,1},{1,1}},{{0,0},{1,0},{0,1},{1,1}} },
		{ {{0,0},{0,1},{0,2},{0,3}},{{-1,1},{0,1},{1,1},{-2,1}},{{0,0},{0,1},{0,2},{0,3}},{{2,1},{-1,1},{0,1},{1,1}} },
		{ {{0,0},{0,1},{1,1},{1,2}},{{0,1},{0,2},{-1,2},{1,1}},{{0,0},{0,1},{1,1},{1,2}},{{0,1},{0,2},{-1,2},{1,1}} },
		{ {{-1,1},{0,1},{0,2},{1,2}},{{0,0},{0,1},{-1,1},{-1,2}},{{-1,1},{0,1},{0,2},{1,2}},{{0,0},{0,1},{-1,1},{-1,2}} },
		{ {{-1,2},{-1,1},{0,1},{1,1}},{{-1,0},{0,0},{0,1},{0,2}},{{-1,1},{0,1},{1,1},{1,0}},{{0,0},{0,1},{0,2},{1,2}} },
		{ {{-1,0},{-1,1},{0,1},{1,1}},{{1,0},{0,0},{0,1},{0,2}},{{-1,1},{0,1},{1,1},{1,2}},{{0,0},{0,1},{0,2},{-1,2}} },
		{ {{0,0},{-1,1},{0,1},{1,1}},{{1,1},{0,0},{0,1},{0,2}},{{-1,1},{0,1},{1,1},{0,2}},{{0,0},{0,1},{0,2},{-1,1}} },
	};
}

class Tetris{
	static int cubeS=0;
	int score=-1,lastScore=0,maxScore=0;
	int cubeSize=40;
	JFrame jfrm;
	JPanel headPanel,mainPanel,gamePanel,statusPanel,textPanel,controlPanel;
	JButton cubes[],nextCubes[],restartButton,leftBtn,rightBtn,upBtn,downBtn;
	JButton uselessbtn1,uselessbtn2,buttonOnScreenToggle;
	JLabel titleLabel,scoreLabel,gameOverLabel;
	GameOperator go;
	boolean screenbtn=false;
	final static Color COLORS[]={new Color(255,255,255),new Color(250,80,80),new Color(80,250,80),new Color(80,80,250),new Color(230,230,30),new Color(210,30,210),new Color(30,210,210)};
	static final int EMPTY=0;

	Tetris(int cs){
		if(cs>0)
			cubeSize=cs;
		System.out.println(cubeSize);


		jfrm = new JFrame("Tetris demo");
		jfrm.setLayout(new BorderLayout());
		jfrm.setSize(16*cubeSize,20*cubeSize+80);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jfrm.setLayout(new BorderLayout());

		headPanel = new JPanel();
		headPanel.setLayout(new FlowLayout());
		jfrm.add(headPanel,BorderLayout.NORTH);

		titleLabel = new JLabel("SimpleTetris!  --by suntt2019");
		headPanel.add(titleLabel);


		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		jfrm.add(mainPanel);

		gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(20,9));
		mainPanel.add(gamePanel);
		gamePanel.setPreferredSize(new Dimension(9*cubeSize,20*cubeSize));

		cubes = new JButton[180];
		for(int i=0;i<180;i++){
			cubes[i] = new JButton();
			//cubes[i].setPreferredSize(new Dimension(50,50));
			cubes[i].setEnabled(false);
			cubes[i].setBackground(COLORS[EMPTY]);
			gamePanel.add(cubes[i]);
		}

		statusPanel = new JPanel();
		statusPanel.setLayout(new FlowLayout());
		mainPanel.add(statusPanel);
		statusPanel.setPreferredSize(new Dimension((int)(6.1*cubeSize),20*cubeSize));

		textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(5,1));
		statusPanel.add(textPanel);

		scoreLabel = new JLabel("得分:"+score);
		textPanel.add(scoreLabel);

		textPanel.add(new JLabel("(放下一个方块+1分)"));
		textPanel.add(new JLabel("(填满一行+20分)"));
		textPanel.add(new JLabel("--------------------------------"));
		textPanel.add(new JLabel( "          ↓下一个方块↓           "));

		JPanel nextPanel = new JPanel();
		nextPanel.setLayout(new GridLayout(5,5));
		statusPanel.add(nextPanel);
		nextPanel.setPreferredSize(new Dimension(5*cubeSize,5*cubeSize));

		nextCubes = new JButton[25];
		for(int i=0;i<25;i++){
			nextCubes[i] = new JButton();
			//cubes[i].setPreferredSize(new Dimension(50,50));
			nextCubes[i].setEnabled(false);
			nextCubes[i].setBackground(COLORS[EMPTY]);
			nextPanel.add(nextCubes[i]);
		}

		gameOverLabel = new JLabel("");
		statusPanel.add(gameOverLabel);


		restartButton = new JButton("重新开始");
		restartButton.addActionListener((e)->go.gameRestart());

		leftBtn = new JButton("←");
		leftBtn.addActionListener((e)->{
			jfrm.requestFocusInWindow();
			if(!go.gameEnded)
				go.goingLeft();
		});
		rightBtn = new JButton("→");
		rightBtn.addActionListener((e)->{
			jfrm.requestFocusInWindow();
			if(!go.gameEnded)
				go.goingRight();
		});
		upBtn = new JButton("↑");
		upBtn.addActionListener((e)->{
			jfrm.requestFocusInWindow();
			if(!go.gameEnded)
				go.changingPosture();
		});
		downBtn = new JButton("↓");
		downBtn.addActionListener((e)->{
			jfrm.requestFocusInWindow();
			if(!go.gameEnded)
				go.goingDown();
		});

		uselessbtn1 = new JButton("");
		uselessbtn2 = new JButton("");
		uselessbtn1.setEnabled(false);
		uselessbtn2.setEnabled(false);
		uselessbtn1.setContentAreaFilled(false);
		uselessbtn2.setContentAreaFilled(false);

		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2,3));
		controlPanel.setPreferredSize(new Dimension(6*cubeSize,4*cubeSize));
		controlPanel.add(uselessbtn1);
		controlPanel.add(upBtn);
		controlPanel.add(uselessbtn2);
		controlPanel.add(leftBtn);
		controlPanel.add(downBtn);
		controlPanel.add(rightBtn);
		statusPanel.add(controlPanel);
		statusPanel.remove(controlPanel);

		buttonOnScreenToggle = new JButton("屏幕按钮");
		buttonOnScreenToggle.addActionListener((e)->{
			jfrm.requestFocusInWindow();
			if(screenbtn){
				statusPanel.remove(controlPanel);
			}else{
				statusPanel.add(controlPanel);
			}
			screenbtn=!screenbtn;
			jfrm.repaint();
			jfrm.setVisible(true);
		});
		statusPanel.add(buttonOnScreenToggle);

		jfrm.dispatchEvent(new FocusEvent(jfrm,FocusEvent.FOCUS_GAINED,true));


		jfrm.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){
				if(!go.gameEnded){
					switch(e.getKeyChar()){
						case 'a': case 'A': case '4':
							go.goingLeft();
							break;
						case 'd': case 'D': case '6':
							go.goingRight();
							break;
						case 's': case 'S':case '2':
							go.goingDown(); 
							break;
						case 'w': case 'W': case '8':
							go.changingPosture();
							break;
						default:
							//System.out.println(e.getKeyChar());
					}
					// System.out.println(e);
					// switch(e.getKeyCode()){
					// 	case KeyEvent.VK_LEFT:
					// 		go.goingLeft();
					// 		break;
					// 	case KeyEvent.VK_RIGHT:
					// 		go.goingRight();
					// 		break;
					// 	case KeyEvent.VK_DOWN:
					// 		go.goingDown(); 
					// 		break;
					// 	case KeyEvent.VK_UP:
					// 		go.changingPosture();
					// 		break;
					// }
				}
				if(go.gameEnded&&(e.getKeyChar()=='R'||e.getKeyChar()=='r')){
					go.gameRestart();
					try{
						Thread.sleep(500);
					}
					catch(InterruptedException exc){
						System.out.println("InterruptedException");
					}
				}

			}
			public void keyPressed(KeyEvent e){
				// System.out.println(e);
			}
			public void keyReleased(KeyEvent e){
				// System.out.println(e);	
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
		scoreLabel.setText("得分:"+score);
		if(go.gameEnded){
			gameOverLabel.setText("游戏结束");
		}else{
			gameOverLabel.setText("上次:"+lastScore+",最高:"+maxScore);
		}
		return;
	}


	public static void main(String args[]){
		cubeS=0;
		if(args.length!=0)
			cubeS=Integer.parseInt(args[0]);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Tetris t = new Tetris(cubeS);
				t.init();
			}
		});
	}

	void init(){
		go = new GameOperator();
		go.init(this);
		printSheet(go.getSheet());
		jfrm.requestFocusInWindow();
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

		for(int i=0;i<9;i++)
			for(int j=0;j<20;j++)
				sheet[i][j]=EMPTY;
		thisBlockColor=EMPTY;
		thisBlockStatus[0]=4;
		thisBlockStatus[4]=-1;
		nextBlockStatus[2]=(int)(Math.random()*7);//type-random
		nextBlockStatus[3]=(int)(Math.random()*4);//posture-random
		nextBlockStatus[4]=(int)(Math.random()*6)+1;//color-random
		generateNewBlock();
	}


	void gameEnd(){
		if(gameEnded)
			return;
		gameEnded=true;
		t.upadteGameData();
		t.statusPanel.remove(t.buttonOnScreenToggle);
		t.statusPanel.add(t.restartButton);
		t.statusPanel.add(t.buttonOnScreenToggle);
		return;
	}

	void gameRestart(){
		t.statusPanel.remove(t.restartButton);
		t.jfrm.requestFocusInWindow();
		new Thread(new Runnable(){
			public void run(){
				int x,y;
				t.lastScore=t.score;
				if(t.score>t.maxScore)
					t.maxScore=t.score;
				try{
					Thread.sleep(500);
				}
				catch(InterruptedException exc){
					System.out.println("InterruptedException");
				}
				//System.out.println("gameRestart");
				for(int i=0;i<4;i++){
					x=blockInfo.blockShape[nextBlockStatus[2]][nextBlockStatus[3]][i][0]+2;
					y=blockInfo.blockShape[nextBlockStatus[2]][nextBlockStatus[3]][i][1]+1;
					t.nextCubes[y*5+x].setBackground(t.COLORS[EMPTY]);
				}
				init(t);
				t.score=0;
				gameEnded=false;
				t.upadteGameData();
				// while(true){
				// 	try{
				// 	Thread.sleep(200);
				// 	}
				// 	catch(InterruptedException exc){
				// 		System.out.println("InterruptedException");
				// 	}
				// 	for(int i=0;i<9;i++)
				// 		for(int j=0;j<20;j++)
				// 			System.out.print(sheet[i][j]+" ");
				// }
			}
		}).start();
		return;
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
			x=blockInfo.blockShape[nextBlockStatus[2]][nextBlockStatus[3]][i][0]+2;
			y=blockInfo.blockShape[nextBlockStatus[2]][nextBlockStatus[3]][i][1]+1;
			t.nextCubes[y*5+x].setBackground(t.COLORS[EMPTY]);
		}

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

		

		nextBlockStatus[2]=(int)(Math.random()*7);//type-random
		nextBlockStatus[3]=(int)(Math.random()*4);//posture-random
		nextBlockStatus[4]=(int)(Math.random()*6)+1;//color-random

		//System.out.println("nextBlockStatus[2,3,4]:"+nextBlockStatus[2]+"~"+nextBlockStatus[3]+"~"+nextBlockStatus[4]);

		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][0]+thisBlockStatus[0];
			y=blockInfo.blockShape[thisBlockStatus[2]][thisBlockStatus[3]][i][1]+thisBlockStatus[1];
			if(sheet[x][y]>0){
				gameEnd();
			}
		}

		for(int i=0;i<4;i++){
			x=blockInfo.blockShape[nextBlockStatus[2]][nextBlockStatus[3]][i][0]+2;
			y=blockInfo.blockShape[nextBlockStatus[2]][nextBlockStatus[3]][i][1]+1;
			t.nextCubes[y*5+x].setBackground(t.COLORS[nextBlockStatus[4]]);
		}

		t.score++;
		startAutoFallingTread();
		gameStatusUpdate();
	}

	void gameStatusUpdate(){
		
		for(int i=0;i<20;i++){
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
				t.score+=20;
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
			if(sheet[i][0]>0){
				gameEnd();
			}
		}
		
		t.printSheet(sheet);
		t.upadteGameData();

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

