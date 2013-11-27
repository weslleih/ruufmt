package br.com.wesllei.ruufmt;

public class ItemPrato {
	private String tipo;
    private String prato;
            
    public ItemPrato(String tipo, String prato) {
            super();
            this.tipo = tipo;
            this.prato = prato;
    }
    
    public String getTipo() {
            return tipo;
    }
    public void setTipo(String tipo) {
            this.tipo = tipo;
    }
    public String getPrato() {
            return prato;
    }
    public void setPrato(String prato) {
            this.prato = prato;
    }
}
