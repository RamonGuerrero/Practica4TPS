package tsppractica1.pkg0;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author RamonGuerrero
 * @version TSPPractica1.0"13/09/15"
 */
public class TSPPractica10 {

    static boolean banderaTermino=false;    //Esta bandera me indica si ya termino la ejecucion para cerrar los Archivos
    static String nuevalinea = System.getProperty("line.separator"); // Esta cadena es un salto de linea
    static Arbol arbol=new Arbol();
    /**
     * @param args the command line arguments
     * @throws IOException  para los archivos de escritura .INST .ERR
     */
    public static void main(String[] args) throws IOException {
        
        /*La funcion del ciclo es verificar que Exista un archivo.ASM*/
        String ruta;
        boolean rutaValida=false;
        File existente = null;
        do {
            ruta=JOptionPane.showInputDialog(null,"Ingresa Ruta (.ASM):"); //cadena de la ruta donde se localiza el archivo
            if(ruta.contains(".asm")){
                existente=new File(ruta);
                if(existente.exists()){
                    JOptionPane.showMessageDialog(null,"El archivo se prosesara");
                    rutaValida=true;
                }    
                else
                    JOptionPane.showMessageDialog(null,"El archivo no existe");
            }
            else
                JOptionPane.showMessageDialog(null,"El archivo no es extencion .ASM");
        }while(!rutaValida);
        
        FileReader fr;   //objeto de la clase FileReader para lograr leer el archivo
        BufferedReader entrada;    //objeto de la clase BufferedReader para leer una linea completa del archivo
        
        String rutaERR=ruta; //cadena donde se va a guardar la ruta del Archivo.ERR
        rutaERR = rutaERR.replaceAll(".asm", ".ERR"); //se replaza .ASM por .ERR
        File archivoERR =new File(rutaERR);// crea el archivo en la rutaERR
        FileWriter frERR =new FileWriter(archivoERR);//abre el archivo para escribir caracter por caracter
        BufferedWriter brERR=new BufferedWriter(frERR);//ayuda a escribir en archivo mediante un buffer
        brERR.write("___________Archivo de Errores(.ERR)________"+nuevalinea+nuevalinea+nuevalinea);
        brERR.write("# Linea ||         ERROR                   "+nuevalinea);
        brERR.write("-------------------------------------------"+nuevalinea+nuevalinea);
        
        String rutaINST=ruta;//cadena donde se va a guardar la ruta del Archivo.INST
        rutaINST= rutaINST.replaceAll(".asm",".INST");//se remplaza .ASM por .INST
        File archivoINST =new File(rutaINST); //se crea el archivo en la rutaINST
        FileWriter frINST =new FileWriter(archivoINST); // se abre el archivo para escribir caracter por caracter
        BufferedWriter brINST=new BufferedWriter(frINST); //ayuda a escribir en archivo mediante un buffer
        brINST.write("_______________________Archivo de Instrucciones(.INST)___________________________________________________"+nuevalinea+nuevalinea+nuevalinea);
        brINST.write("# Linea   || Etiqueta || CODOP    || Operando             || Modo Dir  ||Contador Localidades"+nuevalinea);
        brINST.write("----------------------------------------------------------------------------------------------------------"+nuevalinea+nuevalinea);
        
        String rutaTDS=ruta;
        rutaTDS=rutaTDS.replace(".asm",".TDS");
        File archivoTDS=new File(rutaTDS);
        FileWriter frTDS =new FileWriter(archivoTDS);
        BufferedWriter brTDS=new BufferedWriter(frTDS);
        brTDS.write("_________Tabla de Simbolos(.TDS)___________"+nuevalinea);
        brTDS.write("Etiqueta|| Valor"+nuevalinea);
        
        ArrayList Etiquetas = new ArrayList();
        
        /*Aqui vamos a leer el archivo TABOP.TXT*/
        StringTokenizer tokenTabop; //nos ayudara a tokenizar los parametros para insertar en el arbol
        String rutaTabop=System.getProperty("user.dir")+"\\src\\TABOP.TXT";//nos declara la ruta del TABOP.TXT
        FileReader frTabop;
        try {
            
            frTabop = new FileReader(rutaTabop);
            BufferedReader brTabop=new BufferedReader(frTabop);
            String lineaTabop=brTabop.readLine();
            String codop,codMaq,modDir,aux;
            Byte cal,porcal,total;
            while(lineaTabop!=null){
                tokenTabop= new StringTokenizer(lineaTabop,"|");
                if(tokenTabop.countTokens()==6){
                    codop=tokenTabop.nextToken();
                    modDir=tokenTabop.nextToken();
                    codMaq=tokenTabop.nextToken();
                    aux=tokenTabop.nextToken();
                    cal=Byte.parseByte(aux);
                    aux=tokenTabop.nextToken();
                    porcal=Byte.parseByte(aux);
                    aux=tokenTabop.nextToken();
                    total=Byte.parseByte(aux);
                    arbol.insertar(codop, modDir, codMaq, cal, porcal, total);//se inserta el nuevo nodo
                }
                lineaTabop=brTabop.readLine();
            }
            frTabop.close();//cerramos el archivo del TABOP.TXT
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Arbol.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* En este try-cath sirve para leer el archivo de Lectura .ASM */
        try { 
/*  Primer parte: Recorre el archivo de lectura*/
            fr = new FileReader(existente); //se abre el acrhivo en forma de lectura
            entrada = new BufferedReader(fr);
            
            /* Objeto de la Clase FormaLinea*/
            FormaLinea linea=null;
            
            String lineas= entrada.readLine(); //primer linea del archivo
            short contlineas=0,contadorORG=0; // contador de las lineas del archivo
            String validas=null;//me va indicar si existio error o fue valida la linea
            String errores=null;
            short contErrores=0,contLineasValidas=0,contEtiquetas=0;//contador de lineas validas y lineas con errores

            while(lineas!=null){ // ciclo para recorrer todo el archivo de lectura
                contlineas++;
                linea=new FormaLinea(contlineas,lineas);// se construye el objeto 
                linea.quitaComentarios();//metodo para quitar los comentarios
                String codopTabop=linea.separarLinea();//separa las lineas que no son comentarios y lineas vacias
                System.out.println("Codop: "+codopTabop);
                if(codopTabop!=null){
                    String busquedad=arbol.Buscar(codopTabop);
                    String modoDir=arbol.retornaModoDireccionamiento();
                    String TotalByte=arbol.retornaTotalBytes();
                    boolean banderaTabop=linea.validarCodop(busquedad,modoDir,contadorORG,TotalByte);
                    System.out.println("Se encontro el codop:"+banderaTabop); 
                    linea.Validar(banderaTabop);//Valida la linea
                    if(linea.esORG()){
                        contadorORG++;
                    }
                }  
                banderaTermino=linea.esEND();//el metodo retorna si se encontro el comando END
                linea.Automata();//Recorre los estados de la linea para verificar cual fue el error
                validas=linea.ImprimirLineasValidas();//retorna la linea valida con un formato de escritura si la linea no fue valida retorna null
                errores=linea.ImprimirLineaErrores();//retorna la linea con error con un formato de escritura si la linea no contiene errores retorna null
                if(validas!=null && errores==null){//Validacion si es valida la linea
                    String tabsim=linea.ImprimirTabSim(Etiquetas);
                    if(!tabsim.equalsIgnoreCase("Existe")){
                        Etiquetas.add(linea.RetornaEtq());
                        brTDS.write(tabsim+nuevalinea);
                        brINST.write(validas+nuevalinea);
                        contEtiquetas++;
                        contLineasValidas++;
                    }
                    else if(tabsim.isEmpty()){
                        brINST.write(validas+nuevalinea);
                        contLineasValidas++;
                    }
                    else{
                        errores="#"+contlineas+"||    ERROR etiqueta existente en la Tabla de Simbolos pero la linea es Valida"+nuevalinea;
                    }
                }
                if(errores!=null){//validacion si la linea contiene errores
                    brERR.write(errores+nuevalinea);
                    contErrores++;
                }   
                if(banderaTermino==true){//validacion si se encontro el comando END para terminar
                    break;   
                }
                lineas= entrada.readLine(); // salta de linea en el Archivo de Lectura
                System.out.println("#"+contlineas+" linea saltada\n\n");
            }
        if(banderaTermino==false){
            banderaTermino=true;//al final del archivo no se encontro el comadno END escribe algun error
            brERR.write("      ERROR no se encontro el comando END");
            contErrores++;
        }    
        if(contErrores==0)
            brERR.write("      No existen Errores en Archivo.ASM       ");
        if(contLineasValidas==0)
            brINST.write("     No existen Lineas Validas para Ensamblador      ");
        if(contEtiquetas==0)
            brTDS.write("No existen Etiquetas");
        
         fr.close();
         brERR.close();
         brINST.close();
         brTDS.close();
         /*comienza e coquinero*/
         Cont_Localidades nuevo =new Cont_Localidades();
         int cl=0,cla=cl;//Contador de Localidades
         
         List<String> Directivas_1Byte=Arrays.asList("DB","DC.B","FCB");
         List<String> Directivas_2Byte=Arrays.asList("DW","DC.W","FDB");
         List<String> Directivas_Memoria=Arrays.asList("DS","DS.B","RMB");
         List<String> Directivas_Memoria2=Arrays.asList("DS.W","RMW"); 
        File archivoinst=new File(rutaINST);
        
        String rutaNew= rutaINST.replaceAll(".INST",".ins");
        System.out.println("Ruta escritura: "+rutaNew);
        File archivonew =new File(rutaNew); //se crea el archivo en la rutaINST
        FileWriter frnew =new FileWriter(archivonew); // se abre el archivo para escribir caracter por caracter
        BufferedWriter brnew=new BufferedWriter(frnew);
        
        String rutaTDSNew= rutaINST.replaceAll(".INST",".td");
        System.out.println("Ruta escritura tds: "+rutaTDSNew);
        File archivoTDSNew =new File(rutaTDSNew); //se crea el archivo en la rutaINST
        FileWriter frTDSNew =new FileWriter(archivoTDSNew); // se abre el archivo para escribir caracter por caracter
        BufferedWriter brTDSNew=new BufferedWriter(frTDSNew);
        
            try{
                FileReader frinst=new FileReader(archivoinst);
                BufferedReader brinst=new BufferedReader(frinst);
                
                String l=null;
                StringTokenizer tokenes;
                System.out.println("linea Reader: "+l);
                for(int i=1;i<7;i++){
                    l=brinst.readLine();
                    brnew.write(l+nuevalinea);
                    System.out.println("#"+i+"linea Reader: "+l);
                }
                l=brinst.readLine();
                brTDSNew.write("_______________Tabla de Simbolos________________________"+nuevalinea);
                brTDSNew.write("Etiqueta  || Valor"+nuevalinea);
                System.out.println("linea Reader antes While: "+l);
                while(l!=null){
                    tokenes=new StringTokenizer(l,"||");
                    if(tokenes.countTokens()==6){
                        String numerolinea=tokenes.nextToken();
                        String etq=tokenes.nextToken();
                        String codop=tokenes.nextToken();
                        String oper=tokenes.nextToken();
                        String modoDir=tokenes.nextToken();
                        String totalbytes=tokenes.nextToken();
                        etq=etq.replaceAll("\\s","");
                        codop=codop.replaceAll("\\s","");
                        oper=oper.trim();
                        
                        System.out.println(numerolinea);
                        System.out.println(etq);
                        System.out.println(codop);
                        System.out.println(oper);
                        System.out.println(modoDir);
                        System.out.println(totalbytes);
                        if(!totalbytes.equalsIgnoreCase("PORCALCULAR")){
                            System.out.println("entro diferente porcalcular");
                            cla=cl;
                            cl+=Integer.parseInt(totalbytes);
                            String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                            System.out.println("Contado: "+cl+"ContadoLA: "+cla);
                            System.out.println("linea a escribir"+x);
                            brnew.write(x+nuevalinea);
                            if(!etq.equalsIgnoreCase("NULL")){
                                System.out.println("tiene etiqueta");
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                        else{
                            System.out.println("entro porcalcular");
                            codop=codop.replaceAll("\\s","");
                            if(codop.equalsIgnoreCase("ORG")){
                                System.out.println("entro org");
                                oper=oper.replaceAll("\\s","");
                                cl=nuevo.ORG(oper);
                                cla=cl;
                                String y=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+y);
                                brnew.write(y+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cl);
                                        y=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(y+nuevalinea);
                                }
                            }
                            else if(codop.equalsIgnoreCase("END")){
                                System.out.println("entro end");
                                cla=cl;
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cl);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                            else if(Directivas_1Byte.contains(codop.toUpperCase())){
                                System.out.println("entro 1byte");
                                cla=cl;
                                cl=nuevo.Byte1(cl);
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                            else if(Directivas_2Byte.contains(codop.toUpperCase())){
                                System.out.println("entro 2byte");
                                cla=cl;
                                cl=nuevo.Bytes2(cl);
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                            else if(Directivas_Memoria.contains(codop.toUpperCase())){
                                System.out.println("entro Memoria1");
                                oper=oper.replaceAll("\\s","");
                                cla=cl;
                                cl=nuevo.Memoria1Byte(cl,oper);
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                            else if(Directivas_Memoria2.contains(codop.toUpperCase())){
                                System.out.println("entro Memoria2");
                                oper=oper.replaceAll("\\s","");
                                cla=cl;
                                cl=nuevo.Memoria2Byte(cl,oper);
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                            else if(codop.equalsIgnoreCase("EQU")){
                                System.out.println("entro equ");
                                oper=oper.replaceAll("\\s","");
                                int valorEtqAbs=nuevo.EQU(oper);
                                cla=valorEtqAbs;
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        System.out.println("contadorLocalidades: "+cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        System.out.println("Linea  a escribir:"+x);
                                        brTDSNew.write(x+nuevalinea);
                            }
                            else if(codop.equalsIgnoreCase("FCC")){
                                System.out.println("entro fcc");
                                oper=oper.trim();
                                cla=cl;
                                cl=nuevo.Caracter(cl,oper);
                                String x=nuevo.ImprimirINST(etq,codop,oper,numerolinea,modoDir,cla);
                                System.out.println("ContadorL: "+cl+"ContadorLA: "+cla);
                                System.out.println("imprimir a inst: "+x);
                                brnew.write(x+nuevalinea);
                                if(!etq.equalsIgnoreCase("NULL")){
                                        String etiqueta=etq;
                                        String valor=Integer.toHexString(cla);
                                        x=nuevo.ImprimirTDS(etiqueta,valor);
                                        brTDSNew.write(x+nuevalinea);
                                }
                            }
                        }
                    }
                    l=brinst.readLine();
                }
                brinst.close();
                brnew.close();
                brTDSNew.close();
                
                archivoinst=new File(rutaINST);
                archivoinst.delete();
                
                File archivotds=new File(rutaINST.replaceAll(".INST",".TDS"));
                archivotds.delete();
            }
            catch(FileNotFoundException ex){
                Logger.getLogger(Arbol.class.getName()).log(Level.SEVERE, null, ex);
            }
         JOptionPane.showMessageDialog(null,"!Listo!\n Ya se cerraron los archivos");
         
        } catch (FileNotFoundException ex) {// excepcion de Archivo de Lectura
            Logger.getLogger(TSPPractica10.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) { //exepcion de Lectura de l Archivo de Lectura
            Logger.getLogger(TSPPractica10.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
}
