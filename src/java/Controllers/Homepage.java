/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

/**
 *
 * @author nmohamed
 */


import static Controllers.EnviarMensaje.createFolder;
import atg.taglib.json.util.JSONObject;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.LoginVerification;
import model.User;
import javax.servlet.http.HttpSession;
import model.Folder;
import model.Mensaje;
import model.Students;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


@Controller
public class Homepage extends MultiActionController  {
    public static Connection cn;
    public static Connection cn2;
    public static Statement st;
    public static Statement st2;
    
    private Object getBean(String nombrebean, ServletContext servlet){
        ApplicationContext contexto = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet);
        Object beanobject = contexto.getBean(nombrebean);
        return beanobject;
    }
    
    @RequestMapping
    public ModelAndView inicio(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        //connection to comunication
        DriverManagerDataSource dataSource = (DriverManagerDataSource) this.getBean("comunicacion", hsr.getServletContext());
        this.cn = dataSource.getConnection();
        st = this.cn.createStatement(); 
        //connection to datasourceAH
        DriverManagerDataSource dataSource2 = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
        this.cn2 = dataSource2.getConnection();
        this.st2 = cn2.createStatement();
        return new ModelAndView("userform");
    }
    
    @RequestMapping("/login.htm")
    public ModelAndView login(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
            HttpSession session = hsr.getSession();
            User user = new User();
            int scgrpid = 0;
            boolean result = false;
            ArrayList<Students> children ;
            LoginVerification login = new LoginVerification();
            ModelAndView mv = new ModelAndView("redirect:/menu/start.htm");
            String txtusuario = hsr.getParameter("txtusuario");
            if(txtusuario==null){
               return new ModelAndView("userform");
            }else{
               user = login.consultUserDB(hsr.getParameter("txtusuario"), hsr.getParameter("txtpassword"));
               // if the username or password incorrect
               if(user.getId()==0){
                    mv = new ModelAndView("userform");
                    String message = "Username or password incorrect";
                    mv.addObject("message", message);
                    return mv;
                }
                //if the user is not part of the group
                else{
                   scgrpid=login.getSecurityGroupID("Communications APP");
                   result = login.fromGroup(scgrpid, user.getId());
                   if (result == true){
                       setTipo(user);
                       session.setAttribute("user", user);
                       return mv;
                   }
                    else{
                        children=login.isparent( user.getId());    
                        if(!children.isEmpty()){
                            setTipo(user);
                            session.setAttribute("user", user);
                            return mv; 
                        }
                        else{
                           mv = new ModelAndView("userform");
                           String message = "Username or Password incorrect";
                           mv.addObject("message", message);
                           return mv;
                        }
                    }
                }
             }
    }

        //user.setId(10333);
        //user.setId(10366);

    public void setTipo(User user) {
        boolean padre = false, profesor = false;
        try {
            String consulta = "SELECT count(*) AS cuenta FROM Staff where Faculty = 1 and StaffID =" + user.getId();
            ResultSet rs = st2.executeQuery(consulta);
            if (rs.next()) {
                profesor = rs.getInt("cuenta") > 0;
            }
            consulta = "SELECT count(*) AS cuenta FROM Parent_Student where ParentID =" + user.getId();
            ResultSet rs2 = st2.executeQuery(consulta);
            if (rs2.next()) {
                padre = rs2.getInt("cuenta") > 0;
            }
        } catch (SQLException ex) {
            System.out.println("error");
        }
        if (padre && profesor) {
            user.setType(0);
        } else if (padre) {
            user.setType(1);
        } else if(profesor){
            user.setType(2);
        } else {
            user.setType(3);
        }
    }

    private String recolocar(String in){
        String add = "";
        int nocopiar = 0; 
        char c;
        for(int i = 0;i<in.length();i++){
            c=in.charAt(i);
            if(c=='<')
                nocopiar = 1;
            else if(c=='>' && nocopiar==1)
                nocopiar = 0;
            else if(c=='/' && nocopiar==1)
                nocopiar = 2;
            if(nocopiar == 0 && c!='>')
                add+=c;
        }
        return add;
    }
    
    @RequestMapping("/menu/start.htm")
    public ModelAndView menu(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
         ArrayList<Mensaje> listaMensajes = new ArrayList<>();
         ArrayList<Folder> listaFolders = new ArrayList<>();
         ModelAndView mv = new ModelAndView("menu");
         User u = (User)hsr.getSession().getAttribute("user");
         try{
            ResultSet folder = st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='Inbox'");
            if(!folder.next())
                EnviarMensaje.createFolder(st,""+u.getId(),"Inbox");
            ResultSet folder2 = st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='Sent'");
            if(!folder2.next())
                EnviarMensaje.createFolder(st,""+u.getId(),"Sent");
            ResultSet folder3 = st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='Litter'");
            if(!folder3.next())
                EnviarMensaje.createFolder(st,""+u.getId(),"Litter");
            ResultSet rs = st.executeQuery("select mensaje.msgid,msg_folder.idfolder,parentid,fecha,prio,asunto,texto,msfrom,fromname "
                    + "from mensaje inner join msg_folder on mensaje.msgid=msg_folder.msgid "
                    + "inner join folder on msg_folder.idfolder=folder.idfolder and folder.nombre='Inbox'"
                    + "inner join msg_from_to on msg_from_to.msto="+u.getId()
                    + " where folder.idpersona="+u.getId());
            while(rs.next()){
                String text = rs.getString("texto");
                if(text.length()>20)
                    text = recolocar(text.substring(0, 20));
                listaMensajes.add(new Mensaje(rs.getInt(2),rs.getInt(1),rs.getString("asunto"),text,
                     Integer.parseInt(rs.getString("prio")),rs.getString("fromname"),rs.getString("fecha"),1));
            }
            ResultSet rs2 = st.executeQuery("select nombre,idfolder from folder "
                    + "where idpersona = "+u.getId());
            while(rs2.next()){
                listaFolders.add(new Folder(rs2.getString(2),rs2.getString(1)));
            }
         }catch(SQLException ex){
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
         }
         mv.addObject("lista", listaMensajes);
         mv.addObject("folders", listaFolders);
         return mv;
    }
    
    
    @RequestMapping("/menu/createfolder.htm")
    @ResponseBody
    public String createFolder(@RequestParam("nombre") String nombre,HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ArrayList<Folder> listaFolders = new ArrayList();
        User u = (User)hsr.getSession().getAttribute("user");
        if(nombre.length()>0)
            EnviarMensaje.createFolder(st,""+u.getId(), nombre);
        ResultSet rs2 = st.executeQuery("select nombre,idfolder from folder "
                    + "where idpersona = "+u.getId());
            while(rs2.next()){
                //String asunto, String texto, int prio, String sender,String fecha, int parentid
                listaFolders.add(new Folder(rs2.getString(2),rs2.getString(1)));
            }
        return new Gson().toJson(listaFolders);//u.getId();
    }        

    @RequestMapping("/menu/chargefolder.htm")
    @ResponseBody
    public String chargeFolder(@RequestParam("seleccion") String id, HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        ArrayList<Mensaje> listaMensajes = new ArrayList<>();
        try{
            ResultSet rs = st.executeQuery("select mensaje.msgid,parentid,fecha,prio,asunto,texto "
                    + "from mensaje inner join msg_folder on mensaje.msgid=msg_folder.msgid "
                    + "inner join folder on msg_folder.idfolder=folder.idfolder "
                    + "where folder.idfolder="+id);
            while(rs.next()){
                String text = rs.getString("texto");
                if(text.length()>20)
                    text = recolocar(text.substring(0, 20));
                listaMensajes.add(new Mensaje(Integer.parseInt(id),rs.getInt(1),rs.getString("asunto"),text,
                     Integer.parseInt(rs.getString("prio")),"AppTest",rs.getString("fecha"),1));
            }
            for(Mensaje m:listaMensajes){
                ResultSet rs2 = st.executeQuery("select * from msg_from_to where msgid="+m.getId());
                if(rs2.next())
                    m.setSender(rs2.getString("fromname"));
            }
        }catch(SQLException e){

        }
        return new Gson().toJson(listaMensajes);
    }
    
    @RequestMapping("/menu/borrarmsg.htm")
    @ResponseBody
    public String borrarMsg(@RequestParam("id") String f, HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        boolean corte=true,papelera=false;
        String msgid = "";
        String folderid = "";
        String idpersona = "";
        String idpapelera = "";
        for(int i = 0;i<f.length();i++){
            if(f.charAt(i)=='p')
                papelera = true;
            else if(corte && f.charAt(i)!=' ')
                msgid += f.charAt(i);
            else if(f.charAt(i)==' ')
                corte = false;
            else
                folderid +=f.charAt(i);
        }
        
        try{
            st.executeUpdate("delete from msg_folder where msgid="+msgid+" and idfolder="+folderid);
        }catch(SQLException e){
                
        }
        if(papelera){
            try{
                ResultSet rs = st.executeQuery("select * from folder where idfolder="+folderid);
                if(rs.next())
                    idpersona=rs.getString("idpersona");
                ResultSet rs2 = st.executeQuery("select * from folder where nombre='Litter' and idpersona="+idpersona);
                if(rs2.next())
                    idpapelera=rs2.getString("idfolder");
                st.executeUpdate("insert into msg_folder values("+msgid+","+idpapelera+")");
            }catch(SQLException e){

            }
        }else{
            try{
                boolean borrarmsg = false;
                ResultSet rs = st.executeQuery("select count(*) from msg_folder where msgid="+msgid);
                if(rs.next()){
                    int num = rs.getInt(1);
                    borrarmsg = num<1;
                }
                if(borrarmsg){
                    st.executeUpdate("delete from mensaje where msgid="+msgid);
                    st.executeUpdate("delete from msg_from_to where msgid="+msgid);
                }
            }catch(SQLException e){
                
            }
        }
        return "";
    }
    
    @RequestMapping("/menu/vermsg.htm")
    public ModelAndView vermensaje(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        Mensaje m=null; 
        ModelAndView mv = new ModelAndView("vermensaje");
        String id = hsr.getParameter("ver_button");
        try{
            String sender="";
            ResultSet rs = st.executeQuery("select * from msg_from_to where msgid="+id);
            if(rs.next()){
                sender = rs.getString("msfrom");
                mv.addObject("fromname",rs.getString("fromname"));
            }
            ResultSet rs2 = st.executeQuery("select * from mensaje where msgid="+id);
            if(rs2.next())
                //String asunto, String texto, int prio, String sender,String fecha, int parentid
                m = new Mensaje(rs2.getString("asunto"),rs2.getString("texto"),rs2.getInt("prio"),
                                sender,rs2.getString("fecha"),rs2.getInt("parentid"));
                
        }catch(SQLException e){
            System.err.println("fallo al cargar");
        }
        mv.addObject("mensaje", m);
        return mv;
    }
    
    @RequestMapping("/menu/vermsgajax.htm")
    public String vermensajeajax(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        JSONObject jsonObj = new JSONObject();
        String id = hsr.getParameter("id");
        try{
            String sender="";
            ResultSet rs = st.executeQuery("select * from msg_from_to where msgid="+id);
            if(rs.next()){
                sender = rs.getString("msfrom");
                jsonObj.put("sender",sender);
            }
            ResultSet rs2 = st.executeQuery("select * from mensaje where msgid="+id);
            if(rs2.next()){                
                jsonObj.put("asunto", rs2.getString("asunto"));
                jsonObj.put("texto",rs2.getString("texto"));
                jsonObj.put("fecha", rs2.getString("fecha"));
//String asunto, String texto, int prio, String sender,String fecha, int parentid
//                m = new Mensaje(rs2.getString("asunto"),rs2.getString("texto"),rs2.getInt("prio"),
//                                sender,rs2.getString("fecha"),rs2.getInt("parentid"));
            }
        }catch(SQLException e){
            System.err.println("fallo al cargar");
        }
        return jsonObj.toString();
    }

    @RequestMapping("/menu/recover.htm")
    public String recuperar(@RequestParam("id") String f,HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        boolean corte=true;
        String id = "";
        String idfolder = "";
        for(int i = 0;i<f.length();i++){
            if(corte && f.charAt(i)!=' ')
                id += f.charAt(i);
            else if(f.charAt(i)==' ')
                corte = false;
            else
                idfolder +=f.charAt(i);
        }
        String idfolderInbox = "";
        User u = (User)hsr.getSession().getAttribute("user");
        try{
            st.execute("delete from msg_folder where msgid="+id+" and idfolder="+idfolder);
            ResultSet rs = st.executeQuery("select * from folder where idpersona="+u.getId()+" and "
                    + "nombre='Inbox'");
            if(rs.next())
                idfolderInbox = rs.getString("idfolder");
            st.execute("insert into msg_folder values("+id+","+idfolderInbox+")");
        }catch(SQLException e){
            System.err.println("fallo en recover");
        }
        return "";
    }
    
    @RequestMapping("/menu/enviar.htm")
    public ModelAndView menuEnviar(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = null; 
        User u = (User)(hsr.getSession().getAttribute("user"));
        if(u.getType() == 1){
            mv = new ModelAndView("redirect:/enviarmensajepadre/start.htm");
        }
        else{
            mv = new ModelAndView("redirect:/enviarmensaje/start.htm?reply=false");
        }
        return mv;
    }
    
    @RequestMapping("/menu/responder.htm")
    public ModelAndView menuResponder(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = null; 
        User u = (User)(hsr.getSession().getAttribute("user"));
        mv = new ModelAndView("redirect:/enviarmensajepadre/enviar.htm?reply=false&parentid="
                + hsr.getParameter("parentid") + "&destinatarios="
                + hsr.getParameter("destinatarios") + "&asunto="
                + hsr.getParameter("asunto") + "&NotificationMessage="
                + hsr.getParameter("NotificationMessage"));
        return mv;
    }

    @RequestMapping("/menu/recibidos.htm")
    public ModelAndView menuRecibidos(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
         ModelAndView mv = new ModelAndView("menu");
         return mv;
    }

}




