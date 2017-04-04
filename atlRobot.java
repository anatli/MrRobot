package atl;

import java.util.Map;
import java.util.Random;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.control.RobotSetup;

public class atlRobot extends Robot {
	public final boolean[][] Matrix=matrix();
	public celda[][] MatrixCeldas;
	public static celda inicio;
	public static celda meta;
	
	public void run(){
		int path[]=findPath();
		celdas();
		turnLeft (getHeading() %90);
		turnGunRight(90);
		while(true){
			ahead(100000);
			turnRight(90);
		}
	}
	//creamos matriz celdas que guarda las posiciones e inicializa los valores de f y g a 0 y de h 
	//con el num de celdas hasta llegar a la pos final.
	private void celdas(){
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(Matrix[i][j]){
					MatrixCeldas[i][j]=null;
				}else{
					int h=Math.abs(i-meta.row)+Math.abs(j-meta.col);
					MatrixCeldas[i][j]=new celda(i,j,0,0,h);
				}
			}
		}
	}
	
	//matriz con posicion de tanques
	private boolean[][] matrix(){
		 Random rnd = new Random();
		 rnd.setSeed(29);
		 boolean [][] MyMatrix = new boolean [10][10] ;
		 int numTanks = 0;
		 
		 //Creamos matriz con posición de tanques
		 while (numTanks < 30){
				int numRandR = rnd.nextInt(10);
				int numRandC = rnd.nextInt(10);
				if(MyMatrix[numRandR][numRandC] == false){
					MyMatrix[numRandR][numRandC] = true;
					numTanks++;
				}
			}
		return MyMatrix;
	}
	//posicion de inicio del tanque y posicion final a la que queremos llegar con él.
	public void stablishPositions(){
		Random rnd = new Random();
		 rnd.setSeed(29);
		 boolean [][] MyMatrix = new boolean [10][10] ;
		 int tanqueX = 0;
		 int tanqueY = 0;
		 //Obtenemos posición inicial del tanque atl
		 boolean ok=true;
			while(ok){
				tanqueX=rnd.nextInt(10);
				tanqueY=rnd.nextInt(10);
				if(MyMatrix[tanqueX][tanqueY]==false){
					MyMatrix[tanqueX][tanqueY]=true;
					ok=false;
				}
			}
		inicio=new celda(tanqueX, tanqueY);
		ok=true;
			while(ok){
				tanqueX=rnd.nextInt(10);
				tanqueY=rnd.nextInt(10);
				if(MyMatrix[tanqueX][tanqueY]==false){
					ok=false;
				}
			}
		meta=new celda(tanqueX,tanqueY);
	}
	//array de celdas adyacentes libres 
	celda[] neighbours(int i,int j){
		celda res[]=new celda[4];
		int num=0;
		
		if(i!=0){
			if(!Matrix[i-1][j]){
				res[num]=MatrixCeldas[i-1][j];
				num++;
			}
		}
		if(i!=9){
			if(!Matrix[i+1][j]){
				res[num]=MatrixCeldas[i+1][j];
				num++;
			}
		}
		if(j!=0){
			if(!Matrix[i][j-1]){
				res[num]=MatrixCeldas[i][j-1];
				num++;
			}		
		}
		if(j!=9){
			if(!Matrix[i][j+1]){
				res[num]=MatrixCeldas[i][j+1];
				num++;
			}
		}
		return res;
	}
	
	//camino A*
	private int[] findPath(){
		stablishPositions();
		 int closedSet[]=new int[100];
		 int openSet[]=new int[100];
		 

		return null;
	}
	
}
