package borrar;
import java.util.*;


public class Run {
	public static final Random rnd = new Random();
	
	public boolean[][] Matrix=new boolean[10][10];
	public Celda[][] MatrixCeldas=new Celda[10][10];
	public static Celda inicio;
	public static Celda meta;
	
	public Run(){
		Matrix=matrix();
		celdas();
		System.out.println(" Posición inicial: ( "+inicio.row+" , "+inicio.col+" )");
		System.out.println(" Posición final: ( "+meta.row+" , "+meta.col+" )");
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(MatrixCeldas[i][j]!=null)System.out.print("( "+MatrixCeldas[i][j].row+" , "+MatrixCeldas[i][j].col+"..."+MatrixCeldas[i][j].h+" )");
			}
			System.out.println(" ");
		}
		List<Celda> path=new LinkedList<Celda>();
		path=findPath();
		if(path==null)System.out.println("null");
		
		Iterator<Celda> it= path.iterator();
		Celda aux=new Celda(0,0);
		while(it.hasNext()){
			System.out.println("ole");
			aux=it.next();
			System.out.print("( "+aux.row+" , "+aux.col+"..."+aux.f+" )");
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
					MatrixCeldas[i][j]=new Celda(i,j,0,0,h);
				}
			}
		}
	}
	
	//matriz con posicion de tanques
	private boolean[][] matrix(){
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
		 boolean ok=true;
			int row = 0,col = 0;
			while(ok){
				row=rnd.nextInt(10);
				col=rnd.nextInt(10);
				if(MyMatrix[row][col]==false){
					ok=false;
				}
			}
			inicio=new Celda(row,col);
			ok=true;
			while(ok){
				row=rnd.nextInt(10);
				col=rnd.nextInt(10);
				if(MyMatrix[row][col]==false){
					ok=false;
				}
			}
			meta=new Celda(row,col);
		return MyMatrix;
	}
	//posicion de inicio del tanque y posicion final a la que queremos llegar con él.
//	public void establishPositions(){
//		 rnd.setSeed(29);
//		 boolean [][] MyMatrix = new boolean [10][10] ;
//		 int tanqueX = 0;
//		 int tanqueY = 0;
//		 //Obtenemos posición inicial del tanque atl
//		 boolean ok=true;
//			while(ok){
//				tanqueX=rnd.nextInt(10);
//				tanqueY=rnd.nextInt(10);
//				if(MyMatrix[tanqueX][tanqueY]==false){
//					MyMatrix[tanqueX][tanqueY]=true;
//					ok=false;
//				}
//			}
//		inicio=new Celda(tanqueX, tanqueY);
//		ok=true;
//			while(ok){
//				tanqueX=rnd.nextInt(10);
//				tanqueY=rnd.nextInt(10);
//				if(MyMatrix[tanqueX][tanqueY]==false){
//					ok=false;
//				}
//			}
//		meta=new Celda(tanqueX,tanqueY);
//	}
	//array de celdas adyacentes libres 
	Celda[] neighbours(Celda node){
		Celda res[]=new Celda[4];
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
	private List<Celda> findPath(){
		//establishPositions();
		
		 List<Celda> closedSet=new LinkedList<Celda>();//Nodes already evaluated
		 List<Celda> openSet=new LinkedList<Celda>();//Tentative nodes to be evaluated
		 openSet.add(inicio);
		 Celda current;
		 while(!openSet.isEmpty()){
			 current=lowestF(openSet);
			 System.out.println("Current: "+current.row+", "+current.col);
			 if(current.equals(meta)){
				 return pathFromStart(current);
			 }
			 openSet.remove(current);
			 closedSet.add(current);
			Celda[] neigh=neighbours(current);
			
			for(int aux=0;aux<4;aux++){
				if(neigh[aux]!=null){	
					if(!closedSet.contains(neigh[aux])){
						int tentative_g=neigh[aux].g+1;
						if(!openSet.contains(neigh[aux]) || tentative_g<neigh[aux].g){
							//falta parent[neigh]=current
							neigh[aux].cameFrom=current;
							neigh[aux].g(tentative_g);
							neigh[aux].f(neigh[aux].g+neigh[aux].h);
							if(!openSet.contains(neigh[aux])){
								openSet.add(neigh[aux]);
								System.out.println("añadido neighbour: "+neigh[aux].row+", "+neigh[aux].col);
							}
						}
					}	
					
				}
			}
			 
		 }
		return null;
	}
	private Celda lowestF(List<Celda> list){
		Celda res=list.get(0);
		for(int i=1;i<list.size();++i){
			if(list.get(i).f<res.f){
				res=list.get(i);
			}
		}
		return res;
	}
	private List<Celda> pathFromStart(Celda finall){
		List<Celda> res=new LinkedList<Celda>();
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
