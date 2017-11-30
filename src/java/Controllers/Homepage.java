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
   Connection cn;
    private Object getBean(String nombrebean, ServletContext servlet)
{
        ApplicationContext contexto = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet);
        Object beanobject = contexto.getBean(nombrebean);
        return beanobject;
}
    public ModelAndView inicio(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
    
        return new ModelAndView("userform");
    }
  @RequestMapping
public ModelAndView login(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource) this.getBean("dataSourceAH", hsr.getServletContext());
        this.cn = dataSource.getConnection();
        HttpSession session = hsr.getSession();
        User user = new User();
        int scgrpid = 0;
        boolean result = false;
        ArrayList<Students> children ;
        LoginVerification login = new LoginVerification();
        ModelAndView mv = new ModelAndView("redirect:/menu/start.htm");
        if("QuickBook".equals(hsr.getParameter("txtusuario"))){
           return mv;
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
               scgrpid=login.getSecurityGroupID("MontesoriTest");
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
            Statement st = this.cn.createStatement();
            String consulta = "SELECT count(*) AS cuenta FROM AH_ZAF.dbo.Staff where Faculty = 1 and StaffID =" + user.getId();
            ResultSet rs = st.executeQuery(consulta);
            if (rs.next()) {
                profesor = rs.getInt("cuenta") > 0;
            }
            consulta = "SELECT count(*) AS cuenta FROM AH_ZAF.dbo.Parent_Student where ParentID =" + user.getId();
            ResultSet rs2 = st.executeQuery(consulta);
            if (rs2.next()) {
                padre = rs2.getInt("cuenta") > 0;
            }
            

        } catch (SQLException ex) {

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
         DriverManagerDataSource dataSource;    
         dataSource = (DriverManagerDataSource)this.getBean("comunicacion",hsr.getServletContext());
         this.cn = dataSource.getConnection();
         Statement st = this.cn.createStatement();
         try{
            ResultSet rs = st.executeQuery("select mensaje.msgid,parentid,fecha,prio,asunto,texto,msfrom "
                    + "from mensaje inner join msg_folder on mensaje.msgid=msg_folder.msgid "
                    + "inner join folder on msg_folder.idfolder=folder.idfolder and folder.nombre='inbox'"
                    + "inner join msg_from_to on msg_from_to.msto="+u.getId()
                    + " where folder.idpersona="+u.getId());
            while(rs.next()){
                String text = rs.getString("texto");
                if(text.length()>20)
                    text = recolocar(text.substring(0, 20));
                listaMensajes.add(new Mensaje(rs.getString("asunto"),text,
                     Integer.parseInt(rs.getString("prio")),rs.getString("msfrom"),rs.getString("fecha"),1));
            }
            ResultSet rs2 = st.executeQuery("select nombre,idfolder from folder "
                    + "where idpersona = "+u.getId());
            while(rs2.next()){
                //String asunto, String texto, int prio, String sender,String fecha, int parentid
                listaFolders.add(new Folder(rs2.getString(2),rs2.getString(1)));
            }
            dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
            this.cn = dataSource.getConnection();
            st = this.cn.createStatement();
            for(int i = 0;i<listaMensajes.size();i++){
               ResultSet rs3 = st.executeQuery("select FirstName from Person where personID ="+listaMensajes.get(i).getSender());
               rs3.next();
               String prueba = rs3.getString("FirstName");
               prueba.charAt(1);
               listaMensajes.get(i).setSender(rs3.getString("firstname"));
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
        DriverManagerDataSource dataSource;    
        dataSource = (DriverManagerDataSource)this.getBean("comunicacion",hsr.getServletContext());
        this.cn = dataSource.getConnection();
        Statement st = this.cn.createStatement();
        User u = (User)hsr.getSession().getAttribute("user");
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
        DriverManagerDataSource dataSource;    
        dataSource = (DriverManagerDataSource)this.getBean("comunicacion",hsr.getServletContext());
        this.cn = dataSource.getConnection();
        Statement st = this.cn.createStatement();
        try{
            ResultSet rs = st.executeQuery("select mensaje.msgid,parentid,fecha,prio,asunto,texto "
                    + "from mensaje inner join msg_folder on mensaje.msgid=msg_folder.msgid "
                    + "inner join folder on msg_folder.idfolder=folder.idfolder "
    //                + "inner join msg_from_to on mensaje.msgid=msg_from_to.msgid"
                    + "where folder.idfolder="+id);
            while(rs.next()){
                String text = rs.getString("texto");
                if(text.length()>20)
                    text = recolocar(text.substring(0, 20));
                listaMensajes.add(new Mensaje(rs.getString("asunto"),text,
                     Integer.parseInt(rs.getString("prio")),"chemamola",rs.getString("fecha"),1));
            }
        }catch(SQLException e){

        }
        return new Gson().toJson(listaMensajes);
    }

    @RequestMapping("/menu/startp.htm")
    public ModelAndView menu2(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
         ModelAndView mv = new ModelAndView("menu");
         return mv;
    }

    @RequestMapping("/menu/enviar.htm")
    public ModelAndView menuEnviar(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = null; 
        User u = (User)(hsr.getSession().getAttribute("user"));
        if(u.getType() == 1){
            mv = new ModelAndView("redirect:/enviarmensajepadre/start.htm");
        }
        else
            mv = new ModelAndView("redirect:/enviarmensaje/start.htm");
        return mv;
    }

    @RequestMapping("/menu/recibidos.htm")
    public ModelAndView menuRecibidos(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
         ModelAndView mv = new ModelAndView("menu");
         return mv;
    }

}
