/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsppractica1.pkg0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author RamonGuerrero
 * @version TSPPractica1.0"13/09/15"
 */
public class FormaLinea {

    short numeroLinea;
    String linea;

    /* Variables de token*/
    StringTokenizer token = null;
    String etq = null;
    String codop = null;
    String oper = null;

    /* Estado de la Linea*/
    String estado = "q0";
    String estadoOperando="";

    
    /* bandera si debe de llevar operando*/
    boolean llevaOperando = false;
    String modoDir=null,modoDireccionamiento=null;
    byte calBytes,porBytes;
    String totalBytes;
    
    /*Directivas */
    List<String>  Directivas= Arrays.asList("ORG","EQU","DW","DB","DC.W","DC.B","FCB","FDB","FCC","DS","DS.B","DS.W","RMB","RMW");
    /**
     * @param numeroLinea es el contructor del numero de la linea
     * @param linea es el contructor del contenido del la linea
     */

    public FormaLinea(short numeroLinea, String linea) {
        this.numeroLinea = numeroLinea;
        this.linea = linea;
    }

    /**
     * Sustrae los comentarios
     */
    public void quitaComentarios() {
        String comentario;
        int contComillasDobles=0;
        for (int i = 0; i < linea.length(); i++) {
            if(linea.charAt(i)== '"')
                contComillasDobles++;
            if (linea.charAt(i) == ';' && contComillasDobles==0) {
                comentario = linea.substring(i);
                linea = linea.replaceAll(comentario, " ");
                break;
            }
        }
    }

    /**
     * Separa las lineas segun la cantidad de token
     */
    public String separarLinea() {
        if(linea.contains("\"")){
            linea=linea.replaceAll("\\s*$","");
            StringTokenizer tokenComillas = new StringTokenizer(linea,"\"",true);
            System.out.println("linea tiene comillas dobles" + tokenComillas.countTokens());
            if(tokenComillas.countTokens()==1){
                estado+="w1";//Error sintaxis invalida
                etq = null;
                codop = null;
                oper = null;
            }    
            else if(tokenComillas.countTokens()==3){
                token=new StringTokenizer(tokenComillas.nextToken());
                if(token.countTokens()==2){
                    if(VerificaEspacioBlanco()){
                        etq = null;
                        codop = null;
                        oper = null;
                        estado+="q1";//ERROR Etiqueta comienza en espacio en blanco  
                    }
                    else{
                        etq=token.nextToken();
                        codop=token.nextToken();
                        oper=tokenComillas.nextToken()+tokenComillas.nextToken();
                    }
                }
                else if(token.countTokens()==1){
                    if(VerificaEspacioBlanco()){
                        etq="NULL";
                        codop=token.nextToken();
                        oper=tokenComillas.nextToken()+tokenComillas.nextToken();
                    }
                    else{
                        etq = null;
                        codop = null;
                        oper = null;
                        estado+="w2";//ERROR codop no comienza con espacio en blanco
                    }
                        
                }
            }
            else if(tokenComillas.countTokens()==4){
                token=new StringTokenizer(tokenComillas.nextToken());
                if(token.countTokens()==2){
                    if(VerificaEspacioBlanco()){
                        etq = null;
                        codop = null;
                        oper = null;
                        estado+="q1";//ERROR Etiqueta comienza en espacio en blanco  
                    }
                    else{
                        etq=token.nextToken();
                        codop=token.nextToken();
                        oper=tokenComillas.nextToken()+tokenComillas.nextToken()+tokenComillas.nextToken();
                    }
                }
                else if(token.countTokens()==1){
                    if(VerificaEspacioBlanco()){
                        etq="NULL";
                        codop=token.nextToken();
                        oper=tokenComillas.nextToken()+tokenComillas.nextToken()+tokenComillas.nextToken();
                    }
                    else{
                        etq = null;
                        codop = null;
                        oper = null;
                        estado+="w2";//ERROR codop no comienza con espacio en blanco
                    }
                        
                }
            }
            else{
                etq = null;
                codop = null;
                oper = null;
                estado+="q2";//ERROR exedente de token
            }
        }
        
        else{
         token = new StringTokenizer(linea);
         if (token.countTokens() == 0) {
            etq = null;
            codop = null;
            oper = null;
            estado += "q0";//estado del automata sin error solo finaliza
         } else if (token.countTokens() == 1) {
            if (VerificaEspacioBlanco()) {
                codop = token.nextToken();
                etq = "NULL";
                oper = "NULL";
            } else {
                estado += "r1";//estado del automata donde no existe codop
            }
         } else if (token.countTokens() == 2) {
            if (VerificaEspacioBlanco()) {
                codop = token.nextToken();
                oper = token.nextToken();
                etq = "NULL";
            } else {
                etq = token.nextToken();
                codop = token.nextToken();
                oper = "NULL";
            }
        } else if (token.countTokens() == 3) {
                if(VerificaEspacioBlanco()){
                    estado+="q2";//ERROR una etiqueta no debe comenzar con espacio en blanco
                }
                else {
                etq = token.nextToken();
                codop = token.nextToken();
                oper = token.nextToken();
                }
         } else {
            etq = null;
            codop = null;
            oper = null;
            estado += "q2";//estado del automata donde exeden token 
            }
        }
        return codop;
    }

    /**
     * Valida CODOP con una busquedad en el Tabop
     *
     * @param busquedad String este es el resultado de lo que retorna arbol.Buscar()
     * @return true si el CODOP es valido false si no es valido
     */
    public boolean validarCodop(String busquedad,String modo,int contadorORG,String TotalBytes){
        
        if(codop==null){
            llevaOperando=false;
            modoDir=null;
            return false;
        }
        else if(codop.equalsIgnoreCase("ORG")){
            if(contadorORG==0){
                llevaOperando=true;
                modoDir=null;
                return true;
            }
            else
                estado+="g9";//ERROR existe mas de un ORG
        }
        else if(codop.equalsIgnoreCase("EQU")){
            llevaOperando=true;
            modoDir=null;
            return true;
        }
        else if (Directivas.contains(codop.toUpperCase())){
            if(contadorORG>=1){
                llevaOperando=true;
                modoDir=null;
                return true;
            }
            else if(contadorORG==0){
             estado+="h1";//ERROR Directiva antes del ORG     
            }
            else
                estado+="g9";//ERROR mas de un ORG
        }
        else if(codop.equalsIgnoreCase("end")){
            llevaOperando=false;
            modoDir="";
            totalBytes="PORCALCULAR";
            return true;
        }
        else if(busquedad==null){
            modoDir=null;
            llevaOperando=false;
            return false; 
        }  
        else{
            if (busquedad.contains("SN")&&contadorORG>=1) {
                System.out.println(busquedad);
                modoDir=modo;
                porBytes=0;
                totalBytes=TotalBytes;
                llevaOperando=false;
                return true;
            }
            else if(busquedad.contains("SS")&&contadorORG>=1){
                System.out.println(busquedad);
                modoDir=modo;
                porBytes=1;
                totalBytes=TotalBytes;
                llevaOperando=true;
                return true;
            }
            else{
                llevaOperando=false;
                modoDir=null; 
                return false;
            }  
        } 
        return false;
    }

    /**
     * Valida ETIQUETA con la expresion regular
     *
     * @return true cuando es valida la ETIQUETA false si es invalida
     */
    public boolean validarEtiqueta() {
        Pattern patronEtiqueta = Pattern.compile("^[a-zA-Z]{1}[\\w]{0,7}$");
        Matcher comparaEtiqueta = patronEtiqueta.matcher(etq);
        return comparaEtiqueta.matches();
    }

    /**
     * Valida el OPERANDO con la expresion regular
     *
     * @return true cuando es valido el OPERANDO false si es invalido
     */
    public boolean validarOperando() {
        boolean  banderaOperando=false;
        Operando bandera= new Operando();
        if(!codop.equalsIgnoreCase("end")){
            banderaOperando=bandera.Validarlo(modoDir, oper, porBytes,codop,etq);
            modoDireccionamiento=bandera.RetornaModDir();
            if(modoDireccionamiento==""){
                totalBytes="PORCALCULAR";
            }
            else{
                StringTokenizer tokenDir=new StringTokenizer(modoDir,"|");
                StringTokenizer tokenTotal=new StringTokenizer(totalBytes,"|");
                System.out.println("total modos: "+tokenDir.countTokens()+" total de total: "+tokenTotal.countTokens());
                for(int i=1;i<=tokenDir.countTokens();i++){
                    String auxDir=tokenDir.nextToken();
                    System.out.println("Modo Dir: "+auxDir);
                    if(auxDir.equalsIgnoreCase(modoDireccionamiento)){
                        System.out.println("Son iguales");
                        for(int j=1;j<i;j++){
                            System.out.println("Token total: "+tokenTotal.nextToken()+"# "+j);
                        }
                        totalBytes=tokenTotal.nextToken();
                        break;
                    }
                }
            } 
            System.out.println("Modo Direccionamiento retornado de operando: "+modoDireccionamiento+"TotalBytes: "+totalBytes);
            if(banderaOperando){//
                return true;
            }
            else {//operando no valido para el modo de direccionamiento
                estadoOperando=bandera.RetornaEstado();
                return false;
            }
        }
return true;  
    }

    /**
     * Metodo donde va validando los token y si existe error añade un estado del
     * automata
     * @param banderaCodop
     */
    public void Validar(boolean banderaCodop){
        if (etq != null && codop != null && oper != null) {
            boolean banderaEtq = validarEtiqueta();
            boolean banderaOper = validarOperando();
            if (banderaEtq && banderaCodop && banderaOper) {
                if(llevaOperando==false && !oper.contentEquals("NULL"))
                    estado+="r2";//Error donde se encontro el codop pero no debe llevar operando
                else if(llevaOperando==true && oper.contentEquals("NULL"))
                    estado+="r3";//Error donde el codop debe llevar operando
                else if(llevaOperando==true && !oper.contentEquals("NULL"))
                    estado += "qf";//Estado final del automata
                else if(llevaOperando==false && oper.contentEquals("NULL"))
                    estado += "qf";//Estado final del automata
                else
                    estado +="t0";//Error no identificado
            } 
            else {
                estado += "q3";// estado del automata donde Error en comandos invalido
                if (!banderaEtq) {
                    estado += "q4";//Error el comando de etiqueta invalido
                }
                if (!banderaCodop) {
                    estado += "q5";//Error el codop no se encontro en el tabop
                }
                if (!banderaOper) {
                    estado += "q6";//Error el comando operando invalido
                    
                }
            }
        } else {
            if ("q0q0".equals(estado)) {//linea vacia
            } else {
                System.out.println("Hay algun error (FormaLinea Validar)");
            }
        }
    }

    /**
     * El metodo verifica si se encuntra el comando END
     *
     * @return true si se encontro el comand o END false la linea no contiene el
     * comando
     */
    public boolean esEND() {
        if (etq != null && codop != null && oper != null) {
            Pattern patronEND = Pattern.compile("^[    ]*[e|E]{1}[n|N]{1}[d|D]{1}[     ]*$");
            Matcher comparaEND = patronEND.matcher(codop);
            if (comparaEND.matches()) {
                estado += "q9";
                return true;//Estado de fin del ensamblador
            }
        }
        return false;
    }
    public boolean esORG(){
        if (etq != null && codop != null && oper != null) {
            return codop.equalsIgnoreCase("ORG");
        }
        return false;
    }

    /**
     * El metodo recorre la cadena de los estados y verifica en que estados
     * recorrio la linea
     *
     * @return un formato de impresion segun el estado de la linea
     */
    public String Automata() {
        String saltoLinea = System.getProperty("line.separator");
        String imprimirError = "E", imprimirValido = "V";
        String aux = estado.substring(2, 4);
        if (null != aux) {
            switch (aux) {
                case "q0":
                    imprimirError = "L";// el caracter "L" significa que fue una linea vacia
                    break;
                case "r1":
                    imprimirError += "    ERROR No existe Codigo de Operacion" + saltoLinea;
                    break;
                case "r2":
                    imprimirError +="     ERROR El Codigo de Operacion NO debe llevar Operando"+saltoLinea;
                    break;
                case "r3":
                    imprimirError +="     ERROR El Codigo de Operacion debe llevar Operando"+saltoLinea;
                    break;
                case "q1":
                    imprimirError += "    ERROR en ETIQUETA no es valido el primer caracter un Espacio Blanco" + saltoLinea;
                    break;
                case "q2":
                    imprimirError += "    ERROR exedente de tokenes" + saltoLinea;
                    break;
                case "w1":
                    imprimirError +="   ERROR Sintaxis invalida"+saltoLinea;
                case "w2":
                    imprimirError +="   Error Codop no comienza con espacio en Blanco"+saltoLinea;
                case "q3":
                    imprimirError += "    ERROR un comando Invalido" + saltoLinea;
                    for (int i = 4; i < estado.length(); i += 2) {
                        aux = estado.substring(i, i + 2);
                        if (null != aux) {
                            switch (aux) {
                                case "q4":
                                    imprimirError += "    ERROR ETIQUETA invalida" + saltoLinea;
                                    break;
                                case "q5":
                                    imprimirError += "    ERROR CODIGO de OPERACION no se encontro en Tabop" + saltoLinea;
                                    break;
                                case "q6":
                                    imprimirError += "    ERROR OPERANDO invalido" + saltoLinea;
                                    String auxOper;
                                    for(int j=0;j<estadoOperando.length();j+=2){
                                        auxOper=estadoOperando.substring(j,j+2);
                                        if(null != auxOper){
                                            switch(auxOper){
                                                case "MI":
                                                    imprimirError +="   ERROR modo direccionamiento no coinside con operando"+saltoLinea;break;
                                                case "t1":
                                                    imprimirError +="   ERROR operando no valido para ningun modo direccionamiiento"+saltoLinea;break;
                                                case "b1":
                                                    imprimirError +="   ERROR sintaxis del operando invalido"+saltoLinea;break;
                                                case "i1":
                                                    imprimirError+="    ERROR numero fuera de rango" +saltoLinea;break;
                                                case "i2":
                                                    imprimirError +="   ERROR Modo de direccioanmiento no soporta Etiqueta"+saltoLinea;break;
                                                case "E2":
                                                    imprimirError +="   ERROR Etiqueta invalida"+saltoLinea;break;
                                                case "x1":
                                                    imprimirError +="   ERROR no existe registro en lista de validos"+saltoLinea;break;
                                                case "x6":
                                                    imprimirError +="   ERROR No existe un registro"+saltoLinea;break;
                                                case "Y5":
                                                    imprimirError +="   ERROR modo de direccionamiento no acepta campos vacios antes de la ,"+saltoLinea;break;
                                                case "Z3":
                                                    imprimirError +="   ERROR acumulador no valido"+saltoLinea;break;
                                                case "g1":
                                                    imprimirError +="   ERROR el operando debe llevar dobles comillas al inicio y al final"+saltoLinea;break;
                                                case "g2":
                                                    imprimirError +="   ERROR el Codigo de Operacion debe llevar Etiqueta"+saltoLinea;break;
                                                case "g3":
                                                    imprimirError +="   ERROR Debe llevar Operando Valido"+saltoLinea;break;
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case "g9":
                    
                    imprimirError +="   ERROR se encuentran mas de un ORG"+saltoLinea;break;
                case "h1":
                    imprimirError +="   ERROR una Directiva antes del ORG"+saltoLinea;break;
                case "h2":
                    imprimirError +="   ERROR la Etiqueta ya existe en la Tabla de Simbolos"+saltoLinea;break;
                case "q9":
                    imprimirValido += "    se encontro el comando END valido " + saltoLinea;
                    return imprimirValido;
                case "qf":
                    imprimirValido += modoDireccionamiento + saltoLinea;
                    return imprimirValido;
                default:
                    imprimirError +="   Error no identificado";break;
            }
        }
        return imprimirError;
    }

    /**
     * Metodo donde verifica la linea si comienza con espacio en Blanco o
     * Tabulador
     *
     * @return true si el primer caracter de la linea es espacio en Blanco false
     * el primer caracter de linea es difente de Espacio en Blanco
     */
    public boolean VerificaEspacioBlanco() {
        return linea.startsWith(" ") || linea.startsWith("\t");
    }

    /**
     * Separa la linea valida con un fomato de escritura para el archivo.INST
     *
     * @return la linea valida con un formato de impresion si la linea no es
     * valida retorna un null
     */
    public String ImprimirLineasValidas() {
        String cadena = Automata();
        String a = null;
        if (cadena.charAt(0) == 'V') {
                byte numCaracteres = 10;
                int longEtq = etq.length() + 1;
                int longCodop = codop.length() + 1;
                int longOper = oper.length();
                String convNum = Integer.toString(numeroLinea);
                int longnum = convNum.length() + 2;
                a = "# " + convNum;
                for (; longnum < numCaracteres; longnum++) {
                    a += " ";
                }
                a += "|| " + etq;
                for (; longEtq < numCaracteres; longEtq++) {
                    a += " ";
                }
                a += "|| " + codop;
                for (; longCodop < numCaracteres; longCodop++) {
                    a += " ";
                }
                a += "|| " + oper;
                for(; longOper <= 20;longOper++){
                    a +=" ";
                }
                a += "|| " + modoDireccionamiento+"||"+totalBytes;                 
        }
        return a;
    }
    public String ImprimirTabSim(ArrayList vectorEtq){
       String  cadena=Automata(),aux="";
       if(cadena.startsWith("V")){
           Tabsim tabsim = new Tabsim(etq,codop,oper);
           aux=tabsim.Analizador();
           if(!aux.isEmpty()){
              if(!vectorEtq.contains(etq)){
                  return aux;
              }
              else
                  return "Existe";//ERROR Etiqueta ya existente en Tabla de Simbolos
           }
           else
               return "";//No contiene etiqueta
        }
       return aux;
    }
    public String RetornaEtq(){
        return etq;
    }

    /**
     * se toma una linea con Errores y la separa con un formato para escribir en
     * archivo .ERR
     *
     * @return String la linea con error con un formato de impresion si la linea
     * no contiene errores retorna null
     */
    public String ImprimirLineaErrores() {
        String cadena = Automata();
        String b = null;
        if (cadena.startsWith("E")) {
            byte numCaracteres = 10;
            String convNum = Integer.toString(numeroLinea);
            int longnum = convNum.length() + 2;
            b = "# " + convNum;
            for (; longnum < numCaracteres; longnum++) {
                b += " ";
            }
            b += "||" + cadena.substring(1);
        }
        return b;
    }
}
