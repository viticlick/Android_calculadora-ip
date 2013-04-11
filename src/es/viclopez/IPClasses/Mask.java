package es.viclopez.IPClasses;

public class Mask extends IP {

	private int cidr;
	
	public Mask( int a , int b , int c , int d ){
		super( a , b, c, d );
	}
	
	public Mask( int n ){
		super( 0 , 0 , 0 , 0);
		cidr = n ;
		if( n < 9 ){
			setIP( subMask( n ) , 0 , 0 , 0 );
		}else if( n < 17 ){
			setIP( 255 , subMask( n - 8 ) , 0 , 0 );
		}else if( n < 25 ){
			setIP( 255 , 255 , subMask( n - 16) , 0 );
		}else if( n < 33 ){
			setIP( 255 , 255 , 255 , subMask( n - 24 ) );
		}
	}
	
	private final int subMask( int n ){
		int subMask=0;
		
		while( n > 0 ){
			subMask = subMask >> 1 ;
			subMask = subMask | 128 ;
			n--;
		}
		
		return subMask;
	}
		
	public final int getCIDR(){
		return cidr;
	}
	
	public final IP getWildCard(){
		return new IP(  this.getA() ^ 255 ,
				this.getB() ^ 255 ,
				this.getC() ^ 255 ,
				this.getD() ^ 255 );
	}
}
