/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Servelets;

import credentials.Credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author c0565705
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet{
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                // There are no parameters at all
                out.println(getResults("SELECT * FROM product"));
            } else {
                // There are some parameters
                String id = request.getParameter("id");
                out.println(getResults("SELECT * FROM product WHERE ProductID = ?", id));
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("%s\t%s\t%s\t%s\n", rs.getInt("ProductID"), rs.getString("Name"), rs.getString("Description"), rs.getInt("Quantity")));
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                // There are some parameters            
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                out.println(doUpdate("INSERT INTO product (Name, Description, Quantity) VALUES (?, ?, ?)", name, description, quantity));
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
        
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                // There are some parameters
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                out.println(doUpdate("UPDATE product SET Name = ?, Description = ?, Quantity = ? WHERE ProductId = ?", name, description, quantity, id));
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    @Override
    protected void doDelete (HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id")) {
                // There are some parameters
                String id = request.getParameter("id");
                
                out.println(doUpdate("DELETE FROM product WHERE ProductId = ?", id));
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
