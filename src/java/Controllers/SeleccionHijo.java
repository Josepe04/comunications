/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
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
        ModelAndView mv = Homepage.checklogin(hsr);
         if(mv!=null)
             return mv;
        mv = new ModelAndView("seleccionHijo");
        return mv;
    }
    
    @RequestMapping("/seleccionHijo/seleccionado.htm")
    public ModelAndView seleccionado(HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        ModelAndView mv = Homepage.checklogin(hsr);
         if(mv!=null)
             return mv;
        if(hsr.getParameter("submit").equals("father"))
            return new ModelAndView("redirect:/enviarmensajepadre/start.htm");
        else
            return new ModelAndView("redirect:/enviarmensaje/start.htm");
    }
    
}
