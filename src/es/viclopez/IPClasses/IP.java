package es.viclopez.IPClasses;

public class IP {
	
	public int BASE_DECIMAL = 0;
	public int BASE_BINARY = 1;
	public int BASE_OCTAL = 2;
	public int BASE_HEXADECIMAL = 3;
	
	//The IP is of the form a.b.c.d
	private int a;
	private int b;
	private int c;
	private int d;
	
	public IP(int i, int j, int k, int l) {
		this.a = i;
		this.b = j;
		this.c = k;
		this.d = l;
	}
	
	public IP( String ip ){
		String[] ipString = ip.split( "." );
		if( ipString.length == 4 ){
			this.a = Integer.getInteger( ipString[0] );
			this.b = Integer.getInteger( ipString[1] );
			this.c = Integer.getInteger( ipString[2] );
			this.d = Integer.getInteger( ipString[3] );
		}
	}
	
	public IP and( IP ip ){
		return new IP( this.a & ip.getA() ,
				this.b & ip.getB() ,
				this.c & ip.getC() ,
				this.d & ip.getD() 
				);
		
	}

	public final IP nextIP(){
		
		int ipA = this.a;
		int ipB = this.b;
		int ipC = this.c;
		int ipD = this.d;
		
		ipD = ipD + 1;
		if( ipD == 256 ){
			ipD = 0;
			ipC = ipC + 1;
			if( ipC == 256 ){
				ipC = 0;
				ipB = ipB + 1;
				if( ipB == 256 ){
					ipB = 0;
					ipA = ipA + 1;
					//Si ipA == 256 lanzar error
				}
			}
		}
		
		
		return new IP( ipA , ipB , ipC , ipD ); 
	}
	
	public IP or( IP ip ){
		return new IP( this.a | ip.getA() ,
				this.b | ip.getB() ,
				this.c | ip.getC() ,
				this.d | ip.getD()
				);
		
	}

	public final String toString(int base){
		String toret = "";
		switch( base ){
		case 0:
			toret = toDecimalString();
			break;
		case 1:
			toret = toBinaryString();
			break;
		case 2:
			toret = toOctalString();
			break;
		case 3:
			toret = toHexString();
			break;
		}
		
		return toret;
	}

	protected final int getA() {
		return a;
	}
	
	protected final int getB() {
		return b;
	}
	
	protected final int getC() {
		return c;
	}
	
	protected final int getD() {
		return d;
	}
	
	protected final void setIP(int a , int b , int c , int d ){
		this.a = a ;
		this.b = b ; 
		this.c = c ;
		this.d = d ;
	}
	
	private final String toBinaryString(){
		String toret = "";
		toret = toBinaryString(a) 
				+ " " + toBinaryString( b )
				+ " " + toBinaryString( c )
				+ " " + toBinaryString( d );
		
		return toret;
	}

	private final String toBinaryString( int n ){
		String toret = Integer.toBinaryString(n);
		while( toret.length() < 8 ){
			toret = "0" + toret;
		}
		return toret;
	}

	private final String toDecimalString(){
		return a + "." + b + "." + c + "." + d;
	}

	private final String toHexString(){
		String toret = "";
		toret = toHexString(a)
				+ " " + toHexString(b)
				+ " " + toHexString(c)
				+ " " + toHexString(d);
		return toret;
	}

	private final String toHexString( int n ){
		String toret = Integer.toHexString(n);
		if( toret.length() == 1 ){
			toret = "0" + toret;
		}
		return toret;
	}
	
	private final String toOctalString(){
		String toret = "";
		toret = toOctalString(a)
				+ " " + toOctalString(b)
				+ " " + toOctalString(c)
				+ " " + toOctalString(d);
		return toret;
	}
	
	private final String toOctalString( int n ){
		String toret = Integer.toOctalString(n);
		while( toret.length() < 3 ){
			toret = "0" + toret ;
		}
		return toret;
	}
	
}
