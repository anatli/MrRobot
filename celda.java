package atl;

public class celda {
	public int row;
	public int col;
	public int f;
	public int g;
	public int h;
	public celda(int a,int b,int c,int d, int f){
		row=a;
		col=b;
		this.f=c;
		g=d;
		h=f;
	}
	public celda(int a, int b){
		row=a;
		col=b;
		f=0;
		g=0;
		h=0;
	}
	public void f(int n){
		f=n;
	}
	public void g(int n){
		g=n;
	}
	public void h(int n){
		h=n;
	}
}
