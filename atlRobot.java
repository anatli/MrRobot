package atl;

import java.util.*;

import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.control.RobotSetup;

public class atlRobot extends Robot {
	public final boolean[][] Matrix=matrix();
	public celda[][] MatrixCeldas;
	public static celda inicio;
	public static celda meta;
	
	public void run(){
		List<celda> path=findPath();
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
	public void establishPositions(){
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
	celda[] neighbours(celda node){
		celda res[]=new celda[4];
		int num=0;
		int i=node.row;
		int j=node.col;
		
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
	private List<celda> findPath(){
		//establishPositions();
		
		 List<celda> closedSet=new LinkedList<celda>();//Nodes already evaluated
		 List<celda> openSet=new LinkedList<celda>();//Tentative nodes to be evaluated
		 openSet.add(inicio);
		 celda current;
		 while(!openSet.isEmpty()){
			 current=lowestF(openSet);
			 if(current.equals(meta)){
				 return pathFromStart(current);
			 }
			 openSet.remove(current);
			 closedSet.add(current);
			celda[] neigh=neighbours(current);
			
			for(int aux=0;aux<4;aux++){
				if(neigh[aux]!=null){	
					if(!closedSet.contains(neigh[aux])){
						int tentative_g=neigh[aux].g+1;
						if(!openSet.contains(neigh[aux]) || tentative_g<neigh[aux].g){
							//falta parent[neigh]=current
							neigh[aux].cameFrom=current;
							neigh[aux].g(tentative_g);
							neigh[aux].f(neigh[aux].g+neigh[aux].h);
							if(!openSet.contains(neigh[aux]))openSet.add(neigh[aux]);
						}
					}	
					
				}
			}
			 
		 }

		return null;
	}
	private celda lowestF(List<celda> list){
		celda res=list.get(0);
		for(int i=1;i<list.size();++i){
			if(list.get(i).f<res.f){
				res=list.get(i);
			}
		}
		return res;
	}
	private List<celda> pathFromStart(celda finall){
		List<celda> res=new LinkedList<celda>();
		if(finall.equals(inicio)){
			res.add(inicio);
			return res;
		}else{
			res=(pathFromStart(finall.cameFrom));
			res.add(finall);
			return res;
		}	
	}
	
}
