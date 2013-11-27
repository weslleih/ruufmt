package br.com.wesllei.ruufmt;

import com.google.gson.Gson;

public class MappingCardapio {

	private String json;
	private Cardapio cardapio;

	public MappingCardapio(String json) {
		this.json = json;
		Gson gson = new Gson();
        cardapio = gson.fromJson( json, Cardapio.class );
	}
	public Cardapio getCardapio(){
		return cardapio;
	}

}
