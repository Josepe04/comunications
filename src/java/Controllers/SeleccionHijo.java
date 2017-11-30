/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Hijo;
import model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Norhan
 */
@Controller
public class SeleccionHijo {

    Connection cn;
    //static Logger log = Logger.getLogger(ProgressbyStudent.class.getName());
    private ServletContext servlet;

    private Object getBean(String nombrebean, ServletContext servlet) {
        ApplicationContext contexto = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet);
        Object beanobject = contexto.getBean(nombrebean);
        return beanobject;
    }
    
    @RequestMapping("/seleccionHijo/start.htm")
    public ModelAndView seleccion(HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        ModelAndView mv = new ModelAndView("seleccionHijo");
        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource) this.getBean("dataSourceAH", hsr.getServletContext());
        this.cn = dataSource.getConnection();
        User u = (User)hsr.getSession().getAttribute("user");
        //mv.addObject("hijos", getChildren(u));
        return mv;
    }
    
    @RequestMapping("/seleccionHijo/seleccionado.htm")
    public ModelAndView seleccionado(HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        ModelAndView mv = new ModelAndView("redirect:/menu/startp.htm");
        ArrayList<Integer> idchilds = new ArrayList<>();
        int length = Integer.parseInt(hsr.getParameter("length"));
        for(int i = 0; i < length;i++){
            String s = hsr.getParameter(""+i);
            if(s!=null)
                idchilds.add(Integer.parseInt(s));
        }
        if(!idchilds.isEmpty())
            hsr.getSession().setAttribute("hijo", idchilds.get(0));
//        else 
//            mv = new ModelAndView("redirect:/seleccionHijo/start.htm");
        return mv;
    }
    

    public ArrayList<Hijo> getChildren(User u) throws SQLException {
//        this.conectarOracle();
        int id = u.getId();
        ArrayList<Hijo> listaAlumnos = new ArrayList<>();
        try {

            Statement st = this.cn.createStatement();
            String consulta = "SELECT Person.FirstName, Person.LastName, Person_Student.SchoolCode, Person_Student.StudentID, Parent_Student.Custody, Parent_Student.Correspondence,"
                    + " Parent_Student.PWBlock, Parent_Student.ParentID"
                    + " FROM  AH_ZAF.dbo.Parent_Student INNER JOIN"
                    + " AH_ZAF.dbo.Person_Student ON Parent_Student.StudentID = AH_ZAF.dbo.Person_Student.StudentID INNER JOIN"
                    + " AH_ZAF.dbo.Person ON AH_ZAF.dbo.Person_Student.StudentID = AH_ZAF.dbo.Person.PersonID"
                    + " WHERE ((AH_ZAF.dbo.Parent_Student.Correspondence = 1) or (AH_ZAF.dbo.Parent_Student.PWBlock <> 1) ) AND"
                    + " (AH_ZAF.dbo.Parent_Student.ParentID = "+u.getId()+")";
            ResultSet rs = st.executeQuery(consulta);
            while (rs.next()) {
                listaAlumnos.add(new Hijo(rs.getInt("StudentID"),rs.getString("FirstName")
                        ,rs.getString("LastName")));
            }
            //this.finalize();

        } catch (SQLException ex) {
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            //log.error(ex+errors.toString());
        }

        return listaAlumnos;
    }
}
