/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

//import java.util.ArrayList;

import java.util.ArrayList;


/**
 *
 * @author Norhan
 */
public class Mensaje {

    private String asunto;
    private String texto;
    private String sender;
    private int parentid;
    private int id;
    private int folderid;
    private int prio;
    private String fecha;
    private ArrayList<String> destinatarios; 

    public Mensaje(int aInt, int aInt0, String string, String text, int parseInt, String string0, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }
    
    public String getSender(){
        return sender;
    }
    
    public void setSender(String sender){
        this.sender = sender;
    }
    
    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getParentid() {
        return parentid;
    }
    
    public void setDestinatarios(ArrayList<String> destinatarios) {
        this.destinatarios = destinatarios;
    }
    
    public void setDestinatarios(String[] destinatarios) {
        this.destinatarios = new ArrayList<>();
        for(String s: destinatarios){
            this.destinatarios.add(s);
        }
        
    }

    public ArrayList<String> getDestinatarios() {
        return destinatarios;
    }
    
    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setPrio(int prio) {
        this.prio = prio;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getTexto() {
        return texto;
    }

    public int getPrio() {
        return prio;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public void setFolderid(int folderid) {
        this.folderid = folderid;
    }

    public int getFolderid() {
        return folderid;
    }

    public Mensaje(String asunto, String texto, int prio, String sender) {
        this.asunto = asunto;
        this.texto = texto;
        this.prio = prio;
        this.sender = sender;
    }
    
    public Mensaje(int folderid,int id,String asunto, String texto, int prio, String sender) {
        this.asunto = asunto;
        this.texto = texto;
        this.prio = prio;
        this.sender = sender;
        this.id = id;
        this.folderid=folderid;
    }
    
    public Mensaje(int folderid,int id,String asunto, String texto, int prio, String sender,String fecha, int parentid) {
        this.asunto = asunto;
        this.texto = texto;
        this.prio = prio;
        this.sender = sender;
        this.fecha = fecha;
        this.id = id;
        this.folderid=folderid;
    }
    
    public Mensaje(String asunto, String texto, int prio, String sender,String fecha, int parentid) {
        this.asunto = asunto;
        this.texto = texto;
        this.prio = prio;
        this.sender = sender;
        this.fecha = fecha;
    }
    
    public Mensaje(String asunto, String texto, int parentid, int prio) {
        this.asunto = asunto;
        this.texto = texto;
        this.prio = prio;
        this.parentid = parentid;
    }
   
    
}
