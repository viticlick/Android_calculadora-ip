package es.viclopez.IPClasses;

import java.util.LinkedList;

public class Network extends SubNet{

	public Network( IP ip, Mask mask ) {
		super(ip, mask);
	}
	
	public enum NETCLASS { A , B , C , D , E };
	private NETCLASS netClass;
	
	private LinkedList< Network > subNets;
	
	public void divideNetwork( Mask mask ){
		//precondition: mask > this.mask
		//				mask.getCIDR >= 30
		//Obtiene el número de subredes que se puede obtener
		
		subNets.clear();
		double numberSubnets =  Math.pow( 2 , ( mask.getCIDR() - this.getMask().getCIDR() ) ); 
		IP nextIp = this.getIP();
		
		for( int i = 0 ; i < numberSubnets ; i++ ){
			
			Network nt = new Network( nextIp  , mask );
			subNets.add(nt);
			nextIp = nextIp.or( mask.getWildCard() );
			nextIp = nextIp.nextIP();
		}
	}
	
	public LinkedList<Network> getSubnets(){
		
		LinkedList< Network > lista = new LinkedList< Network >();
		if( ! subNets.isEmpty() ){
			for( Network nt : subNets ){
				for( Network snt : nt.getSubnets() ){
					lista.add(snt);
				}
			}
		}else{
			lista.add(this);
		}
		
		return lista;
	}
	
	
}
