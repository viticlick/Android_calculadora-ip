package es.viclopez.IPClasses;

public class SubNet {
	
	private IP ip;
	private Mask mask;
	
	public SubNet( IP ip , Mask mask ){
		this.ip = ip;
		this.mask = mask;
	}
	
	public final IP getNetAddres(){
		return ip.and(mask);
	}
	
	public final IP getBroadcastAddres(){
		return ip.or(mask.getWildCard());
	}
	
	public final long getHosts(){	
		//2^(32-CIDR) - 2
		return (long) ( Math.pow( 2, ( 32 - mask.getCIDR() ) ) - 2 );
	 }
	
	protected final Mask getMask()
	{
		return this.mask;
	}
	
	protected final IP getIP(){
		return this.ip;
	}
	
}
