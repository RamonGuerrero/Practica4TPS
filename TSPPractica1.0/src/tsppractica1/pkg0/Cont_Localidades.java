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
public class Cont_Localidades {
    /**
     * Constructor para tener la ruta del archivo de Instrucciones
     */

    public String ImprimirINST(String etq,String codop,String oper,String numerolinea,String modoDir,int cla){
        int longEtq = etq.length() + 1;
        int longCodop = codop.length() + 1;
        int longOper = oper.length();
        int longnum = numerolinea.length()+2;
        int longDir= modoDir.length()+1;
        String contHexa=Integer.toHexString(cla);
        int longCont=contHexa.length();
        String a = numerolinea;
        for (; longnum < 10; longnum++) {
            a += " ";
        }
        a += "|| " + etq;
        for (; longEtq < 10; longEtq++) {
            a += " ";
        }
        a += "|| " + codop;
        for (; longCodop < 10; longCodop++) {
            a += " ";
        }
        a += "|| " + oper;
        for(; longOper <= 20;longOper++){
            a +=" ";
        }
        a += "|| " + modoDir;
        for(; longDir<=10;longDir++){
            a+=" ";                 
        }
        a+="||";
        for(; longCont<4;longCont++)
            a+="0";
        return a+contHexa;
    }
    public String ImprimirTDS(String etiqueta,String valor){
        
        int longetq=etiqueta.length()+1;
        int longvalor=valor.length();
        String a=etiqueta;
        for(;longetq<=10;longetq++)
            a+=" ";
        a+="|| ";
        for(;longvalor<4;longvalor++)
            a+="0";
        return a+valor;
    }
    public int ORG(String oper){
        if(oper.startsWith("%")){
            return Integer.parseInt(oper.substring(1),2);
        }
        else if(oper.startsWith("@")){
            return Integer.parseInt(oper.substring(1),8);
        }
        else if(oper.startsWith("$")){
            return Integer.parseInt(oper.substring(1),16);
        }
        else
            return Integer.parseInt(oper);
    }
    public int EQU(String oper){
        if(oper.startsWith("%")){
            return Integer.parseInt(oper.substring(1),2);
        }
        else if(oper.startsWith("@")){
            return Integer.parseInt(oper.substring(1),8);
        }
        else if(oper.startsWith("$")){
            return Integer.parseInt(oper.substring(1),16);
        }
        else
            return Integer.parseInt(oper);
    }
    public int  Byte1(int contadorLocalidades){
        return ++contadorLocalidades;
    }
    public int  Bytes2(int contadorLocalidades){
        return contadorLocalidades+=2;
    }
    public int  Memoria1Byte(int contadorLocalidades,String oper){
        if(oper.startsWith("%")){
            contadorLocalidades+=Integer.parseInt(oper.substring(1),2);
        }
        else if(oper.startsWith("@")){
            contadorLocalidades+=Integer.parseInt(oper.substring(1),8);
        }
        else if(oper.startsWith("$")){
            contadorLocalidades+=Integer.parseInt(oper.substring(1),16);
        }
        else
            contadorLocalidades+=Integer.parseInt(oper);
        return contadorLocalidades;
    }
    public int  Memoria2Byte(int contadorLocalidades,String oper){
        if(oper.startsWith("%")){
            contadorLocalidades+=(Integer.parseInt(oper.substring(1),2))*2;
        }
        else if(oper.startsWith("@")){
            contadorLocalidades+=(Integer.parseInt(oper.substring(1),8))*2;
        }
        else if(oper.startsWith("$")){
            contadorLocalidades+=(Integer.parseInt(oper.substring(1),16))*2;
        }
        else
            contadorLocalidades+=(Integer.parseInt(oper))*2;
        return contadorLocalidades;
    }
    public int Caracter(int contadorLocalidades,String oper){
        oper=oper.replaceAll("\"","");
        int longitud=oper.length();
        return contadorLocalidades+=longitud;
    }
}
