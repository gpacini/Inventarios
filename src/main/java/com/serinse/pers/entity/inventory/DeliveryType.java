package com.serinse.pers.entity.inventory;

public enum DeliveryType  {

	ENTREGA("Ingreso"), DESPACHO("Salida"), REDISTRIBUCION("Redistribucion");
	
	private final String name;
	
	private DeliveryType(String name){
		this.name = name;
	}
	
	public final String getName(){
		return name;
	}
	
	public static DeliveryType getByName(String name) throws Exception{
		if( name.equalsIgnoreCase(ENTREGA.name) ){
			return ENTREGA;
		} else if( name.equalsIgnoreCase(DESPACHO.name) ){
			return DESPACHO;
		} else {
			throw new Exception("No existe");
		}
	}
	
}
