package atl;

public class celda{
	public int row;
	public int col;
	public int f;
	public int g;
	public int h;
	public celda cameFrom;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof celda))
			return false;
		celda other = (celda) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
}
