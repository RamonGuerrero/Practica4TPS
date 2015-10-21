/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tsppractica1.pkg0;

/**
 *
 * @author RamonGuerrero
 */
public class Tabsim {
    
    
    String etiqueta;
    String codop;
    String oper;
    
    public Tabsim(String Etq,String Cod,String Ope){
        this.etiqueta=Etq;
        this.codop=Cod;
        this.oper=Ope;
    }
    
    public String Analizador(){
        String aux=etiqueta;
        if(!etiqueta.equalsIgnoreCase("NULL")){
            if(codop.equalsIgnoreCase("EQU")){
                for(int i=0;i<8;i++){
                    if(i>=etiqueta.length())
                      aux+=" ";
                }
                aux+="||"+oper;
                return aux; 
            }
            else{
                return etiqueta+"||";
            }
        }
        else
            System.out.println("Esta linea no tiene Etiqueta");
        return "";
    }
}
